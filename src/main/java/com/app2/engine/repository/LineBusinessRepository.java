package com.app2.engine.repository;

import com.app2.engine.entity.app.LineBusiness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LineBusinessRepository extends JpaRepository<LineBusiness, Long> {

    List<LineBusiness> findByCode(@Param("code") String code);

    @Query("select o  from LineBusiness o where o.code not in :codeList or o.code is null")
    List<LineBusiness> findByCodeNotIn(@Param("codeList") List<String> codeList);
}
