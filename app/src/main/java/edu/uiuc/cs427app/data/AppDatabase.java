package edu.uiuc.cs427app.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import edu.uiuc.cs427app.data.dao.LocationDao;
import edu.uiuc.cs427app.data.dao.UserDao;
import edu.uiuc.cs427app.data.models.Location;
import edu.uiuc.cs427app.data.models.User;

@Database(entities = {
        User.class,
        Location.class
}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Returns the user dao
     * @return the user dao
     */
    public abstract UserDao userDao();

    /**
     * Returns the locations dao
     * @return the locations dao
     */
    public abstract LocationDao locationsDao();
}
