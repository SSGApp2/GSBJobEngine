package com.app2.engine.job;

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
import org.springframework.web.bind.annotation.GetMapping;

import javax.transaction.Transactional;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Component
public class CBSBatchTask {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    CBSBatchTaskService cbsBatchTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Transactional
    @Scheduled(cron = "0 10 19 * * ?")
    public void lsCollectionStatusTask() {
        //รับข้อมูลสถานะการติดตามหนี้ (Collection Status) : รับจากระบบ LEAD
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start create file get information on the dunning status.");
        LOGGER.info("File : LS_COLLECTION_STATUS_YYYYMMDD.txt");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.lsCollectionStatusTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LS_COLLECTION_STATUS_YYYYMMDD.txt");
            batchTransaction.setStatus("S");
            ResponseEntity<String> response = cbsBatchTaskService.lsCollectionStatusTask();
            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            } else {
                String fileName = response.getBody();
                smbFileService.localFileToRemoteFile(fileName,"CBS");
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

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void stblcntryTask() {
        // ประเทศ : ส่งให้ระบบ LEAD
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start receive file master data country.");
        LOGGER.info("File : STBLCNTRY_YYYYMMDD.txt");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.stblcntryTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("STBLCNTRY_YYYYMMDD.txt");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "01");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.stblcntryTask(fileName);
            masterDataProvinceTask();
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    public void masterDataProvinceTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataProvinceTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataProvinceTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_STBLCNTRY1");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "02");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataProvinceTask(fileName);
            masterDataDistrictTask();
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    public void masterDataDistrictTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataDistrictTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataDistrictTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_ZUTBLDIST");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "03");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataDistrictTask(fileName);
            masterDataSubDistrictTask();
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    public void masterDataSubDistrictTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataSubDistrictTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataSubDistrictTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_ZUTBLSDISTCD");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "04");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataSubDistrictTask(fileName);
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void masterDataBranchTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataBranchTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataBranchTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_UTBLBRCD");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "05");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataBranchTask(fileName);
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void masterDataCostCenterTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataCostCenterTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataCostCenterTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_UTBLCCNTR");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "06");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataCostCenterTask(fileName);
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void masterDataWorkingDaysTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataWorkingDaysTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataWorkingDaysTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_UTBLNBD");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "07");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataWorkingDaysTask(fileName);
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void masterDataHolidayTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataHolidayTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataHolidayTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_UTBLNBD1");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "08");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataHolidayTask(fileName);
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?")//ss mm hh every day
    public void masterDataOUTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataOUTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataOUTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_ZUTBLOUBRCD");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "09");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataOUTask(fileName);
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void masterDataMarketCodeTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataMarketCodeTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataMarketCodeTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_MT_MARKET_CODE");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "10");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataMarketCodeTask(fileName);
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void masterDataProductGroupTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataProductGroupTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataProductGroupTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_MT_PRODUCT_GROUP");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "11");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataProductGroupTask(fileName);
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void masterDataProductSubtypeTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File masterDataProductSubtypeTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataProductSubtypeTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_MT_PRODUCT_SUBTYPE");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "12");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataProductSubtypeTask(fileName);
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void masterDataProductTypeTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataProductTypeTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataProductTypeTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_MT_PRODUCT_TYPE");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "13");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataProductTypeTask(fileName);
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void masterDataTitleTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Copy File MasterDataTitleTask");
        BatchTransaction batchTransaction = null;
        String fileName = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.masterDataTitleTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("MasterData_ZUTBLTITLE");
            batchTransaction.setStatus("S");

            String timeLog = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            String today = timeLog + ".txt";
            ParameterDetail file = parameterDetailRepository.findByParameterAndCode("MASTERDATA_FILE", "16");
            fileName = file.getVariable1() + today;
            smbFileService.remoteFileToLocalFile(fileName,"CBS");
            ResponseEntity<String> response = cbsBatchTaskService.masterDataTitleTask(fileName);
        }
        catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(fileName +"  "+e.getMessage());
            LOGGER.error("Error {}", e.getMessage());
        }
        finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(cron = "0 10 19 * * ?") //ss mm hh every day
    public void zleTask() {
        //รับข้อมูลลูกหนี้ที่ได้รับจากกรมบังคับคดี ที่มีการ update กลุ่ม Restriction ในแต่ละวัน : รับจากระบบ LEAD
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start create file receive information received from the Legal Execution Department with Restriction Group updates each day.");
        LOGGER.info("File : ZLE_YYYYMMDD.txt");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.zleTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("ZLE_YYYYMMDD.txt");
            batchTransaction.setStatus("S");
            ResponseEntity<String> response = cbsBatchTaskService.batchZLETask();
            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            } else {
                String fileName = response.getBody();
                smbFileService.localFileToRemoteFile(fileName,"CBS");
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

    @Transactional
    @Scheduled(cron = "0 30 0 * * ?")
    public void lsAcnTask(){
        //ส่ง Account Data Synchronization : ส่งให้ระบบ LEAD
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start receive file account data synchronization.");
        LOGGER.info("File : LS_ACN_YYYYMMDD.txt");
        BatchTransaction batchTransaction = null;
        try {
            String fileName = "LS_ACN_"+DateUtil.codeCurrentDate()+".txt";
            smbFileService.remoteFileToLocalFile(fileName,"CBS");

            ResponseEntity<String> response = cbsBatchTaskService.lsAcn();

            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.lsAcnTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LS_ACN_YYYYMMDD.txt");
            batchTransaction.setReason(response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody().equals("success")) {
                batchTransaction.setStatus("S");
            }else {
                batchTransaction.setStatus("E");
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

    @Transactional
//    @Scheduled(cron = "0 0 22 * * *") //ss mm hh every day
    public void accountEndLegalUpdateTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Create File accountEndLegalUpdateTask");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.accountEndLegalUpdateTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("accountEndLegalUpdateTask");
            batchTransaction.setStatus("S");
            ResponseEntity<String> response = cbsBatchTaskService.accountEndLegalUpdateTask();
            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            } else {
                String pathFile = response.getBody();
                smbFileService.localFileToRemoteFile(pathFile,"DCMS");
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

    @Transactional
//    @Scheduled(cron = "0 49 14 * * *") //ss mm hh every day
//    @Scheduled(fixedRate = 30000)
    public void createFileTXTRestrictionZLE() {
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info(" createFileTXTRestrictionZLE ");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("CBSBatchTask.createFileTXTRestrictionZLE");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("createFileTxtRestriction");
            batchTransaction.setStatus("S");

            ResponseEntity<String> response = cbsBatchTaskService.createFileTXTRestrictionZLE();

            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            }

            String fileName = response.getBody();
            smbFileService.localFileToRemoteFile(fileName,"CBS");

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

}
