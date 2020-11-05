package com.app2.engine.service.impl;

import com.app2.engine.config.Statement;
import com.app2.engine.entity.app.*;
import com.app2.engine.repository.*;
import com.app2.engine.repository.custom.DocumentRepositoryCustom;
import com.app2.engine.service.AbstractEngineService;
import com.app2.engine.service.LitigationUpdateService;
import com.app2.engine.service.SmbFileService;
import com.app2.engine.util.AppUtil;
import com.app2.engine.util.DateUtil;
import com.app2.engine.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LitigationUpdateServiceImpl extends AbstractEngineService implements LitigationUpdateService {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SmbFileService smbFileService;

    @Autowired
    ParameterRepository parameterRepository;

    @Autowired
    ParameterDetailRepository parameterDetailRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentProgressRepository documentProgressRepository;

    @Autowired
    DocumentRepositoryCustom documentRepositoryCustom;

    @Autowired
    CourtOrderDetailRepository courtOrderDetailRepository;

    @Value("${Api.LitigationUpdate}")
    private String API_LitigationUpdate;

    @PersistenceContext
    private EntityManager entityManager;


    private enum BKC_HEADER {
        SEQ, LEGAL_ID, WF_TYPE_ID, WF_TYPE_DESC, ACN, COLL_ID, COLL_TYPE, ASSIGN_LAWYER_DT, NOTICE_DT, JUDGMENT_UNDECIDED_NO, JUDGMENT_UNDECIDED_YEAR, JUDGMENT_SUE_DT, JUDGMENT_DT, JUDGMENT_DECIDED_NO, JUDGMENT_DECIDED_YEAR, JUDGMENT_RESULT_DESC, BANKRUPT_DT, GAZETTE_DT, DUE_DT, SETTLEMENT_DT, SETTLEMENT_SEQ, PRINCIPAL_AMT, INTEREST_AMT, COURT_DT, COURT_SEQ, COURT_PRINCIPAL_AMT, COURT_INTEREST_AMT, SEIZE_DT, LED_APPRAISAL, APPROVED_DT, APPRAISAL_VAL, AUCTION_DT, AUCTION_AMT, LITIGTION_STATUS
    }

    private enum BKO_HEADER {
        SEQ, LEGAL_ID, WF_TYPE_ID, WF_TYPE_DESC, ACN, COLL_ID, COLL_TYPE, ASSIGN_LAWYER_DT, JUDGMENT_UNDECIDED_NO, JUDGMENT_UNDECIDED_YEAR, JUDGMENT_SUE_DT, JUDGMENT_DT, PLAINTIFF, JUDGMENT_DECIDED_NO, JUDGMENT_DECIDED_YEAR, JUDGMENT_RESULT_DESC, BANKRUPT_DT, GAZETTE_DT, DUE_DT, SETTLEMENT_DT, SETTLEMENT_SEQ, PRINCIPAL_AMT, INTEREST_AMT, COURT_DT, COURT_SEQ, COURT_PRINCIPAL_AMT, COURT_INTEREST_AMT, SEIZE_DT, LED_APPRAISAL, APPROVED_DT, APPRAISAL_VAL, AUCTION_DT, AUCTION_AMT, LITIGTION_STATUS
    }

    private enum CVA_HEADER {
        SEQ, LEGAL_ID, WF_TYPE_ID, WF_TYPE_DESC, ACN, COLL_ID, COLL_TYPE, ASSIGN_LAWYER_DT, JUDGMENT_UNDECIDED_NO, JUDGMENT_UNDECIDED_YEAR, JUDGMENT_SUE_DT, JUDGMENT_DECIDED_NO, JUDGMENT_DECIDED_YEAR, JUDGMENT_DT, JUDGMENT_RESULT_DESC, JUDGMENT_AMOUNT, APPEAL_RESULT_DESC, DEKA_RESULT_DESC, SEIZE_DT, LED_APPRAISAL, APPROVED_DT, APPRAISAL_VAL, AUCTION_DT, AUCTION_AMT, LITIGTION_STATUS
    }

    private enum CVC_HEADER {
        SEQ, LEGAL_ID, WF_TYPE_ID, WF_TYPE_DESC, ACN, COLL_ID, COLL_TYPE, ASSIGN_LAWYER_DT, NOTICE_DT, JUDGMENT_UNDECIDED_NO, JUDGMENT_UNDECIDED_YEAR, JUDGMENT_SUE_DT, JUDGMENT_DECIDED_NO, JUDGMENT_DECIDED_YEAR, JUDGMENT_RESULT_DESC, JUDGMENT_AMOUNT, APPEAL_DECIDED_NO, APPEAL_DECIDED_YEAR, APPEAL_DT, APPEAL_RESULT_DESC, APPEAL_AMOUNT, DEKA_DECIDED_NO, DEKA_DECIDED_YEAR, DEKA_DT, DEKA_RESULT_DESC, DEKA_AMOUNT, SEIZE_DT, LED_APPRAISAL, APPROVED_DT, APPRAISAL_VAL, AUCTION_DT, AUCTION_AMT, LITIGTION_STATUS
    }

    private enum CVO_HEADER {
        SEQ, LEGAL_ID, WF_TYPE_ID, WF_TYPE_DESC, ACN, COLL_ID, COLL_TYPE, PLAINTIFF, PT_JUDGMENT_DECIDED_NO, PT_JUDGMENT_DECIDED_YEAR, PT_JUDGMENT_DT, PT_SEIZE_DT, ASSIGN_LAWYER_DT, OFFICER_SENDDOC_DT, PREFERED_DEBT_DT, PREFERED_DEBT_AMOUNT, PREFERED_COURT_DT, PREFERED_COURT_AMOUNT, SM_JUDGMENT_UNDECIDED_NO, SM_JUDGMENT_UNDECIDED_YEAR, SM_JUDGMENT_SUE_DT, SM_JUDGMENT_DECIDED_NO, SM_JUDGMENT_DECIDED_YEAR, SM_JUDGMENT_DT, SM_APPEAL_AMOUNT, APPEAL_DT, APPEAL_AMOUNT, APPEAL_COURT_DT, APPEAL_COURT_AMOUNT, AP_JUDGMENT_UNDECIDED_NO, AP_JUDGMENT_UNDECIDED_YEAR, AP_JUDGMENT_SUE_DT, AP_JUDGMENT_DECIDED_NO, AP_JUDGMENT_DECIDED_YEAR, AP_JUDGMENT_DT, AP_APPEAL_AMOUNT, AUCTION_TYPE, SEIZE_DT, LED_APPRAISAL, APPROVED_DT, APPRAISAL_VAL, AUCTION_DT, AUCTION_AMT, LITIGTION_STATUS
    }

    private char delimiterPipe = '|';

    public String convertDateToFile(String pattern,String dateTime){
        Date date = new Date(Long.valueOf(dateTime.toString()));
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
        return dateFormat.format(date);
    }

    @Override
    public void litigationUpdateBKC(String date) {
        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String pathFile = FileUtil.isNotExistsDirCreated(parameter_DL.getVariable2(), date);

            String fileName = "LitigationUpdate_BKC_" + date + ".csv";

            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(pathFile + "/" + fileName));
            bufferedWriter.write('\ufeff');
            CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.newFormat(delimiterPipe).withRecordSeparator('\n')
                    .withHeader(BKC_HEADER.class));

            List<Map<String, Object>> listMap = dataForBKC(date);
            String total = String.format("%010d",listMap.size());

            for (Map<String, Object> objectMap : listMap) {
                csvPrinter.printRecord(
                        objectMap.get(BKC_HEADER.SEQ.toString())
                        , objectMap.get(BKC_HEADER.LEGAL_ID.toString())
                        , objectMap.get(BKC_HEADER.WF_TYPE_ID.toString())
                        , objectMap.get(BKC_HEADER.WF_TYPE_DESC.toString())
                        , objectMap.get(BKC_HEADER.ACN.toString())
                        , objectMap.get(BKC_HEADER.COLL_ID.toString())
                        , objectMap.get(BKC_HEADER.COLL_TYPE.toString())
                        , objectMap.get(BKC_HEADER.ASSIGN_LAWYER_DT.toString())
                        , objectMap.get(BKC_HEADER.NOTICE_DT.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_UNDECIDED_NO.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_UNDECIDED_YEAR.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_SUE_DT.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_DT.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(BKC_HEADER.JUDGMENT_RESULT_DESC.toString())
                        , objectMap.get(BKC_HEADER.BANKRUPT_DT.toString())
                        , objectMap.get(BKC_HEADER.GAZETTE_DT.toString())
                        , objectMap.get(BKC_HEADER.DUE_DT.toString())
                        , objectMap.get(BKC_HEADER.SETTLEMENT_DT.toString())
                        , objectMap.get(BKC_HEADER.SETTLEMENT_SEQ.toString())
                        , objectMap.get(BKC_HEADER.PRINCIPAL_AMT.toString())
                        , objectMap.get(BKC_HEADER.INTEREST_AMT.toString())
                        , objectMap.get(BKC_HEADER.COURT_DT.toString())
                        , objectMap.get(BKC_HEADER.COURT_SEQ.toString())
                        , objectMap.get(BKC_HEADER.COURT_PRINCIPAL_AMT.toString())
                        , objectMap.get(BKC_HEADER.COURT_INTEREST_AMT.toString())
                        , objectMap.get(BKC_HEADER.SEIZE_DT.toString())
                        , objectMap.get(BKC_HEADER.LED_APPRAISAL.toString())
                        , objectMap.get(BKC_HEADER.APPROVED_DT.toString())
                        , objectMap.get(BKC_HEADER.APPRAISAL_VAL.toString())
                        , objectMap.get(BKC_HEADER.AUCTION_DT.toString())
                        , objectMap.get(BKC_HEADER.AUCTION_AMT.toString())
                        , objectMap.get(BKC_HEADER.LITIGTION_STATUS.toString())
                );
            }

            csvPrinter.printRecord("TOTAL : "+total);
            csvPrinter.flush();
            csvPrinter.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<Map<String, Object>> dataForBKC(String date) {
        String newDate = DateUtil.convertStringToDate(date);
//        date = "2020-07-17"; ////test

        List<Map<String, Object>> listMap = new ArrayList<>();
        // --- Get data For file here

        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();
        querySql.append(Statement.GET_DATA_BKC);
        querySql.append("where d.doc_type = 2\n" +
                "and d.updated_date like '%" + newDate + "%'\n" +
                "and dh.user_role_to = 'LAW'\n" +
                "order by d.updated_date DESC,dp.updated_date DESC,ap.updated_date DESC,ag.updated_date DESC," +
                "ass.updated_date DESC,con.updated_date,dh.[sequence] DESC");

        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List<Map> maps = new ArrayList<>(query.list());
        List<Map> listData = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        if (!maps.isEmpty()) {
            for (Map bkc : maps) {
                List<Map> mapDup = listData
                        .stream()
                        .filter(value -> value.get("id").toString().equals(bkc.get("id").toString()))
                        .collect(Collectors.toList());
                if (mapDup.isEmpty()) {
                    if (bkc.get("doc_number") != null && bkc.get("action_time") != null
                            && bkc.get("notic_doc_send_date") != null && bkc.get("black_case_number") != null
                            && bkc.get("law_suit_send_date") != null && bkc.get("red_case_number") != null
                            && bkc.get("adjudication") != null && bkc.get("adj_date") != null
                            && bkc.get("gazette_date") != null && bkc.get("account_payment_date") != null
                            && bkc.get("debt_amount") != null && bkc.get("amount") != null
                            && bkc.get("confiscate_date") != null && bkc.get("cost_est_legal_ex_office") != null
                            && bkc.get("cost_est_legal_bank_date") != null && bkc.get("amount_buy") != null
                            && bkc.get("doc_status") != null) {
                        listData.add(bkc);
                    }
                }
            }

            for (int i = 0; i < listData.size(); i++) {
                Map<String, Object> BKCMap = new HashMap();
                Map dataMap = mapper.convertValue(listData.get(i), Map.class);

                String[] blackCaseNumber = dataMap.get("black_case_number").toString().split("/");
                String blackCaseNumberOne = blackCaseNumber[0];
                String blackCaseNumberTwo = blackCaseNumber[1];

                String[] redCaseNumber = dataMap.get("red_case_number").toString().split("/");
                String redCaseNumberOne = redCaseNumber[0];
                String redCaseNumberTwo = redCaseNumber[1];

                Parameter parameter = parameterRepository.findByCode("ADJUDICATION");
                List<ParameterDetail> parameterDetails = parameterDetailRepository.findByParameter(parameter);
                String adjudication = "";
                for (ParameterDetail detail : parameterDetails) {
                    if (dataMap.get("adjudication").toString().equals(detail.getCode())) {
                        adjudication = detail.getName();
                    }
                }

                BKCMap.put(BKC_HEADER.SEQ.toString(), i + 1);
                BKCMap.put(BKC_HEADER.LEGAL_ID.toString(), dataMap.get("doc_number").toString());
                BKCMap.put(BKC_HEADER.WF_TYPE_ID.toString(), "");
                BKCMap.put(BKC_HEADER.WF_TYPE_DESC.toString(), "");
                BKCMap.put(BKC_HEADER.ACN.toString(), "");
                BKCMap.put(BKC_HEADER.COLL_ID.toString(), "");
                BKCMap.put(BKC_HEADER.COLL_TYPE.toString(), "");
                BKCMap.put(BKC_HEADER.ASSIGN_LAWYER_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("action_time").toString()));
                BKCMap.put(BKC_HEADER.NOTICE_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("notic_doc_send_date").toString()));
                BKCMap.put(BKC_HEADER.JUDGMENT_UNDECIDED_NO.toString(), blackCaseNumberOne);
                BKCMap.put(BKC_HEADER.JUDGMENT_UNDECIDED_YEAR.toString(), blackCaseNumberTwo);
                BKCMap.put(BKC_HEADER.JUDGMENT_SUE_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("law_suit_send_date").toString()));
                BKCMap.put(BKC_HEADER.JUDGMENT_DT.toString(), "");
                BKCMap.put(BKC_HEADER.JUDGMENT_DECIDED_NO.toString(), redCaseNumberOne);
                BKCMap.put(BKC_HEADER.JUDGMENT_DECIDED_YEAR.toString(), redCaseNumberTwo);
                BKCMap.put(BKC_HEADER.JUDGMENT_RESULT_DESC.toString(), adjudication);
                BKCMap.put(BKC_HEADER.BANKRUPT_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("adj_date").toString()));
                BKCMap.put(BKC_HEADER.GAZETTE_DT.toString(), dataMap.get("gazette_date").toString());
                BKCMap.put(BKC_HEADER.DUE_DT.toString(), "");
                BKCMap.put(BKC_HEADER.SETTLEMENT_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("account_payment_date").toString()));
                BKCMap.put(BKC_HEADER.SETTLEMENT_SEQ.toString(), "");
                BKCMap.put(BKC_HEADER.PRINCIPAL_AMT.toString(), String.format("%.2f", dataMap.get("debt_amount")));
                BKCMap.put(BKC_HEADER.INTEREST_AMT.toString(), String.format("%.2f", dataMap.get("amount")));
                BKCMap.put(BKC_HEADER.COURT_DT.toString(), "");
                BKCMap.put(BKC_HEADER.COURT_SEQ.toString(), "");
                BKCMap.put(BKC_HEADER.COURT_PRINCIPAL_AMT.toString(), "");
                BKCMap.put(BKC_HEADER.COURT_INTEREST_AMT.toString(), "");
                BKCMap.put(BKC_HEADER.SEIZE_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("confiscate_date").toString()));
                BKCMap.put(BKC_HEADER.LED_APPRAISAL.toString(), dataMap.get("cost_est_legal_ex_office").toString());
                BKCMap.put(BKC_HEADER.APPROVED_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("cost_est_legal_bank_date").toString()));
                BKCMap.put(BKC_HEADER.APPRAISAL_VAL.toString(), "");
                BKCMap.put(BKC_HEADER.AUCTION_DT.toString(), "");
                BKCMap.put(BKC_HEADER.AUCTION_AMT.toString(), String.format("%.2f", dataMap.get("amount_buy")));
                BKCMap.put(BKC_HEADER.LITIGTION_STATUS.toString(), dataMap.get("doc_status").toString());

                listMap.add(BKCMap);
            }
        }
        return listMap;
    }

    @Override
    public void litigationUpdateBKO(String date) {
        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String pathFile = FileUtil.isNotExistsDirCreated(parameter_DL.getVariable2(), date);

            String fileName = "LitigationUpdate_BKO_" + date + ".csv";

            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(pathFile + "/" + fileName));
            bufferedWriter.write('\ufeff');
            CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.newFormat(delimiterPipe).withRecordSeparator('\n')
                    .withHeader(BKO_HEADER.class));

            List<Map<String, Object>> listMap = dataForBKO(date);
            String total = String.format("%010d",listMap.size());

            for (Map<String, Object> objectMap : listMap) {
                csvPrinter.printRecord(
                        objectMap.get(BKO_HEADER.SEQ.toString())
                        , objectMap.get(BKO_HEADER.LEGAL_ID.toString())
                        , objectMap.get(BKO_HEADER.WF_TYPE_ID.toString())
                        , objectMap.get(BKO_HEADER.WF_TYPE_DESC.toString())
                        , objectMap.get(BKO_HEADER.ACN.toString())
                        , objectMap.get(BKO_HEADER.COLL_ID.toString())
                        , objectMap.get(BKO_HEADER.COLL_TYPE.toString())
                        , objectMap.get(BKO_HEADER.ASSIGN_LAWYER_DT.toString())
                        , objectMap.get(BKO_HEADER.JUDGMENT_UNDECIDED_NO.toString())
                        , objectMap.get(BKO_HEADER.JUDGMENT_UNDECIDED_YEAR.toString())
                        , objectMap.get(BKO_HEADER.JUDGMENT_SUE_DT.toString())
                        , objectMap.get(BKO_HEADER.JUDGMENT_DT.toString())
                        , objectMap.get(BKO_HEADER.PLAINTIFF.toString())/////don't have in excel
                        , objectMap.get(BKO_HEADER.JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(BKO_HEADER.JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(BKO_HEADER.JUDGMENT_RESULT_DESC.toString())
                        , objectMap.get(BKO_HEADER.BANKRUPT_DT.toString())
                        , objectMap.get(BKO_HEADER.GAZETTE_DT.toString())
                        , objectMap.get(BKO_HEADER.DUE_DT.toString())
                        , objectMap.get(BKO_HEADER.SETTLEMENT_DT.toString())
                        , objectMap.get(BKO_HEADER.SETTLEMENT_SEQ.toString())
                        , objectMap.get(BKO_HEADER.PRINCIPAL_AMT.toString())
                        , objectMap.get(BKO_HEADER.INTEREST_AMT.toString())
                        , objectMap.get(BKO_HEADER.COURT_DT.toString())
                        , objectMap.get(BKO_HEADER.COURT_SEQ.toString())
                        , objectMap.get(BKO_HEADER.COURT_PRINCIPAL_AMT.toString())
                        , objectMap.get(BKO_HEADER.COURT_INTEREST_AMT.toString())
                        , objectMap.get(BKO_HEADER.SEIZE_DT.toString())
                        , objectMap.get(BKO_HEADER.LED_APPRAISAL.toString())
                        , objectMap.get(BKO_HEADER.APPROVED_DT.toString())
                        , objectMap.get(BKO_HEADER.APPRAISAL_VAL.toString())
                        , objectMap.get(BKO_HEADER.AUCTION_DT.toString())
                        , objectMap.get(BKO_HEADER.AUCTION_AMT.toString())
                        , objectMap.get(BKO_HEADER.LITIGTION_STATUS.toString())
                );
            }

            csvPrinter.printRecord("TOTAL : "+total);
            csvPrinter.flush();
            csvPrinter.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Transactional
    public List<Map<String, Object>> dataForBKO(String date) {
        String newDate = DateUtil.convertStringToDate(date);
//        String curDate = "2020-07-17"; ////test

        List<Map<String, Object>> listMap = new ArrayList<>();
        // --- Get data For file here

        Session session = (Session) entityManager.getDelegate();
        StringBuilder querySql = new StringBuilder();
        querySql.append(Statement.GET_DATA_BKO);
        querySql.append("where d.updated_date like '%" + newDate + "%'\n" +
                "order by d.updated_date DESC ,document_progress.updated_date DESC ,account_payment.updated_date DESC ,document_history.action_time DESC");

        SQLQuery query = session.createSQLQuery(querySql.toString());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List<Map> maps = new ArrayList<>(query.list());
        List<Map> listData = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        if (!maps.isEmpty()) {
            for (Map bko : maps) {
                List<Map> mapDup = listData
                        .stream()
                        .filter(value -> value.get("id").toString().equals(bko.get("id").toString()))
                        .collect(Collectors.toList());
                if (mapDup.isEmpty()) {
                    if (bko.get("doc_number") != null && bko.get("action_time") != null
                            && bko.get("black_case_number") != null && bko.get("red_case_number") != null
                            && bko.get("date_adjudicate_out") != null && bko.get("gazette_date") != null
                            && bko.get("account_payment_date") != null && bko.get("debt_amount") != null
                            && bko.get("amount") != null) {
                        listData.add(bko);
                    }
                }
            }

            for (int i = 0; i < listData.size(); i++) {
                Map<String, Object> BKOMap = new HashMap();
                Map dataMap = mapper.convertValue(listData.get(i), Map.class);

                String[] blackCaseNumber = dataMap.get("black_case_number").toString().split("/");
                String blackCaseNumberOne = blackCaseNumber[0];
                String blackCaseNumberTwo = blackCaseNumber[1];

                String[] redCaseNumber = dataMap.get("red_case_number").toString().split("/");
                String redCaseNumberOne = redCaseNumber[0];
                String redCaseNumberTwo = redCaseNumber[1];

                BKOMap.put(BKO_HEADER.SEQ.toString(), i + 1);
                BKOMap.put(BKO_HEADER.LEGAL_ID.toString(), dataMap.get("doc_number").toString());
                BKOMap.put(BKO_HEADER.WF_TYPE_ID.toString(), "0");
                BKOMap.put(BKO_HEADER.WF_TYPE_DESC.toString(), "0");
                BKOMap.put(BKO_HEADER.ACN.toString(), "0");
                BKOMap.put(BKO_HEADER.COLL_ID.toString(), "0");
                BKOMap.put(BKO_HEADER.COLL_TYPE.toString(), "0");
                BKOMap.put(BKO_HEADER.ASSIGN_LAWYER_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("action_time").toString()));
                BKOMap.put(BKO_HEADER.PLAINTIFF.toString(), "0");
                BKOMap.put(BKO_HEADER.JUDGMENT_UNDECIDED_NO.toString(), blackCaseNumberOne);
                BKOMap.put(BKO_HEADER.JUDGMENT_UNDECIDED_YEAR.toString(), blackCaseNumberTwo);
                BKOMap.put(BKO_HEADER.JUDGMENT_SUE_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.JUDGMENT_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.JUDGMENT_DECIDED_NO.toString(), redCaseNumberOne);
                BKOMap.put(BKO_HEADER.JUDGMENT_DECIDED_YEAR.toString(), redCaseNumberTwo);
                BKOMap.put(BKO_HEADER.JUDGMENT_RESULT_DESC.toString(), "0");
                BKOMap.put(BKO_HEADER.BANKRUPT_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("date_adjudicate_out").toString()));
                BKOMap.put(BKO_HEADER.GAZETTE_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("gazette_date").toString()));
                BKOMap.put(BKO_HEADER.DUE_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.SETTLEMENT_DT.toString(), convertDateToFile("dd/MM/yyyy", dataMap.get("account_payment_date").toString()));
                BKOMap.put(BKO_HEADER.SETTLEMENT_SEQ.toString(), "0");
                BKOMap.put(BKO_HEADER.PRINCIPAL_AMT.toString(), String.format("%.2f", dataMap.get("debt_amount")));
                BKOMap.put(BKO_HEADER.INTEREST_AMT.toString(), String.format("%.2f", dataMap.get("amount")));
                BKOMap.put(BKO_HEADER.COURT_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.COURT_SEQ.toString(), "0");
                BKOMap.put(BKO_HEADER.COURT_PRINCIPAL_AMT.toString(), "0");
                BKOMap.put(BKO_HEADER.COURT_INTEREST_AMT.toString(), "0");
                BKOMap.put(BKO_HEADER.SEIZE_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.LED_APPRAISAL.toString(), "0");
                BKOMap.put(BKO_HEADER.APPROVED_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.APPRAISAL_VAL.toString(), "0");
                BKOMap.put(BKO_HEADER.AUCTION_DT.toString(), "0");
                BKOMap.put(BKO_HEADER.AUCTION_AMT.toString(), "0");
                BKOMap.put(BKO_HEADER.LITIGTION_STATUS.toString(), "0");

                listMap.add(BKOMap);
            }
        }
        return listMap;
    }

    @Override
    public void litigationUpdateCVA(String date) {
        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String pathFile = FileUtil.isNotExistsDirCreated(parameter_DL.getVariable2(), date);

            String fileName = "LitigationUpdate_CVA_" + date + ".csv";

            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(pathFile + "/" + fileName));
            CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.newFormat(delimiterPipe).withRecordSeparator('\n')
                    .withHeader(CVA_HEADER.class));

            List<Map<String, Object>> listMap = dataForCVA();
            String total = String.format("%010d",listMap.size());

            for (Map<String, Object> objectMap : listMap) {
                csvPrinter.printRecord(
                        objectMap.get(CVA_HEADER.SEQ.toString())
                        , objectMap.get(CVA_HEADER.LEGAL_ID.toString())
                        , objectMap.get(CVA_HEADER.WF_TYPE_ID.toString())
                        , objectMap.get(CVA_HEADER.WF_TYPE_DESC.toString())
                        , objectMap.get(CVA_HEADER.ACN.toString())
                        , objectMap.get(CVA_HEADER.COLL_ID.toString())
                        , objectMap.get(CVA_HEADER.COLL_TYPE.toString())
                        , objectMap.get(CVA_HEADER.ASSIGN_LAWYER_DT.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_UNDECIDED_NO.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_UNDECIDED_YEAR.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_SUE_DT.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_DT.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_RESULT_DESC.toString())
                        , objectMap.get(CVA_HEADER.JUDGMENT_AMOUNT.toString())
                        , objectMap.get(CVA_HEADER.APPEAL_RESULT_DESC.toString())
                        , objectMap.get(CVA_HEADER.DEKA_RESULT_DESC.toString())
                        , objectMap.get(CVA_HEADER.SEIZE_DT.toString())
                        , objectMap.get(CVA_HEADER.LED_APPRAISAL.toString())
                        , objectMap.get(CVA_HEADER.APPROVED_DT.toString())
                        , objectMap.get(CVA_HEADER.APPRAISAL_VAL.toString())
                        , objectMap.get(CVA_HEADER.AUCTION_DT.toString())
                        , objectMap.get(CVA_HEADER.AUCTION_AMT.toString())
                        , objectMap.get(CVA_HEADER.LITIGTION_STATUS.toString())
                );
            }
            csvPrinter.printRecord("TOTAL : "+total);
            csvPrinter.flush();
            csvPrinter.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<Map<String, Object>> dataForCVA() {
        List<Map<String, Object>> listMap = new ArrayList<>();
        // --- Get data For file here
        return listMap;
    }

    @Override
    public void litigationUpdateCVC(String date) {
        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String pathFile = FileUtil.isNotExistsDirCreated(parameter_DL.getVariable2(), date);

            String fileName = "LitigationUpdate_CVC_" + date + ".csv";

            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(pathFile + "/" + fileName));
            CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.newFormat(delimiterPipe).withRecordSeparator('\n')
                    .withHeader(CVC_HEADER.class));

            List<Map<String, Object>> listMap = dataForCVC(date);
            String total = String.format("%010d",listMap.size());

            for (Map<String, Object> objectMap : listMap) {
                csvPrinter.printRecord(
                        objectMap.get(CVC_HEADER.SEQ.toString())
                        , objectMap.get(CVC_HEADER.LEGAL_ID.toString())
                        , objectMap.get(CVC_HEADER.WF_TYPE_ID.toString())
                        , objectMap.get(CVC_HEADER.WF_TYPE_DESC.toString())
                        , objectMap.get(CVC_HEADER.ACN.toString())
                        , objectMap.get(CVC_HEADER.COLL_ID.toString())
                        , objectMap.get(CVC_HEADER.COLL_TYPE.toString())
                        , objectMap.get(CVC_HEADER.ASSIGN_LAWYER_DT.toString())
                        , objectMap.get(CVC_HEADER.NOTICE_DT.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_UNDECIDED_NO.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_UNDECIDED_YEAR.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_SUE_DT.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_RESULT_DESC.toString())
                        , objectMap.get(CVC_HEADER.JUDGMENT_AMOUNT.toString())
                        , objectMap.get(CVC_HEADER.APPEAL_DECIDED_NO.toString())
                        , objectMap.get(CVC_HEADER.APPEAL_DECIDED_YEAR.toString())
                        , objectMap.get(CVC_HEADER.APPEAL_DT.toString())
                        , objectMap.get(CVC_HEADER.APPEAL_RESULT_DESC.toString())
                        , objectMap.get(CVC_HEADER.APPEAL_AMOUNT.toString())
                        , objectMap.get(CVC_HEADER.DEKA_DECIDED_NO.toString())
                        , objectMap.get(CVC_HEADER.DEKA_DECIDED_YEAR.toString())
                        , objectMap.get(CVC_HEADER.DEKA_DT.toString())
                        , objectMap.get(CVC_HEADER.DEKA_RESULT_DESC.toString())
                        , objectMap.get(CVC_HEADER.DEKA_AMOUNT.toString())
                        , objectMap.get(CVC_HEADER.SEIZE_DT.toString())
                        , objectMap.get(CVC_HEADER.LED_APPRAISAL.toString())
                        , objectMap.get(CVC_HEADER.APPROVED_DT.toString())
                        , objectMap.get(CVC_HEADER.APPRAISAL_VAL.toString())
                        , objectMap.get(CVC_HEADER.AUCTION_DT.toString())
                        , objectMap.get(CVC_HEADER.AUCTION_AMT.toString())
                        , objectMap.get(CVC_HEADER.LITIGTION_STATUS.toString())
                );
            }
            csvPrinter.printRecord("TOTAL : "+total);
            csvPrinter.flush();
            csvPrinter.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);

        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<Map<String, Object>> dataForCVC(String date) {
        List<Map<String, Object>> listMap = new ArrayList<>();
        String fileDate = DateUtil.convertStringToDate(date);
        ObjectMapper oMapper = new ObjectMapper();

        List resultCVCtList = findLitigationUpdate("CVC",fileDate);

        for (int i=0 ; i<resultCVCtList.size() ; i++) {
            Map<String, Object> CVCMap = new HashMap();
            Map dataMap = oMapper.convertValue(resultCVCtList.get(i), Map.class);
            CVCMap.put(CVC_HEADER.SEQ.toString(),i+1);
            CVCMap.put(CVC_HEADER.LEGAL_ID.toString(),dataMap.get("LEGAL_ID"));
            CVCMap.put(CVC_HEADER.WF_TYPE_ID.toString(),"");
            CVCMap.put(CVC_HEADER.WF_TYPE_DESC.toString(),"");
            CVCMap.put(CVC_HEADER.ACN.toString(),"");
            CVCMap.put(CVC_HEADER.COLL_ID.toString(),"");
            CVCMap.put(CVC_HEADER.COLL_TYPE.toString(),"");
            CVCMap.put(CVC_HEADER.ASSIGN_LAWYER_DT.toString(),dataMap.get("ASSIGN_LAWYER_DT"));
            CVCMap.put(CVC_HEADER.NOTICE_DT.toString(),"");
            CVCMap.put(CVC_HEADER.JUDGMENT_UNDECIDED_NO.toString(),dataMap.get("JUDGMENT_UNDECIDED_NO"));
            CVCMap.put(CVC_HEADER.JUDGMENT_UNDECIDED_YEAR.toString(),dataMap.get("JUDGMENT_UNDECIDED_YEAR"));
            CVCMap.put(CVC_HEADER.JUDGMENT_SUE_DT.toString(),dataMap.get("JUDGMENT_SUE_DT"));
            CVCMap.put(CVC_HEADER.JUDGMENT_DECIDED_NO.toString(),dataMap.get("JUDGMENT_DECIDED_NO"));
            CVCMap.put(CVC_HEADER.JUDGMENT_DECIDED_YEAR.toString(),dataMap.get("JUDGMENT_DECIDED_YEAR"));
            CVCMap.put(CVC_HEADER.JUDGMENT_RESULT_DESC.toString(),dataMap.get("JUDGMENT_RESULT_DESC"));
            CVCMap.put(CVC_HEADER.JUDGMENT_AMOUNT.toString(),dataMap.get("JUDGMENT_AMOUNT"));
            CVCMap.put(CVC_HEADER.APPEAL_DECIDED_NO.toString(),dataMap.get("APPEAL_DECIDED_NO"));
            CVCMap.put(CVC_HEADER.APPEAL_DECIDED_YEAR.toString(),dataMap.get("APPEAL_DECIDED_YEAR"));
            CVCMap.put(CVC_HEADER.APPEAL_DT.toString(),dataMap.get("APPEAL_DT"));
            CVCMap.put(CVC_HEADER.APPEAL_RESULT_DESC.toString(),dataMap.get("APPEAL_RESULT_DESC"));
            CVCMap.put(CVC_HEADER.APPEAL_AMOUNT.toString(),dataMap.get("APPEAL_AMOUNT"));
            CVCMap.put(CVC_HEADER.DEKA_DECIDED_NO.toString(),dataMap.get("DEKA_DECIDED_NO"));
            CVCMap.put(CVC_HEADER.DEKA_DECIDED_YEAR.toString(),dataMap.get("DEKA_DECIDED_YEAR"));
            CVCMap.put(CVC_HEADER.DEKA_DT.toString(),dataMap.get("DEKA_DT"));
            CVCMap.put(CVC_HEADER.DEKA_RESULT_DESC.toString(),dataMap.get("DEKA_RESULT_DESC"));
            CVCMap.put(CVC_HEADER.DEKA_AMOUNT.toString(),dataMap.get("DEKA_AMOUNT"));
            CVCMap.put(CVC_HEADER.SEIZE_DT.toString(),"");
            CVCMap.put(CVC_HEADER.LED_APPRAISAL.toString(),"");
            CVCMap.put(CVC_HEADER.APPROVED_DT.toString(),"");
            CVCMap.put(CVC_HEADER.APPRAISAL_VAL.toString(),"");
            CVCMap.put(CVC_HEADER.AUCTION_DT.toString(),"");
            CVCMap.put(CVC_HEADER.AUCTION_AMT.toString(),"");
            CVCMap.put(CVC_HEADER.LITIGTION_STATUS.toString(),"");
            listMap.add(CVCMap);
        }
        return listMap;
    }

    @Transactional
    public List findLitigationUpdate(String type ,String curDate) {
        List completeResult = new ArrayList();
        ObjectMapper oMapper = new ObjectMapper();

        List resultDocCurDateList = documentRepositoryCustom.findCurDate(curDate);

        if (type.equals("CVO")){
            for (int i=0 ; i<resultDocCurDateList.size() ; i++){
                Map<String, Object> resultCVOMap = new HashMap();
                Object actionTimeLAW = null;
                Object actionTimeMK2 = null;
                String prefered_preferentialRequestDate = null;
                String prefered_amount = null;
                String prefered_permissDate = null;
                String appeal_preventRequestDate = null;
                String appeal_amount = null;
                String appeal_permissDate = null;

                Map dataMap = oMapper.convertValue(resultDocCurDateList.get(i), Map.class);
                Long idDocument = Long.valueOf(dataMap.get("docID").toString());
                Document document = documentRepository.getOne(idDocument);

                List curDateRoleLAWList = documentRepositoryCustom.findCurDtByRoleByDocument(curDate,"LAW",idDocument.toString());
                List curDateRoleMK2List = documentRepositoryCustom.findCurDtByRoleByDocument(curDate,"MK2",idDocument.toString());

                if (!curDateRoleLAWList.isEmpty()){
                    Map resultRoleMap = oMapper.convertValue(curDateRoleLAWList.get(0), Map.class);
                    actionTimeLAW = convertDateToFile("dd/MM/yyyy",resultRoleMap.get("actionTime").toString());
                }
                if (!curDateRoleMK2List.isEmpty()){
                    Map resultRoleMap = oMapper.convertValue(curDateRoleMK2List.get(0), Map.class);
                    actionTimeMK2 = convertDateToFile("dd/MM/yyyy",resultRoleMap.get("actionTime").toString());
                }

                List<DocumentProgress> docProgressCurDateList = documentProgressRepository.findByDocumentOrderByUpdatedDateDesc(document);

                if (!docProgressCurDateList.isEmpty()){
                    DocumentProgress documentProgress = docProgressCurDateList.get(0);
                    List<CourtOrderDetail> courtOrderDetailCurDatelList = courtOrderDetailRepository.findByDocumentProgressOrderByUpdatedDateDesc(documentProgress);

                    if (AppUtil.isNotNull(document.getTypeProcess()) && AppUtil.isNotNull(document.getDocType())){
                        //การยื่นขอชำระหนี้
                        if (document.getTypeProcess().equals("1") && document.getDocType().equals("3")){
                            if (!docProgressCurDateList.isEmpty()){
                                if (AppUtil.isNotNull(docProgressCurDateList.get(0).getPreferentialRequestDate())){
                                    prefered_preferentialRequestDate = docProgressCurDateList.get(0).getPreferentialRequestDate().toString();
                                }
                            }
                            if (!courtOrderDetailCurDatelList.isEmpty()){
                                prefered_amount = courtOrderDetailCurDatelList.get(0).getTotalAmount().toString();
                                if (AppUtil.isNotNull(courtOrderDetailCurDatelList.get(0).getPermissDate())){
                                    prefered_permissDate = courtOrderDetailCurDatelList.get(0).getPermissDate().toString();
                                }
                            }
                        }
                        //การร้องกันส่วน
                        if (document.getTypeProcess().equals("2") && document.getDocType().equals("3")){
                            if (!docProgressCurDateList.isEmpty()){
                                if (AppUtil.isNotNull(docProgressCurDateList.get(0).getPreventRequestDate())){
                                    appeal_preventRequestDate = docProgressCurDateList.get(0).getPreventRequestDate().toString();
                                }
                            }

                            if (!courtOrderDetailCurDatelList.isEmpty()){
                                appeal_amount = courtOrderDetailCurDatelList.get(0).getTotalAmount().toString();
                                if (AppUtil.isNotNull(courtOrderDetailCurDatelList.get(0).getPermissDate())){
                                    appeal_permissDate = courtOrderDetailCurDatelList.get(0).getPermissDate().toString();
                                }
                            }
                        }
                    }
                }

                resultCVOMap.put("LEGAL_ID",document.getDocNumber());
                resultCVOMap.put("ASSIGN_LAWYER_DT",actionTimeLAW);
                resultCVOMap.put("OFFICER_SENDDOC_DT",actionTimeMK2);
                resultCVOMap.put("PREFERED_DEBT_DT",convertDateToFile("dd/MM/yyyy",prefered_preferentialRequestDate));
                resultCVOMap.put("PREFERED_DEBT_AMOUNT",prefered_amount);
                resultCVOMap.put("PREFERED_COURT_DT",convertDateToFile("dd/MM/yyyy",prefered_permissDate));
                resultCVOMap.put("APPEAL_DT",convertDateToFile("dd/MM/yyyy",appeal_preventRequestDate));
                resultCVOMap.put("APPEAL_AMOUNT",appeal_amount);
                resultCVOMap.put("APPEAL_COURT_DT",convertDateToFile("dd/MM/yyyy",appeal_permissDate));
                completeResult.add(resultCVOMap);
            }
        }else if (type.equals("CVC")){
            for (int i=0 ; i<resultDocCurDateList.size() ; i++){
                Map<String, Object> resultCVCMap = new HashMap();
                String actionTimeLAW = null;
                String blackCaseNumNo = null;
                String blackCaseNumYear = null;
                String lawSuitSendDate = null;
                String redCaseNumNo1 = null;
                String redCaseNumYear1 = null;
                String adjDate1 = null;
                String adjudication1 = null;
                Double judgmentAmount1 = null;
                String redCaseNumNo2 = null;
                String redCaseNumYear2 = null;
                String adjDate2 = null;
                String adjudication2 = null;
                Double judgmentAmount2 = null;
                String redCaseNumNo3 = null;
                String redCaseNumYear3 = null;
                String adjDate3 = null;
                String adjudication3 = null;
                Double judgmentAmount3 = null;

                Map dataMap = oMapper.convertValue(resultDocCurDateList.get(i), Map.class);
                Long idDocument = Long.valueOf(dataMap.get("docID").toString());
                Document document = documentRepository.getOne(idDocument);
                String docType = document.getDocType();

                if (docType == "1"){

                    List curDateRoleLAWList = documentRepositoryCustom.findCurDtByRoleByDocument(curDate,"LAW",idDocument.toString());

                    if (!curDateRoleLAWList.isEmpty()){
                        Map resultRoleMap = oMapper.convertValue(curDateRoleLAWList.get(0), Map.class);
                        if (AppUtil.isNotNull(resultRoleMap.get("actionTime"))){
                            actionTimeLAW = convertDateToFile("dd/MM/yyyy",resultRoleMap.get("actionTime").toString());
                        }
                    }

                    List<DocumentProgress> docProgressCurDateList = documentProgressRepository.findByDocumentOrderByUpdatedDateDesc(document);

                    if (!docProgressCurDateList.isEmpty()){
                        DocumentProgress documentProgress = docProgressCurDateList.get(0);
                        String blackCaseNumber = documentProgress.getBlackCaseNumber();
                        String redCaseNumber = documentProgress.getRedCaseNumber();

                        if (AppUtil.isNotNull(blackCaseNumber)){
                            String[] blackCaseNumAr = blackCaseNumber.split("/");
                            blackCaseNumNo = blackCaseNumAr[0];
                            blackCaseNumYear = blackCaseNumAr[1];
                        }

                        if (AppUtil.isNotNull(documentProgress.getLawSuitSendDate())){
                            lawSuitSendDate = convertDateToFile("dd/MM/yyyy",documentProgress.getLawSuitSendDate().toString());
                        }

                        if (AppUtil.isNotNull(documentProgress.getCourt())){
                            //ศาลชั้นต้น
                            if (documentProgress.getCourt().equals("1")){
                                if (AppUtil.isNotNull(redCaseNumber)){
                                    String[] redCaseNumAr = redCaseNumber.split("/");
                                    redCaseNumNo1 = redCaseNumAr[0];
                                    redCaseNumYear1 = redCaseNumAr[1];
                                }
                                if (AppUtil.isNotNull(documentProgress.getAdjDate())){
                                    adjDate1 = convertDateToFile("dd/MM/yyyy",documentProgress.getAdjDate().toString());
                                }
                                adjudication1 = documentProgress.getAdjudication();
                                judgmentAmount1 = document.getPrincipalBalance()+document.getInterest();
                            }

                            //ศาลอุทธรณ์
                            if (documentProgress.getCourt().equals("2")){
                                if (AppUtil.isNotNull(redCaseNumber)){
                                    String[] redCaseNumAr = redCaseNumber.split("/");
                                    redCaseNumNo2 = redCaseNumAr[0];
                                    redCaseNumYear2 = redCaseNumAr[1];
                                }
                                if (AppUtil.isNotNull(documentProgress.getAdjDate())){
                                    adjDate2 = convertDateToFile("dd/MM/yyyy",documentProgress.getAdjDate().toString());
                                }
                                adjudication2 = documentProgress.getAdjudication();
                                judgmentAmount2 = document.getPrincipalBalance()+document.getInterest();
                            }

                            //ศาลฎีกา
                            if (documentProgress.getCourt().equals("3")){
                                if (AppUtil.isNotNull(redCaseNumber)){
                                    String[] redCaseNumAr = redCaseNumber.split("/");
                                    redCaseNumNo3 = redCaseNumAr[0];
                                    redCaseNumYear3 = redCaseNumAr[1];
                                }
                                if (AppUtil.isNotNull(documentProgress.getAdjDate())){
                                    adjDate3 = convertDateToFile("dd/MM/yyyy",documentProgress.getAdjDate().toString());
                                }
                                adjudication3 = documentProgress.getAdjudication();
                                judgmentAmount3 = document.getPrincipalBalance()+document.getInterest();
                            }
                        }

                    }

                    resultCVCMap.put("LEGAL_ID",document.getDocNumber());
                    resultCVCMap.put("ASSIGN_LAWYER_DT",actionTimeLAW);
                    resultCVCMap.put("JUDGMENT_UNDECIDED_NO",blackCaseNumNo);
                    resultCVCMap.put("JUDGMENT_UNDECIDED_YEAR",blackCaseNumYear);
                    resultCVCMap.put("JUDGMENT_SUE_DT",lawSuitSendDate);

                    resultCVCMap.put("JUDGMENT_DECIDED_NO",redCaseNumNo1);
                    resultCVCMap.put("JUDGMENT_DECIDED_YEAR",redCaseNumYear1);
                    resultCVCMap.put("JUDGMENT_DT",adjDate1);
                    resultCVCMap.put("JUDGMENT_RESULT_DESC",adjudication1);
                    resultCVCMap.put("JUDGMENT_AMOUNT",judgmentAmount1);

                    resultCVCMap.put("APPEAL_DECIDED_NO",redCaseNumNo2);
                    resultCVCMap.put("APPEAL_DECIDED_YEAR",redCaseNumYear2);
                    resultCVCMap.put("APPEAL_DT",adjDate2);
                    resultCVCMap.put("APPEAL_RESULT_DESC",adjudication2);
                    resultCVCMap.put("APPEAL_AMOUNT",judgmentAmount2);

                    resultCVCMap.put("DEKA_DECIDED_NO",redCaseNumNo3);
                    resultCVCMap.put("DEKA_DECIDED_YEAR",redCaseNumYear3);
                    resultCVCMap.put("DEKA_DT",adjDate3);
                    resultCVCMap.put("DEKA_RESULT_DESC",adjudication3);
                    resultCVCMap.put("DEKA_AMOUNT",judgmentAmount3);
                    completeResult.add(resultCVCMap);
                }
            }
        }
        return completeResult;
    }

    @Override
    public void litigationUpdateCVO(String date) {
        try {
            // BATCH_PATH_LOCAL : path LEAD , 01 : code of DCMS
            ParameterDetail parameter_DL = parameterDetailRepository.findByParameterAndCode("BATCH_PATH_LOCAL","01");

            //เช็ค folder วันที่ ถ้ายังไม่มีให้สร้างขึ้นมาใหม่
            String pathFile = FileUtil.isNotExistsDirCreated(parameter_DL.getVariable2(), date);
            String fileName = "/LitigationUpdate_CVO_" + date + ".csv";

            BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(pathFile + "/" + fileName));
            CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.newFormat(delimiterPipe).withRecordSeparator('\n')
                    .withHeader(CVO_HEADER.class));

            List<Map<String, Object>> listMap = dataForCVO(date);
            String total = String.format("%010d",listMap.size());

            for (Map<String, Object> objectMap : listMap) {
                csvPrinter.printRecord(
                        objectMap.get(CVO_HEADER.SEQ.toString())
                        , objectMap.get(CVO_HEADER.LEGAL_ID.toString())
                        , objectMap.get(CVO_HEADER.WF_TYPE_ID.toString())
                        , objectMap.get(CVO_HEADER.WF_TYPE_DESC.toString())
                        , objectMap.get(CVO_HEADER.ACN.toString())
                        , objectMap.get(CVO_HEADER.COLL_ID.toString())
                        , objectMap.get(CVO_HEADER.COLL_TYPE.toString())
                        , objectMap.get(CVO_HEADER.PLAINTIFF.toString())
                        , objectMap.get(CVO_HEADER.PT_JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(CVO_HEADER.PT_JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(CVO_HEADER.PT_JUDGMENT_DT.toString())
                        , objectMap.get(CVO_HEADER.PT_SEIZE_DT.toString())
                        , objectMap.get(CVO_HEADER.ASSIGN_LAWYER_DT.toString())
                        , objectMap.get(CVO_HEADER.OFFICER_SENDDOC_DT.toString())
                        , objectMap.get(CVO_HEADER.PREFERED_DEBT_DT.toString())
                        , objectMap.get(CVO_HEADER.PREFERED_DEBT_AMOUNT.toString())
                        , objectMap.get(CVO_HEADER.PREFERED_COURT_DT.toString())
                        , objectMap.get(CVO_HEADER.PREFERED_COURT_AMOUNT.toString())
                        , objectMap.get(CVO_HEADER.SM_JUDGMENT_UNDECIDED_NO.toString())
                        , objectMap.get(CVO_HEADER.SM_JUDGMENT_UNDECIDED_YEAR.toString())
                        , objectMap.get(CVO_HEADER.SM_JUDGMENT_SUE_DT.toString())
                        , objectMap.get(CVO_HEADER.SM_JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(CVO_HEADER.SM_JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(CVO_HEADER.SM_JUDGMENT_DT.toString())
                        , objectMap.get(CVO_HEADER.SM_APPEAL_AMOUNT.toString())
                        , objectMap.get(CVO_HEADER.APPEAL_DT.toString())
                        , objectMap.get(CVO_HEADER.APPEAL_AMOUNT.toString())
                        , objectMap.get(CVO_HEADER.APPEAL_COURT_DT.toString())
                        , objectMap.get(CVO_HEADER.APPEAL_COURT_AMOUNT.toString())
                        , objectMap.get(CVO_HEADER.AP_JUDGMENT_UNDECIDED_NO.toString())
                        , objectMap.get(CVO_HEADER.AP_JUDGMENT_UNDECIDED_YEAR.toString())
                        , objectMap.get(CVO_HEADER.AP_JUDGMENT_SUE_DT.toString())
                        , objectMap.get(CVO_HEADER.AP_JUDGMENT_DECIDED_NO.toString())
                        , objectMap.get(CVO_HEADER.AP_JUDGMENT_DECIDED_YEAR.toString())
                        , objectMap.get(CVO_HEADER.AP_JUDGMENT_DT.toString())
                        , objectMap.get(CVO_HEADER.AP_APPEAL_AMOUNT.toString())
                        , objectMap.get(CVO_HEADER.AUCTION_TYPE.toString())
                        , objectMap.get(CVO_HEADER.SEIZE_DT.toString())
                        , objectMap.get(CVO_HEADER.LED_APPRAISAL.toString())
                        , objectMap.get(CVO_HEADER.APPROVED_DT.toString())
                        , objectMap.get(CVO_HEADER.APPRAISAL_VAL.toString())
                        , objectMap.get(CVO_HEADER.AUCTION_DT.toString())
                        , objectMap.get(CVO_HEADER.AUCTION_AMT.toString())
                        , objectMap.get(CVO_HEADER.LITIGTION_STATUS.toString())
                );
            }
            csvPrinter.printRecord("TOTAL : "+total);
            csvPrinter.flush();
            csvPrinter.close();

            //Copy file to FTP Server
            smbFileService.localFileToRemoteFile(fileName, "DCMS", date);
        } catch (Exception e) {
            LOGGER.error("Error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<Map<String, Object>> dataForCVO(String date) {
        List<Map<String, Object>> listMap = new ArrayList<>();
        String fileDate = DateUtil.convertStringToDate(date);
        ObjectMapper oMapper = new ObjectMapper();

        List resultCVOtList = findLitigationUpdate("CVO",fileDate);

        for (int i=0 ; i<resultCVOtList.size() ; i++) {
            Map<String, Object> CVOMap = new HashMap();
            Map dataMap = oMapper.convertValue(resultCVOtList.get(i), Map.class);
            CVOMap.put(CVO_HEADER.SEQ.toString(),i+1);
            CVOMap.put(CVO_HEADER.LEGAL_ID.toString(),dataMap.get("LEGAL_ID"));
            CVOMap.put(CVO_HEADER.WF_TYPE_ID.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.WF_TYPE_DESC.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.ACN.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.COLL_ID.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.COLL_TYPE.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.PLAINTIFF.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.PT_JUDGMENT_DECIDED_NO.toString(),"");
            CVOMap.put(CVO_HEADER.PT_JUDGMENT_DECIDED_YEAR.toString(),"");
            CVOMap.put(CVO_HEADER.PT_JUDGMENT_DT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.PT_SEIZE_DT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.ASSIGN_LAWYER_DT.toString(),dataMap.get("ASSIGN_LAWYER_DT"));
            CVOMap.put(CVO_HEADER.OFFICER_SENDDOC_DT.toString(),dataMap.get("OFFICER_SENDDOC_DT"));
            CVOMap.put(CVO_HEADER.PREFERED_DEBT_DT.toString(),dataMap.get("PREFERED_DEBT_DT"));
            CVOMap.put(CVO_HEADER.PREFERED_DEBT_AMOUNT.toString(),dataMap.get("PREFERED_DEBT_AMOUNT"));
            CVOMap.put(CVO_HEADER.PREFERED_COURT_DT.toString(),dataMap.get("PREFERED_COURT_DT"));
            CVOMap.put(CVO_HEADER.PREFERED_COURT_AMOUNT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_JUDGMENT_UNDECIDED_NO.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_JUDGMENT_UNDECIDED_YEAR.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_JUDGMENT_SUE_DT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_JUDGMENT_DECIDED_NO.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_JUDGMENT_DECIDED_YEAR.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_JUDGMENT_DT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.SM_APPEAL_AMOUNT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.APPEAL_DT.toString(),dataMap.get("APPEAL_DT"));
            CVOMap.put(CVO_HEADER.APPEAL_AMOUNT.toString(),dataMap.get("APPEAL_AMOUNT"));
            CVOMap.put(CVO_HEADER.APPEAL_COURT_DT.toString(),dataMap.get("APPEAL_COURT_DT"));
            CVOMap.put(CVO_HEADER.APPEAL_COURT_AMOUNT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_JUDGMENT_UNDECIDED_NO.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_JUDGMENT_UNDECIDED_YEAR.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_JUDGMENT_SUE_DT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_JUDGMENT_DECIDED_NO.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_JUDGMENT_DECIDED_YEAR.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_JUDGMENT_DT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.AP_APPEAL_AMOUNT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.AUCTION_TYPE.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.SEIZE_DT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.LED_APPRAISAL.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.APPROVED_DT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.APPRAISAL_VAL.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.AUCTION_DT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.AUCTION_AMT.toString(),dataMap.get(""));
            CVOMap.put(CVO_HEADER.LITIGTION_STATUS.toString(),dataMap.get(""));
            listMap.add(CVOMap);
        }
        return listMap;
    }
}
