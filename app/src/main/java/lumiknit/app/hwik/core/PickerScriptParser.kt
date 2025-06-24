package lumiknit.app.hwik.core

/*
## PickerScript Grammar Details

For real structure, see PickerScript.kt

### Features

* Based on Javascript, with special commenting syntax for directives.

### Syntax

- PickerScript is just a javascript,
- Special line comments '///' are used as directives.
- '/// <sym> ...'
- <sym> can be used for
  - '/// # <id>': Denote the script ID.
  - '/// @ <key> <value>': Metadata key-value pairs. key is case-insensitive.
    - <key>: name, version, author, description
    - urlRE is a special key that defines the URL regex for which this script is applicable.
  - '/// * <name>': Start of a process, which has multiple steps.
    - Currently, there are 3 kind of processes: 'articleList', 'articleContent', 'search'.
    - 'articleList' is used to extract a list of URLs.
    - 'articleContent' is used to extract content of an article from a URL
    - 'search' is non-empty if there can be some search steps with query.
   - '/// - [wait <seconds>]': Step start marker.
     - If wait is specified, it will wait for the specified number of seconds before executing the next step.

Example:

/// # google_search

/// @ name Google Search
/// @ version 2025.0624.1
/// @ author Aleph
/// @ description Find google search results
/// @ urlre ^https?://www\.google\.com/search\?q=.*$


/// * articleList

/// -
// For the first step, just go to google search page
// '$' is a special variable that contains all state (including inputs, last step's outputs)
window.location.href = "https://www.google.com/search?q=" + $query;

/// - wait 1
// Just wait for 1 second

/// -
let urls = []
document.querySelectorAll("a").forEach((e) => {
	urls.push(e.href)
});
$.urls = urls; // Save the URLs to the state


/// * search
...
*/

class ParserException(
	lineNum: Int,
	errorMsg: String
) : Exception("Error parsing PickerScript at line $lineNum: $errorMsg")

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

	private fun tryParseDirective(): Boolean {
		if (ln >= lines.size) return false

		val line = lines[ln].trim()
		val prefix = "///"

		if (!line.startsWith(prefix)) return false
		ln++ // Move to next line

		val content = line.substring(prefix.length).trim()

		if (line.isEmpty()) {
			throw ParserException(ln, "Empty directive line")
		}

		val sym = content[0]
		var left = content.substring(1).trim()
		val getSplitted =
			{ limit: Int ->
				val out = left.split("\\s+".toRegex(), limit = limit)
				if (out.size < limit) {
					throw ParserException(ln, "Invalid directive format: $line")
				}
				out
			}

		when (sym) {
			'#' -> { // ID
				script.id = left
			}

			'@' -> { // Metadata
				val parts = getSplitted(2)
				val key = parts[0].lowercase()
				val value = parts[1]
				when (key) {
					"name" -> script.meta.name = value
					"version" -> script.meta.version = value
					"author" -> script.meta.author = value
					"description" -> script.meta.description = value
					"urlre" -> script.urlRE = value
					else -> throw ParserException(ln, "Unknown metadata key: $key")
				}
			}

			'*' -> { // Start of a process
				if (processKind.isNotEmpty()) {
					flushProcess() // Flush previous process if exists
				}
				processKind = left.lowercase()
				if (processKind !in listOf("articlelist", "articlecontent", "search")) {
					throw ParserException(ln, "Unknown process kind: $processKind")
				}
			}

			'-' -> { // Step start marker
				// Reset step conditions
				stepCondWaitSeconds = 0.0

				var lowered = left.lowercase()
				val keywordWait = "wait"
				if (lowered.startsWith(keywordWait)) {
					val rest = left.substring(keywordWait.length).trim()
					// Try to parse as double
					try {
						stepCondWaitSeconds = rest.toDouble()
					} catch (e: NumberFormatException) {
						throw ParserException(ln, "Invalid wait seconds: $rest")
					}
				}
			}
		}

		return true
	}

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