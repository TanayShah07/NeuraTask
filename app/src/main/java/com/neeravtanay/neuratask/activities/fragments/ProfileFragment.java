package com.neeravtanay.neuratask.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.activities.ChangePasswordActivity;
import com.neeravtanay.neuratask.activities.LoginActivity;
import java.util.Random;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvAge;
    private Button btnLogout, btnChangePassword;
    private ImageView ivProfile;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private final int[] avatars = {R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3};
    private LoginActivity.UserProfile userProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_profile, container, false);

        tvName = v.findViewById(R.id.tvName);
        tvEmail = v.findViewById(R.id.tvEmail);
        tvAge = v.findViewById(R.id.tvAge);
        btnLogout = v.findViewById(R.id.btnLogout);
        btnChangePassword = v.findViewById(R.id.btnChangePassword);
        ivProfile = v.findViewById(R.id.ivProfile);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (getActivity() != null && getActivity().getIntent() != null) {
            userProfile = (LoginActivity.UserProfile) getActivity().getIntent().getSerializableExtra("userProfile");
        }

        if (userProfile != null) {
            loadProfileFromObject();
        } else {
            loadProfileFromFirestore();
        }

        ivProfile.setOnClickListener(v1 -> showAvatarOptions());
        btnLogout.setOnClickListener(v1 -> {
            auth.signOut();
            startActivity(new android.content.Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });
        btnChangePassword.setOnClickListener(v12 ->
                startActivity(new android.content.Intent(getContext(), ChangePasswordActivity.class))
        );

        return v;
    }

    private void loadProfileFromObject() {
        tvEmail.setText(userProfile.email);
        tvName.setText(userProfile.name.isEmpty() ? "No Name" : userProfile.name);
        tvAge.setText(userProfile.age.isEmpty() ? "" : userProfile.age);
        int avatarIndex = userProfile.avatarIndex;
        if (avatarIndex < 0 || avatarIndex >= avatars.length) {
            avatarIndex = new Random().nextInt(avatars.length);
            userProfile.avatarIndex = avatarIndex;
            db.collection("users").document(auth.getCurrentUser().getUid())
                    .update("avatarIndex", avatarIndex);
        }
        setAvatarDrawable(avatarIndex);
    }

    private void loadProfileFromFirestore() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        userProfile = new LoginActivity.UserProfile(doc);
                    } else {
                        int randomIndex = new Random().nextInt(avatars.length);
                        userProfile = new LoginActivity.UserProfile(auth.getCurrentUser().getEmail());
                        userProfile.avatarIndex = randomIndex;
                        db.collection("users").document(uid).set(userProfile);
                    }
                    loadProfileFromObject();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show());
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
}
