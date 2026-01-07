package com.forces.model;

import com.fasterxml.jackson.annotation.JsonProperty;


import jakarta.persistence.*;

@Entity
@Table(name = "deadzones")
public class DeadZone {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private double latitude;
    
    @Column(nullable = false)
    private double longitude;
    
    @Column(nullable = false)
    private double radius;
    
    private String name;
    
    private String description;
    
    private long timestamp;
    
    // ... שאר הקוד נשאר אותו דבר
}







public class DeadZone {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("latitude")
    private double latitude;
    
    @JsonProperty("longitude")
    private double longitude;
    
    @JsonProperty("radius")
    private double radius; // ברדיוס במטרים
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("timestamp")
    private long timestamp;

    // Constructors
    public DeadZone() {
        this.timestamp = System.currentTimeMillis();
    }

    public DeadZone(String id, double latitude, double longitude, double radius, String name, String description) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.name = name;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DeadZone{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
