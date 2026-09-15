# Nonogram

Nonogram (picture logic) puzzles for the Mudita Kompakt (e-ink), built with the Mudita Mindful Design (MMD) framework. Tap to fill or pencil-mark cells, no timer, no score, no guessing: every bundled puzzle is solvable with line logic alone. Solving a puzzle reveals its picture and name. Progress is saved per puzzle. Fully offline.

## Install

```
./gradlew installDebug
```

Or sideload the release APK from `app/build/outputs/apk/release/` (debug-signed on purpose so it installs directly).

## Structure

- `Puzzles.kt`: puzzle model, clue derivation and the bundled puzzle art
- `NonogramViewModel.kt`: board state, undo, win detection and persisted progress
- `ui/PuzzleListScreen.kt`: puzzle list with solved/in-progress status (names stay hidden until solved)
- `ui/BoardScreen.kt`: clue and grid canvas, fill/mark tools, undo and clear

Every puzzle is verified by `PuzzleSolvabilityTest` to be uniquely solvable without guessing; add new art to `Puzzles.kt` and the test will hold it to the same bar.

## License

[MIT](LICENSE)
