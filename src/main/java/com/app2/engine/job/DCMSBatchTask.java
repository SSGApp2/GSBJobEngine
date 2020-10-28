package com.app2.engine.job;

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

            smbFileService.remoteFileToLocalFile(fileName,"DCMS");

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
    @Scheduled(cron = "0 30 20 * * ?") //ss mm hh every day
    public void ACNEndLegal() {
        // รับข้อมูล Account update ทางคดี และสิ้นสุดคดี (AccountEndLegal) : รับจากระบบ LEAD
        LOGGER.info("**************************************************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info(" ACNEndLegal ");
        LOGGER.info("Start create file get account case update and end case. ");
        LOGGER.info("File : ACN_ENDLEGAL_YYYYMMDD.txt");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("DCMSBatchTask.ACNEndLegal");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("ACN_ENDLEGAL_YYYYMMDD.txt");
            batchTransaction.setStatus("S");

            ResponseEntity<String> response = dcmsBatchTaskService.ACNEndLegal();

            if (!response.getStatusCode().is2xxSuccessful()) {
                batchTransaction.setStatus("E");
                batchTransaction.setReason(response.getBody());
            }

            String fileName = response.getBody();
            smbFileService.localFileToRemoteFile(fileName,"DCMS");

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
    @Scheduled(cron = "0 30 20 * * ?")  //ss mm hh every day
    public void LitigationUpdateBKC() {
        // BKC ----------------------------------------------------
        // รับรายละเอียดข้อมูลแฟ้มดำเนินคดีล้มละลายที่มีการ update ในแต่ละวัน : รับจากระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start create file LitigationUpdateBKC . ");
        BatchTransaction batchTransaction = null;

        try {
            LOGGER.info("File : LitigationUpdate_BKC_YYYYMMDD.csv");
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("DCMSBatchTask.LitigationUpdateBKC");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdate_BKC_YYYYMMDD.csv");
            batchTransaction.setStatus("S");
            litigationUpdateService.litigationUpdateBKC();
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error BKC {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
    }

    @Transactional
    @Scheduled(cron = "0 30 20 * * ?")  //ss mm hh every day
    public void LitigationUpdateBKO() {
        // BKO ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีเจ้าหนี้นอกฟ้องลูกหนี้ธนาคารในคดีล้มละลายที่มีการ update : รับจากระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start create file LitigationUpdateBKO . ");
        LOGGER.info("File : LitigationUpdate_BKO_YYYYMMDD.csv");
        BatchTransaction batchTransaction = null;

        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("DCMSBatchTask.LitigationUpdateBKO");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdate_BKO_YYYYMMDD.csv");
            batchTransaction.setStatus("S");
            litigationUpdateService.litigationUpdateBKO();
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error BKO {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 30 20 * * ?")  //ss mm hh every day
    public void LitigationUpdateCVC() {
        // CVA ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีแพ่งที่มีการ update : รับจากระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start create file LitigationUpdateCVC . ");
        LOGGER.info("File : LitigationUpdate_CVC_YYYYMMDD.csv");
        BatchTransaction batchTransaction = null;

        // CVC ----------------------------------------------------
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("DCMSBatchTask.LitigationUpdateCVC");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdate_CVC_YYYYMMDD.csv");
            batchTransaction.setStatus("S");
            litigationUpdateService.litigationUpdateCVC();
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error CVC {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 30 20 * * ?")  //ss mm hh every day
    public void LitigationUpdateCVA() {
        // CVA ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีลูกหนี้ผิดนัดหลังคำพิพากษาที่มีการ update : รับจากระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start create file LitigationUpdateCVA . ");
        LOGGER.info("File : LitigationUpdate_CVA_YYYYMMDD.csv");
        BatchTransaction batchTransaction = null;

        try {
            LOGGER.info("File : LitigationUpdate_CVA_YYYYMMDD.csv");
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("DCMSBatchTask.LitigationUpdateCVA");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdate_CVA_YYYYMMDD.csv");
            batchTransaction.setStatus("S");
            litigationUpdateService.litigationUpdateCVA();
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error litigationUpdate_CVA {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
        LOGGER.info("**************************************************************************");
    }

    @Transactional
    @Scheduled(cron = "0 30 20 * * ?")  //ss mm hh every day
    public void LitigationUpdateCVO() {
        // CVO ----------------------------------------------------
        // รับข้อมูลแฟ้มดำเนินคดีเจ้าหนี้นอกยึดทรัพย์หลักประกันลูกหนี้ธนาคารที่มีการ update ให้ระบบ : รับจากระบบ LEAD
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start create file LitigationUpdateCVO . ");
        LOGGER.info("File : LitigationUpdate_CVO_YYYYMMDD.csv");
        BatchTransaction batchTransaction = null;

        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("DCMSBatchTask.batchLitigationUpdateCVO");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdate_CVO_YYYYMMDD.csv");
            batchTransaction.setStatus("S");
            litigationUpdateService.litigationUpdateCVO();
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error CVO {}", e.getMessage());
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
