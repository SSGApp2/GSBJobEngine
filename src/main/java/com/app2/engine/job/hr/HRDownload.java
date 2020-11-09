package com.app2.engine.job.hr;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.EmployeeADService;
import com.app2.engine.service.HRDataService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class HRDownload {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HRDataService hrDataService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    EmployeeADService employeeADService;


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "0 30 5 * * ?") //ss mm hh every day
    public void hrDataTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Download to FTP Server.");
        LOGGER.info("Batch HR All");
        
        BatchTransaction batchTransaction = null;
        ///////////////////////////////////////////////////////////////////////////////////
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("HR.Download.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("position");
            batchTransaction.setStatus("S");
            hrDataService.position(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        ///////////////////////////////////////////////////////////////////////////////////
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("HR.Download.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("region");
            batchTransaction.setStatus("S");
            hrDataService.region(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        ///////////////////////////////////////////////////////////////////////////////////
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("HR.Download.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("section");
            batchTransaction.setStatus("S");
            hrDataService.section(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        ///////////////////////////////////////////////////////////////////////////////////
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("HR.Download.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("branch");
            batchTransaction.setStatus("S");
            hrDataService.branch(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        ///////////////////////////////////////////////////////////////////////////////////
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("HR.Download.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("lineBusiness");
            batchTransaction.setStatus("S");
            hrDataService.lineBusiness(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        ///////////////////////////////////////////////////////////////////////////////////
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("HR.Download.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("unit");
            batchTransaction.setStatus("S");
            hrDataService.unit(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        ///////////////////////////////////////////////////////////////////////////////////
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("HR.Download.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("orgGroup");
            batchTransaction.setStatus("S");
            hrDataService.orgGroup(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        ///////////////////////////////////////////////////////////////////////////////////
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("HR.Download.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("company");
            batchTransaction.setStatus("S");
            hrDataService.company(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        ///////////////////////////////////////////////////////////////////////////////////
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("HR.Download.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("hrInterface");
            batchTransaction.setStatus("S");
            hrDataService.hrInterface(DateUtil.codeCurrentDate());
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        ///////////////////////////////////////////////////////////////////////////////////
        LOGGER.info("***************************************");
    }
}
