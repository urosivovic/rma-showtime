package rs.edu.raf.rma.showtime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinConfiguration
import rs.edu.raf.rma.showtime.feature.details.MovieDetailsEffect
import rs.edu.raf.rma.showtime.feature.details.MovieDetailsScreen
import rs.edu.raf.rma.showtime.feature.details.MovieDetailsViewModel
import rs.edu.raf.rma.showtime.di.showtimeModule
import rs.edu.raf.rma.showtime.feature.catalog.FilterScreen
import rs.edu.raf.rma.showtime.feature.catalog.MoviesEffect
import rs.edu.raf.rma.showtime.feature.catalog.MoviesListScreen
import rs.edu.raf.rma.showtime.feature.catalog.MoviesViewModel
import rs.edu.raf.rma.showtime.core.navigation.ShowtimeRoute

private val ShowtimeBackground = Color(0xFF131313)
private val ShowtimeSurface = Color(0xFF22213A)
private val ShowtimeSurfaceAlt = Color(0xFF2E2C4A)
private val ShowtimeAccent = Color(0xFFFF2222)
private val ShowtimeTextMuted = Color(0xFF9A9AA3)
private val ShowtimeRating = Color(0xFFFFC83D)
private val ShowtimeChip = Color(0xFF3B3946)

private val showtimeColorScheme = darkColorScheme(
    background = ShowtimeBackground,
    surface = ShowtimeSurface,
    surfaceVariant = ShowtimeSurfaceAlt,
    primary = ShowtimeAccent,
    secondary = ShowtimeRating,
    tertiary = ShowtimeChip,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = ShowtimeTextMuted,
    onPrimary = Color.White,
)

@Composable
fun ShowtimeApp() {
    KoinApplication(
        configuration = koinConfiguration {
            modules(showtimeModule)
        },
    ) {
        val navController = rememberNavController()
        val moviesViewModel: MoviesViewModel = koinViewModel()
        val moviesState by moviesViewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(moviesViewModel) {
            moviesViewModel.effects.collect { effect ->
                when (effect) {
                    MoviesEffect.NavigateToFilters -> {
                        navController.navigate(ShowtimeRoute.Filter) {
                            launchSingleTop = true
                        }
                    }

                    MoviesEffect.CloseFilters -> {
                        navController.popBackStack()
                    }

                    is MoviesEffect.NavigateToDetails -> {
                        navController.navigate(ShowtimeRoute.movieDetails(effect.movieId)) {
                            launchSingleTop = true
                        }
                    }

                    is MoviesEffect.ShowMessage -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

        MaterialTheme(colorScheme = showtimeColorScheme) {
            Surface(
                color = MaterialTheme.colorScheme.background,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = ShowtimeRoute.Movies,
                    ) {
                        composable(ShowtimeRoute.Movies) {
                            MoviesListScreen(
                                state = moviesState,
                                onIntent = moviesViewModel::onIntent,
                            )
                        }

                        composable(ShowtimeRoute.Filter) {
                            FilterScreen(
                                state = moviesState.draftFilter,
                                availableGenres = moviesState.availableGenres,
                                onIntent = moviesViewModel::onIntent,
                            )
                        }

                        composable(ShowtimeRoute.MovieDetailsPattern) { backStackEntry ->
                            val movieId = backStackEntry.arguments?.read {
                                getStringOrNull(ShowtimeRoute.MovieIdArg)
                            }.orEmpty()
                            val detailsViewModel: MovieDetailsViewModel = koinViewModel(
                                key = "movie-$movieId",
                                parameters = { parametersOf(movieId) },
                            )
                            val detailsState by detailsViewModel.state.collectAsState()

                            LaunchedEffect(detailsViewModel) {
                                detailsViewModel.effects.collect { effect ->
                                    when (effect) {
                                        MovieDetailsEffect.NavigateBack -> {
                                            navController.popBackStack()
                                        }

                                        is MovieDetailsEffect.ShowMessage -> {
                                            snackbarHostState.showSnackbar(effect.message)
                                        }
                                    }
                                }
                            }

                            MovieDetailsScreen(
                                state = detailsState,
                                onIntent = detailsViewModel::onIntent,
                            )
                        }
                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }
        }
    }
}
