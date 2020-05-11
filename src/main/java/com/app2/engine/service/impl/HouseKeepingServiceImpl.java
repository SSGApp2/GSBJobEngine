package com.app2.engine.service.impl;

import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.service.HouseKeepingService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.validator.constraints.EAN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Service
public class HouseKeepingServiceImpl implements HouseKeepingService {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Map> deleteDataByDay() {
    List<ParameterDetail> parameterDetail = parameterDetailRepository.findByParameterDetailsCode("HOUSE_KEEPING");
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

    for(int i=0;i<parameterDetail.size();i++){
        String variable1 = parameterDetail.get(i).getVariable1();
        String variable2 = parameterDetail.get(i).getVariable2();

//        String password = parameterDetail.getVariable3();
//        String backup = parameterDetail.getVariable4();
        variable2 = variable2.replaceAll("expirationDate",variable1);
        LOGGER.debug("variable1    {}", parameterDetail.get(i).getVariable1());
        LOGGER.debug("variable2    {}", variable2);
        LOGGER.debug("parameterDetail.size()    {}", parameterDetail.size());
        LOGGER.debug("i    {}", i);


        querySql.append(variable2);
//        LOGGER.debug("username      {}", variable2);

        LOGGER.info("Query searchAuction : {}", querySql.toString());


    }
        SQLQuery query = session.createSQLQuery(querySql.toString());
    LOGGER.info("LOG QURY {}",query);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }
}
