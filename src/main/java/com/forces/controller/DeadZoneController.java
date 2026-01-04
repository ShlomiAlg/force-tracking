    package com.forces.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forces.model.DeadZone;
import com.forces.service.DeadZoneService;

@RestController
@RequestMapping("/api/deadzones")
@CrossOrigin(origins = "*")
public class DeadZoneController {

    @Autowired
    private DeadZoneService deadzoneService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * הוספת/עדכון Dead Zone
     * POST http://localhost:8080/api/deadzones/add
     */
    @PostMapping("/add")
    public ResponseEntity<DeadZone> addDeadZone(@RequestBody DeadZone deadzone) {
        DeadZone added = deadzoneService.addOrUpdateDeadZone(deadzone);
        
        // שליחה לכל המחוברים דרך WebSocket
        messagingTemplate.convertAndSend("/topic/deadzones", added);
        
        return ResponseEntity.ok(added);
    }

    /**
     * קבלת כל ה-Dead Zones
     * GET http://localhost:8080/api/deadzones/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<DeadZone>> getAllDeadZones() {
        return ResponseEntity.ok(deadzoneService.getAllDeadZones());
    }

    /**
     * קבלת Dead Zone ספציפי
     * GET http://localhost:8080/api/deadzones/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeadZone> getDeadZone(@PathVariable String id) {
        DeadZone deadzone = deadzoneService.getDeadZone(id);
        if (deadzone != null) {
            return ResponseEntity.ok(deadzone);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * מחיקת Dead Zone
     * DELETE http://localhost:8080/api/deadzones/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeDeadZone(@PathVariable String id) {
        boolean removed = deadzoneService.removeDeadZone(id);
        if (removed) {
            messagingTemplate.convertAndSend("/topic/deadzones-removed", id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * בדיקה אם נקודה נמצאת ב-Dead Zone
     * POST http://localhost:8080/api/deadzones/check
     * Body: {"latitude": 32.0853, "longitude": 34.7818}
     */
    @PostMapping("/check")
    public ResponseEntity<List<DeadZone>> checkPoint(@RequestBody Map<String, Double> coordinates) {
        double lat = coordinates.get("latitude");
        double lng = coordinates.get("longitude");
        
        List<DeadZone> zones = deadzoneService.getDeadZonesContainingPoint(lat, lng);
        return ResponseEntity.ok(zones);
    }

    /**
     * מחיקת כל ה-Dead Zones
     * DELETE http://localhost:8080/api/deadzones/all
     */
    @DeleteMapping("/all")
    public ResponseEntity<Void> clearAllDeadZones() {
        deadzoneService.clearAllDeadZones();
        messagingTemplate.convertAndSend("/topic/deadzones-cleared", "all");
        return ResponseEntity.ok().build();
    }

    /**
     * ספירת Dead Zones
     * GET http://localhost:8080/api/deadzones/count
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getDeadZoneCount() {
        return ResponseEntity.ok(deadzoneService.getDeadZoneCount());
    }
}
