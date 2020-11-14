package com.stefanos.order.TzirosActivity;

import android.content.SharedPreferences;
import android.drm.DrmManagerClient;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.stefanos.order.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
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
 * Use the {@link ChartFragmentTziros#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragmentTziros extends Fragment {

    private View view;
    private BarChart barChart;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TzirosAdapter tzirosAdapter;
    public ChartFragmentTziros() {
        // Required empty public constructor
    }


    public static ChartFragmentTziros newInstance(String param1, String param2) {
        ChartFragmentTziros fragment = new ChartFragmentTziros();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_chart_tziros, container, false);
        barChart = view.findViewById(R.id.chart);




        String storeName = readPreferences();

        CollectionReference tzirosRef = db.collection("store").document(storeName).collection("tziros");


        tzirosRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot task, @Nullable FirebaseFirestoreException error) {
                ArrayList<String> xList=new ArrayList<>();
                ArrayList<Float> yList=new ArrayList<>();

                for(QueryDocumentSnapshot documentSnapshot: Objects.requireNonNull(task)){

                    if (documentSnapshot!=null) {
                        double yVariable=Double.parseDouble(String.valueOf(documentSnapshot.get("sum")));
                        String xVariable=String.valueOf(documentSnapshot.get("dayOfTziros"))+"/"+String.valueOf(documentSnapshot.get("monthOfTziros"));

                        xList.add(xVariable);
                        yList.add((float) yVariable);
                    }

                }

                final String[] dateVar = new String[xList.size()];
                ArrayList<BarEntry> yEntrys = new ArrayList<>();
                for(int i = 0; i < yList.size(); i++){

                    yEntrys.add(new BarEntry( i,yList.get(yList.size()-1-i)));
                    dateVar[i]=xList.get(yList.size()-1-i);
                    Log.i("ydata", String.valueOf(yList.get(i)));
                }


                BarDataSet barDataSet=new BarDataSet(yEntrys,"Τζίρος");

                XAxis xAxis = barChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextSize(15f);
                xAxis.setGranularity(1f);
                xAxis.setDrawAxisLine(true);
                xAxis.setDrawGridLines(false);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return dateVar[(int)value];
                    }
                });

                BarData barData = new BarData(barDataSet);


                barChart.setData(barData);
                barChart.invalidate();
            }


        });
        return view;
    }



    private String readPreferences() {

        SharedPreferences prefs = Objects.requireNonNull(this.getActivity()).getSharedPreferences("myStoreName", MODE_PRIVATE);

        return prefs.getString("name", null);
    }
}
