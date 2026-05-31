package rs.edu.raf.rma.showtime.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private var loadJob: Job? = null

    init {
        loadMovie()
    }

    fun onIntent(intent: MovieDetailsIntent) {
        when (intent) {
            MovieDetailsIntent.RetryRequested -> loadMovie()
        }
    }

    private fun loadMovie() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { current ->
                current.copy(contentState = MovieDetailsContentState.Loading)
            }

            runCatching {
                repository.getMovieDetails(movieId)
            }.onSuccess { movie ->
                _state.update { current ->
                    current.copy(contentState = MovieDetailsContentState.Success(movie))
                }
            }.onFailure { throwable ->
                _state.update { current ->
                    current.copy(
                        contentState = MovieDetailsContentState.Error(
                            throwable.message ?: "Something went wrong while loading movie details.",
                        ),
                    )
                }
            }
        }
    }
}
