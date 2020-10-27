package com.app2.engine.spring;

import com.app2.engine.constant.ApplicationConstant;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.NotificationSettingRepository;
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

    @Autowired
    private NotificationSettingRepository notificationSettingRepository;

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
//        ApplicationConstant.GSBEngineLocal = pDetail.getVariable1();
        ApplicationConstant.GSBEngineLocal = "http://localhost:8009/GSBEngine";

        LOGGER.info("GSBAPPServer   :{}", ApplicationConstant.GSBAPPServer);
        LOGGER.info("GSBEngine   :{}", ApplicationConstant.GSBEngine);
        LOGGER.info("GSBEngineLocal   :{}", ApplicationConstant.GSBEngineLocal);
        LOGGER.info("GSBJobEngine   :{}", ApplicationConstant.GSBJobEngine);
        LOGGER.info("GSBMailEngine   :{}", ApplicationConstant.GSBMailEngine);

        ApplicationConstant.notifyTimeGSB02 = notificationSettingRepository.findByProcessType("2").getNotiTime();
        ApplicationConstant.notifyTimeGSB03 = notificationSettingRepository.findByProcessType("3").getNotiTime();
        ApplicationConstant.notifyTimeGSB04 = notificationSettingRepository.findByProcessType("4").getNotiTime();
        ApplicationConstant.notifyTimeGSB06 = notificationSettingRepository.findByProcessType("6").getNotiTime();
        ApplicationConstant.notifyTimeGSB07 = notificationSettingRepository.findByProcessType("7").getNotiTime();
        ApplicationConstant.notifyTimeGSB08 = notificationSettingRepository.findByProcessType("8").getNotiTime();

        LOGGER.info("Notify Time GSB02 : "+ApplicationConstant.notifyTimeGSB02);
        LOGGER.info("Notify Time GSB03 : "+ApplicationConstant.notifyTimeGSB03);
        LOGGER.info("Notify Time GSB04 : "+ApplicationConstant.notifyTimeGSB04);
        LOGGER.info("Notify Time GSB06 : "+ApplicationConstant.notifyTimeGSB06);
        LOGGER.info("Notify Time GSB07 : "+ApplicationConstant.notifyTimeGSB07);
        LOGGER.info("Notify Time GSB08 : "+ApplicationConstant.notifyTimeGSB08);
    }

}
