# Nonogram

Nonogram (picture logic) puzzles for the Mudita Kompakt (e-ink), built with the Mudita Mindful Design (MMD) framework. Tap to fill or pencil-mark cells, no timer, no score, no guessing: every puzzle is solvable with line logic alone. Solving a bundled puzzle reveals its picture and name, and endless random puzzles can be generated at 5x5, 8x8, 10x10 or 12x12 (rejection sampling: random grids are kept only if the line-logic solver completes them, which also makes the solution unique). Progress is saved per puzzle. Fully offline.

## Install

```
./gradlew installDebug
```

Or sideload the release APK from `app/build/outputs/apk/release/` (debug-signed on purpose so it installs directly).

## Structure

- `Puzzles.kt`: puzzle model, clue derivation and the bundled puzzle art
- `Solver.kt`: line-logic solver and the random puzzle generator built on it
- `NonogramViewModel.kt`: board state, undo, win detection and persisted progress
- `ui/PuzzleListScreen.kt`: puzzle list with solved/in-progress status (names stay hidden until solved)
- `ui/BoardScreen.kt`: clue and grid canvas, fill/mark tools, undo and clear

Every puzzle is verified by `PuzzleSolvabilityTest` to be uniquely solvable without guessing; add new art to `Puzzles.kt` and the test will hold it to the same bar.

## License

[MIT](LICENSE)
