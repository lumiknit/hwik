package lumiknit.app.hwik.state

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import kotlinx.coroutines.suspendCancellableCoroutine
import lumiknit.app.hwik.components.modal.ConfirmModalProps
import lumiknit.app.hwik.core.WebViewCookieHandler
import okhttp3.OkHttpClient

object GlobalVM : ViewModel() {
	// Modal
	val confirmModalCallback = mutableStateOf<ConfirmModalProps?>(null)

	suspend fun showConfirmModal(
		title: String,
		message: String,
	): Boolean {
		return suspendCancellableCoroutine { continuation ->
			confirmModalCallback.value = ConfirmModalProps(
				title = title,
				message = message,
				onClick = { confirmed ->
					// Handle cancellation if needed
					continuation.resume(confirmed) { cause, _, _ -> // Handle cancellation if needed
						// Handle cancellation if needed
					}
					confirmModalCallback.value = null // Clear the callback after use
				}
			)
		}
	}

	// WebView Settings
	var hdUserAgent: String = ""
	val cookieJar = WebViewCookieHandler()

	fun setUpImageLoader(context: Context): ImageLoader {
		Log.d("GlobalVM", "Setting up ImageLoader with OkHttpClient")
		return ImageLoader.Builder(context).components {
			add(
				OkHttpNetworkFetcherFactory(
					callFactory = {
						OkHttpClient.Builder()
							.apply {
								// Set the user agent if available
								if (hdUserAgent.isNotEmpty()) {
									// Set the user agent for the OkHttpClient
									this.addInterceptor { chain ->
										val request = chain.request().newBuilder()
											.header("User-Agent", hdUserAgent)
											.build()
										chain.proceed(request)
									}
								}
								// Add logger for resp body
								this.addInterceptor { chain ->
									val response = chain.proceed(chain.request())
									if (response.code > 399) {
										val body = response.body
										val reqHeaders = response.request.headers
										Log.e(
											"OkHttp",
											"Error response: ${response.code}: ${body}"
										)
										Log.e(
											"OkHttp",
											"Request headers: ${reqHeaders}"
										)
									}
									response
								}
							}
							.cookieJar(cookieJar)
							.build()
					}
				)
			)
		}.build()
	}
}