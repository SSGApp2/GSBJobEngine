package com.app2.engine.service.impl;

import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.HouseKeepingService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.JSONUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HouseKeepingServiceImpl extends AbstractEngineService implements HouseKeepingService {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void deleteDataByDay() {
        List<ParameterDetail> parameterDetail = parameterDetailRepository.findByParameterDetailsCode("HOUSE_KEEPING");
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();

        for (int i = 0; i < parameterDetail.size(); i++) {

            String variable1 = parameterDetail.get(i).getVariable1();
            String variable2 = parameterDetail.get(i).getVariable2();
            String variable3 = parameterDetail.get(i).getVariable3();
            String variable4 = parameterDetail.get(i).getVariable4();
            String variable5 = parameterDetail.get(i).getVariable5();

            variable2 = variable2.replaceAll("expirationDate", variable1);
            variable2 = variable2.replaceAll("documentStatus", variable5);

            LOGGER.debug("variable1 : {}", parameterDetail.get(i).getVariable1());
            LOGGER.debug("variable3 : {}", variable3);
            LOGGER.debug("variable4 : {}", variable4);
            LOGGER.debug("variable5 : {}", variable5);

            if (variable3.equals("1")) {

                jdbcTemplate.update(variable2);

            } else if (variable3.equals("2")) {

                List<Map> listDataMap = new ArrayList<>();
                List<String> listDataStr = new ArrayList<>();

                querySql.append(variable2);
                SQLQuery query = session.createSQLQuery(querySql.toString());
                query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
                listDataMap = query.list();

                LOGGER.info("listDataMap : {}",listDataMap);

                for (Map Imz : listDataMap) {
                    if (AppUtil.isNotNull(Imz.get("id"))) {
                        String id = Imz.get("id").toString();
                        listDataStr.add(id);
                    }
                }

                LOGGER.info("listDataStr : {}",listDataStr);

                String jsonString = JSONUtil.toJSON(listDataStr);
                postWithJsonCustom(jsonString, HttpMethod.POST, variable4);

            }
        }
    }
}
