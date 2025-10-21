package com.neeravtanay.neuratask.activities.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.neeravtanay.neuratask.R;

public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle s) {
        View v = inflater.inflate(R.layout.activity_profile, container, false);
        TextView tvName = v.findViewById(R.id.tvName);
        TextView tvEmail = v.findViewById(R.id.tvEmail);
        Button btnLogout = v.findViewById(R.id.btnLogout);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            tvEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), com.neeravtanay.neuratask.activities.LoginActivity.class));
            requireActivity().finish();
        });
        return v;
    }
}
