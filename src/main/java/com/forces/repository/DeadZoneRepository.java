package com.forces.repository;

import com.forces.model.DeadZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeadZoneRepository extends JpaRepository<DeadZone, String> {
}