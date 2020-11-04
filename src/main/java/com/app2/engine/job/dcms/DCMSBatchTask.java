package com.app2.engine.job.dcms;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.DCMSBatchTaskService;
import com.app2.engine.service.LitigationUpdateService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.service.WRNService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Component
public class DCMSBatchTask {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    @Autowired
    SmbFileService smbFileService;

    @Autowired
    DCMSBatchTaskService dcmsBatchTaskService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    LitigationUpdateService litigationUpdateService;

    @Autowired
    WRNService wrnService;

    @Transactional
    @Scheduled(cron = "0 50 23 * * ?") //ss mm hh every day
    public void createDocumentAutoByCifDebtor() {
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info(" createDocumentAutoByCifDebtor ");
        BatchTransaction batchTransaction = null;
        try {
            String fileName = "ACN_STARTLEGAL_"+DateUtil.codeCurrentDate()+".txt";

//            smbFileService.remoteFileToLocalFile(fileName,"DCMS");

            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DCMSBatchTask.ACNStartLegal");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("createDocumentAuto");
            batchTransaction.setStatus("S");

            ResponseEntity<String> response = dcmsBatchTaskService.ACNStartLegal();

            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            }

        } catch (Exception e) {
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
    @Scheduled(cron = "0 0 2 * * ?") //ss mm hh every day
    public void WRNConsent() {
        // ส่งข้อมูลบัญชีที่มีรายการแจ้งเตือนกรณีที่ลูกหนี้ที่ศาลมีคำพิพากษาตามยอมทั้งหมด : ส่งให้ระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start create file send account information that case debtor has judgment of court. ");
        LOGGER.info("File : WRN_CONSENT_YYYYMMDD.txt");
        BatchTransaction batchTransaction = null;

        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DCMSBatchTask.WRNConsent");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("WRN_CONSENT_YYYYMMDD.txt");

            wrnService.wrnConsent();

            batchTransaction.setStatus("S");

        } catch (Exception e) {
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
    @Scheduled(cron = "0 0 2 * * ?") //ss mm hh every day
    public void wrnTdr() {
        // ส่งข้อมูลรายการแจ้งเตือนบัญชีปรับปรุงโครงสร้างหนี้หลังคำพิพากษาผิดนัดชำระหนี้ทั้งหมด : ส่งให้ระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start create file send account information change of the debt. ");
        LOGGER.info("File : WRN_TDR_YYYYMMDD.txt");
        BatchTransaction batchTransaction = null;

        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DCMSBatchTask.wrnTdr");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("WRN_TDR_YYYYMMDD.txt");

            wrnService.wrnTDR();

            batchTransaction.setStatus("S");

        } catch (Exception e) {
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
