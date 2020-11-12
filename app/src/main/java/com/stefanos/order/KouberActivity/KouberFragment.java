package com.stefanos.order.KouberActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stefanos.order.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class KouberFragment extends Fragment {

    RecyclerView recyclerView;
    View view;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    KouberAdapter kouberAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.kouber_fragments, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        String storeName = readPreferences();



        if (getArguments()!=null) {
            Query query = null;
            String getDataOrder=getArguments().getString("number");

            CollectionReference kouberRef = db.collection("store").document(storeName).collection("kouber").
                    document(getDataOrder).collection(getDataOrder);
            query = kouberRef;
            FirestoreRecyclerOptions<Kouber> options = new FirestoreRecyclerOptions.Builder<Kouber>().
                    setQuery(query, Kouber.class).build();
            kouberAdapter = new KouberAdapter(options);
            recyclerView.setAdapter(kouberAdapter);


        }


        return view;
    }

    private String readPreferences() {

        SharedPreferences prefs = Objects.requireNonNull(this.getActivity()).getSharedPreferences("myStoreName", MODE_PRIVATE);

        return prefs.getString("name", null);
    }

    @Override
    public void onStart() {
        super.onStart();
        kouberAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        kouberAdapter.stopListening();
    }


}
