package com.forces.service;

import com.forces.model.DeadZone;
import com.forces.repository.DeadZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeadZoneService {
    
    @Autowired
    private DeadZoneRepository repository;

    public DeadZone addOrUpdateDeadZone(DeadZone deadzone) {
        DeadZone saved = repository.save(deadzone);
        System.out.println("☢️  Saved deadzone to DB: " + saved.getName());
        return saved;
    }

    public List<DeadZone> getAllDeadZones() {
        return repository.findAll();
    }

    public DeadZone getDeadZone(String id) {
        return repository.findById(id).orElse(null);
    }

    public boolean removeDeadZone(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            System.out.println("🗑️  Removed deadzone from DB: " + id);
            return true;
        }
        return false;
    }

    public List<DeadZone> getDeadZonesContainingPoint(double latitude, double longitude) {
        List<DeadZone> result = new ArrayList<>();
        List<DeadZone> all = repository.findAll();
        
        for (DeadZone dz : all) {
            double distance = calculateDistance(latitude, longitude, 
                                               dz.getLatitude(), dz.getLongitude());
            if (distance <= dz.getRadius()) {
                result.add(dz);
            }
        }
        return result;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public void clearAllDeadZones() {
        repository.deleteAll();
        System.out.println("🧹 Cleared all deadzones from DB");
    }

    public int getDeadZoneCount() {
        return (int) repository.count();
    }
}