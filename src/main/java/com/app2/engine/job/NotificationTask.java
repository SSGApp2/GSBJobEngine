package com.app2.engine.job;

import com.app2.engine.constant.ApplicationConstant;
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

    @Autowired
    NotificationSettingRepository notificationSettingRepository;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @Autowired
    NotificationTaskService notificationTaskService;

    private Boolean equalTime(String timeDb){
        Date date = new Date();

        String hhCur = String.format("%02d",Integer.valueOf(String.valueOf(date.getHours())));
        String mnCur = String.format("%02d",Integer.valueOf(String.valueOf(date.getMinutes())));

        if(AppUtil.isNotEmpty(timeDb)){
            String hhNoti = String.format("%02d",Integer.valueOf(timeDb.split(":")[0]));
            String mnNoti = String.format("%02d",Integer.valueOf(timeDb.split(":")[1]));

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
        ApplicationConstant.notifyTimeGSB02 = notificationSettingRepository.findByProcessType("2").getNotiTime();
        LOGGER.info("Noti Time GSB02 : "+ApplicationConstant.notifyTimeGSB02);
        ApplicationConstant.notifyTimeGSB03 = notificationSettingRepository.findByProcessType("3").getNotiTime();
        LOGGER.info("Noti Time GSB03 : "+ApplicationConstant.notifyTimeGSB03);
        ApplicationConstant.notifyTimeGSB04 = notificationSettingRepository.findByProcessType("4").getNotiTime();
        LOGGER.info("Noti Time GSB04 : "+ApplicationConstant.notifyTimeGSB04);
        ApplicationConstant.notifyTimeGSB06 = notificationSettingRepository.findByProcessType("6").getNotiTime();
        LOGGER.info("Noti Time GSB06 : "+ApplicationConstant.notifyTimeGSB06);
        ApplicationConstant.notifyTimeGSB07 = notificationSettingRepository.findByProcessType("7").getNotiTime();
        LOGGER.info("Noti Time GSB07 : "+ApplicationConstant.notifyTimeGSB07);
        ApplicationConstant.notifyTimeGSB08 = notificationSettingRepository.findByProcessType("8").getNotiTime();
        LOGGER.info("Noti Time GSB08 : "+ApplicationConstant.notifyTimeGSB08);
        LOGGER.info("***************************************");
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 60 second
    public void notificationGSB02Task() {
        if(this.equalTime(ApplicationConstant.notifyTimeGSB02)){
            this.batchTransactionNotification("2","NotificationTask.notiGSB02Task");
        }
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 60 second
    public void notificationGSB03Task() {
        if(this.equalTime(ApplicationConstant.notifyTimeGSB03)){
            this.batchTransactionNotification("3","NotificationTask.notiGSB03Task");
        }
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 60 second
    public void notificationGSB04Task() {
        if(this.equalTime(ApplicationConstant.notifyTimeGSB04)){
            this.batchTransactionNotification("4","NotificationTask.notiGSB04Task");
        }
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 60 second
    public void notificationGSB06Task() {
        if(this.equalTime(ApplicationConstant.notifyTimeGSB06)){
            this.batchTransactionNotification("6","NotificationTask.notiGSB06Task");
        }
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 60 second
    public void notificationGSB07Task() {
        if(this.equalTime(ApplicationConstant.notifyTimeGSB07)){
            this.batchTransactionNotification("7","NotificationTask.notiGSB07Task");
        }
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 60 second
    public void notificationGSB08Task() {
        if(this.equalTime(ApplicationConstant.notifyTimeGSB08)){
            this.batchTransactionNotification("8","NotificationTask.notiGSB08Task");
        }
    }

    private void batchTransactionNotification(String processType,String method){
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start Task Notification Process : " + processType);
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
