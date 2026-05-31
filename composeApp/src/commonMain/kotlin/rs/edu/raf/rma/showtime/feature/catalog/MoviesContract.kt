package rs.edu.raf.rma.showtime.feature.catalog

import rs.edu.raf.rma.showtime.core.mvi.UiEffect
import rs.edu.raf.rma.showtime.core.mvi.UiIntent
import rs.edu.raf.rma.showtime.core.mvi.UiState
import rs.edu.raf.rma.showtime.core.model.GenreUiModel
import rs.edu.raf.rma.showtime.core.model.MovieCardUiModel
import rs.edu.raf.rma.showtime.core.model.MovieSortOption
import rs.edu.raf.rma.showtime.core.model.MoviesFilterState

sealed interface MoviesListContentState {
    data object Loading : MoviesListContentState
    data object Empty : MoviesListContentState
    data class Success(val movies: List<MovieCardUiModel>) : MoviesListContentState
    data class Error(val message: String) : MoviesListContentState
}

data class MoviesScreenState(
    val listState: MoviesListContentState = MoviesListContentState.Loading,
    val selectedSort: MovieSortOption = MovieSortOption.Rating,
    val appliedFilter: MoviesFilterState = MoviesFilterState(),
    val draftFilter: MoviesFilterState = MoviesFilterState(),
    val availableGenres: List<GenreUiModel> = emptyList(),
) : UiState

sealed interface MoviesIntent : UiIntent {
    data object RetryRequested : MoviesIntent
    data class SortSelected(val sortOption: MovieSortOption) : MoviesIntent
    data object StartEditingFilters : MoviesIntent
    data object DiscardDraftChanges : MoviesIntent
    data object ClearDraftFilters : MoviesIntent
    data object ApplyDraftFilters : MoviesIntent
    data class QueryChanged(val query: String) : MoviesIntent
    data class GenreToggled(val genre: GenreUiModel) : MoviesIntent
    data class MinYearChanged(val value: String) : MoviesIntent
    data class MaxYearChanged(val value: String) : MoviesIntent
    data class MinRatingChanged(val value: Float) : MoviesIntent
}

sealed interface MoviesEffect : UiEffect
