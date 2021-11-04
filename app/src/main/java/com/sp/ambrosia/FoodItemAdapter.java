package com.sp.ambrosia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FoodItemAdapter extends FirestoreRecyclerAdapter<FoodItem, FoodItemAdapter.FoodItemHolder> {

    private int totalCalories = 0;
    private int noMains = 0, noSides = 0, noDesserts = 0, noSnacks = 0, noDrinks = 0;

    public FoodItemAdapter(@NonNull FirestoreRecyclerOptions<FoodItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FoodItemHolder holder, int position, @NonNull FoodItem model) {
        holder.foodName.setText(model.getName());
        holder.foodType.setText(model.getType());
        holder.foodCalories.setText(model.getCalories());
        switch (model.getType()) {
            case "Main":
                holder.foodIcon.setImageResource(R.drawable.mainicon);
                noMains++;
                break;
            case "Sides":
                holder.foodIcon.setImageResource(R.drawable.sidesicon);
                noSides++;
                break;
            case "Desserts":
                holder.foodIcon.setImageResource(R.drawable.desserticon);
                noDesserts++;
                break;
            case "Snacks":
                holder.foodIcon.setImageResource(R.drawable.snacksicon);
                noSnacks++;
                break;
            case "Drinks":
                holder.foodIcon.setImageResource(R.drawable.drinksicon);
                noDrinks++;
                break;
        }
        totalCalories += Integer.parseInt(model.getCalories());
    }

    public void deleteItem(int position) {
        totalCalories -= Integer.parseInt(getSnapshots().getSnapshot(position).getString("calories"));
        switch (getSnapshots().getSnapshot(position).getString("type")) {
            case "Main":
                noMains--;
                break;
            case "Sides":
                noSides--;
                break;
            case "Desserts":
                noDesserts--;
                break;
            case "Snacks":
                noSnacks++;
                break;
            case "Drinks":
                noDrinks--;
                break;
        }
        getSnapshots().getSnapshot(position).getReference().delete();
    }



    public void reInitialize() {
        totalCalories = 0;
        noMains = noSides = noDesserts = noSnacks = noDrinks = 0;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public int getNoMains() {
        return noMains;
    }

    public int getNoSides() {
        return noSides;
    }

    public int getNoDesserts() {
        return noDesserts;
    }

    public int getNoSnacks() {
        return noSnacks;
    }

    public int getNoDrinks() {
        return noDrinks;
    }

    @NonNull
    @Override
    public FoodItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fooditem, parent, false);
        return new FoodItemHolder(v);
    }

    class FoodItemHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        TextView foodType;
        TextView foodCalories;
        ImageView foodIcon;

        public FoodItemHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodname);
            foodType = itemView.findViewById(R.id.foodtype);
            foodCalories = itemView.findViewById(R.id.foodcalories);
            foodIcon = itemView.findViewById(R.id.foodicon);
        }
    }



}
