package lumiknit.app.hwik

import kotlinx.serialization.Serializable

@Serializable
object RouteMain

@Serializable
object RouteWebViewScreen

@Serializable
object RouteSourceList

@Serializable
data class RouteSourceEdit(
	// If null, it means to create a new source.
	val sourceID: Long? = null,
)

@Serializable
data class RouteSourceTest(
	// If null, it means to create a new source.
	val processStr: String
)

@Serializable
object RoutePreferences

open class NavCallbacks {
	open fun onRouteMain() {}
	open fun onRouteWebShowView() {}
	open fun onRouteSourceList() {}
	open fun onRouteSourceEdit(sourceID: Long?) {}
	open fun onRouteSourceTest(process: String) {}
	open fun onRoutePreferences() {}
	open fun onBack() {}
}