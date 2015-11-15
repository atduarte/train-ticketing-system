package com.cmov.railwaysportugal;


import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class MyTicketsAcitivty extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    RequestQueue queue;
    JsonArrayRequest jsObjRequest ;
    JsonObjectRequest jsonRequest ;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String token;

    protected static ArrayList<Ticket> mytickets;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My Tickets");
        setContentView(R.layout.activity_my_tickets_acitivty);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        Bundle extras = getIntent().getExtras();

        if(extras !=null) {
            token = extras.getString("TOKEN");
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //get my tickets
        mytickets = new ArrayList<>();
        queue = Volley.newRequestQueue(MyTicketsAcitivty.this);
        String url ="http://54.186.113.106/my-tickets";

        jsObjRequest  = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray  response) {
                            //create array
                        JSONArray jsonArray = (JSONArray)response;
                        if (jsonArray != null) {
                            int len = jsonArray.length();
                            for (int i=0;i<len;i++){
                                try {
                                    mytickets.add(new Ticket(jsonArray.get(i).toString()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

                            // Set up the ViewPager with the sections adapter.
                            mViewPager = (ViewPager) findViewById(R.id.container);
                            mViewPager.setAdapter(mSectionsPagerAdapter);
                        }
                    }
                },  new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        } ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", Config.token);
                return params;
            }
        };

        jsObjRequest.setTag("TICKETS");

        queue.add(jsObjRequest);
        //END




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_tickets_acitivty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mytickets.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Ticket "+position;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString("to", mytickets.get(sectionNumber).to);
            args.putString("from", mytickets.get(sectionNumber).from);
            args.putString("date", mytickets.get(sectionNumber).date);
            args.putString("signature", mytickets.get(sectionNumber).jsonQR);
            args.putString("hour",  new Integer(mytickets.get(sectionNumber).departure)/60+":"+new Integer(mytickets.get(sectionNumber).departure)%60);


            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            FrameLayout frlayout;
            View rootView = inflater.inflate(R.layout.fragment_my_tickets_acitivty, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.to);
            TextView textView2 = (TextView) rootView.findViewById(R.id.from);
            TextView textView3 = (TextView) rootView.findViewById(R.id.date);
            TextView textView4 = (TextView) rootView.findViewById(R.id.hour);
            TextView textView5 = (TextView) rootView.findViewById(R.id.title);
            Integer i = getArguments().getInt(ARG_SECTION_NUMBER)+1;
            textView5.setText(textView5.getText()+i.toString());
            textView.setText(getArguments().getString("to"));
            textView2.setText(getArguments().getString("from"));
            textView3.setText(getArguments().getString("date"));
            textView4.setText(getArguments().getString("hour"));
            frlayout = (FrameLayout) rootView.findViewById(R.id.qrcodebiew);
            //View qrcodeviewex = rootView.findViewById(R.id.QRCodeView);
            QRCodeView q1 = new QRCodeView(container.getContext(),getArguments().getString("signature"));
            frlayout.addView(q1);
            return rootView;
        }
    }
}
