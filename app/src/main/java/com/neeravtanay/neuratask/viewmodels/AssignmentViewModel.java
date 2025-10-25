package com.neeravtanay.neuratask.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.repository.AssignmentRepository;
import java.util.List;

public class AssignmentViewModel extends AndroidViewModel {

    private final AssignmentRepository repo;

    public AssignmentViewModel(@NonNull Application app) {
        super(app);
        repo = new AssignmentRepository(app);
    }

    public LiveData<List<AssignmentModel>> getPending() {
        return repo.getPending();
    }

    public LiveData<List<AssignmentModel>> getCompleted() {
        return repo.getCompleted();
    }

    public LiveData<List<AssignmentModel>> getOverdue() {
        return repo.getOverdue();
    }

    public void insert(AssignmentModel assignment) {
        repo.insert(assignment);
    }

    public void update(AssignmentModel assignment) {
        repo.update(assignment);
    }

    public void delete(AssignmentModel assignment) {
        repo.delete(assignment);
    }

    public void syncUnsynced() {
        repo.attemptSyncUnsynced();
    }
}
