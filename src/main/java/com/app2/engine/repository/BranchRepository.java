package com.app2.engine.repository;

import com.app2.engine.entity.app.Branch;
import com.app2.engine.entity.app.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findByCode(@Param("code") String code);

    Branch findOneByCode(@Param("code") String code);

    @Query("select o  from Branch o where o.code not in :codeList or o.code is null")
    List<Branch> findByCodeNotIn(@Param("codeList") List<String> codeList);
}
