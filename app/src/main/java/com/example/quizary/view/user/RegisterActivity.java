package com.example.quizary.view.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.quizary.R;
import com.example.quizary.viewmodel.UserViewModel;

public class RegisterActivity extends AppCompatActivity {
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        EditText usernameInput = findViewById(R.id.username_input);
        EditText emailInput = findViewById(R.id.email_input);
        EditText passwordInput = findViewById(R.id.password_input);
        EditText confirmPasswordInput = findViewById(R.id.confirm_password_input);
        CheckBox termsCheckbox = findViewById(R.id.terms_checkbox);
        Button registerButton = findViewById(R.id.register_button);
        TextView loginLink = findViewById(R.id.login_link);

        registerButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            boolean agreed = termsCheckbox.isChecked();

            // Kiểm tra nhập đủ trường
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra mật khẩu khớp
            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra điều khoản
            if (!agreed) {
                Toast.makeText(RegisterActivity.this, "You must agree to the Terms and Privacy Policy", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi hàm đăng ký
            userViewModel.register(username, password, email);
        });

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        userViewModel.getErrorMessage().observe(this, message -> {
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            if (message.equals("Registration successful")) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
