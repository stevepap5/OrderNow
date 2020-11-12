package com.stefanos.order.ExtraActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stefanos.order.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ExtraAdapter extends FirestoreRecyclerAdapter<ExtraItem, ExtraAdapter.ExtraXwrisHolder> {

    @NonNull
    @Override
    public ExtraXwrisHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.extra_row,parent,false);
        return new ExtraXwrisHolder(view);
    }

    public ExtraAdapter(@NonNull FirestoreRecyclerOptions<ExtraItem> options) {
        super(options);
    }



    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @Override
    protected void onBindViewHolder(@NonNull ExtraXwrisHolder extraXwrisHolder, int i, @NonNull ExtraItem extraItem) {
        extraXwrisHolder.textView.setText(extraItem.getExtra());

    }

    public class ExtraXwrisHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ExtraXwrisHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.extraMenuItemXml);


        }
    }
}
