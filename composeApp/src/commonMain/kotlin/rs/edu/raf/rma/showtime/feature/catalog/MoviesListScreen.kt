package rs.edu.raf.rma.showtime.feature.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import rma_showtime.composeapp.generated.resources.Res
import rma_showtime.composeapp.generated.resources.showtime_logo
import rs.edu.raf.rma.showtime.core.model.MovieCardUiModel
import rs.edu.raf.rma.showtime.core.model.MovieSortOption
import rs.edu.raf.rma.showtime.core.model.activeFiltersCount

@Composable
fun MoviesListScreen(
    state: MoviesScreenState,
    onFilterClick: () -> Unit,
    onMovieClick: (MovieCardUiModel) -> Unit,
    onIntent: (MoviesIntent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MoviesTopBar(
                activeFiltersCount = state.appliedFilter.activeFiltersCount(),
                onFilterClick = onFilterClick,
            )
        },
    ) { paddingValues ->
        when (val listState = state.listState) {
            MoviesListContentState.Loading -> {
                LoadingStateContent(paddingValues = paddingValues)
            }

            MoviesListContentState.Empty -> {
                EmptyStateContent(
                    paddingValues = paddingValues,
                    selectedSort = state.selectedSort,
                    onIntent = onIntent,
                )
            }

            is MoviesListContentState.Error -> {
                ErrorStateContent(
                    paddingValues = paddingValues,
                    message = listState.message,
                    onRetryClick = { onIntent(MoviesIntent.RetryRequested) },
                )
            }

            is MoviesListContentState.Success -> {
                MoviesSuccessContent(
                    paddingValues = paddingValues,
                    selectedSort = state.selectedSort,
                    movies = listState.movies,
                    onMovieClick = onMovieClick,
                    onIntent = onIntent,
                )
            }
        }
    }
}

@Composable
private fun MoviesSuccessContent(
    paddingValues: PaddingValues,
    selectedSort: MovieSortOption,
    movies: List<MovieCardUiModel>,
    onMovieClick: (MovieCardUiModel) -> Unit,
    onIntent: (MoviesIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            MoviesToolbarRow(
                selectedSort = selectedSort,
                moviesCount = movies.size,
                onSortSelected = { sortOption ->
                    onIntent(MoviesIntent.SortSelected(sortOption))
                },
            )
        }

        items(items = movies, key = { movie -> movie.imdbId }) { movie ->
            MovieCard(
                movie = movie,
                onClick = { onMovieClick(movie) },
            )
        }
    }
}

@Composable
private fun LoadingStateContent(
    paddingValues: PaddingValues,
) {
    StateMessageContainer(paddingValues = paddingValues) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading movies...",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun EmptyStateContent(
    paddingValues: PaddingValues,
    selectedSort: MovieSortOption,
    onIntent: (MoviesIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            MoviesToolbarRow(
                selectedSort = selectedSort,
                moviesCount = 0,
                onSortSelected = { sortOption ->
                    onIntent(MoviesIntent.SortSelected(sortOption))
                },
            )
        }

        item {
            StateCard(
                title = "No movies found",
                message = "Try a different search, genre, year range, or rating filter.",
            )
        }
    }
}

@Composable
private fun ErrorStateContent(
    paddingValues: PaddingValues,
    message: String,
    onRetryClick: () -> Unit,
) {
    StateMessageContainer(paddingValues = paddingValues) {
        Text(
            text = "Something went wrong",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetryClick) {
            Text(text = "Retry")
        }
    }
}

@Composable
private fun StateMessageContainer(
    paddingValues: PaddingValues,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

@Composable
private fun StateCard(
    title: String,
    message: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = message,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MoviesTopBar(
    activeFiltersCount: Int,
    onFilterClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShowtimeLogo(size = 30.dp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Showtime",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.weight(1f))

        Box {
            Button(
                onClick = onFilterClick,
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(text = "Filter")
            }

            if (activeFiltersCount > 0) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd),
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = activeFiltersCount.toString(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.background,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun MoviesToolbarRow(
    selectedSort: MovieSortOption,
    moviesCount: Int,
    onSortSelected: (MovieSortOption) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(18.dp),
                onClick = { expanded = true },
            ) {
                Text(
                    text = "Sort: ${selectedSort.label} ↓",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                MovieSortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.label,
                                color = if (option == selectedSort) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                fontWeight = if (option == selectedSort) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                            )
                        },
                        onClick = {
                            expanded = false
                            onSortSelected(option)
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "$moviesCount movies",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun MovieCard(
    movie: MovieCardUiModel,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            MoviePoster(
                title = movie.title,
                imageUrl = movie.posterUrl,
                modifier = Modifier.size(width = 68.dp, height = 84.dp),
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = movie.title,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = buildString {
                        append(movie.year)
                        movie.runtimeMinutes?.let { runtime ->
                            append(" • ")
                            append(runtime)
                            append(" min")
                        }
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "★ ${movie.rating}",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = movie.votesLabel,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    movie.genres.forEach { genre ->
                        GenreChip(label = genre.name)
                    }
                }
            }
        }
    }
}

@Composable
private fun MoviePoster(
    title: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    val posterLabel = title.split(" ")
        .take(2)
        .joinToString(separator = "\n") { word -> word.take(1) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "$title poster",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = posterLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun GenreChip(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.tertiary,
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(
            modifier = Modifier
                .heightIn(min = 18.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                lineHeight = 10.sp,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun ShowtimeLogo(size: Dp) {
    Image(
        painter = painterResource(Res.drawable.showtime_logo),
        contentDescription = "Showtime logo",
        modifier = Modifier.height(size),
        contentScale = ContentScale.Fit,
    )
}
