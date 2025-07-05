package lumiknit.app.hwik.state

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import lumiknit.app.hwik.core.WebViewCookieHandler
import okhttp3.OkHttpClient

object GlobalVM : ViewModel() {
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