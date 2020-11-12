package com.stefanos.order.ExtraFromTableActivity;

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

public class ExtraMenuItemAccessFromTableActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ExtraAccessFromTableAdapter extraAccessFromTableAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_menu_item_access_from_table);
    }

    private void setUpRecyclerView() {


        recyclerView = findViewById(R.id.extraFromTableRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setThePreferencesMenuItem();
        String storeName = readPreferences();
        CollectionReference tablesRef = db.collection("store").document(storeName).
                collection("menuCategory").document(readPreferencesMenuCategory()).
                collection("menuItems").document(readPreferenceMenuItem()).
                collection(readPreferenceMenuItem()+"extra");

        Query query = tablesRef.orderBy("id");
        FirestoreRecyclerOptions<ExtraItem> options = new FirestoreRecyclerOptions.Builder<ExtraItem>().
                setQuery(query, ExtraItem.class).build();

        extraAccessFromTableAdapter= new ExtraAccessFromTableAdapter(options);

        recyclerView.setAdapter(extraAccessFromTableAdapter);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    extraAccessFromTableAdapter.notifyDataSetChanged();
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
        extraAccessFromTableAdapter.startListening();
        toolbar=findViewById(R.id.toolbarExtraFromTableActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.extra_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(extraAccessFromTableAdapter!=null){
            extraAccessFromTableAdapter.stopListening();
        }
    }
}
