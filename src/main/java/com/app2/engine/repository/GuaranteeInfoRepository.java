package com.app2.engine.repository;

import com.app2.engine.entity.app.Document;
import com.app2.engine.entity.app.GuaranteeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GuaranteeInfoRepository extends JpaRepository<GuaranteeInfo,Long>, JpaSpecificationExecutor<GuaranteeInfo> {
    @Query("select DISTINCT u from GuaranteeInfo u left join u.debtorGuaranteeInfo r left join u.document d where r.id = :deptorId and d.id =  :docId ")
    GuaranteeInfo findByDocIdAndDebtorId(@Param("docId")Long docId, @Param("deptorId") Long deptorId);

    @Query("select DISTINCT u from GuaranteeInfo u where (u.civilCaseFlag is null or u.civilCaseFlag = 'N') and (u.confiscateFlag is null or u.confiscateFlag = 'N') and u.document = :documentId ")
    List<GuaranteeInfo> findByFlag(@Param("documentId")Long documentId);

    List<GuaranteeInfo> findByDocument(@Param("document") Document document);

    @Query("select DISTINCT u from GuaranteeInfo u left join u.debtorGuaranteeInfo r left join u.document d where r.accountNo = :accountNo and d.id =  :document ")
    List<GuaranteeInfo> findByDocumentAndAccountNo(@Param("document")Long document,@Param("accountNo")String accountNo);

    List<GuaranteeInfo> findAllBySeizedCollateral(@Param("seizedCollateral")String status);
}
