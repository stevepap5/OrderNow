package com.stefanos.order.MenuCategoryFromTableActivity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.stefanos.order.R;
import com.stefanos.order.TablesActivity.Table;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MenuCategoryAccessFromTableAdapter extends FirestoreRecyclerAdapter<Table, MenuCategoryAccessFromTableAdapter.
        MenuCategoryAccessFromTableHolder> {

    public MenuCategoryAccessFromTableAdapter(@NonNull FirestoreRecyclerOptions<Table> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuCategoryAccessFromTableHolder menuCategoryAccessFromTableHolder, int i, @NonNull Table table) {

            menuCategoryAccessFromTableHolder.textView.setText(table.getTable());
            menuCategoryAccessFromTableHolder.cardView.setBackgroundColor(Color.parseColor("#EEF0F1"));

//        //xarris
        final int pos = i;
        menuCategoryAccessFromTableHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pos!=RecyclerView.NO_POSITION){
                    MenuCategoryAccessFromTableActivity.showMenuCategoryDetails(getSnapshots().getSnapshot(pos),pos);
                }
            }
        });
    }

    @NonNull
    @Override
    public MenuCategoryAccessFromTableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_category_row_from_table,parent,false);
        return new MenuCategoryAccessFromTableHolder(view);
    }

    public class MenuCategoryAccessFromTableHolder extends RecyclerView.ViewHolder {

        TextView textView;
        CardView cardView;
        public MenuCategoryAccessFromTableHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.tableRowCardView);
            textView=itemView.findViewById(R.id.tableTextXml);
        }
    }
}
