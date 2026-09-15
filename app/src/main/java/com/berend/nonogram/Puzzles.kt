package com.berend.nonogram

val BoardSizes = listOf(5, 8, 10)

data class Puzzle(val name: String, val art: List<String>) {
    val rows: Int get() = art.size
    val cols: Int get() = art.first().length
    fun solid(row: Int, col: Int): Boolean = art[row][col] == '#'
    val rowClues: List<List<Int>> = art.map { line -> clues(line.map { it == '#' }) }
    val colClues: List<List<Int>> = (0 until cols).map { c -> clues(art.map { it[c] == '#' }) }
}

fun clues(line: List<Boolean>): List<Int> {
    val out = mutableListOf<Int>()
    var run = 0
    for (solid in line) {
        if (solid) run++ else if (run > 0) { out.add(run); run = 0 }
    }
    if (run > 0) out.add(run)
    return if (out.isEmpty()) listOf(0) else out
}
