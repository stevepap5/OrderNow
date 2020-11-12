package com.stefanos.order.DateAndTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.stefanos.order.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class DateAndTimeActivity extends AppCompatActivity {

    private MaterialButton dateButton;
    private MaterialButton timeButton;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_and_time);

        setDateButton();
        setTimeButton();
        toolbar = (Toolbar) findViewById(R.id.toolbarDate);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.time_date_greek);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void setDateButton(){
        dateButton=findViewById(R.id.startDateXml);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar date = Calendar.getInstance();
                mYear = date.get(Calendar.YEAR);
                mMonth = date.get(Calendar.MONTH);
                mDay = date.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog=new DatePickerDialog(DateAndTimeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                        HashMap<String,Object> exampleTry=new HashMap<>();

                        exampleTry.put("day",dayOfMonth);
                        exampleTry.put("month",monthOfYear+1);
                        exampleTry.put("year",year);
                        DocumentReference dateRef=db.collection("store").
                                document(readPreferences()).collection("date").document("dailyDate");
                        dateRef.set(exampleTry);


                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });
    }

    private void setTimeButton(){
        timeButton=findViewById(R.id.startTimeXml);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(DateAndTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                HashMap<String,Object> exampleTry=new HashMap<>();
                                exampleTry.put("minutes",minute);
                                exampleTry.put("hour",hourOfDay);
                                DocumentReference dateRef=db.collection("store").
                                        document(readPreferences()).collection("date").document("time");
                                dateRef.set(exampleTry);



                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }

    private String readPreferences(){

        SharedPreferences prefs = getSharedPreferences("myStoreName", MODE_PRIVATE);
        String storeName=prefs.getString("name",null);

        return storeName;
    }
}
