package com.stefanos.order.OrofoiActivity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.stefanos.order.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class OrofoiAdapter  extends FirestoreRecyclerAdapter<OrofoiItem, OrofoiAdapter.OrofoiHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public OrofoiAdapter(@NonNull FirestoreRecyclerOptions<OrofoiItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrofoiHolder orofoiHolder, int i, @NonNull OrofoiItem orofoiItem) {
        orofoiHolder.orofoi.setText(orofoiItem.getOrofoi());
        orofoiHolder.cardView.setBackgroundColor(Color.parseColor("#EEF0F1"));
    }

    @NonNull
    @Override
    public OrofoiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orofoi_row, parent, false);
        return new OrofoiAdapter.OrofoiHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public static class  OrofoiHolder extends RecyclerView.ViewHolder {

       static TextView orofoi;
        CardView cardView;
        public OrofoiHolder(@NonNull View itemView) {
            super(itemView);
            orofoi=itemView.findViewById(R.id.orofoiRowXml);
            cardView=itemView.findViewById(R.id.orofoiCardView);
        }
    }
}
