package rs.edu.raf.rma.showtime.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import rs.edu.raf.rma.showtime.core.auth.AuthenticatedRequestRunner
import rs.edu.raf.rma.showtime.core.auth.SessionManager
import rs.edu.raf.rma.showtime.core.datastore.TokenStore
import rs.edu.raf.rma.showtime.core.datastore.createPlatformTokenDataStore
import rs.edu.raf.rma.showtime.data.repository.AuthRepository
import rs.edu.raf.rma.showtime.data.repository.AuthRepositoryImpl
import rs.edu.raf.rma.showtime.data.repository.MovieRepository
import rs.edu.raf.rma.showtime.data.repository.MovieRepositoryImpl
import rs.edu.raf.rma.showtime.data.repository.createShowtimeApi
import rs.edu.raf.rma.showtime.data.repository.createShowtimeHttpClient
import rs.edu.raf.rma.showtime.feature.auth.AuthViewModel
import rs.edu.raf.rma.showtime.feature.details.MovieDetailsViewModel
import rs.edu.raf.rma.showtime.feature.catalog.MoviesViewModel

val showtimeModule = module {
    single { createPlatformTokenDataStore() }
    single { TokenStore(get()) }
    single { SessionManager(get()) }
    single { AuthenticatedRequestRunner(get()) }
    single { createShowtimeHttpClient() }
    single { createShowtimeApi(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<MovieRepository> { MovieRepositoryImpl(get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { MoviesViewModel(get()) }
    viewModel { (movieId: String) ->
        MovieDetailsViewModel(movieId = movieId, repository = get())
    }
}
