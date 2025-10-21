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
import com.neeravtanay.neuratask.viewmodel.AssignmentViewModel;
import java.util.ArrayList;

public class PendingFragment extends Fragment {
    RecyclerView rv;
    AssignmentAdapter adapter;
    AssignmentViewModel vm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle s) {
        View v = inflater.inflate(R.layout.fragment_pending, container, false);
        rv = v.findViewById(R.id.rvPending);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AssignmentAdapter(new ArrayList<>());
        rv.setAdapter(adapter);
        vm = new ViewModelProvider(requireActivity()).get(AssignmentViewModel.class);
        vm.getPending().observe(getViewLifecycleOwner(), list -> {
            adapter.setList(list);
            v.findViewById(R.id.tvEmpty).setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
        });
        return v;
    }
}
