package rs.edu.raf.rma.showtime

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinConfiguration
import rs.edu.raf.rma.showtime.feature.details.MovieDetailsIntent
import rs.edu.raf.rma.showtime.feature.details.MovieDetailsScreen
import rs.edu.raf.rma.showtime.feature.details.MovieDetailsViewModel
import rs.edu.raf.rma.showtime.di.showtimeModule
import rs.edu.raf.rma.showtime.feature.catalog.FilterScreen
import rs.edu.raf.rma.showtime.feature.catalog.MoviesIntent
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

        MaterialTheme(colorScheme = showtimeColorScheme) {
            Surface(
                color = MaterialTheme.colorScheme.background,
            ) {
                NavHost(
                    navController = navController,
                    startDestination = ShowtimeRoute.Movies,
                ) {
                    composable(ShowtimeRoute.Movies) {
                        MoviesListScreen(
                            state = moviesState,
                            onFilterClick = {
                                moviesViewModel.onIntent(MoviesIntent.StartEditingFilters)
                                navController.navigate(ShowtimeRoute.Filter) {
                                    launchSingleTop = true
                                }
                            },
                            onMovieClick = { movie ->
                                navController.navigate(ShowtimeRoute.movieDetails(movie.imdbId)) {
                                    launchSingleTop = true
                                }
                            },
                            onIntent = moviesViewModel::onIntent,
                        )
                    }

                    composable(ShowtimeRoute.Filter) {
                        FilterScreen(
                            state = moviesState.draftFilter,
                            availableGenres = moviesState.availableGenres,
                            onIntent = moviesViewModel::onIntent,
                            onBackClick = {
                                moviesViewModel.onIntent(MoviesIntent.DiscardDraftChanges)
                                navController.popBackStack()
                            },
                            onApplyClick = {
                                moviesViewModel.onIntent(MoviesIntent.ApplyDraftFilters)
                                navController.popBackStack()
                            },
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

                        MovieDetailsScreen(
                            state = detailsState,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onRetryClick = {
                                detailsViewModel.onIntent(MovieDetailsIntent.RetryRequested)
                            },
                        )
                    }
                }
            }
        }
    }
}
