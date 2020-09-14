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

import java.util.Date;

@RestController
@RequestMapping("/api/litigationUpdate")
public class LitigationUpdateController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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

    @GetMapping("/cva")
    public void cva(){
        litigationUpdateService.cva();
    }

    @GetMapping("/cvc")
    public void cvc(){
        litigationUpdateService.cvc();
    }

    @GetMapping("/LitigationUpdate_CVO")
    public void LitigationUpdate_CVO(){
//        LOGGER.info("The time is now {}", dateFormat.format(new Date()));
        BatchTransaction batchTransaction = null;
        try {
            batchTransaction=new BatchTransaction();
            batchTransaction.setControllerMethod("LitigationUpdateTask.LitigationUpdate_CVO");
            batchTransaction.setStartDate(DateUtil.getCurrentDate());
            batchTransaction.setName("LitigationUpdate_CVO");
            batchTransaction.setStatus("S");
            litigationUpdateService.LitigationUpdate_CVO();
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error CVO {}", e.getMessage());
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
        }
    }
}
