package com.neeravtanay.neuratask.database;

import android.content.Context;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.neeravtanay.neuratask.models.AssignmentModel;

@androidx.room.Database(entities = {AssignmentModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    public abstract com.neeravtanay.neuratask.dao.AssignmentDao assignmentDao();
    public static synchronized AppDatabase getInstance(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(), AppDatabase.class, "neuratask_db").build();
        }
        return INSTANCE;
    }
}
