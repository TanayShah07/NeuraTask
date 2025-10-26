package com.neeravtanay.neuratask.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.neeravtanay.neuratask.models.AssignmentModel;
import com.neeravtanay.neuratask.models.NotificationModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {AssignmentModel.class, NotificationModel.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // DAOs
    public abstract AssignmentDao assignmentDao();
    public abstract NotificationDao notificationDao();

    // Thread pool for async DB operations
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    // Singleton pattern for DB instance
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "neuratask_db"
                            )
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries() // Safe for small sync operations
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
