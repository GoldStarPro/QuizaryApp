package com.example.quizary.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.quizary.model.User;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User getUserByUsernameAndPassword(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);
}