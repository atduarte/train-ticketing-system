package com.cmov.railwaysportugalback.Ticket;

import java.util.ArrayList;


public class TicketManager {
    private ArrayList<String> ids = new ArrayList<>();

    public boolean isUsed(Ticket ticket) {
        return ids.contains(ticket.getId());
    }

    public void add(Ticket ticket) {
        ids.add(ticket.getId());
    }

    public ArrayList get() {
        return ids;
    }
}
