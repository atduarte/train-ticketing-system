package com.cmov.railwaysportugalback.Ticket;

import java.util.ArrayList;


public class TicketManager {
    private static ArrayList<String> ids = new ArrayList<>();

    public static boolean isUsed(Ticket ticket) {
        return ids.contains(ticket.getId());
    }

    public static void add(Ticket ticket) {
        ids.add(ticket.getId());
    }

    public static ArrayList get() {
        return ids;
    }
}
