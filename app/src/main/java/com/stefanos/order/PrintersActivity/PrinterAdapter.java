package com.stefanos.order.PrintersActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.stefanos.order.R;
import com.stefanos.order.SQLiteDatabaseForPrinters.IpAdressPrinter;

public class PrinterAdapter extends FirestoreRecyclerAdapter<IpAdressPrinter, PrinterAdapter.PrinterViewHolder> {


    public PrinterAdapter(@NonNull FirestoreRecyclerOptions<IpAdressPrinter> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PrinterViewHolder printerViewHolder, int i, @NonNull IpAdressPrinter ipAdressPrinter) {

        printerViewHolder.printerIpAddressTextView.setText(ipAdressPrinter.getIpAdress());
        printerViewHolder.portTextView.setText(ipAdressPrinter.getPort());
    }

    @NonNull
    @Override
    public PrinterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.printer_item_row,parent,false);
        return new PrinterViewHolder(view);
    }

    public void deleteItem(int position){

        getSnapshots().getSnapshot(position).getReference().delete();

    }

    public class PrinterViewHolder extends RecyclerView.ViewHolder{

        TextView printerIpAddressTextView;
        TextView portTextView;
        public PrinterViewHolder(@NonNull View itemView) {
            super(itemView);

            printerIpAddressTextView=itemView.findViewById(R.id.printerIpAddressXml);
            portTextView=itemView.findViewById(R.id.portXml);
        }
    }
}
