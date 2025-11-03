package com.neeravtanay.neuratask.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.models.NotificationModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {AssignmentModel.class, NotificationModel.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Singleton instance
    private static volatile AppDatabase instance;

    // DAOs
    public abstract AssignmentDao assignmentDao();
    public abstract NotificationDao notificationDao();

    // Executor for background (async) DB operations
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Singleton getter with synchronized double-checked locking
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "neuratask_db"
                            )
                            .fallbackToDestructiveMigration() // ✅ handles schema version upgrades safely
                            .allowMainThreadQueries() // Optional — can remove if you want strict background ops
                            .build();
                }
            }
        }
        return instance;
    }
}
