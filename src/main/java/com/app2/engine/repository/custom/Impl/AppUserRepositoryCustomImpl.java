package com.app2.engine.repository.custom.Impl;

import com.app2.engine.entity.app.AppUser;
import com.app2.engine.entity.app.AreaMapBranch;
import com.app2.engine.repository.custom.AppUserRepositoryCustom;
import com.google.common.collect.Lists;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AppUserRepositoryCustomImpl implements AppUserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AppUser> updateStatusRetire(List<String> empActive) {
        Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(AppUser.class);
        Disjunction dj = Restrictions.disjunction();
        for (List<String> chunkList : Lists.partition(empActive, 900)) {
            dj.add(Restrictions.not(Restrictions.in("username", chunkList)));
        }
        criteria.add(dj);
        criteria.add(Restrictions.eq("userType", "I")); //Internal
        criteria.add(Restrictions.ne("status", "R")); //not retire
        return criteria.list();

    }
}
