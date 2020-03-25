package com.app2.engine.repository;

import com.app2.engine.entity.app.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByCode(@Param("code")String code);
}
