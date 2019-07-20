package acr18as.sheffield.ac.uk.takemeback.roomdb;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface VisitedLocationDAO {

    @Insert
    void insert(VisitedLocation location);

    @Update
    void update(VisitedLocation location);

    @Delete
    void delete(VisitedLocation location);

    @Query("SELECT * FROM visited_location_table ORDER BY id")
    LiveData<List<VisitedLocation>> getAllLocations();

    @Query("DELETE FROM visited_location_table")
    void deleteAllLocations();

}
