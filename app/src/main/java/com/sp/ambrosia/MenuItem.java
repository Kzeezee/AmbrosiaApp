package com.sp.ambrosia;

import com.google.firebase.firestore.Exclude;

public class MenuItem {
    private String name;
    private String type;
    private String calories;
    private String documentID;

    @Exclude
    public String getDocumentID() { return documentID; }

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getCalories() {
        return calories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }


    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
