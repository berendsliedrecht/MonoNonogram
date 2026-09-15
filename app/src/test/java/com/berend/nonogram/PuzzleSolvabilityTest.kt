package com.berend.nonogram

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleSolvabilityTest {

    @Test
    fun cluesAreDerivedFromArt() {
        assertEquals(listOf(0), clues(listOf(false, false)))
        assertEquals(listOf(2, 1), clues(listOf(true, true, false, true)))
        val heart = Puzzles.first { it.name == "Heart" }
        assertEquals(listOf(1, 1), heart.rowClues[0])
        assertEquals(listOf(5), heart.rowClues[1])
        assertEquals(listOf(2), heart.colClues[0])
    }

    @Test
    fun namesAreUnique() {
        assertEquals(Puzzles.size, Puzzles.map { it.name }.toSet().size)
    }

    @Test
    fun artIsRectangular() {
        for (p in Puzzles) assertTrue(p.name, p.art.all { it.length == p.cols })
    }

    // Every puzzle must be solvable with line logic alone (no guessing), which also
    // guarantees the solution is unique.
    @Test
    fun allPuzzlesAreLineSolvable() {
        for (p in Puzzles) assertTrue("${p.name} is not line-solvable", lineSolves(p))
    }

    private fun lineSolves(p: Puzzle): Boolean {
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

    // All ways to lay out the clue runs in a line of length n
    private fun placements(cl: List<Int>, n: Int): List<List<Boolean>> {
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
}
