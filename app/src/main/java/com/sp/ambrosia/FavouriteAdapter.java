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
import com.google.firebase.firestore.DocumentSnapshot;

public class FavouriteAdapter extends FirestoreRecyclerAdapter<FoodItem, FavouriteAdapter.FavouriteHolder> {
    private OnItemClickListener listener;

    public FavouriteAdapter(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FavouriteHolder holder, int position, @NonNull FoodItem model) {
        holder.name.setText(model.getName());
        holder.type.setText(model.getType());
        holder.calories.setText(model.getCalories());
        switch (model.getType()) {
            case "Main":
                holder.icon.setImageResource(R.drawable.mainicon);
                break;
            case "Sides":
                holder.icon.setImageResource(R.drawable.sidesicon);
                break;
            case "Desserts":
                holder.icon.setImageResource(R.drawable.desserticon);
                break;
            case "Snacks":
                holder.icon.setImageResource(R.drawable.snacksicon);
                break;
            case "Drinks":
                holder.icon.setImageResource(R.drawable.drinksicon);
                break;
        }
    }

    @NonNull
    @Override
    public FavouriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fooditem, parent, false);
        return new FavouriteHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class FavouriteHolder extends RecyclerView.ViewHolder {
        TextView name, type, calories;
        ImageView icon;

        public FavouriteHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.foodname);
            type = itemView.findViewById(R.id.foodtype);
            calories = itemView.findViewById(R.id.foodcalories);
            icon = itemView.findViewById(R.id.foodicon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
