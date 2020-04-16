package com.app2.engine.repository;

import com.app2.engine.entity.app.HrInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HrInterfaceRepository extends JpaRepository<HrInterface, Long> {

    List<HrInterface> findByCode(@Param("code") String code);
}
