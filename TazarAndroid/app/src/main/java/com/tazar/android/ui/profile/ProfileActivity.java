package com.tazar.android.ui.profile;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.tazar.android.R;
import com.tazar.android.helpers.PreferencesManager;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {
    private PreferencesManager preferencesManager;
    private ShapeableImageView profileImage;
    private TextView userName;
    private TextView userEmail;
    private TextView userPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Настройка toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Профиль");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        preferencesManager = new PreferencesManager(this);
        
        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.profile_name);
        userEmail = findViewById(R.id.profile_email);
        userPoints = findViewById(R.id.profile_points);

        // Заполняем данные пользователя
        userName.setText(preferencesManager.getUserName());
        userEmail.setText(preferencesManager.getUserEmail());
        userPoints.setText(getString(R.string.points_format, preferencesManager.getUserPoints()));

        String avatarUrl = preferencesManager.getUserAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .into(profileImage);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 