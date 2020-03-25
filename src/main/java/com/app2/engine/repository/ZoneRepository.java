package com.app2.engine.repository;

import com.app2.engine.entity.app.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    List<Zone> findByCode(@Param("code")String code);
}
