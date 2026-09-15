package com.berend.nonogram

import kotlin.random.Random

// All ways to lay out the clue runs in a line of length n
fun placements(cl: List<Int>, n: Int): List<List<Boolean>> {
    if (cl == listOf(0)) return listOf(List(n) { false })
    val head = cl.first()
    val tail = cl.drop(1)
    val out = mutableListOf<List<Boolean>>()
    val maxStart = n - (cl.sum() + cl.size - 1)
    for (start in 0..maxStart) {
        val prefix = List(start) { false } + List(head) { true }
        if (tail.isEmpty()) {
            out.add(prefix + List(n - prefix.size) { false })
        } else {
            for (rest in placements(tail, n - prefix.size - 1)) {
                out.add(prefix + listOf(false) + rest)
            }
        }
    }
    return out
}

// True if the puzzle can be completed with line logic alone (no guessing),
// which also guarantees the solution is unique.
fun lineSolvable(p: Puzzle): Boolean {
    val grid = Array(p.rows) { arrayOfNulls<Boolean>(p.cols) }
    var rowCands = p.rowClues.map { placements(it, p.cols) }
    var colCands = p.colClues.map { placements(it, p.rows) }

    var changed = true
    while (changed) {
        changed = false
        rowCands = rowCands.mapIndexed { r, cands ->
            val kept = cands.filter { pl -> pl.indices.all { i -> grid[r][i] == null || grid[r][i] == pl[i] } }
            if (kept.isEmpty()) return false
            for (i in 0 until p.cols) {
                if (grid[r][i] == null && kept.all { it[i] == kept[0][i] }) {
                    grid[r][i] = kept[0][i]
                    changed = true
                }
            }
            kept
        }
        colCands = colCands.mapIndexed { c, cands ->
            val kept = cands.filter { pl -> pl.indices.all { i -> grid[i][c] == null || grid[i][c] == pl[i] } }
            if (kept.isEmpty()) return false
            for (i in 0 until p.rows) {
                if (grid[i][c] == null && kept.all { it[i] == kept[0][i] }) {
                    grid[i][c] = kept[0][i]
                    changed = true
                }
            }
            kept
        }
    }
    return (0 until p.rows).all { r -> (0 until p.cols).all { c -> grid[r][c] == p.solid(r, c) } }
}

// Rejection sampling: random grids at 0.5-0.65 fill are accepted well over half
// the time at these sizes, so this returns after a couple of ~1 ms attempts.
fun generatePuzzle(size: Int, random: Random = Random.Default): Puzzle {
    while (true) {
        val density = 0.5 + random.nextDouble() * 0.15
        val art = List(size) { row ->
            buildString { repeat(size) { append(if (random.nextDouble() < density) '#' else '.') } }
        }
        val puzzle = Puzzle("random_$size", art)
        if (lineSolvable(puzzle)) return puzzle
    }
}
