package com.app2.engine.repository;


import com.app2.engine.entity.app.Document;
import com.app2.engine.entity.app.DocumentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentProgressRepository extends JpaRepository<DocumentProgress,Long>, JpaSpecificationExecutor<DocumentProgress> {

    DocumentProgress findByDocumentAndCourt(@Param("document")Document document,@Param("court")String court);
    DocumentProgress findByDocument(@Param("document") Document document);
    List<DocumentProgress> findDocumentProgressByDocumentOrderByUpdatedDate(@Param("document")Document document);
    List<DocumentProgress> findByDocumentOrderByUpdatedDateDesc(@Param("document")Document document);
}
