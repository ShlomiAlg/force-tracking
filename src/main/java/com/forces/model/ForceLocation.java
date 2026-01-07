package com.forces.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ForceLocation {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("latitude")
    private double latitude;
    
    @JsonProperty("longitude")
    private double longitude;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("timestamp")
    private long timestamp;
    
    @JsonProperty("signalStrength")
    private int signalStrength = 0; // 0-4
    
    @JsonProperty("isConnected")
    private boolean isConnected = true; // זיהוי ניתוק

    public ForceLocation() {
        this.timestamp = System.currentTimeMillis();
    }

    public ForceLocation(String id, double latitude, double longitude, String type, String name) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.name = name;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getSignalStrength() { return signalStrength; }
    public void setSignalStrength(int signalStrength) { this.signalStrength = signalStrength; }

    public boolean isConnected() { return isConnected; }
    public void setConnected(boolean connected) { isConnected = connected; }

    // בדיקה אם הכוח מנותק (לא עדכן יותר מ-30 שניות)
    public boolean checkIfDisconnected() {
        long timeSinceUpdate = System.currentTimeMillis() - this.timestamp;
        this.isConnected = timeSinceUpdate < 30000; // 30 שניות
        return !this.isConnected;
    }

    @Override
    public String toString() {
        return "ForceLocation{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", signalStrength=" + signalStrength +
                ", isConnected=" + isConnected +
                ", timestamp=" + timestamp +
                '}';
    }
}
