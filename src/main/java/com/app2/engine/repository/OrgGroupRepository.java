package com.app2.engine.repository;

import com.app2.engine.entity.app.OrgGroup;
import com.app2.engine.entity.app.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrgGroupRepository extends JpaRepository<OrgGroup, Long> {
    List<OrgGroup> findByCode(@Param("code") String code);

    @Query("select o  from OrgGroup o where o.code not in :codeList or o.code is null")
    List<OrgGroup> findByCodeNotIn(@Param("codeList") List<String> codeList);
}
