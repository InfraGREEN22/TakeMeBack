package acr18as.sheffield.ac.uk.takemeback.roomdb;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SavedLocationDAO {

    @Insert
    void insert(SavedLocation location);

    @Update
    void update(SavedLocation location);

    @Delete
    void delete(SavedLocation location);

    @Query("SELECT * FROM saved_location_table ORDER BY id")
    LiveData<List<SavedLocation>> getAllLocations();

    @Query("SELECT * FROM saved_location_table ORDER BY id DESC LIMIT 1")
    LiveData<SavedLocation> getLastSavedLocation();

    @Query("DELETE FROM saved_location_table")
    void deleteAllLocations();
}
