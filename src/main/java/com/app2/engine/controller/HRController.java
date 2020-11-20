package com.app2.engine.controller;

import com.app2.engine.service.HRDataService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs/hr/")
public class HRController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HRDataService hrDataService;

    @GetMapping("/downloadAll")
    public void downloadAll(@RequestParam(value = "date", required = false) String date) {

        hrDataService.region(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HRREGION.TXT is completed.");

        hrDataService.section(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HRSECTION.TXT is completed.");

        hrDataService.position(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HRPOSITION.TXT is completed.");

        hrDataService.branch(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HRBRANCH.TXT is completed.");

        hrDataService.lineBusiness(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HRDIV.TXT is completed.");

        hrDataService.unit(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HRUNIT.txt is completed.");

        hrDataService.orgGroup(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HrOrgGroup is completed.");

        hrDataService.company(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HrCompany is completed.");

        hrDataService.hrInterface(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HrInterface is completed.");
    }

    @GetMapping("/HrRegion")
    public void HrRegion(@RequestParam(value = "date", required = false) String date) {
        hrDataService.region(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HRREGION.TXT is completed.");
    }

    @GetMapping("/HrSection")
    public void HrSection(@RequestParam(value = "date", required = false) String date) {
        hrDataService.section(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HRSECTION.TXT is completed.");
    }

    @GetMapping("/HrPosition")
    public void HrPosition(@RequestParam(value = "date", required = false) String date) {
        hrDataService.position(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HRPOSITION.TXT is completed.");
    }

    @GetMapping("/HrBranch")
    public void HrBranch(@RequestParam(value = "date", required = false) String date) {
        hrDataService.branch(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HRBRANCH.TXT is completed.");
    }

    @GetMapping("/HrLineBusiness")
    public void HrLineBusiness(@RequestParam(value = "date", required = false) String date) {
        hrDataService.lineBusiness(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HRDIV.TXT is completed.");
    }

    @GetMapping("/HrUnit")
    public void HrUnit(@RequestParam(value = "date", required = false) String date) {
        hrDataService.unit(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HrUnit is completed.");
    }

    @GetMapping("/HrOrgGroup")
    public void HrOrgGroup(@RequestParam(value = "date", required = false) String date) {
        hrDataService.orgGroup(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HrOrgGroup is completed.");
    }

    @GetMapping("/HrCompany")
    public void HrCompany(@RequestParam(value = "date", required = false) String date) {
        hrDataService.company(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HrCompany is completed.");
    }

    @GetMapping("/HrInterface")
    public void HrInterface(@RequestParam(value = "date", required = false) String date) {
        hrDataService.hrInterface(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : HrInterface is completed.");
    }
}
