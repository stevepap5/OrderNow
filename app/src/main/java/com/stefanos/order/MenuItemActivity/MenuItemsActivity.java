package com.stefanos.order.MenuItemActivity;

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
import android.widget.RadioButton;

import com.stefanos.order.ExtraActivity.ExtraMenuItemActivity;
import com.stefanos.order.R;
import com.stefanos.order.XwrisActivity.XwrisMenuItemActivity;
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

public class MenuItemsActivity extends AppCompatActivity {

    private static Object mContext;

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private TextInputLayout createNewMenuItemTextInputLayout;
    private TextInputLayout createIdTextInputLayout;
    private TextInputLayout createPriceItemTextInputLayout;
    private EditText createId;
    private RadioButton drinksRadioButton;
    private RadioButton cafedesRadioButton;
    private RadioButton appetizerRadioButton;
    private RadioButton mainDishesRadioButton;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private EditText createNewMenuItem;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MenuItemAdapter menuItemAdapter;
    private TextInputLayout createMenuItemPriceTextLayout;
    private EditText createMenuItemPrice;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_items);


        mContext = this;


    }

    private void setUpRecyclerView() {

        recyclerView = findViewById(R.id.menuRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        setThePreferencesMenuCategory();

        String storeName = readPreferences();
        CollectionReference tablesRef = db.collection("store").document(storeName).
                collection("menuCategory").document(readPreferencesMenuCategory()).
                collection("menuItems");

        Query query = tablesRef.orderBy("id");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    menuItemAdapter.notifyDataSetChanged();
                }
            }
        });
        FirestoreRecyclerOptions<MenuItem> options = new FirestoreRecyclerOptions.Builder<MenuItem>().
                setQuery(query, MenuItem.class).build();

        menuItemAdapter = new MenuItemAdapter(options);

        recyclerView.setAdapter(menuItemAdapter);




        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deleteExtraAndXwris(MenuItemAdapter.MenuItemHolder.nameTextView.getText().toString());
//                menuItemAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);


        setFloatingActionButtonMethod();

    }

    private void deleteExtraAndXwris(String menuItemName) {
        Log.i("menuItemName",menuItemName);
        String storeName = readPreferences();
        CollectionReference tablesRefExtra = db.collection("store").document(storeName).
                collection("menuCategory").document(readPreferencesMenuCategory()).
                collection("menuItems").document(menuItemName).collection(menuItemName+"extra");

        tablesRefExtra.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot:task.getResult()){
                        documentSnapshot.getReference().delete();
                    }
                }
            }
        });
        CollectionReference tablesRefXwris = db.collection("store").document(storeName).
                collection("menuCategory").document(readPreferencesMenuCategory()).
                collection("menuItems").document(menuItemName).collection(menuItemName+"xwris");

        tablesRefXwris.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot:task.getResult()){
                        documentSnapshot.getReference().delete();
                    }
                }
            }
        });
        DocumentReference tablesRef = db.collection("store").document(storeName).
                collection("menuCategory").document(readPreferencesMenuCategory()).
                collection("menuItems").document(menuItemName);
        tablesRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    task.getResult().getReference().delete();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
        toolbar=findViewById(R.id.toolbarMenuItems);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.menu_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        menuItemAdapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(menuItemAdapter!=null){
            menuItemAdapter.stopListening();
        }

    }

    public static void showExtraMenuItemsDetails(DocumentSnapshot documentSnapshot, int position){
        if(documentSnapshot.exists()){
            Intent intent=new Intent((Context)mContext, ExtraMenuItemActivity.class);
            intent.putExtra("menuItem",documentSnapshot.getString("nameMenuItem"));
            ((Activity)mContext).startActivity(intent);}
    }

    public static void showXwrisMenuItemsDetails(DocumentSnapshot documentSnapshot, int position){
        if(documentSnapshot.exists()){
            Intent intent=new Intent((Context)mContext, XwrisMenuItemActivity.class);
            intent.putExtra("menuItem",documentSnapshot.getString("nameMenuItem"));
            ((Activity)mContext).startActivity(intent);}
    }

    private void setFloatingActionButtonMethod() {

        floatingActionButton = findViewById(R.id.addMenuItemButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogBuilder = new AlertDialog.Builder(MenuItemsActivity.this);
                view = getLayoutInflater().inflate(R.layout.create_new_menu_item, null);
                alertDialogBuilder.setView(view);
                dialog = alertDialogBuilder.create();
                dialog.show();

                createNewMenuItemTextInputLayout = view.findViewById(R.id.createNewMenuItemXml);
                createIdTextInputLayout=view.findViewById(R.id.createIdTextInputLayout);
                createPriceItemTextInputLayout=view.findViewById(R.id.createPriceTextInputLayout);
                createNewMenuItemTextInputLayout.setHint(getResources().getString(R.string.create_item_greek));
                createNewMenuItem = view.findViewById(R.id.createMenuItemXml);
                createMenuItemPrice = view.findViewById(R.id.createPriceXml);

                drinksRadioButton=view.findViewById(R.id.radio_drinks);
                appetizerRadioButton=view.findViewById(R.id.radio_orektika);
                mainDishesRadioButton=view.findViewById(R.id.radio_kuriws_piata);
                cafedesRadioButton =view.findViewById(R.id.radio_kafedes);

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
                        HashMap<String, Object> exampleTry = new HashMap<>();

                        if (!createId.getText().toString().trim().isEmpty()&&
                                !createNewMenuItem.getText().toString().trim().isEmpty()
                                &&!createMenuItemPrice.getText().toString().trim().isEmpty()&&(cafedesRadioButton.isChecked()||drinksRadioButton.isChecked()
                        ||appetizerRadioButton.isChecked()||mainDishesRadioButton.isChecked())) {
                            String typeOfMenuItem="";
                            int typeId=0;
                            if(cafedesRadioButton.isChecked()){
                                typeOfMenuItem=cafedesRadioButton.getText().toString();
                                typeId=3;
                            }
                            else if(drinksRadioButton.isChecked()){
                                typeOfMenuItem=drinksRadioButton.getText().toString();
                                typeId=4;
                            }
                            else if(appetizerRadioButton.isChecked()){
                                typeOfMenuItem=appetizerRadioButton.getText().toString();
                                typeId=1;
                            }
                            else if(mainDishesRadioButton.isChecked()){
                                typeOfMenuItem=mainDishesRadioButton.getText().toString();
                                typeId=2;
                            }
                            MenuItem menuItem = new MenuItem();
                            menuItem.setId(Integer.parseInt(createId.getText().toString()));
                            menuItem.setNameMenuItem(createNewMenuItem.getText().toString());
                            menuItem.setPrice(Double.parseDouble(createMenuItemPrice.getText().toString()));
                            menuItem.setType(typeOfMenuItem);
                            menuItem.setTypeId(typeId);
                            exampleTry.put("id", menuItem.getId());
                            exampleTry.put("nameMenuItem", menuItem.getNameMenuItem());
                            exampleTry.put("price", menuItem.getPrice());
                            exampleTry.put("type",menuItem.getType());
                            exampleTry.put("typeId",menuItem.getTypeId());
                            DocumentReference newTables = db.collection("store").document(storeName).
                                    collection("menuCategory").document(readPreferencesMenuCategory()).
                                    collection("menuItems").document(createNewMenuItem.getText().toString());
                            newTables.set(exampleTry);

                            DocumentReference menuCategoryChangeStatus = db.collection("store").document(storeName).
                                    collection("menuCategory").document(readPreferencesMenuCategory());

                            HashMap<String, Object> exampleTry2 = new HashMap<>();
                            exampleTry2.put("status","occupied");
                            menuCategoryChangeStatus.update(exampleTry2);

                            DocumentReference retailList = db.collection("store").document(storeName).
                                    collection("retailsList").document(menuItem.getNameMenuItem());
                            HashMap<String, Object> example = new HashMap<>();
                            example.put(menuItem.getNameMenuItem(),menuItem.getNameMenuItem());
                            retailList.set(example);

                            startActivity(new Intent(MenuItemsActivity.this, MenuItemsActivity.class));

                            dialog.dismiss();
                            setUpRecyclerView();
                        } else {
                            createNewMenuItemTextInputLayout.setError("Menu item,id and price must not be empty");
                            createIdTextInputLayout.setError("Menu item,id and price must not be empty");
                            createPriceItemTextInputLayout.setError("Menu item,id and price must not be empty");
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

    private void setThePreferencesMenuCategory() {
        String docId;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            docId = bundle.getString("menuCategory");
            SharedPreferences.Editor editor = getSharedPreferences("myStoreName", MODE_PRIVATE).edit();
            editor.remove("menuCategory");
            editor.apply();
            editor.putString("menuCategory", docId);
            editor.apply();
        }

    }

    public String readPreferencesMenuCategory() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String doc = prefs.getString("menuCategory", null);

        return doc;
    }

}
