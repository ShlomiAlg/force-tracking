package com.forces;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ForceTrackingApplication {
    
    
    public static void main(String[] args) {
        SpringApplication.run(ForceTrackingApplication.class, args);
        System.out.println("========================================");
        System.out.println("🚀 Force Tracking Server Started!");
        System.out.println("📍 Server: http://localhost:8080");
        System.out.println("🗺️  Web UI: http://localhost:8080/index.html");
        System.out.println("📡 WebSocket: ws://localhost:8080/ws");
        System.out.println("========================================");
    }
}
