
package com.example.soccerapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.soccerapp.data.model.Match
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import coil3.compose.AsyncImage


@Composable
fun MatchListScreen(
    matches: List<Match>,
    onMatchClick: (Int) -> Unit
) {

    val sortedMatches = matches.sortedBy { it.utcDate }

    LazyColumn(
        contentPadding = PaddingValues(
            vertical = 8.dp
        )
    ) {
        items(sortedMatches) { match ->
            Column {

                Text(
                    text = "${match.utcDate.substring(5, 7).toInt()}月${match.utcDate.substring(8, 10).toInt()}日 ${match.utcDate.substring(11, 16)}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onMatchClick(match.id)
                        }
                        .padding(
                            horizontal = 20.dp,
                            vertical = 16.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AsyncImage(
                        model = match.homeTeamCrest,
                        contentDescription = null,
                        modifier = Modifier.size(42.dp)
                    )




                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = match.homeTeam,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = " VS ",//weight(1f)が２つある→中央へ
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = match.awayTeam,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    AsyncImage(
                        model = match.awayTeamCrest,
                        contentDescription = null,
                        modifier = Modifier.size(42.dp)
                    )


                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    Text(
                        text = ">"
                    )
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}
//modifier=Composable関数自体への設定