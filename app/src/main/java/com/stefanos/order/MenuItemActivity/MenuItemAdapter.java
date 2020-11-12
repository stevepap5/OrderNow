package com.stefanos.order.MenuItemActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stefanos.order.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MenuItemAdapter extends FirestoreRecyclerAdapter<MenuItem, MenuItemAdapter.MenuItemHolder> {

    public MenuItemAdapter(@NonNull FirestoreRecyclerOptions<MenuItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuItemHolder menuItemHolder, int i, @NonNull MenuItem menuItem) {
        menuItemHolder.nameTextView.setText(menuItem.getNameMenuItem());
        menuItemHolder.idTextView.setText(String.valueOf(menuItem.getId()));
        menuItemHolder.priceTextView.setText(String.valueOf(menuItem.getPrice()));
        final int pos=i;
        menuItemHolder.addExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos!=RecyclerView.NO_POSITION) {
                    MenuItemsActivity.showExtraMenuItemsDetails(getSnapshots().getSnapshot(pos),pos);
                }
            }
        });
        menuItemHolder.addXwris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos!=RecyclerView.NO_POSITION) {
                    MenuItemsActivity.showXwrisMenuItemsDetails(getSnapshots().getSnapshot(pos),pos);
                }
            }
        });

    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public MenuItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_row, parent, false);
        return new MenuItemHolder(view);
    }

    public class MenuItemHolder extends RecyclerView.ViewHolder{

        TextView idTextView;
        TextView nameTextView;
        TextView priceTextView;
        Button addExtra;
        Button addXwris;
        public MenuItemHolder(@NonNull View itemView) {
            super(itemView);

            idTextView=itemView.findViewById(R.id.idTextXml);
            nameTextView=itemView.findViewById(R.id.nameMenuItemTextXml);
            priceTextView=itemView.findViewById(R.id.priceTextXml);
            addExtra=itemView.findViewById(R.id.extraToMenuItemXml);
            addXwris=itemView.findViewById(R.id.xwrisToMenuItemXml);
        }
    }
}
