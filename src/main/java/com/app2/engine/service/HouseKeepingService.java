package com.app2.engine.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface HouseKeepingService {
    public void deleteDataByDay();
    public void setAppUserLoginWrong();
}
