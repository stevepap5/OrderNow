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

                if (pos != RecyclerView.NO_POSITION) {
                    MenuCategoryActivity.showMenuCategoryDetails(getSnapshots().getSnapshot(pos), pos);
                }
            }
        });
    }

    @NonNull
    @Override
    public MenuCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_category_row, parent, false);
        return new MenuCategoryHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public static class MenuCategoryHolder extends RecyclerView.ViewHolder {

        static TextView textView;

        CardView cardView;

        public MenuCategoryHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tableTextXml);
            cardView = itemView.findViewById(R.id.tableRowCardView);
          
        }
    }
}


