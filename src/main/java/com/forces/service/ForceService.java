package com.forces.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.forces.model.ForceLocation;

@Service
public class ForceService {
    
    // אחסון כל הכוחות בזיכרון
    private final Map<String, ForceLocation> forces = new ConcurrentHashMap<>();

    /**
     * עדכון מיקום כוח
     */
    public ForceLocation updateLocation(ForceLocation location) {
        forces.put(location.getId(), location);
        System.out.println("📍 Updated force: " + location.getName() + 
                         " at [" + location.getLatitude() + ", " + location.getLongitude() + "]");
        return location;
    }

    /**
     * קבלת כל הכוחות
     */
    public List<ForceLocation> getAllForces() {
        return new ArrayList<>(forces.values());
    }

    /**
     * קבלת כוח ספציפי לפי ID
     */
    public ForceLocation getForce(String id) {
        return forces.get(id);
    }

    /**
     * מחיקת כוח
     */
    public boolean removeForce(String id) {
        ForceLocation removed = forces.remove(id);
        if (removed != null) {
            System.out.println("🗑️  Removed force: " + removed.getName());
            return true;
        }
        return false;
    }

    /**
     * קבלת כוחות לפי סוג
     */
    public List<ForceLocation> getForcesByType(String type) {
        List<ForceLocation> result = new ArrayList<>();
        for (ForceLocation force : forces.values()) {
            if (force.getType().equals(type)) {
                result.add(force);
            }
        }
        return result;
    }

    /**
     * ספירת כוחות לפי סוג
     */
    public Map<String, Integer> getForceCountByType() {
        Map<String, Integer> counts = new HashMap<>();
        for (ForceLocation force : forces.values()) {
            counts.merge(force.getType(), 1, Integer::sum);
        }
        return counts;
    }

    /**
     * מחיקת כל הכוחות
     */
    public void clearAllForces() {
        forces.clear();
        System.out.println("🧹 Cleared all forces");
    }
}
