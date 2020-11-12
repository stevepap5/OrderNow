package com.stefanos.order.MenuCategoryFromTableActivity;


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


import com.stefanos.order.MenuItemFromTableActivity.MenuItemsAccessFromTableActivity;
import com.stefanos.order.R;
import com.stefanos.order.TablesActivity.Table;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MenuCategoryAccessFromTableActivity extends AppCompatActivity {

    private static Object mContext;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private MenuCategoryAccessFromTableAdapter menuCategoryAccessFromTableAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_category_access_from_individual_table);

        mContext = this;
    }

    private void setUpRecyclerView() {


        recyclerView=findViewById(R.id.menuRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String storeName=readPreferences();
        CollectionReference tablesRef=db.collection("store").document(storeName).
                collection("menuCategory");

        Query query=tablesRef.orderBy("priority");
        FirestoreRecyclerOptions<Table> options=new FirestoreRecyclerOptions.Builder<Table>().
                setQuery(query, Table.class).build();

        menuCategoryAccessFromTableAdapter =new MenuCategoryAccessFromTableAdapter(options);

        recyclerView.setAdapter(menuCategoryAccessFromTableAdapter);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    menuCategoryAccessFromTableAdapter.notifyDataSetChanged();
                }
            }
        });





    }




    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
        menuCategoryAccessFromTableAdapter.startListening();
        toolbar=findViewById(R.id.toolbarMenuCategoryAccessFromTable);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.menu_category_access_from_table_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    protected void onStop() {
        super.onStop();
        if(menuCategoryAccessFromTableAdapter !=null){
            menuCategoryAccessFromTableAdapter.stopListening();
        }

    }


    public static void showMenuCategoryDetails(DocumentSnapshot documentSnapshot, int position){
        if(documentSnapshot.exists()){
            Intent intent=new Intent((Context)mContext, MenuItemsAccessFromTableActivity.class);
            intent.putExtra("menuCategory",documentSnapshot.getString("table"));
            ((Activity)mContext).startActivity(intent);}
    }

    private String readPreferences(){

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName=prefs.getString("name",null);

        return storeName;
    }
}
