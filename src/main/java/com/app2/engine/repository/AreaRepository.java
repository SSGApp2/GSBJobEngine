package com.app2.engine.repository;

import com.app2.engine.entity.app.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByCode(@Param("code")String code);

    @Query("select o  from Area o where o.code not in :codeList or o.code is null")
    List<Area> findByCodeNotIn(@Param("codeList") List<String> codeList);
}
