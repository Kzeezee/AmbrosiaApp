package com.sp.ambrosia;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class HomeFragment extends Fragment {

    private ViewPager viewPager;
    private FloatingActionButton addButton;
    private onFloatingButtonSelected listener;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private TextView headerName, caloriesIntake, caloriesRecommended;
    private ProgressBar caloriesBar;
    private int progr = 0;
    private int recommended = 2500;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Initialize
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        caloriesIntake = view.findViewById(R.id.caloriesIntake);
        caloriesIntake.setText("0 ");
        caloriesRecommended = view.findViewById(R.id.caloriesRecommended);
        caloriesRecommended.setText("/ 2500");
        caloriesBar = view.findViewById(R.id.caloriesBar);
        viewPager = view.findViewById(R.id.slideView);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getActivity());
        viewPager.setAdapter(viewPagerAdapter);
        addButton = view.findViewById(R.id.quickAddB);
        headerName = view.findViewById(R.id.homeName);
        headerName.setText("");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonSelected();
            }
        });

        fStore.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                headerName.setText(documentSnapshot.getString("fName"));
                switch (documentSnapshot.getString("lifestyle")) {
                    case "normal":
                        caloriesRecommended.setText("/ 2500");
                        recommended = 2500;
                        break;
                    case "active":
                        caloriesRecommended.setText("/ 2750");
                        recommended = 2750;
                        break;
                    case "athletic":
                        caloriesRecommended.setText("/ 3000");
                        recommended = 3000;
                        break;
                }
            }
        });

        refresh();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onFloatingButtonSelected) {
            listener = (onFloatingButtonSelected) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement listener");
        }
    }

    public interface onFloatingButtonSelected {
        public void onButtonSelected();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        fStore.collection("users").document(userID).collection("today").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    double totalCalories = 0;
                    if (task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            totalCalories += Double.parseDouble(document.getString("calories"));
                        }
                        caloriesIntake.setText(((int) totalCalories) + " ");
                        double progress = (totalCalories/recommended) * 100;
                        caloriesBar.setProgress((int) progress);
                    }
                }
            }
        });
    }
}