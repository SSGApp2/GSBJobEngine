package com.app2.engine.repository;


import com.app2.engine.entity.app.DocumentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentProgressRepository extends JpaRepository<DocumentProgress,Long>, JpaSpecificationExecutor<DocumentProgress> {
}
