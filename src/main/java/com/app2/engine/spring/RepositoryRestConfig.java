package com.app2.engine.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.http.MediaType;

@Configuration
public class RepositoryRestConfig extends RepositoryRestConfigurerAdapter {


    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
//      http://localhost:8083/rest-api
        config.setBasePath("/rest-api");
        config.setReturnBodyOnCreate(Boolean.TRUE);
        config.setReturnBodyOnUpdate(Boolean.TRUE);
        config.exposeIdsFor(com.app2.engine.entity.base.BaseEntity.class);
        config.useHalAsDefaultJsonMediaType(false);
        config.setDefaultMediaType(MediaType.APPLICATION_JSON);
    }

}
