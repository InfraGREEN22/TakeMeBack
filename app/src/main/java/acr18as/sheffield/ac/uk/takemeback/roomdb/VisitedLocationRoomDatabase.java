package acr18as.sheffield.ac.uk.takemeback.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = VisitedLocation.class, version = 2, exportSchema = false)
public abstract class VisitedLocationRoomDatabase extends RoomDatabase {

    private static VisitedLocationRoomDatabase instance;

    public static synchronized VisitedLocationRoomDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    VisitedLocationRoomDatabase.class, "visited_location_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract VisitedLocationDAO locationDAO();
}
