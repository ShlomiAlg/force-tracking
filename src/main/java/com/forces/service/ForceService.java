package com.forces.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.forces.model.ForceLocation;

@Service
public class ForceService {
    
    private final Map<String, ForceLocation> forces = new ConcurrentHashMap<>();

    public ForceLocation updateLocation(ForceLocation location) {
        location.setTimestamp(System.currentTimeMillis());
        location.setConnected(true);
        forces.put(location.getId(), location);
        
        System.out.println("📍 Updated: " + location.getName() + 
                         " | Signal: " + getSignalIcon(location.getSignalStrength()));
        return location;
    }

    public List<ForceLocation> getAllForces() {
        return new ArrayList<>(forces.values());
    }

    public ForceLocation getForce(String id) {
        return forces.get(id);
    }

    public boolean removeForce(String id) {
        ForceLocation removed = forces.remove(id);
        if (removed != null) {
            System.out.println("🗑️  Removed force: " + removed.getName());
            return true;
        }
        return false;
    }

    public List<ForceLocation> getForcesByType(String type) {
        List<ForceLocation> result = new ArrayList<>();
        for (ForceLocation force : forces.values()) {
            if (force.getType().equals(type)) {
                result.add(force);
            }
        }
        return result;
    }

    public Map<String, Integer> getForceCountByType() {
        Map<String, Integer> counts = new HashMap<>();
        for (ForceLocation force : forces.values()) {
            counts.merge(force.getType(), 1, Integer::sum);
        }
        return counts;
    }

    public void clearAllForces() {
        forces.clear();
        System.out.println("🧹 Cleared all forces");
    }

    // בדיקה אוטומטית כל 10 שניות - אילו כוחות מנותקים
    @Scheduled(fixedRate = 10000)
    public void checkDisconnectedForces() {
        long now = System.currentTimeMillis();
        int disconnectedCount = 0;
        
        for (ForceLocation force : forces.values()) {
            long timeSinceUpdate = now - force.getTimestamp();
            boolean wasConnected = force.isConnected();
            
            if (timeSinceUpdate > 30000) { // 30 שניות
                force.setConnected(false);
                if (wasConnected) {
                    System.out.println("⚠️  Force disconnected: " + force.getName());
                }
                disconnectedCount++;
            }
        }
        
        if (disconnectedCount > 0) {
            System.out.println("📊 Disconnected forces: " + disconnectedCount + "/" + forces.size());
        }
    }

    private String getSignalIcon(int strength) {
        switch (strength) {
            case 0: return "📵 אין";
            case 1: return "📶 חלש";
            case 2: return "📶 בינוני";
            case 3: return "📶 טוב";
            case 4: return "📶 מצוין";
            default: return "❓";
        }
    }
}