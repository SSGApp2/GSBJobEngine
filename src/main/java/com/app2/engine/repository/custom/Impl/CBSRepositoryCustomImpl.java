package com.app2.engine.repository.custom.Impl;

import com.app2.engine.repository.custom.CBSRepositoryCustom;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Repository
public class CBSRepositoryCustomImpl implements CBSRepositoryCustom {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Map> findDocumentMovementsCollection() {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();
        querySql.append("select distinct dma.account_no \n" +
                ",d.id,d.updated_date \n" +
                ",d.doc_status \n" +
                ",d.doc_number \n" +
                ",d.process_status\n" +
                ",d.case_black_no --เลขคดีดำ\n" +
                ",case when d.cur_username is null then d.requester else d.cur_username end cur_username -- ถ้าเป็นค่าว่างใช้ requester\n" +
                ",dpo.adj_red_case_number\n" +
                ",dpo.adjudication\n" +
                ",convert(varchar,dpo.notic_doc_send_date ,103) as notic_doc_send_date --ยื่นคำฟ้องวันที่\n" +
                ",convert(varchar,dpo.law_suit_send_date ,103) as law_suit_send_date --ยื่นคำฟ้องวันที่\n" +
                ",convert(varchar, dpo.regulatory_date , 103) as regulatory_date\t--วันที่ออกคำบังคับ\n" +
                ",convert(varchar, dpo.execute_date , 103) as execute_date\t--วันนที่ออกหมายบังคับ\n" +
                ",wep.type_witness\n" +
                ",convert(varchar,wep.exam_date, 103) as exam_date --วันที่นัดสืบพยาน\n" +
                "from document d\n" +
                "JOIN emp_debt_acc_info e on d.id = e.document\n" +
                "JOIN debtor_map_account dma on e.debtor_map_account = dma.id\n" +
                "left JOIN document_progress dpo on d.id = dpo.document\n" +
                "left join witness_exam_progress wep on wep.document_progress = dpo.id\n" +
                "where d.doc_status <> 'A1' and dma.active = 'Y' \n" +
                "order by d.updated_date asc");
        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    public List<Map> findLsAccountList() {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append("SELECT distinct value  \n" +
                "FROM document  \n" +
                "   CROSS APPLY STRING_SPLIT(credit_account_number, ',')\n" +
                "   order by value ASC; ");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }
}
