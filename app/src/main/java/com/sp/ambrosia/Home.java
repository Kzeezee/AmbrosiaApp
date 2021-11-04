package com.sp.ambrosia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//This activity is misleadingly called Home. This is the main activity that houses all the other fragments
public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.onFloatingButtonSelected {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private boolean vendorCheck;
    private String userID;
    private String restaurantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Firebase related
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        final DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()) {
                    vendorCheck = documentSnapshot.getBoolean("vendor");
                    if (vendorCheck) {
                        restaurantName = documentSnapshot.getString("restaurantname");
                        if (restaurantName == null || restaurantName.equals("")) {
                            Intent restaurantSetup = new Intent(Home.this, RestaurantSetup.class);
                            startActivity(restaurantSetup);
                            finish();
                        }
                    }
                    initialize();
                } else {
                    Log.d("tag", "onEvent: Document do not exist");
                }
            }
        });

        //Clear today's records if user is not a vendor and a new day has pass
        //Also sums up the total calories eaten that day and save it to a history
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.add(Calendar.DATE, -1);
        Date oneDayAgo = cal.getTime();
        Query oldRecords = fStore.collection("users").document(userID).collection("today").whereLessThan("timestamp", oneDayAgo);
        oldRecords.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int totalCalories = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        totalCalories += Integer.parseInt(document.getString("calories"));
                        document.getReference().delete();
                    }
                    Map<String, Object> totalCal = new HashMap<>();
                    totalCal.put("totalcalories", Integer.toString(totalCalories));
                    totalCal.put("timestamp", cal.getTime());
                    /*
                    fStore.collection("users").document(userID).collection("history").add(totalCal).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Log.d("TAG", "SUCCESSFUL ADDING TO HISTORY");
                        }
                    });

                     */
                } else {
                    Log.d("DOCUMENTS", "Error getting documents: ", task.getException());
                }
            }
        });

        checkPermissions();
    }

    private void initialize() {
        Log.d("TAG", String.valueOf(vendorCheck));
        Menu nav_Menu = navigationView.getMenu();
        if (vendorCheck) {
            nav_Menu.findItem(R.id.menuihome).setVisible(false);
            nav_Menu.findItem(R.id.menuitoday).setVisible(false);
            //nav_Menu.findItem(R.id.menuisummary).setVisible(false);
            nav_Menu.findItem(R.id.menuiepicurious).setVisible(false);
            nav_Menu.findItem(R.id.menuiprofile).setVisible(false);
            nav_Menu.findItem(R.id.menuisignout).setVisible(false);
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.container_fragment, new VendorHomeFragment());
            fragmentTransaction.commit();
        } else {
            nav_Menu.findItem(R.id.menuivendorhome).setVisible(false);
            nav_Menu.findItem(R.id.menuiadditem).setVisible(false);
            nav_Menu.findItem(R.id.menuivendorsignout).setVisible(false);
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.container_fragment, new HomeFragment());
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (menuItem.getItemId()) {
            case R.id.menuihome:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, new HomeFragment());
                fragmentTransaction.commit();
                break;

            case R.id.menuitoday:
                Intent today = new Intent(Home.this, Today.class);
                startActivity(today);
                break;

            /*case R.id.menuisummary:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, new SummaryFragment());
                fragmentTransaction.commit();
                break;

             */

            case R.id.menuiprofile:
                Intent profile = new Intent(this, Profile.class);
                startActivity(profile);
                break;

            //Empty as both operations are the same
            //Two buttons for aesthetic and organization purposes
            case R.id.menuisignout:

            case R.id.menuivendorsignout:
                fAuth.getInstance().signOut();
                Intent signout = new Intent(Home.this, Login.class);
                startActivity(signout);
                finish();
                break;

            case R.id.menuivendorhome:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, new VendorHomeFragment());
                fragmentTransaction.commit();
                break;

            case R.id.menuiadditem:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, new AddMenuItemFragment());
                fragmentTransaction.commit();
                break;

            case R.id.menuiepicurious:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment, new FindRecipesFragment());
                fragmentTransaction.commit();
                break;
        }
        return true;
    }

    public void checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.CAMERA);
        int permissionState2 = ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionState1 != PackageManager.PERMISSION_GRANTED && permissionState2 != PackageManager.PERMISSION_GRANTED) {
            showEnablePermissionAlert();
        }
    }

    public void showEnablePermissionAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        //Setting Dialog Title
        alertDialog.setTitle("Permission Settings");
        //Setting Dialog Message
        alertDialog.setMessage("Ambrosia requires permission to use Camera and Storage. " + "Do you want to go to settings menu?");
        //On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                //Goto Application Setting
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Home.this.startActivity(intent);
            }
        });

        //on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!vendorCheck) {
            FindRecipesFragment fragment = (FindRecipesFragment) getSupportFragmentManager().findFragmentById(R.id.container_fragment);
            if (fragment.canGoBack()) {
                fragment.goBack();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onButtonSelected() {
        Intent addFood = new Intent(this, AddUserFoodItem.class);
        startActivity(addFood);
    }
}
