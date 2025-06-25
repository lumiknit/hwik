package lumiknit.app.hwik.core

class ParserException(
	lineNum: Int,
	errorMsg: String
) : Exception("Error parsing PickerScript at line $lineNum: $errorMsg")

/**
 * Return prefix-removed version if exists
 */
private fun String.rmPrefix(prefix: String): String? {
	if (this.startsWith(prefix)) {
		return this.substring(prefix.length).trimStart()
	}
	return null
}

/**
 * Lowercase and remove all underscores
 */
private fun String.canonicalMetaKey(): String {
	return lowercase().replace("_", "")
}

/**
 * Split the string by whitespaces.
 * It must return a list with at least one element.
 */
private fun String.divBySpaces(): Pair<String, String> {
	val arr = this.split("\\s+".toRegex(), 2)
	val a = arr[0].trim().canonicalMetaKey()
	val b = if (arr.size > 1) arr[1].trim() else ""
	return Pair(a, b)
}


/**
 * PickerScriptParser is a parser for PickerScript.
 * It parses the script and extracts the ID, metadata, and processes.
 * The script is expected to be in a specific format with special comments.
 */
class PickerScriptParser(
	src: String
) {
	// Parsing State
	val lines: List<String> = src.lines()
	var ln: Int = 0

	// Parsed Data
	val script = PickerScript()

	var processKind: String = ""
	var steps: MutableList<PickerStep> = mutableListOf()

	var stepCondWaitSeconds: Double = 0.0

	/**
	 * Finish gather steps for the process, and push.
	 */
	private fun flushProcess() {
		if (steps.isEmpty()) {
			return
		}

		val process = PickerProcess(
			steps = mutableListOf<PickerStep>().apply {
				addAll(steps)
			}
		)

		// Note that processKind is already lower-cased
		when (processKind) {
			"articlelist" -> script.articleList = process
			"articlecontent" -> script.articleContent = process
			"search" -> script.search = process

			"" -> throw ParserException(
				ln, "Process kind is empty, but steps are not empty"
			)

			else -> throw ParserException(
				ln, "Unknown process kind: $processKind"
			)
		}
		// Clean steps and kind
		steps.clear()
		processKind = ""
	}

	/**
	 * Handle meta directive
	 * line should be a string AFTER '/// @'
	 */
	private fun handleMetaDirective(line: String) {
		val (key, value) = line.divBySpaces()
		when (key) {
			"id" -> script.id = value
			"urlre" -> script.urlRE = value
			"name" -> script.meta.name = value
			"version" -> script.meta.version = value
			"author" -> script.meta.author = value
			"description" -> script.meta.description = value
			else -> throw ParserException(ln, "Unknown metadata key: $key")
		}
	}

	/**
	 * Handle process start directive
	 * line should be a string AFTER '/// *'
	 */
	private fun handleProcessStartDirective(line: String) {
		if (processKind.isNotEmpty()) {
			flushProcess() // Flush previous process if exists
		}
		processKind = line.lowercase()
		if (processKind !in listOf("articlelist", "articlecontent", "search")) {
			throw ParserException(ln, "Unknown process kind: $processKind")
		}
	}

	/**
	 * Handle step divider directive
	 * line should be a string AFTER '/// -'
	 */
	private fun handleStepDividerDirective(line: String) {
		var (key, left) = line.divBySpaces()
		when (key) {
			"wait" -> {
				try {
					stepCondWaitSeconds = left.toDouble()
				} catch (e: NumberFormatException) {
					throw ParserException(ln, "Invalid wait seconds: '${left}', $e")
				}
			}

			else -> {
				// Reset all steps
				stepCondWaitSeconds = 0.0
			}
		}
	}


	/**
	 * Parse derective
	 */
	private fun tryParseDirective(): Boolean {
		if (ln >= lines.size) return false
		val content = lines[ln].trim().rmPrefix("///")
		if (content == null || content.isEmpty()) return false;
		ln++ // Move to next line

		val sym = content[0]
		var left = content.substring(1).trim()

		when (sym) {
			'@' -> handleMetaDirective(left)
			'*' -> handleProcessStartDirective(left)
			'-' -> handleStepDividerDirective(left)
		}

		return true
	}

	/**
	 * Try to gather code parts.
	 */
	private fun tryParseCode(): Boolean {
		val lnStart = ln

		while (ln < lines.size && !lines[ln].trim().startsWith("///")) {
			ln++ // Skip empty lines
		}

		val code = lines.subList(lnStart, ln).joinToString("\n").trim()

		if (!code.isEmpty()) {
			// Push step
			steps.add(
				PickerStep(
					condWaitSeconds = stepCondWaitSeconds,
					code = code
				)
			)
		}
		return ln > lnStart
	}

	/**
	 * Parse lines
	 */
	fun parseLines() {
		while (ln < lines.size) {
			if (!tryParseDirective() && !tryParseCode()) {
				// If neither directive nor code was parsed, we are done
				break
			}
		}

		if (ln < lines.size) {
			throw ParserException(
				ln,
				"Unexpected content after parsing directives and code"
			)
		}

		flushProcess()
	}


	companion object {
		fun parse(script: String): PickerScript {
			val parser = PickerScriptParser(script)
			parser.parseLines()
			return parser.script
		}
	}
}