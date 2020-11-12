package com.stefanos.order.XwrisFromTableActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.stefanos.order.ExtraActivity.ExtraItem;
import com.stefanos.order.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class XwrisMenuItemFromTableActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private XwrisAccessFromTableAdapter xwrisAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xwris_menu_item_from_table);
    }

    private void setUpRecyclerView() {


        recyclerView = findViewById(R.id.xwrisFromTableRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setThePreferencesMenuItem();
        String storeName = readPreferences();
        CollectionReference tablesRef = db.collection("store").document(storeName).
                collection("menuCategory").document(readPreferencesMenuCategory()).
                collection("menuItems").document(readPreferenceMenuItem()).
                collection(readPreferenceMenuItem()+"xwris");

        Query query = tablesRef.orderBy("id");
        FirestoreRecyclerOptions<ExtraItem> options = new FirestoreRecyclerOptions.Builder<ExtraItem>().
                setQuery(query, ExtraItem.class).build();

        xwrisAdapter = new XwrisAccessFromTableAdapter(options);

        recyclerView.setAdapter(xwrisAdapter);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    xwrisAdapter.notifyDataSetChanged();
                }
            }
        });



    }



    private String readPreferences() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName = prefs.getString("name", null);

        return storeName;
    }

    private String readPreferencesMenuCategory() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String doc = prefs.getString("menuCategory", null);

        return doc;
    }

    private void setThePreferencesMenuItem() {
        String docId;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            docId = bundle.getString("menuItem");
            SharedPreferences.Editor editor = getSharedPreferences("myStoreName", MODE_PRIVATE).edit();
            editor.remove("menuItem");
            editor.apply();
            editor.putString("menuItem", docId);
            editor.apply();
        }

    }

    private String readPreferenceMenuItem() {
        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String doc = prefs.getString("menuItem", null);

        return doc;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
        xwrisAdapter.startListening();
        toolbar=findViewById(R.id.toolbarXwrisFromTableActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.xwris_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(xwrisAdapter!=null){
            xwrisAdapter.stopListening();
        }
    }
}
