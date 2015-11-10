package com.cmov.railwaysportugal;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class ListResultsActivity extends Activity {

    String token;
    String arrivalstation;
    String departurestation;
    String datebirth;

    TimetablesTalk mAuthTask = null;

    RequestQueue queue;
    JsonArrayRequest  jsObjRequest ;
    JsonObjectRequest  jsonRequest ;

    ArrayList<TrainResult> schedules;

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
        tv1.setText(departurestation+" -> "+arrivalstation+"\n"+datebirth);

        schedules = new ArrayList<>();
        mAuthTask = new TimetablesTalk();
        mAuthTask.execute((Void) null);

        //get train results


    }

    protected void createLayout()
    {
        for(int i = 0; i< schedules.size() ; i++)
        {
            addResult((LinearLayout) super.findViewById(R.id.listofresults), schedules.get(i));
        }
    }


    protected void addResult(LinearLayout scrollviewresults, final TrainResult trainresults)
    {

        LinearLayout l1 = new LinearLayout(this);
        l1.setOrientation(LinearLayout.VERTICAL);

        TableRow.LayoutParams textviewlayout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams textviewlayout2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        l1.setLayoutParams(lp);
        //viewById.addView();

        TextView t1 = new TextView(this);
        t1.setGravity(Gravity.CENTER_HORIZONTAL);
        t1.setLines(2);
        t1.setLayoutParams(textviewlayout);
        String textrespresented = new String();
        for(int i = 0; i < trainresults.stations.size() ; i++)
        {
            textrespresented += "Departure: "+trainresults.stations.get(i).departuretime/60 +":"+trainresults.stations.get(i).departuretime%60;
            textrespresented += " ";
            textrespresented += trainresults.stations.get(i).fromStation;
            textrespresented += " ";
            textrespresented += "Dur(min): "+trainresults.stations.get(i).duration.toString();
            textrespresented += " ";
            textrespresented += "Arrival: "+trainresults.stations.get(i).toStation;
            textrespresented += " ";
            textrespresented += trainresults.stations.get(i).arrivaltime/60 +":"+trainresults.stations.get(i).arrivaltime%60;
            textrespresented += " ";
            textrespresented += "\n";
        }
        t1.setText(textrespresented);

        Button b1 = new Button(this);
        b1.setLayoutParams(textviewlayout2);
        b1.setText("Buy");
        //b1.setGravity(Gravity.CENTER_HORIZONTAL);

        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                for(int i = 0; i < trainresults.stations.size(); i++) {
                    queue = Volley.newRequestQueue(ListResultsActivity.this);
                    String url ="http://54.186.113.106/ticket";


                    JSONObject parameters = new JSONObject();
                    try {
                        parameters.put("lineNumber",trainresults.stations.get(i).lineNumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        parameters.put("lineDeparture",trainresults.stations.get(i).departuretime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        parameters.put("from",trainresults.stations.get(i).from);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        parameters.put("to",trainresults.stations.get(i).to);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
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
                            params.put("Authorization", token);
                            return params;
                        }
                    };


                    jsonRequest.setTag("TIMETABLE");

                    // Add the request to the RequestQueue.

                }






            }
        });

        l1.addView(t1);
        l1.addView(b1);

        scrollviewresults.addView(l1);



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
                    params.put("Authorization", token);
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






