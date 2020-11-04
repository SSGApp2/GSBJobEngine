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

    public List<Map> findByObj(DocumentProgress documentProgress, List<String> sort, List<String> projection){
        Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(DocumentProgress.class);
        criteria.createAlias("document", "document");

        List<Map> result = new ArrayList<>();

        if (documentProgress !=null) {
            if (documentProgress.getDocument().getCourt() != null) {
                criteria.add(Restrictions.eq("court", documentProgress.getDocument().getCourt()));
            }
        }

        if (documentProgress !=null) {
            if (documentProgress.getDocument().getId() != null) {
                criteria.add(Restrictions.eq("document.id", documentProgress.getDocument().getId()));
            }
        }

        // Sort
        if (sort != null){
            for (String sortStr : sort){
                String[] sortArr = sortStr.split("_");
                String column = sortArr[0];
                String mode = "";
                if (sortArr.length > 1){
                    mode = sortArr[1];
                }

                if (AppUtil.isEmpty(mode) || mode.toUpperCase().equals("ASC")){
                    criteria.addOrder(Order.asc(column));
                } else {
                    criteria.addOrder(Order.desc(column));
                }
            }
        }
//
//        // Projection
        ProjectionList projectionList = Projections.projectionList();
        if (projection != null){
            for (String pro : projection){
                projectionList.add(Projections.property(pro),pro);
            }
        } else {
            projectionList.add(Projections.property("id"),"id");
            projectionList.add(Projections.property("version"),"version");
            projectionList.add(Projections.property("createdBy"),"createdBy");
            projectionList.add(Projections.property("createdDate"),"createdDate");
            projectionList.add(Projections.property("updatedBy"),"updatedBy");
            projectionList.add(Projections.property("updatedDate"),"updatedDate");

            List<String> attribute = getAttr(documentProgress);
            for (String attr : attribute){
                projectionList.add(Projections.property(attr),attr);
            }

        }
        criteria.setProjection(projectionList);

        criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP); //TOMAP
        result = criteria.list();
        return result;
    }

    @Override
    public List<DocumentProgress> findByDocumentId(Long documentId) {
        Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(DocumentProgress.class);
        criteria.createAlias("document", "document");
        criteria.add(Restrictions.eq("document.id", documentId));
        return criteria.list();
    }

    public List<String> getAttr(DocumentProgress documentProgress) {
        List<String> result = new ArrayList<>();
        List<Object> valueList = new ArrayList<>();
        try {
            Class cls = documentProgress.getClass();
            Object target = documentProgress;
            for (Field field : cls.getDeclaredFields()) {
                Field strField = ReflectionUtils.findField(cls, field.getName());
                if (!strField.getType().equals(List.class) && !strField.getType().equals(Set.class)) {
                    result.add(field.getName());
                    strField.setAccessible(true);
                    Object value = ReflectionUtils.getField(strField, target);
                    if (AppUtil.isNotNull(value) && AppUtil.isNotEmpty(value.toString())) {
                        valueList.add(value);
                        ReflectionUtils.makeAccessible(strField); //set null when emptyString
                        ReflectionUtils.setField(strField, target, null);
                    }
                }
            }
        } catch (Exception e) {

        }
        valueList.hashCode();
        return result;
    }

    @Override
    public Map getReasonLastProgress(Long documentId) {
        Criteria criteria = ((Session) entityManager.getDelegate()).createCriteria(DocumentProgress.class);
        criteria.createAlias("document", "document");
        criteria.add(Restrictions.eq("document.id", documentId));

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("reqApvType"),"reqApvType");
        projectionList.add(Projections.property("reqApvReason"),"reqApvReason");
        criteria.addOrder(Order.desc("court"));
        criteria.setFirstResult(0); //find last
        criteria.setMaxResults(1);
        criteria.setProjection(projectionList);
        criteria.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP); //TOMAP
        return (Map)criteria.uniqueResult();
    }
}
