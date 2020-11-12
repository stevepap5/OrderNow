package com.stefanos.order.TablesActivity;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.stefanos.order.R;
import com.stefanos.order.TableIndividualActivity.TableIndividualActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class TableAdapter extends FirestoreRecyclerAdapter<Table, TableAdapter.TableHolder> {


    public TableAdapter(FirestoreRecyclerOptions<Table> options) {
        super(options);


    }

    @Override
    protected void onBindViewHolder(TableHolder tableHolder, int i, final Table table) {

        tableHolder.textView.setText(table.getTable());
        tableHolder.cardView.setBackgroundColor(Color.parseColor("#EEF0F1"));

        if (table.getStatus() != null) {
            if (table.getStatus().equals("notpaid")) {
                tableHolder.cardView.setBackgroundColor(Color.RED);
            }
        }



        final int pos = i;
        tableHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pos != RecyclerView.NO_POSITION) {

                    Intent intent=new Intent(view.getContext(),TableIndividualActivity.class);
                    intent.putExtra("orofos",table.getOrofosName());
                    intent.putExtra("tableIndividual",table.getTable());
                    view.getContext().startActivity(intent);
                }
            }
        });


    }

    @NonNull
    @Override
    public TableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row, parent, false);
        return new TableHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }


    class TableHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textView;
//        Button button;


        public TableHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tableTextXml);
            cardView = itemView.findViewById(R.id.tableRowCardView);
//            button = itemView.findViewById(R.id.emptyTable);

//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
//                    LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
//                    View viewDialog = layoutInflater.inflate(R.layout.create_paid_dialog, null);
//                    alertDialogBuilder.setView(viewDialog);
//                    final AlertDialog dialog = alertDialogBuilder.create();
//                    dialog.show();
//
//                    TextView titlePaid = viewDialog.findViewById(R.id.paidTitle);
//                    titlePaid.setText(R.string.do_you_want_to_empty_greek);
//                    Button yesButton = viewDialog.findViewById(R.id.yesButtonPaidDialog);
//                    Button noButton = viewDialog.findViewById(R.id.noButtonPaidDialog);
//                    noButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            dialog.dismiss();
//                        }
//                    });
//                    yesButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            SharedPreferences prefs = view.getContext().getSharedPreferences("myStoreName", MODE_PRIVATE);
//                            final String storeName = prefs.getString("name", null);
//                            final FirebaseFirestore db = FirebaseFirestore.getInstance();
//                            assert storeName != null;
//                            CollectionReference changeStatus = db.collection("store").
//                                    document(storeName).collection("tables").
//                                    document(textView.getText().toString()).collection(textView.getText().toString());
//                           changeStatus.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                               @Override
//                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                   for(DocumentSnapshot document: Objects.requireNonNull(task.getResult())){
//                                       document.getReference().delete();
//                                   }
//                                   DocumentReference documentReference=db.collection("store").
//                                           document(storeName).collection("tables").
//                                           document(textView.getText().toString());
//                                   HashMap<String, Object> example2 = new HashMap<>();
//                                   example2.put("status", "");
//                                   documentReference.update(example2);
//                               }
//                           });
//
//                            dialog.dismiss();
//                        }
//                    });
//
//                }
//            });
    }


    }

}
