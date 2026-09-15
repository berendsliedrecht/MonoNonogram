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

@Composable
fun PuzzleListScreen(viewModel: NonogramViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        TextMMD("Nonogram", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))

        LazyColumnMMD(contentPadding = PaddingValues(vertical = 8.dp)) {
            items(Puzzles.size) { index ->
                val puzzle = Puzzles[index]
                val solved = viewModel.isSolved(puzzle)
                val status = when {
                    solved -> "Solved"
                    viewModel.inProgress(puzzle) -> "In progress"
                    else -> null
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.open(puzzle) }
                        .padding(vertical = 14.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Names double as solution spoilers, so hide them until solved
                        TextMMD(
                            text = if (solved) puzzle.name else "Puzzle ${index + 1}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        status?.let { TextMMD(it, fontSize = 14.sp) }
                    }
                    TextMMD("${puzzle.rows} x ${puzzle.cols}", fontSize = 16.sp)
                }
                if (index < Puzzles.lastIndex) HorizontalDividerMMD()
            }
        }
    }
}
