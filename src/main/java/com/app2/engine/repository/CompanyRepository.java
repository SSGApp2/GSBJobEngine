package com.app2.engine.repository;

import com.app2.engine.entity.app.Company;
import com.app2.engine.entity.app.OrgGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByCode(@Param("code") String code);

    @Query("select o  from Company o where o.code not in :codeList or o.code is null")
    List<Company> findByCodeNotIn(@Param("codeList") List<String> codeList);
}
