package rs.edu.raf.rma.showtime.feature.details

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

class MovieDetailsViewModel(
    private val movieId: String,
    private val repository: MovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailsScreenState())
    val state: StateFlow<MovieDetailsScreenState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<MovieDetailsEffect>()
    val effects: SharedFlow<MovieDetailsEffect> = _effects.asSharedFlow()

    private var loadJob: Job? = null

    init {
        loadMovie()
    }

    fun onIntent(intent: MovieDetailsIntent) {
        when (intent) {
            MovieDetailsIntent.BackClicked -> {
                sendEffect(MovieDetailsEffect.NavigateBack)
            }

            MovieDetailsIntent.RetryRequested -> loadMovie()
        }
    }

    private fun dispatch(action: MovieDetailsAction) {
        _state.update { current ->
            MovieDetailsReducer.reduce(current, action)
        }
    }

    private fun sendEffect(effect: MovieDetailsEffect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    private fun loadMovie() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            dispatch(MovieDetailsAction.LoadingStarted)

            runCatching {
                repository.getMovieDetails(movieId)
            }.onSuccess { movie ->
                dispatch(MovieDetailsAction.MovieLoaded(movie))
            }.onFailure { throwable ->
                dispatch(
                    MovieDetailsAction.MovieLoadingFailed(
                        throwable.message ?: "Something went wrong while loading movie details.",
                    ),
                )
            }
        }
    }
}
