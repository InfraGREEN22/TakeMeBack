package acr18as.sheffield.ac.uk.takemeback.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = SavedLocation.class, version = 1, exportSchema = false)
public abstract class SavedLocationRoomDatabase extends RoomDatabase {
    private static SavedLocationRoomDatabase instance;

    public static synchronized SavedLocationRoomDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    SavedLocationRoomDatabase.class, "saved_location_table")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract SavedLocationDAO savedLocationDAO();
}
