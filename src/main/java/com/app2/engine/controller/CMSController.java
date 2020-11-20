package com.app2.engine.controller;

import com.app2.engine.service.CMSBatchTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs/cms/")
public class CMSController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CMSBatchTaskService cmsBatchTaskService;

    @Autowired
    SmbFileService smbFileService;

    @GetMapping("uploadAll")
    public void uploadAll(@RequestParam(value = "date", required = false) String date) {
        // รับข้อมูลสถานะ Litigation (LitigationStatus) : รับจากระบบ LEAD
        cmsBatchTaskService.LEGALSTATUS(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : LEGALSTATUS is completed.");

        // รับข้อมูลการยึดทรัพย์ (CollSeizeDetail) : รับจากระบบ LEAD
        cmsBatchTaskService.SEIZE_INFO(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : SEIZE_INFO is completed.");

        // ข้อมูลศาล : รับจากระบบ LEAD
        cmsBatchTaskService.TBL_MT_COURT(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : TBL_MT_COURT is completed.");

        // ข้อมูลสำนักงานบังคับคดี : รับจากระบบ LEAD
        cmsBatchTaskService.TBL_MT_LED(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : TBL_MT_LED is completed.");
    }

    @GetMapping("legalStatus")
    public void legalStatus(@RequestParam(value = "date", required = false) String date) {
        cmsBatchTaskService.LEGALSTATUS(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : LEGALSTATUS is completed.");
    }

    @GetMapping("seizeInfo")
    public void seizeInfo(@RequestParam(value = "date", required = false) String date) {
        cmsBatchTaskService.SEIZE_INFO(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : SEIZE_INFO is completed.");
    }

    @GetMapping("tblMtCourtTask")
    public void tblMtCourtTask(@RequestParam(value = "date", required = false) String date) {
        cmsBatchTaskService.TBL_MT_COURT(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : TBL_MT_COURT is completed.");
    }

    @GetMapping("tblMtLedTask")
    public void tblMtLedTask(@RequestParam(value = "date", required = false) String date) {
        cmsBatchTaskService.TBL_MT_LED(date != null ? date : DateUtil.codeCurrentDate());
        LOGGER.debug("Batch : TBL_MT_LED is completed.");
    }
}
