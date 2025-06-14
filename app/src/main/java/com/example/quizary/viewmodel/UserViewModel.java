package com.example.quizary.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.quizary.data.repository.UserRepository;
import com.example.quizary.model.User;

import org.mindrot.jbcrypt.BCrypt;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<User> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public MutableLiveData<User> getLoginResult() {
        return loginResult;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void register(String username, String password, String email) {
        User existingUser = userRepository.getUserByUsername(username);
        if (existingUser != null) {
            errorMessage.setValue("Username already exists");
            return;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(username, hashedPassword, email, "USER");
        userRepository.registerUser(user, () -> errorMessage.postValue("Registration successful"));
    }

    public void login(String username, String password) {
        new Thread(() -> {
            User user = userRepository.getUserByUsername(username);
            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                loginResult.postValue(user);
            } else {
                errorMessage.postValue("Invalid username or password");
            }
        }).start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        userRepository.shutdown();
    }
}