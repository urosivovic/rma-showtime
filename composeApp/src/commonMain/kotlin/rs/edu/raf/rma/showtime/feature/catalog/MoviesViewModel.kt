package rs.edu.raf.rma.showtime.feature.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rs.edu.raf.rma.showtime.core.model.MoviesFilterState
import rs.edu.raf.rma.showtime.core.model.toggleGenre
import rs.edu.raf.rma.showtime.data.repository.MovieRepository

class MoviesViewModel(
    private val repository: MovieRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesScreenState())
    val state: StateFlow<MoviesScreenState> = _state.asStateFlow()

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
                _state.update { current ->
                    current.copy(selectedSort = intent.sortOption)
                }
                loadMovies()
            }

            MoviesIntent.StartEditingFilters -> {
                _state.update { current ->
                    current.copy(draftFilter = current.appliedFilter)
                }
            }

            MoviesIntent.DiscardDraftChanges -> {
                _state.update { current ->
                    current.copy(draftFilter = current.appliedFilter)
                }
            }

            MoviesIntent.ClearDraftFilters -> {
                _state.update { current ->
                    current.copy(draftFilter = MoviesFilterState())
                }
            }

            MoviesIntent.ApplyDraftFilters -> {
                _state.update { current ->
                    current.copy(appliedFilter = current.draftFilter)
                }
                loadMovies()
            }

            is MoviesIntent.QueryChanged -> {
                _state.update { current ->
                    current.copy(
                        draftFilter = current.draftFilter.copy(query = intent.query),
                    )
                }
            }

            is MoviesIntent.GenreToggled -> {
                _state.update { current ->
                    current.copy(
                        draftFilter = current.draftFilter.toggleGenre(intent.genre),
                    )
                }
            }

            is MoviesIntent.MinYearChanged -> {
                _state.update { current ->
                    current.copy(
                        draftFilter = current.draftFilter.copy(
                            minYearInput = intent.value.filter(Char::isDigit),
                        ),
                    )
                }
            }

            is MoviesIntent.MaxYearChanged -> {
                _state.update { current ->
                    current.copy(
                        draftFilter = current.draftFilter.copy(
                            maxYearInput = intent.value.filter(Char::isDigit),
                        ),
                    )
                }
            }

            is MoviesIntent.MinRatingChanged -> {
                _state.update { current ->
                    current.copy(
                        draftFilter = current.draftFilter.copy(minRating = intent.value),
                    )
                }
            }
        }
    }

    private fun loadGenres() {
        loadGenresJob?.cancel()
        loadGenresJob = viewModelScope.launch {
            runCatching {
                repository.getGenres()
            }.onSuccess { genres ->
                _state.update { current ->
                    current.copy(availableGenres = genres)
                }
            }
        }
    }

    private fun loadMovies() {
        loadMoviesJob?.cancel()
        loadMoviesJob = viewModelScope.launch {
            _state.update { current ->
                current.copy(listState = MoviesListContentState.Loading)
            }

            val currentState = state.value
            runCatching {
                repository.getMovies(
                    filter = currentState.appliedFilter,
                    sort = currentState.selectedSort,
                )
            }.onSuccess { movies ->
                _state.update { current ->
                    current.copy(
                        listState = if (movies.isEmpty()) {
                            MoviesListContentState.Empty
                        } else {
                            MoviesListContentState.Success(movies)
                        },
                    )
                }
            }.onFailure { throwable ->
                _state.update { current ->
                    current.copy(
                        listState = MoviesListContentState.Error(
                            throwable.message ?: "Something went wrong while loading movies.",
                        ),
                    )
                }
            }
        }
    }
}
