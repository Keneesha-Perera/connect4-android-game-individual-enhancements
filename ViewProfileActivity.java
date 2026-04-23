package com.example.connect_four;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewProfileActivity extends AppCompatActivity {

    private static final String TAG = "ViewProfileActivity";

    private TextView player1NameTextView, player2NameTextView;
    private ImageView player1AvatarImageView, player2AvatarImageView;
    private LinearLayout player2ProfileContainer;
    private Button backButton;
    private SharedPreferences sharedPreferences;
    private boolean isTwoPlayerMode;

    private String player1Name, player2Name;
    private int player1AvatarId, player2AvatarId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Initialize UI elements
        player1NameTextView = findViewById(R.id.player1_name_textview);
        player2NameTextView = findViewById(R.id.player2_name_textview);
        player1AvatarImageView = findViewById(R.id.player1_avatar_imageview);
        player2AvatarImageView = findViewById(R.id.player2_avatar_imageview);
        player2ProfileContainer = findViewById(R.id.player2_profile_container);
        backButton = findViewById(R.id.back_button);

        // Access shared preferences
        sharedPreferences = getSharedPreferences("ConnectFourPreferences", MODE_PRIVATE);

        // Get the game mode from the intent
        isTwoPlayerMode = getIntent().getBooleanExtra("IS_TWO_PLAYER_MODE", false);

        // Load and display profiles
        loadProfiles();

        // Set up the back button
        backButton.setOnClickListener(v -> finish()); // Close the activity and return to the previous screen

        // Restore state if saved instance state is not null
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
    }

    private void loadProfiles() {
        // Load Player 1's profile
        player1Name = sharedPreferences.getString("PLAYER_1_NAME", "Player 1");
        player1AvatarId = sharedPreferences.getInt("PLAYER_1_AVATAR_ID", R.drawable.default_avatar);

        // Set Player 1's profile data
        player1NameTextView.setText(player1Name);
        player1AvatarImageView.setImageResource(player1AvatarId);

        if (isTwoPlayerMode) {
            // Check if Player 2 profile data exists
            player2Name = sharedPreferences.getString("PLAYER_2_NAME", null);
            player2AvatarId = sharedPreferences.getInt("PLAYER_2_AVATAR_ID", -1);

            if (player2Name != null && player2AvatarId != -1) {
                // Show Player 2 profile views
                player2ProfileContainer.setVisibility(View.VISIBLE);
                player2NameTextView.setText(player2Name);
                player2AvatarImageView.setImageResource(player2AvatarId);
            } else {
                // Hide Player 2 profile views if Player 2 profile doesn't exist
                player2ProfileContainer.setVisibility(View.GONE);
            }
        } else {
            // Hide Player 2 profile views in AI mode
            player2ProfileContainer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save player names, avatar IDs, and game mode state
        outState.putString("PLAYER_1_NAME", player1Name);
        outState.putInt("PLAYER_1_AVATAR_ID", player1AvatarId);
        outState.putString("PLAYER_2_NAME", player2Name);
        outState.putInt("PLAYER_2_AVATAR_ID", player2AvatarId);
        outState.putBoolean("IS_TWO_PLAYER_MODE", isTwoPlayerMode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore player names, avatar IDs, and game mode state
        player1Name = savedInstanceState.getString("PLAYER_1_NAME", "Player 1");
        player1AvatarId = savedInstanceState.getInt("PLAYER_1_AVATAR_ID", R.drawable.default_avatar);
        player2Name = savedInstanceState.getString("PLAYER_2_NAME", null);
        player2AvatarId = savedInstanceState.getInt("PLAYER_2_AVATAR_ID", -1);
        isTwoPlayerMode = savedInstanceState.getBoolean("IS_TWO_PLAYER_MODE", false);

        // Restore UI elements based on the restored data
        player1NameTextView.setText(player1Name);
        player1AvatarImageView.setImageResource(player1AvatarId);

        if (isTwoPlayerMode && player2Name != null && player2AvatarId != -1) {
            player2ProfileContainer.setVisibility(View.VISIBLE);
            player2NameTextView.setText(player2Name);
            player2AvatarImageView.setImageResource(player2AvatarId);
        } else {
            player2ProfileContainer.setVisibility(View.GONE);
        }
    }
}
