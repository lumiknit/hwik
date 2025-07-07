package lumiknit.app.hwik

import kotlinx.serialization.Serializable

@Serializable
object RouteMain

@Serializable
data class RouteSearched(
	val keyword: String
)

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

abstract class NavCallbacks {
	abstract fun onRouteMain()
	abstract fun onRouteSearched(keyword: String)
	abstract fun onRouteWebShowView()
	abstract fun onRouteSourceList()
	abstract fun onRouteSourceEdit(sourceID: Long?)
	abstract fun onRouteSourceTest(process: String)
	abstract fun onRoutePreferences()
	abstract fun onBack()
}