package com.cmov.railwaysportugal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimetableActivity extends AppCompatActivity {

    private String token;
    private RequestQueue queue;
    private JsonObjectRequest  jsObjRequest ;
    private String departure;
    private String arrival;

    ArrayList<String> stations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        setTitle("Buy Tickets");
        stations = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            token = extras.getString("TOKEN");
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Spinner departureStation = (Spinner) findViewById(R.id.departure);

        final Spinner arrivalStation = (Spinner) findViewById(R.id.arrivals);


        queue = Volley.newRequestQueue(TimetableActivity.this);
        String url ="http://54.186.113.106/stations";


        // Request a string response from the provided URL.

        jsObjRequest  = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.e("Resquest", "Cheguei aqui token" + response.get("stations").toString());
                            JSONArray jsonarrayresponse = new JSONArray(response.get("stations").toString());

                            if (jsonarrayresponse != null) {
                                int len = jsonarrayresponse.length();
                                for (int i=0;i<len;i++){
                                    stations.add(jsonarrayresponse.get(i).toString());

                                }
                            }
                            ArrayAdapter<String> stations_adapter;
                            stations_adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, stations);
                            departureStation.setAdapter(stations_adapter);
                            arrivalStation.setAdapter(stations_adapter);
                            Log.e("Resquest", "FINAL");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //colocar nas lists
                    }
                },  new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("ERROR","error => "+error.toString());
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", Config.token);
                return params;
            }
        };

        jsObjRequest.setTag("STATIONS");

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest );

        Button mSubmitButton = (Button) findViewById(R.id.submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //verify station and send in bundle
                departure = departureStation.getSelectedItem().toString();
                arrival = arrivalStation.getSelectedItem().toString();
                if(departure!=arrival)
                {
                    Intent i = new Intent(TimetableActivity.this, DateActivity.class);

                    i.putExtra("TOKEN", token);
                    i.putExtra("ARRIVAL", arrival);
                    i.putExtra("DEPARTURE", departure);
                    startActivity(i);
                }

            }
        });


    }

}
