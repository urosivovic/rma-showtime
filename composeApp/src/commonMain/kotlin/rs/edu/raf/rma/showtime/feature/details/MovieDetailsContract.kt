package rs.edu.raf.rma.showtime.feature.details

import rs.edu.raf.rma.showtime.core.mvi.UiEffect
import rs.edu.raf.rma.showtime.core.mvi.UiIntent
import rs.edu.raf.rma.showtime.core.mvi.UiState
import rs.edu.raf.rma.showtime.core.model.MovieDetailsUiModel

sealed interface MovieDetailsContentState {
    data object Loading : MovieDetailsContentState
    data class Success(val movie: MovieDetailsUiModel) : MovieDetailsContentState
    data class Error(val message: String) : MovieDetailsContentState
}

data class MovieDetailsScreenState(
    val contentState: MovieDetailsContentState = MovieDetailsContentState.Loading,
) : UiState

sealed interface MovieDetailsIntent : UiIntent {
    data object RetryRequested : MovieDetailsIntent
}

sealed interface MovieDetailsEffect : UiEffect
