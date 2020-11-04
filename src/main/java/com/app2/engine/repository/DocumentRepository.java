package com.app2.engine.repository;

import com.app2.engine.entity.app.Debtor;
import com.app2.engine.entity.app.Document;
import com.app2.engine.entity.app.GuaranteeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    List<Document> findByDocStatusAndCurRole(@Param("docStatus") String docStatus, @Param("curRole") String curRole);

    List<Document> findByDebtor(@Param("debtor") Debtor debtor);

    @Query("select d from Document d where d.docNumber = :docNumber")
    Document findByDocNumber(@Param("docNumber") String docNumber);

    List<Document> findByCreditAccountNumberContainsAndDocTypeOrderByCreatedDate(@Param("creditAccountNumber") String creditAccountNumber, @Param("docType") String docType);

    List<Document> findByCurRoleAndCurUsername(@Param("curRole") String curRole, @Param("curUsername") String curUsername);

    Document findOneById(@Param("id") Long id);

    @Query(value = "select a.document from (\n" +
            "\tselect\n" +
            "\t\tRank() OVER(\n" +
            "\t\t\tPARTITION BY document\n" +
            "\t\t\tORDER BY [sequence] DESC) AS rank,\n" +
            "\t\tdh.document,dh.[sequence],dh.doc_status\n" +
            "\tfrom document d \n" +
            "\tjoin document_history dh on d.id=dh.document \n" +
            "\twhere (\n" +
            "\t\t\tdh.username_from in (:empIn ) \n" +
            "\t\t\tor dh.user_role_to in (:empIn)\n" +
            "\t\t)\n" +
            ")a where rank=1 and a.doc_status not in  (:docNotIn)", nativeQuery = true)
    List<Long> findDocumentForFistTraceProgress(@Param("empIn") List<String> empIn, @Param("docNotIn") List<String> docNotIn);

    @Query("select d from Document d where d.branchCenter = :branchCenter and SUBSTRING(d.docNumber,LENGTH(d.docNumber) - 9,2) = :year")
    List<Document> findByBranchCenterAndYearInDocNumber(@Param("branchCenter") String branchCenter, @Param("year") String year);

    List<Document> findByGuaranteeInfos(@Param("GuaranteeInfos") GuaranteeInfo guaranteeInfo);


    @Query(value = "select \n" +
            "a.*," +
            "case court \n" +
            "\twhen '1' then  b.description\n" +
            "\twhen '2' then 'ศาลอุทธรณ์'\n" +
            "\twhen '3' then 'ศาลกีฎา'\n" +
            "\twhen '4' then 'ศาลล้มละลายกลาง'\n" +
            "end as description\n"+
            "from (\n" +
            "\tselect\n" +
            "\tRank() OVER(\n" +
            "\t\tPARTITION BY document \n" +
            "\t\tORDER BY court DESC) AS rank,\n" +
            "\tdocument ,court ,court_name \n" +
            "\tfrom document_progress\n" +
            ")a\n" +
            "left join (\n" +
            "\tselect pd.code,description from parameter_detail pd  \n" +
            "\tjoin [parameter] p on p.id=pd.[parameter]\n" +
            "\twhere p.code ='COURT'\n" +
            ")b on a.court_name=b.code\n" +
            " where a.rank =1 and a.document=:id ", nativeQuery = true)
    List<Map> findLastCourtByDocId(@Param("id") Long id);

}
