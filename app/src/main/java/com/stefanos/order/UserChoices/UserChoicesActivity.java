package com.stefanos.order.UserChoices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stefanos.order.PrintersActivity.PrintersActivity;
import com.stefanos.order.R;
import com.stefanos.order.TablesActivity.TablesActivityWithFragment;

public class UserChoicesActivity extends AppCompatActivity {

    private MaterialButton tablesButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_choices);

        setTablesButton();

        toolbar = findViewById(R.id.toolbarUserChoice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.choice_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void setTablesButton() {

        tablesButton = findViewById(R.id.tablesUserXml);
        tablesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String storeName = readPreferences();
                CollectionReference tablesRef = db.collection("store").document(storeName).
                        collection("orofoi");

                Query query = tablesRef.orderBy("priority");

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(UserChoicesActivity.this, TablesActivityWithFragment.class));
                        } else {
                            Toast.makeText(UserChoicesActivity.this,
                                    "Πρέπει να δημιουργήσεις ορόφους", Toast.LENGTH_SHORT).show();
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
