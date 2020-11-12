package com.stefanos.order.TableIndividualActivity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.stefanos.order.OrderItem.OrderItem;
import com.stefanos.order.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class TableIndividualAdapter extends FirestoreRecyclerAdapter<OrderItem, TableIndividualAdapter.OrderItemHolder> {

    FirestoreRecyclerOptions<OrderItem> options;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TableIndividualAdapter(@NonNull FirestoreRecyclerOptions<OrderItem> options) {
        super(options);
        this.options = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull final OrderItemHolder orderItemHolder, final int position, @NonNull final OrderItem orderItem) {


        orderItemHolder.quantity.setText(String.valueOf(orderItem.getQuantity()));
        orderItemHolder.menuItemName.setText(orderItem.getNameItem());
        orderItemHolder.menuItemPrice.setText(String.valueOf(orderItem.getPriceItem()));
        orderItemHolder.extra.setText(orderItem.getExtra());
        orderItemHolder.xwris.setText(orderItem.getXwris());
        if(orderItem.isEnabledPartialPayment()){
            orderItemHolder.constraintLayout.setBackgroundColor(Color.parseColor("#5FE40D"));
        }else{
            orderItemHolder.constraintLayout.setBackgroundColor(Color.parseColor("#EEF0F1"));
        }
        orderItemHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(orderItem.isEnabledPartialPayment()){
                   orderItemHolder.constraintLayout.setBackgroundColor(Color.parseColor("#EEF0F1"));
                   HashMap<String,Object> example=new HashMap<>();
                   example.put("enabledPartialPayment",false);
                   DocumentReference documentReference=getSnapshots().getSnapshot(position).getReference();
                   documentReference.update(example);
               }else{
                   HashMap<String,Object> example=new HashMap<>();
                   example.put("enabledPartialPayment",true);
                   DocumentReference documentReference=getSnapshots().getSnapshot(position).getReference();
                   documentReference.update(example);
                   orderItemHolder.constraintLayout.setBackgroundColor(Color.parseColor("#5FE40D"));
               }
            }
        });


    }

    @NonNull
    @Override
    public OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_table_row, parent, false);
        return new OrderItemHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    public class OrderItemHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView quantity;
        TextView menuItemName;
        TextView menuItemPrice;
        TextView extra;
        TextView xwris;
        ConstraintLayout constraintLayout;


        public OrderItemHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            quantity = itemView.findViewById(R.id.quantityXml);
            menuItemName = itemView.findViewById(R.id.nameMenuItemXml);
            menuItemPrice = itemView.findViewById(R.id.priceXml);
            extra = itemView.findViewById(R.id.extraXml);
            xwris = itemView.findViewById(R.id.xwrisXml);
            constraintLayout = itemView.findViewById(R.id.constraintLayoutIndividualTableRow);




        }
    }


}
