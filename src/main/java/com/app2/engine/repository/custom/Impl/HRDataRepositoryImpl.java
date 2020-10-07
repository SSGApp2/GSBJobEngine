package com.app2.engine.repository.custom.Impl;

import com.app2.engine.repository.custom.HRDataRepository;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Repository
public class HRDataRepositoryImpl implements HRDataRepository {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public int updateDepartmentInActive(Date date) {
        String hql = "update Department o set o.status='I' where o.activeDate <:date "; //update status to inactive
        int row = ((Session) entityManager.getDelegate()).createQuery(hql).setParameter("date", date).executeUpdate();
        LOGGER.debug("row {}", row);
        return row;
    }
}
