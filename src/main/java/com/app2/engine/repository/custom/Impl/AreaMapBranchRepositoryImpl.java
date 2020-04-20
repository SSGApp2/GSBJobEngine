package com.app2.engine.repository.custom.Impl;

import com.app2.engine.entity.app.AreaMapBranch;
import com.app2.engine.repository.custom.AreaMapBranchRepositoryCustom;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AreaMapBranchRepositoryImpl implements AreaMapBranchRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AreaMapBranch> fineByAreaCodeAndBranchCode(String areaCode, String branchCode){
        Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(AreaMapBranch.class);
        criteria.createAlias("area","area");
        criteria.createAlias("branch","branch");
        criteria.add(Restrictions.eq("area.code",areaCode));
        criteria.add(Restrictions.eq("branch.code",branchCode));
        return criteria.list();
    }
}
