package com.cmov.railwaysportugalback.Ticket;

public class Ticket {

    protected String id;
    protected String from;
    protected String to;
    protected String date;
    protected Integer departure;
    protected Integer lineNumber;
    protected String signature;

    public Ticket(String id, Integer lineNumber, String from, String to, String date, Integer departure, String signature) {
        this.id = id;
        this.lineNumber = lineNumber;
        this.from = from;
        this.to = to;
        this.date = date;
        this.departure = departure;
        this.signature = signature;
    }

    public String getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDate() {
        return date;
    }

    public Integer getDeparture() {
        return departure;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public String getSignature() {
        return signature;
    }

    public String getSignable() {
        return id + lineNumber.toString() + from + to + date + departure.toString();
    }
}
