package com.app2.engine.job.cbs;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.CBSBatchTaskService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class CBSUpload {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    CBSBatchTaskService cbsBatchTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?")
    public void LS_COLLECTION_STATUS() {
        //ส่งข้อมูลสถานะการติดตามหนี้
        LOGGER.info("***************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : LS_COLLECTION_STATUS_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Upload.LS_COLLECTION_STATUS");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("LS_COLLECTION_STATUS_YYYYMMDD.txt");
        try {
            cbsBatchTaskService.LS_COLLECTION_STATUS(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage(), e);
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }

        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?")
    public void ZLE() {
        //รับข้อมูลลูกหนี้ที่ได้รับจากกรมบังคับคดี ที่มีการ update กลุ่ม Restriction ในแต่ละวัน
        LOGGER.info("***************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : ZLE_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Upload.ZLE");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("ZLE_YYYYMMDD.txt");

        try {
            cbsBatchTaskService.ZLE(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage(), e);
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 19 * * ?")
    public void LS_ACCOUNT_LIST() {
        //รับข้อมูลบัญชีทั้งหมดที่ถูกดำเนินคดี
        LOGGER.info("***************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : LS_ACCOUNTLIST_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Upload.LS_ACCOUNT_LIST");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("LS_ACCOUNTLIST_YYYYMMDD.txt");
        try {
            cbsBatchTaskService.LS_ACCOUNT_LIST(DateUtil.codeCurrentDate());
            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage(), e);
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }

        LOGGER.info("***************************************");
    }
}
