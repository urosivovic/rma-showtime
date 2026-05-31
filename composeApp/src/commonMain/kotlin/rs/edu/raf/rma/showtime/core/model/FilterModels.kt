package rs.edu.raf.rma.showtime.core.model

data class MoviesFilterState(
    val query: String = "",
    val selectedGenre: GenreUiModel? = null,
    val minYearInput: String = "",
    val maxYearInput: String = "",
    val minRating: Float = 0f,
)

enum class MovieSortOption(
    val label: String,
    val apiValue: String,
    val apiOrder: String = "desc",
) {
    Rating("Rating", "imdb_rating"),
    Year("Year", "year"),
    Title("Title", "title", "asc"),
    Popularity("Popularity", "popularity"),
}

fun MoviesFilterState.toggleGenre(genre: GenreUiModel): MoviesFilterState =
    copy(
        selectedGenre = if (selectedGenre?.id == genre.id) {
            null
        } else {
            genre
        },
    )

fun MoviesFilterState.activeFiltersCount(): Int {
    var count = 0
    if (query.isNotBlank()) count += 1
    if (selectedGenre != null) count += 1
    if (minYearInput.isNotBlank()) count += 1
    if (maxYearInput.isNotBlank()) count += 1
    if (minRating > 0f) count += 1
    return count
}
