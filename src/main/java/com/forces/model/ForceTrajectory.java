package com.forces.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ForceTrajectory {
    
    @JsonProperty("forceId")
    private String forceId;
    
    @JsonProperty("locations")
    private List<LocationPoint> locations;
    
    @JsonProperty("maxHistorySize")
    private int maxHistorySize = 50; // שמור עד 50 מיקומים אחרונים

    public ForceTrajectory() {
        this.locations = new ArrayList<>();
    }

    public ForceTrajectory(String forceId) {
        this.forceId = forceId;
        this.locations = new ArrayList<>();
    }

    // הוספת מיקום חדש
    public void addLocation(double latitude, double longitude, long timestamp) {
        LocationPoint point = new LocationPoint(latitude, longitude, timestamp);
        locations.add(point);
        
        // שמור רק את ה-N מיקומים האחרונים
        if (locations.size() > maxHistorySize) {
            locations.remove(0);
        }
    }

    // קבלת כל המיקומים
    public List<LocationPoint> getLocations() {
        return new ArrayList<>(locations);
    }

    // קבלת N מיקומים אחרונים
    public List<LocationPoint> getLastNLocations(int n) {
        int size = locations.size();
        if (size <= n) {
            return new ArrayList<>(locations);
        }
        return new ArrayList<>(locations.subList(size - n, size));
    }

    // קבלת המיקום האחרון
    public LocationPoint getLastLocation() {
        if (locations.isEmpty()) {
            return null;
        }
        return locations.get(locations.size() - 1);
    }

    // חישוב מהירות ממוצעת (מטר לשנייה)
    public double getAverageSpeed() {
        if (locations.size() < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;
        long totalTime = 0;

        for (int i = 1; i < locations.size(); i++) {
            LocationPoint prev = locations.get(i - 1);
            LocationPoint curr = locations.get(i);
            
            totalDistance += calculateDistance(prev, curr);
            totalTime += (curr.getTimestamp() - prev.getTimestamp());
        }

        double totalTimeSeconds = totalTime / 1000.0;
        return totalTimeSeconds > 0 ? totalDistance / totalTimeSeconds : 0.0;
    }

    // חישוב כיוון תנועה (זווית בדרגות, 0 = צפון)
    public double getCurrentHeading() {
        if (locations.size() < 2) {
            return 0.0;
        }

        LocationPoint prev = locations.get(locations.size() - 2);
        LocationPoint curr = locations.get(locations.size() - 1);

        double lat1 = Math.toRadians(prev.getLatitude());
        double lat2 = Math.toRadians(curr.getLatitude());
        double lon1 = Math.toRadians(prev.getLongitude());
        double lon2 = Math.toRadians(curr.getLongitude());

        double dLon = lon2 - lon1;
        
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - 
                   Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        
        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (bearing + 360) % 360;
    }

    // חישוב מרחק בין שתי נקודות (Haversine)
    private double calculateDistance(LocationPoint p1, LocationPoint p2) {
        final int R = 6371000; // רדיוס כדור הארץ במטרים
        
        double lat1 = Math.toRadians(p1.getLatitude());
        double lat2 = Math.toRadians(p2.getLatitude());
        double dLat = Math.toRadians(p2.getLatitude() - p1.getLatitude());
        double dLon = Math.toRadians(p2.getLongitude() - p1.getLongitude());
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    // Getters and Setters
    public String getForceId() {
        return forceId;
    }

    public void setForceId(String forceId) {
        this.forceId = forceId;
    }

    public int getMaxHistorySize() {
        return maxHistorySize;
    }

    public void setMaxHistorySize(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
    }

    public int getLocationCount() {
        return locations.size();
    }

    // Inner class - נקודת מיקום בודדת
    public static class LocationPoint {
        private double latitude;
        private double longitude;
        private long timestamp;

        public LocationPoint() {}

        public LocationPoint(double latitude, double longitude, long timestamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
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

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return String.format("[%.6f, %.6f] at %d", latitude, longitude, timestamp);
        }
    }
}
