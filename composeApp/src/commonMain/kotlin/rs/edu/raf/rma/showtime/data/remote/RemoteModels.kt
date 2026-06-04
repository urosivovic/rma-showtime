package rs.edu.raf.rma.showtime.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import rs.edu.raf.rma.showtime.core.auth.AuthUser
import rs.edu.raf.rma.showtime.core.model.GenreUiModel
import rs.edu.raf.rma.showtime.core.model.MovieCardUiModel
import rs.edu.raf.rma.showtime.core.model.MovieDetailsUiModel
import rs.edu.raf.rma.showtime.core.model.PersonUiModel

@Serializable
data class PaginatedResponseDto<T>(
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
    val items: List<T>,
)

@Serializable
data class GenreDto(
    val id: Int,
    val name: String,
)

@Serializable
data class MovieListItemDto(
    val imdbId: String,
    val title: String,
    val year: Int? = null,
    val runtime: Int? = null,
    val imdbRating: Float? = null,
    val imdbVotes: Int? = null,
    val posterPath: String? = null,
    val popularity: Float? = null,
    val genres: List<GenreDto> = emptyList(),
)

@Serializable
data class MovieDetailDto(
    val imdbId: String,
    val title: String,
    val overview: String? = null,
    val year: Int? = null,
    val runtime: Int? = null,
    val budget: Long? = null,
    val revenue: Long? = null,
    val languageCode: String? = null,
    val popularity: Float? = null,
    val imdbRating: Float? = null,
    val imdbVotes: Int? = null,
    val tmdbRating: Float? = null,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val genres: List<GenreDto> = emptyList(),
)

@Serializable
data class PersonSummaryDto(
    val imdbId: String,
    val name: String,
    val profilePath: String? = null,
)

@Serializable
data class MovieImageDto(
    val filePath: String,
)

@Serializable
data class MovieImagesDto(
    val posters: List<MovieImageDto> = emptyList(),
    val backdrops: List<MovieImageDto> = emptyList(),
    val logos: List<MovieImageDto> = emptyList(),
)

@Serializable
data class MovieVideoDto(
    val key: String,
    val site: String,
    val name: String? = null,
    val type: String? = null,
    val official: Boolean = false,
)

@Serializable
data class ConfigEntryDto(
    val key: String,
    val value: String,
)

@Serializable
data class SignupRequestDto(
    @SerialName("full_name")
    val fullName: String,
    val username: String,
    val password: String,
)

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String,
)

@Serializable
data class AuthResponseDto(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Long,
    val user: UserDto,
)

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    @SerialName("full_name")
    val fullName: String,
)

data class ImageConfig(
    val imageBaseUrl: String,
    val posterSizes: List<String>,
    val backdropSizes: List<String>,
)

fun GenreDto.toUiModel(): GenreUiModel = GenreUiModel(
    id = id,
    name = name,
)

fun UserDto.toDomain(): AuthUser = AuthUser(
    id = id,
    username = username,
    fullName = fullName,
)

fun MovieListItemDto.toUiModel(imageConfig: ImageConfig): MovieCardUiModel = MovieCardUiModel(
    imdbId = imdbId,
    title = title,
    year = year ?: 0,
    runtimeMinutes = runtime,
    rating = imdbRating.toRatingLabel(),
    votesLabel = imdbVotes.toVotesLabel(),
    genres = genres.map { genre -> genre.toUiModel() },
    posterUrl = imageConfig.posterUrl(posterPath),
    popularity = popularity ?: 0f,
)

fun MovieDetailDto.toUiModel(
    imageConfig: ImageConfig,
    images: MovieImagesDto,
    cast: List<PersonSummaryDto>,
    videos: List<MovieVideoDto>,
): MovieDetailsUiModel = MovieDetailsUiModel(
    imdbId = imdbId,
    title = title,
    year = year ?: 0,
    runtimeMinutes = runtime,
    imdbRating = imdbRating.toRatingLabel(),
    imdbVotesLabel = imdbVotes.toVotesLabel(),
    tmdbRating = tmdbRating.toRatingLabel(),
    genres = genres.map { genre -> genre.toUiModel() },
    overview = overview?.ifBlank { null } ?: "No overview available.",
    budgetLabel = budget.toMoneyLabel(),
    revenueLabel = revenue.toMoneyLabel(),
    languageLabel = languageCode?.uppercase() ?: "N/A",
    popularityLabel = popularity.toCompactDecimalLabel(),
    posterUrl = imageConfig.posterUrl(posterPath),
    backdropUrl = imageConfig.backdropUrl(backdropPath ?: images.backdrops.firstOrNull()?.filePath),
    imageUrls = images.backdrops
        .take(4)
        .mapNotNull { image -> imageConfig.backdropUrl(image.filePath) },
    trailerUrl = videos.toTrailerUrl(),
    cast = cast.map { person -> person.toUiModel(imageConfig) },
)

fun PersonSummaryDto.toUiModel(imageConfig: ImageConfig): PersonUiModel = PersonUiModel(
    imdbId = imdbId,
    name = name,
    profileUrl = imageConfig.profileUrl(profilePath),
)

fun List<ConfigEntryDto>.toImageConfig(): ImageConfig {
    val configMap = associate { entry -> entry.key to entry.value }

    return ImageConfig(
        imageBaseUrl = configMap["image_base_url"].orEmpty(),
        posterSizes = configMap["poster_sizes"].orEmpty().splitCommaSeparated(),
        backdropSizes = configMap["backdrop_sizes"].orEmpty().splitCommaSeparated(),
    )
}

private fun String.splitCommaSeparated(): List<String> =
    split(',')
        .map { value -> value.trim() }
        .filter { value -> value.isNotEmpty() }

private fun Float?.toRatingLabel(): String {
    val value = this ?: return "0.0"
    val rounded = (value * 10).toInt()
    val whole = rounded / 10
    val decimal = rounded % 10
    return "$whole.$decimal"
}

private fun Float?.toCompactDecimalLabel(): String {
    val value = this ?: return "N/A"
    val rounded = (value * 10).toInt()
    val whole = rounded / 10
    val decimal = rounded % 10
    return "$whole.$decimal"
}

private fun Int?.toVotesLabel(): String {
    val value = this ?: return "0 votes"

    return when {
        value >= 1_000_000 -> "${value / 1_000_000}.${(value % 1_000_000) / 100_000}M votes"
        value >= 1_000 -> "${value / 1_000}K votes"
        else -> "$value votes"
    }
}

private fun Long?.toMoneyLabel(): String {
    val value = this ?: return "N/A"

    return when {
        value >= 1_000_000_000L -> "$${value / 1_000_000_000}B"
        value >= 1_000_000L -> "$${value / 1_000_000}M"
        value >= 1_000L -> "$${value / 1_000}K"
        else -> "$$value"
    }
}

private fun List<MovieVideoDto>.toTrailerUrl(): String? {
    val preferredVideo = firstOrNull { video ->
        video.site.equals("YouTube", ignoreCase = true) && video.official
    } ?: firstOrNull { video ->
        video.site.equals("YouTube", ignoreCase = true)
    } ?: return null

    return "https://www.youtube.com/watch?v=${preferredVideo.key}"
}

private fun ImageConfig.posterUrl(path: String?): String? {
    val posterSize = posterSizes.firstOrNull { size -> size == "w185" }
        ?: posterSizes.firstOrNull()
        ?: return null
    val posterPath = path ?: return null
    return "$imageBaseUrl$posterSize$posterPath"
}

private fun ImageConfig.backdropUrl(path: String?): String? {
    val backdropSize = backdropSizes.firstOrNull { size -> size == "w780" }
        ?: backdropSizes.firstOrNull()
        ?: return null
    val backdropPath = path ?: return null
    return "$imageBaseUrl$backdropSize$backdropPath"
}

private fun ImageConfig.profileUrl(path: String?): String? {
    val profilePath = path ?: return null
    return "${imageBaseUrl}w185$profilePath"
}
