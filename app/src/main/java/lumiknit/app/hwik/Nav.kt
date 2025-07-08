package lumiknit.app.hwik

import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.Serializable

object Navigator {
	@Serializable
	sealed class RouteObj

	@Serializable
	object RouteBack : RouteObj()

	@Serializable
	object RouteMain : RouteObj()

	@Serializable
	data class RouteSearched(
		val keyword: String
	) : RouteObj()

	@Serializable
	object RouteWebViewScreen : RouteObj()

	@Serializable
	object RouteSourceList : RouteObj()

	@Serializable
	data class RouteSourceEdit(
		// If null, it means to create a new source.
		val sourceID: Long? = null,
	) : RouteObj()

	@Serializable
	data class RouteSourceTest(
		// If null, it means to create a new source.
		val processStr: String
	) : RouteObj()

	@Serializable
	object RoutePreferences : RouteObj()

	val channel = Channel<RouteObj>(capacity = 4)

	fun go(route: RouteObj) {
		channel.trySend(route)
	}
}