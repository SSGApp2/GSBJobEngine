package com.app2.engine.repository.custom.Impl;

import com.app2.engine.repository.custom.CMSRepositoryCustom;
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
public class CMSRepositoryCustomImpl implements CMSRepositoryCustom {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Map> findLegalStatusGuarantee() {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append("SELECT gi.id as guaranteeID\n" +
                ",gi.document\n" +
                ",dgi.doc_number as docNumber\n" +
                ",gi.seized_collateral as seizedCollateral\n" +
                ",gi.seized_collateral_lawyer as seizedCollateralLawyer\n" +
                ",gi.seized_collateral_group as seizedCollateralGroup\n" +
                "FROM guarantee_info gi \n" +
                "Left join debtor_guarantee_info dgi on gi.debtor_guarantee_info = dgi.id\n" +
                "where gi.document <>''");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    public List<Map> findLegalStatusDocHistory(String document) {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append("SELECT proceed_document as proceedDocument\n" +
                ",doc_status as docStatus\n" +
                "FROM document_history dh \n" +
                "where doc_status <>''\n" +
                "and document = :document\n" +
                "order by [sequence] DESC");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setParameter("document", document);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    public List<Map> findLegalStatusAssetSale(String guarantee) {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append("SELECT result_sell as resultSell\n" +
                "FROM asset_sale as2\n" +
                "where guarantee_info = :guarantee\n" +
                "order by sale_time DESC");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setParameter("guarantee", guarantee);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    public List<Map> findSeizeInfoGuarantee() {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append("SELECT gi.id as guaranteeID" +
                ",gi.document\n" +
                ",dgi.doc_number  as docNumber\n" +
                ",doc.office_legal as officeLegal\n" +
                "FROM guarantee_info gi \n" +
                "left join document doc on gi.document = doc.id\n" +
                "Left join debtor_guarantee_info dgi on gi.debtor_guarantee_info = dgi.id\n" +
                "where gi.seized_collateral = 'Y'\n" +
                "and gi.document <>''");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    public List<Map> findSeizeInfoDocProgress(String document) {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append("SELECT dp.red_case_number as redCaseNumber\n" +
                ",dp.black_case_number as blackCaseNumber\n" +
                "FROM  document_progress dp \n" +
                "where dp.document = :document\n" +
                "and (dp.red_case_number <>'' or (black_case_number <>'' and black_case_number <>'/'))\n" +
                "order by dp.updated_date DESC");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setParameter("document", document);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    public List<Map> findSeizeInfoConfiscate(String guarantee) {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append("SELECT c.confiscate_date as confiscateDate\n" +
                ",c.court_adjudicate as courtAdjudicate \n" +
                "FROM confiscate c \n" +
                "where c.guarantee_info = :guarantee\n" +
                "order by c.updated_date DESC");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setParameter("guarantee", guarantee);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }
}
