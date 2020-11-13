create or alter FUNCTION fc_jb001 (
	@DOCUPDATE AS VARCHAR(255)
)
RETURNS TABLE
AS
RETURN
WITH CTE AS (
	SELECT
	d.id as docID
	,d.doc_number as docNumber
	,d.doc_type as docType
	,d.type_process as typeProcess
	,d.updated_date
	,dh.[sequence] as sequence
	,dh.updated_by
	,dh.user_role_to as userRoleTo
	,dh.action_time as actionTime
	,dp.preferential_request_date as preferentialRequestDate
	,cod.total_amount as totalAmount
	,cod.permiss_date as permissDate
	,ROW_NUMBER() OVER(PARTITION BY d.id ORDER BY d.id ASC,dh.[sequence] DESC ,dp.updated_date DESC,cod.updated_date DESC) as RowNumber
	FROM document d
	left join document_history dh on d.id = dh.document
	LEFT JOIN document_progress dp on d.id = dp.document
	LEFT JOIN court_order_detail cod on dp.id = cod.document_progress
	WHERE datediff(day, d.updated_date , @DOCUPDATE) = 0
)
SELECT * FROM CTE
WHERE RowNumber = 1;

---SELECT * FROM fc_jb001('2020-11-12')