package com.stefanos.order.XwrisActivity;

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
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.stefanos.order.ExtraActivity.ExtraItem;
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

public class XwrisMenuItemActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private XwrisAdapter xwrisAdapter;
    private FloatingActionButton floatingActionButton;
    private AlertDialog dialog;
    private AlertDialog.Builder alertDialogBuilder;
    private TextInputLayout createNewExtraTextInputLayout;
    private TextInputLayout createIdTextInputLayout;
    private EditText createId;
    private EditText createExtra;
    private MaterialButton cancelButton;
    private MaterialButton saveButton;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xwris_menu_item);
    }

    private void setUpRecyclerView() {


        recyclerView = findViewById(R.id.xwrisRecyclerView);
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

        xwrisAdapter = new XwrisAdapter(options);

        recyclerView.setAdapter(xwrisAdapter);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                   xwrisAdapter.notifyDataSetChanged();
                }
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {


                xwrisAdapter.deleteItem(viewHolder.getAdapterPosition());


            }
        }).attachToRecyclerView(recyclerView);

        setFloatingActionButtonMethod();
    }

    private void setFloatingActionButtonMethod() {

        floatingActionButton = findViewById(R.id.addXwrisButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogBuilder = new AlertDialog.Builder(XwrisMenuItemActivity.this);
                view = getLayoutInflater().inflate(R.layout.create_xwris_dialog, null);
                alertDialogBuilder.setView(view);
                dialog = alertDialogBuilder.create();
                dialog.show();

                createNewExtraTextInputLayout = view.findViewById(R.id.createXwrisMenuItemXml);
                createExtra = view.findViewById(R.id.createXwisXml);


                createIdTextInputLayout = view.findViewById(R.id.createIdTextInputLayout);
                createId = view.findViewById(R.id.createIdXml);
                cancelButton = view.findViewById(R.id.cancelMenuItemButtonXml);
                saveButton = view.findViewById(R.id.saveMenuItemButtonXml);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String storeName = readPreferences();
                        Log.i("SOS", storeName);
                        HashMap<String, Object> exampleTry = new HashMap<>();

                        if (!createId.getText().toString().trim().isEmpty() &&
                                !createExtra.getText().toString().trim().isEmpty()) {
                            ExtraItem extraItem = new ExtraItem();
                            extraItem.setExtra(createExtra.getText().toString().trim());
                            extraItem.setId(Integer.valueOf(createId.getText().toString().trim()));
                            exampleTry.put("id", extraItem.getId());
                            exampleTry.put("extra", extraItem.getExtra());
                            DocumentReference newExtra = db.collection("store").document(storeName).
                                    collection("menuCategory").document(readPreferencesMenuCategory()).
                                    collection("menuItems").document(readPreferenceMenuItem()).
                                    collection(readPreferenceMenuItem()+"xwris").document();
                            newExtra.set(exampleTry);


                            startActivity(new Intent(XwrisMenuItemActivity.this, XwrisMenuItemActivity.class));

                            dialog.dismiss();
                            setUpRecyclerView();
                        } else {
                            createNewExtraTextInputLayout.setError("'Εξτρα και σειρά προτεραιότητας δεν πρέπει να είναι κενά");
                            createIdTextInputLayout.setError("'Εξτρα και σειρά προτεραιότητας δεν πρέπει να είναι κενά");

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
        toolbar=findViewById(R.id.toolbarXwrisActivity);
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
