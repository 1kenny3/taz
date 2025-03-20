package com.tazar.android.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tazar.android.R;
import com.tazar.android.TazarApplication;
import com.tazar.android.utils.PreferenceManager;

public class ProfileFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        TextView usernameText = view.findViewById(R.id.username_text);
        TextView emailText = view.findViewById(R.id.email_text);
        
        PreferenceManager preferenceManager = TazarApplication.getPreferenceManager();
        if (preferenceManager != null) {
            if (usernameText != null) {
                usernameText.setText(preferenceManager.getUsername());
            }
            if (emailText != null) {
                emailText.setText(preferenceManager.getEmail());
            }
        }
    }
} 