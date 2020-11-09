package com.app2.engine.controller;

import com.app2.engine.service.EmployeeADService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs/ad/")
public class ADController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EmployeeADService employeeADService;

    @GetMapping("/downloadAll")
    public void downloadAll(@RequestParam(value = "date", required = false) String date) {

        employeeADService.InsertOrUpdateEmp(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : InsertOrUpdateEmp is completed.");

    }

    @GetMapping("/InsertOrUpdateEmp")
    public void InsertOrUpdateEmp(@RequestParam(value = "date", required = false) String date) {
        employeeADService.InsertOrUpdateEmp(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : InsertOrUpdateEmp is completed.");
    }
}
