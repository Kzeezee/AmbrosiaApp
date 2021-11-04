package com.sp.ambrosia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class AddMenuItemFragment extends Fragment implements View.OnClickListener {

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userID;

    private EditText mealName, mealCalories;
    private ImageView mealIcon;
    private Button addMenuItemB;
    private ProgressBar progressBar;
    private String mealType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addmenuitem, container, false);

        //Firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        //Normal views Init
        mealName = view.findViewById(R.id.mealname);
        mealCalories = view.findViewById(R.id.mealcalories);
        addMenuItemB = view.findViewById(R.id.addMenuItemB);
        mealIcon = view.findViewById(R.id.mealicon);
        addMenuItemB.setOnClickListener(this);
        mealIcon.setImageResource(R.drawable.mainicon);
        progressBar = view.findViewById(R.id.ProgressbarAddMenuItem);

        //Spinner related
        Spinner spinner = view.findViewById(R.id.mealtype);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.meal_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = parent.getItemAtPosition(position).toString();

                switch (type) {
                    case "Main":
                        mealIcon.setImageResource(R.drawable.mainicon);
                        mealType = "Main";
                        break;
                    case "Sides":
                        mealIcon.setImageResource(R.drawable.sidesicon);
                        mealType = "Sides";
                        break;
                    case "Desserts":
                        mealIcon.setImageResource(R.drawable.desserticon);
                        mealType = "Desserts";
                        break;
                    case "Snacks":
                        mealIcon.setImageResource(R.drawable.snacksicon);
                        mealType = "Snacks";
                        break;
                    case "Drinks":
                        mealIcon.setImageResource(R.drawable.drinksicon);
                        mealType = "Drinks";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        String mealname = mealName.getText().toString();
        String mealtype = mealType;
        String mealcalories = mealCalories.getText().toString();

        if (mealname.isEmpty() || mealcalories.isEmpty()) {
            Toast.makeText(getActivity(), "Food's name and calories cannot be blank!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        Map<String, Object> mealInfo = new HashMap<>();
        mealInfo.put("name", mealname);
        mealInfo.put("type", mealtype);
        mealInfo.put("calories", mealcalories);

        fStore.collection("users").document(userID).collection("menuitems").add(mealInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getActivity(), "Menu Item has successfully been added!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error adding Menu Item!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}