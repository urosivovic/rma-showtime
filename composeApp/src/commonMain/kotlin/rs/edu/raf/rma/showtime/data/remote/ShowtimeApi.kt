package rs.edu.raf.rma.showtime.data.remote

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

interface ShowtimeApi {

    @POST("auth/signup")
    suspend fun signup(
        @Body body: SignupRequestDto,
    ): AuthResponseDto

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequestDto,
    ): AuthResponseDto

    @GET("me")
    suspend fun getMe(
        @Header("Authorization") authorization: String,
    ): UserDto

    @GET("movies")
    suspend fun getMovies(
        @Query("page_size") pageSize: Int,
        @Query("sort_by") sortBy: String,
        @Query("sort_order") sortOrder: String,
        @Query("query") query: String? = null,
        @Query("genre_id") genreId: Int? = null,
        @Query("min_year") minYear: Int? = null,
        @Query("max_year") maxYear: Int? = null,
        @Query("min_rating") minRating: Float? = null,
    ): PaginatedResponseDto<MovieListItemDto>

    @GET("genres")
    suspend fun getGenres(): List<GenreDto>

    @GET("config")
    suspend fun getConfig(): List<ConfigEntryDto>

    @GET("movies/{movieId}")
    suspend fun getMovieDetails(
        @Path("movieId") movieId: String,
    ): MovieDetailDto

    @GET("movies/{movieId}/images")
    suspend fun getMovieImages(
        @Path("movieId") movieId: String,
        @Query("type") type: String = "backdrop",
    ): MovieImagesDto

    @GET("movies/{movieId}/cast")
    suspend fun getMovieCast(
        @Path("movieId") movieId: String,
        @Query("page_size") pageSize: Int = 10,
    ): PaginatedResponseDto<PersonSummaryDto>

    @GET("movies/{movieId}/videos")
    suspend fun getMovieVideos(
        @Path("movieId") movieId: String,
        @Query("type") type: String = "Trailer",
    ): List<MovieVideoDto>
}
