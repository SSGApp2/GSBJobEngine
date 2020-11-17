package com.app2.engine.controller;


import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.DocumentTaskService;
import com.app2.engine.service.EmployeeADService;
import com.app2.engine.service.HRDataService;
import com.app2.engine.service.HouseKeepingService;
import com.app2.engine.service.NotificationTaskService;
import com.app2.engine.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    HRDataService hrDataService;

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    DocumentTaskService documentTaskService;

    @Autowired
    HouseKeepingService houseKeepingService;

    @Autowired
    NotificationTaskService notificationTaskService;

    @Autowired
    EmployeeADService employeeADService;


    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @GetMapping("/assignedDocAuto")
    public ResponseEntity<String> assignedDocAuto() {
        return documentTaskService.assignedDocAuto();
    }

    @GetMapping("/sendDemandBook")
    public ResponseEntity<String> sendDemandBook() {
        return documentTaskService.sendDemandBook();
    }

    @GetMapping("/houseKeeping")
    public void deleteDataByDay() {
        houseKeepingService.deleteDataByDay();
    }

    @GetMapping("/notification")
    public void notification(@RequestParam("processType")String processType) {
        notificationTaskService.notification(processType);
    }
}
