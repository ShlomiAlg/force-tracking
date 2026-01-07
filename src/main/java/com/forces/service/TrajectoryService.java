package com.forces.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.forces.algorithm.KalmanFilter;
import com.forces.model.ForceTrajectory;
import com.forces.model.PredictionResult;

@Service
public class TrajectoryService {
    
    // אחסון מסלולים לכל כוח
    private final Map<String, ForceTrajectory> trajectories = new ConcurrentHashMap<>();
    
    // Kalman Filter לכל כוח
    private final Map<String, KalmanFilter> kalmanFilters = new ConcurrentHashMap<>();
    
    // זמן המדידה הקודמת לכל כוח
    private final Map<String, Long> lastUpdateTimes = new ConcurrentHashMap<>();

    /**
     * הוספת מיקום חדש לכוח
     */
    public void addLocation(String forceId, double latitude, double longitude) {
        long currentTime = System.currentTimeMillis();
        
        // יצירת trajectory אם לא קיים
        trajectories.putIfAbsent(forceId, new ForceTrajectory(forceId));
        ForceTrajectory trajectory = trajectories.get(forceId);
        
        // הוספת המיקום
        trajectory.addLocation(latitude, longitude, currentTime);
        
        // עדכון Kalman Filter
        updateKalmanFilter(forceId, latitude, longitude, currentTime);
        
        System.out.println("📊 Added location for " + forceId + 
                         ": [" + latitude + ", " + longitude + "]");
    }

    /**
     * עדכון Kalman Filter
     */
    private void updateKalmanFilter(String forceId, double lat, double lng, long currentTime) {
        // יצירת filter אם לא קיים
        kalmanFilters.putIfAbsent(forceId, new KalmanFilter());
        KalmanFilter filter = kalmanFilters.get(forceId);
        
        // חישוב הזמן שעבר מהמדידה הקודמת
        Long lastTime = lastUpdateTimes.get(forceId);
        double deltaTime = 1.0; // ברירת מחדל: שנייה אחת
        
        if (lastTime != null) {
            deltaTime = (currentTime - lastTime) / 1000.0; // המרה לשניות
        }
        
        // עדכון הפילטר
        filter.update(lat, lng, deltaTime);
        lastUpdateTimes.put(forceId, currentTime);
        
        System.out.println("🔮 Kalman filter updated for " + forceId + 
                         " (deltaTime: " + deltaTime + "s)");
    }

    /**
     * קבלת חיזוי מסלול לכוח
     */
    public PredictionResult predictTrajectory(String forceId, int secondsAhead) {
        ForceTrajectory trajectory = trajectories.get(forceId);
        KalmanFilter filter = kalmanFilters.get(forceId);
        
        if (trajectory == null || filter == null || trajectory.getLocationCount() < 2) {
            return null; // אין מספיק נתונים
        }
        
        PredictionResult result = new PredictionResult();
        result.setForceId(forceId);
        
        // מיקום נוכחי
        ForceTrajectory.LocationPoint lastLocation = trajectory.getLastLocation();
        PredictionResult.Position currentPos = new PredictionResult.Position(
            lastLocation.getLatitude(),
            lastLocation.getLongitude(),
            lastLocation.getTimestamp()
        );
        result.setCurrentPosition(currentPos);
        
        // חיזוי מסלול
        List<PredictionResult.Position> predictedPath = new ArrayList<>();
        int steps = 10; // 10 נקודות חיזוי
        double timeStep = secondsAhead / (double) steps;
        
        for (int i = 1; i <= steps; i++) {
            double[] futurePos = filter.predictFuturePosition(i * timeStep);
            PredictionResult.Position pos = new PredictionResult.Position(
                futurePos[0],
                futurePos[1],
                System.currentTimeMillis() + (long)(i * timeStep * 1000)
            );
            predictedPath.add(pos);
        }
        result.setPredictedPath(predictedPath);
        
        // מהירות וכיוון
        result.setSpeed(trajectory.getAverageSpeed());
        result.setHeading(trajectory.getCurrentHeading());
        
        // רמת ביטחון (על בסיס כמות הנתונים)
        double confidence = Math.min(1.0, trajectory.getLocationCount() / 10.0);
        result.setConfidence(confidence);
        
        System.out.println("🎯 Predicted trajectory for " + forceId + 
                         " (" + secondsAhead + "s ahead, confidence: " + 
                         String.format("%.2f", confidence) + ")");
        
        return result;
    }

    /**
     * קבלת כל המסלולים
     */
    public Map<String, ForceTrajectory> getAllTrajectories() {
        return new HashMap<>(trajectories);
    }

    /**
     * קבלת מסלול ספציפי
     */
    public ForceTrajectory getTrajectory(String forceId) {
        return trajectories.get(forceId);
    }

    /**
     * מחיקת מסלול
     */
    public void removeTrajectory(String forceId) {
        trajectories.remove(forceId);
        kalmanFilters.remove(forceId);
        lastUpdateTimes.remove(forceId);
        System.out.println("🗑️  Removed trajectory for " + forceId);
    }

    /**
     * ניקוי כל המסלולים
     */
    public void clearAll() {
        trajectories.clear();
        kalmanFilters.clear();
        lastUpdateTimes.clear();
        System.out.println("🧹 Cleared all trajectories");
    }

    /**
     * סטטיסטיקות
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalForces", trajectories.size());
        
        int totalPoints = 0;
        for (ForceTrajectory trajectory : trajectories.values()) {
            totalPoints += trajectory.getLocationCount();
        }
        stats.put("totalLocationPoints", totalPoints);
        stats.put("averagePointsPerForce", 
                  trajectories.isEmpty() ? 0 : totalPoints / trajectories.size());
        
        return stats;
    }
}
