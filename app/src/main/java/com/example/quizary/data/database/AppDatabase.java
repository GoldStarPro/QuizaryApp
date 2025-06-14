package com.example.quizary.data.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.quizary.data.dao.UserDao;
import com.example.quizary.model.User;

import org.mindrot.jbcrypt.BCrypt;

import java.util.concurrent.Executors;

@Database(entities = {User.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;
    private static final String TAG = "AppDatabase";

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    Log.d(TAG, "Creating new database instance");
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "quizary_database")
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        UserDao dao = INSTANCE.userDao();
                                        String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
                                        User admin = new User("admin", hashedPassword, "admin@quizary.com", "ADMIN");
                                        dao.insert(admin);
                                        Log.d(TAG, "Default admin account created");
                                    });
                                }
                            })
                            .build();
                    Log.d(TAG, "Database instance created");
                }
            }
        }
        return INSTANCE;
    }
}