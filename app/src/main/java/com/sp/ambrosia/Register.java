package com.sp.ambrosia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText firstName, lastName, emailR, passwordR, repasswordR;
    private Button registerButton;
    private ProgressBar ProgressbarR;
    FirebaseAuth fAuth;
    private CheckBox vendorchecked;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        firstName = findViewById(R.id.firstname);
        lastName = findViewById(R.id.lastname);
        emailR = findViewById(R.id.emailR);
        passwordR = findViewById(R.id.passwordR);
        repasswordR = findViewById(R.id.repasswordR);
        registerButton = findViewById(R.id.registerB);
        vendorchecked = findViewById(R.id.foodVendor);
        ProgressbarR = findViewById(R.id.ProgressbarR);
        ProgressbarR.setVisibility(View.INVISIBLE);

        //Firebase instantiate
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailR.getText().toString().trim();
                String password = passwordR.getText().toString();
                final String fName = firstName.getText().toString().trim();
                final String lName = lastName.getText().toString().trim();
                String rpassword = repasswordR.getText().toString();
                final Boolean vendor;

                if (TextUtils.isEmpty(fName)) {
                    firstName.setError("Name is empty!");
                    return;
                }
                if (TextUtils.isEmpty(lName)) {
                    lastName.setError("Name is empty!");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailR.setError("Email is empty!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordR.setError("Password is empty!");
                    return;
                }
                if (password.length() < 6) {
                    passwordR.setError("Password must be more than 6 characters!");
                    return;
                }
                if (!rpassword.equals(password)) {
                    repasswordR.setError("Password does not match!");
                    return;
                }

                if (vendorchecked.isChecked()) {
                    vendor = true;
                } else {
                    vendor = false;
                }
                ProgressbarR.setVisibility(View.VISIBLE);

                //Registering with Firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Getting userID and creating a collection that stores all the user's information
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);

                            //Storing user data into the document itself
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName", fName);
                            user.put("lName", lName);
                            user.put("vendor", vendor);
                            if (vendor) {
                                user.put("restaurantname", "");
                            }
                            if (!vendor) {
                                user.put("lifestyle", "normal");
                            }
                            documentReference.set(user);

                            Intent main = new Intent(Register.this, Home.class);
                            startActivity(main);
                            ProgressbarR.setVisibility(View.INVISIBLE);
                            finish();

                        } else {
                            Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            ProgressbarR.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    public void loginredirect(View view) {
        Intent redirect = new Intent(Register.this, Login.class);
        startActivity(redirect);
        finish();
    }

    public void foodvendorquery(View view) {
        Toast.makeText(Register.this, "Check this option if you are a Food Vendor looking to set up and use Ambrosia in your business" + "\n\n" +
                "This disables normal user features. Please sign up with a personal non-vendor account to use normal Ambrosia features.", Toast.LENGTH_LONG).show();
    }
}
