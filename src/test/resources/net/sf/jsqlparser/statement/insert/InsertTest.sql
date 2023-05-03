-- testInsertTableArrays4
insert into sal_emp values('carol',array[20000,25000,25000,25000],array[['breakfast','consulting'],['meeting','lunch']])
;

-- testModifierIgnore
INSERT IGNORE INTO `AoQiSurvey_FlashVersion_Single` VALUES (302215163, 'WIN 16,0,0,235')
;

-- testInsertMultiRowValue
INSERT INTO mytable (col1, col2) VALUES (a, b), (d, e)
;

-- testInsertTableWithAliasIssue526
INSERT INTO account t (name, addr, phone) SELECT * FROM user
;

-- testInsertWithReturning2
INSERT INTO mytable (mycolumn) VALUES ('1') RETURNING *
;

-- testInsertWithReturning3
INSERT INTO mytable (mycolumn) VALUES ('1') RETURNING id AS a1, id2 AS a2
;

-- testInsertUnionSelectIssue1491
insert into table1(tf1,tf2,tf2)select sf1,sf2,sf3 from s1 union select rf1,rf2,rf2 from r1
;

-- testInsertUnionSelectIssue1491
insert into table1(tf1,tf2,tf2)(select sf1,sf2,sf3 from s1 union select rf1,rf2,rf2 from r1)
;

-- testInsertUnionSelectIssue1491
insert into table1(tf1,tf2,tf2)(select sf1,sf2,sf3 from s1)union(select rf1,rf2,rf2 from r1)
;

-- testInsertUnionSelectIssue1491
insert into table1(tf1,tf2,tf2)((select sf1,sf2,sf3 from s1)union(select rf1,rf2,rf2 from r1))
;

-- testInsertUnionSelectIssue1491
(with a as(select*from dual)select*from a)
;

-- testInsertKeyWordIntervalIssue682
INSERT INTO BILLING_TASKS (TIMEOUT, INTERVAL, RETRY_UPON_FAILURE, END_DATE, MAX_RETRY_COUNT, CONTINUOUS, NAME, LAST_RUN, START_TIME, NEXT_RUN, ID, UNIQUE_NAME, INTERVAL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, NULL, ?, ?, ?, ?, ?)
;

-- testNextValueFor
INSERT INTO tracker (monitor_id, user_id, module_name, item_id, item_summary, team_id, date_modified, action, visible, id) VALUES (?, ?, ?, ?, ?, ?, to_date(?, 'YYYY-MM-DD HH24:MI:SS'), ?, ?, NEXT VALUE FOR TRACKER_ID_SEQ)
;

-- testInsertValuesWithDuplicateEliminationInDeparsing
INSERT INTO TEST (ID, COUNTER) VALUES (123, 0) ON DUPLICATE KEY UPDATE COUNTER = COUNTER + 1
;

-- testHexValues2
INSERT INTO TABLE2 VALUES ('1', "DSDD", 0xEFBFBDC7AB)
;

-- testHexValues3
INSERT INTO TABLE2 VALUES ('1', "DSDD", 0xabcde)
;

-- testInsertSetInDeparsing
INSERT INTO mytable SET col1 = 12, col2 = name1 * name2
;

-- testHexValues
INSERT INTO TABLE2 VALUES ('1', "DSDD", x'EFBFBDC7AB')
;

-- testWithDeparsingIssue406
insert into mytab3(a,b,c)select a,b,c from mytab where exists(with t as(select*from mytab2)select*from t)
;

-- testSimpleInsert
INSERT INTO example (num, name, address, tel) VALUES (1, 'name', 'test ', '1234-1234')
;

-- testWithAtFront
with foo as(select attr from bar)insert into lalelu(attr)select attr from foo
;

-- testInsertSetWithDuplicateEliminationInDeparsing
INSERT INTO mytable SET col1 = 122 ON DUPLICATE KEY UPDATE col2 = col2 + 1, col3 = 'saint'
;

-- testInsertKeyWordEnableIssue592
INSERT INTO T_USER (ID, EMAIL_VALIDATE, ENABLE, PASSWORD) VALUES (?, ?, ?, ?)
;

-- testInsertWithKeywords
INSERT INTO kvPair (value, key) VALUES (?, ?)
;

-- testModifierPriority1
INSERT DELAYED INTO kvPair (value, key) VALUES (?, ?)
;

-- testModifierPriority2
INSERT LOW_PRIORITY INTO kvPair (value, key) VALUES (?, ?)
;

-- testModifierPriority3
INSERT HIGH_PRIORITY INTO kvPair (value, key) VALUES (?, ?)
;

-- testDisableKeywordIssue945
INSERT INTO SOMESCHEMA.TEST (DISABLE, TESTCOLUMN) VALUES (1, 1)
;

-- testWithListIssue282
WITH myctl AS (SELECT a, b FROM mytable) INSERT INTO mytable SELECT a, b FROM myctl
;

-- testInsertWithReturning
INSERT INTO mytable (mycolumn) VALUES ('1') RETURNING id
;

-- testKeywordDefaultIssue1470
INSERT INTO mytable (col1, col2, col3) VALUES (?, 'sadfsd', default)
;

-- testInsertOutputClause
insert into dbo.employeesales(lastname,firstname,currentsales)output inserted.employeeid,inserted.lastname,inserted.firstname,inserted.currentsales,inserted.projectedsales into @mytablevar select c.lastname,c.firstname,sp.salesytd from sales.salesperson as sp inner join person.person as c on sp.businessentityid=c.businessentityid where sp.businessentityid like '2%' order by c.lastname,c.firstname
;

-- testInsertOnConflictIssue1551
insert into distributors(did,dname)values(5,'gizmo transglobal'),(6,'associated computing,inc')on conflict(did)do update set dname=excluded.dname
;

-- testInsertOnConflictIssue1551
insert into distributors(did,dname)values(7,'redline gmbh')on conflict(did)do nothing
;

-- testInsertOnConflictIssue1551
insert into distributors as d(did,dname)values(8,'anvil distribution')on conflict(did)do update set dname=excluded.dname||'(formerly '||d.dname||')' where d.zipcode<>'21201'
;

-- testInsertOnConflictIssue1551
insert into distributors(did,dname)values(9,'antwerp design')on conflict on constraint distributors_pkey do nothing
;

-- testInsertOnConflictIssue1551
insert into distributors(did,dname)values(10,'conrad international')on conflict(did)where is_active do nothing
;

-- testNextValIssue773
INSERT INTO tableA (ID, c1, c2) SELECT hibernate_sequence.nextval, c1, c2 FROM tableB
;

-- testBackslashEscapingIssue827
INSERT INTO my_table (my_column_1, my_column_2) VALUES ('my_value_1\\', 'my_value_2')
;

-- testInsertWithSelect
insert into mytable(mycolumn)with a as(select mycolumn from mytable)select mycolumn from a
;

-- testInsertWithSelect
insert into mytable(mycolumn)(with a as(select mycolumn from mytable)select mycolumn from a)
;

-- testIssue223
insert into user values(2001,'\'clark\'','kent')
;

-- testNextVal
INSERT INTO tracker (monitor_id, user_id, module_name, item_id, item_summary, team_id, date_modified, action, visible, id) VALUES (?, ?, ?, ?, ?, ?, to_date(?, 'YYYY-MM-DD HH24:MI:SS'), ?, ?, NEXTVAL FOR TRACKER_ID_SEQ)
;

-- testOracleHint
insert into mytable values(1,2,3)
;

-- testInsertSelect
INSERT INTO mytable (mycolumn) SELECT mycolumn FROM mytable
;

-- testInsertSelect
INSERT INTO mytable (mycolumn) (SELECT mycolumn FROM mytable)
;

-- testKeywordPrecisionIssue363
INSERT INTO test (user_id, precision) VALUES (1, '111')
;

-- testDuplicateKey
INSERT INTO Users0 (UserId, Key, Value) VALUES (51311, 'T_211', 18) ON DUPLICATE KEY UPDATE Value = 18
;

-- insertOnConflictObjectsTest
with a(a,b,c)as(select 1,2,3)insert into test select*from a on conflict(a)do nothing
;

-- insertOnConflictObjectsTest
with a(a,b,c)as(select 1,2,3)insert into test select*from a on conflict on constraint a do update set a=b/2,b=b/2 where a=1
;

