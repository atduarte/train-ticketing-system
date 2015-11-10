package com.cmov.railwaysportugal;

import android.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by smsesteves on 06/11/2015.
 */
public class TrainResult {


    ArrayList<Station> stations;

    TrainResult(String tr)
    {
        JSONArray js = null;

        try {
             js = new JSONArray(tr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(stations==null) {
            stations = new ArrayList<>();
        }
        int len = js.length();
        for (int i=0;i<len;i++) {
            try {
                stations.add(new Station(js.get(i).toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
