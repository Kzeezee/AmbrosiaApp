package com.sp.ambrosia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.zxing.Result;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddUserFoodItem extends AppCompatActivity {

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference favouriteRef;
    private FavouriteAdapter favouriteAdapter;
    private String userID;
    private TabHost host;
    private EditText foodName, foodCalories;
    private ImageView foodIcon;
    private Spinner foodType;
    private Button addB, favouriteB, clearB;
    private CodeScanner mCodeScanner;
    private ProgressBar progressBar;
    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adduserfooditem);
        setTitle("Add Item");

        //Initialize
        foodName = findViewById(R.id.userFoodName);
        foodCalories = findViewById(R.id.userFoodCalories);
        foodType = findViewById(R.id.userFoodType);
        foodIcon = findViewById(R.id.userFoodIcon);
        progressBar = findViewById(R.id.userProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        addB = findViewById(R.id.userFoodAddB);
        addB.setOnClickListener(addFood);
        favouriteB = findViewById(R.id.userFavouriteB);
        favouriteB.setOnClickListener(favouriteFood);
        clearB = findViewById(R.id.clearFieldB);
        clearB.setOnClickListener(clearFields);
        userID = fAuth.getCurrentUser().getUid();
        favouriteRef = fStore.collection("users").document(userID).collection("favourites");
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);

        //Spinners related
        adapter = ArrayAdapter.createFromResource(this, R.array.meal_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodType.setAdapter(adapter);
        foodType.setOnItemSelectedListener(SpinnerListener);

        //Tabs
        host = findViewById(R.id.tabHost);
        host.setup();
        //Tab1
        TabHost.TabSpec spec = host.newTabSpec("Scan");
        spec.setContent(R.id.Scan_Tab);
        spec.setIndicator("Scan");
        host.addTab(spec);
        //Tab2
        spec = host.newTabSpec("Manual");
        spec.setContent(R.id.Manual_Tab);
        spec.setIndicator("Manual Add");
        host.addTab(spec);
        //Tab3
        spec = host.newTabSpec("Quick Add");
        spec.setContent(R.id.Quick_Add);
        spec.setIndicator("Favourites");
        host.addTab(spec);
        host.setCurrentTab(0);
        host.setOnTabChangedListener(OnTabChanged);
        //Also enables Scanner immediately since its on Tab 0 aka Scan Tab
        mCodeScanner.startPreview();

        //Scanners
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                AddUserFoodItem.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String Result = result.toString();
                        String name, type, calories;
                        if (Result.contains("|")) {
                            //Acquire information about the QRCode
                            String[] parts = Result.split("\\|");
                            name = parts[0];
                            type = parts[1];
                            calories = parts[2];

                            //Go to Manual Add Tab and auto fill in the information
                            foodName.setText(name);
                            foodCalories.setText(calories);
                            switch (type) {
                                case "Main":
                                    foodType.setSelection(adapter.getPosition("Main"));
                                    foodIcon.setImageResource(R.drawable.mainicon);
                                    break;
                                case "Sides":
                                    foodType.setSelection(adapter.getPosition("Sides"));
                                    foodIcon.setImageResource(R.drawable.sidesicon);
                                    break;
                                case "Desserts":
                                    foodType.setSelection(adapter.getPosition("Desserts"));
                                    foodIcon.setImageResource(R.drawable.desserticon);
                                    break;
                                case "Snacks":
                                    foodType.setSelection(adapter.getPosition("Snacks"));
                                    foodIcon.setImageResource(R.drawable.snacksicon);
                                    break;
                                case "Drinks":
                                    foodType.setSelection(adapter.getPosition("Drinks"));
                                    foodIcon.setImageResource(R.drawable.drinksicon);
                                    break;
                            }
                            host.setCurrentTab(1);
                        } else {
                            Toast.makeText(AddUserFoodItem.this, "This is not a Ambrosia QR Code", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = favouriteRef.orderBy("type", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<FoodItem> options = new FirestoreRecyclerOptions.Builder<FoodItem>()
                .setQuery(query, FoodItem.class)
                .build();

        favouriteAdapter = new FavouriteAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_favourite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(favouriteAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                favouriteAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        //RecyclerView onItemClick listener to auto input the favoured item information to add
        favouriteAdapter.setOnItemClickListener(new FavouriteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                FoodItem foodItem = documentSnapshot.toObject(FoodItem.class);
                foodName.setText(foodItem.getName());
                foodCalories.setText(foodItem.getCalories());
                switch (foodItem.getType()) {
                    case "Main":
                        foodType.setSelection(adapter.getPosition("Main"));
                        foodIcon.setImageResource(R.drawable.mainicon);
                        break;
                    case "Sides":
                        foodType.setSelection(adapter.getPosition("Sides"));
                        foodIcon.setImageResource(R.drawable.sidesicon);
                        break;
                    case "Desserts":
                        foodType.setSelection(adapter.getPosition("Desserts"));
                        foodIcon.setImageResource(R.drawable.desserticon);
                        break;
                    case "Snacks":
                        foodType.setSelection(adapter.getPosition("Snacks"));
                        foodIcon.setImageResource(R.drawable.snacksicon);
                        break;
                    case "Drinks":
                        foodType.setSelection(adapter.getPosition("Drinks"));
                        foodIcon.setImageResource(R.drawable.drinksicon);
                        break;
                }
                host.setCurrentTab(1);
            }
        });
    }

    private View.OnClickListener clearFields = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            foodName.setText("");
            foodCalories.setText("");
            foodType.setSelection(adapter.getPosition("Main"));
            foodIcon.setImageResource(R.drawable.mainicon);
        }
    };

    private View.OnClickListener favouriteFood = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            String name, type, calories;
            name = foodName.getText().toString().trim();
            calories = foodCalories.getText().toString().trim();
            if (name.isEmpty() || calories.isEmpty()) {
                Toast.makeText(AddUserFoodItem.this, "Food's name and calories cannot be blank!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
            } else {
                switch (foodType.getSelectedItem().toString()) {
                    case "Main":
                        type = "Main";
                        break;
                    case "Sides":
                        type = "Sides";
                        break;
                    case "Desserts":
                        type = "Desserts";
                        break;
                    case "Snacks":
                        type = "Snacks";
                        break;
                    case "Drinks":
                        type = "Drinks";
                        break;
                    default:
                        type = "Main";
                        break;
                }
                //Add it to FireStore
                Map<String, Object> data = new HashMap<>();
                data.put("name", name);
                data.put("type", type);
                data.put("calories", calories);
                fStore.collection("users").document(userID).collection("favourites").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddUserFoodItem.this, "Item favourited!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    };

    private View.OnClickListener addFood = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            String name, type, calories;
            name = foodName.getText().toString().trim();
            calories = foodCalories.getText().toString().trim();
            if (name.isEmpty() || calories.isEmpty()) {
                Toast.makeText(AddUserFoodItem.this, "Food's name and calories cannot be blank!", Toast.LENGTH_SHORT).show();
            } else {
                switch (foodType.getSelectedItem().toString()) {
                    case "Main":
                        type = "Main";
                        break;
                    case "Sides":
                        type = "Sides";
                        break;
                    case "Desserts":
                        type = "Desserts";
                        break;
                    case "Snacks":
                        type = "Snacks";
                        break;
                    case "Drinks":
                        type = "Drinks";
                        break;
                    default:
                        type = "Main";
                        break;
                }
                //Add it to FireStore
                Map<String, Object> data = new HashMap<>();
                data.put("name", name);
                data.put("type", type);
                data.put("calories", calories);
                //This adds a timestamp field for the food the user added today
                //This allows the main activity to delete the records for today when a new day has passed.
                data.put("timestamp", FieldValue.serverTimestamp());
                fStore.collection("users").document(userID).collection("today").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddUserFoodItem.this, "Item successfully added!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    };

    private TabHost.OnTabChangeListener OnTabChanged = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            switch (host.getCurrentTab()) {
                case 0:
                    mCodeScanner.startPreview();
                    break;

                case 1:

                case 2:
                    mCodeScanner.stopPreview();
                    break;

                default:
                    break;
            }
        }
    };

    private AdapterView.OnItemSelectedListener SpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String type = parent.getItemAtPosition(position).toString();

            switch (type) {
                case "Main":
                    foodIcon.setImageResource(R.drawable.mainicon);
                    break;
                case "Sides":
                    foodIcon.setImageResource(R.drawable.sidesicon);
                    break;
                case "Desserts":
                    foodIcon.setImageResource(R.drawable.desserticon);
                    break;
                case "Snacks":
                    foodIcon.setImageResource(R.drawable.snacksicon);
                    break;
                case "Drinks":
                    foodIcon.setImageResource(R.drawable.drinksicon);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        favouriteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        favouriteAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCodeScanner.releaseResources();
    }

}