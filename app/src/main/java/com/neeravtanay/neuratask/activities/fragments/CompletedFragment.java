package com.neeravtanay.neuratask.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.adapters.AssignmentAdapter;
import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.viewmodels.AssignmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class CompletedFragment extends Fragment implements AssignmentAdapter.AssignmentListener {

    private RecyclerView recyclerView;
    private TextView tvEmptyMessage;
    private AssignmentAdapter adapter;
    private AssignmentViewModel viewModel;
    private List<AssignmentModel> completedList = new ArrayList<>();

    public CompletedFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCompleted);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyCompleted);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new AssignmentAdapter(completedList, this);
        recyclerView.setAdapter(adapter);

        // ViewModel initialization
        viewModel = new ViewModelProvider(requireActivity()).get(AssignmentViewModel.class);

        // ✅ Observe completed assignments only
        viewModel.getCompleted().observe(getViewLifecycleOwner(), assignments -> {
            completedList.clear();
            if (assignments != null) {
                completedList.addAll(assignments);
            }

            adapter.notifyDataSetChanged();
            toggleEmptyMessage();
        });

        return view;
    }

    private void toggleEmptyMessage() {
        if (completedList.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditClicked(AssignmentModel assignment) {
        // Completed tasks usually aren’t editable, but you can allow editing if you wish
    }

    @Override
    public void onDeleteClicked(AssignmentModel assignment) {
        viewModel.delete(assignment);
    }
}
