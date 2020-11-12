package com.stefanos.order.KouberActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stefanos.order.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment {

    View view;
    BarChart barChart;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ChartFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ChartFragment newInstance(String param1, String param2) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chart, container, false);
        barChart = view.findViewById(R.id.chart);

        final ArrayList<BarEntry> entriesList = new ArrayList<>();
        String storeName = readPreferences();

        CollectionReference kouberRef = db.collection("store").document(storeName).collection("kouber");
        kouberRef.orderBy("priceKouber").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                   int i=0;
                    for(DocumentSnapshot documentSnapshot: Objects.requireNonNull(task.getResult())){
                        int xVariable=Integer.parseInt(String.valueOf(documentSnapshot.get("quantityKouber")));
                        double yVariable=Double.parseDouble(String.valueOf(documentSnapshot.get("priceKouber")));
                        entriesList.add(new BarEntry((float)xVariable, (float) yVariable));
                        i++;
                    }

                    BarDataSet dataSet = new BarDataSet(entriesList,"label");
                    BarData barData = new BarData(dataSet);
                    barChart.setData(barData);

                    barChart.invalidate();
                }
            }
        });

        return view;
    }

    private String readPreferences() {

        SharedPreferences prefs = Objects.requireNonNull(this.getActivity()).getSharedPreferences("myStoreName", MODE_PRIVATE);

        return prefs.getString("name", null);
    }
}
