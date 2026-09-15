package com.berend.nonogram

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

enum class Cell { Empty, Filled, Marked }
enum class Tool { Fill, Mark }

class NonogramViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("puzzles", Context.MODE_PRIVATE)

    var current: Puzzle? by mutableStateOf(null)
        private set
    var solved: Boolean by mutableStateOf(false)
        private set
    // Duration of the solve that just happened, null when reopening an old solved board
    var lastSolveMillis: Long? by mutableStateOf(null)
        private set
    var tool: Tool by mutableStateOf(Tool.Fill)
    val grid = mutableStateListOf<Cell>()
    private val undoStack = mutableStateListOf<Pair<Int, Cell>>()
    val canUndo: Boolean get() = undoStack.isNotEmpty()

    fun gamesPlayed(size: Int) = prefs.getInt("stat_played_$size", 0)

    fun bestMillis(size: Int): Long? = prefs.getLong("stat_best_$size", 0).takeIf { it > 0 }

    fun open(size: Int) {
        val art = prefs.getString("art_random_$size", null)?.split("\n")
        val puzzle = if (art?.size == size && art.all { it.length == size }) {
            Puzzle("random_$size", art)
        } else {
            generateAndSave(size)
        }
        val saved = prefs.getString("grid_${puzzle.name}", null)
        grid.clear()
        if (saved?.length == puzzle.rows * puzzle.cols) {
            grid.addAll(saved.map { c -> if (c == '#') Cell.Filled else if (c == 'x') Cell.Marked else Cell.Empty })
        } else {
            repeat(puzzle.rows * puzzle.cols) { grid.add(Cell.Empty) }
        }
        solved = prefs.getBoolean("solved_${puzzle.name}", false) && matchesSolution(puzzle)
        lastSolveMillis = null
        tool = Tool.Fill
        undoStack.clear()
        current = puzzle
    }

    fun newPuzzle() {
        val size = current?.rows ?: return
        generateAndSave(size)
        open(size)
    }

    fun close() {
        current = null
    }

    fun tap(index: Int) {
        val puzzle = current ?: return
        if (solved) return
        val size = puzzle.rows
        if (!prefs.contains("started_$size")) {
            prefs.edit().putLong("started_$size", System.currentTimeMillis()).apply()
        }
        val prev = grid[index]
        grid[index] = when (tool) {
            Tool.Fill -> if (prev == Cell.Filled) Cell.Empty else Cell.Filled
            Tool.Mark -> if (prev == Cell.Marked) Cell.Empty else Cell.Marked
        }
        undoStack.add(index to prev)
        if (matchesSolution(puzzle)) {
            // Reveal a clean picture: drop the pencil marks
            for (i in grid.indices) if (grid[i] == Cell.Marked) grid[i] = Cell.Empty
            solved = true
            undoStack.clear()
            recordSolve(size)
        }
        saveGrid(puzzle)
    }

    fun undo() {
        val (index, prev) = undoStack.removeLastOrNull() ?: return
        grid[index] = prev
        current?.let(::saveGrid)
    }

    fun clear() {
        val puzzle = current ?: return
        for (i in grid.indices) grid[i] = Cell.Empty
        undoStack.clear()
        solved = false
        lastSolveMillis = null
        prefs.edit().remove("started_${puzzle.rows}").apply()
        saveGrid(puzzle)
    }

    private fun generateAndSave(size: Int): Puzzle {
        val puzzle = generatePuzzle(size)
        prefs.edit()
            .putString("art_random_$size", puzzle.art.joinToString("\n"))
            .remove("grid_${puzzle.name}")
            .remove("started_$size")
            .putBoolean("solved_${puzzle.name}", false)
            .apply()
        return puzzle
    }

    private fun recordSolve(size: Int) {
        val editor = prefs.edit()
            .putBoolean("solved_random_$size", true)
            .putInt("stat_played_$size", gamesPlayed(size) + 1)
        val started = prefs.getLong("started_$size", 0)
        if (started > 0) {
            val elapsed = System.currentTimeMillis() - started
            lastSolveMillis = elapsed
            val best = bestMillis(size)
            if (best == null || elapsed < best) editor.putLong("stat_best_$size", elapsed)
        }
        editor.remove("started_$size").apply()
    }

    private fun matchesSolution(puzzle: Puzzle) = grid.indices.all { i ->
        (grid[i] == Cell.Filled) == puzzle.solid(i / puzzle.cols, i % puzzle.cols)
    }

    private fun saveGrid(puzzle: Puzzle) {
        val encoded = grid.joinToString("") { c ->
            if (c == Cell.Filled) "#" else if (c == Cell.Marked) "x" else "."
        }
        prefs.edit().putString("grid_${puzzle.name}", encoded).apply()
    }
}

fun formatDuration(millis: Long): String {
    val s = millis / 1000
    return if (s >= 3600) "%d:%02d:%02d".format(s / 3600, (s % 3600) / 60, s % 60)
    else "%d:%02d".format(s / 60, s % 60)
}
