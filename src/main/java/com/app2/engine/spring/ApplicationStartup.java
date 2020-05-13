package com.app2.engine.spring;

import com.app2.engine.constant.ApplicationConstant;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.ParameterDetailRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LogManager.getLogger(ApplicationStartup.class);

    @Autowired
    private ParameterDetailRepository parameterDetailRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        LOGGER.info("ApplicationStartup.....!");
        LOGGER.info("Swagger UI : /swagger-ui.html");
        LOGGER.info("Spring Data REST : /rest-api");


        ParameterDetail pDetail = parameterDetailRepository.findByParameterAndCode("APP_CONFIG", "20");
        ApplicationConstant.GSBAPPServer = pDetail.getVariable1();
        pDetail = parameterDetailRepository.findByParameterAndCode("APP_CONFIG", "21");
        ApplicationConstant.GSBEngine = pDetail.getVariable1();
        pDetail = parameterDetailRepository.findByParameterAndCode("APP_CONFIG", "22");
        ApplicationConstant.GSBJobEngine = pDetail.getVariable1();
        pDetail = parameterDetailRepository.findByParameterAndCode("APP_CONFIG", "23");
        ApplicationConstant.GSBMailEngine = pDetail.getVariable1();
        pDetail = parameterDetailRepository.findByParameterAndCode("APP_CONFIG", "24");
        ApplicationConstant.GSBEngineLocal = pDetail.getVariable1();

        LOGGER.info("GSBAPPServer   :{}", ApplicationConstant.GSBAPPServer);
        LOGGER.info("GSBEngine   :{}", ApplicationConstant.GSBEngine);
        LOGGER.info("GSBEngineLocal   :{}", ApplicationConstant.GSBEngineLocal);
        LOGGER.info("GSBJobEngine   :{}", ApplicationConstant.GSBJobEngine);
        LOGGER.info("GSBMailEngine   :{}", ApplicationConstant.GSBMailEngine);

    }

}
