package com.cmov.railwaysportugal;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by smsesteves on 08/11/2015.
 */
public class Ticket {
    public String id;
    public String date;
    public String from;
    public String to;
    public String departure;
    public String signature;

    Ticket(String tr)
    {
        JSONObject js=null;
        try {
            js = new JSONObject(tr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            id = js.getString("id");
            date = js.getString("date");
            from = js.getString("from");
            to = js.getString("to");
            departure = js.getString("departure");
            signature = js.getString("signature");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
