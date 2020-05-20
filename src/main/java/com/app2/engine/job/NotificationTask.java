package com.app2.engine.job;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.entity.app.NotificationSetting;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.repository.NotificationSettingRepository;
import com.app2.engine.service.NotificationTaskService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationTask {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private static String notiTimeGSB02 = "";
    private static String notiTimeGSB03 = "";
    private static String notiTimeGSB04 = "";
    private static String notiTimeGSB06 = "";
    private static String notiTimeGSB07 = "";
    private static String notiTimeGSB08 = "";

    @Autowired
    NotificationSettingRepository notificationSettingRepository;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    NotificationTaskService notificationTaskService;

    private Boolean equalTime(String timeDb){
        Date date = new Date();

        String hhCur = String.valueOf(date.getHours());
        String mnCur = String.valueOf(date.getMinutes());

        if(AppUtil.isNotEmpty(timeDb)){
            String hhNoti = timeDb.split(":")[0];
            String mnNoti = timeDb.split(":")[1];

            if(hhNoti.equals(hhCur)){
                if(mnNoti.equals(mnCur)){
                    return true;
                }
            }
        }

        return false;
    }

    @Transactional
    @Scheduled(cron = "0 1 0 * * ?")
    public void setNotiTimeFromDatabase() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start set noti time from db");
        notiTimeGSB02 = notificationSettingRepository.findByProcessType("2").getNotiTime();
        LOGGER.info("Noti Time GSB02 : "+notiTimeGSB02);
        notiTimeGSB03 = notificationSettingRepository.findByProcessType("3").getNotiTime();
        LOGGER.info("Noti Time GSB03 : "+notiTimeGSB03);
        notiTimeGSB04 = notificationSettingRepository.findByProcessType("4").getNotiTime();
        LOGGER.info("Noti Time GSB04 : "+notiTimeGSB04);
        notiTimeGSB06 = notificationSettingRepository.findByProcessType("6").getNotiTime();
        LOGGER.info("Noti Time GSB06 : "+notiTimeGSB06);
        notiTimeGSB07 = notificationSettingRepository.findByProcessType("7").getNotiTime();
        LOGGER.info("Noti Time GSB07 : "+notiTimeGSB07);
        notiTimeGSB08 = notificationSettingRepository.findByProcessType("8").getNotiTime();
        LOGGER.info("Noti Time GSB08 : "+notiTimeGSB08);
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(fixedDelay = 60000) // 60 second
    public void notificationTask() {

        if(this.equalTime(notiTimeGSB02)){
            this.batchTransactionNotification("2","NotificationTask.notiGSB02Task");
        }

        if(this.equalTime(notiTimeGSB03)){
            this.batchTransactionNotification("3","NotificationTask.notiGSB03Task");
        }

        if(this.equalTime(notiTimeGSB04)){
            this.batchTransactionNotification("4","NotificationTask.notiGSB04Task");
        }

        if(this.equalTime(notiTimeGSB06)){
            this.batchTransactionNotification("6","NotificationTask.notiGSB06Task");
        }

        if(this.equalTime(notiTimeGSB07)){
            this.batchTransactionNotification("7","NotificationTask.notiGSB07Task");
        }

        if(this.equalTime(notiTimeGSB08)){
            this.batchTransactionNotification("8","NotificationTask.notiGSB08Task");
        }

    }

    private void batchTransactionNotification(String processType,String method){
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start notification process "+processType+" task");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod(method);
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("notification");
            batchTransaction.setStatus("S");
            ResponseEntity<String> response = notificationTaskService.notification(processType);
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

        LOGGER.info("***************************************");
    }

}
