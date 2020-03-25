package com.app2.engine.repository;

import com.app2.engine.entity.app.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByCodeAndSubCode(@Param("code") String code, @Param("subCode") String subCode);
}
