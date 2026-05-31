package rs.edu.raf.rma.showtime.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import rs.edu.raf.rma.showtime.core.model.GenreUiModel
import rs.edu.raf.rma.showtime.core.model.MovieCardUiModel
import rs.edu.raf.rma.showtime.core.model.MovieDetailsUiModel
import rs.edu.raf.rma.showtime.core.model.MovieSortOption
import rs.edu.raf.rma.showtime.core.model.MoviesFilterState
import rs.edu.raf.rma.showtime.data.remote.ShowtimeApi
import rs.edu.raf.rma.showtime.data.remote.createShowtimeApi
import rs.edu.raf.rma.showtime.data.remote.toImageConfig
import rs.edu.raf.rma.showtime.data.remote.toUiModel
import rs.edu.raf.rma.showtime.data.remote.ImageConfig

interface MovieRepository {
    suspend fun getMovies(
        filter: MoviesFilterState,
        sort: MovieSortOption,
    ): List<MovieCardUiModel>

    suspend fun getGenres(): List<GenreUiModel>

    suspend fun getMovieDetails(movieId: String): MovieDetailsUiModel
}

class MovieRepositoryImpl(
    private val api: ShowtimeApi,
) : MovieRepository {

    private var cachedImageConfig: ImageConfig? = null

    override suspend fun getMovies(
        filter: MoviesFilterState,
        sort: MovieSortOption,
    ): List<MovieCardUiModel> {
        val imageConfig = getImageConfig()
        val response = api.getMovies(
            pageSize = 30,
            sortBy = sort.apiValue,
            sortOrder = sort.apiOrder,
            query = filter.query.trim().takeIf { value -> value.isNotBlank() },
            genreId = filter.selectedGenre?.id,
            minYear = filter.minYearInput.toIntOrNull(),
            maxYear = filter.maxYearInput.toIntOrNull(),
            minRating = filter.minRating.takeIf { value -> value > 0f },
        )

        return response.items.map { movie -> movie.toUiModel(imageConfig) }
    }

    override suspend fun getGenres(): List<GenreUiModel> =
        api.getGenres().map { genre -> genre.toUiModel() }

    override suspend fun getMovieDetails(movieId: String): MovieDetailsUiModel = coroutineScope {
        val imageConfig = getImageConfig()

        val movieDetailsDeferred = async { api.getMovieDetails(movieId) }
        val imagesDeferred = async { api.getMovieImages(movieId) }
        val castDeferred = async { api.getMovieCast(movieId) }
        val videosDeferred = async { api.getMovieVideos(movieId) }

        movieDetailsDeferred.await().toUiModel(
            imageConfig = imageConfig,
            images = imagesDeferred.await(),
            cast = castDeferred.await().items,
            videos = videosDeferred.await(),
        )
    }

    private suspend fun getImageConfig(): ImageConfig =
        cachedImageConfig ?: api.getConfig().toImageConfig().also { config ->
            cachedImageConfig = config
        }
}

fun createShowtimeHttpClient(): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            },
        )
    }
}

fun createShowtimeApi(client: HttpClient): ShowtimeApi =
    de.jensklingenberg.ktorfit.Ktorfit.Builder()
        .baseUrl("https://rma.finlab.rs/")
        .httpClient(client)
        .build()
        .createShowtimeApi()
