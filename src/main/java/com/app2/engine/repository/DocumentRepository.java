package com.app2.engine.repository;

import com.app2.engine.entity.app.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    Document findOneById(@Param("id") Long id);

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
