package com.cmov.railwaysportugalback.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.cmov.railwaysportugalback.ApiData;
import com.cmov.railwaysportugalback.R;
import com.cmov.railwaysportugalback.TripInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class MenuActivity extends AppCompatActivity {

    protected TripInfo[]tripsInfo;
    protected Spinner tripSpinner;
    protected Spinner timesSpinner;
    protected Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tripSpinner = (Spinner) findViewById(R.id.trip_spinner);
        timesSpinner = (Spinner) findViewById(R.id.times_spinner);
        submitButton = (Button) findViewById(R.id.button);

        // Get

        Gson gson = new Gson();
        tripsInfo = gson.fromJson(ApiData.tripsInfo, TripInfo[].class);

        // Make Trip Spinner

        ArrayList<String> tripsName = new ArrayList<>();
        for (TripInfo tripInfo : tripsInfo) tripsName.add(tripInfo.getName());

        ArrayAdapter<String> trips = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tripsName);
        trips.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tripSpinner.setAdapter(trips);
        tripSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> times = new ArrayList<>();
                for (Integer[] tripTimes : getSelectedTripInfo().getTimes()) {
                    Integer departure = tripTimes[0];
                    times.add(String.format("%02d", departure / 60) + ":" + String.format("%02d", departure % 60));
                }

                ArrayAdapter<String> timesAdapter = new ArrayAdapter<>(MenuActivity.this, android.R.layout.simple_spinner_dropdown_item, times);
                timesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                timesSpinner.setAdapter(timesAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        timesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TripInfo selectedTrip = getSelectedTripInfo();
                Integer[] time = getSelectedTime();

                Intent i = new Intent(MenuActivity.this, MainActivity.class);
                i.putExtra("name", selectedTrip.getName());
                i.putExtra("lineNumber", selectedTrip.getLineNumber());
                i.putExtra("departure", time[0]);
                i.putExtra("capacity", time[1]);
                startActivity(i);
            }
        });
    }

    protected TripInfo getSelectedTripInfo()
    {
        return tripsInfo[(int) tripSpinner.getSelectedItemId()];
    }

    protected Integer[] getSelectedTime()
    {
        TripInfo selectedTrip = getSelectedTripInfo();
        return selectedTrip.getTimes()[(int)timesSpinner.getSelectedItemId()];
    }

}
