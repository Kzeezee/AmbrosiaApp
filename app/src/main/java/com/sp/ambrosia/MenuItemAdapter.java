package com.sp.ambrosia;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MenuItemAdapter extends ArrayAdapter<MenuItem> {
    public MenuItemAdapter(Context context, List<MenuItem> object) {
        super(context, 0, object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.fooditem,parent,false);
        }

        TextView foodName = convertView.findViewById(R.id.foodname);
        TextView foodType = convertView.findViewById(R.id.foodtype);
        TextView foodCalories = convertView.findViewById(R.id.foodcalories);
        ImageView foodIcon = convertView.findViewById(R.id.foodicon);

        MenuItem menuItem = getItem(position);

        foodName.setText("Dish: " + menuItem.getName());
        foodType.setText("Type: " + menuItem.getType());
        foodCalories.setText("Calories: " + menuItem.getCalories());
        switch (menuItem.getType()) {
            case "Main":
                foodIcon.setImageResource(R.drawable.mainicon);
                break;
            case "Desserts":
                foodIcon.setImageResource(R.drawable.desserticon);
                break;
            case "Drinks":
                foodIcon.setImageResource(R.drawable.drinksicon);
                break;
            case "Snacks":
                foodIcon.setImageResource(R.drawable.snacksicon);
                break;
            case "Sides":
                foodIcon.setImageResource(R.drawable.sidesicon);
                break;
        }

        return convertView;
    }
}
