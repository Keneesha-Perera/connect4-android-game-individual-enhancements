package com.example.connect_four;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StatisticsActivity extends AppCompatActivity {

    private TextView player1GamesPlayedTextView;
    private TextView player1WinsTextView;
    private TextView player1LossesTextView;
    private TextView player1WinPercentageTextView;

    private TextView player2GamesPlayedTextView;
    private TextView player2WinsTextView;
    private TextView player2LossesTextView;
    private TextView player2WinPercentageTextView;

    private Button backButton;

    private String player1Name;
    private String player2Name;
    private int totalGamesPlayed;
    private int player1Wins;
    private int player2Wins;
    private int draws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Initialize the TextViews for Player 1
        player1GamesPlayedTextView = findViewById(R.id.player1_games_played);
        player1WinsTextView = findViewById(R.id.player1_wins);
        player1LossesTextView = findViewById(R.id.player1_losses);
        player1WinPercentageTextView = findViewById(R.id.player1_win_percentage);

        // Initialize the TextViews for Player 2
        player2GamesPlayedTextView = findViewById(R.id.player2_games_played);
        player2WinsTextView = findViewById(R.id.player2_wins);
        player2LossesTextView = findViewById(R.id.player2_losses);
        player2WinPercentageTextView = findViewById(R.id.player2_win_percentage);

        // Initialize the Back Button
        backButton = findViewById(R.id.back_button);

        // Retrieve the statistics data passed from GameActivity
        player1Name = getIntent().getStringExtra("PLAYER_1_NAME");
        player2Name = getIntent().getStringExtra("PLAYER_2_NAME");
        totalGamesPlayed = getIntent().getIntExtra("TOTAL_GAMES_PLAYED", 0);
        player1Wins = getIntent().getIntExtra("PLAYER_1_WINS", 0);
        player2Wins = getIntent().getIntExtra("PLAYER_2_WINS", 0);
        draws = getIntent().getIntExtra("DRAWS", 0);

        // Calculate individual games played for each player
        int player1TotalGames = player1Wins + totalGamesPlayed - player1Wins - draws;
        int player2TotalGames = player2Wins + totalGamesPlayed - player2Wins - draws;

        // Display Player 1 statistics
        updatePlayerStatistics(player1Name, player1Wins, player1TotalGames, player1GamesPlayedTextView, player1WinsTextView, player1LossesTextView, player1WinPercentageTextView);

        // Display Player 2 statistics if a second player exists
        if (player2Name != null && !player2Name.isEmpty()) {
            updatePlayerStatistics(player2Name, player2Wins, player2TotalGames, player2GamesPlayedTextView, player2WinsTextView, player2LossesTextView, player2WinPercentageTextView);
        } else {
            // Hide Player 2 stats if no second player
            player2GamesPlayedTextView.setVisibility(View.GONE);
            player2WinsTextView.setVisibility(View.GONE);
            player2LossesTextView.setVisibility(View.GONE);
            player2WinPercentageTextView.setVisibility(View.GONE);
        }

        // Set up the back button click listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous activity
                finish();
            }
        });
    }

    /**
     * Update statistics for a player.
     *
     * @param playerName             The name of the player.
     * @param wins                   The number of wins for the player.
     * @param totalGames             The total number of games played by the player.
     * @param gamesPlayedTextView    The TextView to display the number of games played.
     * @param winsTextView           The TextView to display the number of wins.
     * @param lossesTextView         The TextView to display the number of losses.
     * @param winPercentageTextView  The TextView to display the win percentage.
     */
    private void updatePlayerStatistics(String playerName, int wins, int totalGames, TextView gamesPlayedTextView, TextView winsTextView, TextView lossesTextView, TextView winPercentageTextView) {
        int losses = totalGames - wins - draws; // Calculate losses

        double winPercentage = totalGames > 0 ? (double) wins / totalGames * 100 : 0;

        // Update the TextViews with statistics
        gamesPlayedTextView.setText("Games Played: " + totalGames);
        winsTextView.setText("Wins: " + wins);
        lossesTextView.setText("Losses: " + losses);
        winPercentageTextView.setText("Win Percentage: " + String.format("%.2f", winPercentage) + "%");
    }
}
