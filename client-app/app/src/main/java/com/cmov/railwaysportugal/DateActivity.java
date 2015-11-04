package com.cmov.railwaysportugal;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class DateActivity extends Activity {

    String token;
    String arrivalstation;
    String departurestation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        Bundle extras = getIntent().getExtras();
        DatePicker datePicker = (DatePicker) findViewById(R.id.dateresult);
        datePicker.setMinDate(System.currentTimeMillis());

        extras.getString("TOKEN", token);
        extras.getString("ARRIVAL", arrivalstation);
        extras.getString("DEPARTURE", departurestation);


        Button mSubmitAll = (Button) findViewById(R.id.submitall);
        mSubmitAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DateActivity.this, ListResultsActivity.class);
                startActivity(i);
            }
        });
    }

}
