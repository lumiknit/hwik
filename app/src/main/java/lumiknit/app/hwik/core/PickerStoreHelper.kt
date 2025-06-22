package lumiknit.app.hwik.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.coroutines.executeAsync

data class PickerScriptFetchResult(
	val raw: String?,
	val script: PickerScript?,
	val error: String?
)

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun fetchPickerScript(
	url: String
): PickerScriptFetchResult {
	val client = OkHttpClient()
	val request = Request.Builder()
		.url(sanitizeFetchURL(url))
		.build()
	val call = client.newCall(request)

	var raw: String? = null
	var script: PickerScript? = null

	try {
		val resp = call.executeAsync()
		if (!resp.isSuccessful) {
			return PickerScriptFetchResult(null, null, "Unexpected code ${resp.code}")
		}
		raw = resp.body.string()
		script = PickerScript.fromJSON(raw)
		return PickerScriptFetchResult(raw, script, null)
	} catch (e: Exception) {
		return PickerScriptFetchResult(
			raw,
			script,
			"Error fetching script: ${e.message}"
		)
	}
}