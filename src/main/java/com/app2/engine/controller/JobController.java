package com.app2.engine.controller;

import com.app2.engine.service.DocumentTaskService;
import com.app2.engine.service.HRDataService;
import com.app2.engine.service.HouseKeepingService;
import com.app2.engine.service.NotificationTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HRDataService hrDataService;

    @Autowired
    DocumentTaskService documentTaskService;

    @Autowired
    HouseKeepingService houseKeepingService;

    @Autowired
    NotificationTaskService notificationTaskService;

    @GetMapping("/HrRegion")
    public void HrRegion() {
        hrDataService.region();
    }

    @GetMapping("/HrSection")
    public void HrSection() {
        hrDataService.section();
    }

    @GetMapping("/HrPosition")
    public void HrPosition() {
        hrDataService.position();
    }

    @GetMapping("/HrBranch")
    public void HrBranch() {
        hrDataService.branch();
    }

    @GetMapping("/HrLineBusiness")
    public void HrLineBusiness() {
        hrDataService.lineBusiness();
    }

    @GetMapping("/HrUnit")
    public void HrUnit() {
        hrDataService.unit();
    }

    @GetMapping("/HrOrgGroup")
    public void HrOrgGroup() {
        hrDataService.orgGroup();
    }

    @GetMapping("/HrCompany")
    public void HrCompany() {
        hrDataService.company();
    }

    @GetMapping("/HrInterface")
    public void HrInterface() {
        hrDataService.hrInterface();
    }


    @GetMapping("/assignedDocAuto")
    public ResponseEntity<String> assignedDocAuto() {
        return documentTaskService.assignedDocAuto();
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
