package lumiknit.app.hwik.core

private val generousURLRegex = Regex(
	"""^([A-Za-z0-9]+:/*)?([^/]+)(/.*)?$""",
)

/**
 * Convert a given string to a valid URL.
 */
fun sanitizeFetchURL(maybeURL: String): String {
	val trimmed = maybeURL.trim()

	val matchResult = generousURLRegex.matchEntire(trimmed)
	val protocol = matchResult?.groups?.get(1)?.value

	// Check the url has protocol, if not put 'https://'
	if (protocol.isNullOrEmpty()) {
		return "https://$trimmed"
	}
	return trimmed
}