package com.stefanos.order.TzirosActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import com.stefanos.order.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Objects;


public class TzirosActivityWithFragment extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ArrayList<Fragment> fragmentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tziros_with_fragment);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {

        toolbar = (Toolbar) findViewById(R.id.toolbarTzirosFragment);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.tziros_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pagerTziros);
        tabLayout = (TabLayout) findViewById(R.id.tabTzirosLayout);

        fragmentArrayList = new ArrayList<>();
        Fragment exampleFragment1 = new TzirosFragment();
        fragmentArrayList.add(exampleFragment1);
        Fragment chartFragment=new ChartFragmentTziros();
        fragmentArrayList.add(chartFragment);

        FragmentAdapterTziros pagerAdapter = new FragmentAdapterTziros(getSupportFragmentManager(),
                getApplicationContext(), fragmentArrayList);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(R.string.kathimerina_kouber_greek);
        tabLayout.getTabAt(1).setText(R.string.diagramma_greek);

    }


}
