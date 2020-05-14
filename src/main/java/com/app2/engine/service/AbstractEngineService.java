package com.app2.engine.service;

import com.app2.engine.constant.ApplicationConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractEngineService {
    protected static Logger LOGGER = LoggerFactory.getLogger(AbstractEngineService.class);

    public ResponseEntity<String> getResultByExchange(String urlParam) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers = setHeaderUserMapDetails(headers);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Content-Type", "application/json; charset=utf-8");
        HttpEntity<String> entity = new HttpEntity<String>("", headers);
        String url = ApplicationConstant.GSBEngine + urlParam;
//        String url = ApplicationConstant.GSBEngineLocal + urlParam;
        LOGGER.info(" request :{}", url);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public HttpHeaders setHeaderUserMapDetails(HttpHeaders headers) {
        List<String> listOuCode = new ArrayList<>();
        List<String> listUserName = new ArrayList<>();
        try {
            listUserName.add(ApplicationConstant.APPLICATION_NAME);
        } catch (Exception e) {
            LOGGER.error("ERROR: {}", e.getMessage());
        }
        headers.put("SESSION_USERNAME", listUserName);
        return headers;
    }

    public ResponseEntity<String> postWithJsonCustom(String parameterMap, HttpMethod httpMethod, String urlParam) {
        RestTemplate restTemplate = new RestTemplate();
        String url = ApplicationConstant.GSBEngineLocal + urlParam;

        LOGGER.info("postWithJsonCustom url :{}", url);

        MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers = setHeaderUserMapDetails(headers);
        HttpEntity<String> entity = new HttpEntity<String>(parameterMap, headers);
        LOGGER.info("postWithJsonCustom entity : {} ", entity);

        if (httpMethod == null) {
            httpMethod = HttpMethod.POST;
        }

        FormHttpMessageConverter converter = new FormHttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(mediaType));

        restTemplate.getMessageConverters().add(converter);
        ResponseEntity<String> reponseEntity = restTemplate.postForEntity(url, entity, String.class);

        return reponseEntity;
    }
}
