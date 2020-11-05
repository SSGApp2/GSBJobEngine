package com.app2.engine.repository;


import com.app2.engine.entity.app.CourtOrderDetail;
import com.app2.engine.entity.app.DocumentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourtOrderDetailRepository extends JpaRepository<CourtOrderDetail,Long>{
    List<CourtOrderDetail> findByDocumentProgress(@Param("documentProgress") DocumentProgress documentProgress);
    List<CourtOrderDetail> findByDocumentProgressOrderByUpdatedDateDesc(@Param("documentProgress") DocumentProgress documentProgress);
}
