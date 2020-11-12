package com.stefanos.order.TablesActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.stefanos.order.KouberActivity.FragmentAdapterKouber;
import com.stefanos.order.MenuItemActivity.MenuItemsActivity;
import com.stefanos.order.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class TablesActivityWithFragment extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    private static Object mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables_with_fragment);

        setUpMethod();

    }

    private void setUpMethod() {
        mContext = this;
        toolbar = (Toolbar) findViewById(R.id.toolbarTables);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.tables_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        String storeName = readPreferences();
        final CollectionReference tabsName = db.collection("store").document(storeName).
                collection("orofoi");
        Query query = tabsName.orderBy("priority");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    ArrayList<String> tabTitles = new ArrayList<>();
                    ArrayList<Fragment> fragments = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("orofoiChecked", true);
                        bundle.putString("orofos", (String) documentSnapshot.get("orofoi"));
                        Fragment exampleFragment = new TablesFragment();
                        exampleFragment.setArguments(bundle);
                        fragments.add(exampleFragment);
                        tabTitles.add((String) documentSnapshot.get("orofoi"));

                    }

                    FragmentAdapterKouber pagerAdapter = new FragmentAdapterKouber(getSupportFragmentManager(), getApplicationContext(), fragments);
                    viewPager.setAdapter(pagerAdapter);

                    tabLayout.setupWithViewPager(viewPager);

                    for (int j = 0; j < tabTitles.size(); j++) {
                        tabLayout.getTabAt(j).setText(tabTitles.get(j));
                    }
                }
            }
        });

    }




    private String readPreferences() {

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName = prefs.getString("name", null);

        return storeName;
    }

}
