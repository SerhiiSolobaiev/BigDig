package com.myandroid.bigdiga.model;

import android.graphics.Color;

public enum  Status {
    DOWNLOADED(1,"Downloaded", Color.GREEN),
    ERROR(2,"Error", Color.RED),
    UNKNOWN(3,"Unknown", Color.GRAY);

    private int id;
    private String status;
    private int color;

    Status(int id, String status, int color) {
        this.id = id;
        this.status = status;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public int getColor(){
        return color;
    }

    @Override
    public String toString() {
        return status;
    }
}
