package com.app2.engine.job.cbs;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.service.CBSBatchTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class Download {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    CBSBatchTaskService cbsBatchTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;


    @Transactional
//    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
//    @Scheduled(fixedRate = 60000) // 60 second
    public void MASTER_DATA_BRANCH() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : UTBLBRCD_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_BRANCH");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("UTBLBRCD_YYYYMMDD.txt");
        try {
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "05");
            String fileName = file.getVariable1() + DateUtil.codeCurrentDate() +".txt";
            smbFileService.remoteFileToLocalFile(fileName,"CBS",DateUtil.codeCurrentDate());
            cbsBatchTaskService.masterDataBranchTask(fileName,DateUtil.codeCurrentDate());
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
//    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
//    @Scheduled(fixedRate = 60000) // 60 second
    public void MASTER_DATA_COST_CENTER() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : UTBLCCNTR_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_COST_CENTER");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("UTBLCCNTR_YYYYMMDD.txt");

        try {
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "06");
            String fileName = file.getVariable1() + DateUtil.codeCurrentDate() +".txt";
            smbFileService.remoteFileToLocalFile(fileName,"CBS",DateUtil.codeCurrentDate());
            cbsBatchTaskService.masterDataCostCenterTask(fileName,DateUtil.codeCurrentDate());
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
//    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
//    @Scheduled(fixedRate = 60000) // 60 second
    public void MASTER_DATA_WORKING_DAYS() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : UTBLNBD_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_WORKING_DAYS");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("UTBLNBD_YYYYMMDD.txt");

        try {
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "07");
            String fileName = file.getVariable1() + DateUtil.codeCurrentDate() +".txt";
            smbFileService.remoteFileToLocalFile(fileName,"CBS",DateUtil.codeCurrentDate());
            cbsBatchTaskService.masterDataWorkingDaysTask(fileName,DateUtil.codeCurrentDate());
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
//    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
//    @Scheduled(fixedRate = 60000) // 60 second
    public void MASTER_DATA_HOLIDAY() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : UTBLNBD1_YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_HOLIDAY");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("UTBLNBD1_YYYYMMDD.txt");

        try {
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "08");
            String fileName = file.getVariable1() + DateUtil.codeCurrentDate() +".txt";
            smbFileService.remoteFileToLocalFile(fileName,"CBS",DateUtil.codeCurrentDate());
            cbsBatchTaskService.masterDataHolidayTask(fileName,DateUtil.codeCurrentDate());
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
//    @Scheduled(cron = "0 50 23 * * ?")//ss mm hh every day
//    @Scheduled(fixedRate = 60000) // 60 second
    public void MASTER_DATA_OU() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("File name : ZUTBLOUBRCD__YYYYMMDD.txt");

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("CBS.Download.MASTER_DATA_OU");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("ZUTBLOUBRCD__YYYYMMDD.txt");

        try {
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "09");
            String fileName = file.getVariable1() + DateUtil.codeCurrentDate() +".txt";
            smbFileService.remoteFileToLocalFile(fileName,"CBS",DateUtil.codeCurrentDate());
            cbsBatchTaskService.masterDataOUTask(fileName,DateUtil.codeCurrentDate());
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
