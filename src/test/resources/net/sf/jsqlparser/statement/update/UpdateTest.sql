-- testUpdateLowPriority
UPDATE LOW_PRIORITY table1 A SET A.columna = 'XXX'
;

-- testUpdateWithFrom
UPDATE table1 SET columna = 5 FROM table1 LEFT JOIN table2 ON col1 = col2
;

-- testWith
with a as(select 1 id_instrument_ref),b as(select 1 id_instrument_ref)update cfe.instrument_ref set id_instrument=null where id_instrument_ref=(select id_instrument_ref from a)
;

-- testUpdateMultiTable
UPDATE T1, T2 SET T1.C2 = T2.C2, T2.C3 = 'UPDATED' WHERE T1.C1 = T2.C1 AND T1.C2 < 10
;

-- testUpdateWithFunctions
UPDATE tablename SET col = SUBSTRING(col2, 1, 2)
;

-- testUpdateIssue338_1
UPDATE mytable SET status = (status & 1)
;

-- testUpdateIssue338_2
UPDATE mytable SET status = (status + 1)
;

-- testUpdateIssue962Validate
UPDATE tbl_user_card SET validate = '1', identityCodeFlag = 1 WHERE id = 9150000293816
;

-- testUpdateWithSelect2
UPDATE mytable SET (col1, col2, col3) = (SELECT a, b, c FROM mytable2)
;

-- testUpdateWithOrderByAndLimit
UPDATE tablename SET col = 'thing' WHERE id = 1 ORDER BY col LIMIT 10
;

-- testUpdateIgnoreModifier
UPDATE IGNORE table1 A SET A.columna = 'XXX'
;

-- testUpdateIgnoreModifier
UPDATE table1 A SET A.columna = 'XXX'
;

-- testUpdateWithSelect
UPDATE NATION SET (N_NATIONKEY) = (SELECT ? FROM SYSIBM.SYSDUMMY1)
;

-- testUpdateIssue508LeftShift
UPDATE user SET num = 1 << 1 WHERE id = 1
;

-- testUpdateIssue338
UPDATE mytable SET status = (status & ~1)
;

-- testUpdateIssue750
update a,(select*from c)b set a.id=b.id where a.id=b.id
;

-- testUpdateIssue826
update message_topic inner join message_topic_config on message_topic.id=message_topic_config.topic_id set message_topic_config.enable_flag='n',message_topic_config.updated_by='test',message_topic_config.update_at='2019-07-16' where message_topic.name='test' and message_topic_config.enable_flag='y'
;

-- testUpdateWithOrderBy
UPDATE tablename SET col = 'thing' WHERE id = 1 ORDER BY col
;

-- testUpdateWithDeparser
UPDATE table1 AS A SET A.columna = 'XXX' WHERE A.cod_table = 'YYY'
;

-- testUpdateMultipleModifiers
UPDATE LOW_PRIORITY IGNORE table1 A SET A.columna = 'XXX'
;

-- testUpdateOutputClause
update humanresources.employee set vacationhours=vacationhours*1.25,modifieddate=getdate()output inserted.businessentityid,deleted.vacationhours,inserted.vacationhours,inserted.modifieddate into @mytablevar
;

-- testUpdateOutputClause
update production.workorder set scrapreasonid=4 output deleted.scrapreasonid,inserted.scrapreasonid,inserted.workorderid,inserted.productid,p.name into @mytestvar from production.workorder as wo inner join production.product as p on wo.productid=p.productid and wo.scrapreasonid=16 and p.productid=733
;

-- testUpdateWithReturningList
UPDATE tablename SET col = 'thing' WHERE id = 1 ORDER BY col LIMIT 10 RETURNING col_1, col_2, col_3
;

-- testUpdateWithReturningList
UPDATE tablename SET col = 'thing' WHERE id = 1 RETURNING col_1, col_2, col_3
;

-- testUpdateWithReturningList
UPDATE tablename SET col = 'thing' WHERE id = 1 ORDER BY col LIMIT 10 RETURNING col_1 AS Bar, col_2 AS Baz, col_3 AS Foo
;

-- testUpdateWithReturningList
UPDATE tablename SET col = 'thing' WHERE id = 1 RETURNING col_1 AS Bar, col_2 AS Baz, col_3 AS Foo
;

-- testUpdateWithReturningList
UPDATE tablename SET col = 'thing' WHERE id = 1 RETURNING ABS(col_1) AS Bar, ABS(col_2), col_3 AS Foo
;

-- testOracleHint
update mytable set col1='as',col2=?,col3=565 where o>=3
;

-- testUpdateIssue167_SingleQuotes
update tablename set name='customer 2',address='address \' ddad2',auth_key='samplekey' where id=2
;

-- testUpdateWithReturningAll
UPDATE tablename SET col = 'thing' WHERE id = 1 ORDER BY col LIMIT 10 RETURNING *
;

-- testUpdateWithReturningAll
UPDATE tablename SET col = 'thing' WHERE id = 1 RETURNING *
;

-- testUpdateWithLimit
UPDATE tablename SET col = 'thing' WHERE id = 1 LIMIT 10
;

-- testUpdateSetsIssue1316
update test set(a,b)=(select '1','2')
;

-- testUpdateSetsIssue1316
update test set a='1',b='2'
;

-- testUpdateSetsIssue1316
update test set(a,b)=('1','2')
;

-- testUpdateSetsIssue1316
update test set(a,b)=(values('1','2'))
;

-- testUpdateSetsIssue1316
update test set(a,b)=(1,(select 2))
;

-- testUpdateSetsIssue1316
update prpjpaymentbill b set(b.packagecode,b.packageremark,b.agentcode)=(select p.payrefreason,p.classcode,p.riskcode from prpjcommbill p where p.policertiid='sddh200937010330006366'),b.payrefnotype='05',b.packageunit='4101170402' where b.payrefno='b370202091026000005'
;

-- testUpdateVariableAssignment
UPDATE transaction_id SET latest_id_wallet = (@cur_id_wallet := latest_id_wallet) + 1
;

