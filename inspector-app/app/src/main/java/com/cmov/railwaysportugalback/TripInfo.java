package com.cmov.railwaysportugalback;


public class TripInfo {
    public Integer lineNumber;
    public String name;
    public Integer[][] times;

    public String getName() {
        return name;
    }

    public Integer[][] getTimes() {
        return times;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }
}
