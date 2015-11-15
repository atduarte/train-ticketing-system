package com.cmov.railwaysportugal;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class DateActivity extends Activity {

    private String token;
    private String arrivalstation;
    private String departurestation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        Bundle extras = getIntent().getExtras();
        final DatePicker datePicker = (DatePicker) findViewById(R.id.dateresult);
        datePicker.setMinDate(System.currentTimeMillis());

        token = extras.getString("TOKEN");
        arrivalstation = extras.getString("ARRIVAL");
        departurestation = extras.getString("DEPARTURE");

        Button mSubmitAll = (Button) findViewById(R.id.submitall);
        mSubmitAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DateActivity.this, ListResultsActivity.class);
                i.putExtra("TOKEN", token);
                i.putExtra("ARRIVAL", arrivalstation);
                i.putExtra("DEPARTURE", departurestation);
                Integer datecenas = new Integer(datePicker.getMonth())+1;
                i.putExtra("DATE", datePicker.getYear() + "-" + datecenas.toString() + "-" + datePicker.getDayOfMonth());
                startActivity(i);
            }
        });
    }

}
