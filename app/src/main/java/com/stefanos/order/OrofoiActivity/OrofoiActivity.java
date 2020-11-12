package com.stefanos.order.OrofoiActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.stefanos.order.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Objects;

public class OrofoiActivity extends AppCompatActivity {


    private FloatingActionButton floatingActionButtonMenu;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OrofoiAdapter orofoiAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private AlertDialog.Builder alertDialogBuilderMenu;
    private AlertDialog dialogMenu;
    private MaterialButton saveButtonMenu;
    private MaterialButton cancelButtonMenu;
    private TextInputLayout createOrofoiTextLayout;
    private TextInputLayout createPriorityTextinputLayout;
    private EditText createOrofoi;
    private EditText createPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orofoi);
    }

    private void setUpRecyclerView() {


        recyclerView = findViewById(R.id.orofoiRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String storeName = readPreferences();
        CollectionReference tablesRef = db.collection("store").document(storeName).
                collection("orofoi");

        Query query = tablesRef.orderBy("priority");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    orofoiAdapter.notifyDataSetChanged();
                }
            }
        });

        FirestoreRecyclerOptions<OrofoiItem> options = new FirestoreRecyclerOptions.Builder<OrofoiItem>().
                setQuery(query, OrofoiItem.class).build();
        orofoiAdapter = new OrofoiAdapter(options);

        recyclerView.setAdapter(orofoiAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                orofoiAdapter.deleteItem(viewHolder.getAdapterPosition());

            }
        }).attachToRecyclerView(recyclerView);

        setFloatingActionButtonMethod();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
        orofoiAdapter.startListening();
        toolbar=findViewById(R.id.toolbarOrofoi);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.orofoi_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    protected void onStop() {
        super.onStop();
        if(orofoiAdapter!=null){
            orofoiAdapter.stopListening();
        }

    }

    private void setFloatingActionButtonMethod() {
        floatingActionButtonMenu = findViewById(R.id.addOrofoiButton);
        floatingActionButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogBuilderMenu = new AlertDialog.Builder(OrofoiActivity.this);
                view = getLayoutInflater().inflate(R.layout.create_orofoi_dialog, null);
                alertDialogBuilderMenu.setView(view);
                dialogMenu = alertDialogBuilderMenu.create();
                dialogMenu.show();

                createOrofoiTextLayout = view.findViewById(R.id.createOrofoTextLayout);
                createOrofoiTextLayout.setHint(getResources().getString(R.string.dimiourgia_orofou_greek));
                createPriorityTextinputLayout = view.findViewById(R.id.createPriorityTextInputLayout);
                createOrofoi = view.findViewById(R.id.createOrofoXml);
                createPriority = view.findViewById(R.id.createOrofoPriorityXml);
                cancelButtonMenu = view.findViewById(R.id.cancelButtonXml);
                saveButtonMenu = view.findViewById(R.id.saveButtonXml);

                cancelButtonMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogMenu.dismiss();
                    }
                });

                saveButtonMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!createPriority.getText().toString().trim().isEmpty() &&
                                !createOrofoi.getText().toString().trim().isEmpty()) {
                            String storeName = readPreferences();

                            HashMap<String, Object> exampleTry = new HashMap<>();
                            OrofoiItem orofoiItem = new OrofoiItem();
                            orofoiItem.setOrofoi(createOrofoi.getText().toString());
                            orofoiItem.setPriority(Integer.parseInt(createPriority.getText().toString()));
                            exampleTry.put("orofoi", orofoiItem.getOrofoi());
                            exampleTry.put("priority", orofoiItem.getPriority());
                            DocumentReference newTables = db.collection("store").document(storeName).
                                    collection("orofoi").document(createOrofoi.getText().toString());
                            newTables.set(exampleTry);

                            startActivity(new Intent(OrofoiActivity.this, OrofoiActivity.class));

                            dialogMenu.dismiss();
                            setUpRecyclerView();
                        } else {
                            createOrofoiTextLayout.setError("Οι όροφοι και η σειρά προτεραιότητας δεν πρέπει να είναι κενά");
                            createPriorityTextinputLayout.setError("Οι όροφοι και η σειρά προτεραιότητας δεν πρέπει να είναι κενά");
                        }
                    }
                });


            }
        });
    }

    private String readPreferences() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName = prefs.getString("name", null);

        return storeName;
    }
}
