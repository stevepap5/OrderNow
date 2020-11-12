package com.stefanos.order.XwrisActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stefanos.order.ExtraActivity.ExtraItem;
import com.stefanos.order.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class XwrisAdapter extends FirestoreRecyclerAdapter<ExtraItem, XwrisAdapter.XwrisHolder> {

    public XwrisAdapter(@NonNull FirestoreRecyclerOptions<ExtraItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull XwrisAdapter.XwrisHolder xwrisHolder, int i, @NonNull ExtraItem extraItem) {
        xwrisHolder.textView.setText(extraItem.getExtra());
    }

    @NonNull
    @Override
    public XwrisAdapter.XwrisHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.xwris_row,parent,false);
        return new XwrisAdapter.XwrisHolder(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public class XwrisHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public XwrisHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.xwrisMenuItemXml);
        }
    }
}
