package com.example.connect_four;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private Spinner gridSizeSpinner;
    private EditText player1NameEditText, player2NameEditText;
    private Button startGameButton, player1ColorButton, player2ColorButton,viewProfileButton, profile_creation_button, player1ProfileCreationButton, player2ProfileCreationButton;
    private CheckBox twoPlayerCheckBox, aiModeCheckBox;
    private int player1Color = Color.RED; // Default color for Player 1
    private int player2Color = Color.YELLOW; // Default color for Player 2
    private boolean isTwoPlayerMode = true; // Default to 2 Player Mode

    private TextView player2Label;
    private TextView player2ColorLabel;
    private ActivityResultLauncher<Intent> player1ProfileLauncher;
    private ActivityResultLauncher<Intent> player2ProfileLauncher;
    private SharedPreferences sharedPreferences;
    private boolean isPlayer1ProfileCreated = false;
    private boolean isPlayer2ProfileCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ConnectFourPreferences", MODE_PRIVATE);

        // Clear profile-related data on app start
        clearProfileData();

        gridSizeSpinner = findViewById(R.id.grid_size_spinner);
        player1NameEditText = findViewById(R.id.player1_name);
        player2NameEditText = findViewById(R.id.player2_name);
        startGameButton = findViewById(R.id.start_game_button);
        player1ColorButton = findViewById(R.id.player1_color_button);
        player2ColorButton = findViewById(R.id.player2_color_button);
        twoPlayerCheckBox = findViewById(R.id.two_player_mode_checkbox);
        aiModeCheckBox = findViewById(R.id.ai_mode_checkbox);
        player2Label = findViewById(R.id.player2_label);
        player2ColorLabel = findViewById(R.id.player2_color_label);
        profile_creation_button = findViewById(R.id.profile_creation_button);
        player1ProfileCreationButton = findViewById(R.id.player1_profile_creation_button);
        player2ProfileCreationButton = findViewById(R.id.player2_profile_creation_button);
        viewProfileButton = findViewById(R.id.view_profile_button);


        // Restore saved state if available
        if (savedInstanceState != null) {
            player1NameEditText.setText(savedInstanceState.getString("PLAYER_1_NAME"));
            player2NameEditText.setText(savedInstanceState.getString("PLAYER_2_NAME"));
            gridSizeSpinner.setSelection(savedInstanceState.getInt("GRID_SIZE_INDEX"));
            player1Color = savedInstanceState.getInt("PLAYER_1_COLOR");
            player2Color = savedInstanceState.getInt("PLAYER_2_COLOR");
            player1ColorButton.setBackgroundColor(player1Color);
            player2ColorButton.setBackgroundColor(player2Color);
            isTwoPlayerMode = savedInstanceState.getBoolean("IS_TWO_PLAYER_MODE");
            twoPlayerCheckBox.setChecked(isTwoPlayerMode);
            aiModeCheckBox.setChecked(!isTwoPlayerMode);
            isPlayer1ProfileCreated = savedInstanceState.getBoolean("IS_PLAYER_1_PROFILE_CREATED");
            isPlayer2ProfileCreated = savedInstanceState.getBoolean("IS_PLAYER_2_PROFILE_CREATED");
            updateVisibility(); // Ensure correct visibility on restore
        } else {
            isPlayer1ProfileCreated = sharedPreferences.getBoolean("IS_PLAYER_1_PROFILE_CREATED", false);
            isPlayer2ProfileCreated = sharedPreferences.getBoolean("IS_PLAYER_2_PROFILE_CREATED", false);
        }

        // Update button text based on profile creation status
        updateProfileButtons();

        // Load and display player profiles
        loadProfileData();

        player1ColorButton.setOnClickListener(v -> openColorPicker(1));
        player2ColorButton.setOnClickListener(v -> openColorPicker(2));

        // Set onClickListener for the CheckBoxes
        twoPlayerCheckBox.setOnClickListener(v -> {
            if (twoPlayerCheckBox.isChecked()) {
                aiModeCheckBox.setChecked(false);
                isTwoPlayerMode = true;
            } else {
                aiModeCheckBox.setChecked(true);
                isTwoPlayerMode = false;
            }
            updateVisibility(); // Update visibility when the mode changes
        });

        aiModeCheckBox.setOnClickListener(v -> {
            if (aiModeCheckBox.isChecked()) {
                twoPlayerCheckBox.setChecked(false);
                isTwoPlayerMode = false;
            } else {
                twoPlayerCheckBox.setChecked(true);
                isTwoPlayerMode = true;
            }
            updateVisibility(); // Update visibility when the mode changes
        });

        // Set OnClickListener for Create Profile Button
        profile_creation_button.setOnClickListener(v -> {
            if (twoPlayerCheckBox.isChecked() || aiModeCheckBox.isChecked()) {
                // Proceed with profile creation
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else {
                // Display toast message if no mode is selected
                Toast.makeText(MainActivity.this, "Please select player mode first", Toast.LENGTH_SHORT).show();
            }
        });

        // Register the launcher for Player 1 profile creation
        player1ProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Update the flag and SharedPreferences for Player 1 profile
                        isPlayer1ProfileCreated = true;
                        sharedPreferences.edit().putBoolean("IS_PLAYER_1_PROFILE_CREATED", true).apply();
                        updateProfileButtons();
                    }
                });

        // Register the launcher for Player 2 profile creation
        player2ProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Update the flag and SharedPreferences for Player 2 profile
                        isPlayer2ProfileCreated = true;
                        sharedPreferences.edit().putBoolean("IS_PLAYER_2_PROFILE_CREATED", true).apply();
                        updateProfileButtons();
                    }
                });

        // Set OnClickListeners for profile creation buttons
        player1ProfileCreationButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("PLAYER_IDENTIFIER", "PLAYER_1");
            player1ProfileLauncher.launch(intent);
        });

        player2ProfileCreationButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("PLAYER_IDENTIFIER", "PLAYER_2");
            player2ProfileLauncher.launch(intent);
        });

        startGameButton.setOnClickListener(v -> {
            if (twoPlayerCheckBox.isChecked() || aiModeCheckBox.isChecked()){
                // Retrieve the selected grid size and player names
                String gridSize = gridSizeSpinner.getSelectedItem().toString();
                String player1Name = player1NameEditText.getText().toString();
                String player2Name = player2NameEditText.getText().toString();

                // Create an Intent to start GameActivity
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("GRID_SIZE", gridSize);
                intent.putExtra("PLAYER_1_NAME", player1Name);
                intent.putExtra("PLAYER_2_NAME", player2Name);
                intent.putExtra("PLAYER_1_COLOR", player1Color);
                intent.putExtra("PLAYER_2_COLOR", player2Color);
                intent.putExtra("AI_MODE", !isTwoPlayerMode); // Pass AI mode status

                // Start GameActivity
                startActivity(intent);}
            else{
                // Display toast message if no mode is selected
                Toast.makeText(MainActivity.this, "Please select player mode first", Toast.LENGTH_SHORT).show();
            }
        });

        // Add this block to initialize the viewProfileButton and set its onClickListener
        viewProfileButton.setOnClickListener(v -> {
            if (isPlayer1ProfileCreated || isPlayer2ProfileCreated) {
                Intent intent = new Intent(MainActivity.this, ViewProfileActivity.class);
                intent.putExtra("IS_TWO_PLAYER_MODE", isTwoPlayerMode);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "No profiles available to view", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to clear profile-related data
    private void clearProfileData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("PLAYER_1_NAME");
        editor.remove("PLAYER_2_NAME");
        editor.remove("PLAYER_1_AVATAR_ID");
        editor.remove("PLAYER_2_AVATAR_ID");
        editor.remove("IS_PLAYER_1_PROFILE_CREATED");
        editor.remove("IS_PLAYER_2_PROFILE_CREATED");
        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of player names, grid size index, colors, and selected mode
        outState.putString("PLAYER_1_NAME", player1NameEditText.getText().toString());
        outState.putString("PLAYER_2_NAME", player2NameEditText.getText().toString());
        outState.putInt("GRID_SIZE_INDEX", gridSizeSpinner.getSelectedItemPosition());
        outState.putInt("PLAYER_1_COLOR", player1Color);
        outState.putInt("PLAYER_2_COLOR", player2Color);
        outState.putBoolean("IS_TWO_PLAYER_MODE", isTwoPlayerMode);
        outState.putBoolean("IS_PLAYER_1_PROFILE_CREATED", isPlayer1ProfileCreated);
        outState.putBoolean("IS_PLAYER_2_PROFILE_CREATED", isPlayer2ProfileCreated);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the state of player names, grid size index, colors, and selected mode
        player1NameEditText.setText(savedInstanceState.getString("PLAYER_1_NAME"));
        player2NameEditText.setText(savedInstanceState.getString("PLAYER_2_NAME"));
        gridSizeSpinner.setSelection(savedInstanceState.getInt("GRID_SIZE_INDEX"));
        player1Color = savedInstanceState.getInt("PLAYER_1_COLOR");
        player2Color = savedInstanceState.getInt("PLAYER_2_COLOR");
        player1ColorButton.setBackgroundColor(player1Color);
        player2ColorButton.setBackgroundColor(player2Color);
        isTwoPlayerMode = savedInstanceState.getBoolean("IS_TWO_PLAYER_MODE");
        isPlayer1ProfileCreated = savedInstanceState.getBoolean("IS_PLAYER_1_PROFILE_CREATED");
        isPlayer2ProfileCreated = savedInstanceState.getBoolean("IS_PLAYER_2_PROFILE_CREATED");
        twoPlayerCheckBox.setChecked(isTwoPlayerMode);
        aiModeCheckBox.setChecked(!isTwoPlayerMode);
        updateVisibility(); // Update visibility based on restored state
    }

    private void openColorPicker(int player) {
        ColorPickerDialog dialog = new ColorPickerDialog(this, (color) -> {
            if (player == 1) {
                player1Color = color;
                player1ColorButton.setBackgroundColor(color);
            } else {
                player2Color = color;
                player2ColorButton.setBackgroundColor(color);
            }
        }, player == 1 ? player1Color : player2Color);
        dialog.show();
    }

    private void updateVisibility() {
        // Show/Hide the player name and color fields based on the game mode
        if (isTwoPlayerMode) {
            player1NameEditText.setVisibility(View.VISIBLE);
            player2NameEditText.setVisibility(View.VISIBLE);
            player1ColorButton.setVisibility(View.VISIBLE);
            player2ColorButton.setVisibility(View.VISIBLE);

            // Make Player 2 labels visible
            player2Label.setVisibility(View.VISIBLE);
            player2ColorLabel.setVisibility(View.VISIBLE);

            // Show profile creation buttons for both players
            player1ProfileCreationButton.setVisibility(View.VISIBLE);
            player2ProfileCreationButton.setVisibility(View.VISIBLE);
            profile_creation_button.setVisibility(View.GONE); // Hide generic profile creation button
        } else {
            player1NameEditText.setVisibility(View.VISIBLE);
            player2NameEditText.setVisibility(View.GONE); // Hide Player 2 fields in AI mode
            player1ColorButton.setVisibility(View.VISIBLE);
            player2ColorButton.setVisibility(View.GONE); // Hide Player 2 color button in AI mode

            // Hide Player 2 labels
            player2Label.setVisibility(View.GONE);
            player2ColorLabel.setVisibility(View.GONE);

            // Show only Player 1 profile creation button
            player1ProfileCreationButton.setVisibility(View.VISIBLE);
            player2ProfileCreationButton.setVisibility(View.GONE);
            profile_creation_button.setVisibility(View.GONE);
        }
    }

    private void updateProfileButtons() {
        if (isPlayer1ProfileCreated) {
            player1ProfileCreationButton.setText("Edit Profile for Player 1");
        } else {
            player1ProfileCreationButton.setText("Create Profile for Player 1");
        }

        if (isPlayer2ProfileCreated) {
            player2ProfileCreationButton.setText("Edit Profile for Player 2");
        } else {
            player2ProfileCreationButton.setText("Create Profile for Player 2");
        }
    }

    private void loadProfileData() {
        // Retrieve saved profile data
        String player1Name = sharedPreferences.getString("PLAYER_1_NAME", "Player 1");
        String player2Name = sharedPreferences.getString("PLAYER_2_NAME", "Player 2");
        int player1AvatarId = sharedPreferences.getInt("PLAYER_1_AVATAR_ID", -1);
        int player2AvatarId = sharedPreferences.getInt("PLAYER_2_AVATAR_ID", -1);

        // Update UI with retrieved data
        player1NameEditText.setText(player1Name);
        player2NameEditText.setText(player2Name);
        // Set avatars if you have ImageViews or other methods to display avatars
    }
}
