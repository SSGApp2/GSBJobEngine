package com.app2.engine.repository.custom.Impl;

import com.app2.engine.entity.app.EmployeeInternal;
import com.app2.engine.repository.custom.EmployeeInternalRepositoryCustom;
import com.app2.engine.util.AppUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class EmployeeInternalRepositoryCustomImpl implements EmployeeInternalRepositoryCustom{
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EmployeeInternal> findEmpAssignedDocAuto(Long branchId, String branchCenter, String loanType, String docAutoType) {
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        querySql.append("select\n" +
                "\tei.username\n" +
                "from\n" +
                "\temployee_internal ei\n");

        if (String.valueOf(docAutoType).equals("B")){
            querySql.append("inner join auto_branch ab on ab.employee_internal = ei.id\n");
        }

        if(String.valueOf(docAutoType).equals("C")){
            querySql.append("inner join auto_loan_type alt on alt.employee_internal = ei.id\n");
        }

        querySql.append("where\n" +
                "\tei.department_for_lead = '"+branchCenter+"'\n");

        if (String.valueOf(docAutoType).equals("B") && AppUtil.isNotNull(branchId)) {
            querySql.append("\tand ab.branch = "+branchId+"\n");
        }

        if(String.valueOf(docAutoType).equals("C") && AppUtil.isNotEmpty(loanType)){
            querySql.append("\tand alt.loan_type_code = '"+loanType+"'\n");
        }

        querySql.append("\tand ei.username in (\n" +
                "\t\n" +
                "\t\tselect\n" +
                "\t\t\tau.username\n" +
                "\t\tfrom\n" +
                "\t\t\tapp_user au\n" +
                "\t\tinner join app_user_role aur on au.id = aur.app_user\n" +
                "\t\tinner join app_role ar on aur.app_role = ar.id\n" +
                "\t\twhere\n" +
                "\t\t\tau.user_type = 'I' \n" +
                "\t\t\tand ar.code = 'MK1'\n" +
                "\t\t)\n" +
                "\t\t\n" +
                "\tand ei.username not in ( \n" +
                "\t\n" +
                "\t\tselect\n" +
                "\t\t\trrah.username\n" +
                "\t\tfrom\n" +
                "\t\t\tround_robin_assigned_his rrah\n");

        if (String.valueOf(docAutoType).equals("B")) {
            querySql.append("\t\tinner join branch b on rrah.branch = b.id\n");
        }

        querySql.append("\t\twhere\n");

        if (String.valueOf(docAutoType).equals("B") && AppUtil.isNotNull(branchId)) {
            querySql.append("\t\t\tb.id = "+branchId+" and \n");
        }

        if(String.valueOf(docAutoType).equals("C") && AppUtil.isNotEmpty(loanType)){
            querySql.append("\trrah.loan_type = '"+loanType+"' and \n");
        }

        querySql.append("\t\t\trrah.branch_center = '"+branchCenter+"'\n" +
                "\t\t)\n" +
                "\t\n" +
                "order by\n" +
                "\tei.username asc");

        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(Transformers.aliasToBean(EmployeeInternal.class));
        return query.list();

    }
}
