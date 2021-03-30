package com.app2.engine.repository.custom.Impl;

import com.app2.engine.repository.custom.DCMSRepositoryCustom;
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
public class DCMSRepositoryCustomImpl implements DCMSRepositoryCustom {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Map> findAcnEndLegal() {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append("SELECT dadi.account_no as accountNo,d.doc_type as docType\n" +
                ",d.select_reason_close as selectReasonClose \n" +
                ",d.reason_close as reasonClose \n" +
                "FROM debtor_acc_debt_info dadi \n" +
                "join document d on d.debtor = dadi.debtor");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    public List<Map> litigationUpdateBKC(String date) {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append("SELECT  DISTINCT d.id\n" +
                ",d.updated_date as doc_up\n" +
                ",dp.updated_date as dp_up\n" +
                ",ap.updated_date as ap_up\n" +
                ",ag.updated_date as ag_up\n" +
                ",ass.updated_date as ass_up\n" +
                ",con.updated_date as con_up\n" +
                ",dh.[sequence]\n" +
                ",d.doc_number\n" +
                ",dh.action_time\n" +
                ",dp.notic_doc_send_date\n" +
                ",dp.black_case_number\n" +
                ",dp.law_suit_send_date\n" +
                ",dp.red_case_number\n" +
                ",dp.adjudication\n" +
                ",dp.adj_date\n" +
                ",dp.gazette_date\n" +
                ",ap.account_payment_date\n" +
                ",ap.debt_amount\n" +
                ",ap.amount\n" +
                ",con.confiscate_date\n" +
                ",con.cost_est_legal_ex_office\n" +
                ",con.cost_est_legal_bank_date\n" +
                ",ass.amount_buy\n" +
                ",d.doc_status \n" +
                "FROM document d \n" +
                "JOIN document_history dh on d.id = dh.document \n" +
                "JOIN confiscate con on d.id = con.document \n" +
                "join document_progress dp on d.id = dp.document \n" +
                "join account_payment ap on dp.id = ap.document_progress \n" +
                "join asset_group ag on d.id = ag.document \n" +
                "join asset_sale ass on ag.id = ass.asset_group \n" +
                "where d.doc_type = 2\n" +
                "and datediff(day, d.updated_date , '" + date + "') = 0 \n" +
                "and dh.user_role_to = 'LAW'\n" +
                "order by d.updated_date DESC\n" +
                ",dp.updated_date DESC\n" +
                ",ap.updated_date DESC\n" +
                ",ag.updated_date DESC\n" +
                ",ass.updated_date DESC\n" +
                ",con.updated_date\n" +
                ",dh.[sequence] DESC");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    public List<Map> litigationUpdateBKO(String date) {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append("SELECT DISTINCT d.id\n" +
                ",d.updated_date as doc_up\n" +
                ",document_progress.updated_date as progress_up\n" +
                ",account_payment.updated_date  as account_up\n" +
                ",d.doc_number \n" +
                ",document_history.action_time\n" +
                ",document_progress.black_case_number \n" +
                ",document_progress.red_case_number \n" +
                ",document_progress.date_adjudicate_out \n" +
                ",document_progress.gazette_date \n" +
                ",account_payment.account_payment_date \n" +
                ",account_payment.debt_amount \n" +
                ",account_payment.amount \n" +
                "from document d\n" +
                "join document_history on d.id = document_history.document\n" +
                "join document_progress on d.id = document_progress.document\n" +
                "join account_payment on document_progress.id = account_payment.document_progress \n" +
                "where datediff(day, d.updated_date , '" + date + "') = 0\n" +
                "order by d.updated_date DESC \n" +
                ",document_progress.updated_date DESC \n" +
                ",account_payment.updated_date DESC \n" +
                ",document_history.action_time DESC");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    public List<Map> litigationUpdateCVC(String date) {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append("WITH CTE AS (\n" + "SELECT d.id as docID\n" +
                ",d.doc_number as docNumber\n" + ",d.doc_type as docType\n" +
                ",d.principal_balance as principalBalance\n" +
                ",d.interest \n" + ",dh.[sequence] as sequence\n" +
                ",dh.user_role_to as userRoleTo\n" + ",dh.action_time as actionTime\n" +
                ",dp.black_case_number as blackCaseNumber\n" +
                ",dp.red_case_number as redCaseNumber\n" +
                ",dp.law_suit_send_date as lawSuitSendDate\n" +
                ",dp.court \n" +
                ",dp.adj_date as adjDate\n" +
                ",dp.adjudication \n" +
                ",ROW_NUMBER() OVER(PARTITION BY d.id \n" +
                "ORDER BY d.id ASC\n" +
                ",dh.[sequence] DESC\n" +
                ",dp.updated_date DESC) as RowNumber \n" +
                "FROM document d \n" +
                "left join document_history dh on d.id = dh.document\n" +
                "LEFT JOIN document_progress dp on d.id = dp.document \n" +
                "WHERE datediff(day, d.updated_date , '" + date + "') = 0\n" +
                "and d.doc_type = 1\n" +
                "and dh.user_role_to = 'LAW')\n" +
                "SELECT * FROM CTE \n" +
                "WHERE RowNumber = 1");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    public List<Map> litigationUpdateCVO(String date) {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT * FROM fc_jb001('" + date + "')");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }
}
