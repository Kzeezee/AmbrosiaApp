package com.sp.ambrosia;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class RestaurantSetup extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    private TextView header;
    private EditText restaurantName;
    private Button restaurantSetupB;

    private String userID;
    private String fName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurantsetup);

        header = findViewById(R.id.restaurantsetupheader);
        restaurantName = findViewById(R.id.restaurantsetupname);
        restaurantSetupB = findViewById(R.id.restaurantsetupb);
        restaurantSetupB.setOnClickListener(SetupDone);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()) {
                    header.setText("Welcome, " + documentSnapshot.getString("fName") + "!");
                } else {
                    Log.d("tag", "onEvent: Document do not exist");
                }
            }
        });
    }

    View.OnClickListener SetupDone = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (restaurantName.getText().toString() == null || restaurantName.getText().toString() == "") {
                Toast.makeText(RestaurantSetup.this, "Restaurant name cannot be empty!", Toast.LENGTH_SHORT);
            } else {
                DocumentReference documentReference = fStore.collection("users").document(userID);

                //Storing user data into the document itself
                Map<String, Object> user = new HashMap<>();
                user.put("restaurantname", restaurantName.getText().toString());
                documentReference.update(user);

                Intent redirect = new Intent(RestaurantSetup.this, Home.class);
                startActivity(redirect);
                finish();
            }
        }
    };
}
