package com.berend.nonogram

import kotlin.random.Random
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
        for (p in Puzzles) assertTrue("${p.name} is not line-solvable", lineSolvable(p))
    }

    @Test
    fun generatedPuzzlesAreLineSolvableAtEverySize() {
        val random = Random(1)
        for (size in listOf(5, 8, 10, 12)) {
            repeat(5) {
                val p = generatePuzzle(size, random)
                assertEquals(size, p.rows)
                assertEquals(size, p.cols)
                assertTrue("generated $size x $size is not line-solvable", lineSolvable(p))
            }
        }
    }
}
