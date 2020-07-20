package com.app2.engine.repository;

import com.app2.engine.entity.app.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


public interface ParameterRepository extends JpaSpecificationExecutor<Parameter>, JpaRepository<Parameter, Long>, PagingAndSortingRepository<Parameter, Long> {

	Parameter findByCode(@Param("code") String code);

}
