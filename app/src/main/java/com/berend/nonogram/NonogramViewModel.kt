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
    // Size of the current random puzzle, null when playing a bundled one
    var randomSize: Int? by mutableStateOf(null)
        private set
    var solved: Boolean by mutableStateOf(false)
        private set
    var tool: Tool by mutableStateOf(Tool.Fill)
    val grid = mutableStateListOf<Cell>()
    private val undoStack = mutableStateListOf<Pair<Int, Cell>>()
    val canUndo: Boolean get() = undoStack.isNotEmpty()

    fun isSolved(puzzle: Puzzle) = prefs.getBoolean("solved_${puzzle.name}", false)

    fun inProgress(puzzle: Puzzle) =
        prefs.getString("grid_${puzzle.name}", null)?.any { it != '.' } == true

    fun randomSolved(size: Int) = prefs.getBoolean("solved_random_$size", false)

    fun randomInProgress(size: Int) =
        prefs.getString("grid_random_$size", null)?.any { it != '.' } == true

    fun open(puzzle: Puzzle) {
        val saved = prefs.getString("grid_${puzzle.name}", null)
        grid.clear()
        if (saved?.length == puzzle.rows * puzzle.cols) {
            grid.addAll(saved.map { c -> if (c == '#') Cell.Filled else if (c == 'x') Cell.Marked else Cell.Empty })
        } else {
            repeat(puzzle.rows * puzzle.cols) { grid.add(Cell.Empty) }
        }
        solved = isSolved(puzzle) && matchesSolution(puzzle)
        tool = Tool.Fill
        undoStack.clear()
        randomSize = null
        current = puzzle
    }

    fun openRandom(size: Int) {
        val art = prefs.getString("art_random_$size", null)?.split("\n")
        val puzzle = if (art?.size == size && art.all { it.length == size }) {
            Puzzle("random_$size", art)
        } else {
            newRandomPuzzle(size)
        }
        open(puzzle)
        randomSize = size
    }

    fun newRandom() {
        val size = randomSize ?: return
        open(newRandomPuzzle(size))
        randomSize = size
    }

    private fun newRandomPuzzle(size: Int): Puzzle {
        val puzzle = generatePuzzle(size)
        prefs.edit()
            .putString("art_random_$size", puzzle.art.joinToString("\n"))
            .remove("grid_${puzzle.name}")
            .putBoolean("solved_${puzzle.name}", false)
            .apply()
        return puzzle
    }

    fun close() {
        current = null
    }

    fun tap(index: Int) {
        val puzzle = current ?: return
        if (solved) return
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
            prefs.edit().putBoolean("solved_${puzzle.name}", true).apply()
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
        saveGrid(puzzle)
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
