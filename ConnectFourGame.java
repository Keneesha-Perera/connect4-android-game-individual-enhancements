package com.example.connect_four;

import java.util.Random;

public class ConnectFourGame {

    private int[][] board;
    private int rows;
    private int cols;
    private int currentPlayer;
    private Random random;

    public ConnectFourGame(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        board = new int[rows][cols];
        currentPlayer = 1; // Player 1 starts first
        random = new Random();
    }

    // Method to drop a disc into the board
    public void dropDisc(int col, int player) {
        for (int row = rows - 1; row >= 0; row--) {
            if (board[row][col] == 0) {
                board[row][col] = player;
                break;
            }
        }
    }

    // Method to check if a column is full
    public boolean isColumnFull(int col) {
        return board[0][col] != 0;
    }

    // Method to check if the board is full
    public boolean isBoardFull() {
        for (int col = 0; col < cols; col++) {
            if (board[0][col] == 0) {
                return false;
            }
        }
        return true;
    }

    // Method to get the current player
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    // Method to switch the current player
    public void switchPlayer() {
        currentPlayer = currentPlayer == 1 ? 2 : 1;
    }

    // Method to check if the current player has won
    public boolean checkWin() {
        return checkHorizontalWin() || checkVerticalWin() || checkDiagonalWin();
    }

    // Check for a horizontal win
    private boolean checkHorizontalWin() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col <= cols - 4; col++) {
                if (board[row][col] == currentPlayer &&
                        board[row][col] == board[row][col + 1] &&
                        board[row][col] == board[row][col + 2] &&
                        board[row][col] == board[row][col + 3]) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check for a vertical win
    private boolean checkVerticalWin() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row <= rows - 4; row++) {
                if (board[row][col] == currentPlayer &&
                        board[row][col] == board[row + 1][col] &&
                        board[row][col] == board[row + 2][col] &&
                        board[row][col] == board[row + 3][col]) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check for diagonal wins (both \ and /)
    private boolean checkDiagonalWin() {
        // Check for / diagonal wins
        for (int row = 3; row < rows; row++) {
            for (int col = 0; col <= cols - 4; col++) {
                if (board[row][col] == currentPlayer &&
                        board[row][col] == board[row - 1][col + 1] &&
                        board[row][col] == board[row - 2][col + 2] &&
                        board[row][col] == board[row - 3][col + 3]) {
                    return true;
                }
            }
        }
        // Check for \ diagonal wins
        for (int row = 3; row < rows; row++) {
            for (int col = 3; col < cols; col++) {
                if (board[row][col] == currentPlayer &&
                        board[row][col] == board[row - 1][col - 1] &&
                        board[row][col] == board[row - 2][col - 2] &&
                        board[row][col] == board[row - 3][col - 3]) {
                    return true;
                }
            }
        }
        return false;
    }

    // Method to reset the board for a new game
    public void reset() {
        board = new int[rows][cols];
        currentPlayer = 1;
    }

    // Method to get the AI's move (random valid move)
    public int getAiMove() {
        int col;
        do {
            col = random.nextInt(cols);
        } while (isColumnFull(col));
        return col;
    }

    // New Method: Check if the move is valid
    public boolean isValidMove(int col) {
        return !isColumnFull(col);
    }

    // New Method: Make a move (drop the disc and switch player)
    public boolean makeMove(int col) {
        if (isValidMove(col)) {
            dropDisc(col, currentPlayer);
            if (checkWin()) {
                return true; // Return true if the move wins the game
            }
            switchPlayer(); // Switch to the next player
            return false;
        }
        return false; // Invalid move
    }
}
