package com.example.quizary.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.quizary.data.dao.UserDao;
import com.example.quizary.data.database.AppDatabase;
import com.example.quizary.model.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executorService;
    private static final String TAG = "UserRepository";

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userDao = db.userDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public void registerUser(User user, Runnable onComplete) {
        executorService.execute(() -> {
            try {
                userDao.insert(user);
                Log.d(TAG, "User inserted: " + user.getUsername() + ", Role: " + user.getRole());
                if (onComplete != null) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error inserting user", e);
            }
        });
    }

    public User getUserByUsername(String username) {
        try {
            Future<User> future = executorService.submit(() -> userDao.getUserByUsername(username));
            User user = future.get();
            Log.d(TAG, "Username query result: " + (user != null ? user.getUsername() + ", Role: " + user.getRole() : "null"));
            return user;
        } catch (Exception e) {
            Log.e(TAG, "Error checking username", e);
            return null;
        }
    }

    public List<User> getAllUsers() {
        try {
            Future<List<User>> future = executorService.submit(() -> userDao.getAllUsers());
            List<User> users = future.get();
            Log.d(TAG, "All users: " + users.size());
            return users;
        } catch (Exception e) {
            Log.e(TAG, "Error fetching all users", e);
            return null;
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}