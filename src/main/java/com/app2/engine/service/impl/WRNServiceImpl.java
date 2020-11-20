package com.app2.engine.service.impl;

import com.app2.engine.entity.app.*;
import com.app2.engine.repository.BatchTransactionRepository;
import com.app2.engine.repository.DebtorAccDebtInfoRepository;
import com.app2.engine.repository.custom.DebtorAccDebtInfoRepositoryCustom;
import com.app2.engine.repository.DocumentProgressRepository;
import com.app2.engine.repository.DocumentRepository;
import com.app2.engine.repository.custom.DocumentProgressRepositoryCustom;
import com.app2.engine.repository.custom.EmpDebtAccInfoRepositoryCustom;
import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.service.WRNService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WRNServiceImpl extends AbstractEngineService implements WRNService {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmbFileService smbFileService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DebtorAccDebtInfoRepository debtorAccDebtInfoRepository;

    @Autowired
    private DebtorAccDebtInfoRepositoryCustom debtorAccDebtInfoRepositoryCustom;

    @Autowired
    private EmpDebtAccInfoRepositoryCustom empDebtAccInfoRepositoryCustom;

    @Autowired
    private DocumentProgressRepositoryCustom documentProgressRepositoryCustom;

    @Autowired
    private DocumentProgressRepository documentProgressRepository;

    @Autowired
    private BatchTransactionRepository batchTransactionRepository;

    @Override
    @Transactional
    public void WRN_CONSENT(String date) {
        BufferedReader bfReader = null;

        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setControllerMethod("DCMS.Download.WRN_CONSENT");
        batchTransaction.setStartDate(DateUtil.getCurrentDate());
        batchTransaction.setName("WRN_CONSENT_YYYYMMDD.txt");

        try {
            // --- Copy File WRN_CONSENT_YYYYMMDD.txt ---
            String fileName = "WRN_CONSENT_" + date + ".txt";

            String pathFile = smbFileService.remoteFileToLocalFile(fileName, "DCMS", date);

            if (pathFile.equals("") || pathFile.isEmpty()) {
                LOGGER.error("  WRN_CONSENT Not have Path File : {}", pathFile);
            } else {
                // --- WRN_CONSENT_YYYYMMDD.txt ---
                if (Files.exists(Paths.get(pathFile))) {
                    LOGGER.info("  WRN_CONSENT Path File : {}", pathFile);
                    bfReader = new BufferedReader(new InputStreamReader(new FileInputStream(pathFile), "UTF-8"));
                    List<Map<String, Object>> listMap = new ArrayList<>();

                    String delimeter = "\\|";
                    int length = 7;
                    int rowNumber = 0;
                    String line;
                    while ((line = bfReader.readLine()) != null) {
                        String lineArr[] = line.split(delimeter);
                        if (lineArr.length >= length && rowNumber > 0) {
                            Map<String, Object> objectMap = new HashMap<>();
                            objectMap.put("ACCT_NO", lineArr[0]);
                            objectMap.put("ACCT_NAME", lineArr[1]);
                            objectMap.put("OUT_BAL", lineArr[2]);
                            objectMap.put("TDU_AMT", lineArr[3]);
                            objectMap.put("JUDGMENT_DT", lineArr[4]);
                            objectMap.put("TDR_DT", lineArr[5]);
                            objectMap.put("BRANCH_CD", lineArr[6]);

                            listMap.add(objectMap);
                        }
                        rowNumber++;
                    }

                    if (listMap.size() > 0) {
                        updateWRN(listMap, "1");
                    }
                } else {
                    LOGGER.error(" WRN_CONSENT Not find file from path : {}", pathFile);
                }
            }
            batchTransaction.setStatus("S");
        } catch (Exception e) {
            batchTransaction.setStatus("E");
            batchTransaction.setReason(e.getMessage());
            LOGGER.error("Error : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            batchTransaction.setEndDate(DateUtil.getCurrentDate());
            batchTransactionRepository.saveAndFlush(batchTransaction);
            if (bfReader != null) {
                try {
                    bfReader.close();
                } catch (IOException e) {
                    LOGGER.error("Error : {}", e.getMessage(), e);
                }
            }
        }
    }

    @Override
    @Transactional
    public void WRN_TDR(String date) {
        BufferedReader bfReader = null;
        try {
            // --- Copy File WRN_TDR_YYYYMMDD.txt ---
            String fileName = "WRN_TDR_" + date + ".txt";

            String pathFile = smbFileService.remoteFileToLocalFile(fileName, "DCMS", date);

            if (pathFile.equals("") || pathFile.isEmpty()) {
                LOGGER.error("  WRN_TDR Not have Path File : {}", pathFile);
            } else {
                // --- WRN_TDR_YYYYMMDD.txt ---
                if(Files.exists(Paths.get(pathFile))) {
                    LOGGER.info("  WRN_TDR Path File : {}", pathFile);
                    bfReader = new BufferedReader(new InputStreamReader(new FileInputStream(pathFile), "UTF-8"));
                    List<Map<String, Object>> listMap = new ArrayList<>();

                    String delimeter = "\\|";
                    int length = 7;
                    int rowNumber = 0;
                    String line;
                    while ((line = bfReader.readLine()) != null) {
                        String lineArr[] = line.split(delimeter);
                        if (lineArr.length >= length && rowNumber > 0) {
                            Map<String, Object> objectMap = new HashMap<>();
                            objectMap.put("ACCT_NO", lineArr[0]);
                            objectMap.put("ACCT_NAME", lineArr[1]);
                            objectMap.put("OUT_BAL", lineArr[2]);
                            objectMap.put("TDU_AMT", lineArr[3]);
                            objectMap.put("JUDGMENT_DT", lineArr[4]);
                            objectMap.put("TDR_DT", lineArr[5]);
                            objectMap.put("BRANCH_CD", lineArr[6]);

                            listMap.add(objectMap);
                        }
                        rowNumber++;
                    }

                    if (listMap.size() > 0) {
                        updateWRN(listMap, "2");
                    }
                }else{
                    LOGGER.error("  WRN_TDR Not find file from path : {}", pathFile);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if (bfReader != null) {
                try {
                    bfReader.close();
                } catch (IOException e) {
                    LOGGER.error("Error {}", e.getMessage(), e);
                }
            }
        }
    }

    public void updateWRN(List<Map<String, Object>> mapList, String trdFlag) {
        try {

            for (Map<String, Object> wrnMap : mapList) {
                // --- Update DebtorAccDebtInfo ---
                String tdr_dt = wrnMap.get("TDR_DT").toString();
                if (!tdr_dt.isEmpty() && !"".equals(tdr_dt)) {
                    DebtorAccDebtInfo debtorAccDebtInfo = debtorAccDebtInfoRepositoryCustom.findByAccountNo(wrnMap.get("ACCT_NO").toString());
                    if (debtorAccDebtInfo != null) {
                        debtorAccDebtInfo.setTdrFlag(trdFlag);
                        Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(tdr_dt);
                        debtorAccDebtInfo.setTdrDate(date);
                        debtorAccDebtInfoRepository.saveAndFlush(debtorAccDebtInfo);
                    }
                }

                List<EmpDebtAccInfo> empDebtAccInfolist1 = empDebtAccInfoRepositoryCustom.findByAccountNo(wrnMap.get("ACCT_NO").toString());

                for (EmpDebtAccInfo empDebtAccInfo1 : empDebtAccInfolist1) {
                    if (empDebtAccInfo1.getDocument() != null) {
                        Long documentId = empDebtAccInfo1.getDocument().getId();
                        List<EmpDebtAccInfo> empDebtAccInfolist2 = empDebtAccInfoRepositoryCustom.findDocumentId(documentId);
                        Double balance = 0D;
                        Double interest = 0D;
                        Date JUDGMENT_DT_Date;
                        Date JUDGMENT_DT_Date_MAX = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse("01/01/2001");

                        // --- หาบัญชีที่อยู่ใน document เดียวกันมา update ---
                        for (EmpDebtAccInfo empDebtAccInfo2 : empDebtAccInfolist2) {
                            for (Map<String, Object> wrnMap2 : mapList) {
                                if (empDebtAccInfo2.getDebtorMapAccount().getAccountNo().equals(wrnMap2.get("ACCT_NO").toString())) {
                                    balance += Double.parseDouble(wrnMap2.get("OUT_BAL").toString());
                                    interest += Double.parseDouble(wrnMap2.get("TDU_AMT").toString());
                                    String JUDGMENT_DT = wrnMap.get("JUDGMENT_DT").toString();
                                    JUDGMENT_DT_Date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(JUDGMENT_DT);
                                    if (JUDGMENT_DT_Date.getTime() > JUDGMENT_DT_Date_MAX.getTime()) {
                                        JUDGMENT_DT_Date_MAX = JUDGMENT_DT_Date;
                                    }
                                }
                            }
                        }

                        // --- Update Document ---
                        Long idDoc = empDebtAccInfo1.getDocument().getId();
                        Document document = documentRepository.findOneById(idDoc);
                        if (AppUtil.isNotNull(document)) {
                            document.setPrincipalBalance(balance);
                            document.setInterest(interest);
                            documentRepository.saveAndFlush(document);
                        }

                        // --- Update DocumentProgress ---
                        List<DocumentProgress> documentProgresslist = documentProgressRepositoryCustom.findByDocumentId(idDoc);
                        for (DocumentProgress documentProgress : documentProgresslist) {
                            documentProgress.setDateAdjudicateOut(JUDGMENT_DT_Date_MAX);
                            documentProgressRepository.saveAndFlush(documentProgress);
                        }
                    }
                }

            }

        } catch (Exception e) {
            LOGGER.error("Error --- updateWRN ---- {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
