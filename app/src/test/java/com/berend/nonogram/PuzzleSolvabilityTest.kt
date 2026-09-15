package com.berend.nonogram

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleSolvabilityTest {

    @Test
    fun cluesAreDerivedFromArt() {
        assertEquals(listOf(0), clues(listOf(false, false)))
        assertEquals(listOf(2, 1), clues(listOf(true, true, false, true)))
        val heart = Puzzle("heart", listOf(
            ".#.#.",
            "#####",
            "#####",
            ".###.",
            "..#.."))
        assertEquals(listOf(1, 1), heart.rowClues[0])
        assertEquals(listOf(5), heart.rowClues[1])
        assertEquals(listOf(2), heart.colClues[0])
    }

    @Test
    fun ambiguousPuzzleIsRejected() {
        // Two mirrored solutions satisfy these clues, so line logic cannot finish
        val checkerboard = Puzzle("ambiguous", listOf(
            "#.",
            ".#"))
        assertFalse(lineSolvable(checkerboard))
    }

    @Test
    fun generatedPuzzlesAreLineSolvableAtEverySize() {
        val random = Random(1)
        for (size in BoardSizes) {
            repeat(5) {
                val p = generatePuzzle(size, random)
                assertEquals(size, p.rows)
                assertEquals(size, p.cols)
                assertTrue("generated $size x $size is not line-solvable", lineSolvable(p))
            }
        }
    }

    @Test
    fun durationsFormatAsClockTimes() {
        assertEquals("0:07", formatDuration(7_000))
        assertEquals("12:05", formatDuration(725_000))
        assertEquals("1:00:01", formatDuration(3_601_000))
    }
}
