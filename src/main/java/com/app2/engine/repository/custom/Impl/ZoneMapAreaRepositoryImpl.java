package com.app2.engine.repository.custom.Impl;

import com.app2.engine.entity.app.ZoneMapArea;
import com.app2.engine.repository.custom.ZoneMapAreaRepositoryCustom;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ZoneMapAreaRepositoryImpl implements ZoneMapAreaRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ZoneMapArea> findByZoneCodeAndAreaCode(String zoneCode,String areaCode){
        Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(ZoneMapArea.class);
        criteria.createAlias("zone","zone");
        criteria.createAlias("area","area");
        criteria.add(Restrictions.eq("zone.code",zoneCode));
        criteria.add(Restrictions.eq("area.code",areaCode));
        return criteria.list();
    }
}
