package edu.uiuc.cs427app.di;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import org.chromium.net.CronetEngine;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;

import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import edu.uiuc.cs427app.data.AppDatabase;
import edu.uiuc.cs427app.data.dao.LocationDao;
import edu.uiuc.cs427app.data.dao.UserDao;
import edu.uiuc.cs427app.data.repository.LocationsRepository;
import edu.uiuc.cs427app.data.repository.UserRepository;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    /**
     * Provide Contexts
     * @param application application.
     * @return Context of application
     */
    @Provides
    public Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    /**
     * Provide Database
     * @param context context.
     * @return AppDatabase from context.
     */
    @Provides
    public AppDatabase provideDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    /**
     * Provide LocationDao
     * @param appDatabase appDatabase.
     * @return LocationDao.
     */
    @Provides
    public LocationDao provideLocationDao(AppDatabase appDatabase) {
        return appDatabase.locationsDao();
    }

    /**
     * Provide UserDao
     * @param appDatabase appDatabase.
     * @return UserDao.
     */
    @Provides
    public UserDao provideUserDao(AppDatabase appDatabase) {
        return appDatabase.userDao();
    }

    /**
     * Provide LocationsRepository
     * @param locationDao LocationDao.
     * @return LocationsRepository.
     */
    @Provides
    public LocationsRepository provideLocationsRepository(LocationDao locationDao) {
        return new LocationsRepository(locationDao);
    }

    /**
     * Provide UserRepository
     * @param userDao userDao.
     * @param context context.
     * @return UserRepository.
     */
    @Provides
    public UserRepository provideUserRepository(UserDao userDao, Context context) {
        return new UserRepository(userDao, context);
    }

    /**
     * Provide Cronet Engine for network operations
     * @param context ApplicationContext
     * @return CronetEngine
     */
    @Provides
    @Singleton
    public CronetEngine provideCronetEngine(@ApplicationContext Context context) {
        return new CronetEngine.Builder(context).build();
    }

}
