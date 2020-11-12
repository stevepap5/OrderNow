package com.stefanos.order.MenuItemFromTableActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.stefanos.order.ExtraFromTableActivity.ExtraMenuItemAccessFromTableActivity;
import com.stefanos.order.MenuItemActivity.MenuItem;
import com.stefanos.order.R;
import com.stefanos.order.XwrisFromTableActivity.XwrisMenuItemFromTableActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MenuItemsAccessFromTableActivity extends AppCompatActivity {

    private static Object mContext;

    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MenuItemAccessFromTableAdapter menuItemAccessFromTableAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_items_access_from_individual_table);

        mContext = this;
    }

    private void setUpRecyclerView() {

        recyclerView = findViewById(R.id.menuRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        setThePreferencesMenuCategory();

        String storeName = readPreferences();
        CollectionReference tablesRef = db.collection("store").document(storeName).
                collection("menuCategory").document(readPreferencesMenuCategory()).
                collection("menuItems");

        Query query = tablesRef.orderBy("id");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    menuItemAccessFromTableAdapter.notifyDataSetChanged();
                }
            }
        });
        FirestoreRecyclerOptions<MenuItem> options = new FirestoreRecyclerOptions.Builder<MenuItem>().
                setQuery(query, MenuItem.class).build();

        menuItemAccessFromTableAdapter = new MenuItemAccessFromTableAdapter(options);

        recyclerView.setAdapter(menuItemAccessFromTableAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
        toolbar=findViewById(R.id.toolbarMenuItemsAccessFromTable);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.menu_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        menuItemAccessFromTableAdapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(menuItemAccessFromTableAdapter!=null){
            menuItemAccessFromTableAdapter.stopListening();
        }

    }



    private String readPreferences() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName = prefs.getString("name", null);

        return storeName;
    }

    private void setThePreferencesMenuCategory() {
        String docId;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            docId = bundle.getString("menuCategory");
            SharedPreferences.Editor editor = getSharedPreferences("myStoreName", MODE_PRIVATE).edit();
            editor.remove("menuCategory");
            editor.apply();
            editor.putString("menuCategory", docId);
            editor.apply();
        }

    }

    public static void showExtraMenuItemsDetails(DocumentSnapshot documentSnapshot, int position){
        if(documentSnapshot.exists()){
            Intent intent=new Intent((Context)mContext, ExtraMenuItemAccessFromTableActivity.class);
            intent.putExtra("menuItem",documentSnapshot.getString("nameMenuItem"));
            ((Activity)mContext).startActivity(intent);}
    }

    public static void showXwrisMenuItemsDetails(DocumentSnapshot documentSnapshot, int position){
        if(documentSnapshot.exists()){
            Intent intent=new Intent((Context)mContext, XwrisMenuItemFromTableActivity.class);
            intent.putExtra("menuItem",documentSnapshot.getString("nameMenuItem"));
            ((Activity)mContext).startActivity(intent);}
    }

    public String readPreferencesMenuCategory() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String doc = prefs.getString("menuCategory", null);

        return doc;
    }
}
