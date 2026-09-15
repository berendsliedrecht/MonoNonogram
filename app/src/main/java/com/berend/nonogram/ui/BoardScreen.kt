package com.berend.nonogram.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berend.nonogram.Cell
import com.berend.nonogram.NonogramViewModel
import com.berend.nonogram.Puzzle
import com.berend.nonogram.Puzzles
import com.berend.nonogram.Tool
import com.mudita.mmd.components.buttons.ButtonMMD
import com.mudita.mmd.components.buttons.OutlinedButtonMMD
import com.mudita.mmd.components.text.TextMMD
import com.mudita.mmd.components.top_app_bar.TopAppBarMMD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(viewModel: NonogramViewModel) {
    val puzzle = viewModel.current ?: return
    BackHandler(onBack = viewModel::close)

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBarMMD(
            navigationIcon = {
                IconButton(onClick = viewModel::close) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            },
            title = {
                val size = viewModel.randomSize
                TextMMD(
                    text = when {
                        size != null -> "Random $size x $size"
                        viewModel.solved -> puzzle.name
                        else -> "Puzzle ${Puzzles.indexOf(puzzle) + 1}"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Board(
                puzzle = puzzle,
                grid = viewModel.grid,
                enabled = !viewModel.solved,
                onTap = viewModel::tap,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.solved) {
                TextMMD(
                    text = if (viewModel.randomSize != null) "Solved!" else "Solved: ${puzzle.name}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    ToolButton("Fill", active = viewModel.tool == Tool.Fill, modifier = Modifier.weight(1f)) {
                        viewModel.tool = Tool.Fill
                    }
                    ToolButton("Mark", active = viewModel.tool == Tool.Mark, modifier = Modifier.weight(1f)) {
                        viewModel.tool = Tool.Mark
                    }
                    OutlinedButtonMMD(onClick = viewModel::undo, enabled = viewModel.canUndo) {
                        TextMMD("Undo", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButtonMMD(onClick = viewModel::clear, modifier = Modifier.weight(1f)) {
                    TextMMD(if (viewModel.solved) "Play again" else "Clear", fontSize = 16.sp)
                }
                if (viewModel.randomSize != null) {
                    OutlinedButtonMMD(onClick = viewModel::newRandom, modifier = Modifier.weight(1f)) {
                        TextMMD("New puzzle", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolButton(label: String, active: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    if (active) {
        ButtonMMD(
            onClick = onClick,
            // Pure black/white: anything in between dithers on e-ink
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
            border = BorderStroke(2.dp, Color.Black),
            modifier = modifier,
        ) {
            TextMMD(label, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    } else {
        OutlinedButtonMMD(onClick = onClick, modifier = modifier) {
            TextMMD(label, fontSize = 16.sp)
        }
    }
}

@Composable
private fun Board(
    puzzle: Puzzle,
    grid: List<Cell>,
    enabled: Boolean,
    onTap: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val measurer = rememberTextMeasurer()
    val clueCols = puzzle.rowClues.maxOf { it.size }
    val clueRows = puzzle.colClues.maxOf { it.size }
    val totalCols = clueCols + puzzle.cols
    val totalRows = clueRows + puzzle.rows

    Canvas(
        modifier = modifier
            .aspectRatio(totalCols.toFloat() / totalRows)
            .pointerInput(puzzle, enabled) {
                detectTapGestures { offset ->
                    if (!enabled) return@detectTapGestures
                    val cell = size.width.toFloat() / totalCols
                    val col = (offset.x / cell).toInt() - clueCols
                    val row = (offset.y / cell).toInt() - clueRows
                    if (row in 0 until puzzle.rows && col in 0 until puzzle.cols) {
                        onTap(row * puzzle.cols + col)
                    }
                }
            },
    ) {
        val cell = size.width / totalCols
        val originX = clueCols * cell
        val originY = clueRows * cell
        val thin = 1.dp.toPx()
        val bold = 2.5f.dp.toPx()
        val clueStyle = TextStyle(
            color = Color.Black,
            fontSize = (cell * 0.45f).toSp(),
            fontWeight = FontWeight.Bold,
        )

        // Cell contents
        for (row in 0 until puzzle.rows) {
            for (col in 0 until puzzle.cols) {
                val x = originX + col * cell
                val y = originY + row * cell
                when (grid[row * puzzle.cols + col]) {
                    Cell.Filled -> drawRect(Color.Black, topLeft = Offset(x, y), size = Size(cell, cell))
                    Cell.Marked -> {
                        val inset = cell * 0.3f
                        drawLine(Color.Black, Offset(x + inset, y + inset), Offset(x + cell - inset, y + cell - inset), thin * 1.5f)
                        drawLine(Color.Black, Offset(x + inset, y + cell - inset), Offset(x + cell - inset, y + inset), thin * 1.5f)
                    }
                    Cell.Empty -> {}
                }
            }
        }

        // Thin grid lines span the clue bands too, so clues visibly align with their line
        for (col in 0..puzzle.cols) {
            val x = originX + col * cell
            drawLine(Color.Black, Offset(x, 0f), Offset(x, size.height), thin)
        }
        for (row in 0..puzzle.rows) {
            val y = originY + row * cell
            drawLine(Color.Black, Offset(0f, y), Offset(size.width, y), thin)
        }

        // Bold frame and every-5 separators over the grid area only
        for (col in 0..puzzle.cols step 5) {
            val x = originX + col * cell
            drawLine(Color.Black, Offset(x, originY), Offset(x, size.height), bold)
        }
        for (row in 0..puzzle.rows step 5) {
            val y = originY + row * cell
            drawLine(Color.Black, Offset(0f, y), Offset(size.width, y), bold)
        }

        fun drawClue(value: Int, cellX: Float, cellY: Float) {
            val layout = measurer.measure(AnnotatedString(value.toString()), clueStyle)
            drawText(
                layout,
                topLeft = Offset(
                    cellX + (cell - layout.size.width) / 2f,
                    cellY + (cell - layout.size.height) / 2f,
                ),
            )
        }

        // Row clues, right-aligned against the grid
        puzzle.rowClues.forEachIndexed { row, clues ->
            clues.forEachIndexed { i, value ->
                drawClue(value, (clueCols - clues.size + i) * cell, originY + row * cell)
            }
        }
        // Column clues, bottom-aligned against the grid
        puzzle.colClues.forEachIndexed { col, clues ->
            clues.forEachIndexed { i, value ->
                drawClue(value, originX + col * cell, (clueRows - clues.size + i) * cell)
            }
        }
    }
}
