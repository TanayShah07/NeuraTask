package com.neeravtanay.neuratask.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.adapters.AssignmentAdapter;
import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.utils.EditTaskDialog;
import com.neeravtanay.neuratask.viewmodels.AssignmentViewModel;
import java.util.ArrayList;

public class PendingFragment extends Fragment {

    private RecyclerView rv;
    private AssignmentAdapter adapter;
    private AssignmentViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pending, container, false);

        rv = v.findViewById(R.id.rvPending);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(AssignmentViewModel.class);

        adapter = new AssignmentAdapter(new ArrayList<>(), new AssignmentAdapter.AssignmentListener() {
            @Override
            public void onEditClicked(AssignmentModel assignment) {
                new EditTaskDialog(requireContext(), assignment, viewModel).show();
            }

            @Override
            public void onDeleteClicked(AssignmentModel assignment) {
                viewModel.delete(assignment);
            }
        });

        rv.setAdapter(adapter);

        viewModel.getPending().observe(getViewLifecycleOwner(), list -> {
            adapter.setAssignments(list);
            View emptyView = v.findViewById(R.id.tvEmpty);
            if (emptyView != null) {
                emptyView.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        return v;
    }
}
