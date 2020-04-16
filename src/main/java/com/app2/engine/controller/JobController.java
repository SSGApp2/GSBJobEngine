package com.app2.engine.controller;

import com.app2.engine.service.DocumentTaskService;
import com.app2.engine.service.HRDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HRDataService hrDataService;

    @Autowired
    DocumentTaskService documentTaskService;

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
}
