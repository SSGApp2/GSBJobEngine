package com.app2.engine.repository.custom.Impl;

import com.app2.engine.entity.app.EmpDebtAccInfo;
import com.app2.engine.repository.custom.EmpDebtAccInfoRepositoryCustom;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class EmpDebtAccInfoRepositoryCustomImpl implements EmpDebtAccInfoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EmpDebtAccInfo> findByAccountNo(String accountNo) {
        Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(EmpDebtAccInfo.class);
        criteria.createAlias("debtorMapAccount", "debtorMapAccount");
        criteria.add(Restrictions.eq("debtorMapAccount.accountNo",accountNo));
        return criteria.list();
    }

    @Override
    public List<EmpDebtAccInfo> findDocumentId(Long documentId) {
        Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(EmpDebtAccInfo.class);
        criteria.createAlias("document", "document");
        criteria.add(Restrictions.eq("document.id",documentId));
        return criteria.list();
    }
}
