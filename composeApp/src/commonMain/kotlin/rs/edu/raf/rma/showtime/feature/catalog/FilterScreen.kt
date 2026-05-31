package rs.edu.raf.rma.showtime.feature.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import rs.edu.raf.rma.showtime.core.model.GenreUiModel
import rs.edu.raf.rma.showtime.core.model.MoviesFilterState
import rs.edu.raf.rma.showtime.feature.catalog.MoviesIntent

@Composable
fun FilterScreen(
    state: MoviesFilterState,
    availableGenres: List<GenreUiModel>,
    onIntent: (MoviesIntent) -> Unit,
    onBackClick: () -> Unit,
    onApplyClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Text(text = "Back")
            }

            Text(
                text = "Filter Movies",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )

            TextButton(
                onClick = { onIntent(MoviesIntent.ClearDraftFilters) },
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Text(text = "Clear All")
            }
        }

        FilterSection(title = "Search") {
            OutlinedTextField(
                value = state.query,
                onValueChange = { value ->
                    onIntent(MoviesIntent.QueryChanged(value))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Movie title") },
            )
        }

        FilterSection(title = "Genre") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                availableGenres.forEach { genre ->
                    FilterChip(
                        selected = state.selectedGenre?.id == genre.id,
                        onClick = {
                            onIntent(MoviesIntent.GenreToggled(genre))
                        },
                        label = {
                            Text(
                                text = genre.name,
                                fontSize = 13.sp,
                            )
                        },
                    )
                }
            }
        }

        FilterSection(title = "Year Range") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = state.minYearInput,
                    onValueChange = { value ->
                        onIntent(MoviesIntent.MinYearChanged(value))
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("From") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedTextField(
                    value = state.maxYearInput,
                    onValueChange = { value ->
                        onIntent(MoviesIntent.MaxYearChanged(value))
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("To") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        }

        FilterSection(title = "Minimum Rating") {
            Text(
                text = "IMDb ${state.minRating.asRatingLabel()}",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Slider(
                value = state.minRating,
                onValueChange = { value ->
                    onIntent(MoviesIntent.MinRatingChanged(value))
                },
                valueRange = 0f..10f,
            )
        }

        Button(
            onClick = onApplyClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Apply Filters")
        }
    }
}

private fun Float.asRatingLabel(): String {
    val scaled = (this * 10).roundToInt()
    val whole = scaled / 10
    val decimal = scaled % 10
    return "$whole.$decimal"
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        content()
    }
}
