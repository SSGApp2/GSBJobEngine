package com.app2.engine.job;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.HouseKeepingService;
import com.app2.engine.util.DateUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class HouseKeepingTask {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    HouseKeepingService houseKeepingService;

    @Autowired
    BatchTransactionRepository batchTransactionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Scheduled(cron = "0 30 0 * * *") //ss mm hh every day
    public void deleteAppuserHistoryTask() {
        LOGGER.info("***************************************");
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        LOGGER.info("Start deleteAppuserHistoryTask");
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction = new BatchTransaction();
            batchTransaction.setControllerMethod("HouseKeepingTask.deleteAppuserHistoryTask");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("houseKeeping");
            batchTransaction.setStatus("S");
            houseKeepingService.deleteDataByDay();
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
    @Scheduled(cron = "0 1 0 * * *") //ss mm hh every day
    public void setAppUserLoginWrongTask() {
//        LOGGER.info("***************************************");
//        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
//        LOGGER.info("Start task1 ");
//        houseKeepingService.UpdateLoginWrong();
        Date currentDate = DateUtil.getCurrentDate();
        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();
        querySql.append("UPDATE app_user  SET login_wrong = 0 ,updated_by = 'GSBJobEngine',updated_date=:updated_date");
        LOGGER.debug("SQL Query {}", querySql.toString());
        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setParameter("updated_date",currentDate);
        query.executeUpdate();

        }
}
