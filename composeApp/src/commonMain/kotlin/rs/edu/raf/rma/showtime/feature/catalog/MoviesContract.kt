package rs.edu.raf.rma.showtime.feature.catalog

import rs.edu.raf.rma.showtime.core.mvi.UiEffect
import rs.edu.raf.rma.showtime.core.mvi.UiIntent
import rs.edu.raf.rma.showtime.core.mvi.UiState
import rs.edu.raf.rma.showtime.core.mvi.UiAction
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
    data object FilterClicked : MoviesIntent
    data object DiscardFiltersClicked : MoviesIntent
    data object ClearDraftFiltersClicked : MoviesIntent
    data object ApplyFiltersClicked : MoviesIntent
    data class MovieClicked(val movieId: String) : MoviesIntent
    data class QueryChanged(val query: String) : MoviesIntent
    data class GenreToggled(val genre: GenreUiModel) : MoviesIntent
    data class MinYearChanged(val value: String) : MoviesIntent
    data class MaxYearChanged(val value: String) : MoviesIntent
    data class MinRatingChanged(val value: Float) : MoviesIntent
}

sealed interface MoviesAction : UiAction {
    data object LoadingStarted : MoviesAction
    data class MoviesLoaded(val movies: List<MovieCardUiModel>) : MoviesAction
    data class MoviesLoadingFailed(val message: String) : MoviesAction
    data class GenresLoaded(val genres: List<GenreUiModel>) : MoviesAction
    data class SortSelected(val sortOption: MovieSortOption) : MoviesAction
    data object FiltersEditingStarted : MoviesAction
    data object DraftFiltersDiscarded : MoviesAction
    data object DraftFiltersCleared : MoviesAction
    data object DraftFiltersApplied : MoviesAction
    data class QueryChanged(val query: String) : MoviesAction
    data class GenreToggled(val genre: GenreUiModel) : MoviesAction
    data class MinYearChanged(val value: String) : MoviesAction
    data class MaxYearChanged(val value: String) : MoviesAction
    data class MinRatingChanged(val value: Float) : MoviesAction
}

sealed interface MoviesEffect : UiEffect {
    data object NavigateToFilters : MoviesEffect
    data object CloseFilters : MoviesEffect
    data class NavigateToDetails(val movieId: String) : MoviesEffect
    data class ShowMessage(val message: String) : MoviesEffect
}
