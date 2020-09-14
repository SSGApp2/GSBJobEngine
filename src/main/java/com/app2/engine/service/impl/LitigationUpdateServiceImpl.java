package com.app2.engine.service.impl;

import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.LitigationUpdateService;
import com.app2.engine.service.SmbFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LitigationUpdateServiceImpl extends AbstractEngineService implements LitigationUpdateService {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmbFileService smbFileService;

    @Value("${Api.LitigationUpdate}")
    private String API_LitigationUpdate;

    @Override
    public void bkc() {
        try {
            // --- Send To GSBEngine Type bkc Create File LitigationUpdate_BKC_YYYYMMDD.csv ---
            ResponseEntity<String> result = getResultByExchange(API_LitigationUpdate + "?type=bkc");
            LOGGER.info(" LitigationUpdate_BKC http status : {}", result.getStatusCode().toString());
            LOGGER.info(" Result : {} ", result.getBody());
            String fileName = result.getBody();
//            String pathName = smbFileService.localFileToRemoteFile(fileName,"DCMS");

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void bko() {
        try {
            // --- Send To GSBEngine Type bko Create File LitigationUpdate_BKO_YYYYMMDD.csv ---
            ResponseEntity<String> result = getResultByExchange(API_LitigationUpdate + "?type=bko");
            LOGGER.info(" LitigationUpdate_BKO http status : {}", result.getStatusCode().toString());
            LOGGER.info(" Result : {} ", result.getBody());
            String fileName = result.getBody();
//            String pathName = smbFileService.localFileToRemoteFile(fileName,"DCMS");

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cva() {
        try {
            // --- Send To GSBEngine Type cva Create File LitigationUpdate_CVA_YYYYMMDD.csv ---
            ResponseEntity<String> result = getResultByExchange(API_LitigationUpdate + "?type=cva");
            LOGGER.info(" LitigationUpdate_CVA http status : {}", result.getStatusCode().toString());
            LOGGER.info(" Result : {} ", result.getBody());
            String fileName = result.getBody();
//            String pathName = smbFileService.localFileToRemoteFile(fileName,"DCMS");

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void LitigationUpdate_CVC() {
        try {
            ResponseEntity<String> result = getResultByExchange(API_LitigationUpdate + "?type=cvc");
            LOGGER.info(" LitigationUpdate_CVC http status : {}", result.getStatusCode().toString());
            LOGGER.info(" Result : {} ", result.getBody());
            String fileName = result.getBody();
//            String pathName = smbFileService.localFileToRemoteFile(fileName,"DCMS");
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cvo() {
        try {
            // --- Send To GSBEngine Type cvo Create File LitigationUpdate_CVO_YYYYMMDD.csv ---
            ResponseEntity<String> result = getResultByExchange(API_LitigationUpdate + "?type=cvo");
            LOGGER.info(" LitigationUpdate_CVO http status : {}", result.getStatusCode().toString());
            LOGGER.info(" Result : {} ", result.getBody());
            String fileName = result.getBody();
//            String pathName = smbFileService.localFileToRemoteFile(fileName,"DCMS");

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
