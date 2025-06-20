package lumiknit.app.hwik

import kotlinx.serialization.Serializable

@Serializable
object RouteMain

@Serializable
object RouteSourceList

@Serializable
object RoutePreferences

open class NavCallbacks {
	open fun onRouteMain() {}
	open fun onRouteWebShowView() {}
	open fun onRouteSourceList() {}
	open fun onRoutePreferences() {}
	open fun onBack() {}
}