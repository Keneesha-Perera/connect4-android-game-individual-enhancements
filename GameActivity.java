package com.example.connect_four;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import android.content.SharedPreferences;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;


import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private GridLayout gameBoard;
    private TextView playerTurnTextView;
    private Button resetButton,undoButton,pauseExitButton; // Reset button
    private int player1Color, player2Color;
    private String player1Name;
    private String player2Name;

    private boolean isPlayer1Turn = true; // Track whose turn it is
    private int rows = 6, columns = 7; // Default grid size
    private Button[][] gridButtons; // 2D array to hold button references
    private int[][] gridState; // 2D array to track disc positions (0 = empty, 1 = player 1, 2 = player 2)

    // Separate stacks to store last moves for each player
    private Stack<int[]> player1Moves = new Stack<>();
    private Stack<int[]> player2Moves = new Stack<>();
    // New variables for AI mode
    private boolean isAiMode = false; // Track if AI mode is enabled
    private Random random; // Random number generator for AI moves
    private int totalGamesPlayed = 0;
    private int player1Wins = 0;
    private int player2Wins = 0;
    private int draws = 0;
    private boolean player1LastUndo = false; // Tracks if player 1 just undid a move
    private boolean player2LastUndo = false; // Tracks if player 2 just undid a move
    private TextView moveCountTextView; // Reference to the move count TextView
    private int movesMade = 0; // Tracks the number of moves made
    private int totalMoves = 0; // Tracks the total possible moves

    private Switch themeSwitch;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "GamePrefs";
    private static final String DARK_MODE_KEY = "dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the saved theme preference
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        setAppTheme(isDarkMode);

        setContentView(R.layout.activity_game);

        themeSwitch = findViewById(R.id.theme_switch);
        themeSwitch.setChecked(isDarkMode); // Set switch to current theme


        gameBoard = findViewById(R.id.game_board);
        playerTurnTextView = findViewById(R.id.player_turn_textview);
        resetButton = findViewById(R.id.reset_button); // Initialize reset button
        undoButton = findViewById(R.id.undo_button);
        pauseExitButton = findViewById(R.id.pause_exit_button);

        // Retrieve the settings passed from MainActivity
        String gridSize = getIntent().getStringExtra("GRID_SIZE");
        player1Name = getIntent().getStringExtra("PLAYER_1_NAME");
        player2Name = getIntent().getStringExtra("PLAYER_2_NAME");
        player1Color = getIntent().getIntExtra("PLAYER_1_COLOR", Color.RED);
        player2Color = getIntent().getIntExtra("PLAYER_2_COLOR", Color.YELLOW);
        isAiMode = getIntent().getBooleanExtra("AI_MODE", false); // Check if AI mode is selected

        moveCountTextView = findViewById(R.id.move_count_textview); // Initialize the move count TextView

        Button viewStatsButton = findViewById(R.id.stats_button);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the theme preference
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(DARK_MODE_KEY, isChecked);
            editor.apply();

            // Update the theme
            setAppTheme(isChecked);
        });
        viewStatsButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, StatisticsActivity.class);
            intent.putExtra("PLAYER_1_NAME", player1Name);
            intent.putExtra("PLAYER_2_NAME", player2Name);
            intent.putExtra("TOTAL_GAMES_PLAYED", totalGamesPlayed);
            intent.putExtra("PLAYER_1_WINS", player1Wins);
            intent.putExtra("PLAYER_2_WINS", player2Wins);
            intent.putExtra("DRAWS", draws);
            startActivity(intent);
        });

        // Initialize random number generator
        random = new Random();

        // Set up the game board based on the selected grid size
        setUpGameBoard(gridSize);

        // Set player names and turn text
        playerTurnTextView.setText(player1Name + "'s turn");

        resetButton.setOnClickListener(v -> {
            // Animate the reset button
            animateButtonScale(resetButton);

            // Reset the game after the animation ends
            resetButton.postDelayed(this::resetGame, 200); // Delay reset to match animation duration
        });



        // Set undo button onClickListener
        undoButton.setOnClickListener(v -> undoLastMove());


        pauseExitButton.setOnClickListener(v -> {
            // Animate the pause exit button
            animateButtonScale(pauseExitButton);

            // Delay the exit game logic to match the animation duration
            pauseExitButton.postDelayed(this::exitGame, 200); // Call exitGame after the animation
        });


        // If the game is restored, we should update the UI accordingly
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
    }

    private void setAppTheme(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    private void animateButtonScale(final Button button) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1.0f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1.0f, 1.2f, 1.0f);
        scaleX.setDuration(300); // Duration of the scale animation
        scaleY.setDuration(300); // Duration of the scale animation

        scaleX.start();
        scaleY.start();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save grid state
        outState.putSerializable("GRID_STATE", gridState);
        // Save current player's turn
        outState.putBoolean("IS_PLAYER_1_TURN", isPlayer1Turn);
        // Save grid size
        outState.putInt("ROWS", rows);
        outState.putInt("COLUMNS", columns);
        outState.putSerializable("PLAYER_1_MOVES", player1Moves);
        outState.putSerializable("PLAYER_2_MOVES", player2Moves);
        outState.putInt("TOTAL_GAMES_PLAYED", totalGamesPlayed);
        outState.putInt("PLAYER_1_WINS", player1Wins);
        outState.putInt("PLAYER_2_WINS", player2Wins);
        outState.putInt("DRAWS", draws);
        outState.putInt("MOVES_MADE", movesMade);
        outState.putInt("TOTAL_MOVES", totalMoves);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore grid state
        gridState = (int[][]) savedInstanceState.getSerializable("GRID_STATE");
        // Restore current player's turn
        isPlayer1Turn = savedInstanceState.getBoolean("IS_PLAYER_1_TURN");
        // Restore grid size
        rows = savedInstanceState.getInt("ROWS");
        columns = savedInstanceState.getInt("COLUMNS");
        player1Moves = (Stack<int[]>) savedInstanceState.getSerializable("PLAYER_1_MOVES");
        player2Moves = (Stack<int[]>) savedInstanceState.getSerializable("PLAYER_2_MOVES");
        totalGamesPlayed = savedInstanceState.getInt("TOTAL_GAMES_PLAYED");
        player1Wins = savedInstanceState.getInt("PLAYER_1_WINS");
        player2Wins = savedInstanceState.getInt("PLAYER_2_WINS");
        draws = savedInstanceState.getInt("DRAWS");
        movesMade = savedInstanceState.getInt("MOVES_MADE", 0);
        totalMoves = savedInstanceState.getInt("TOTAL_MOVES", rows * columns);

        // Update UI
        updateGameBoard();
        updateMoveCount();
    }

    private void updateGameBoard() {
        // Update the UI based on the restored grid state
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (gridState[i][j] == 1) {
                    gridButtons[i][j].setBackgroundColor(player1Color);
                } else if (gridState[i][j] == 2) {
                    gridButtons[i][j].setBackgroundColor(player2Color);
                } else {
                    gridButtons[i][j].setBackgroundColor(Color.WHITE); // Reset to empty
                }
            }
        }
        // Update player turn text
        playerTurnTextView.setText((isPlayer1Turn ? player1Name : player2Name) + "'s turn");
    }

    private void setUpGameBoard(String gridSize) {
        // Determine the grid size based on the user's selection
        switch (gridSize) {
            case "Small (6x5)":
                rows = 5;
                columns = 6;
                break;
            case "Large (8x7)":
                rows = 7;
                columns = 8;
                break;
            default:
                rows = 6;
                columns = 7;
                break;
        }
        totalMoves = rows * columns; // Calculate total moves based on the grid size
        movesMade = 0; // Reset moves made at the start of the game
        updateMoveCount(); // Update the move count display

        // Initialize the button grid and grid state
        gridButtons = new Button[rows][columns];
        gridState = new int[rows][columns]; // 0 = empty, 1 = player 1, 2 = player 2

        // Set the row and column count for the GridLayout
        gameBoard.setRowCount(rows);
        gameBoard.setColumnCount(columns);

        // Dynamically create the grid cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Button cellButton = new Button(this);
                // Set layout parameters to create grid lines
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.setMargins(2, 2, 2, 2); // Adjust margins to create visible grid lines
                cellButton.setLayoutParams(params);
                cellButton.setPadding(0, 0, 0, 0); // No internal padding for a cleaner look
                cellButton.setBackgroundColor(Color.WHITE); // White cells

                // Set the onClickListener for the cell
                final int col = j; // Store column index for the onClick listener
                cellButton.setOnClickListener(v -> onCellClick(col));

                gridButtons[i][j] = cellButton; // Store button reference
                gameBoard.addView(cellButton); // Add button to the game board
            }
        }

        // Set button size dynamically based on available space
        setButtonSize();
    }

    private void setButtonSize() {
        // Calculate the total margins for the columns
        int totalHorizontalMargins = (columns + 1) * 4; // Adjust margins as needed
        int availableWidth = getResources().getDisplayMetrics().widthPixels - totalHorizontalMargins;

        // Calculate the total margins for the rows
        int totalVerticalMargins = (rows + 1) * 4; // Adjust margins as needed
        int availableHeight = getResources().getDisplayMetrics().heightPixels - totalVerticalMargins;

        // Calculate the button size based on both width and height
        int buttonSize = (int) (Math.min(availableWidth / columns, availableHeight / rows) * 0.8); // Use 80% of the available space

        // Update each button's layout parameters to set specific sizes
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = buttonSize; // Set width directly
                params.height = buttonSize; // Set height directly
                params.setMargins(2, 2, 2, 2); // Adjust margins if needed

                gridButtons[i][j].setLayoutParams(params); // Apply new layout parameters
            }
        }
    }


    private void onCellClick(int col) {
        // Find the lowest empty row in the selected column
        for (int row = rows - 1; row >= 0; row--) {
            if (gridState[row][col] == 0) {
                // Update grid state
                gridState[row][col] = isPlayer1Turn ? 1 : 2;

                // Push the move to the corresponding stack based on the player
                if (isPlayer1Turn) {
                    player1Moves.push(new int[]{row, col});
                } else {
                    player2Moves.push(new int[]{row, col});
                }

                movesMade++; // Increment moves made
                updateMoveCount(); // Update the move count display

                // Create a drop animation
                final Button button = gridButtons[row][col];
                button.setTranslationY(-1000); // Start the button above the screen
                button.setBackgroundColor(isPlayer1Turn ? player1Color : player2Color); // Set color

                final int finalRow = row;
                button.animate()
                        .translationY(0) // Move the button to its final position
                        .setDuration(500) // Duration of the animation
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // Check for a win after the animation ends
                                if (checkForWin(finalRow, col)) {
                                    showWinMessage(isPlayer1Turn ? player1Name : player2Name);
                                } else {
                                    // Switch turns
                                    isPlayer1Turn = !isPlayer1Turn;

                                    // Update player turn text
                                    if (isAiMode && !isPlayer1Turn) {
                                        playerTurnTextView.setText("AI player's turn"); // Update for AI turn
                                    } else {
                                        playerTurnTextView.setText((isPlayer1Turn ? player1Name : player2Name) + "'s turn");
                                    }

                                    // Disable user interaction if AI mode is enabled and it's AI's turn
                                    if (isAiMode && !isPlayer1Turn) {
                                        gameBoard.setEnabled(false); // Disable the board to prevent user clicks
                                        makeAiMove(); // Make AI move
                                    }
                                }
                            }
                        })
                        .start(); // Start the animation

                return; // Exit once the move is made
            }
        }

        // If the column is full
        Toast.makeText(this, "Column is full! Choose another column.", Toast.LENGTH_SHORT).show();
    }


    private void makeAiMove() {
        // Delay AI move for 1 second to simulate thinking time
        new Handler().postDelayed(() -> {
            // Simple AI: random move
            int col;
            do {
                col = random.nextInt(columns);
            } while (gridState[0][col] != 0); // Find an empty column

            // Trigger onCellClick for AI's move
            onCellClick(col);
            // Re-enable user interaction after AI move
            gameBoard.setEnabled(true);
            // Update turn text after AI's move
            playerTurnTextView.setText(player1Name + "'s turn"); // Reset back to player 1
        }, 1000);
    }

    private List<int[]> winningCells = new ArrayList<>(); // Store winning cell coordinates

    private boolean checkForWin(int row, int col) {
        winningCells.clear(); // Clear previous winning cells
        boolean hasWon = checkDirection(row, col, 1, 0) || // Horizontal
                checkDirection(row, col, 0, 1) || // Vertical
                checkDirection(row, col, 1, 1) || // Diagonal \
                checkDirection(row, col, 1, -1);   // Diagonal /

        if (hasWon) {
            winningCells.add(new int[]{row, col}); // Include the last added cell
            flashWinningCells(winningCells); // Flash the winning cells if there's a win
        }

        return hasWon;
    }


    private boolean checkDirection(int row, int col, int rowStep, int colStep) {
        int count = 1; // Start with current piece
        List<int[]> tempWinningCells = new ArrayList<>(); // Temporarily store winning cells

        // Check in one direction
        count += countPieces(row, col, rowStep, colStep, tempWinningCells);
        // Check in the opposite direction
        count += countPieces(row, col, -rowStep, -colStep, tempWinningCells);

        // If count is 4 or more, store the winning cells
        if (count >= 4) {
            winningCells.addAll(tempWinningCells);
            return true;
        }
        return false;
    }


    private int countPieces(int row, int col, int rowStep, int colStep, List<int[]> tempWinningCells) {
        int colorToCheck = gridState[row][col];
        int count = 0;

        // Check until out of bounds
        while (true) {
            row += rowStep;
            col += colStep;

            // Check for valid position
            if (row < 0 || row >= rows || col < 0 || col >= columns) {
                break;
            }
            // Check if it's the same color
            if (gridState[row][col] == colorToCheck) {
                count++;
                tempWinningCells.add(new int[]{row, col}); // Store the winning cell
            } else {
                break;
            }
        }
        return count;
    }

    private void flashWinningCells(List<int[]> winningCells) {
        // Flash the winning cells
        for (int[] cell : winningCells) {
            int row = cell[0];
            int col = cell[1];
            Button button = gridButtons[row][col];

            // Flash the button for winning cells
            ObjectAnimator colorAnim = ObjectAnimator.ofArgb(
                    button,
                    "backgroundColor",
                    Color.parseColor("#6A5ACD"), // Slate Blue (soft but vibrant)
                    Color.parseColor("#4682B4"), // Steel Blue (calm and pleasant)
                    Color.parseColor("#8FBC8F")  // Dark Sea Green (soft, earthy tone)
            );

            colorAnim.setDuration(200); // Duration of the flash
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            // Start the animation
            colorAnim.start();
            // Store the color animator to stop it later
            button.setTag(colorAnim); // Set the animator as a tag to access it later
        }
        // Stop the flashing after 3 seconds and reset the board
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (int[] cell : winningCells) {
                int row = cell[0];
                int col = cell[1];
                Button button = gridButtons[row][col];

                // Stop the animation
                ObjectAnimator colorAnim = (ObjectAnimator) button.getTag();
                if (colorAnim != null) {
                    colorAnim.cancel(); // Stop the animation
                }
                // Reset the button background to the normal state (you can set this to whatever color your normal state is)
                button.setBackgroundColor(Color.WHITE); // Set to normal state
            }
            // Optionally reset the entire board
            resetGame(); // Call resetGame to return all buttons to their normal state if needed
        }, 3000); // Duration for how long you want the flashing to last
    }




    private void showWinMessage(String winner) {
        totalGamesPlayed++;

        // Disable the game board
        gameBoard.setEnabled(false);

        // Create a dialog for the game over screen
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.game_over);

        // Find views in the game over layout
        TextView winnerBanner = dialog.findViewById(R.id.winnerBanner);
        Button playAgainButton = dialog.findViewById(R.id.playAgainButton);
        Button shareButton = dialog.findViewById(R.id.shareButton);

        // Set the winner text
        if (winner.equals(player1Name)) {
            player1Wins++;
            winnerBanner.setText(player1Name + " wins!");
        } else if (winner.equals(player2Name)) {
            player2Wins++;
            winnerBanner.setText(player2Name + " wins!");
        } else {
            draws++;
            winnerBanner.setText("It's a draw!");
        }

        // Play Again button functionality
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                resetGame();
            }
        });

        // Share button functionality
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareGameResult(winner);
            }
        });

        // Show the dialog
        dialog.show();

        // Animate the dialog for a more attractive UI
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    private void shareGameResult(String winner) {
        String shareText;
        if (winner.equals(player1Name)) {
            shareText = player1Name + " won the game!";
        } else if (winner.equals(player2Name)) {
            shareText = player2Name + " won the game!";
        } else {
            shareText = "The game ended in a draw!";
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share your victory!");
        startActivity(shareIntent);
    }




    private void resetGame() {
        // Reset the game state without restarting the activity
        gridState = new int[rows][columns]; // Reset grid state
        isPlayer1Turn = true; // Reset to player 1's turn
        playerTurnTextView.setText(player1Name + "'s turn"); // Update UI
        movesMade = 0; // Reset moves made
        updateMoveCount(); // Update the move count display
        // Reset grid buttons
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                gridButtons[i][j].setBackgroundColor(Color.WHITE); // Reset button color to white
                gridButtons[i][j].clearAnimation(); // Clear any animations
            }
        }
        // If you have a list of winning cells, clear it
        winningCells.clear(); // Assuming you have a collection to track winning cells
    }
    private void undoLastMove() {
        // Determine the stack of moves for the current player
        Stack<int[]> currentPlayerStack = isPlayer1Turn ? player1Moves : player2Moves;

        // Check if the stack is empty, meaning no moves to undo for the current player
        if (currentPlayerStack.isEmpty()) {
            Toast.makeText(this, "No moves to undo!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the player has just undone their last move
        if ((isPlayer1Turn && player1LastUndo) || (!isPlayer1Turn && player2LastUndo)) {
            Toast.makeText(this, "You cannot undo again in the same turn!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prevent undo if it's AI's move and it's currently the AI's turn
        if (isAiMode && !isPlayer1Turn) {
            Toast.makeText(this, "Cannot undo AI's move!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pop the move from the current player's stack and update the board state
        int[] lastMove = currentPlayerStack.pop();
        int row = lastMove[0];
        int col = lastMove[1];

        // Shift the discs above the undone move down
        for (int i = row; i > 0; i--) {
            // Move the disc from the row above to the current row
            gridState[i][col] = gridState[i - 1][col];
            // Update the button color to reflect the shifted disc
            if (gridState[i][col] == 1) {
                gridButtons[i][col].setBackgroundColor(player1Color);
            } else if (gridState[i][col] == 2) {
                gridButtons[i][col].setBackgroundColor(player2Color);
            } else {
                gridButtons[i][col].setBackgroundColor(Color.WHITE);
            }
        }

        // Clear the top cell in the column since it should now be empty
        gridState[0][col] = 0;
        gridButtons[0][col].setBackgroundColor(Color.WHITE);

        // Set flags to indicate that the current player has just undone their last move
        if (isPlayer1Turn) {
            player1LastUndo = true;
            player2LastUndo = false; // Reset the other player's undo status
        } else {
            player2LastUndo = true;
            player1LastUndo = false; // Reset the other player's undo status
        }
        movesMade--; // Decrement moves made
        updateMoveCount(); // Update the move count display

        // Update the player turn text to reflect whose turn it is
        playerTurnTextView.setText((isPlayer1Turn ? player1Name : player2Name) + "'s turn");

        // Allow the same player to make another move after undo
    }
    private void updateMoveCount() {
        int remainingMoves = totalMoves - movesMade;
        moveCountTextView.setText("Moves Made: " + movesMade + " | Remaining Moves: " + remainingMoves);
    }

    // Function to handle exiting the game
    private void exitGame() {
        finish(); // Exits the current activity
    }
}
