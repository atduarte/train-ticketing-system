package com.cmov.railwaysportugalback.Ticket;

import org.json.JSONException;
import org.json.JSONObject;

public class TicketParser {
    public Ticket parse(String ticketJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(ticketJson);

        return new Ticket(
                jsonObject.getString("id"),
                jsonObject.getInt("lineNumber"),
                jsonObject.getString("from"),
                jsonObject.getString("to"),
                jsonObject.getString("date"),
                jsonObject.getInt("departure"),
                jsonObject.getString("signature")
        );
    }
}
