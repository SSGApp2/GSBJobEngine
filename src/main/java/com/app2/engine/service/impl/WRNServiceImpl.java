package com.app2.engine.service.impl;

import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.service.WRNService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class WRNServiceImpl extends AbstractEngineService implements WRNService {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmbFileService smbFileService;

    @Value("${Api.WRN}")
    private String API_WRN;

    @Override
    @Transactional
    public void wrnConsent() {
        try {
            // --- Copy File WRN_CONSENT_YYYYMMDD.txt ---
            String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String fileName = "WRN_CONSENT_" + curDate + ".txt";
//            String pathName = "D:/ProjectInSoft/GSB/WRN_CONSENT_20200714.txt";
            String pathName = smbFileService.remoteFileToLocalFile(fileName,"WRN");
            // --- Send To GSBEngine Type 1 ---
            ResponseEntity<String> result = getResultByExchange(API_WRN + "?type=1&pathFile=" + pathName);
            LOGGER.info(" WRN_CONSENT http status : {}", result.getStatusCode().toString());


        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void wrnTDR() {
        try {
            // --- Copy File WRN_TDR_YYYYMMDD.txt ---
            String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String fileName = "WRN_TDR_" + curDate + ".txt";
//            String pathName = "D:/ProjectInSoft/GSB/WRN_TDR_20200714.txt";
            String pathName = smbFileService.remoteFileToLocalFile(fileName,"WRN");
            // --- Send To GSBEngine Type 2 ---
            ResponseEntity<String> result = getResultByExchange(API_WRN + "?type=2&pathFile=" + pathName);
            LOGGER.info(" WRN_TDR http status : {}", result.getStatusCode().toString());

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
