package com.app2.engine.repository.custom.Impl;

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
