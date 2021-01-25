package com.app2.engine.service;

import org.springframework.http.ResponseEntity;

public interface DCMSBatchTaskService {
    void ACN_STARTLEGAL(String date,String syncInterface);

    void ACN_ENDLEGAL(String date);

    void ACN_ENDLEGAL_TOTAL(String date);
}
