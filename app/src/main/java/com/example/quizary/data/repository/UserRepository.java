package com.example.quizary.data.repository;

import android.content.Context;
import android.util.Log; // <-- Thêm import này

import com.example.quizary.data.dao.UserDao;
import com.example.quizary.data.database.AppDatabase;
import com.example.quizary.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserRepository {
    private static final String TAG = "UserRepository"; // <-- Định nghĩa TAG cho logging
    private final UserDao userDao;
    private final ExecutorService executorService;

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userDao = db.userDao();
        executorService = Executors.newFixedThreadPool(2); // Thread pool for background tasks
    }

    public void registerUser(User user, Runnable onComplete) {
        executorService.execute(() -> {
            try {
                userDao.insert(user);
                if (onComplete != null) {
                    // Post back to main thread if needed
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error registering user: " + user.getUsername(), e);
            }
        });
    }

    public User loginUser(String username, String password) {
        try {
            Future<User> future = executorService.submit(() -> userDao.getUserByUsernameAndPassword(username, password));
            return future.get(); // Wait for result
        } catch (Exception e) {
            Log.e(TAG, "Error logging in user: " + username, e);
            return null;
        }
    }

    public User getUserByUsername(String username) {
        try {
            Future<User> future = executorService.submit(() -> userDao.getUserByUsername(username));
            return future.get(); // Wait for result
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by username: " + username, e);
            return null;
        }
    }

    // Shutdown executor when repository is no longer needed
    public void shutdown() {
        executorService.shutdown();
    }
}