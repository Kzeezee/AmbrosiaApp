package com.sp.ambrosia;

public class FoodItem {
    private String name, type, calories;

    public FoodItem() {
        //empty constructor needed
    }

    public FoodItem(String name, String type, String calories) {
        this.name = name;
        this.type = type;
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCalories() {
        return calories;
    }
}
