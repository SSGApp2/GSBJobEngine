package com.app2.engine.job;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
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
public class DataHRTask {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HRDataService hrDataService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "0 30 0 * * *") //ss mm hh every day
    public void hrDataTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        BatchTransaction batchTransaction = null;
        ///////////////////////////////////////////////////////////////////////////////////
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("DataHRTask.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("position");
            batchTransaction.setStatus("S");
            hrDataService.position();
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
            batchTransaction.setControllerMethod("DataHRTask.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("region");
            batchTransaction.setStatus("S");
            hrDataService.region();
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
            batchTransaction.setControllerMethod("DataHRTask.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("section");
            batchTransaction.setStatus("S");
            hrDataService.section();
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
            batchTransaction.setControllerMethod("DataHRTask.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("branch");
            batchTransaction.setStatus("S");
            hrDataService.branch();
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
            batchTransaction.setControllerMethod("DataHRTask.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("lineBusiness");
            batchTransaction.setStatus("S");
            hrDataService.lineBusiness();
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
            batchTransaction.setControllerMethod("DataHRTask.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("unit");
            batchTransaction.setStatus("S");
            hrDataService.unit();
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
            batchTransaction.setControllerMethod("DataHRTask.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("orgGroup");
            batchTransaction.setStatus("S");
            hrDataService.orgGroup();
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
            batchTransaction.setControllerMethod("DataHRTask.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("company");
            batchTransaction.setStatus("S");
            hrDataService.company();
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
            batchTransaction.setControllerMethod("DataHRTask.hrDataTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("hrInterface");
            batchTransaction.setStatus("S");
            hrDataService.hrInterface();
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
