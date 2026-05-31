package rs.edu.raf.rma.showtime.core.model

data class GenreUiModel(
    val id: Int,
    val name: String,
)

data class MovieCardUiModel(
    val imdbId: String,
    val title: String,
    val year: Int,
    val runtimeMinutes: Int? = null,
    val rating: String,
    val votesLabel: String,
    val genres: List<GenreUiModel>,
    val posterUrl: String? = null,
    val popularity: Float = 0f,
)

data class PersonUiModel(
    val imdbId: String,
    val name: String,
    val profileUrl: String? = null,
)

data class MovieDetailsUiModel(
    val imdbId: String,
    val title: String,
    val year: Int,
    val runtimeMinutes: Int?,
    val imdbRating: String,
    val imdbVotesLabel: String,
    val tmdbRating: String,
    val genres: List<GenreUiModel>,
    val overview: String,
    val budgetLabel: String,
    val revenueLabel: String,
    val languageLabel: String,
    val popularityLabel: String,
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val imageUrls: List<String> = emptyList(),
    val trailerUrl: String? = null,
    val cast: List<PersonUiModel> = emptyList(),
)

val MovieCardUiModel.ratingValue: Float
    get() = rating.toFloatOrNull() ?: 0f
