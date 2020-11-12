package com.stefanos.order.KouberActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stefanos.order.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class KouberAdapter extends FirestoreRecyclerAdapter<Kouber, KouberAdapter.KouberHolder> {



    public KouberAdapter(@NonNull FirestoreRecyclerOptions<Kouber> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull KouberHolder kouberHolder, int i, @NonNull Kouber kouber) {

        kouberHolder.quantityKouber.setText(String.valueOf(kouber.getQuantityKouber()));
        kouberHolder.nameKouber.setText(kouber.getNameKouber());
        kouberHolder.priceKouber.setText(String.valueOf(kouber.getPriceKouber()));
    }

    @NonNull
    @Override
    public KouberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kouber_row, parent, false);
        return new KouberHolder(view);
    }

    public class KouberHolder extends RecyclerView.ViewHolder {

        private TextView quantityKouber;
        private TextView nameKouber;
        private TextView priceKouber;
        public KouberHolder(@NonNull View itemView) {
            super(itemView);

            quantityKouber=itemView.findViewById(R.id.quantityKouberXml);
            nameKouber=itemView.findViewById(R.id.nameKouberXml);
            priceKouber=itemView.findViewById(R.id.priceKouberXml);

        }
    }
}
