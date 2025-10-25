package com.neeravtanay.neuratask.activities.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.activities.ChangePasswordActivity;
import com.neeravtanay.neuratask.activities.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvAge;
    private Button btnLogout, btnChangePassword;
    private ImageView ivProfile;

    private FirebaseUser user;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle s) {
        View v = inflater.inflate(R.layout.activity_profile, container, false);

        tvName = v.findViewById(R.id.tvName);
        tvEmail = v.findViewById(R.id.tvEmail);
        tvAge = v.findViewById(R.id.tvAge);
        btnLogout = v.findViewById(R.id.btnLogout);
        btnChangePassword = v.findViewById(R.id.btnChangePassword);
        ivProfile = v.findViewById(R.id.ivProfile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Default blank profile icon
        ivProfile.setImageResource(R.drawable.ic_profile_blank);

        // Load existing profile image if exists
        if (user != null) {
            tvEmail.setText(user.getEmail());
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String age = documentSnapshot.getString("age");
                            String profileUrl = documentSnapshot.getString("profileUrl");

                            tvName.setText(name != null ? name : "No Name");
                            tvAge.setText(age != null ? age : "");

                            if (profileUrl != null && !profileUrl.isEmpty()) {
                                if (profileUrl.startsWith("http") || profileUrl.startsWith("gs://")) {
                                    Glide.with(requireContext()).load(profileUrl).circleCrop().into(ivProfile);
                                } else if (profileUrl.startsWith("android.resource://")) {
                                    int resId = getResources().getIdentifier(
                                            profileUrl.substring(profileUrl.lastIndexOf("/") + 1),
                                            "drawable",
                                            requireContext().getPackageName()
                                    );
                                    ivProfile.setImageResource(resId);
                                }
                            }
                        }
                    });
        }

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri selectedImage = result.getData().getData();
                            if (selectedImage != null) uploadProfileImage(selectedImage);
                        }
                    }
                });

        // Profile click -> choose option
        ivProfile.setOnClickListener(view -> showProfileOptions());

        // Logout
        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });

        // Change password
        btnChangePassword.setOnClickListener(view ->
                startActivity(new Intent(getContext(), ChangePasswordActivity.class))
        );

        return v;
    }

    private void showProfileOptions() {
        String[] options = {"Select from Gallery", "Set Random Avatar"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Set Profile Picture")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryLauncher.launch(intent);
                    } else {
                        setRandomAvatar();
                    }
                }).show();
    }

    private void uploadProfileImage(Uri uri) {
        if (user == null) return;

        StorageReference ref = storageRef.child("profiles/" + user.getUid() + ".jpg");
        ref.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    Glide.with(requireContext()).load(downloadUri).circleCrop().into(ivProfile);
                    db.collection("users").document(user.getUid())
                            .update("profileUrl", downloadUri.toString());
                    Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setRandomAvatar() {
        if (user == null) return;
        int[] avatars = {R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3};
        int randomIndex = new Random().nextInt(avatars.length);
        int avatarRes = avatars[randomIndex];
        ivProfile.setImageResource(avatarRes);

        // Convert drawable to bitmap
        Drawable drawable = getResources().getDrawable(avatarRes);
        Bitmap bitmap = drawableToBitmap(drawable);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = storageRef.child("profiles/" + user.getUid() + ".jpg");
        ref.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    db.collection("users").document(user.getUid())
                            .update("profileUrl", "android.resource://" + requireContext().getPackageName() + "/" + avatarRes);
                    Toast.makeText(getContext(), "Random avatar set!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Helper: convert drawable to bitmap
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
