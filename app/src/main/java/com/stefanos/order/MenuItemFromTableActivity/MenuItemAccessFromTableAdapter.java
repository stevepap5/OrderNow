package com.stefanos.order.MenuItemFromTableActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.stefanos.order.MenuItemActivity.MenuItem;
import com.stefanos.order.OrderItem.OrderItem;
import com.stefanos.order.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


public class MenuItemAccessFromTableAdapter extends FirestoreRecyclerAdapter<MenuItem, MenuItemAccessFromTableAdapter.MenuItemTableHolder> {



    public MenuItemAccessFromTableAdapter(@NonNull FirestoreRecyclerOptions<MenuItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MenuItemTableHolder menuItemHolder, int i, @NonNull MenuItem menuItem) {
        menuItemHolder.nameTextView.setText(menuItem.getNameMenuItem());
        menuItemHolder.idTextView.setText(String.valueOf(menuItem.getId()));
        menuItemHolder.priceTextView.setText(String.valueOf(menuItem.getPrice()));

        if(menuItem.getType().equals(menuItemHolder.drinksRadioButton.getText().toString())){
            menuItemHolder.drinksRadioButton.toggle();
        }
        if(menuItem.getType().equals(menuItemHolder.cafedesRadioButton.getText().toString())){
            menuItemHolder.cafedesRadioButton.toggle();
        }
        if(menuItem.getType().equals(menuItemHolder.orektikaRadioButton.getText().toString())){
            menuItemHolder.orektikaRadioButton.toggle();
        }
        if(menuItem.getType().equals(menuItemHolder.mainDishesRadioButton.getText().toString())){
            menuItemHolder.mainDishesRadioButton.toggle();
        }
        final int pos=i;
        menuItemHolder.extraToMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos!=RecyclerView.NO_POSITION) {
                    MenuItemsAccessFromTableActivity.showExtraMenuItemsDetails(getSnapshots().getSnapshot(pos),pos);
                }
            }
        });
       menuItemHolder.xwrisToMenuItem.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (pos!=RecyclerView.NO_POSITION) {
                   MenuItemsAccessFromTableActivity.showXwrisMenuItemsDetails(getSnapshots().getSnapshot(pos),pos);
               }
           }
       });


    }

    @NonNull
    @Override
    public MenuItemTableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_row_access_from_table, parent, false);
        return new MenuItemTableHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public class MenuItemTableHolder extends RecyclerView.ViewHolder {

        TextView idTextView;
        TextView nameTextView;
        TextView priceTextView;
        Button extraToMenuItem;
        Button xwrisToMenuItem;
        RadioButton cafedesRadioButton;
        RadioButton drinksRadioButton;
        RadioButton orektikaRadioButton;
        RadioButton mainDishesRadioButton;
        CardView cardView;
        ConstraintLayout constraintLayout;


        public MenuItemTableHolder(@NonNull final View itemView) {
            super(itemView);

            idTextView = itemView.findViewById(R.id.idTextFromTableXml);
            nameTextView = itemView.findViewById(R.id.nameMenuItemFromTableTextXml);
            priceTextView = itemView.findViewById(R.id.priceTextXml);
            extraToMenuItem = itemView.findViewById(R.id.extraToMenuItemXml);
            xwrisToMenuItem=itemView.findViewById(R.id.xwrisToMenuItemXml);
            cardView=itemView.findViewById(R.id.kouberCardView);
            constraintLayout=itemView.findViewById(R.id.constaintLayout);
            cafedesRadioButton=itemView.findViewById(R.id.radioKafedesAccessFromTable);
            drinksRadioButton=itemView.findViewById(R.id.radioDrinksAccessFromTable);
            orektikaRadioButton=itemView.findViewById(R.id.radioOrektikaAccessFromTable);
            mainDishesRadioButton=itemView.findViewById(R.id.radioKuriwsPiataAccessFromTable);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences prefs = view.getContext().getSharedPreferences("myStoreName", MODE_PRIVATE);
                    String tableId= prefs.getString("tableIndividual", null);
                    String storeName = prefs.getString("name", null);
                    String orofosName = prefs.getString("orofos", null);
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
                    else if(orektikaRadioButton.isChecked()){
                        typeOfMenuItem=orektikaRadioButton.getText().toString();
                        typeId=1;
                    }
                    else if(mainDishesRadioButton.isChecked()){
                        typeOfMenuItem=mainDishesRadioButton.getText().toString();
                        typeId=2;
                    }

                    HashMap<String, Object> example = new HashMap<>();
                    OrderItem orderItem=new OrderItem();
                    orderItem.setNameItem(nameTextView.getText().toString());
                    orderItem.setQuantity(1);
                    orderItem.setExtra("");
                    orderItem.setXwris("");
                    orderItem.setItemID(typeId);
                    orderItem.setTypeItem(typeOfMenuItem);
                    orderItem.setEnabledPartialPayment(false);
                    orderItem.setPriceItem(Double.parseDouble(priceTextView.getText().toString()));
                    example.put("nameItem",orderItem.getNameItem());
                    example.put("quantity",orderItem.getQuantity());
                    example.put("priceItem",orderItem.getPriceItem());
                    example.put("extra",orderItem.getExtra());
                    example.put("xwris",orderItem.getXwris());
                    example.put("itemID",orderItem.getItemID());
                    example.put("typeItem",orderItem.getTypeItem());
                    example.put("enabledPartialPayment",orderItem.isEnabledPartialPayment());
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    assert storeName != null;
                    assert tableId != null;
                    assert orofosName != null;
                    DocumentReference individualOrder = db.collection("store").
                            document(storeName).
                            collection("orofoi").document(orofosName).
                            collection("tables").
                            document(tableId).
                            collection(tableId).document();
                    individualOrder.set(example);
                    SharedPreferences.Editor editor = view.getContext().getSharedPreferences("myStoreName", MODE_PRIVATE).edit();

                    editor.putString("documentId",individualOrder.getId());
                    editor.apply();


                    HashMap<String, Object> example2 = new HashMap<>();
                    example2.put("status", "notpaid");

                    DocumentReference changeStatus = db.collection("store").
                            document(storeName). collection("orofoi").document(orofosName).
                            collection("tables").document(tableId);
                    changeStatus.update(example2);
                    new CountDownTimer(500, 50) {

                        @Override
                        public void onTick(long arg0) {
                            // TODO Auto-generated method stub
                           constraintLayout.setBackgroundColor(Color.parseColor("#5FE40D"));
                        }

                        @Override
                        public void onFinish() {
                            constraintLayout.setBackgroundColor(Color.parseColor("#EEF0F1"));
                        }
                    }.start();

                }
            });


        }
    }


}
