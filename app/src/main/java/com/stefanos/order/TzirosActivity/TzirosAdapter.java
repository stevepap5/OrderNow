package com.stefanos.order.TzirosActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stefanos.order.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class TzirosAdapter  extends FirestoreRecyclerAdapter<TzirosItem, TzirosAdapter.TzirosHolder> {

    public TzirosAdapter(@NonNull FirestoreRecyclerOptions<TzirosItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TzirosHolder tzirosHolder, int i, @NonNull TzirosItem tzirosItem) {
        tzirosHolder.tziros.setText(String.valueOf(tzirosItem.getSum()));
        tzirosHolder.date.setText(String.valueOf(tzirosItem.getDayOfTziros())+"/"+String.valueOf(tzirosItem.getMonthOfTziros())
                +"/"+String.valueOf(tzirosItem.getYearOfTziros()));
    }

    @NonNull
    @Override
    public TzirosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tziros_row, parent, false);
        return new TzirosHolder(view);
    }

    public class TzirosHolder  extends RecyclerView.ViewHolder {

        TextView date;
        TextView tziros;
        public TzirosHolder(@NonNull View itemView) {
            super(itemView);

            date=itemView.findViewById(R.id.dateTzirosXml);
            tziros=itemView.findViewById(R.id.tzirosXml);
        }
    }
}
