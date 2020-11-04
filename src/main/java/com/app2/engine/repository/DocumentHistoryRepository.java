package com.app2.engine.repository;

import com.app2.engine.entity.app.Document;
import com.app2.engine.entity.app.DocumentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentHistoryRepository extends JpaRepository<DocumentHistory,Long>, JpaSpecificationExecutor<DocumentHistory>{
    List<DocumentHistory> findByDocumentOrderBySequenceDesc(@Param("document") Document document);
}
