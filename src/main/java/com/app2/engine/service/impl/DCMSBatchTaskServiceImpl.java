package com.app2.engine.service.impl;

import com.app2.engine.entity.app.Debtor;
import com.app2.engine.entity.app.DebtorAccDebtInfo;
import com.app2.engine.entity.app.Document;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.DebtorAccDebtInfoRepository;
import com.app2.engine.repository.DocumentRepository;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.repository.custom.DebtorAccDebtInfoRepositoryCustom;
import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.DCMSBatchTaskService;
import com.app2.engine.service.DocumentService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.FileUtil;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DCMSBatchTaskServiceImpl extends AbstractEngineService implements DCMSBatchTaskService {
    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Autowired
    DebtorAccDebtInfoRepository debtorAccDebtInfoRepository;

    @Autowired
    DebtorAccDebtInfoRepositoryCustom debtorAccDebtInfoRepositoryCustom;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    DocumentService documentService;

    @Value("${sync.interface}")
    private String syncInterface;

    @Override
    public ResponseEntity<String> ACNStartLegal() {
        String url = "/jobs/ACNStartLegal";
        return getResultByExchange(url);
    }

    @Override
    public void ACN_END_LEGAL(String date) {
        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);

            String fileName = "ACN_ENDLEGAL_" + date + ".txt";

            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/" + fileName));

            List<Map> debtorAccDebtInfoList = debtorAccDebtInfoRepositoryCustom.findAcnEndLegal();

            for (Map debtorAccDebtInfo : debtorAccDebtInfoList) {
                BigInteger accountNo;
                String wfTypeID = "";
                String wfTypeDesc = "";
                String endDt = "";
                String endStepReasonID = "";
                String endStepReasonDesc = "";
                String description = "";

                accountNo = new BigInteger(String.valueOf(debtorAccDebtInfo.get("accountNo")));

                if (AppUtil.isNotNull(debtorAccDebtInfo.get("docType"))) {
                    String docType = String.valueOf(debtorAccDebtInfo.get("docType"));

                    if (docType.equals("1")) {
                        wfTypeID = "1";
                    } else if (docType.equals("2")) {
                        wfTypeID = "3";
                    } else if (docType.equals("3")) {
                        wfTypeID = "4";
                    } else if (docType.equals("4")) {
                        wfTypeID = "5";
                    } else if (docType.equals("5")) {
                        wfTypeID = "6";
                    }
                }

                String dataStr = accountNo + "|" + wfTypeID + "|" + wfTypeDesc + "|" + endDt + "|" + endStepReasonID + "|" + endStepReasonDesc + "|" + description;
                writer.write(dataStr);
                writer.newLine();
            }
            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void ACN_END_LEGAL_TOTAL(String date) {
        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail params = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL", "01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String path = FileUtil.isNotExistsDirCreated(params.getVariable2(), date);

            String fileName = "ACN_ENDLEGAL_TOTAL_" + date + ".txt";
            int count = 0;

            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/" + fileName));

            List<Map> debtorAccDebtInfoList = debtorAccDebtInfoRepositoryCustom.findAcnEndLegal();

            if (!debtorAccDebtInfoList.isEmpty()) {
                count = debtorAccDebtInfoList.size();
            }

            writer.write(String.valueOf(count));
            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(),e);
            throw new RuntimeException(e.getMessage());
        }
    }
}


