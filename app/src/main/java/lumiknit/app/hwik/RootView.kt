package lumiknit.app.hwik

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import lumiknit.app.hwik.components.modal.ConfirmModal
import lumiknit.app.hwik.screen.main.MainScreen
import lumiknit.app.hwik.screen.prefs.PreferencesView
import lumiknit.app.hwik.screen.sources.SourceEditScreen
import lumiknit.app.hwik.screen.sources.SourceListScreen
import lumiknit.app.hwik.screen.sources.SourceTestScreen
import lumiknit.app.hwik.screen.webcontainer.WebShowView
import lumiknit.app.hwik.state.GlobalStore

// Routes

@Composable
fun RootView() {
	val navController = rememberNavController()

	var showWebView by remember { mutableStateOf(false) }

	var navCallbacks = object : NavCallbacks() {
		override fun onRouteMain() {
			navController.navigate(RouteMain) {
				// Clear the back stack to prevent going back to the previous screen
				popUpTo(RouteMain) { inclusive = true }
			}
		}

		override fun onRouteWebShowView() {
			showWebView = true
		}

		override fun onRouteSourceList() {
			navController.navigate(RouteSourceList)
		}

		override fun onRouteSourceEdit(sourceID: Long?) {
			navController.navigate(RouteSourceEdit(sourceID))
		}

		override fun onRouteSourceTest(process: String) {
			navController.navigate(RouteSourceTest(process))
		}

		override fun onRoutePreferences() {
			navController.navigate(RoutePreferences)
		}

		override fun onBack() {
			navController.navigateUp()
		}
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(
				color = MaterialTheme.colorScheme.background
			)
	) {
		NavHost(
			navController = navController,
			startDestination = RouteMain,
			enterTransition = {
				// Slide into
				slideIntoContainer(
					AnimatedContentTransitionScope.SlideDirection.Left,
				)
			},
			exitTransition = {
				// Slide out
				slideOutOfContainer(
					AnimatedContentTransitionScope.SlideDirection.Left,
				)
			},
			popEnterTransition = {
				// Slide into
				slideIntoContainer(
					AnimatedContentTransitionScope.SlideDirection.Right,
				)
			},
			popExitTransition = {
				// Slide out
				slideOutOfContainer(
					AnimatedContentTransitionScope.SlideDirection.Right,
				)
			}
		) {
			composable<RouteMain> {
				MainScreen(
					navCallbacks = navCallbacks,
				)
			}
			composable<RouteSourceList> {
				SourceListScreen(
					navCallbacks = navCallbacks,
				)
			}
			composable<RouteSourceEdit> { v ->
				val e: RouteSourceEdit = v.toRoute()
				SourceEditScreen(
					navCallbacks = navCallbacks,
					sourceID = e.sourceID,
				)
			}
			composable<RouteSourceTest> { v ->
				val e: RouteSourceTest = v.toRoute()
				SourceTestScreen(
					navCallbacks = navCallbacks,
					processJSON = e.processStr,
				)
			}
			composable<RoutePreferences> {
				PreferencesView(
					navCallbacks = navCallbacks,
				)
			}
		}

		WebShowView(
			modifier = Modifier.zIndex(
				if (showWebView) 1f else -1f
			),
			url = "https://www.naver.com",
			onClose = {
				showWebView = false
			})

		val cmc = GlobalStore.confirmModalCallback.value
		if (cmc != null) {
			ConfirmModal(
				props = cmc
			)
		}
	}
}