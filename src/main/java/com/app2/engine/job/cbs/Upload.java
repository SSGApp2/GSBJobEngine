package com.app2.engine.job.cbs;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.entity.app.Parameter;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.repository.ParameterRepository;
import com.app2.engine.repository.custom.DocumentRepositoryCustom;
import com.app2.engine.service.CBSBatchTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.DateUtil;
import com.app2.engine.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class Upload {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    CBSBatchTaskService cbsBatchTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;


    @Transactional
    @Scheduled(cron = "0 0 21 * * ?")
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
    @Scheduled(cron = "0 30 2 * * ?")
    public void ZLE() {
        //รับข้อมูลลูกหนี้ที่ได้รับจากกรมบังคับคดี ที่มีการ update กลุ่ม Restriction ในแต่ละวัน
        LOGGER.info("***************************************");
        LOGGER.info("The time is now : {}", dateFormat.format(new Date()));
        LOGGER.info("Upload to FTP Server.");
        LOGGER.info("File name : ZLE_YYYYMMDD.txt");

        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBS.Upload.ZLE");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("ZLE_YYYYMMDD.txt");
            batchTransaction.setStatus("S");
            ResponseEntity<String> response = cbsBatchTaskService.batchZLETask();
            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            } else {
                String fileName = response.getBody();
//                smbFileService.localFileToRemoteFile(fileName, "CBS");
            }
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
