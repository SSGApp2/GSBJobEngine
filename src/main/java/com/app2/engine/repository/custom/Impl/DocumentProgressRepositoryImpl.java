package com.app2.engine.repository.custom.Impl;


import com.app2.engine.entity.app.DocumentProgress;
import com.app2.engine.repository.custom.DocumentProgressRepositoryCustom;
import com.app2.engine.util.AppUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class DocumentProgressRepositoryImpl implements DocumentProgressRepositoryCustom {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DocumentProgress> findByDocumentId(Long documentId) {
        Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(DocumentProgress.class);
        criteria.createAlias("document", "document");
        criteria.add(Restrictions.eq("document.id", documentId));
        return criteria.list();
    }
}
