package com.cmov.railwaysportugal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListResultsActivity extends AppCompatActivity {

    String token;
    String arrivalstation;
    String departurestation;
    String datebirth;

    TimetablesTalk mAuthTask = null;
    ListView listtripsview;
    RequestQueue queue;
    JsonArrayRequest  jsObjRequest ;
    JsonObjectRequest  jsonRequest ;

    ArrayList<TrainResult> schedules;

    ArrayList<String> listtrips = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_results);
        //set title text
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            token = extras.getString("TOKEN");
            arrivalstation = extras.getString("ARRIVAL");
            departurestation = extras.getString("DEPARTURE");
            datebirth = extras.getString("DATE");
        }

        TextView tv1 =(TextView)this.findViewById(R.id.titlelist);


        listtripsview = (ListView)this.findViewById(R.id.listcenas);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(departurestation+" -> "+arrivalstation);
            actionBar.setSubtitle(datebirth);
        }

        listtrips = new ArrayList<>();
        schedules = new ArrayList<>();
        mAuthTask = new TimetablesTalk();
        mAuthTask.execute((Void) null);

        //get train results



    }

    protected void createLayout()
    {


        for(int i = 0; i< schedules.size() ; i++)
        {
            addResult( schedules.get(i));
        }


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listtrips);

        listtripsview.setAdapter(adapter);
        listtripsview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                TrainResult trainresults = schedules.get(position);

                for(int i = 0; i < trainresults.stations.size(); i++) {
                    queue = Volley.newRequestQueue(ListResultsActivity.this);
                    String url ="http://54.186.113.106/ticket";


                    JSONObject parameters = new JSONObject();
                    try {
                        parameters.put("lineNumber",trainresults.stations.get(i).lineNumber);

                        parameters.put("lineDeparture",trainresults.stations.get(i).departuretime);

                        parameters.put("from",trainresults.stations.get(i).from);

                        parameters.put("to",trainresults.stations.get(i).to);

                        parameters.put("date",datebirth);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    // Request a string response from the provided URL.

                    jsonRequest  = new JsonObjectRequest(Request.Method.POST, url, parameters,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject  response) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(ListResultsActivity.this).create();
                                    alertDialog.setTitle("Ticket");
                                    alertDialog.setMessage("Confirm Purchase?");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();

                                                }
                                            });
                                    alertDialog.show();
                                }
                            },  new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AlertDialog alertDialog = new AlertDialog.Builder(ListResultsActivity.this).create();
                            alertDialog.setTitle("Tickets");
                            alertDialog.setMessage("Error, try again!");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            jsonRequest.setTag("TIMETABLE");
                                            queue.add(jsonRequest);
                                        }
                                    });
                            alertDialog.show();

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


                    jsonRequest.setTag("TIMETABLE");
                    queue.add(jsonRequest);
                    // Add the request to the RequestQueue.

                }



            }

        });
    }


    protected void addResult(final TrainResult trainresults)
    {


        Double distance = 0.0;
        String textrespresented = new String();
        for(int i = 0; i < trainresults.stations.size() ; i++)
        {


            textrespresented += String.format("%02d:%02d", trainresults.stations.get(i).departuretime/60 , trainresults.stations.get(i).departuretime%60);
            textrespresented += "-";
            textrespresented += String.format("%02d:%02d", trainresults.stations.get(i).arrivaltime / 60, trainresults.stations.get(i).arrivaltime % 60);
            if(i == 0 && trainresults.stations.size()>1 )
            {
                textrespresented += " / ";
            }
            if(i == 1)
            {
                textrespresented +="\n";
                textrespresented += "Change at: "+trainresults.stations.get(i).fromStation;
            }
            distance = distance + trainresults.stations.get(i).distance*1.0;
            if(i == 0 && trainresults.stations.size()==1 )
            {
                textrespresented +="\n";
            }
        }

        textrespresented += " "+ distance + "€";

        listtrips.add(textrespresented);



    }

    public class TimetablesTalk extends AsyncTask<Void, Void, Boolean> {

        TimetablesTalk() {

        }


        @Override
        protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.


            queue = Volley.newRequestQueue(ListResultsActivity.this);
            String url ="http://54.186.113.106/tickets?from="+departurestation+"&to="+arrivalstation+"&date="+datebirth;



            // Request a string response from the provided URL.

            jsObjRequest  = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray  response) {

                            Log.e("Resquest", "Cheguei aqui token" + response.toString());

                            Log.e("Resquest", "FINAL");


                            JSONArray jsonArray = (JSONArray)response;
                            if (jsonArray != null) {
                                int len = jsonArray.length();
                                for (int i=0;i<len;i++){
                                    try {
                                        schedules.add(new TrainResult(jsonArray.get(i).toString()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            createLayout();


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

            jsObjRequest.setTag("TIMETABLE");

            // Add the request to the RequestQueue.
            queue.add(jsObjRequest );


        return true;
    }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }


}






