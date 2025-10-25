package com.neeravtanay.neuratask.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.neeravtanay.neuratask.R;
import com.neeravtanay.neuratask.models.AssignmentModel;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    public interface AssignmentListener {
        void onEditClicked(AssignmentModel assignment);
        void onDeleteClicked(AssignmentModel assignment);
    }

    private List<AssignmentModel> assignmentList;
    private final AssignmentListener listener;

    public AssignmentAdapter(List<AssignmentModel> assignmentList, AssignmentListener listener) {
        this.assignmentList = assignmentList;
        this.listener = listener;
    }

    public void setAssignments(List<AssignmentModel> assignments) {
        this.assignmentList = assignments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssignmentModel assignment = assignmentList.get(position);
        holder.titleText.setText(assignment.getTitle());
        holder.subjectText.setText(assignment.getSubject());
        holder.dueText.setText("Due: " + assignment.getDueTimestamp());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClicked(assignment));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClicked(assignment));
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, subjectText, dueText;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tvTitle);
            subjectText = itemView.findViewById(R.id.tvSubject);
            dueText = itemView.findViewById(R.id.tvDue);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
