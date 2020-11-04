package com.app2.engine.service.impl;

import com.app2.engine.entity.app.Debtor;
import com.app2.engine.entity.app.DebtorAccDebtInfo;
import com.app2.engine.entity.app.Document;
import com.app2.engine.entity.app.ParameterDetail;
import com.app2.engine.repository.DebtorAccDebtInfoRepository;
import com.app2.engine.repository.DocumentRepository;
import com.app2.engine.repository.ParameterDetailRepository;
import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.DCMSBatchTaskService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.List;

@Service
public class DCMSBatchTaskServiceImpl extends AbstractEngineService implements DCMSBatchTaskService{
    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Autowired
    DebtorAccDebtInfoRepository debtorAccDebtInfoRepository;

    @Autowired
    DocumentRepository documentRepository;

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

            List<DebtorAccDebtInfo> debtorAccDebtInfoList = debtorAccDebtInfoRepository.findAll();

            for (DebtorAccDebtInfo debtorAccDebtInfo : debtorAccDebtInfoList){
                BigInteger accountNo;
                String wfTypeID = "";
                String wfTypeDesc = "";
                String endDt = "";
                String endStepReasonID = "";
                String endStepReasonDesc = "";
                String description = "";

                Debtor debtor = debtorAccDebtInfo.getDebtor();

                List<Document> documentList = documentRepository.findByDebtor(debtor);

                for (Document document : documentList){

                    accountNo = new BigInteger(debtorAccDebtInfo.getAccountNo());

                    if (AppUtil.isNotNull(document.getDocType())){
                        if (document.getDocType().equals("1")){
                            wfTypeID = "1";
                        }else if (document.getDocType().equals("2")){
                            wfTypeID = "3";
                        }else if (document.getDocType().equals("3")){
                            wfTypeID = "4";
                        }else if (document.getDocType().equals("4")){
                            wfTypeID = "5";
                        }else if (document.getDocType().equals("5")){
                            wfTypeID = "6";
                        }
                    }

                    String dataStr = accountNo+"|"+wfTypeID+"|"+wfTypeDesc+"|"+endDt+"|"+endStepReasonID+"|"+endStepReasonDesc+"|"+description;
                    writer.write(dataStr);
                    writer.newLine();
                }
            }
            writer.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);

        }catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}


