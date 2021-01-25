package com.app2.engine.controller;


import com.app2.engine.service.DocumentTaskService;
import com.app2.engine.service.HouseKeepingService;
import com.app2.engine.service.NotificationTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    @Autowired
    DocumentTaskService documentTaskService;

    @Autowired
    HouseKeepingService houseKeepingService;

    @Autowired
    NotificationTaskService notificationTaskService;

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
    public void notification(@RequestParam("processType") String processType) {
        notificationTaskService.notification(processType);
    }
}
