package com.sp.ambrosia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private Button loginB;
    private EditText emailL, passwordL;
    private ProgressBar ProgressbarL;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loginB = findViewById(R.id.loginB);
        emailL = findViewById(R.id.emailL);
        passwordL = findViewById(R.id.passwordL);
        ProgressbarL = findViewById(R.id.ProgressbarL);
        fAuth = FirebaseAuth.getInstance();
        ProgressbarL.setVisibility(View.INVISIBLE);

        if (fAuth.getCurrentUser() != null) {
            Intent main = new Intent(Login.this, Home.class);
            startActivity(main);
            finish();
        }

        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailL.getText().toString().trim();
                String password = passwordL.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    emailL.setError("Empty!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordL.setError("Empty!");
                }
                ProgressbarL.setVisibility(View.VISIBLE);

                //Authenticate the User
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), Home.class));
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            ProgressbarL.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    public void registerdirect(View view) {
        Intent registerI = new Intent(Login.this, Register.class);
        startActivity(registerI);
        finish();
    }
}
