package com.stefanos.order.TablesActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.stefanos.order.R;
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
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class TablesFragment extends Fragment {


    private FloatingActionButton floatingActionButton;
    private View view;
    private RecyclerView recyclerView;
    private TableAdapter tableAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private TextInputLayout createNewTableTextInputLayout;
    private TextInputLayout createPriorityTextInputLayout;
    private EditText createPriority;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private EditText createNewTable;
    private static Object mContext;
    public TablesFragment() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_tables, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager=new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mContext=this;
        CollectionReference tablesRef = null;
        if (getArguments()!=null) {
            String getDataOrofos=getArguments().getString("orofos");
            String storeName = readPreferences();

            tablesRef = db.collection("store").document(storeName).
                    collection("orofoi").document(getDataOrofos).collection("tables");
        }

        Query query = tablesRef.orderBy("priority");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    tableAdapter.notifyDataSetChanged();
                }
            }
        });
        FirestoreRecyclerOptions<Table> options = new FirestoreRecyclerOptions.Builder<Table>().
                setQuery(query, Table.class).build();

        tableAdapter = new TableAdapter(options);
        recyclerView.setAdapter(tableAdapter);

        String storeName = readPreferences();
        final DocumentReference loginuser=db.collection("store").document(storeName).
                collection("user").document(readPreferencesUsername());
        final boolean[] user = {false};
        loginuser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot getTask=task.getResult();
                    assert getTask != null;
                    user[0] = (boolean) getTask.get("user");


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

                if (tableAdapter.getItem(viewHolder.getAdapterPosition()).getStatus().equals("")&&
                        !user[0]) {
                    tableAdapter.deleteItem(viewHolder.getAdapterPosition());

                } else {
                    Toast.makeText(getActivity(),"Το τραπέζι πρέπει να είναι άδειο και να είστε διαχειριστής για να το σβήσετε",
                            Toast.LENGTH_LONG).show();
                    tableAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            }
        }).attachToRecyclerView(recyclerView);

        setFloatingActionButtonMethod(view);

        return view;
    }

    private String readPreferences() {

        SharedPreferences prefs = Objects.requireNonNull(this.getActivity()).getSharedPreferences("myStoreName", MODE_PRIVATE);

        return prefs.getString("name", null);
    }

    private   String readPreferencesUsername() {

        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("myStoreName", MODE_PRIVATE);
        String userName = prefs.getString("username", null);

        return userName;
    }


    @Override
    public void onStart() {
        super.onStart();
        tableAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        tableAdapter.stopListening();
    }


    private void setFloatingActionButtonMethod(View view) {

        floatingActionButton = view.findViewById(R.id.addTableButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    view = getLayoutInflater().inflate(R.layout.create_table_dialog, null);
                    alertDialogBuilder.setView(view);
                    dialog = alertDialogBuilder.create();
                    dialog.show();

                    createNewTableTextInputLayout = view.findViewById(R.id.createNewTableXml);
                    createPriorityTextInputLayout = view.findViewById(R.id.createPriorityTextInputLayout);
                    createNewTableTextInputLayout.setHint(getResources().getString(R.string.create_table_greek));
                    createPriority = view.findViewById(R.id.createTablePriorityXml);
                    createNewTable = view.findViewById(R.id.createTableXml);
                    cancelButton = view.findViewById(R.id.cancelButtonXml);
                    saveButton = view.findViewById(R.id.saveButtonXml);

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

                            HashMap<String, Object> exampleTry = new HashMap<>();
                            Table table = new Table();
                            if (!createPriority.getText().toString().trim().isEmpty() && !createNewTable.getText().toString().trim().isEmpty()) {
                                table.setTable(createNewTable.getText().toString());
                                table.setPriority(Integer.parseInt(createPriority.getText().toString()));
                                table.setStatus("");
                                table.setOrofosName(getArguments().getString("orofos"));
                                exampleTry.put("table", table.getTable());
                                exampleTry.put("priority", table.getPriority());
                                exampleTry.put("status",table.getStatus());
                                exampleTry.put("orofosName",table.getOrofosName());
                                DocumentReference newTables = db.collection("store").document(storeName).
                                        collection("orofoi").document(getArguments().getString("orofos")).
                                        collection("tables").document(createNewTable.getText().toString());
                                newTables.set(exampleTry);

//                                startActivity(new Intent(TablesActivity.this, TablesActivity.class));

                                dialog.dismiss();

                            } else {
                                createNewTableTextInputLayout.setError("Το τραπέζι και η σειρά προτεραιότητας δεν πρέπει να είναι κενά");
                                createPriorityTextInputLayout.setError("Το τραπέζι και η σειρά προτεραιότητας δεν πρέπει να είναι κενά");

                            }
                        }
                    });



            }
        });


    }
}
