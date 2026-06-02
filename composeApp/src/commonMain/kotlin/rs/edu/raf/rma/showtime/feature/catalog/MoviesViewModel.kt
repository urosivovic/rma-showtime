package rs.edu.raf.rma.showtime.feature.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rs.edu.raf.rma.showtime.data.repository.MovieRepository

class MoviesViewModel(
    private val repository: MovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesScreenState())
    val state: StateFlow<MoviesScreenState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<MoviesEffect>()
    val effects: SharedFlow<MoviesEffect> = _effects.asSharedFlow()

    private var loadMoviesJob: Job? = null
    private var loadGenresJob: Job? = null

    init {
        loadGenres()
        loadMovies()
    }

    fun onIntent(intent: MoviesIntent) {
        when (intent) {
            MoviesIntent.RetryRequested -> {
                if (state.value.availableGenres.isEmpty()) {
                    loadGenres()
                }
                loadMovies()
            }

            is MoviesIntent.SortSelected -> {
                dispatch(MoviesAction.SortSelected(intent.sortOption))
                loadMovies()
            }

            MoviesIntent.FilterClicked -> {
                dispatch(MoviesAction.FiltersEditingStarted)
                sendEffect(MoviesEffect.NavigateToFilters)
            }

            MoviesIntent.DiscardFiltersClicked -> {
                dispatch(MoviesAction.DraftFiltersDiscarded)
                sendEffect(MoviesEffect.CloseFilters)
            }

            MoviesIntent.ClearDraftFiltersClicked -> {
                dispatch(MoviesAction.DraftFiltersCleared)
            }

            MoviesIntent.ApplyFiltersClicked -> {
                dispatch(MoviesAction.DraftFiltersApplied)
                loadMovies()
                sendEffect(MoviesEffect.CloseFilters)
            }

            is MoviesIntent.MovieClicked -> {
                sendEffect(MoviesEffect.NavigateToDetails(intent.movieId))
            }

            is MoviesIntent.QueryChanged -> {
                dispatch(MoviesAction.QueryChanged(intent.query))
            }

            is MoviesIntent.GenreToggled -> {
                dispatch(MoviesAction.GenreToggled(intent.genre))
            }

            is MoviesIntent.MinYearChanged -> {
                dispatch(MoviesAction.MinYearChanged(intent.value))
            }

            is MoviesIntent.MaxYearChanged -> {
                dispatch(MoviesAction.MaxYearChanged(intent.value))
            }

            is MoviesIntent.MinRatingChanged -> {
                dispatch(MoviesAction.MinRatingChanged(intent.value))
            }
        }
    }

    private fun dispatch(action: MoviesAction) {
        _state.update { current ->
            MoviesReducer.reduce(current, action)
        }
    }

    private fun sendEffect(effect: MoviesEffect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    private fun loadGenres() {
        loadGenresJob?.cancel()
        loadGenresJob = viewModelScope.launch {
            runCatching {
                repository.getGenres()
            }.onSuccess { genres ->
                dispatch(MoviesAction.GenresLoaded(genres))
            }.onFailure { throwable ->
                sendEffect(
                    MoviesEffect.ShowMessage(
                        throwable.message ?: "Something went wrong while loading genres.",
                    ),
                )
            }
        }
    }

    private fun loadMovies() {
        loadMoviesJob?.cancel()
        loadMoviesJob = viewModelScope.launch {
            dispatch(MoviesAction.LoadingStarted)

            val currentState = state.value
            runCatching {
                repository.getMovies(
                    filter = currentState.appliedFilter,
                    sort = currentState.selectedSort,
                )
            }.onSuccess { movies ->
                dispatch(MoviesAction.MoviesLoaded(movies))
            }.onFailure { throwable ->
                dispatch(
                    MoviesAction.MoviesLoadingFailed(
                        throwable.message ?: "Something went wrong while loading movies.",
                    ),
                )
            }
        }
    }
}
