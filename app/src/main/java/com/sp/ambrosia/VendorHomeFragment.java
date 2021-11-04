package com.sp.ambrosia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class VendorHomeFragment extends Fragment {

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID, firstName, restName;
    private TextView header, section;
    private List<MenuItem> mMenuItemList = new ArrayList<MenuItem>();
    private ListView mMenuItemListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_vendorhome, container, false);

        //Initialization
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        header = view.findViewById(R.id.headervendor);
        section = view.findViewById(R.id.sectiontextvendor);
        section.setText("");
        userID = fAuth.getCurrentUser().getUid();
        mMenuItemListView = view.findViewById(R.id.listvendor);
        mMenuItemListView.setOnItemClickListener(onListMenuItemClick);

        //Personalization of UI
        fStore.collection("users").document(userID)
                .addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()) {
                    firstName = documentSnapshot.getString("fName");
                    restName = documentSnapshot.getString("restaurantname");
                    header.setText("Hello, " + firstName);

                } else {
                    Log.d("tag", "onEvent: Document do not exist");
                }
            }
        });

        //Updating UI if there are already generated MenuItems stored in FireStore
        fStore.collection("users").document(userID)
                .collection("menuitems").orderBy("type").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        section.setText("Your restaurant, " + restName + ", currently has the following menu items with QR codes and their information:");

                        //list menu items
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MenuItem item = document.toObject(MenuItem.class);
                            item.setDocumentID(document.getId());
                            mMenuItemList.add(item);
                        }
                        MenuItemAdapter mMenuItemAdapter = new MenuItemAdapter(getActivity(), mMenuItemList);
                        mMenuItemListView.setAdapter(mMenuItemAdapter);

                    } else {
                        section.setText("Your restaurant, " + restName + ", currently has no menu items added with Ambrosia. Add one now through the navigation drawer!");
                    }

                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }

            }
        });

        return view;
    }

    AdapterView.OnItemClickListener onListMenuItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MenuItem item = mMenuItemList.get(position);

            Intent qrPageDirect = new Intent(getActivity(), MenuItemInfo.class);
            qrPageDirect.putExtra("documentID", item.getDocumentID());
            qrPageDirect.putExtra("name", item.getName());
            qrPageDirect.putExtra("type", item.getType());
            qrPageDirect.putExtra("calories", item.getCalories());
            startActivity(qrPageDirect);

        }
    };
}

