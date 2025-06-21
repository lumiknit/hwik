package lumiknit.app.hwik

import kotlinx.serialization.Serializable

@Serializable
object RouteMain

@Serializable
object RouteSourceList

@Serializable
data class RouteSourceEdit(
	// If null, it means to create a new source.
	var sourceID: String? = null,
)

@Serializable
object RoutePreferences

open class NavCallbacks {
	open fun onRouteMain() {}
	open fun onRouteWebShowView() {}
	open fun onRouteSourceList() {}
	open fun onRouteSourceEdit(sourceID: String?) {}
	open fun onRoutePreferences() {}
	open fun onBack() {}
}