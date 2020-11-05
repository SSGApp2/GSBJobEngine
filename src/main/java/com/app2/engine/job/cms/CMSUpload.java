package com.app2.engine.job.cms;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
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

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Transactional
    @Scheduled(cron = "0 0 21 * * ?") //ss mm hh every day
    public void SEIZE_INFO() {
        // รับข้อมูลการยึดทรัพย์ (CollSeizeDetail) : รับจากระบบ LEAD
        LOGGER.info("***************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : SEIZEINFO_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CMS.Upload.SEIZE_INFO");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("SEIZEINFO_YYYYMMDD.txt");
        try {
            cmsBatchTaskService.SEIZE_INFO(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");

        }catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 0 21 * * ?") //ss mm hh every day
    public void LEGAL_STATUS() {
        // รับข้อมูลสถานะ Litigation (LitigationStatus) : รับจากระบบ LEAD
        LOGGER.info("***************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : LEGAL_STATUS_YYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CMS.Upload.LEGAL_STATUS");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("LEGAL_STATUS_YYYMMDD.txt");

        try {
            cmsBatchTaskService.LEGAL_STATUS(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");

        }catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 0 22 * * ?") //ss mm hh every day
    public void TBL_MT_COURT() {
        // ข้อมูลศาล : รับจากระบบ LEAD
        LOGGER.info("***************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : LEGAL_STATUS_YYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CMS.Upload.TBL_MT_COURT");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("TBL_MT_COURT_YYYYMMDD.txt");
        try {
            cmsBatchTaskService.TBL_MT_COURT(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");

        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 0 22 * * ?") //ss mm hh every day
    public void TBL_MT_LED() {
        // ข้อมูลสำนักงานบังคับคดี : รับจากระบบ LEAD
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File : TBL_MT_LED_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CMS.Upload.TBL_MT_LED");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("TBL_MT_LED_YYYYMMDD.txt");
        try {
            cmsBatchTaskService.TBL_MT_LED(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");

        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }
}
