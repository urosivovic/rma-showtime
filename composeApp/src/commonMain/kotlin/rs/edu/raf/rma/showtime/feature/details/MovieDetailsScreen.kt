package rs.edu.raf.rma.showtime.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import rs.edu.raf.rma.showtime.core.model.MovieDetailsUiModel

@Composable
fun MovieDetailsScreen(
    state: MovieDetailsScreenState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
) {
    when (val contentState = state.contentState) {
        MovieDetailsContentState.Loading -> {
            MovieDetailsLoading(onBackClick = onBackClick)
        }

        is MovieDetailsContentState.Error -> {
            MovieDetailsError(
                message = contentState.message,
                onBackClick = onBackClick,
                onRetryClick = onRetryClick,
            )
        }

        is MovieDetailsContentState.Success -> {
            MovieDetailsContent(
                movie = contentState.movie,
                onBackClick = onBackClick,
            )
        }
    }
}

@Composable
private fun MovieDetailsLoading(
    onBackClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(20.dp),
    ) {
        BackOverlayButton(
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopStart),
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading movie details...",
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun MovieDetailsError(
    message: String,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(20.dp),
    ) {
        BackOverlayButton(
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopStart),
        )

        Card(
            modifier = Modifier.align(Alignment.Center),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Could not load this movie",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = message,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(onClick = onRetryClick) {
                    Text(text = "Retry")
                }
            }
        }
    }
}

@Composable
private fun MovieDetailsContent(
    movie: MovieDetailsUiModel,
    onBackClick: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
        ) {
            if (movie.backdropUrl != null) {
                AsyncImage(
                    model = movie.backdropUrl,
                    contentDescription = "${movie.title} backdrop",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Transparent,
                                MaterialTheme.colorScheme.background,
                            ),
                        ),
                    ),
            )

            BackOverlayButton(
                onBackClick = onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(14.dp),
            )

            if (movie.trailerUrl != null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(86.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    onClick = {
                        uriHandler.openUri(movie.trailerUrl)
                    },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "▶",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-48).dp)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                DetailsPoster(
                    title = movie.title,
                    imageUrl = movie.posterUrl,
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = movie.title,
                        fontSize = 24.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Bold,
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp,
                    )
                }
            }

            RatingsRow(movie = movie)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                movie.genres.forEach { genre ->
                    DetailsGenreChip(label = genre.name)
                }
            }

            DetailsSection(title = "Overview") {
                Text(
                    text = movie.overview,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                )
            }

            DetailsSection(title = "Info") {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    InfoBadge(label = "Budget", value = movie.budgetLabel)
                    InfoBadge(label = "Revenue", value = movie.revenueLabel)
                    InfoBadge(label = "Language", value = movie.languageLabel)
                    InfoBadge(label = "Popularity", value = movie.popularityLabel)
                }
            }

            if (movie.imageUrls.isNotEmpty()) {
                DetailsSection(title = "Images") {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        movie.imageUrls.forEach { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "${movie.title} image",
                                modifier = Modifier
                                    .width(210.dp)
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
            }

            if (movie.cast.isNotEmpty()) {
                DetailsSection(title = "Actors") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        movie.cast.forEach { person ->
                            CastRow(person = person)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RatingsRow(
    movie: MovieDetailsUiModel,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "★ ${movie.imdbRating}/10",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = movie.imdbVotesLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "TMDB ${movie.tmdbRating}",
            color = Color(0xFF5DA8FF),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun DetailsPoster(
    title: String,
    imageUrl: String?,
) {
    Box(
        modifier = Modifier
            .width(108.dp)
            .height(162.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "$title poster",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = title.take(1),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun DetailsGenreChip(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun DetailsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        content()
    }
}

@Composable
private fun InfoBadge(
    label: String,
    value: String,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(
            modifier = Modifier
                .width(104.dp)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun CastRow(
    person: rs.edu.raf.rma.showtime.core.model.PersonUiModel,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            if (person.profileUrl != null) {
                AsyncImage(
                    model = person.profileUrl,
                    contentDescription = person.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Text(
                    text = person.name.take(1),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = person.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun BackOverlayButton(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.42f),
        shape = RoundedCornerShape(16.dp),
        onClick = onBackClick,
    ) {
        Text(
            text = "Back",
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
