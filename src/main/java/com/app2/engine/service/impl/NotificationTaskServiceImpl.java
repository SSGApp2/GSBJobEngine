package com.app2.engine.service.impl;

import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.NotificationTaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NotificationTaskServiceImpl extends AbstractEngineService implements NotificationTaskService{

    @Override
    public ResponseEntity<String> notification(String processType) {
        return getResultByExchange("/jobs/notification?processType="+processType);
    }
}
