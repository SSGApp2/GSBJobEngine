-- Function สำหรับหาหน่วยงานลูก
CREATE OR ALTER FUNCTION deptchildren(
	@CODE AS VARCHAR(255)
)
RETURNS TABLE
AS
RETURN
WITH deptchildren AS
(
	select code ,control_dept,type,status  from department where code=@CODE
	UNION  all
	select d2.code ,d2.control_dept,d2.type,d2.status  from department d2
	INNER  JOIN  deptchildren ON d2.control_dept= deptchildren.code
	where d2.status ='Y'
)
SELECT * FROM deptchildren ;

--select * from deptchildren('15002203')