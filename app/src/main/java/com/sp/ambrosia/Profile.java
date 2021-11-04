package com.sp.ambrosia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userID, lifestyle;
    private TextView fName, lName, email;
    private RadioGroup lifestyleType;
    private RadioButton athletic, active, normal;
    private Button updateB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        fName = findViewById(R.id.profilefname);
        lName = findViewById(R.id.profilelname);
        email = findViewById(R.id.profileemail);
        lifestyleType = findViewById(R.id.lifestyleType);
        updateB = findViewById(R.id.profileUpdateB);
        updateB.setOnClickListener(updateProfile);
        athletic = findViewById(R.id.radioathletic);
        active = findViewById(R.id.radioactive);
        normal = findViewById(R.id.radionormal);

        email.setText(fAuth.getCurrentUser().getEmail());
        fStore.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        fName.setText(document.getString("fName"));
                        lName.setText(document.getString("lName"));
                        lifestyle = document.getString("lifestyle");

                        switch (lifestyle) {
                            case "athletic":
                                lifestyleType.check(R.id.radioathletic);
                                break;
                            case "active":
                                lifestyleType.check(R.id.radioactive);
                                break;
                            case "normal":
                                lifestyleType.check(R.id.radionormal);
                                break;
                        }
                    }
                }
            }
        });
    }

    private View.OnClickListener updateProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (lifestyleType.getCheckedRadioButtonId()) {
                case R.id.radioathletic:
                    lifestyle = "athletic";
                    athletic.setChecked(true);
                    break;
                case R.id.radioactive:
                    lifestyle = "active";
                    active.setChecked(true);
                    break;
                case R.id.radionormal:
                    lifestyle = "normal";
                    normal.setChecked(true);
                    break;
            }
            fStore.collection("users").document(userID).update("lifestyle", lifestyle).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(Profile.this, "Lifestyle successfully updated!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
