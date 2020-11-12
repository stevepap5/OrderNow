package com.stefanos.order.KouberActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.stefanos.order.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KouberActivityWithFragment extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kouber);

        setUpView();

    }

    private void setUpView(){

        toolbar = (Toolbar) findViewById(R.id.toolbarKouber);
        makeKouberDaiyKouber();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.kouber_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewPager = (ViewPager) findViewById(R.id.pagerKouber);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

                 ArrayList<Fragment> fragmentArrayList=new ArrayList<>();

                  for (int i=0;i<5;i++) {
                      Fragment exampleFragment = new KouberFragment();
                      Bundle bundle = new Bundle();

                      bundle.putString("number", String.valueOf(i));
                      exampleFragment.setArguments(bundle);
                      fragmentArrayList.add(exampleFragment);

                  }
                  FragmentAdapterKouber pagerAdapter = new FragmentAdapterKouber(getSupportFragmentManager(),
                          getApplicationContext(), fragmentArrayList);
                  viewPager.setAdapter(pagerAdapter);

                  tabLayout.setupWithViewPager(viewPager);
                  for(int i=0;i<5;i++){
                      DocumentReference documentReference=db.collection("store").document(readPreferences()).collection("kouber").
                              document(String.valueOf(i)).collection(String.valueOf(i)).document(String.valueOf(0));

                      final int finalI = i;
                      documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                          @Override
                          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                              if(task.isSuccessful()){
                                  DocumentSnapshot documentSnapshot=task.getResult();
                                  if (documentSnapshot.exists()) {
                                      tabLayout.getTabAt(finalI).setText(documentSnapshot.get("currentDay")+"/"+
                                              documentSnapshot.get("currentMonth")+"/"+documentSnapshot.get("currentYear"));
                                  }
                              }
                          }
                      });
                  }



    }

    private String readPreferences() {

        SharedPreferences prefs = Objects.requireNonNull(KouberActivityWithFragment.this).getSharedPreferences("myStoreName", MODE_PRIVATE);

        return prefs.getString("name", null);
    }

    private void makeKouberDaiyKouber() {
        CollectionReference kouberDailyRef = db.collection("store").document(readPreferences()).collection("kouberDaily");

        kouberDailyRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    final HashMap<Object, Object>[] kouberArray = new HashMap[1000000];
                    int j = 0;
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    int currentMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1) ;
                    int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) ;
                    int dateDays=currentYear*365+currentMonth*30+currentDay;
                    for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        j = Integer.parseInt(String.valueOf(documentSnapshot.get("currentDateDays")));

                        if (dateDays-j<30) {
                            if (kouberArray[j] == null) {

                                kouberArray[j] = new HashMap<>();

                                HashMap<Object, Object> hashMap = new HashMap<>();
                                hashMap.put("currentDay", documentSnapshot.get("currentDay"));
                                hashMap.put("currentMonth", documentSnapshot.get("currentMonth"));
                                hashMap.put("currentYear", documentSnapshot.get("currentYear"));
                                hashMap.put("currentDateDays", documentSnapshot.get("currentDateDays"));
                                hashMap.put("nameKouber", documentSnapshot.get("nameKouber"));
                                hashMap.put("priceKouber", documentSnapshot.get("priceKouber"));
                                hashMap.put("quantityKouber", documentSnapshot.get("quantityKouber"));
                                String name = String.valueOf(documentSnapshot.get("nameKouber"));
                                kouberArray[j].put(name, hashMap);

                            } else {

                                if (kouberArray[j].containsKey(String.valueOf(documentSnapshot.get("nameKouber")))) {

                                    HashMap<Object, Object> newHashMap = new HashMap<>();
                                    newHashMap = (HashMap<Object, Object>) kouberArray[j].get(String.valueOf(documentSnapshot.get("nameKouber")));
                                    double price = Double.parseDouble(String.valueOf(newHashMap.get("priceKouber"))) + Double.parseDouble(String.valueOf(documentSnapshot.get("priceKouber")));
                                    newHashMap.put("priceKouber", price);
                                    Integer quantity = Integer.parseInt(String.valueOf(newHashMap.get("quantityKouber"))) + Integer.parseInt(String.valueOf(documentSnapshot.get("quantityKouber")));
                                    newHashMap.put("quantityKouber", quantity);
                                } else {
                                    HashMap<Object, Object> hashMap = new HashMap<>();
                                    hashMap.put("currentDay", documentSnapshot.get("currentDay"));
                                    hashMap.put("currentMonth", documentSnapshot.get("currentMonth"));
                                    hashMap.put("currentYear", documentSnapshot.get("currentYear"));
                                    hashMap.put("currentDateDays", documentSnapshot.get("currentDateDays"));
                                    hashMap.put("nameKouber", documentSnapshot.get("nameKouber"));
                                    hashMap.put("priceKouber", documentSnapshot.get("priceKouber"));
                                    hashMap.put("quantityKouber", documentSnapshot.get("quantityKouber"));
                                    String name = String.valueOf(documentSnapshot.get("nameKouber"));
                                    kouberArray[j].put(name, hashMap);
                                }


                            }
                        } else {
                            documentSnapshot.getReference().delete();
                        }
                    }
                    int count=0;
                    for (int i = kouberArray.length - 1; i >= 0; i--) {
                        if (kouberArray[i] != null) {
                            Log.i("kouberArray",String.valueOf(kouberArray[i]));
                            int k=0;
                            for (Map.Entry entry : kouberArray[i].entrySet()) {

                                DocumentReference kouber = db.collection("store").document(readPreferences()).collection("kouber").
                                        document(String.valueOf(count)).collection(String.valueOf(count)).document(String.valueOf(k));
                                HashMap<Object, Object> hashMap = new HashMap<>();

                                hashMap = (HashMap<Object, Object>) entry.getValue();
                                kouber.set(hashMap);
                                k++;
                            }

                            count++;
                        }

                        if(count>4){
                            break;
                        }
                    }


                }
            }
        });


    }
}
