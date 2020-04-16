package com.app2.engine.repository;

import com.app2.engine.entity.app.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByCode(@Param("code") String code);

    @Query("select o  from Unit o where o.code not in :codeList or o.code is null")
    List<Unit> findByCodeNotIn(@Param("codeList") List<String> codeList);
}
