package com.nayphilim.showcaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView registerLink, errorText;
    private EditText emailText, passwordText;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        errorText = findViewById(R.id.errorText);
        registerLink = findViewById(R.id.register);
        registerLink.setOnClickListener(this);

        emailText = findViewById(R.id.emailSignIn);
        passwordText = findViewById(R.id.passwordSignIn);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        progressBar = findViewById(R.id.loginProgressBar);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(SignInActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                startRegisterActivity();
                break;
            case R.id.loginButton:
                loginUser();
                break;
        }

    }

    private void loginUser() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if(email.isEmpty()){
            emailText.setError("Email is required");
            emailText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Please enter a valid email address");
            emailText.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordText.setError("Password is required");
            passwordText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()) {
                        startProfileActivity();
                    }else{
                        user.sendEmailVerification();
                        errorText.setText("Please verify your account via email before logging in.");
                        errorText.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                }
                else{
                    errorText.setText("Email or password incorrect. Please try again.");
                    errorText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                }
            }
        });

    }

    private void startRegisterActivity() {
        Intent intent = new Intent(SignInActivity.this,RegisterActivity.class );
        startActivity(intent);
    }

    private void startProfileActivity() {
        Intent intent = new Intent(SignInActivity.this,MainActivity.class );
        startActivity(intent);
    }
}