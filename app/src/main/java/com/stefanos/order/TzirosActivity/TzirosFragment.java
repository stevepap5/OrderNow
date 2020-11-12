package com.stefanos.order.TzirosActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stefanos.order.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class TzirosFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    TzirosAdapter tzirosAdapter;

    public TzirosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeTzirosDaiyTziros();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_tziros,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        String storeName=readPreferences();

        CollectionReference tzirosRef=db.collection("store").document(storeName).collection("tziros");
        Query query = tzirosRef.orderBy("tzirosDateDays", Query.Direction.DESCENDING).limit(10);

        tzirosRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if(task.isSuccessful()){
                   tzirosAdapter.notifyDataSetChanged();
               }
            }
        });

        FirestoreRecyclerOptions<TzirosItem> options = new FirestoreRecyclerOptions.Builder<TzirosItem>().
                setQuery(query, TzirosItem.class).build();

        tzirosAdapter = new TzirosAdapter(options);


        recyclerView.setAdapter(tzirosAdapter);
        // Inflate the layout for this fragment
        return view;
    }

    private String readPreferences() {

        SharedPreferences prefs = Objects.requireNonNull(this.getActivity()).getSharedPreferences("myStoreName", MODE_PRIVATE);

        return prefs.getString("name", null);
    }

    @Override
    public void onStart() {
        super.onStart();
        tzirosAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        tzirosAdapter.stopListening();
    }

    private  void  makeTzirosDaiyTziros(){
        CollectionReference tzirosDailyRef=db.collection("store").document(readPreferences()).collection("tzirosDaily");

        tzirosDailyRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    double summ=0;
                    final HashMap[] tzirosArray=new HashMap[1000000];
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    int currentMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1) ;
                    int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) ;
                    int dateDays=currentYear*365+currentMonth*30+currentDay;
                    for(DocumentSnapshot documentSnapshot: Objects.requireNonNull(task.getResult())){


                        if (dateDays-Integer.parseInt(String.valueOf(documentSnapshot.get("currentDateDays")))<30) {
                            if ( tzirosArray[Integer.parseInt(String.valueOf(documentSnapshot.get("currentDateDays")))]==null) {
                                HashMap<Object,Object> hashMap=new HashMap<>();
                                hashMap.put("dayOfTziros",documentSnapshot.get("currentDay"));
                                hashMap.put("monthOfTziros",documentSnapshot.get("currentMonth"));
                                hashMap.put("yearOfTziros",documentSnapshot.get("currentYear"));
                                hashMap.put("tzirosDateDays",documentSnapshot.get("currentDateDays"));
                                hashMap.put("sum",documentSnapshot.get("sum"));
                                tzirosArray[Integer.parseInt(String.valueOf(documentSnapshot.get("currentDateDays")))]=  hashMap;
                            } else {

                                summ=(double)tzirosArray[Integer.parseInt(String.valueOf(documentSnapshot.get("currentDateDays")))].get("sum")
                                        +Double.parseDouble(String.valueOf(documentSnapshot.get("sum")));
                                tzirosArray[Integer.parseInt(String.valueOf(documentSnapshot.get("currentDateDays")))].put("sum",summ);
                            }
                        } else {
                            documentSnapshot.getReference().delete();
                        }
                    }


                    for(int i=tzirosArray.length-1;i>=0;i--){
                        if(tzirosArray[i]!=null){


                                final DocumentReference tziros=db.collection("store").document(readPreferences()).collection("tziros").
                                        document(String.valueOf(tzirosArray[i].get("tzirosDateDays")));

                            final int finalI2 = i;
                            tziros.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        if(task.getResult().exists()){
                                            tziros.update(tzirosArray[finalI2]);
                                        }
                                        else {
                                            tziros.set(tzirosArray[finalI2]);
                                        }
                                    }
                                }
                            });



                        }
                    }
                }
            }
        });


    }
}
