package com.stefanos.order.AdministratorChoicesActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.stefanos.order.KouberActivity.KouberActivityWithFragment;
import com.stefanos.order.MenuCategory.MenuCategoryActivity;
import com.stefanos.order.OrofoiActivity.OrofoiActivity;
import com.stefanos.order.PrintersActivity.PrintersActivity;
import com.stefanos.order.R;
import com.stefanos.order.TablesActivity.TablesActivityWithFragment;
import com.stefanos.order.TzirosActivity.TzirosActivityWithFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class AdministratorChoicesActivity extends AppCompatActivity {

    private MaterialButton menuButton;
    private MaterialButton tablesButton;
    private MaterialButton tzirosButton;
    private MaterialButton proiontaButton;
    private MaterialButton orofoiButton;
    private MaterialButton printersButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_choices);


        setTablesButton();
        setMenuButton();
        setOrofoiButton();

        setTzirosButton();
        setProiontaButton();
        setPrintersButton();

        toolbar = findViewById(R.id.toolbarAdministratorChoice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.choice_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void setPrintersButton() {

        printersButton = findViewById(R.id.printersButtonXml);
        printersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdministratorChoicesActivity.this, PrintersActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setTablesButton() {

        tablesButton = findViewById(R.id.tablesXml);
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
                            startActivity(new Intent(AdministratorChoicesActivity.this, TablesActivityWithFragment.class));
                        } else {
                            Toast.makeText(AdministratorChoicesActivity.this,
                                    "Πρέπει να δημιουργήσεις ορόφους", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void setMenuButton() {

        menuButton = findViewById(R.id.menuXml);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdministratorChoicesActivity.this, MenuCategoryActivity.class));
            }
        });
    }

    private void setTzirosButton() {

        tzirosButton = findViewById(R.id.tzirosXml);
        tzirosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdministratorChoicesActivity.this, TzirosActivityWithFragment.class));
            }
        });
    }

    private void setProiontaButton() {

        proiontaButton = findViewById(R.id.proiontaXml);
        proiontaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdministratorChoicesActivity.this, KouberActivityWithFragment.class));
            }
        });
    }

    private void setOrofoiButton() {
        orofoiButton = findViewById(R.id.orofoiXml);
        orofoiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdministratorChoicesActivity.this, OrofoiActivity.class));
            }
        });
    }


    private String readPreferences() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName = prefs.getString("name", null);

        return storeName;
    }


}
