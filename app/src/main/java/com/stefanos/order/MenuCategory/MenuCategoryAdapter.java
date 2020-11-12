package com.stefanos.order.MenuCategory;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.stefanos.order.R;
import com.stefanos.order.TablesActivity.Table;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class MenuCategoryAdapter extends FirestoreRecyclerAdapter<Table, MenuCategoryAdapter.MenuCategoryHolder> {


    public MenuCategoryAdapter(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuCategoryHolder menuCategoryHolder, int i, @NonNull Table table) {

        menuCategoryHolder.cardView.setBackgroundColor(Color.parseColor("#EEF0F1"));
        menuCategoryHolder.textView.setText(table.getTable());

//        //xarris
        final int pos = i;
        menuCategoryHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pos!=RecyclerView.NO_POSITION){
                    MenuCategoryActivity.showMenuCategoryDetails(getSnapshots().getSnapshot(pos),pos);
                }
            }
        });
    }

    @NonNull
    @Override
    public MenuCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_category_row,parent,false);
        return new MenuCategoryHolder(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class MenuCategoryHolder extends RecyclerView.ViewHolder{

        TextView textView;
        Button button;
        CardView cardView;

        public MenuCategoryHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tableTextXml);
            cardView=itemView.findViewById(R.id.tableRowCardView);
            button=itemView.findViewById(R.id.emptyTable);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                    LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                    View viewDialog = layoutInflater.inflate(R.layout.create_delete_dialog, null);
                    alertDialogBuilder.setView(viewDialog);
                    final AlertDialog dialog = alertDialogBuilder.create();
                    dialog.show();

                    TextView titlePaid = viewDialog.findViewById(R.id.totalSumDelete);
                    titlePaid.setText(R.string.do_you_want_to_empty_menu_category_greek);
                    Button yesButton = viewDialog.findViewById(R.id.yesButtonPaidDialogDelete);
                    Button noButton = viewDialog.findViewById(R.id.noButtonPaidDialogDelete);
                    noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences prefs = view.getContext().getSharedPreferences("myStoreName", MODE_PRIVATE);
                            final String storeName = prefs.getString("name", null);
                            final FirebaseFirestore db = FirebaseFirestore.getInstance();
                            assert storeName != null;
                            CollectionReference changeStatus = db.collection("store").
                                    document(storeName).collection("menuCategory").
                                    document(textView.getText().toString()).collection("menuItems");
                            changeStatus.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    if (queryDocumentSnapshots!=null) {
                                        for(DocumentSnapshot document:queryDocumentSnapshots){
                                            document.getReference().delete();
                                        }
                                        DocumentReference documentReference=db.collection("store").
                                                document(storeName).collection("menuCategory").
                                                document(textView.getText().toString());
                                        HashMap<String, Object> example2 = new HashMap<>();
                                        example2.put("status", "");
                                        documentReference.update(example2);
                                    }
                                }
                            });

                            dialog.dismiss();
                        }
                    });

                }
            });
        }
        }
    }


