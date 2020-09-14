package com.app2.engine.controller;

import com.app2.engine.entity.app.BatchTransaction;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.service.LitigationUpdateService;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/litigationUpdate")
public class LitigationUpdateController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private BatchTransactionRepository batchTransactionRepository;

    @Autowired
    private LitigationUpdateService litigationUpdateService;

    @GetMapping("/bkc")
    public void bkc(){
        litigationUpdateService.bkc();
    }

    @GetMapping("/bko")
    public void bko(){
        litigationUpdateService.bko();
    }


    @GetMapping("/litigationUpdate_CVA")
    public void litigationUpdate_CVA(){
        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("LitigationUpdateTask.batchLitigationUpdate_CVA");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdateCVA");
            batchTransaction.setStatus("S");
            litigationUpdateService.litigationUpdate_CVA();
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error litigationUpdate_CVA {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
    }

    @GetMapping("/cvc")
    public void cvc(){
        litigationUpdateService.cvc();
    }

    @GetMapping("/cvo")
    public void cvo(){
        litigationUpdateService.cvo();
    }
}
