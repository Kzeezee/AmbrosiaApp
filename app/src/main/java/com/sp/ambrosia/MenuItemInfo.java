package com.sp.ambrosia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MenuItemInfo extends AppCompatActivity {

    private EditText itemName, itemCalories;
    private ImageView itemIcon, QRImage;
    private Spinner itemType;
    private String documentID, foodName, foodType, foodCalories, userID;
    private Button updateB, deleteB;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ProgressBar progressBar;
    private boolean Check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuiteminfo);

        //Initializing
        itemName = findViewById(R.id.mealnameINFO);
        itemType = findViewById(R.id.mealtypeINFO);
        itemIcon = findViewById(R.id.mealiconINFO);
        itemCalories = findViewById(R.id.mealcaloriesINFO);
        QRImage = findViewById(R.id.QRImageView);
        progressBar = findViewById(R.id.progressBarInfo);
        progressBar.setVisibility(View.INVISIBLE);
        updateB = findViewById(R.id.updateMenuItemB);
        updateB.setOnClickListener(UpdateItem);
        deleteB = findViewById(R.id.deleteMenuItemB);
        deleteB.setOnClickListener(DeleteItem);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        Check = false;

        //Getting ListView item's information from Intent
        Intent intent = getIntent();
        documentID = intent.getStringExtra("documentID");
        foodName = intent.getStringExtra("name");
        foodType = intent.getStringExtra("type");
        foodCalories = intent.getStringExtra("calories");

        String data = foodName + "|" + foodType + "|" + foodCalories;
        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 250);
        qrgEncoder.setColorBlack(Color.BLACK);
        qrgEncoder.setColorWhite(Color.WHITE);
        Bitmap bitmap = qrgEncoder.getBitmap();
        QRImage.setImageBitmap(bitmap);

        //Input the information into Widgets so that Vendor can also edit nutrition information here
        itemName.setText(foodName);
        itemCalories.setText(foodCalories);
        switch (foodType) {
            case "Main":
                itemIcon.setImageResource(R.drawable.mainicon);
                break;
            case "Sides":
                itemIcon.setImageResource(R.drawable.sidesicon);
                break;
            case "Desserts":
                itemIcon.setImageResource(R.drawable.desserticon);
                break;
            case "Snacks":
                itemIcon.setImageResource(R.drawable.snacksicon);
                break;
            case "Drinks":
                itemIcon.setImageResource(R.drawable.drinksicon);
                break;
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.meal_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemType.setAdapter(adapter);
        itemType.setSelection(adapter.getPosition(foodType));
        itemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = parent.getItemAtPosition(position).toString();

                switch (type) {
                    case "Main":
                        itemIcon.setImageResource(R.drawable.mainicon);
                        foodType = "Main";
                        break;
                    case "Sides":
                        itemIcon.setImageResource(R.drawable.sidesicon);
                        foodType = "Sides";
                        break;
                    case "Desserts":
                        itemIcon.setImageResource(R.drawable.desserticon);
                        foodType = "Desserts";
                        break;
                    case "Snacks":
                        itemIcon.setImageResource(R.drawable.snacksicon);
                        foodType = "Snacks";
                        break;
                    case "Drinks":
                        itemIcon.setImageResource(R.drawable.drinksicon);
                        foodType = "Drinks";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    View.OnClickListener UpdateItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            Map<String, Object> newMealInfo = new HashMap<>();
            newMealInfo.put("name", itemName.getText().toString());
            newMealInfo.put("type", foodType);
            newMealInfo.put("calories", itemCalories.getText().toString());

            fStore.collection("users").document(userID)
                    .collection("menuitems").document(documentID)
                    .set(newMealInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Intent back = new Intent(MenuItemInfo.this, Home.class);
                            startActivity(back);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MenuItemInfo.this, "Error updating document. " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    };

    View.OnClickListener DeleteItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            //Adds a safety feature so user has to tap the button twice to delete a menu item
            if (!Check) {
                Toast.makeText(MenuItemInfo.this, "Are you sure you want to delete this menu item? Tap again to delete", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                Check = true;
            } else {
                Check = false;
                fStore.collection("users").document(userID)
                        .collection("menuitems").document(documentID)
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Intent back = new Intent(MenuItemInfo.this, Home.class);
                        startActivity(back);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MenuItemInfo.this, "Error deleting document. " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    };

    //This makes sure that if user leaves the activity, the activity is killed so there will be no instances of 2 activities at once.
    //This makes sure that the Firestore is not accidentally being inputted several data etc.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
