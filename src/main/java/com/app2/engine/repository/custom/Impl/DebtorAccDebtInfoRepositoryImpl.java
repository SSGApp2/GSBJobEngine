package com.app2.engine.repository.custom.Impl;

import com.app2.engine.entity.app.DebtorAccDebtInfo;
import com.app2.engine.repository.custom.DebtorAccDebtInfoRepositoryCustom;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Repository
public class DebtorAccDebtInfoRepositoryImpl implements DebtorAccDebtInfoRepositoryCustom {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DebtorAccDebtInfo findByAccountNo(String accountNo) {
        Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(DebtorAccDebtInfo.class);
        criteria.add(Restrictions.eq("accountNo",accountNo));
        List<DebtorAccDebtInfo> debtorAccDebtInfos = criteria.list();
        if(debtorAccDebtInfos.size() > 0){
            return debtorAccDebtInfos.get(0);
        }
        return null;

    }
}
