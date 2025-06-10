package com.example.project;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProfileBottom extends BottomSheetDialogFragment {

    TextView name;
    TextView email;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_bottom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name=view.findViewById(R.id.profile_name);
        email=view.findViewById(R.id.profile_email);
        Button logoutButton = view.findViewById(R.id.btn_logout);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", MODE_PRIVATE);
        name.setText(sharedPreferences.getString("name",""));
        email.setText( sharedPreferences.getString("email",""));

        logoutButton.setOnClickListener(e -> {

            SharedPreferences sharedPreferencess = getActivity().getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencess.edit();
            editor.remove("user_id");
            editor.remove("role");
            editor.apply();

            Intent intent=new Intent(requireContext(), Login.class);
            startActivity(intent);
            getActivity().finish();
            Toast.makeText(getContext(), "Logout ", Toast.LENGTH_SHORT).show();


        });
    }
}

