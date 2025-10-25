# Tic Tac Toe
I created a game of Tic Tac Toe in Java using the awt and swing graphics library.

```
src/
└── com/games/TicTacToe.java
resources/
└── sounds/
    ├── click.wav
    ├── win.wav
    └── tie.wav
```

Here’s a comprehensive summary of all the enhancements I’ve made to the original Tic-Tac-Toe game tutorial

## 🎮 Core Game Features Added
1. Score Tracking & Best-of-5 Match System
   * ✅ Round wins tracked for Player X and O.
   * ✅ Match wins tracked separately (first to 3 wins = match champion).
   * ✅ Score label: "Player X: 2 Player O: 1".
   * ✅ After match ends:
     * Declares overall winner ("Human wins the match!" or "Computer wins the match!").
     * Disables board.
     * Hides "Reset Round" button.
2. Game Flow Controls
   * ✅ "Reset Round": Clears board, keeps scores (only visible during active match).
   * ✅ "New Match": Resets all scores and starts fresh best-of-5.
   * ✅ "Back to Home": Closes current game and returns to mode selection.

## 🤖 AI Opponent (Computer Mode)
   * ✅ Mode selection dialog at startup:
     * "Play against a Human or the Computer?"
   * ✅ Human always plays X, Computer plays O.
   * ✅ Smart AI logic:
     * Tries to win if possible.
     * Blocks human win.
     * Prefers center, then corners.
     * Falls back to random move.
   * ✅ Small delay (600ms) before AI move for realism.
   * ✅ Human cannot click during AI’s turn.

## 🎨 Beautiful Modern UI
* ✅ Dark theme with elegant color palette:
  * Background: #1e1e2e (deep navy)
  * Tiles: #3a3d5a (muted purple-gray)
  * X: Coral red (#ff6b6b)
  * O: Mint teal (#4dccbd)
* ✅ Visible grid lines between tiles using asymmetric MatteBorder.
* ✅ Hover effects on tiles (lighter background + brighter border).
* ✅ Winning tiles highlighted with:
  * Green text if X (Human) wins → #4ade80
  * Red text if O (Computer) wins → #f87171
* ✅ Tie tiles: Warm amber (#f5a97f)
* ✅ Polished buttons with padding, custom fonts (Segoe UI), and consistent styling.

## 🔊 Sound Effects (Optional but Immersive)
✅ Plays .wav sounds from /sounds/ resource folder:
* click.wav → on tile placement
  * in.wav → on round win
  * tie.wav → on tie
*✅ Graceful fallback if sound files missing (no crash).

## 🏠 Navigation & Session Management
* ✅ Mode selector shown before game loads (via JOptionPane).
* ✅ "Back to Home" button lets user switch between Human/AI anytime.
* ✅ Each game instance is clean and independent (no leftover state).

# 🚀 What’s Next? (Ideas)
* Add difficulty levels (Easy/Normal/Hard AI)
* Save match history or high scores
* Add player name input (e.g., "Alex vs Computer")
* Implement animated win sequence (flashing, particle effects)
* Create a light mode toggle