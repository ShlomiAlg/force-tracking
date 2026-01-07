package com.forces.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PredictionResult {
    
    @JsonProperty("forceId")
    private String forceId;
    
    @JsonProperty("currentPosition")
    private Position currentPosition;
    
    @JsonProperty("predictedPath")
    private List<Position> predictedPath;
    
    @JsonProperty("speed")
    private double speed; // מ"ש
    
    @JsonProperty("heading")
    private double heading; // דרגות
    
    @JsonProperty("confidence")
    private double confidence; // 0-1

    public PredictionResult() {}

    // Inner class
    public static class Position {
        private double latitude;
        private double longitude;
        private long timestamp;

        public Position() {}

        public Position(double latitude, double longitude, long timestamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
        }

        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    // Getters and Setters
    public String getForceId() { return forceId; }
    public void setForceId(String forceId) { this.forceId = forceId; }
    
    public Position getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(Position currentPosition) { this.currentPosition = currentPosition; }
    
    public List<Position> getPredictedPath() { return predictedPath; }
    public void setPredictedPath(List<Position> predictedPath) { this.predictedPath = predictedPath; }
    
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    
    public double getHeading() { return heading; }
    public void setHeading(double heading) { this.heading = heading; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
}
