package com.neeravtanay.neuratask.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.activities.ChangePasswordActivity;

import java.io.Serializable;
import java.util.Random;

public class ProfileFragment extends Fragment {

    private static final String ARG_USER_PROFILE = "userProfile";

    private TextView tvName, tvEmail, tvAge;
    private Button btnLogout, btnChangePassword;
    private ImageView ivProfile;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private final int[] avatars = {R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3};
    private UserProfile userProfile;

    public static ProfileFragment newInstance(UserProfile profile) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_PROFILE, profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_profile, container, false);

        tvName = v.findViewById(R.id.tvName);
        tvEmail = v.findViewById(R.id.tvEmail);
        tvAge = v.findViewById(R.id.tvAge);
        btnLogout = v.findViewById(R.id.btnLogout);
        btnChangePassword = v.findViewById(R.id.btnChangePassword);
        ivProfile = v.findViewById(R.id.ivProfile);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Retrieve UserProfile from arguments
        if (getArguments() != null) {
            userProfile = (UserProfile) getArguments().getSerializable(ARG_USER_PROFILE);
        }

        if (userProfile != null) {
            loadProfileFromObject();
        } else if (auth.getCurrentUser() != null) {
            loadProfileFromFirestore();
        }

        ivProfile.setOnClickListener(v1 -> showAvatarOptions());
        btnLogout.setOnClickListener(v1 -> {
            auth.signOut();
            if (getContext() != null) {
                startActivity(new android.content.Intent(getContext(), com.neeravtanay.neuratask.activities.LoginActivity.class));
                requireActivity().finish();
            }
        });
        btnChangePassword.setOnClickListener(v12 ->
                startActivity(new android.content.Intent(getContext(), ChangePasswordActivity.class))
        );

        return v;
    }

    private void loadProfileFromFirestore() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        userProfile = doc.toObject(UserProfile.class);
                        if (userProfile != null) loadProfileFromObject();
                    } else {
                        int randomIndex = new Random().nextInt(avatars.length);
                        userProfile = new UserProfile(auth.getCurrentUser().getEmail());
                        userProfile.avatarIndex = randomIndex;
                        db.collection("users").document(uid)
                                .set(userProfile, SetOptions.merge());
                        loadProfileFromObject();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                );
    }

    private void loadProfileFromObject() {
        tvEmail.setText(userProfile.email);
        tvName.setText(userProfile.name == null || userProfile.name.isEmpty() ? "No Name" : userProfile.name);
        tvAge.setText(userProfile.age == null ? "" : userProfile.age);

        int avatarIndex = userProfile.avatarIndex;
        if (avatarIndex < 0 || avatarIndex >= avatars.length) {
            avatarIndex = new Random().nextInt(avatars.length);
            userProfile.avatarIndex = avatarIndex;
            db.collection("users").document(auth.getCurrentUser().getUid())
                    .update("avatarIndex", avatarIndex);
        }
        setAvatarDrawable(avatarIndex);
    }

    private void showAvatarOptions() {
        String[] avatarNames = {"Avatar 1", "Avatar 2", "Avatar 3"};
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Select Avatar")
                .setItems(avatarNames, (dialog, which) -> setAvatar(which))
                .show();
    }

    private void setAvatar(int index) {
        if (index < 0 || index >= avatars.length) return;
        setAvatarDrawable(index);
        userProfile.avatarIndex = index;
        db.collection("users").document(auth.getCurrentUser().getUid())
                .update("avatarIndex", index)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Avatar set!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save avatar", Toast.LENGTH_SHORT).show());
    }

    private void setAvatarDrawable(int index) {
        Glide.with(requireContext())
                .load(avatars[index])
                .circleCrop()
                .into(ivProfile);
    }

    // Serializable UserProfile class
    public static class UserProfile implements Serializable {
        public String name = "";
        public String email;
        public String age = "";
        public int avatarIndex = 0;

        public UserProfile() {} // Firestore constructor

        public UserProfile(String email) {
            this.email = email;
        }
    }
}
