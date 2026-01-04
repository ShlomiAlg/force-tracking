package com.forces.service;

import com.forces.model.DeadZone;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeadZoneService {
    
    private final Map<String, DeadZone> deadzones = new ConcurrentHashMap<>();

    /**
     * הוספה/עדכון של Dead Zone
     */
    public DeadZone addOrUpdateDeadZone(DeadZone deadzone) {
        deadzones.put(deadzone.getId(), deadzone);
        System.out.println("☢️  Added/Updated deadzone: " + deadzone.getName() + 
                         " with radius " + deadzone.getRadius() + "m");
        return deadzone;
    }

    /**
     * קבלת כל ה-Dead Zones
     */
    public List<DeadZone> getAllDeadZones() {
        return new ArrayList<>(deadzones.values());
    }

    /**
     * קבלת Dead Zone ספציפי
     */
    public DeadZone getDeadZone(String id) {
        return deadzones.get(id);
    }

    /**
     * מחיקת Dead Zone
     */
    public boolean removeDeadZone(String id) {
        DeadZone removed = deadzones.remove(id);
        if (removed != null) {
            System.out.println("🗑️  Removed deadzone: " + removed.getName());
            return true;
        }
        return false;
    }

    /**
     * בדיקה אם נקודה נמצאת בתוך Dead Zone
     */
    public List<DeadZone> getDeadZonesContainingPoint(double latitude, double longitude) {
        List<DeadZone> result = new ArrayList<>();
        for (DeadZone dz : deadzones.values()) {
            double distance = calculateDistance(
                latitude, longitude, 
                dz.getLatitude(), dz.getLongitude()
            );
            if (distance <= dz.getRadius()) {
                result.add(dz);
            }
        }
        return result;
    }

    /**
     * חישוב מרחק בין שתי נקודות (במטרים)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // רדיוס כדור הארץ במטרים
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    /**
     * מחיקת כל ה-Dead Zones
     */
    public void clearAllDeadZones() {
        deadzones.clear();
        System.out.println("🧹 Cleared all deadzones");
    }

    /**
     * ספירת Dead Zones
     */
    public int getDeadZoneCount() {
        return deadzones.size();
    }
}
