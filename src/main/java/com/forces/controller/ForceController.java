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

@RestController
@RequestMapping("/api/forces")
@CrossOrigin(origins = "*")
public class ForceController {

    @Autowired
    private ForceService forceService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * עדכון מיקום כוח (מהטלפון)
     * POST http://localhost:8080/api/forces/update
     */
    @PostMapping("/update")
    public ResponseEntity<ForceLocation> updateLocation(@RequestBody ForceLocation location) {
        ForceLocation updated = forceService.updateLocation(location);
        
        // שליחה לכל המחוברים דרך WebSocket
        messagingTemplate.convertAndSend("/topic/locations", updated);
        
        return ResponseEntity.ok(updated);
    }

    /**
     * קבלת כל הכוחות
     * GET http://localhost:8080/api/forces/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<ForceLocation>> getAllForces() {
        return ResponseEntity.ok(forceService.getAllForces());
    }

    /**
     * קבלת כוח ספציפי
     * GET http://localhost:8080/api/forces/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ForceLocation> getForce(@PathVariable String id) {
        ForceLocation force = forceService.getForce(id);
        if (force != null) {
            return ResponseEntity.ok(force);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * מחיקת כוח
     * DELETE http://localhost:8080/api/forces/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeForce(@PathVariable String id) {
        boolean removed = forceService.removeForce(id);
        if (removed) {
            messagingTemplate.convertAndSend("/topic/removed", id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * קבלת כוחות לפי סוג
     * GET http://localhost:8080/api/forces/type/{type}
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ForceLocation>> getForcesByType(@PathVariable String type) {
        return ResponseEntity.ok(forceService.getForcesByType(type));
    }

    /**
     * סטטיסטיקה - ספירה לפי סוג
     * GET http://localhost:8080/api/forces/stats/count
     */
    @GetMapping("/stats/count")
    public ResponseEntity<Map<String, Integer>> getForceStats() {
        return ResponseEntity.ok(forceService.getForceCountByType());
    }

    /**
     * מחיקת כל הכוחות
     * DELETE http://localhost:8080/api/forces/all
     */
    @DeleteMapping("/all")
    public ResponseEntity<Void> clearAllForces() {
        forceService.clearAllForces();
        messagingTemplate.convertAndSend("/topic/cleared", "all");
        return ResponseEntity.ok().build();
    }

    /**
     * בדיקת תקינות השרת
     * GET http://localhost:8080/api/forces/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Server is running ✓");
    }
}
