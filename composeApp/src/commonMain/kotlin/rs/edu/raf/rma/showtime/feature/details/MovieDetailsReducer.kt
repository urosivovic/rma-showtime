package rs.edu.raf.rma.showtime.feature.details

object MovieDetailsReducer {
    fun reduce(
        state: MovieDetailsScreenState,
        action: MovieDetailsAction,
    ): MovieDetailsScreenState =
        when (action) {
            MovieDetailsAction.LoadingStarted -> state.copy(
                contentState = MovieDetailsContentState.Loading,
            )

            is MovieDetailsAction.MovieLoaded -> state.copy(
                contentState = MovieDetailsContentState.Success(action.movie),
            )

            is MovieDetailsAction.MovieLoadingFailed -> state.copy(
                contentState = MovieDetailsContentState.Error(action.message),
            )
        }
}
