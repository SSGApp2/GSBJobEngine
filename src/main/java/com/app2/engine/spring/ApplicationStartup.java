package com.app2.engine.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LogManager.getLogger(ApplicationStartup.class);

//    @Autowired
//    private AppParameterRepository appParameterRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        LOGGER.info("ApplicationStartup.....!");
        LOGGER.info("Swagger UI : /swagger-ui.html");
        LOGGER.info("Spring Data REST : /rest-api");

//        AppParameter appParameterConfig = appParameterRepository.findByCode("50");//example
    }

}
