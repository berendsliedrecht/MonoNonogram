package com.berend.nonogram.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berend.nonogram.NonogramViewModel
import com.berend.nonogram.Puzzles
import com.mudita.mmd.components.divider.HorizontalDividerMMD
import com.mudita.mmd.components.lazy.LazyColumnMMD
import com.mudita.mmd.components.text.TextMMD

private val RandomSizes = listOf(5, 8, 10, 12)

@Composable
fun PuzzleListScreen(viewModel: NonogramViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        TextMMD("Nonogram", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))

        LazyColumnMMD(contentPadding = PaddingValues(vertical = 8.dp)) {
            items(RandomSizes.size) { index ->
                val size = RandomSizes[index]
                val status = when {
                    viewModel.randomSolved(size) -> "Solved"
                    viewModel.randomInProgress(size) -> "In progress"
                    else -> null
                }
                PuzzleRow(
                    title = "Random",
                    status = status,
                    sizeLabel = "$size x $size",
                    onClick = { viewModel.openRandom(size) },
                )
                HorizontalDividerMMD()
            }
            items(Puzzles.size) { index ->
                val puzzle = Puzzles[index]
                val solved = viewModel.isSolved(puzzle)
                PuzzleRow(
                    // Names double as solution spoilers, so hide them until solved
                    title = if (solved) puzzle.name else "Puzzle ${index + 1}",
                    status = when {
                        solved -> "Solved"
                        viewModel.inProgress(puzzle) -> "In progress"
                        else -> null
                    },
                    sizeLabel = "${puzzle.rows} x ${puzzle.cols}",
                    onClick = { viewModel.open(puzzle) },
                )
                if (index < Puzzles.lastIndex) HorizontalDividerMMD()
            }
        }
    }
}

@Composable
private fun PuzzleRow(title: String, status: String?, sizeLabel: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            TextMMD(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            status?.let { TextMMD(it, fontSize = 14.sp) }
        }
        TextMMD(sizeLabel, fontSize = 16.sp)
    }
}
