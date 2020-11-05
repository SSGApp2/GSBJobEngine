package com.app2.engine.config;

public class Statement {

    public static String GET_DATA_BKC = "SELECT  DISTINCT d.id," +
            "d.updated_date as doc_up," +
            "dp.updated_date as dp_up," +
            "ap.updated_date as ap_up," +
            "ag.updated_date as ag_up," +
            "ass.updated_date as ass_up," +
            "con.updated_date as con_up," +
            "dh.[sequence],\n" +
            "d.doc_number,\n" +
            "dh.action_time,\n" +
            "dp.notic_doc_send_date,\n" +
            "dp.black_case_number,\n" +
            "dp.law_suit_send_date,\n" +
            "dp.red_case_number,\n" +
            "dp.adjudication,\n" +
            "dp.adj_date,\n" +
            "dp.gazette_date,\n" +
            "ap.account_payment_date,\n" +
            "ap.debt_amount,\n" +
            "ap.amount,\n" +
            "con.confiscate_date,\n" +
            "con.cost_est_legal_ex_office,\n" +
            "con.cost_est_legal_bank_date,\n" +
            "ass.amount_buy,\n" +
            "d.doc_status \n" +
            "FROM document d \n" +
            "JOIN document_history dh on d.id = dh.document \n" +
            "JOIN confiscate con on d.id = con.document \n" +
            "join document_progress dp on d.id = dp.document \n" +
            "join account_payment ap on dp.id = ap.document_progress \n" +
            "join asset_group ag on d.id = ag.document \n" +
            "join asset_sale ass on ag.id = ass.asset_group ";

    public static String GET_DATA_BKO = "SELECT DISTINCT d.id\n" +
            ",d.updated_date as doc_up\n" +
            ",document_progress.updated_date as progress_up\n" +
            ",account_payment.updated_date  as account_up\n" +
            ",d.doc_number \n" +
            ",document_history.action_time\n" +
            ",document_progress.black_case_number \n" +
            ",document_progress.red_case_number \n" +
            ",document_progress.date_adjudicate_out \n" +
            ",document_progress.gazette_date \n" +
            ",account_payment.account_payment_date \n" +
            ",account_payment.debt_amount \n" +
            ",account_payment.amount \n" +
            "from document d\n" +
            "join document_history on d.id = document_history.document\n" +
            "join document_progress on d.id = document_progress.document\n" +
            "join account_payment on document_progress.id = account_payment.document_progress ";

    public static String GET_DOC_CUR_DATE = "SELECT \n" +
            "d.id as docID\n" +
            "FROM document d \n";


    public static String GET_DOC_CUR_DATE_AND_DOCHIS_BY_ROLE_BY_DOC = "SELECT \n" +
            "d.id as idDoc\n" +
            ",dh.[sequence] as sequence\n" +
            ",dh.user_role_to as userRoleTo\n" +
            ",dh.action_time as actionTime\n" +
            "FROM document_history dh \n" +
            "LEFT JOIN document d ON d.id = dh.document \n";
}
