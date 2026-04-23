package com.example.connect_four;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private GridView avatarGridView;
    private Button saveProfileButton;
    private AvatarAdapter avatarAdapter;
    private SharedPreferences sharedPreferences;
    private String playerIdentifier;
    private int selectedAvatarId = -1; // To store the selected avatar ID
    private int selectedPosition = -1; // To track the selected position

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameEditText = findViewById(R.id.username_edit_text);
        avatarGridView = findViewById(R.id.avatar_grid_view);
        saveProfileButton = findViewById(R.id.save_profile_button);

        avatarAdapter = new AvatarAdapter(this);
        avatarGridView.setAdapter(avatarAdapter);

        sharedPreferences = getSharedPreferences("ConnectFourPreferences", MODE_PRIVATE);

        // Retrieve the player identifier
        playerIdentifier = getIntent().getStringExtra("PLAYER_IDENTIFIER");

        // Load existing profile data
        loadUserProfile();

        // Handle avatar selection
        avatarGridView.setOnItemClickListener((parent, view, position, id) -> {
            // Remove visual effect from the previously selected avatar
            if (selectedPosition != -1) {
                View previousView = avatarGridView.getChildAt(selectedPosition);
                if (previousView != null) {
                    ((ImageView) previousView).setAlpha(1.0f); // Reset transparency
                }
            }

            // Update selected position and avatar ID
            selectedPosition = position;
            selectedAvatarId = (int) id; // Store selected avatar ID

            // Apply visual effect to the newly selected avatar
            ((ImageView) view).setAlpha(0.5f); // Change transparency for the selected avatar

            // Show a toast message
            Toast.makeText(ProfileActivity.this, "Avatar selected!", Toast.LENGTH_SHORT).show();
        });

        // Handle save profile button click
        saveProfileButton.setOnClickListener(v -> saveUserProfile());

        // Restore state if saved instance state is not null
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
    }

    // Load user profile method
    private void loadUserProfile() {
        String usernameKey = playerIdentifier + "_NAME";
        String avatarIdKey = playerIdentifier + "_AVATAR_ID";

        String username = sharedPreferences.getString(usernameKey, "");
        int avatarId = sharedPreferences.getInt(avatarIdKey, -1);

        usernameEditText.setText(username);

        if (avatarId != -1) {
            selectedAvatarId = avatarId;
            // Scroll to the position of the selected avatar in the GridView
            for (int i = 0; i < avatarGridView.getCount(); i++) {
                if ((int) avatarGridView.getItemIdAtPosition(i) == avatarId) {
                    avatarGridView.setSelection(i);
                    // Highlight the selected avatar
                    View view = avatarGridView.getChildAt(i);
                    if (view != null) {
                        ((ImageView) view).setAlpha(0.5f); //Change transparency
                    }
                    break;
                }
            }
        }
    }

    // Save user profile method
    private void saveUserProfile() {
        String username = usernameEditText.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedAvatarId == -1) {
            Toast.makeText(this, "Please select an avatar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save profile data to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String usernameKey = playerIdentifier + "_NAME";
        String avatarIdKey = playerIdentifier + "_AVATAR_ID";

        editor.putString(usernameKey, username);
        editor.putInt(avatarIdKey, selectedAvatarId);
        editor.apply();

        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();

        // Notify MainActivity that the profile was saved successfully
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current state of the username and selected avatar ID
        outState.putString("USERNAME", usernameEditText.getText().toString());
        outState.putInt("SELECTED_AVATAR_ID", selectedAvatarId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the username and selected avatar ID
        String username = savedInstanceState.getString("USERNAME", "");
        selectedAvatarId = savedInstanceState.getInt("SELECTED_AVATAR_ID", -1);

        usernameEditText.setText(username);

        // Scroll to the position of the selected avatar in the GridView
        if (selectedAvatarId != -1) {
            for (int i = 0; i < avatarGridView.getCount(); i++) {
                if ((int) avatarGridView.getItemIdAtPosition(i) == selectedAvatarId) {
                    avatarGridView.setSelection(i);
                    // Highlight the selected avatar
                    View view = avatarGridView.getChildAt(i);
                    if (view != null) {
                        ((ImageView) view).setAlpha(0.5f); // Example: change transparency
                    }
                    break;
                }
            }
        }
    }
}
