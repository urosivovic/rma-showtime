package rs.edu.raf.rma.showtime.core.navigation

object ShowtimeRoute {
    const val Movies = "movies"
    const val Filter = "filter"
    const val MovieIdArg = "movieId"
    const val MovieDetailsPattern = "movie/{$MovieIdArg}"

    fun movieDetails(movieId: String): String = "movie/$movieId"
}
