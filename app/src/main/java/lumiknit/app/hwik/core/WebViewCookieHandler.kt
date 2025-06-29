package lumiknit.app.hwik.core

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class WebViewCookieHandler : CookieJar {
	private val webviewCookieManager = CookieManager.getInstance()

	override fun saveFromResponse(
		url: HttpUrl,
		cookies: List<Cookie>
	) {
		val urlString = url.toString()
		for (cookie in cookies) {
			webviewCookieManager.setCookie(urlString, cookie.toString())
		}
	}

	override fun loadForRequest(url: HttpUrl): List<Cookie> {
		val urlString = url.toString()
		val cookiesString = webviewCookieManager.getCookie(urlString)

		if (cookiesString != null && cookiesString.isNotEmpty()) {
			val cookieHeaders = cookiesString.split(";")
			val cookies = mutableListOf<Cookie>()
			for (header in cookieHeaders) {
				Cookie.parse(url, header.trim())?.let { cookies.add(it) }
			}
			return cookies
		}
		return emptyList()
	}
}