-- testDeclareTable
DECLARE @MyTableVar TABLE (EmpID int NOT NULL, OldVacationHours int, NewVacationHours int, ModifiedDate datetime)
;

-- testDeclareTypeWithDefault
DECLARE @find varchar (30) = 'Man%'
;

-- testDeclareTypeList
DECLARE @group nvarchar (50), @sales money
;

-- testDeclareAs
DECLARE @LocationTVP AS LocationTableType
;

-- testDeclareType
DECLARE @find nvarchar (30)
;

-- testDeclareTypeList2
DECLARE @group nvarchar (50), @sales varchar (50)
;

