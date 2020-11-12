package com.stefanos.order.PrintersActivity;

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
import com.stefanos.order.R;
import com.stefanos.order.SQLiteDatabaseForPrinters.DatabaseHandler;
import com.stefanos.order.SQLiteDatabaseForPrinters.IpAdressPrinter;

import java.util.HashMap;

public class PrintersActivity extends AppCompatActivity {

    private FloatingActionButton printerAddButton;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private RecyclerView printersRecyclerView;
    private PrinterAdapter printerAdapter;
    private TextInputLayout createNewPrinterItemTextInputLayout;
    private TextInputLayout createNewPrinterPortTextInputLayout;
    private EditText createPort;
    private EditText createIpAddress;
    private MaterialButton savePrinterButton;
    private MaterialButton cancelPrinterButton;
    private Toolbar toolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printers);


    }

    private void setToolbar() {

        toolbar = findViewById(R.id.toolbarPrinters);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.printers_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setPrinterAddButton() {


        printerAddButton = findViewById(R.id.addPrinterItemButton);
        printerAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogBuilder = new AlertDialog.Builder(PrintersActivity.this);
                view = getLayoutInflater().inflate(R.layout.create_new_printer_item, null);
                alertDialogBuilder.setView(view);
                dialog = alertDialogBuilder.create();
                dialog.show();

                createNewPrinterItemTextInputLayout = view.findViewById(R.id.createNewPrinterItemXml);
                createIpAddress = view.findViewById(R.id.createIpAddressItemXml);

                createNewPrinterPortTextInputLayout=view.findViewById(R.id.createNewPrinterItemPortXml);
                createPort=view.findViewById(R.id.createPortItemXml);

                cancelPrinterButton = view.findViewById(R.id.cancelPrinterItemButtonXml);
                savePrinterButton = view.findViewById(R.id.savePrinterItemButtonXml);

                cancelPrinterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                savePrinterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!createIpAddress.getText().toString().trim().isEmpty()&&!createPort.getText().toString().trim().isEmpty()) {

                            PrinterItem printerItem = new PrinterItem();
                            printerItem.setPrinter(createIpAddress.getText().toString());
                            printerItem.setPort(createPort.getText().toString());

                            HashMap<Object, Object> exampleTry = new HashMap<>();
                            exampleTry.put("ipAdress", printerItem.getPrinter());
                            exampleTry.put("port",printerItem.getPort());

                            String storeName = readPreferences();

                            DocumentReference tablesRef = db.collection("store").document(storeName).
                                    collection("printers").document(printerItem.getPrinter());
                            tablesRef.set(exampleTry);
                            startActivity(new Intent(PrintersActivity.this, PrintersActivity.class));

                            dialog.dismiss();
                            setUpRecyclerView();
                        } else {
                            createNewPrinterItemTextInputLayout.setError("Η IP Address και το Port δεν πρέπει να είναι κενή");
                            createNewPrinterPortTextInputLayout.setError("Η IP Address και το Port δεν πρέπει να είναι κενή");
                        }

                    }
                });


            }
        });

    }



    private void setUpRecyclerView() {

        printersRecyclerView = findViewById(R.id.printerRecyclerView);
        printersRecyclerView.setHasFixedSize(true);
        printersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String storeName = readPreferences();
        CollectionReference tablesRef = db.collection("store").document(storeName).
                collection("printers");

        Query query = tablesRef;

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    databaseHandler=new DatabaseHandler(PrintersActivity.this);
                    databaseHandler.deleteAllIpAddresses();
                    int i=1;
                    for (DocumentSnapshot documentSnapshot:task.getResult()){

                        IpAdressPrinter ipAdressPrinter=new IpAdressPrinter();
                        ipAdressPrinter.setIpAdress(String.valueOf(documentSnapshot.get("ipAdress")));
                        ipAdressPrinter.setPort(String.valueOf(documentSnapshot.get("port")));
                        ipAdressPrinter.setId(i);
                        i++;
                        databaseHandler.addIpAddress(ipAdressPrinter);
                    }

                    printerAdapter.notifyDataSetChanged();
                }
            }
        });
        FirestoreRecyclerOptions<IpAdressPrinter> options = new FirestoreRecyclerOptions.Builder<IpAdressPrinter>().
                setQuery(query, IpAdressPrinter.class).build();

        printerAdapter = new PrinterAdapter(options);

        printersRecyclerView.setAdapter(printerAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                printerAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(printersRecyclerView);

        setPrinterAddButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setToolbar();
        setUpRecyclerView();
        printerAdapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (printerAdapter != null) {
            printerAdapter.stopListening();
        }

    }

    private String readPreferences() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName = prefs.getString("name", null);

        return storeName;
    }
}
