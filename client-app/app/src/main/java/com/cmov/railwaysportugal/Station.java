package com.cmov.railwaysportugal;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by smsesteves on 06/11/2015.
 */
public class Station {

    Integer lineNumber;
    Integer from;
    Integer to ;

    String fromStation;
    String toStation;

    Double distance;
    Double duration;

    Integer departuretime;
    Integer arrivaltime;

    Integer linedepaturetime;

    Station(String tr)
    {
        JSONObject js=null;
        try {
            js = new JSONObject(tr);
            js.getJSONObject("times").getString("arrival");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            lineNumber = Integer.parseInt(js.get("lineNumber").toString());
            distance = Double.parseDouble(js.get("distance").toString());
            arrivaltime = Integer.parseInt(js.getJSONObject("times").getString("arrival"));
            departuretime = Integer.parseInt(js.getJSONObject("times").getString("departure"));
            linedepaturetime = Integer.parseInt(js.getJSONObject("lineTimes").getString("departure"));
            duration = Double.parseDouble(js.get("duration").toString());
            from = Integer.parseInt(js.get("from").toString());
            to = Integer.parseInt(js.get("to").toString());
            fromStation = js.getString("fromName");
            toStation = js.getString("toName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
