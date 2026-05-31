package rs.edu.raf.rma.showtime.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import rs.edu.raf.rma.showtime.data.repository.MovieRepository
import rs.edu.raf.rma.showtime.data.repository.MovieRepositoryImpl
import rs.edu.raf.rma.showtime.data.repository.createShowtimeApi
import rs.edu.raf.rma.showtime.data.repository.createShowtimeHttpClient
import rs.edu.raf.rma.showtime.feature.details.MovieDetailsViewModel
import rs.edu.raf.rma.showtime.feature.catalog.MoviesViewModel

val showtimeModule = module {
    single { createShowtimeHttpClient() }
    single { createShowtimeApi(get()) }
    single<MovieRepository> { MovieRepositoryImpl(get()) }

    viewModel { MoviesViewModel(get()) }
    viewModel { (movieId: String) ->
        MovieDetailsViewModel(movieId = movieId, repository = get())
    }
}
