package rs.edu.raf.rma.showtime.feature.catalog

import rs.edu.raf.rma.showtime.core.model.MoviesFilterState
import rs.edu.raf.rma.showtime.core.model.toggleGenre

object MoviesReducer {
    fun reduce(
        state: MoviesScreenState,
        action: MoviesAction,
    ): MoviesScreenState =
        when (action) {
            MoviesAction.LoadingStarted -> state.copy(
                listState = MoviesListContentState.Loading,
            )

            is MoviesAction.MoviesLoaded -> state.copy(
                listState = if (action.movies.isEmpty()) {
                    MoviesListContentState.Empty
                } else {
                    MoviesListContentState.Success(action.movies)
                },
            )

            is MoviesAction.MoviesLoadingFailed -> state.copy(
                listState = MoviesListContentState.Error(action.message),
            )

            is MoviesAction.GenresLoaded -> state.copy(
                availableGenres = action.genres,
            )

            is MoviesAction.SortSelected -> state.copy(
                selectedSort = action.sortOption,
            )

            MoviesAction.FiltersEditingStarted -> state.copy(
                draftFilter = state.appliedFilter,
            )

            MoviesAction.DraftFiltersDiscarded -> state.copy(
                draftFilter = state.appliedFilter,
            )

            MoviesAction.DraftFiltersCleared -> state.copy(
                draftFilter = MoviesFilterState(),
            )

            MoviesAction.DraftFiltersApplied -> state.copy(
                appliedFilter = state.draftFilter,
            )

            is MoviesAction.QueryChanged -> state.copy(
                draftFilter = state.draftFilter.copy(query = action.query),
            )

            is MoviesAction.GenreToggled -> state.copy(
                draftFilter = state.draftFilter.toggleGenre(action.genre),
            )

            is MoviesAction.MinYearChanged -> state.copy(
                draftFilter = state.draftFilter.copy(
                    minYearInput = action.value.filter(Char::isDigit),
                ),
            )

            is MoviesAction.MaxYearChanged -> state.copy(
                draftFilter = state.draftFilter.copy(
                    maxYearInput = action.value.filter(Char::isDigit),
                ),
            )

            is MoviesAction.MinRatingChanged -> state.copy(
                draftFilter = state.draftFilter.copy(minRating = action.value),
            )
        }
}
