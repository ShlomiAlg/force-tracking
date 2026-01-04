package com.forces.controller;

import com.forces.model.ForceTrajectory;
import com.forces.model.PredictionResult;
import com.forces.service.TrajectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/trajectory")
@CrossOrigin(origins = "*")
public class TrajectoryController {

    @Autowired
    private TrajectoryService trajectoryService;

    /**
     * קבלת חיזוי מסלול לכוח
     * GET http://localhost:8080/api/trajectory/predict/{forceId}?seconds=60
     */
    @GetMapping("/predict/{forceId}")
    public ResponseEntity<PredictionResult> predictTrajectory(
            @PathVariable String forceId,
            @RequestParam(defaultValue = "60") int seconds) {
        
        PredictionResult prediction = trajectoryService.predictTrajectory(forceId, seconds);
        
        if (prediction == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(prediction);
    }

    /**
     * קבלת היסטוריית מיקומים לכוח
     * GET http://localhost:8080/api/trajectory/history/{forceId}
     */
    @GetMapping("/history/{forceId}")
    public ResponseEntity<ForceTrajectory> getTrajectoryHistory(@PathVariable String forceId) {
        ForceTrajectory trajectory = trajectoryService.getTrajectory(forceId);
        
        if (trajectory == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(trajectory);
    }

    /**
     * קבלת כל המסלולים
     * GET http://localhost:8080/api/trajectory/all
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, ForceTrajectory>> getAllTrajectories() {
        return ResponseEntity.ok(trajectoryService.getAllTrajectories());
    }

    /**
     * סטטיסטיקות
     * GET http://localhost:8080/api/trajectory/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(trajectoryService.getStatistics());
    }

    /**
     * מחיקת מסלול
     * DELETE http://localhost:8080/api/trajectory/{forceId}
     */
    @DeleteMapping("/{forceId}")
    public ResponseEntity<Void> removeTrajectory(@PathVariable String forceId) {
        trajectoryService.removeTrajectory(forceId);
        return ResponseEntity.ok().build();
    }

    /**
     * ניקוי כל המסלולים
     * DELETE http://localhost:8080/api/trajectory/all
     */
    @DeleteMapping("/all")
    public ResponseEntity<Void> clearAllTrajectories() {
        trajectoryService.clearAll();
        return ResponseEntity.ok().build();
    }
}
