package com.stefanos.order.MenuCategory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.stefanos.order.MenuItemActivity.MenuItemsActivity;
import com.stefanos.order.R;
import com.stefanos.order.TablesActivity.Table;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MenuCategoryActivity extends AppCompatActivity {

    private static Object mContext;
    private FloatingActionButton floatingActionButtonMenu;
    private AlertDialog.Builder alertDialogBuilderMenu;
    private AlertDialog dialogMenu;
    private MaterialButton saveButtonMenu;
    private MaterialButton cancelButtonMenu;
    private TextInputLayout createMenuTextLayout;
    private TextInputLayout createPriorityTextinputLayout;
    private EditText createNewMenuCategory;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private MenuCategoryAdapter menuCategoryAdapter;
    private RecyclerView recyclerView;
    private EditText createPriority;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_category);


        mContext = this;
//        setUpRecyclerView();


    }

    private void setUpRecyclerView() {


        recyclerView=findViewById(R.id.menuRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String storeName=readPreferences();
        CollectionReference tablesRef=db.collection("store").document(storeName).
                collection("menuCategory");

        Query query=tablesRef.orderBy("priority");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    menuCategoryAdapter.notifyDataSetChanged();
                }
            }
        });

        FirestoreRecyclerOptions<Table> options=new FirestoreRecyclerOptions.Builder<Table>().
                setQuery(query, Table.class).build();
        menuCategoryAdapter =new MenuCategoryAdapter(options);

        recyclerView.setAdapter(menuCategoryAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                if (menuCategoryAdapter.getItem(viewHolder.getAdapterPosition()).getStatus().equals("")) {
                    menuCategoryAdapter.deleteItem(viewHolder.getAdapterPosition());
                } else {
                    Toast.makeText(MenuCategoryActivity.this,"Η κατηγορία στο μενού πρέπει να είναι άδεια για να την σβήσεις",Toast.LENGTH_LONG).show();
                    menuCategoryAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }

            }
        }).attachToRecyclerView(recyclerView);

        setFloatingActionButtonMethod();
    }

    private void setFloatingActionButtonMethod(){

        floatingActionButtonMenu=findViewById(R.id.addTableButton);
        floatingActionButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogBuilderMenu = new AlertDialog.Builder(MenuCategoryActivity.this);
                view = getLayoutInflater().inflate(R.layout.create_table_dialog, null);
                alertDialogBuilderMenu.setView(view);
                dialogMenu = alertDialogBuilderMenu.create();
                dialogMenu.show();

                createMenuTextLayout=view.findViewById(R.id.createNewTableXml);
                createMenuTextLayout.setHint(getResources().getString(R.string.menu_category_greek));
                createPriorityTextinputLayout=view.findViewById(R.id.createPriorityTextInputLayout);
                createNewMenuCategory=view.findViewById(R.id.createTableXml);
                createPriority=view.findViewById(R.id.createTablePriorityXml);
                cancelButtonMenu=view.findViewById(R.id.cancelButtonXml);
                saveButtonMenu=view.findViewById(R.id.saveButtonXml);

                cancelButtonMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogMenu.dismiss();
                    }
                });

                saveButtonMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!createPriority.getText().toString().trim().isEmpty()&&
                        !createNewMenuCategory.getText().toString().trim().isEmpty()) {
                            String storeName=readPreferences();
                            Log.i("SOS",storeName);
                            HashMap<String, Object> exampleTry=new HashMap<>();
                            Table table =new Table();
                            table.setTable(createNewMenuCategory.getText().toString());
                            table.setPriority(Integer.parseInt(createPriority.getText().toString()));
                            table.setStatus("");
                            exampleTry.put("table", table.getTable());
                            exampleTry.put("priority",table.getPriority());
                            exampleTry.put("status",table.getStatus());
                            DocumentReference newTables=db.collection("store").document(storeName).
                                    collection("menuCategory").document(createNewMenuCategory.getText().toString());
                            newTables.set(exampleTry);

                            startActivity(new Intent(MenuCategoryActivity.this, MenuCategoryActivity.class));

                            dialogMenu.dismiss();
                            setUpRecyclerView();
                        } else {
                            createMenuTextLayout.setError("Η κατηγορία μενού και η σειρά προτεραιότητας δεν πρέπει να είναι κενά");
                            createPriorityTextinputLayout.setError("Η κατηγορία μενού και η σειρά προτεραιότητας δεν πρέπει να είναι κενά");
                        }
                    }
                });


            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
        menuCategoryAdapter.startListening();
        toolbar=findViewById(R.id.toolbarMenuCategory);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.menu_category_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    protected void onStop() {
        super.onStop();
       if(menuCategoryAdapter!=null){
           menuCategoryAdapter.stopListening();
       }

    }


    public static void showMenuCategoryDetails(DocumentSnapshot documentSnapshot, int position){
        if(documentSnapshot.exists()){
            Intent intent=new Intent((Context)mContext, MenuItemsActivity.class);
            intent.putExtra("menuCategory",documentSnapshot.getString("table"));
            ((Activity)mContext).startActivity(intent);}
    }

    private String readPreferences(){

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName=prefs.getString("name",null);

        return storeName;
    }
}
