package com.stefanos.order.XwrisFromTableActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class XwrisAccessFromTableAdapter extends FirestoreRecyclerAdapter<ExtraItem, XwrisAccessFromTableAdapter.XwrisFromTableHolder> {

    public XwrisAccessFromTableAdapter(@NonNull FirestoreRecyclerOptions<ExtraItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final XwrisFromTableHolder xwrisFromTableHolder, int i, @NonNull ExtraItem extraItem) {
       xwrisFromTableHolder.textView.setText(extraItem.getExtra());



    }

    @NonNull
    @Override
    public XwrisFromTableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.xwris_access_from_table_row,parent,false);
        return new XwrisFromTableHolder(view);
    }

    public class XwrisFromTableHolder  extends RecyclerView.ViewHolder {

        TextView textView;

        public XwrisFromTableHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.xwrisAccessFromTableMenuItemXml);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    SharedPreferences prefs = view.getContext().getSharedPreferences("myStoreName", MODE_PRIVATE);
                    String storeName = prefs.getString("name", null);
                    String orofosName = prefs.getString("orofos", null);
                    String tableId=prefs.getString("tableIndividual",null);
                    assert tableId != null;
                    assert orofosName != null;
                    assert storeName != null;
                    final DocumentReference xwrisAdd=db.collection("store").document(storeName).
                            collection("orofoi").document(orofosName).
                            collection("tables").document(tableId).collection(tableId).document(Objects.requireNonNull(prefs.getString("documentId", null)));
                    final String[] string = {""};
                    xwrisAdd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot=task.getResult();
                                string[0] =documentSnapshot.getString("xwris");
                                HashMap<String, Object> example = new HashMap<>();
                                example.put("xwris",string[0]+"\n"+"χωρίς "+textView.getText().toString());
                                xwrisAdd.update(example);

                            }
                        }
                    });

                    new CountDownTimer(500, 50) {

                        @Override
                        public void onTick(long arg0) {
                            // TODO Auto-generated method stub
                            textView.setBackgroundColor(Color.parseColor("#5FE40D"));
                        }

                        @Override
                        public void onFinish() {
                            textView.setBackgroundColor(Color.parseColor("#EEF0F1"));
                        }
                    }.start();


                }
            });
        }
    }
}
