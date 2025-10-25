package com.neeravtanay.neuratask.database;

import android.content.Context;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.neeravtanay.neuratask.models.AssignmentModel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {AssignmentModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    // DAO
    public abstract AssignmentDao assignmentDao();

    // Thread pool for database operations
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    // Singleton instance
    public static synchronized AppDatabase getInstance(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                            AppDatabase.class, "neuratask_db")
                    .build();
        }
        return INSTANCE;
    }
}
