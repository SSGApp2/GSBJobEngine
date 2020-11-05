package com.app2.engine.repository.custom.Impl;

import com.app2.engine.config.Statement;
import com.app2.engine.repository.custom.DocumentRepositoryCustom;
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
public class DocumentRepositoryImpl implements DocumentRepositoryCustom {

    static final Logger LOGGER = LoggerFactory.getLogger(DocumentRepositoryImpl.class);

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
                ",case when d.cur_username is null then d.requester else d.cur_username end cur_username -- ถ้าเป็นค่าว่างใช้ requester\n" +
                ",dpo.adj_red_case_number\n" +
                ",dpo.adjudication\n" +
                ",wep.type_witness\n" +
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
    public List findCurDate(String curDate) {
        String curDateFormat = curDate;
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append(Statement.GET_DOC_CUR_DATE);
        querySql.append("WHERE datediff(day, d.updated_date , '" + curDateFormat + "') = 0");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    public List findCurDtByRoleByDocument(String curDate, String role, String docID) {
        String curDateFormat = curDate;
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append(Statement.GET_DOC_CUR_DATE_AND_DOCHIS_BY_ROLE_BY_DOC);
        querySql.append(" WHERE dh.user_role_to = '" + role + "' ");
        querySql.append(" AND document = " + docID + " ORDER BY dh.[sequence] DESC ");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }
}
