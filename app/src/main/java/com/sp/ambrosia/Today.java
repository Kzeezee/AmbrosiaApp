package com.sp.ambrosia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Today extends AppCompatActivity {

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private String userID = fAuth.getCurrentUser().getUid();
    private CollectionReference todayRef = fStore.collection("users").document(userID).collection("today");

    private TextView improvements;
    private Button refreshB;

    private FoodItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today);

        setUpRecyclerView();

        improvements = findViewById(R.id.improvements);
        improvements.setText("");
        refreshB = findViewById(R.id.refreshButton);
        refreshB.setOnClickListener(refresh);
        recommendations();
    }

    private void setUpRecyclerView() {
        Query query = todayRef.orderBy("type", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<FoodItem> options = new FirestoreRecyclerOptions.Builder<FoodItem>()
                .setQuery(query, FoodItem.class)
                .build();

        adapter = new FoodItemAdapter(options);

        RecyclerView rV = findViewById(R.id.recycler_view_main);
        rV.setHasFixedSize(true);
        rV.setLayoutManager(new LinearLayoutManager(this));
        rV.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(rV);
    }

    private View.OnClickListener refresh = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            recommendations();
        }
    };

    private void recommendations() {
        //Determining suggestions with a base text of telling the no. of calories consumed today
        String recommendations = "";
        boolean hasRecommendations = false;
        recommendations += "You are consuming " + adapter.getTotalCalories() + " calories.\n";
        if (adapter.getNoSnacks() > 2) {
            recommendations += "If you are hungry, try and eat earlier meals rather than fitting more snacks in between.\n";
            hasRecommendations = true;
        }
        if (adapter.getNoDesserts() > 1) {
            recommendations += "Try and limit yourself to 1 Dessert per day.\n";
            hasRecommendations = true;
        }
        if (adapter.getNoDrinks() > 1) {
            recommendations += "If your beverages are sugared drinks, try and limit to at max 1 sugared drinks per day.\n";
            hasRecommendations = true;
        }
        if (adapter.getNoMains() > 4) {
            recommendations += "Too many main courses easily leads to high calories intake. Do consider how much you are consuming.\n";
            hasRecommendations = true;
        }
        if (adapter.getTotalCalories() > 2500) {
            recommendations += "You are consuming above the recommended active adult calories intake. Do cut down on it if you are not actively exercising.\n";
            hasRecommendations = true;
        }
        if (hasRecommendations) {
            recommendations += "You are not eating too many of each type. Keep up the good work!\n";
        }
        improvements.setText(recommendations);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
