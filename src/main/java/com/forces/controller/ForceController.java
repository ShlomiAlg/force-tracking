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

import com.forces.model.ForceLocation;
import com.forces.service.ForceService;
import com.forces.service.TrajectoryService;

@RestController
@RequestMapping("/api/forces")
@CrossOrigin(origins = "*")
public class ForceController {

    @Autowired
    private ForceService forceService;

    @Autowired
    private TrajectoryService trajectoryService;  // ← חשוב!

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/update")
    public ResponseEntity<ForceLocation> updateLocation(@RequestBody ForceLocation location) {
        ForceLocation updated = forceService.updateLocation(location);
        
        // ← זה החלק החשוב! שמירה בהיסטוריה
        trajectoryService.addLocation(
            location.getId(),
            location.getLatitude(),
            location.getLongitude()
        );
        
        messagingTemplate.convertAndSend("/topic/locations", updated);
        
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ForceLocation>> getAllForces() {
        return ResponseEntity.ok(forceService.getAllForces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForceLocation> getForce(@PathVariable String id) {
        ForceLocation force = forceService.getForce(id);
        if (force != null) {
            return ResponseEntity.ok(force);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeForce(@PathVariable String id) {
        boolean removed = forceService.removeForce(id);
        if (removed) {
            trajectoryService.removeTrajectory(id);  // ← נקה גם את ההיסטוריה
            messagingTemplate.convertAndSend("/topic/removed", id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ForceLocation>> getForcesByType(@PathVariable String type) {
        return ResponseEntity.ok(forceService.getForcesByType(type));
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Map<String, Integer>> getForceStats() {
        return ResponseEntity.ok(forceService.getForceCountByType());
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> clearAllForces() {
        forceService.clearAllForces();
        trajectoryService.clearAll();  // ← נקה את כל ההיסטוריה
        messagingTemplate.convertAndSend("/topic/cleared", "all");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Server is running ✓");
    }
}