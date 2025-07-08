package lumiknit.app.hwik

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.delay
import lumiknit.app.hwik.screen.main.MainScreen
import lumiknit.app.hwik.screen.prefs.PreferencesView
import lumiknit.app.hwik.screen.searched.SearchedScreen
import lumiknit.app.hwik.screen.sources.SourceEditScreen
import lumiknit.app.hwik.screen.sources.SourceListScreen
import lumiknit.app.hwik.screen.sources.SourceTestScreen
import lumiknit.app.hwik.screen.webview.WebShowScreen
import lumiknit.app.hwik.screen.webworker.WebWorkerSet

// Routes

@Composable
fun RootView() {
	val navController = rememberNavController()

	// Handle navigation

	LaunchedEffect(Unit) {
		while (true) {
			delay(100)
			val received = Navigator.channel.receive()
			when (received) {
				is Navigator.RouteBack -> navController.navigateUp()

				is Navigator.RouteMain -> navController.navigate(Navigator.RouteMain) {
					// Clear the back stack to prevent going back to the previous screen
					popUpTo(Navigator.RouteMain) { inclusive = true }
				}

				else -> navController.navigate(received)
			}
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
			startDestination = Navigator.RouteMain,
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
			composable<Navigator.RouteMain> {
				MainScreen()
			}
			composable<Navigator.RouteSearched> { v ->
				val e: Navigator.RouteSearched = v.toRoute()
				SearchedScreen(
					searchKeyword = e.keyword,
				)
			}
			composable<Navigator.RouteWebViewScreen> {
				WebShowScreen(modifier = Modifier)
			}
			composable<Navigator.RouteSourceList> {
				SourceListScreen(
				)
			}
			composable<Navigator.RouteSourceEdit> { v ->
				val e: Navigator.RouteSourceEdit = v.toRoute()
				SourceEditScreen(
					sourceID = e.sourceID,
				)
			}
			composable<Navigator.RouteSourceTest> { v ->
				val e: Navigator.RouteSourceTest = v.toRoute()
				SourceTestScreen(
					processJSON = e.processStr,
				)
			}
			composable<Navigator.RoutePreferences> {
				PreferencesView(
				)
			}
		}

		WebWorkerSet(3)

		ModalContainer()
	}
}