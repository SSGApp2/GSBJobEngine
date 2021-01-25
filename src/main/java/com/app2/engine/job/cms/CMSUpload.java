package com.app2.engine.job.cms;

import com.app2.engine.service.CMSBatchTaskService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CMSUpload {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    CMSBatchTaskService cmsBatchTaskService;

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?") //ss mm hh every day
    public void SEIZE_INFO() {
        // รับข้อมูลการยึดทรัพย์ (CollSeizeDetail) : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : SEIZEINFO_YYYYMMDD.txt");

        cmsBatchTaskService.SEIZE_INFO(DateUtil.codeCurrentDateBeforeOneDay());

        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?") //ss mm hh every day
    public void LEGALSTATUS() {
        // รับข้อมูลสถานะ Litigation (LitigationStatus) : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : LEGALSTATUS_YYYMMDD.txt");

        cmsBatchTaskService.LEGALSTATUS(DateUtil.codeCurrentDateBeforeOneDay());

        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?") //ss mm hh every day
    public void TBL_MT_COURT() {
        // ข้อมูลศาล : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : LEGAL_STATUS_YYYMMDD.txt");

        cmsBatchTaskService.TBL_MT_COURT(DateUtil.codeCurrentDateBeforeOneDay());

        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?") //ss mm hh every day
    public void TBL_MT_LED() {
        // ข้อมูลสำนักงานบังคับคดี : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File : TBL_MT_LED_YYYYMMDD.txt");

        cmsBatchTaskService.TBL_MT_LED(DateUtil.codeCurrentDateBeforeOneDay());

        LOGGER.info("**************************************************************************");
    }
}
