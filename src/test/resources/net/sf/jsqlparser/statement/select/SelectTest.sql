-- testIssue584MySQLValueListExpression
SELECT a, b FROM T WHERE (T.a, T.b) = (c, d)
;

-- testIssue584MySQLValueListExpression
SELECT a FROM T WHERE (T.a) = (SELECT b FROM T, c, d)
;

-- testPivotWithAlias2
SELECT * FROM (SELECT * FROM mytable LEFT JOIN mytable2 ON Factor_ID = Id) f PIVOT (max(f.value) FOR f.factoryCode IN (ZD, COD, SW, PH)) d
;

-- testPivotWithAlias3
SELECT * FROM (SELECT * FROM mytable LEFT JOIN mytable2 ON Factor_ID = Id) PIVOT (max(f.value) FOR f.factoryCode IN (ZD, COD, SW, PH)) d
;

-- testPivotWithAlias4
SELECT * FROM (SELECT a.Station_ID stationId, b.Factor_Code factoryCode, a.Value value FROM T_Data_Real a LEFT JOIN T_Bas_Factor b ON a.Factor_ID = b.Id) f PIVOT (max(f.value) FOR f.factoryCode IN (ZD, COD, SW, PH)) d
;

-- testAnalyticFunctionIssue670
SELECT last_value(some_column IGNORE NULLS) OVER (PARTITION BY some_other_column_1, some_other_column_2 ORDER BY some_other_column_3 ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) column_alias FROM some_table
;

-- testSelectAliasWithoutAs
SELECT mycolumn "My Column Name" FROM mytable
;

-- testAllConditionSubSelect
select e1.empno,e1.sal from emp e1 where e1.sal>all(select e2.sal from emp e2 where e2.deptno=10)
;

-- testSimpleSigns
SELECT +1, -1 FROM tableName
;

-- testIsNotTrue
SELECT col FROM tbl WHERE col IS NOT TRUE
;

-- testMultiValueIn2
SELECT * FROM mytable WHERE (trim(a), trim(b)) IN (SELECT a, b FROM mytable2)
;

-- testMultiValueIn3
SELECT * FROM mytable WHERE (SSN, SSM) IN (('11111111111111', '22222222222222'))
;

-- testMultiValueIn4
SELECT * FROM mytable WHERE (a, b) IN ((1, 2), (3, 4), (5, 6), (7, 8))
;

-- testIssue572TaskReplacement
SELECT task_id AS "Task Id" FROM testtable
;

-- testNestedCaseCondition
SELECT CASE WHEN CASE WHEN 1 THEN 10 ELSE 20 END > 15 THEN 'BBB' END FROM tab1
;

-- testNestedCaseCondition
SELECT (CASE WHEN (CASE a WHEN 1 THEN 10 ELSE 20 END) > 15 THEN 'BBB' END) FROM tab1
;

-- testSelectForUpdateOfTable
SELECT foo.*, bar.* FROM foo, bar WHERE foo.id = bar.foo_id FOR UPDATE OF foo
;

-- testTopWithJdbcParameter
SELECT TOP ?1 * FROM mytable WHERE mytable.col = 9
;

-- testPostgreSQLRegExpCaseSensitiveMatch
SELECT a, b FROM foo WHERE a ~ '[help].*'
;

-- testTableFunctionWithNoParams
SELECT f2 FROM SOME_FUNCTION()
;

-- testKeyWordCreateIssue941_2
select f.select from `from` f
;

-- testMultiValueIn_NTuples
SELECT * FROM mytable WHERE (a, b, c, d, e) IN ((1, 2, 3, 4, 5), (6, 7, 8, 9, 10), (11, 12, 13, 14, 15))
;

-- testNotExistsIssue
SELECT * FROM t001 t WHERE NOT EXISTS (SELECT * FROM t002 t1 WHERE t.c1 = t1.c1 AND t.c2 = t1.c2 AND ('241' IN (t1.c3 || t1.c4)))
;

-- testMultiValueNotInBinds
SELECT * FROM mytable WHERE (a, b) NOT IN ((?, ?), (?, ?))
;

-- testConditionalParametersForFunctions
SELECT myFunc(SELECT mycol FROM mytable)
;

-- testKeyWordExceptIssue1026
SELECT * FROM xxx WHERE exclude = 1
;

-- testKeyWordExceptIssue1040
SELECT FORMAT(100000, 2)
;

-- testKeyWordExceptIssue1044
SELECT SP_ID FROM ST_PR WHERE INSTR(',' || SP_OFF || ',', ',' || ? || ',') > 0
;

-- testKeyWordExceptIssue1055
SELECT INTERVAL ? DAY
;

-- testWhereIssue240_0
SELECT count(*) FROM mytable WHERE 0
;

-- testWhereIssue240_1
SELECT count(*) FROM mytable WHERE 1
;

-- testOracleJoin2
SELECT * FROM tabelle1, tabelle2 WHERE tabelle1.a(+) = tabelle2.b
;

-- testOracleJoin3
SELECT * FROM tabelle1, tabelle2 WHERE tabelle1.a(+) > tabelle2.b
;

-- testOracleJoin4
SELECT * FROM tabelle1, tabelle2 WHERE tabelle1.a(+) = tabelle2.b AND tabelle1.b(+) IN ('A', 'B')
;

-- testArrayRange
SELECT (arr[1:3])[1] FROM MYTABLE
;

-- testFormatKeywordIssue1078
SELECT FORMAT(date, 'yyyy-MM') AS year_month FROM mine_table
;

-- testIsDistinctFrom
SELECT name FROM tbl WHERE name IS DISTINCT FROM foo
;

-- testSessionKeywordIssue876
SELECT ID_COMPANY FROM SESSION.COMPANY
;

-- testCrossApplyIssue344
select s.*,c.*,calc2.summary from student s join class c on s.class_id=c.id cross apply(select s.first_name+' '+s.last_name+'('+s.sex+')' as student_full_name)calc1 cross apply(select case c.some_styling_type when 'a' then c.name+'-'+calc1.student_full_name when 'b' then calc1.student_full_name+'-'+c.name else calc1.student_full_name end as summary)calc2
;

-- testKeyWordView
select ma.m_a_id,ma.anounsment,ma.max_view,ma.end_date,ma.view from member_anounsment as ma where(((ma.end_date>now())and(ma.max_view>=ma.view))and((ma.member_id='xxx')))
;

-- testIssue583CharacterLiteralAsAlias
SELECT CASE WHEN T.ISC = 1 THEN T.EXTDESC WHEN T.b = 2 THEN '2' ELSE T.C END AS 'Test' FROM T
;

-- testMultiPartTableNameWithSchemaName
SELECT columnName FROM schemaName.tableName
;

-- testSimpleJoinOnExpressionIssue1229
select t1.column1,t1.column2,t2.field1,t2.field2 from t_dt_ytb_01 t1,t_dt_ytb_02 t2 on t1.column1=t2.field1
;

-- testEscaped
SELECT _utf8'testvalue'
;

-- testInterval5_Issue228
SELECT ADDDATE(timeColumn1, INTERVAL 420 MINUTES) AS timeColumn1 FROM tbl
;

-- testInterval5_Issue228
SELECT ADDDATE(timeColumn1, INTERVAL -420 MINUTES) AS timeColumn1 FROM tbl
;

-- testKeywordAlgorithmIssue1137
SELECT algorithm FROM tablename
;

-- testKeywordAlgorithmIssue1138
SELECT * FROM in.tablename
;

-- testSelectBrackets2
SELECT (EXTRACT(epoch FROM age(d1, d2)) / 2)::numeric
;

-- testSelectBrackets3
SELECT avg((EXTRACT(epoch FROM age(d1, d2)) / 2)::numeric)
;

-- testSelectBrackets4
SELECT (1 / 2)::numeric
;

-- testKeywordCharacterIssue884
SELECT Character, Duration FROM actor
;

-- testSimpleAdditionsAndSubtractionsWithSigns
SELECT 1 - 1, 1 + 1, -1 - 1, -1 + 1, +1 + 1, +1 - 1 FROM tableName
;

-- testProblematicDeparsingIssue1183
SELECT ARRAY_AGG(NAME ORDER BY ID) FILTER (WHERE NAME IS NOT NULL)
;

-- testSimilarToIssue789
SELECT * FROM mytable WHERE (w_id SIMILAR TO '/foo/__/bar/(left|right)/[0-9]{4}-[0-9]{2}-[0-9]{2}(/[0-9]*)?')
;

-- testReservedKeywordsMSSQLUseIndexIssue1325
select col from table use index(primary)
;

-- testParenthesisAroundFromItem2
SELECT * FROM (mytable myalias)
;

-- testParenthesisAroundFromItem3
SELECT * FROM (mytable) myalias
;

-- testNotVariant
SELECT ! (1 + 1)
;

-- testCheckDateFunctionIssue
SELECT DATEDIFF(NOW(), MIN(s.startTime))
;

-- testOneColumnFullTextSearchMySQL
SELECT MATCH (col1) AGAINST ('test' IN NATURAL LANGUAGE MODE) relevance FROM tbl
;

-- testForUpdateWaitParseDeparse
SELECT * FROM mytable FOR UPDATE WAIT 60
;

-- testNamedParametersPR702_2
SELECT substring(id, 2, 3) FROM mytable
;

-- testNamedParametersPR702_2
SELECT substring(id from 2 for 3) FROM mytable
;

-- testSelectAliasInQuotes
SELECT mycolumn AS "My Column Name" FROM mytable
;

-- selectIsolationKeywordsAsAlias
SELECT col FROM tbl cs
;

-- testIssue235SimplifiedCase3
SELECT CASE WHEN (CASE WHEN (CASE WHEN (1) THEN 0 END) THEN 0 END) THEN 0 END FROM a
;

-- testIssue235SimplifiedCase4
SELECT CASE WHEN (CASE WHEN (CASE WHEN (CASE WHEN (1) THEN 0 END) THEN 0 END) THEN 0 END) THEN 0 END FROM a
;

-- testIssue167_singleQuoteEscape
select 'a'
;

-- testIssue167_singleQuoteEscape
select ''''
;

-- testIssue167_singleQuoteEscape
select '\''
;

-- testIssue167_singleQuoteEscape
select 'ab''ab'
;

-- testIssue167_singleQuoteEscape
select 'ab\'ab'
;

-- testNestedCast
SELECT acolumn::bit (64)::bigint FROM mytable
;

-- testOrderBy
SELECT * FROM tab1 WHERE a > 34 GROUP BY tab1.b ORDER BY tab1.a DESC, tab1.b ASC
;

-- testOrderBy
SELECT * FROM tab1 WHERE a > 34 GROUP BY tab1.b ORDER BY tab1.a, 2
;

-- testCastTypeProblem2
SELECT col1::varchar FROM tabelle1
;

-- testCastTypeProblem3
SELECT col1::varchar (256) FROM tabelle1
;

-- testCastTypeProblem4
SELECT 5::varchar (256) FROM tabelle1
;

-- testCastTypeProblem5
SELECT 5.67::varchar (256) FROM tabelle1
;

-- testCastTypeProblem6
SELECT 'test'::character varying FROM tabelle1
;

-- testCastTypeProblem7
SELECT CAST('test' AS character varying) FROM tabelle1
;

-- testCastTypeProblem8
SELECT CAST('123' AS double precision) FROM tabelle1
;

-- testMultiValueInBinds
SELECT * FROM mytable WHERE (a, b) IN ((?, ?), (?, ?))
;

-- testCastInCast
SELECT CAST(CAST(a AS numeric) AS varchar) FROM tabelle1
;

-- testSkipFirst
SELECT SKIP ?1 FIRST f1 c1, c2 FROM t1
;

-- testMultiPartTableNameWithDatabaseNameAndSchemaName
SELECT columnName FROM databaseName.schemaName.tableName
;

-- testCurrentIssue940
SELECT date(current) AS test_date FROM systables WHERE tabid = 1
;

-- testForXmlPath
SELECT '|' + person_name FROM person JOIN person_group ON person.person_id = person_group.person_id WHERE person_group.group_id = 1 FOR XML PATH('')
;

-- testNotVariant2
SELECT ! 1 + 1
;

-- testNotVariant3
SELECT NOT (1 + 1)
;

-- testNotVariant4
SELECT * FROM mytable WHERE NOT (1 = 1)
;

-- testNotVariant4
SELECT * FROM mytable WHERE ! (1 = 1)
;

-- testSelectJoinWithComma
SELECT cb.Genus, cb.Species FROM Coleccion_de_Briofitas AS cb, unigeoestados AS es WHERE es.nombre = "Tamaulipas" AND cb.the_geom = es.geom
;

-- testAdditionalLettersGerman
SELECT colä, colö, colü FROM testtableäöü
;

-- testAdditionalLettersGerman
SELECT colA, colÖ, colÜ FROM testtableÄÖÜ
;

-- testAdditionalLettersGerman
SELECT Äcol FROM testtableÄÖÜ
;

-- testAdditionalLettersGerman
SELECT ßcolß FROM testtableß
;

-- testVariableAssignment
SELECT @SELECTVariable = 2
;

-- testSelectAllOperatorIssue1140
SELECT * FROM table t0 WHERE t0.id != all(5)
;

-- testIssue566PostgreSQLEscaped
SELECT E'test'
;

-- testFullTextSearchInDefaultMode
SELECT col FROM tbl WHERE MATCH (col1,col2,col3) AGAINST ('test') ORDER BY col
;

-- testMySQLHintStraightJoin
SELECT col FROM tbl STRAIGHT_JOIN tbl2 ON tbl.id = tbl2.id
;

-- testIntervalWithColumn
SELECT DATE_ADD(start_date, INTERVAL duration MINUTE) AS end_datetime FROM appointment
;

-- testTryCast
SELECT TRY_CAST(a AS varchar) FROM tabelle1
;

-- testTryCast
SELECT CAST(a AS varchar2) FROM tabelle1
;

-- testTop
SELECT TOP 3 * FROM mytable WHERE mytable.col = 9
;

-- testWithUnionProblem
WITH test AS ((SELECT mslink FROM tablea) UNION (SELECT mslink FROM tableb)) SELECT * FROM tablea WHERE mslink IN (SELECT mslink FROM test)
;

-- testMysqlQuote
SELECT `a.OWNERLASTNAME`, `OWNERFIRSTNAME` FROM `ANTIQUEOWNERS` AS a, ANTIQUES AS b WHERE b.BUYERID = a.OWNERID AND b.ITEM = 'Chair'
;

-- testMissingOffsetIssue620
SELECT a, b FROM test OFFSET 0
;

-- testMissingOffsetIssue620
SELECT a, b FROM test LIMIT 1 OFFSET 0
;

-- testKeyWordOfIssue1029
SELECT of.Full_Name_c AS FullName FROM comdb.Offer_c AS of
;

-- testMissingLimitIssue1505
(SELECT * FROM mytable) LIMIT 1
;

-- testForUpdateNoWait
SELECT * FROM mytable FOR UPDATE NOWAIT
;

-- testCheckDateFunctionIssue_2
SELECT DATE_SUB(NOW(), INTERVAL :days DAY)
;

-- testCheckDateFunctionIssue_3
SELECT DATE_SUB(NOW(), INTERVAL 1 DAY)
;

-- testEndKeyword
SELECT end AS end_6 FROM mytable
;

-- testCaseWithComplexWhenExpression
select av.app_id,max(av.version_no)as version_no from app_version av join app_version_policy avp on av.id=avp.app_version_id where av.`status`=1 and case when avp.area is not null and length(avp.area)>0 then avp.area like concat('%,','12',',%')or avp.area like concat('%,','13',',%')else 1=1 end
;

-- testEmptyDoubleQuotes_2
SELECT * FROM mytable WHERE col = " "
;

-- testProblemIssue375Simplified
select*from(((pg_catalog.pg_class c inner join pg_catalog.pg_namespace n on n.oid=c.relnamespace and c.relname='business' and n.nspname='public')inner join pg_catalog.pg_attribute a on(not a.attisdropped)and a.attnum>0 and a.attrelid=c.oid)inner join pg_catalog.pg_type t on t.oid=a.atttypid)left outer join pg_attrdef d on a.atthasdef and d.adrelid=a.attrelid and d.adnum=a.attnum order by n.nspname,c.relname,attnum
;

-- testGroupedByWithExtraBracketsIssue1168
select sum(a)as amount,b,c from test_table group by rollup((a,b),c)
;

-- testGroupedByWithExtraBracketsIssue1210
select a,b,c from table group by rollup(a,b,c)
;

-- testGroupedByWithExtraBracketsIssue1210
select a,b,c from table group by rollup((a,b,c))
;

-- testProblemIssue485Date
SELECT * FROM tab WHERE tab.date = :date
;

-- testNotProblem1
SELECT * FROM mytab WHERE NOT v IN (1, 2, 3, 4, 5, 6, 7)
;

-- testNotProblem2
SELECT * FROM mytab WHERE NOT func(5)
;

-- testSelectInnerWith
SELECT * FROM (WITH actor AS (SELECT 'a' aid FROM DUAL) SELECT aid FROM actor)
;

-- testReservedKeyword2
SELECT open FROM tableName
;

-- testReservedKeyword3
SELECT * FROM mytable1 t JOIN mytable2 AS prior ON t.id = prior.id
;

-- testNotLikeIssue775
SELECT * FROM mybatisplus WHERE id NOT LIKE ?
;

-- testSqlNoCache
SELECT SQL_NO_CACHE sales.date FROM sales
;

-- testTryCastInTryCast
SELECT TRY_CAST(TRY_CAST(a AS numeric) AS varchar) FROM tabelle1
;

-- testLongQualifiedNamesIssue763
SELECT mongodb.test.test.intField, postgres.test.test.intField, postgres.test.test.datefield FROM mongodb.test.test JOIN postgres.postgres.test.test ON mongodb.test.test.intField = postgres.test.test.intField WHERE mongodb.test.test.intField = 123
;

-- testProblemIssue375
select n.nspname,c.relname,a.attname,a.atttypid,t.typname,a.attnum,a.attlen,a.atttypmod,a.attnotnull,c.relhasrules,c.relkind,c.oid,pg_get_expr(d.adbin,d.adrelid),case t.typtype when 'd' then t.typbasetype else 0 end,t.typtypmod,c.relhasoids from(((pg_catalog.pg_class c inner join pg_catalog.pg_namespace n on n.oid=c.relnamespace and c.relname='business' and n.nspname='public')inner join pg_catalog.pg_attribute a on(not a.attisdropped)and a.attnum>0 and a.attrelid=c.oid)inner join pg_catalog.pg_type t on t.oid=a.atttypid)left outer join pg_attrdef d on a.atthasdef and d.adrelid=a.attrelid and d.adnum=a.attnum order by n.nspname,c.relname,attnum
;

-- testProblemIssue435
SELECT if(z, 'a', 'b') AS business_type FROM mytable1
;

-- testProblemIssue445
SELECT E.ID_NUMBER, row_number() OVER (PARTITION BY E.ID_NUMBER ORDER BY E.DEFINED_UPDATED DESC) rn FROM T_EMPLOYMENT E
;

-- testKeywordSequenceIssue1074
SELECT * FROM t_user WITH (NOLOCK)
;

-- testKeywordSequenceIssue1075
SELECT a.sequence FROM all_procedures a
;

-- testFirst
SELECT FIRST 5 alias.columnName1, alias.columnName2 FROM schemaName.tableName alias ORDER BY alias.columnName2 DESC
;

-- testFirst
SELECT FIRST firstVar c1, c2 FROM t
;

-- testIlike
SELECT col1 FROM table1 WHERE col1 ILIKE '%hello%'
;

-- testIsNot
SELECT * FROM test WHERE a IS NOT NULL
;

-- testLimit
SELECT * FROM mytable WHERE mytable.col = 9 LIMIT ? OFFSET 3
;

-- testLimit
SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?
;

-- testLimit
(SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?) UNION (SELECT * FROM mytable2 WHERE mytable2.col = 9 OFFSET ?) LIMIT 4 OFFSET 3
;

-- testLimit
(SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?) UNION ALL (SELECT * FROM mytable2 WHERE mytable2.col = 9 OFFSET ?) UNION ALL (SELECT * FROM mytable3 WHERE mytable4.col = 9 OFFSET ?) LIMIT 4 OFFSET 3
;

-- testRlike
SELECT * FROM mytable WHERE first_name RLIKE '^Ste(v|ph)en$'
;

-- testSigns
SELECT (-(1)), -(1), (-(columnName)), -(columnName), (-1), -1, (-columnName), -columnName FROM tableName
;

-- testUnion
SELECT * FROM mytable WHERE mytable.col = 9 UNION SELECT * FROM mytable3 WHERE mytable3.col = ? UNION SELECT * FROM mytable2 LIMIT 3, 4
;

-- testUnion
SELECT * FROM mytable WHERE mytable.col = 9 UNION SELECT * FROM mytable3 WHERE mytable3.col = ? UNION SELECT * FROM mytable2 ORDER BY COL DESC FETCH FIRST 1 ROWS ONLY WITH UR
;

-- testRegexpLike1
SELECT * FROM mytable WHERE REGEXP_LIKE(first_name, '^Ste(v|ph)en$')
;

-- testRegexpLike2
SELECT CASE WHEN REGEXP_LIKE(first_name, '^Ste(v|ph)en$') THEN 1 ELSE 2 END FROM mytable
;

-- testRegexpMySQL
SELECT * FROM mytable WHERE first_name REGEXP '^Ste(v|ph)en$'
;

-- testCase
SELECT a, CASE b WHEN 1 THEN 2 END FROM tab1
;

-- testCase
SELECT a, (CASE WHEN (a > 2) THEN 3 END) AS b FROM tab1
;

-- testCase
SELECT a, (CASE WHEN a > 2 THEN 3 ELSE 4 END) AS b FROM tab1
;

-- testCase
SELECT a, (CASE b WHEN 1 THEN 2 WHEN 3 THEN 4 ELSE 5 END) FROM tab1
;

-- testCase
SELECT a, (CASE WHEN b > 1 THEN 'BBB' WHEN a = 3 THEN 'AAA' END) FROM tab1
;

-- testCase
SELECT a, (CASE WHEN b > 1 THEN 'BBB' WHEN a = 3 THEN 'AAA' END) FROM tab1 WHERE c = (CASE WHEN d <> 3 THEN 5 ELSE 10 END)
;

-- testCase
SELECT a, CASE a WHEN 'b' THEN 'BBB' WHEN 'a' THEN 'AAA' END AS b FROM tab1
;

-- testCase
SELECT a FROM tab1 WHERE CASE b WHEN 1 THEN 2 WHEN 3 THEN 4 ELSE 5 END > 34
;

-- testCase
SELECT a FROM tab1 WHERE CASE b WHEN 1 THEN 2 + 3 ELSE 4 END > 34
;

-- testCase
SELECT a, (CASE WHEN (CASE a WHEN 1 THEN 10 ELSE 20 END) > 15 THEN 'BBB' END) FROM tab1
;

-- testCast
SELECT CAST(a AS varchar) FROM tabelle1
;

-- testCast
SELECT CAST(a AS varchar2) FROM tabelle1
;

-- testFrom
SELECT * FROM mytable AS mytable0, mytable1 alias_tab1, mytable2 AS alias_tab2, (SELECT * FROM mytable3) AS mytable4 WHERE mytable.col = 9
;

-- testJoin
SELECT * FROM tab1 LEFT OUTER JOIN tab2 ON tab1.id = tab2.id
;

-- testJoin
SELECT * FROM tab1 LEFT OUTER JOIN tab2 ON tab1.id = tab2.id INNER JOIN tab3
;

-- testJoin
SELECT * FROM tab1 LEFT OUTER JOIN tab2 ON tab1.id = tab2.id JOIN tab3
;

-- testJoin
SELECT * FROM tab1 LEFT OUTER JOIN tab2 ON tab1.id = tab2.id INNER JOIN tab3
;

-- testJoin
SELECT * FROM TA2 LEFT OUTER JOIN O USING (col1, col2) WHERE D.OasSD = 'asdf' AND (kj >= 4 OR l < 'sdf')
;

-- testJoin
SELECT * FROM tab1 INNER JOIN tab2 USING (id, id2)
;

-- testJoin
SELECT * FROM tab1 RIGHT OUTER JOIN tab2 USING (id, id2)
;

-- testJoin
SELECT * FROM foo AS f LEFT OUTER JOIN (bar AS b RIGHT OUTER JOIN baz AS z ON f.id = z.id) ON f.id = b.id
;

-- testJoin
SELECT * FROM foo AS f, OUTER bar AS b WHERE f.id = b.id
;

-- testLike
SELECT * FROM tab1 WHERE a LIKE 'test'
;

-- testLike
SELECT * FROM tab1 WHERE a LIKE 'test' ESCAPE 'test2'
;

-- testPR73
SELECT date_part('day', TIMESTAMP '2001-02-16 20:38:40')
;

-- testPR73
SELECT EXTRACT(year FROM DATE '2001-02-16')
;

-- testSkip
SELECT SKIP 5 alias.columnName1, alias.columnName2 FROM schemaName.tableName alias ORDER BY alias.columnName2 DESC
;

-- testSkip
SELECT SKIP skipVar c1, c2 FROM t
;

-- testTime
SELECT * FROM tab1 WHERE a > {t '04:05:34'}
;

-- testWith
WITH DINFO (DEPTNO, AVGSALARY, EMPCOUNT) AS (SELECT OTHERS.WORKDEPT, AVG(OTHERS.SALARY), COUNT(*) FROM EMPLOYEE AS OTHERS GROUP BY OTHERS.WORKDEPT), DINFOMAX AS (SELECT MAX(AVGSALARY) AS AVGMAX FROM DINFO) SELECT THIS_EMP.EMPNO, THIS_EMP.SALARY, DINFO.AVGSALARY, DINFO.EMPCOUNT, DINFOMAX.AVGMAX FROM EMPLOYEE AS THIS_EMP INNER JOIN DINFO INNER JOIN DINFOMAX WHERE THIS_EMP.JOB = 'SALESREP' AND THIS_EMP.WORKDEPT = DINFO.DEPTNO
;

-- testIssue215_possibleEndlessParsing
SELECT (CASE WHEN ((value LIKE '%t1%') OR (value LIKE '%t2%')) THEN 't1s' WHEN ((((((((((((((((((((((((((((value LIKE '%t3%') OR (value LIKE '%t3%')) OR (value LIKE '%t3%')) OR (value LIKE '%t4%')) OR (value LIKE '%t4%')) OR (value LIKE '%t5%')) OR (value LIKE '%t6%')) OR (value LIKE '%t6%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t8%')) OR (value LIKE '%t8%')) OR (value LIKE '%CTO%')) OR (value LIKE '%cto%')) OR (value LIKE '%Cto%')) OR (value LIKE '%t9%')) OR (value LIKE '%t9%')) OR (value LIKE '%COO%')) OR (value LIKE '%coo%')) OR (value LIKE '%Coo%')) OR (value LIKE '%t10%')) OR (value LIKE '%t10%')) OR (value LIKE '%CIO%')) OR (value LIKE '%cio%')) OR (value LIKE '%Cio%')) OR (value LIKE '%t11%')) OR (value LIKE '%t11%')) THEN 't' WHEN ((((value LIKE '%t12%') OR (value LIKE '%t12%')) OR (value LIKE '%VP%')) OR (value LIKE '%vp%')) THEN 'Vice t12s' WHEN ((((((value LIKE '% IT %') OR (value LIKE '%t13%')) OR (value LIKE '%t13%')) OR (value LIKE '% it %')) OR (value LIKE '%tech%')) OR (value LIKE '%Tech%')) THEN 'IT' WHEN ((((value LIKE '%Analyst%') OR (value LIKE '%t14%')) OR (value LIKE '%Analytic%')) OR (value LIKE '%analytic%')) THEN 'Analysts' WHEN ((value LIKE '%Manager%') OR (value LIKE '%manager%')) THEN 't15' ELSE 'Other' END) FROM tab1
;

-- testJsonExpression
SELECT data->'images'->'thumbnail'->'url' AS thumb FROM instagram
;

-- testJsonExpression
SELECT * FROM sales WHERE sale->'items'->>'description' = 'milk'
;

-- testJsonExpression
SELECT * FROM sales WHERE sale->'items'->>'quantity' = 12::TEXT
;

-- testJsonExpression
SELECT SUM(CAST(sale->'items'->>'quantity' AS integer)) AS total_quantity_sold FROM sales
;

-- testJsonExpression
SELECT sale->>'items' FROM sales
;

-- testJsonExpression
SELECT json_typeof(sale->'items'), json_typeof(sale->'items'->'quantity') FROM sales
;

-- testJsonExpression
select doc->'site_name' from websites where doc @>'{"tags":[{"term":"paris"},{"term":"food"}]}'
;

-- testJsonExpression
select*from sales where sale->'items' @>'[{"count":0}]'
;

-- testJsonExpression
select*from sales where sale->'items' ? 'name'
;

-- testJsonExpression
select*from sales where sale->'items'-# 'name'
;

-- testAnalyticPartitionBooleanExpressionIssue864_2
SELECT COUNT(*) OVER (PARTITION BY (event = 'admit' OR event = 'family visit') ) family_visits FROM patients
;

-- testWhereIssue240_true
SELECT count(*) FROM mytable WHERE true
;

-- testAnalyticPartitionBooleanExpressionIssue864
SELECT COUNT(*) OVER (PARTITION BY (event = 'admit' OR event = 'family visit') ORDER BY day ROWS BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING) family_visits FROM patients
;

-- testCollisionWithSpecialStringFunctionsIssue1284
select test(a in(1)and 2=2)
;

-- testCollisionWithSpecialStringFunctionsIssue1284
select sum(if(column1 in('value1','value2'),1,0))as tcp_logs,sum(if(column1 in('value1','value2')and column2='value3',1,0))as base_tcp_logs from table1 where recv_time>=todatetime('2021-07-20 00:00:00')and recv_time<todatetime('2021-07-21 00:00:00')
;

-- testConcatProblem2_5_1
SELECT (a || b) || c FROM testtable
;

-- testConcatProblem2_5_2
SELECT (a + b) + c FROM testtable
;

-- testH2CaseWhenFunctionIssue1091
SELECT CASEWHEN(ID = 1, 'A', 'B') FROM mytable
;

-- testProblemSqlAnalytic8Complex
SELECT ID, NAME, SALARY, SUM(SALARY) OVER () AS SUM_SAL, AVG(SALARY) OVER () AS AVG_SAL, MIN(SALARY) OVER () AS MIN_SAL, MAX(SALARY) OVER () AS MAX_SAL, COUNT(*) OVER () AS ROWS2 FROM STAFF WHERE ID < 60 ORDER BY ID
;

-- testMultiPartNamesIssue163
SELECT mymodel.name FROM com.myproject.MyModelClass AS mymodel
;

-- testMultiPartNamesIssue608
SELECT @@sessions.tx_read_only
;

-- testMultiPartNamesIssue643
SELECT id, bid, pid, devnum, pointdesc, sysid, zone, sort FROM fault ORDER BY id DESC LIMIT ?, ?
;

-- testIssue566LargeView
select "campaignid","target_source","non_indv_type","group_sponsor","segmnts","country_cd","target_state","target_city","target_zip","sic_class","naics_class","gender_cd","occupation","credit_score","marital_status","import_id","birth_dt","status" from(select x.campaignid,x.fieldname,case when y.value is null then 'all' else y.value end as value from(select campaignid,fieldname from campaign cross join(select distinct fieldname from fieldcriteria))x left join(select campaignid,fieldname,(case fieldname when u'businesstype' then d.displayvalue when u'leadtargetsource' then e.displayvalue else value end)as value from fieldcriteria a,stringfieldcriteria_value b left join(select b.code,b.displayvalue,lookupname from lookuplist a,lookupvalue b where a.id=b.lookuplist_id and lookupname='nonindividualtype')d on b.value=d.code left join(select b.code,b.displayvalue,lookupname from lookuplist a,lookupvalue b where a.id=b.lookuplist_id and lookupname='leadtargetsource')e on b.value=e.code,campaign c where a.id=b.field_criteria_id and a.criteria_id=c.id)y on x.campaignid=y.campaignid and x.fieldname=y.fieldname)pivot(max(value)for fieldname in('leadtargetsource' as target_source,'businesstype' as non_indv_type,'groupsponsor' as group_sponsor,'segments' as segmnts,'countrycd' as country_cd,'stateprovcd' as target_state,'city' as target_city,'postalcode' as target_zip,'sicclassification' as sic_class,'naicsclassification' as naics_class,'gendercd' as gender_cd,'occupation' as occupation,'creditscore' as credit_score,'maritalstatuscd' as marital_status,'importid' as import_id,'birthdate' as birth_dt,'status' as status))
;

-- testCastToSignedInteger
SELECT CAST(contact_id AS SIGNED INTEGER) FROM contact WHERE contact_id = 20
;

-- testWithUnionAllProblem
WITH test AS ((SELECT mslink FROM tablea) UNION ALL (SELECT mslink FROM tableb)) SELECT * FROM tablea WHERE mslink IN (SELECT mslink FROM test)
;

-- testOracleJoin2_1
select*from tabelle1,tabelle2 where tabelle1.a(+)=tabelle2.b
;

-- testOracleJoin2_1
select*from tabelle1,tabelle2 where tabelle1.a(+)=tabelle2.b
;

-- testOracleJoin2_1
select*from tabelle1,tabelle2 where tabelle1.a(+)=tabelle2.b
;

-- testOracleJoin2_1
select*from tabelle1,tabelle2 where tabelle1.a(+)=tabelle2.b
;

-- testOracleJoin2_1
select*from tabelle1,tabelle2 where tabelle1.a(+)=tabelle2.b
;

-- testOracleJoin2_2
select*from tabelle1,tabelle2 where tabelle1.a=tabelle2.b(+)
;

-- testOracleJoin2_2
select*from tabelle1,tabelle2 where tabelle1.a=tabelle2.b(+)
;

-- testOracleJoin2_2
select*from tabelle1,tabelle2 where tabelle1.a=tabelle2.b(+)
;

-- testOracleJoin2_2
select*from tabelle1,tabelle2 where tabelle1.a=tabelle2.b(+)
;

-- testOracleJoin2_2
select*from tabelle1,tabelle2 where tabelle1.a=tabelle2.b(+)
;

-- testOracleJoin3_1
SELECT * FROM tabelle1, tabelle2 WHERE tabelle1.a > tabelle2.b(+)
;

-- testAnyConditionSubSelect
select e1.empno,e1.sal from emp e1 where e1.sal>any(select e2.sal from emp e2 where e2.deptno=10)
;

-- testConnectByRootIssue1255
select last_name "employee",connect_by_root last_name "manager",level-1 "pathlen",sys_connect_by_path(last_name,'/')"path" from employees where level>1 and department_id=110 connect by prior employee_id=manager_id
;

-- testConnectByRootIssue1255
select name,sum(salary)"total_salary" from(select connect_by_root last_name as name,salary from employees where department_id=110 connect by prior employee_id=manager_id)group by name
;

-- testConnectByRootIssue1255
select connect_by_root last_name as name,salary from employees where department_id=110 connect by prior employee_id=manager_id
;

-- testGroupedByIssue1176
select id_instrument,count(*)from cfe.instrument group by(id_instrument)
;

-- testGroupedByIssue1176
select count(*)from cfe.instrument group by()
;

-- testCreateTableWithParameterDefaultFalseIssue1088
SELECT p.*, rhp.house_id FROM rel_house_person rhp INNER JOIN person p ON rhp.person_id = p.if WHERE rhp.house_id IN (SELECT house_id FROM rel_house_person WHERE person_id = :personId AND current_occupant = :current) AND rhp.current_occupant = :currentOccupant
;

-- testProblemLargeNumbersIssue390
SELECT * FROM student WHERE student_no = 20161114000000035001
;

-- testUniqueInsteadOfDistinctIssue299
select unique trunc(timez(ludate)+8/24)bus_dt,j.object j_name,timez(j.starttime)start_time,timez(j.endtime)end_time from test_1 j
;

-- testCaseKeyword
SELECT * FROM Case
;

-- testNotProblemIssue721
SELECT * FROM dual WHERE NOT regexp_like('a', '[\w]+')
;

-- testFuncConditionParameter2
SELECT if(a < b, c)
;

-- testFuncConditionParameter3
select cast((max(cast(iif(isnumeric(license_no)=1,license_no,0)as int))+2)as varchar)from lcps.t_license where profession_id=60 and license_type=100 and year(issue_date)% 2=case when year(issue_date)% 2=0 then 0 else 1 end and isnumeric(license_no)=1
;

-- testFuncConditionParameter4
select iif(isnumeric(license_no)=1,license_no,0)from mytable
;

-- testJoinWithTrailingOnExpressionIssue1302
select*from table1 tb1 inner join table2 tb2 inner join table3 tb3 inner join table4 tb4 on(tb3.aaa=tb4.aaa)on(tb2.aaa=tb3.aaa)on(tb1.aaa=tb2.aaa)
;

-- testJoinWithTrailingOnExpressionIssue1302
select*from table1 tbl1 inner join table2 tbl2 inner join table3 tbl3 on(tbl2.column1=tbl3.column1)on(tbl1.column2=tbl2.column2)where tbl1.column1=123
;

-- testIssue215_possibleEndlessParsing2
SELECT (CASE WHEN ((value LIKE '%t1%') OR (value LIKE '%t2%')) THEN 't1s' ELSE 'Other' END) FROM tab1
;

-- testIssue215_possibleEndlessParsing3
SELECT * FROM mytable WHERE ((((((((((((((((((((((((((((value LIKE '%t3%') OR (value LIKE '%t3%')) OR (value LIKE '%t3%')) OR (value LIKE '%t4%')) OR (value LIKE '%t4%')) OR (value LIKE '%t5%')) OR (value LIKE '%t6%')) OR (value LIKE '%t6%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t8%')) OR (value LIKE '%t8%')) OR (value LIKE '%CTO%')) OR (value LIKE '%cto%')) OR (value LIKE '%Cto%')) OR (value LIKE '%t9%')) OR (value LIKE '%t9%')) OR (value LIKE '%COO%')) OR (value LIKE '%coo%')) OR (value LIKE '%Coo%')) OR (value LIKE '%t10%')) OR (value LIKE '%t10%')) OR (value LIKE '%CIO%')) OR (value LIKE '%cio%')) OR (value LIKE '%Cio%')) OR (value LIKE '%t11%')) OR (value LIKE '%t11%'))
;

-- testIssue215_possibleEndlessParsing4
SELECT * FROM mytable WHERE ((value LIKE '%t3%') OR (value LIKE '%t3%'))
;

-- testIssue215_possibleEndlessParsing5
SELECT * FROM mytable WHERE ((((((value LIKE '%t3%') OR (value LIKE '%t3%')) OR (value LIKE '%t3%')) OR (value LIKE '%t4%')) OR (value LIKE '%t4%')) OR (value LIKE '%t5%'))
;

-- testIssue215_possibleEndlessParsing6
SELECT * FROM mytable WHERE (((((((((((((value LIKE '%t3%') OR (value LIKE '%t3%')) OR (value LIKE '%t3%')) OR (value LIKE '%t4%')) OR (value LIKE '%t4%')) OR (value LIKE '%t5%')) OR (value LIKE '%t6%')) OR (value LIKE '%t6%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t8%')) OR (value LIKE '%t8%'))
;

-- testIssue215_possibleEndlessParsing7
SELECT * FROM mytable WHERE (((((((((((((((((((((value LIKE '%t3%') OR (value LIKE '%t3%')) OR (value LIKE '%t3%')) OR (value LIKE '%t4%')) OR (value LIKE '%t4%')) OR (value LIKE '%t5%')) OR (value LIKE '%t6%')) OR (value LIKE '%t6%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t8%')) OR (value LIKE '%t8%')) OR (value LIKE '%CTO%')) OR (value LIKE '%cto%')) OR (value LIKE '%Cto%')) OR (value LIKE '%t9%')) OR (value LIKE '%t9%')) OR (value LIKE '%COO%')) OR (value LIKE '%coo%')) OR (value LIKE '%Coo%'))
;

-- testPivotXml1
SELECT * FROM mytable PIVOT XML (count(a) FOR b IN ('val1'))
;

-- testPivotXml2
SELECT * FROM mytable PIVOT XML (count(a) FOR b IN (SELECT vals FROM myothertable))
;

-- testPivotXml3
SELECT * FROM mytable PIVOT XML (count(a) FOR b IN (ANY))
;

-- testExistsKeywordIssue1076_1
SELECT mycol, EXISTS (SELECT mycol FROM mytable) mycol2 FROM mytable
;

-- testTableSpecificAllColumnsIssue1346
select count(*)from a
;

-- testTableSpecificAllColumnsIssue1346
select count(a.*)from a
;

-- testProblemSqlFuncParamIssue605_2
SELECT func(SELECT col1 FROM mytable)
;

-- testIssue162_doubleUserVar
SELECT @@SPID AS ID, SYSTEM_USER AS "Login Name", USER AS "User Name"
;

-- testKeywordDuplicate2
SELECT * FROM mytable WHERE duplicate = 5
;

-- testIssue563MultiSubJoin_2
SELECT c FROM ((SELECT a FROM t))
;

-- testAnyComparisionExpressionValuesList1232
select*from foo where id!=all(values 1,2,3)
;

-- testAnyComparisionExpressionValuesList1232
select*from foo where id!=all(?::uid[])
;

-- testProblemSqlIssue330_2
SELECT CAST('90 days' AS interval)
;

-- testSelectWithBrackets2
(SELECT 1)
;

-- testOrderByNullsFirst
SELECT a FROM tab1 ORDER BY a NULLS FIRST
;

-- testProblemSqlIssue603_2
SELECT CAST(col1 AS UNSIGNED INTEGER) FROM mytable
;

-- testMultiTableJoin
SELECT * FROM taba INNER JOIN tabb ON taba.a = tabb.a, tabc LEFT JOIN tabd ON tabc.c = tabd.c
;

-- testWithIsolation
SELECT * FROM mytable WHERE mytable.col = 9 WITH ur
;

-- testWithIsolation
SELECT * FROM mytable WHERE mytable.col = 9 WITH Cs
;

-- testProblemSqlServer_Modulo_mod
SELECT mod(5, 2) FROM A
;

-- testWindowClauseWithoutOrderByIssue869
SELECT subject_id, student_id, mark, sum(mark) OVER (PARTITION BY (subject_id) ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) FROM marks
;

-- testMultiPartTypesIssue992
SELECT CAST('*' AS pg_catalog.text)
;

-- testNamedParametersPR702
SELECT substring(id, 2, 3), substring(id from 2 for 3), substring(id from 2), trim(BOTH ' ' from 'foo bar '), trim(LEADING ' ' from 'foo bar '), trim(TRAILING ' ' from 'foo bar '), trim(' ' from 'foo bar '), position('foo' in 'bar'), overlay('foo' placing 'bar' from 1), overlay('foo' placing 'bar' from 1 for 2) FROM my table
;

-- testWithAsRecursiveIssue874
WITH rn AS (SELECT rownum rn FROM dual CONNECT BY level <= (SELECT max(cases) FROM t1)) SELECT pname FROM t1, rn WHERE rn <= cases ORDER BY pname
;

-- testProblemSqlAnalytic
SELECT a, row_number() OVER (ORDER BY a) AS n FROM table1
;

-- testUnionWithOrderByAndLimitAndNoBrackets
SELECT id FROM table1 UNION SELECT id FROM table2 ORDER BY id ASC LIMIT 55
;

-- testIssue160_signedParameter2
SELECT * FROM mytable WHERE -? = 5
;

-- testPivotWithAlias
SELECT * FROM (SELECT * FROM mytable LEFT JOIN mytable2 ON Factor_ID = Id) f PIVOT (max(f.value) FOR f.factoryCode IN (ZD, COD, SW, PH))
;

-- testNestedCaseComplexExpressionIssue1306
select case when 'usd'='usd' then 0 else case when 'usd'='euro' then(case when 'a'='b' then 0 else 1 end*100)else 2 end end as "column1" from test_schema.table_name
;

-- testNestedCaseComplexExpressionIssue1306
select case when 'usd'='usd' then 0 else case when 'usd'='euro' then case when 'a'='b' then 0 else 1 end*100 else 2 end end as "column1" from test_schema.table_name
;

-- testAdditionalLettersSpanish
SELECT * FROM años
;

-- testJsonExpressionWithIntegerParameterIssue909
select uc."id",u.nickname,u.avatar,b.title,uc.images,uc.created_at as createdat from library.ugc_comment uc inner join library.book b on(uc.books_id->>0)::integer=b."id" inner join library.users u on uc.user_id=u.user_id where uc.id=1
;

-- testTimestamptzDateTimeLiteral
SELECT * FROM table WHERE x >= TIMESTAMPTZ '2021-07-05 00:00:00+00'
;

-- testSelectStatementWithForUpdateAndSkipLockedTokens
SELECT * FROM test FOR UPDATE SKIP LOCKED
;

-- testLateral1
SELECT O.ORDERID, O.CUSTNAME, OL.LINETOTAL FROM ORDERS AS O, LATERAL(SELECT SUM(NETAMT) AS LINETOTAL FROM ORDERLINES AS LINES WHERE LINES.ORDERID = O.ORDERID) AS OL
;

-- testSpeedTestIssue235_2
select 'cr' as `^cr`,(1-(sum((case when(`tbl`.`as`='cancelled')then(case when(round((((((period_diff(date_format(`tbl`.`cd`,'%y%m'),date_format(subtime(current_timestamp(),25200),'%y%m'))+month(subtime(current_timestamp(),25200)))-month('2012-02-01'))-1)/3)-round((((month(subtime(current_timestamp(),25200))-month('2012-02-01'))-1)/3))))=-3)then 1 else 0 end)else 0 end))/sum((case when(`tbl`.`as`='active')then(case when(round((((((period_diff(date_format(`tbl`.`ocd`,'%y%m'),date_format(subtime(current_timestamp(),25200),'%y%m'))+month(subtime(current_timestamp(),25200)))-month('2012-02-01'))-1)/3)-round((((month(subtime(current_timestamp(),25200))-month('2012-02-01'))-1)/3))))<=-3)then 1 else 0 end)else 0 end))))as `^p3q trr`,(1-(sum((case when(`tbl`.`as`='cancelled')then(case when(round((((((period_diff(date_format(`tbl`.`cd`,'%y%m'),date_format(subtime(current_timestamp(),25200),'%y%m'))+month(subtime(current_timestamp(),25200)))-month('2012-02-01'))-1)/3)-round((((month(subtime(current_timestamp(),25200))-month('2012-02-01'))-1)/3))))=-2)then 1 else 0 end)else 0 end))/sum((case when(`tbl`.`as`='active')then(case when(round((((((period_diff(date_format(`tbl`.`ocd`,'%y%m'),date_format(subtime(current_timestamp(),25200),'%y%m'))+month(subtime(current_timestamp(),25200)))-month('2012-02-01'))-1)/3)-round((((month(subtime(current_timestamp(),25200))-month('2012-02-01'))-1)/3))))<=-2)then 1 else 0 end)else 0 end))))as `^p2q trr`,(1-(sum((case when(`tbl`.`as`='cancelled')then(case when(round((((((period_diff(date_format(`tbl`.`cd`,'%y%m'),date_format(subtime(current_timestamp(),25200),'%y%m'))+month(subtime(current_timestamp(),25200)))-month('2012-02-01'))-1)/3)-round((((month(subtime(current_timestamp(),25200))-month('2012-02-01'))-1)/3))))=-1)then 1 else 0 end)else 0 end))/sum((case when(`tbl`.`as`='active')then(case when(round((((((period_diff(date_format(`tbl`.`ocd`,'%y%m'),date_format(subtime(current_timestamp(),25200),'%y%m'))+month(subtime(current_timestamp(),25200)))-month('2012-02-01'))-1)/3)-round((((month(subtime(current_timestamp(),25200))-month('2012-02-01'))-1)/3))))<=-1)then 1 else 0 end)else 0 end))))as `^pq trr`,(1-(sum((case when((round((((((period_diff(date_format(`tbl`.`cd`,'%y%m'),date_format(subtime(current_timestamp(),25200),'%y%m'))+month(subtime(current_timestamp(),25200)))-month('2012-02-01'))-1)/3)-round((((month(subtime(current_timestamp(),25200))-month('2012-02-01'))-1)/3))))=0)and(`tbl`.`as`='cancelled'))then 1 else 0 end))/sum((case when(`tbl`.`as`='active')then 1 else 0 end))))as `^cq trr` from `tbl` group by 'cr' limit 25000
;

-- testUnPivot
SELECT * FROM sale_stats UNPIVOT (quantity FOR product_code IN (product_a AS 'A', product_b AS 'B', product_c AS 'C'))
;

-- testTimezoneExpressionWithColumnBasedTimezone
SELECT 1 FROM tbl WHERE col AT TIME ZONE timezone_col < '2021-11-05 00:00:35'::date + INTERVAL '1 day' * 0
;

-- testPostgresDollarQuotes_1372
SELECT UPPER($$some text$$) FROM a
;

-- testPostgresDollarQuotes_1372
SELECT * FROM a WHERE a.test = $$where text$$
;

-- testPostgresDollarQuotes_1372
SELECT * FROM a WHERE a.test = $$$$
;

-- testPostgresDollarQuotes_1372
SELECT * FROM a WHERE a.test = $$ $$
;

-- testPostgresDollarQuotes_1372
SELECT aa AS $$My Column Name$$ FROM a
;

-- testIssue223_singleQuoteEscape
select '\'test\''
;

-- testSelectNumericBind
SELECT a FROM b WHERE c = :1
;

-- testRegexpBinaryMySQL
SELECT * FROM mytable WHERE first_name REGEXP BINARY '^Ste(v|ph)en$'
;

-- testOracleHierarchicalQueryIssue196
SELECT num1, num2, level FROM carol_tmp START WITH num2 = 1008 CONNECT BY num2 = PRIOR num1 ORDER BY level DESC
;

-- testWhereIssue240_false
SELECT count(*) FROM mytable WHERE false
;

-- testMultiValueIn
SELECT * FROM mytable WHERE (a, b, c) IN (SELECT a, b, c FROM mytable2)
;

-- testProblemIssue375Simplified2
select*from(pg_catalog.pg_class c inner join pg_catalog.pg_namespace n on n.oid=c.relnamespace and c.relname='business' and n.nspname='public')inner join pg_catalog.pg_attribute a on(not a.attisdropped)and a.attnum>0 and a.attrelid=c.oid
;

-- testPreserveAndOperator
SELECT * FROM mytable WHERE 1 = 2 && 2 = 3
;

-- testProblemSqlIntersect
(SELECT * FROM a) INTERSECT (SELECT * FROM b)
;

-- testProblemSqlIntersect
SELECT * FROM a INTERSECT SELECT * FROM b
;

-- testProblemFunction
SELECT test() FROM testtable
;

-- testIssue508LeftRightBitwiseShift
SELECT 1 << 1
;

-- testIssue508LeftRightBitwiseShift
SELECT 1 >> 1
;

-- testBooleanValue2
SELECT col FROM t WHERE 3 < 5 AND a
;

-- testConcatProblem2
SELECT MAX((((((SPA.SOORTAANLEVERPERIODE)::VARCHAR (2) || (VARCHAR(SPA.AANLEVERPERIODEJAAR))::VARCHAR (4)) || TO_CHAR(SPA.AANLEVERPERIODEVOLGNR, 'FM09'::VARCHAR)) || TO_CHAR((10000 - SPA.VERSCHIJNINGSVOLGNR), 'FM0999'::VARCHAR)) || (SPA.GESLACHT)::VARCHAR (1))) AS GESLACHT_TMP FROM testtable
;

-- testBitwise
SELECT col1 & 32, col2 ^ col1, col1 | col2 FROM table1
;

-- testKeyWordExceptIssue1055_2
SELECT * FROM mytable WHERE A.end_time > now() AND A.end_time <= date_add(now(), INTERVAL ? DAY)
;

-- testSelectItems
SELECT myid AS MYID, mycol, tab.*, schema.tab.*, mytab.mycol2, myschema.mytab.mycol, myschema.mytab.* FROM mytable WHERE mytable.col = 9
;

-- testSelectItems
SELECT myid AS MYID, (SELECT MAX(ID) AS myid2 FROM mytable2) AS myalias FROM mytable WHERE mytable.col = 9
;

-- testSelectItems
SELECT (myid + myid2) AS MYID FROM mytable WHERE mytable.col = 9
;

-- testSelectJoin2
SELECT * FROM pg_constraint WHERE pg_attribute.attnum = ANY(pg_constraint.conkey)
;

-- testSelectJoin2
SELECT * FROM pg_constraint WHERE pg_attribute.attnum = ALL(pg_constraint.conkey)
;

-- testSelectTuple
SELECT hyperloglog_distinct((1, 2)) FROM t
;

-- testTopWithTies
SELECT TOP (5) PERCENT WITH TIES columnName1, columnName2 FROM tableName
;

-- testTableFunctionInExprIssue923_3
with ap_claims as(select clm.internal_id as claim_id,ap.claim_id as clm,adjst_clm_id,tot_billed_amt,tot_net_payable,entry_date,orig_rev_clm_id,orig_adjst_clm_id,epm.payor_name,ckr.check_number,ckr.check_date,coalesce(clm_eob.clm_cd_1,'')||case when clm_eob.clm_cd_2 is null or clm_eob.clm_cd_2='' then '' else ',' end||coalesce(clm_eob.clm_cd_2,'')||case when clm_eob.clm_cd_3 is null or clm_eob.clm_cd_3='' then '' else ',' end||coalesce(clm_eob.clm_cd_3,'')||case when clm_eob.clm_cd_4 is null or clm_eob.clm_cd_4='' then '' else ',' end||coalesce(clm_eob.clm_cd_4,'')||case when clm_eob.clm_cd_5 is null or clm_eob.clm_cd_5='' then '' else ',' end||coalesce(clm_eob.clm_cd_5,'')||case when clm_eob.clm_cd_6 is null or clm_eob.clm_cd_6='' then '' else ',' end||coalesce(clm_eob.clm_cd_6,'')||case when clm_eob.clm_cd_7 is null or clm_eob.clm_cd_7='' then '' else ',' end||coalesce(clm_eob.clm_cd_7,'')||case when clm_eob.clm_cd_8 is null or clm_eob.clm_cd_8='' then '' else ',' end||coalesce(clm_eob.clm_cd_8,'')||case when clm_eob.clm_cd_9 is null or clm_eob.clm_cd_9='' then '' else ',' end||coalesce(clm_eob.clm_cd_9,'')||case when clm_eob.clm_cd_10 is null or clm_eob.clm_cd_10='' then '' else ',' end||coalesce(clm_eob.clm_cd_10,'')as clm_eob,min_eob_date,case ap.status_c when 1 then 'new' when 2 then 'pending' when 3 then 'denied' when 4 then 'clean' when 5 then 'void' else ' ' end as claim_status,ckr.ap_run_date,mailed_date as chk_mail_date,ap2.penalty_amt,ap2.interest_total from ap_claim ap inner join clm_map clm on ap.claim_id=clm.cid left outer join ap_claim_check dofr on ap.claim_id=dofr.claim_id left outer join ap_check ckr on dofr.check_id=ckr.check_id left outer join anthem_epm epm on epm.payor_id=ap.payor_id left join ap_claim_2 ap2 on ap.claim_id=ap2.claim_id left outer join(select ceob.claim_id,'y' as clm_lvl_pnd,min(ceob.entry_date)as min_eob_date,max(case when ceob.line=1 then eob.mnemonic else null end)as clm_cd_1,max(case when ceob.line=2 then eob.mnemonic else null end)as clm_cd_2,max(case when ceob.line=3 then eob.mnemonic else null end)as clm_cd_3,max(case when ceob.line=4 then eob.mnemonic else null end)as clm_cd_4,max(case when ceob.line=5 then eob.mnemonic else null end)as clm_cd_5,max(case when ceob.line=6 then eob.mnemonic else null end)as clm_cd_6,max(case when ceob.line=7 then eob.mnemonic else null end)as clm_cd_7,max(case when ceob.line=8 then eob.mnemonic else null end)as clm_cd_8,max(case when ceob.line=9 then eob.mnemonic else null end)as clm_cd_9,max(case when ceob.line=10 then eob.mnemonic else null end)as clm_cd_10 from ap_claim_eob_cd ceob left outer join anthem_eob_code eob on ceob.eob_code_id=eob.eob_code_id where eob.code_type_c=2 group by ceob.claim_id)clm_eob on clm_eob.claim_id=ap.claim_id where nvl(dofr.service_count,0)>0 group by clm.internal_id,ap.claim_id,adjst_clm_id,tot_billed_amt,tot_net_payable,entry_date,orig_rev_clm_id,orig_adjst_clm_id,epm.payor_name,ckr.check_number,ckr.check_date,coalesce(clm_eob.clm_cd_1,'')||case when clm_eob.clm_cd_2 is null or clm_eob.clm_cd_2='' then '' else ',' end||coalesce(clm_eob.clm_cd_2,'')||case when clm_eob.clm_cd_3 is null or clm_eob.clm_cd_3='' then '' else ',' end||coalesce(clm_eob.clm_cd_3,'')||case when clm_eob.clm_cd_4 is null or clm_eob.clm_cd_4='' then '' else ',' end||coalesce(clm_eob.clm_cd_4,'')||case when clm_eob.clm_cd_5 is null or clm_eob.clm_cd_5='' then '' else ',' end||coalesce(clm_eob.clm_cd_5,'')||case when clm_eob.clm_cd_6 is null or clm_eob.clm_cd_6='' then '' else ',' end||coalesce(clm_eob.clm_cd_6,'')||case when clm_eob.clm_cd_7 is null or clm_eob.clm_cd_7='' then '' else ',' end||coalesce(clm_eob.clm_cd_7,'')||case when clm_eob.clm_cd_8 is null or clm_eob.clm_cd_8='' then '' else ',' end||coalesce(clm_eob.clm_cd_8,'')||case when clm_eob.clm_cd_9 is null or clm_eob.clm_cd_9='' then '' else ',' end||coalesce(clm_eob.clm_cd_9,'')||case when clm_eob.clm_cd_10 is null or clm_eob.clm_cd_10='' then '' else ',' end||coalesce(clm_eob.clm_cd_10,''),min_eob_date,case ap.status_c when 1 then 'new' when 2 then 'pending' when 3 then 'denied' when 4 then 'clean' when 5 then 'void' else ' ' end,ckr.ap_run_date,mailed_date,ap2.penalty_amt,ap2.interest_total),duprow as(select clm.internal_id as orig_internal_id,ap.claim_id as orig_claim_id,ap.date_received as orig_date_received,ap2.interest_total,ap2.penalty_amt,ref1.external_id_num as auth_num,lob.lob_name,ap.serv_date,ap.tot_billed_amt,ap.tot_net_payable,oaj.clm_eob as true_orig_eob,adj.clm_eob as orig_eob,adj2.clm_eob as aj1_eob,adj3.clm_eob as aj2_eob,adj4.clm_eob as aj3_eob,adj5.clm_eob as aj4_eob,adj6.clm_eob as aj5_eob,oaj.min_eob_date as true_orig_eob_date,adj.min_eob_date as aj1_min_eob_date,adj2.min_eob_date as aj2_min_eob_date,adj3.min_eob_date as aj3_min_eob_date,adj4.min_eob_date as aj4_min_eob_date,adj5.min_eob_date as aj5_min_eob_date,case ap.status_c when 1 then 'new' when 2 then 'pending' when 3 then 'denied' when 4 then 'clean' when 5 then 'void' else ' ' end as status_c,ven.vendor_name,coalesce(adj5.claim_status,adj4.claim_status,adj3.claim_status,adj2.claim_status,adj.claim_status,(case ap.status_c when 1 then 'new' when 2 then 'pending' when 3 then 'denied' when 4 then 'clean' when 5 then 'void' else ' ' end))as kinal_claim_status,coalesce(adj5.tot_net_payable,adj4.tot_net_payable,adj3.tot_net_payable,adj2.tot_net_payable,adj.tot_net_payable,ap.tot_net_payable)as kinal_tot_net_paybale,coalesce(adj5.penalty_amt,adj4.penalty_amt,adj3.penalty_amt,adj2.penalty_amt,adj.penalty_amt,ap2.penalty_amt)as kinal_penalty_amt,coalesce(adj5.interest_total,adj4.interest_total,adj3.interest_total,adj2.interest_total,adj.interest_total,ap2.interest_total)as kinal_interest_amt,dup.internal_id as dupe_internal_id,dup.claim_id as dupe_claim_id,crm,crm_date,crm_topic,crm_subtopic,row_number()over(partition by clm.internal_id order by dup.internal_id)as row_num from ap_claim ap inner join clm_map clm on ap.claim_id=clm.cid and ap.orig_adjst_clm_id is null and ap.orig_rev_clm_id is null inner join patient pat on pat.pat_id=ap.pat_id inner join ap_claim_2 ap2 on ap2.claim_id=ap.claim_id left outer join anthem_vendor ven on ven.vendor_id=ap.vendor_id left outer join claim_referral_lk ref on ref.claim_id=ap.claim_id and ref.line=1 left outer join referral ref1 on ref1.referral_id=ref.referral_id left outer join anthem_lob lob on lob.lob_id=ap.clm_lob_id left outer join(select a.subj_claim_id,max(case when rownumb=1 then crm end)as crm,max(case when rownumb=1 then entry_date else null end)as crm_date,max(case when rownumb=1 then name else null end)as crm_topic,max(case when rownumb=1 then subname else null end)as crm_subtopic from(select css.subj_claim_id,css.entry_date,topic.name,sub.name as subname,nmap.internal_id as crm,row_number()over(partition by css.subj_claim_id order by record_entry_time)as rownumb from cust_service css left outer join zc_ncs_topic topic on topic.topic_c=css.topic_c left outer join zc_sub_topic sub on sub.sub_topic_c=css.sub_topic_c left outer join ncs_map nmap on css.comm_id=nmap.cid where sub.name like('%itemized%'))a group by a.subj_claim_id)crm_num on crm_num.subj_claim_id=ap.claim_id left join ap_claims adj on ap.claim_id=adj.orig_adjst_clm_id left join ap_claims adj2 on adj.clm=adj2.orig_adjst_clm_id left join ap_claims adj3 on adj2.clm=adj3.orig_adjst_clm_id left join ap_claims adj4 on adj3.clm=adj4.orig_adjst_clm_id left join ap_claims adj5 on adj4.clm=adj5.orig_adjst_clm_id left join ap_claims adj6 on adj5.clm=adj6.orig_adjst_clm_id left outer join ap_claims oaj on oaj.clm=ap.claim_id inner join(select ap.claim_id from ap_claim ap left outer join ap_claim_eob_code clmeob on clmeob.claim_id=ap.claim_id left outer join anthem_eob_code code on code.eob_code_id=clmeob.eob_code_id where clmeob.eob_code_id in(124306)and code.code_type_c=2 group by ap.claim_id)eob1 on eob1.claim_id=ap.claim_id left outer join(select claim_id,serv_date,tot_billed_amt,vendor_id,tot_net_payable,clm.internal_id,pat_id from ap_claim ap2 inner join clm_map clm on clm.cid=ap2.claim_id where ap2.orig_adjst_clm_id is null and ap2.orig_rev_clm_id is null and ap2.status_c<>5)dup on dup.serv_date<=ap.serv_date+3 and dup.serv_date>=ap.serv_date-3 and ap.tot_billed_amt<=dup.tot_billed_amt+500 and ap.tot_billed_amt>=dup.tot_billed_amt-500 and ap.vendor_id=dup.vendor_id and dup.pat_id=ap.pat_id and dup.claim_id<>ap.claim_id where ap.status_c in(1,2,3)and ap.tot_net_payable=0 and ap.tot_billed_amt>=10000.00 and ap.orig_rev_clm_id is null and ap.orig_adjst_clm_id is null),code1 as(select a.orig_internal_id,a.orig_claim_id,a.serv_date as orig_serv_date,a.orig_date_received,a.auth_num,a.lob_name,a.tot_billed_amt as orig_billed_amt,a.interest_total,a.penalty_amt,a.tot_net_payable as orig_tot_net_payable,a.vendor_name as orig_vendor,a.status_c as orig_status,a.kinal_claim_status as orig_kinal_claim_status,a.kinal_tot_net_paybale as orig_kinal_net_payable,a.kinal_penalty_amt as orig_kinal_penalty_amt,a.kinal_interest_amt as orig_kinal_interest_amt,a.true_orig_eob as orig_eob,a.orig_eob as adj1_eob,a.aj1_eob as adj2_eob,a.aj2_eob as adj3_eob,a.aj3_eob as adj4_eob,a.aj4_eob as adj5_eob,a.true_orig_eob_date,a.aj1_min_eob_date,a.aj2_min_eob_date,a.aj3_min_eob_date,a.aj4_min_eob_date,a.aj5_min_eob_date,coalesce(a.aj5_min_eob_date,a.aj4_min_eob_date,a.aj3_min_eob_date,a.aj2_min_eob_date,a.aj1_min_eob_date,a.true_orig_eob_date)as original_kinal_eob_date,crm,crm_date,crm_topic,crm_subtopic,max(case when a.row_num=1 then a.dupe_internal_id end)as dup_clm_1,max(case when a.row_num=1 then a.dupe_claim_id end)as dup_clm_id_1,max(case when a.row_num=2 then a.dupe_internal_id end)as dup_clm_2,max(case when a.row_num=2 then a.dupe_claim_id end)as dup_clm_id_2,max(case when a.row_num=3 then a.dupe_internal_id end)as dup_clm_3,max(case when a.row_num=3 then a.dupe_claim_id end)as dup_clm_id_3,max(case when a.row_num=4 then a.dupe_internal_id end)as dup_clm_4,max(case when a.row_num=4 then a.dupe_claim_id end)as dup_clm_id_4,max(case when a.row_num=5 then a.dupe_internal_id end)as dup_clm_5,max(case when a.row_num=5 then a.dupe_claim_id end)as dup_clm_id_5 from duprow a group by a.orig_internal_id,a.orig_claim_id,a.serv_date,a.orig_date_received,a.auth_num,a.lob_name,a.tot_billed_amt,a.interest_total,a.penalty_amt,a.tot_net_payable,a.vendor_name,a.status_c,a.kinal_claim_status,a.kinal_tot_net_paybale,a.kinal_penalty_amt,a.kinal_interest_amt,a.true_orig_eob,a.orig_eob,a.aj1_eob,a.aj2_eob,a.aj3_eob,a.aj4_eob,a.true_orig_eob_date,a.aj1_min_eob_date,a.aj2_min_eob_date,a.aj3_min_eob_date,a.aj4_min_eob_date,a.aj5_min_eob_date,coalesce(a.aj5_min_eob_date,a.aj4_min_eob_date,a.aj3_min_eob_date,a.aj2_min_eob_date,a.aj1_min_eob_date,a.true_orig_eob_date),crm,crm_date,crm_topic,crm_subtopic)select orig_internal_id as claim_id,orig_serv_date,orig_vendor,payor_name as orig_payor,orig_billed_amt,orig_status,orig_eob,orig_eob_date,orig_kinal_net_payable,orig_kinal_claim_status,trim(replace(orig_kinal_eob,',',''))as orig_kinal_eob,orig_kinal_eob_date,dup_clm_1,dupe_1_kinal_tot_net_paybale,dupe_1_kinal_claim_status,case when dupe_1_kinal_claim_status='clean' then null else trim(replace(dup_1_kinal_eob,',',''))end as dup_1_kinal_eob,dup_1_kinal_eob_date,dup_1_kinal_interest,dup_1_kinal_penalty,dup_clm_2,dupe_2_kinal_tot_net_paybale,dupe_2_kinal_claim_status,case when dupe_2_kinal_claim_status='clean' then null else trim(replace(dup_2_kinal_eob,',',''))end as dup_2_kinal_eob,dup_2_kinal_eob_date,dup_2_kinal_interest,dup_2_kinal_penalty,dup_clm_3,dupe_3_kinal_tot_net_paybale,dupe_3_kinal_claim_status,case when dupe_3_kinal_claim_status='clean' then null else trim(replace(dup_3_kinal_eob,',',''))end as dup_3_kinal_eob,dup_3_kinal_eob_date,dup_3_kinal_interest,dup_3_kinal_penalty,dup_clm_4,dupe_4_kinal_tot_net_paybale,dupe_4_kinal_claim_status,case when dupe_4_kinal_claim_status='clean' then null else trim(replace(dup_4_kinal_eob,',',''))end as dup_4_kinal_eob,dup_4_kinal_eob_date,dup_4_kinal_interest,dup_4_kinal_penalty,all_kinal_net_payable,all_kinal_net_penalty,all_kinal_net_interest,all_kinal_status,case when all_kinal_status='clean' then null else trim(replace(all_kinal_eob,',',''))end as all_kinal_eob,all_kinal_eob_date,crm,crm_date,crm_topic,crm_subtopic,entry_date as clh66_entry_date,resolution_datetime as clh66_resolved_datetime,info_code1,info_code1_entry_date,info_code2,info_code2_entry_date,info_code3,info_code3_entry_date from(select ap.orig_internal_id,ap.orig_claim_id,ap.orig_serv_date,ap.orig_date_received,ap.interest_total,ap.penalty_amt,ap.auth_num,ap.lob_name,ap.orig_vendor,ap.orig_billed_amt,ap.orig_tot_net_payable,ap.orig_status,ap.orig_eob,ap.true_orig_eob_date as orig_eob_date,ap.orig_kinal_net_payable,ap.orig_kinal_claim_status,ap_claims.payor_name,coalesce(adj5_eob,adj4_eob,adj3_eob,adj2_eob,adj1_eob,orig_eob)as orig_kinal_eob,coalesce(aj5_min_eob_date,aj4_min_eob_date,aj3_min_eob_date,aj2_min_eob_date,aj1_min_eob_date,true_orig_eob_date)as orig_kinal_eob_date,(coalesce(test2.dupe_1_kinal_tot_net_paybale,0)+coalesce(test2.dupe_2_kinal_tot_net_paybale,0)+coalesce(test2.dupe_3_kinal_tot_net_paybale,0)+coalesce(test2.dupe_4_kinal_tot_net_paybale,0)+coalesce(test2.dupe_5_kinal_tot_net_paybale,0))as all_dupes_tot_net_payable,case when((coalesce(test2.dupe_1_kinal_tot_net_paybale,0)+coalesce(test2.dupe_2_kinal_tot_net_paybale,0)+coalesce(test2.dupe_3_kinal_tot_net_paybale,0)+coalesce(test2.dupe_4_kinal_tot_net_paybale,0)+coalesce(test2.dupe_5_kinal_tot_net_paybale,0))>0)or ap.orig_kinal_net_payable>0 then 'y' else 'n' end as is_claim_satisfied,ap.dup_clm_1,test2.dupe_1_kinal_tot_net_paybale,test2.dupe_1_kinal_claim_status as dupe_1_kinal_claim_status,replace(test2.dup_1_kinal_eob,'',null)as dup_1_kinal_eob,test2.dup_1_kinal_eob_date,test2.dup_1_kinal_interest,test2.dup_1_kinal_penalty,ap.dup_clm_2,test2.dupe_2_kinal_tot_net_paybale,test2.dupe_2_kinal_claim_status as dupe_2_kinal_claim_status,replace(test2.dup_2_kinal_eob,'',null)as dup_2_kinal_eob,test2.dup_2_kinal_eob_date,test2.dup_2_kinal_interest,test2.dup_2_kinal_penalty,ap.dup_clm_3,test2.dupe_3_kinal_tot_net_paybale,test2.dupe_3_kinal_claim_status as dupe_3_kinal_claim_status,replace(test2.dup_3_kinal_eob,'',null)as dup_3_kinal_eob,test2.dup_3_kinal_eob_date,test2.dup_3_kinal_interest,test2.dup_3_kinal_penalty,ap.dup_clm_4,test2.dupe_4_kinal_tot_net_paybale,test2.dupe_4_kinal_claim_status as dupe_4_kinal_claim_status,replace(test2.dup_4_kinal_eob,'',null)as dup_4_kinal_eob,test2.dup_4_kinal_eob_date,test2.dup_4_kinal_interest,test2.dup_4_kinal_penalty,coalesce(test2.dupe_4_kinal_tot_net_paybale,test2.dupe_3_kinal_tot_net_paybale,test2.dupe_2_kinal_tot_net_paybale,test2.dupe_1_kinal_tot_net_paybale,ap.orig_kinal_net_payable)all_kinal_net_payable,coalesce(test2.dupe_4_kinal_claim_status,test2.dupe_3_kinal_claim_status,test2.dupe_2_kinal_claim_status,test2.dupe_1_kinal_claim_status,ap.orig_kinal_claim_status)as all_kinal_status,coalesce(replace(test2.dup_4_kinal_eob,'',null),replace(test2.dup_3_kinal_eob,'',null),replace(test2.dup_2_kinal_eob,'',null),replace(test2.dup_1_kinal_eob,'',null),ap.adj5_eob,ap.adj4_eob,ap.adj3_eob,ap.adj2_eob,ap.adj1_eob,ap.orig_eob)as all_kinal_eob,coalesce(test2.dup_4_kinal_eob_date,test2.dup_3_kinal_eob_date,test2.dup_2_kinal_eob_date,test2.dup_1_kinal_eob_date,aj5_min_eob_date,aj4_min_eob_date,aj3_min_eob_date,aj2_min_eob_date,aj1_min_eob_date,ap.true_orig_eob_date)as all_kinal_eob_date,coalesce(test2.dup_4_kinal_penalty,test2.dup_3_kinal_penalty,test2.dup_2_kinal_penalty,test2.dup_1_kinal_penalty,ap.orig_kinal_penalty_amt)as all_kinal_net_penalty,coalesce(test2.dup_4_kinal_interest,test2.dup_3_kinal_interest,test2.dup_2_kinal_interest,test2.dup_1_kinal_interest,ap.orig_kinal_interest_amt)as all_kinal_net_interest,ap.crm,ap.crm_date,ap.crm_topic,ap.crm_subtopic,clh.entry_date,clh.resolution_datetime,info_code.info_code1,info_code.info_code1_entry_date,info_code.info_code2,info_code.info_code2_entry_date,info_code.info_code3,info_code.info_code3_entry_date from code1 ap left outer join ap_claims on ap_claims.clm=ap.orig_claim_id left outer join(select ap.claim_id,clm.internal_id,eob.mnemonic,eob.code_type_c,apc.entry_date,apc.resolution_datetime from ap_claim ap inner join ap_claim_eob_code apc on apc.claim_id=ap.claim_id inner join anthem_eob_code eob on eob.eob_code_id=apc.eob_code_id inner join clm_map clm on clm.cid=ap.claim_id where eob.mnemonic=('clh66'))clh on clh.internal_id=ap.orig_internal_id left outer join(select a.internal_id,max(case when rownuma=1 then mnemonic else null end)as info_code1,max(case when rownuma=1 then entry_date else null end)as info_code1_entry_date,max(case when rownuma=2 then mnemonic else null end)as info_code2,max(case when rownuma=2 then entry_date else null end)as info_code2_entry_date,max(case when rownuma=3 then mnemonic else null end)as info_code3,max(case when rownuma=3 then entry_date else null end)as info_code3_entry_date from(select clm.internal_id,eob.mnemonic,apc.entry_date,row_number()over(partition by ap.claim_id order by apc.entry_date)as rownuma from ap_claim ap inner join ap_claim_eob_code apc on apc.claim_id=ap.claim_id inner join anthem_eob_code eob on eob.eob_code_id=apc.eob_code_id inner join clm_map clm on clm.cid=ap.claim_id where eob.code_type_c=3)a group by a.internal_id)info_code on info_code.internal_id=ap.orig_internal_id left outer join(select orig_internal_id,orig_claim_id,orig_status,orig_kinal_claim_status,max(case when duplicate_claim_number in '1' then coalesce(claim_status2,claim_status1)else null end)as dupe_1_kinal_claim_status,max(case when duplicate_claim_number in '2' then coalesce(claim_status2,claim_status1)else null end)as dupe_2_kinal_claim_status,max(case when duplicate_claim_number in '3' then coalesce(claim_status2,claim_status1)else null end)as dupe_3_kinal_claim_status,max(case when duplicate_claim_number in '4' then coalesce(claim_status2,claim_status1)else null end)as dupe_4_kinal_claim_status,max(case when duplicate_claim_number in '5' then coalesce(claim_status2,claim_status1)else null end)as dupe_5_kinal_claim_status,max(case when duplicate_claim_number in '1' then coalesce(tot_net_payable2,tot_net_payable1)else null end)as dupe_1_kinal_tot_net_paybale,max(case when duplicate_claim_number in '2' then coalesce(tot_net_payable2,tot_net_payable1)else null end)as dupe_2_kinal_tot_net_paybale,max(case when duplicate_claim_number in '3' then coalesce(tot_net_payable2,tot_net_payable1)else null end)as dupe_3_kinal_tot_net_paybale,max(case when duplicate_claim_number in '4' then coalesce(tot_net_payable2,tot_net_payable1)else null end)as dupe_4_kinal_tot_net_paybale,max(case when duplicate_claim_number in '5' then coalesce(tot_net_payable2,tot_net_payable1)else null end)as dupe_5_kinal_tot_net_paybale,max(case when duplicate_claim_number in '1' then coalesce(clm_eob2,clm_eob1)else null end)as dup_1_kinal_eob,max(case when duplicate_claim_number in '2' then coalesce(clm_eob2,clm_eob1)else null end)as dup_2_kinal_eob,max(case when duplicate_claim_number in '3' then coalesce(clm_eob2,clm_eob1)else null end)as dup_3_kinal_eob,max(case when duplicate_claim_number in '4' then coalesce(clm_eob2,clm_eob1)else null end)as dup_4_kinal_eob,max(case when duplicate_claim_number in '5' then coalesce(clm_eob2,clm_eob1)else null end)as dup_5_kinal_eob,max(case when duplicate_claim_number in '1' then coalesce(min_eob_date2,min_eob_date1)else null end)as dup_1_kinal_eob_date,max(case when duplicate_claim_number in '2' then coalesce(min_eob_date2,min_eob_date1)else null end)as dup_2_kinal_eob_date,max(case when duplicate_claim_number in '3' then coalesce(min_eob_date2,min_eob_date1)else null end)as dup_3_kinal_eob_date,max(case when duplicate_claim_number in '4' then coalesce(min_eob_date2,min_eob_date1)else null end)as dup_4_kinal_eob_date,max(case when duplicate_claim_number in '5' then coalesce(min_eob_date2,min_eob_date1)else null end)as dup_5_kinal_eob_date,max(case when duplicate_claim_number in '1' then coalesce(penalty2,penalty1)else null end)as dup_1_kinal_penalty,max(case when duplicate_claim_number in '2' then coalesce(penalty2,penalty1)else null end)as dup_2_kinal_penalty,max(case when duplicate_claim_number in '3' then coalesce(penalty2,penalty1)else null end)as dup_3_kinal_penalty,max(case when duplicate_claim_number in '4' then coalesce(penalty2,penalty1)else null end)as dup_4_kinal_penalty,max(case when duplicate_claim_number in '5' then coalesce(penalty2,penalty1)else null end)as dup_5_kinal_penalty,max(case when duplicate_claim_number in '1' then coalesce(interest2,interest1)else null end)as dup_1_kinal_interest,max(case when duplicate_claim_number in '2' then coalesce(interest2,interest1)else null end)as dup_2_kinal_interest,max(case when duplicate_claim_number in '3' then coalesce(interest2,interest1)else null end)as dup_3_kinal_interest,max(case when duplicate_claim_number in '4' then coalesce(interest2,interest1)else null end)as dup_4_kinal_interest,max(case when duplicate_claim_number in '5' then coalesce(interest2,interest1)else null end)as dup_5_kinal_interest from(select orig_internal_id as orig_internal_id,orig_claim_id as orig_claim_id,orig_status as orig_status,orig_kinal_claim_status as orig_kinal_claim_status,ap1.claim_status as claim_status1,adj.claim_status as claim_status2,ap1.tot_net_payable as tot_net_payable1,adj.tot_net_payable as tot_net_payable2,ap1.clm_eob as clm_eob1,adj.clm_eob as clm_eob2,ap1.min_eob_date as min_eob_date1,adj.min_eob_date as min_eob_date2,ap1.penalty_amt as penalty1,adj.penalty_amt as penalty2,ap1.interest_total as interest1,adj.interest_total as interest2,'1' as duplicate_claim_number from code1 ap left join ap_claims ap1 on ap.dup_clm_id_1=ap1.clm left join ap_claims adj on ap.dup_clm_id_1=adj.orig_adjst_clm_id union all select orig_internal_id as orig_internal_id,orig_claim_id as orig_claim_id,orig_status as orig_status,orig_kinal_claim_status as orig_kinal_claim_status,ap2.claim_status,adj_2.claim_status,ap2.tot_net_payable,adj_2.tot_net_payable,ap2.clm_eob as clm_eob1,adj_2.clm_eob as clm_eob2,ap2.min_eob_date as min_eob_date1,adj_2.min_eob_date as min_eob_date2,ap2.penalty_amt as penalty1,adj_2.penalty_amt as penalty2,ap2.interest_total as interest1,adj_2.interest_total as interest2,'2' as duplicate_claim_number from code1 ap left join ap_claims ap2 on ap.dup_clm_id_2=ap2.clm left join ap_claims adj_2 on ap.dup_clm_id_2=adj_2.orig_adjst_clm_id union all select orig_internal_id as orig_internal_id,orig_claim_id as orig_claim_id,orig_status as orig_status,orig_kinal_claim_status as orig_kinal_claim_status,ap3.claim_status,adj_3.claim_status,ap3.tot_net_payable,adj_3.tot_net_payable,ap3.clm_eob as clm_eob1,adj_3.clm_eob as clm_eob2,ap3.min_eob_date as min_eob_date1,adj_3.min_eob_date as min_eob_date2,ap3.penalty_amt as penalty1,adj_3.penalty_amt as penalty2,ap3.interest_total as interest1,adj_3.interest_total as interest2,'3' as duplicate_claim_number from code1 ap left join ap_claims ap3 on ap.dup_clm_id_3=ap3.clm left join ap_claims adj_3 on ap.dup_clm_id_3=adj_3.orig_adjst_clm_id union all select orig_internal_id as orig_internal_id,orig_claim_id as orig_claim_id,orig_status as orig_status,orig_kinal_claim_status as orig_kinal_claim_status,ap4.claim_status,adj_4.claim_status,ap4.tot_net_payable,adj_4.tot_net_payable,ap4.clm_eob as clm_eob1,adj_4.clm_eob as clm_eob2,ap4.min_eob_date as min_eob_date1,adj_4.min_eob_date as min_eob_date2,ap4.penalty_amt as penalty1,adj_4.penalty_amt as penalty2,ap4.interest_total as interest1,adj_4.interest_total as interest2,'4' as duplicate_claim_number from code1 ap left join ap_claims ap4 on ap.dup_clm_id_4=ap4.clm left join ap_claims adj_4 on ap.dup_clm_id_4=adj_4.orig_adjst_clm_id union all select orig_internal_id as orig_internal_id,orig_claim_id as orig_claim_id,orig_status as orig_status,orig_kinal_claim_status as orig_kinal_claim_status,ap5.claim_status,adj_5.claim_status,ap5.tot_net_payable,adj_5.tot_net_payable,ap5.clm_eob as clm_eob1,adj_5.clm_eob as clm_eob2,ap5.min_eob_date as min_eob_date1,adj_5.min_eob_date as min_eob_date2,ap5.penalty_amt as penalty1,adj_5.penalty_amt as penalty2,ap5.interest_total as interest1,adj_5.interest_total as interest2,'5' as duplicate_claim_number from code1 ap left join ap_claims ap5 on ap.dup_clm_id_5=ap5.clm left join ap_claims adj_5 on ap.dup_clm_id_5=adj_5.orig_adjst_clm_id)test group by orig_internal_id,orig_claim_id,orig_status,orig_kinal_claim_status)test2 on test2.orig_internal_id=ap.orig_internal_id)test3
;

-- testTableFunctionInExprIssue923_4
select max(case when duplicate_claim_number in '1' then coalesce(claim_status2,claim_status1)else null end)as dupe_1_kinal_claim_status
;

-- testTableFunctionInExprIssue923_5
select case when duplicate_claim_number in '1' then coalesce(claim_status2,claim_status1)else null end
;

-- testTableFunctionInExprIssue923_6
SELECT * FROM mytable WHERE func(a) IN '1'
;

-- testBrackets2
SELECT [a] FROM t
;

-- testBrackets3
SELECT * FROM "2016"
;

-- testGeometryDistance
SELECT * FROM foo ORDER BY a <-> b
;

-- testGeometryDistance
SELECT * FROM foo ORDER BY a <#> b
;

-- testKeywordFilterIssue1255
SELECT col1 AS filter FROM table
;

-- testProblemSqlServer_Modulo_Proz
SELECT 5 % 2 FROM A
;

-- testSqlServerHints
SELECT * FROM TB_Sys_Pedido WITH (NOLOCK) WHERE ID_Pedido = :ID_Pedido
;

-- selectWithSingleIn
SELECT 1 FROM dual WHERE a IN 1
;

-- testTrueFalseLiteral
SELECT * FROM tbl WHERE true OR clm1 = 3
;

-- testOrderKeywordIssue932
SELECT order FROM tmp3
;

-- testOrderKeywordIssue932
SELECT tmp3.order FROM tmp3
;

-- testLimitOffsetKeyWordAsNamedParameter
SELECT * FROM mytable LIMIT :limit
;

-- testMultiPartNames1
SELECT a.b
;

-- testMultiPartNames2
SELECT a.b.*
;

-- testMultiPartNames3
SELECT a.*
;

-- testMultiPartNames4
SELECT a.b.c.d.e.f.g.h
;

-- testMultiPartNames5
SELECT * FROM a.b.c.d.e.f.g.h
;

-- testFirstWithKeywordLimit
SELECT LIMIT ? alias.columnName1, alias.columnName2 FROM schemaName.tableName alias ORDER BY alias.columnName2 DESC
;

-- testKeyWorkInsertIssue393
SELECT insert("aaaabbb", 4, 4, "****")
;

-- testGroupConcat
SELECT student_name, GROUP_CONCAT(DISTINCT test_score ORDER BY test_score DESC SEPARATOR ' ') FROM student GROUP BY student_name
;

-- testWithRecursive
WITH RECURSIVE t (n) AS ((SELECT 1) UNION ALL (SELECT n + 1 FROM t WHERE n < 100)) SELECT sum(n) FROM t
;

-- testSqlCache
SELECT SQL_CACHE sales.date FROM sales
;

-- testSelectKeepOver
SELECT MIN(salary) KEEP (DENSE_RANK FIRST ORDER BY commission_pct) OVER (PARTITION BY department_id ) "Worst" FROM employees ORDER BY department_id, salary
;

-- testLimitClauseDroppedIssue845_2
SELECT * FROM employee ORDER BY emp_id LIMIT 10 OFFSET 2
;

-- testFunctionRight
SELECT right(table1.col1, 4) FROM table1
;

-- testSelectBrackets
SELECT avg((123.250)::numeric)
;

-- testKeywordDuplicate
SELECT mytable.duplicate FROM mytable
;

-- testMultiPartColumnName
SELECT columnName FROM tableName
;

-- testNamedWindowDefinitionIssue1581
SELECT sum(salary) OVER w, avg(salary) OVER w FROM empsalary WINDOW w AS (PARTITION BY depname ORDER BY salary DESC)
;

-- testIntegerDivOperator
SELECT col DIV 3
;

-- testIntervalWithFunction
SELECT DATE_ADD(start_date, INTERVAL COALESCE(duration, 21) MINUTE) AS end_datetime FROM appointment
;

-- testLimitOffsetIssue462_2
SELECT * FROM mytable LIMIT ?1 OFFSET ?2
;

-- testPerformanceIssue1397
select "table1"."label",(case when("table1"."description" in('registration q2 001-alternative capacitor devices-stuff sample','registration q2 002-alternative capacitor devices-stuff sample','registration q2 005-alternative capacitor devices-stuff sample','registration q2 2021-alternative capacitor devices-stuff sample','registration q4 000-alternative capacitor devices','registration q4 001-alternative capacitor devices-stuff sample','registration q4 002-alternative capacitor devices-stuff sample','registration q4 006-alternative capacitor devices-stuff sample'))then 'alternative capacitor devices' when("table1"."description" in('registration q2 000-entry alt propane','registration q2 001-entry alt propane','registration q2 002-entry alt propane','registration q4 000-entry alt propane','registration q4 001-entry alt propane','registration q4 002-entry alt propane','registration q4 005-camper yeah-entry alt'))then 'entry alt propane' when("table1"."description" in('registration q1 000-entry amps','registration q2 000-entry amps','registration q2 001-entry amps','registration q2 002-entry amps','registration q2 005-entry amps','registration q4 000-entry amps','registration q4 001-entry amps','registration q4 002-entry amps','registration q4 005-entry amps','registration q4 006-entry amps'))then 'entry amps' when("table1"."description" in('registration q2 000-fullsize amp alt','registration q2 001-fullsize amp alt','registration q2 002-fullsize amp alt','registration q2 005-fullsize amp alt','registration q2 2021-fullsize amp alt','registration q4 000-fullsize amp alt','registration q4 001-fullsize amp alt','registration q4 002-fullsize amp alt','registration q4 005-fullsize amp alt','registration q4 006-fullsize amp alt'))then 'fullsize amp alt' when("table1"."description" in('registration q1 000-purple device makes-450 ratings','registration q2 000-purple device makes-450 ratings','registration q2 001-purple device makes-450 ratings','registration q2 002-purple device makes-450 ratings','registration q2 005-purple device makes-450 ratings','registration q2 2021-purple device makes-450 ratings','registration q4 000-purple device makes-450 ratings','registration q4 001-purple device makes-450 ratings','registration q4 002-purple device makes-450 ratings','registration q4 005-purple device makes-450 ratings','registration q4 006-purple device makes-450 ratings'))then 'purple device makes' when("table1"."description" in('registration q2 000-purple device makes non-waterproof-any-american','registration q2 001-purple device makes non-waterproof-any-american','registration q2 002-purple device makes non-waterproof-any-american','registration q4 000-purple device makes non-waterproof-any-american'))then 'purple device makes non-waterproof-any-american' when("table1"."description" in('registration q2 000-waterproof speakers','registration q2 001-waterproof speakers','registration q2 002-waterproof speakers','registration q2 005-waterproof speakers','registration q2 2021-waterproof speakers','registration q4 000-waterproof speakers','registration q4 001-waterproof speakers','registration q4 002-waterproof speakers','registration q4 005-waterproof speakers','registration q4 006-waterproof speakers'))then 'waterproof speakers' when("table1"."description" in('registration q2 000-waterproof makes-450 ratings','registration q2 001-waterproof makes-450 ratings','registration q2 002-waterproof makes-450 ratings','registration q2 005-waterproof makes-450 ratings','registration q2 2021-waterproof makes-450 ratings','registration q4 000-waterproof makes-450 ratings','registration q4 001-waterproof makes-450 ratings','registration q4 002-waterproof makes-450 ratings','registration q4 005-waterproof makes-450 ratings','registration q4 006-waterproof makes-450 ratings'))then 'waterproof makes' when("table1"."description" in('registration q2 001-waterproof propane redsize','registration q2 002-waterproof propane redsize','registration q2 005-waterproof propane redsize','registration q2 2021-waterproof propane redsize','registration q4 001-waterproof propane redsize','registration q4 002-waterproof propane redsize','registration q4 005-waterproof propane redsize','registration q4 006-waterproof propane redsize'))then 'waterproof propane redsize' when("table1"."description" in('registration q2 001-waterproof propane small','registration q2 002-waterproof propane small','registration q2 005-waterproof propane small','registration q4 001-waterproof propane small','registration q4 002-waterproof propane small','registration q4 005-waterproof propane small','registration q4 006-waterproof propane small'))then 'waterproof propane small' when("table1"."description" in('registration q2 000-red-junk-waterproof','registration q2 001-red-junk-waterproof','registration q2 002-red-junk-waterproof','registration q2 005-red-junk-waterproof','registration q2 2021-red-junk-waterproof','registration q4 000-red-junk-waterproof','registration q4 001-red-junk-waterproof','registration q4 002-red-junk-waterproof','registration q4 005-red-junk-waterproof','registration q4 006-red-junk-waterproof'))then 'red-junk waterproof' when("table1"."description"='registration q2 2021-redsize amps')then 'redsize amps' when("table1"."description" in('registration q2 000-near-entry waterproof','registration q2 001-near-entry waterproof','registration q2 002-near-entry waterproof','registration q2 005-near-entry waterproof','registration q2 2021-near-entry waterproof','registration q4 000-near-entry waterproof','registration q4 001-near-entry waterproof','registration q4 002-near-entry waterproof','registration q4 005-near-entry waterproof','registration q4 006-near-entry waterproof'))then 'near-entry waterproof' when("table1"."description" in('registration q2 000-home theaters-smooth','registration q2 001-home theaters-smooth','registration q2 002-home theaters-smooth','registration q2 005-home theaters-smooth','registration q2 2021-home theaters-smooth','registration q4 000-home theaters-smooth','registration q4 001-home theaters-smooth','registration q4 002-home theaters-smooth','registration q4 005-home theaters-smooth','registration q4 006-home theaters-smooth'))then 'home theaters-smooth' when("table1"."description" in('registration q2 000-home theaters-fullsize','registration q2 001-home theaters-fullsize','registration q2 002-home theaters-fullsize','registration q2 005-home theaters-fullsize','registration q2 2021-home theaters-fullsize','registration q4 000-home theaters-fullsize','registration q4 001-home theaters-fullsize','registration q4 002-home theaters-fullsize','registration q4 005-home theaters-fullsize','registration q4 006-home theaters-fullsize'))then 'home theaters-fullsize' when("table1"."description" in('registration q2 000-home theaters-fullsize-half full','registration q2 001-home theaters-fullsize-half full','registration q2 002-home theaters-fullsize-half full','registration q2 005-home theaters-fullsize-half full','registration q2 2021-home theaters-fullsize-half full','registration q4 000-home theaters-fullsize-half full','registration q4 001-home theaters-fullsize-half full','registration q4 002-home theaters-fullsize-half full','registration q4 005-home theaters-fullsize-half full'))then 'home theaters-fullsize-half full' when("table1"."description" in('registration q1 000-small amps','registration q1 001-small amps','registration q1 002-small amps','registration q1 005-small amps','registration q1 006-small amps','registration q1 2021-small amps','registration q2 000-small amps','registration q2 001-small amps','registration q2 002-small amps','registration q2 005-small amps','registration q2 2021-small amps','registration q3 000-small amps','registration q3 001-small amps','registration q3 002-small amps','registration q3 006-small amps','registration q4 000-small amps','registration q4 001-small amps','registration q4 002-small amps','registration q4 005-small amps','registration q4 006-small amps'))then 'small amps' when("table1"."description" in('registration q2 000-small-entry amps-any-american','registration q2 001-small-entry amps-any-american','registration q2 002-small-entry amps-any-american','registration q4 000-small-entry amps-any-american'))then 'small-entry amps-any-american' when("table1"."description" in('registration q2 005-camper yeah-entry alt','registration q4 006-camper yeah-entry alt'))then 'camper yeah-entry alt' when("table1"."description" in('registration q2 2021-camper yeah-fullsize','registration q4 006-camper yeah-fullsize'))then 'camper yeah-fullsize' when("table1"."description" in('registration q2 000-camper yeah-redsize','registration q2 001-camper yeah-redsize','registration q2 002-camper yeah-redsize','registration q2 005-camper yeah-redsize','registration q2 2021-camper yeah-redsize','registration q4 000-camper yeah-redsize','registration q4 001-camper yeah-redsize','registration q4 002-camper yeah-redsize','registration q4 005-camper yeah-redsize','registration q4 006-camper yeah-redsize'))then 'camper yeah-redsize' when("table1"."description" in('registration q1 000-camper yeah-small','registration q1 001-camper yeah-small','registration q1 002-camper yeah-small','registration q1 005-camper yeah-small','registration q1 006-camper yeah-small','registration q1 2021-camper yeah-small','registration q2 000-camper yeah-small','registration q2 001-camper yeah-small','registration q2 002-camper yeah-small','registration q2 005-camper yeah-small','registration q2 2021-camper yeah-small','registration q3 000-camper yeah-small','registration q3 001-camper yeah-small','registration q3 002-camper yeah-small','registration q3 006-camper yeah-small','registration q4 000-camper yeah-small','registration q4 001-camper yeah-small','registration q4 002-camper yeah-small','registration q4 005-camper yeah-small','registration q4 006-camper yeah-small'))then 'camper yeah-small' when("table1"."description" in('registration q2 000-camper yeah-small-any-american','registration q2 001-camper yeah-small-any-american','registration q2 002-camper yeah-small-any-american','registration q4 000-camper yeah-small-any-american'))then 'camper yeah-small-any-american' when("table1"."description" in('registration q2 000-camper yeah-premium redsize alt','registration q4 000-camper yeah-premium redsize alt'))then 'camper yeah premium redsize alt' when("table1"."description" in('registration q2 002-camper wagon','registration q2 005-camper wagon','registration q2 2021-camper wagon','registration q4 002-camper wagon','registration q4 005-camper wagon','registration q4 006-camper wagon'))then 'camper wagon' when("table1"."description" in('registration q2 005-campers amps','registration q2 2021-campers amps','registration q4 005-campers amps','registration q4 006-campers amps'))then 'campers amps' when("table1"."description" in('registration q2 000-campery','registration q2 001-campery','registration q2 002-campery','registration q2 005-campery','registration q2 2021-campery','registration q4 000-campery','registration q4 001-campery','registration q4 002-campery','registration q4 005-campery','registration q4 006-campery'))then 'campery' when("table1"."description" in('registration q1 000-upper reddle alt','registration q1 001-upper reddle alt','registration q1 002-upper reddle alt','registration q1 005-upper reddle alt','registration q1 006-upper reddle alt','registration q1 2021-upper reddle alt','registration q2 000-upper reddle alt','registration q2 001-upper reddle alt','registration q2 002-upper reddle alt','registration q2 005-upper reddle alt','registration q3 000-upper reddle alt','registration q3 001-upper reddle alt','registration q3 002-upper reddle alt','registration q3 006-upper reddle alt','registration q4 000-upper reddle alt','registration q4 001-upper reddle alt','registration q4 002-upper reddle alt','registration q4 005-upper reddle alt','registration q4 006-upper reddle alt'))then 'upper reddle alt' when("table1"."description" in('registration q2 000-upper reddle alt-any-american','registration q2 001-upper reddle alt-any-american','registration q2 002-upper reddle alt-any-american','registration q4 000-upper reddle alt-any-american'))then 'upper reddle alt-any-american' when("table1"."description" in('registration q2 000-fires-smooth','registration q2 001-fires-smooth','registration q2 002-fires-smooth','registration q2 005-fires-smooth','registration q2 2021-fires-smooth','registration q4 000-fires-smooth','registration q4 001-fires-smooth','registration q4 002-fires-smooth','registration q4 005-fires-smooth','registration q4 006-fires-smooth'))then 'fires-smooth' else "table1"."description" end)as "segment",(case when("table1"."description" in('registration q1 000-entry amps','registration q1 000-purple device makes-450 ratings','registration q1 000-small amps','registration q1 000-camper yeah-small','registration q1 000-upper reddle alt'))then '000 q1' when("table1"."description" in('registration q2 000-entry alt propane','registration q2 000-entry amps','registration q2 000-fullsize amp alt','registration q2 000-purple device makes-450 ratings','registration q2 000-purple device makes non-waterproof-any-american','registration q2 000-waterproof speakers','registration q2 000-waterproof makes-450 ratings','registration q2 000-red-junk-waterproof','registration q2 000-near-entry waterproof','registration q2 000-home theaters-smooth','registration q2 000-home theaters-fullsize','registration q2 000-home theaters-fullsize-half full','registration q2 000-small amps','registration q2 000-small-entry amps-any-american','registration q2 000-camper yeah-redsize','registration q2 000-camper yeah-premium redsize alt','registration q2 000-camper yeah-small','registration q2 000-camper yeah-small-any-american','registration q2 000-campery','registration q2 000-upper reddle alt','registration q2 000-upper reddle alt-any-american','registration q2 000-fires-smooth'))then '000 q2' when("table1"."description" in('registration q3 000-small amps','registration q3 000-camper yeah-small','registration q3 000-upper reddle alt'))then '000 q3' when("table1"."description" in('registration q4 000-alternative capacitor devices','registration q4 000-entry alt propane','registration q4 000-entry amps','registration q4 000-fullsize amp alt','registration q4 000-purple device makes-450 ratings','registration q4 000-purple device makes non-waterproof-any-american','registration q4 000-waterproof speakers','registration q4 000-waterproof makes-450 ratings','registration q4 000-red-junk-waterproof','registration q4 000-near-entry waterproof','registration q4 000-home theaters-smooth','registration q4 000-home theaters-fullsize','registration q4 000-home theaters-fullsize-half full','registration q4 000-small amps','registration q4 000-small-entry amps-any-american','registration q4 000-camper yeah-redsize','registration q4 000-camper yeah-premium redsize alt','registration q4 000-camper yeah-small','registration q4 000-camper yeah-small-any-american','registration q4 000-campery','registration q4 000-upper reddle alt','registration q4 000-upper reddle alt-any-american','registration q4 000-fires-smooth'))then '000 q4' when("table1"."description" in('registration q1 001-small amps','registration q1 001-camper yeah-small','registration q1 001-upper reddle alt'))then '001 q1' when("table1"."description" in('registration q2 001-alternative capacitor devices-stuff sample','registration q2 001-entry alt propane','registration q2 001-entry amps','registration q2 001-fullsize amp alt','registration q2 001-purple device makes-450 ratings','registration q2 001-purple device makes non-waterproof-any-american','registration q2 001-waterproof speakers','registration q2 001-waterproof makes-450 ratings','registration q2 001-waterproof propane redsize','registration q2 001-waterproof propane small','registration q2 001-red-junk-waterproof','registration q2 001-near-entry waterproof','registration q2 001-home theaters-smooth','registration q2 001-home theaters-fullsize','registration q2 001-home theaters-fullsize-half full','registration q2 001-small amps','registration q2 001-small-entry amps-any-american','registration q2 001-camper yeah-redsize','registration q2 001-camper yeah-small','registration q2 001-camper yeah-small-any-american','registration q2 001-campery','registration q2 001-upper reddle alt','registration q2 001-upper reddle alt-any-american','registration q2 001-fires-smooth'))then '001 q2' when("table1"."description" in('registration q3 001-small amps','registration q3 001-camper yeah-small','registration q3 001-upper reddle alt'))then '001 q3' when("table1"."description" in('registration q4 001-alternative capacitor devices-stuff sample','registration q4 001-entry alt propane','registration q4 001-entry amps','registration q4 001-fullsize amp alt','registration q4 001-purple device makes-450 ratings','registration q4 001-waterproof speakers','registration q4 001-waterproof makes-450 ratings','registration q4 001-waterproof propane redsize','registration q4 001-waterproof propane small','registration q4 001-red-junk-waterproof','registration q4 001-near-entry waterproof','registration q4 001-home theaters-smooth','registration q4 001-home theaters-fullsize','registration q4 001-home theaters-fullsize-half full','registration q4 001-small amps','registration q4 001-camper yeah-redsize','registration q4 001-camper yeah-small','registration q4 001-campery','registration q4 001-upper reddle alt','registration q4 001-fires-smooth'))then '001 q4' when("table1"."description" in('registration q1 002-small amps','registration q1 002-camper yeah-small','registration q1 002-upper reddle alt'))then '002 q1' when("table1"."description" in('registration q2 002-alternative capacitor devices-stuff sample','registration q2 002-entry alt propane','registration q2 002-entry amps','registration q2 002-fullsize amp alt','registration q2 002-purple device makes-450 ratings','registration q2 002-purple device makes non-waterproof-any-american','registration q2 002-waterproof speakers','registration q2 002-waterproof makes-450 ratings','registration q2 002-waterproof propane redsize','registration q2 002-waterproof propane small','registration q2 002-red-junk-waterproof','registration q2 002-near-entry waterproof','registration q2 002-home theaters-smooth','registration q2 002-home theaters-fullsize','registration q2 002-home theaters-fullsize-half full','registration q2 002-small amps','registration q2 002-small-entry amps-any-american','registration q2 002-camper yeah-redsize','registration q2 002-camper yeah-small','registration q2 002-camper yeah-small-any-american','registration q2 002-camper wagon','registration q2 002-campery','registration q2 002-upper reddle alt','registration q2 002-upper reddle alt-any-american','registration q2 002-fires-smooth'))then '002 q2' when("table1"."description" in('registration q3 002-small amps','registration q3 002-camper yeah-small','registration q3 002-upper reddle alt'))then '002 q3' when("table1"."description" in('registration q4 002-alternative capacitor devices-stuff sample','registration q4 002-entry alt propane','registration q4 002-entry amps','registration q4 002-fullsize amp alt','registration q4 002-purple device makes-450 ratings','registration q4 002-waterproof speakers','registration q4 002-waterproof makes-450 ratings','registration q4 002-waterproof propane redsize','registration q4 002-waterproof propane small','registration q4 002-red-junk-waterproof','registration q4 002-near-entry waterproof','registration q4 002-home theaters-smooth','registration q4 002-home theaters-fullsize','registration q4 002-home theaters-fullsize-half full','registration q4 002-small amps','registration q4 002-camper yeah-redsize','registration q4 002-camper yeah-small','registration q4 002-camper wagon','registration q4 002-campery','registration q4 002-upper reddle alt','registration q4 002-fires-smooth'))then '002 q4' when("table1"."description" in('registration q1 005-small amps','registration q1 005-camper yeah-small','registration q1 005-upper reddle alt'))then '005 q1' when("table1"."description" in('registration q2 005-alternative capacitor devices-stuff sample','registration q2 005-entry amps','registration q2 005-fullsize amp alt','registration q2 005-purple device makes-450 ratings','registration q2 005-waterproof speakers','registration q2 005-waterproof makes-450 ratings','registration q2 005-waterproof propane redsize','registration q2 005-waterproof propane small','registration q2 005-red-junk-waterproof','registration q2 005-near-entry waterproof','registration q2 005-home theaters-smooth','registration q2 005-home theaters-fullsize','registration q2 005-home theaters-fullsize-half full','registration q2 005-small amps','registration q2 005-camper yeah-entry alt','registration q2 005-camper yeah-redsize','registration q2 005-camper yeah-small','registration q2 005-camper wagon','registration q2 005-campers amps','registration q2 005-campery','registration q2 005-upper reddle alt','registration q2 005-fires-smooth'))then '005 q2' when("table1"."description" in('registration q4 005-entry amps','registration q4 005-fullsize amp alt','registration q4 005-purple device makes-450 ratings','registration q4 005-waterproof speakers','registration q4 005-waterproof makes-450 ratings','registration q4 005-waterproof propane redsize','registration q4 005-waterproof propane small','registration q4 005-red-junk-waterproof','registration q4 005-near-entry waterproof','registration q4 005-home theaters-smooth','registration q4 005-home theaters-fullsize','registration q4 005-home theaters-fullsize-half full','registration q4 005-small amps','registration q4 005-camper yeah-entry alt','registration q4 005-camper yeah-redsize','registration q4 005-camper yeah-small','registration q4 005-camper wagon','registration q4 005-campers amps','registration q4 005-campery','registration q4 005-upper reddle alt','registration q4 005-fires-smooth'))then '005 q4' when("table1"."description" in('registration q1 006-small amps','registration q1 006-camper yeah-small','registration q1 006-upper reddle alt'))then '006 q1' when("table1"."description" in('registration q3 006-small amps','registration q3 006-camper yeah-small','registration q3 006-upper reddle alt'))then '006 q3' when("table1"."description" in('registration q4 006-alternative capacitor devices-stuff sample','registration q4 006-entry amps','registration q4 006-fullsize amp alt','registration q4 006-purple device makes-450 ratings','registration q4 006-waterproof speakers','registration q4 006-waterproof makes-450 ratings','registration q4 006-waterproof propane redsize','registration q4 006-waterproof propane small','registration q4 006-red-junk-waterproof','registration q4 006-near-entry waterproof','registration q4 006-home theaters-smooth','registration q4 006-home theaters-fullsize','registration q4 006-small amps','registration q4 006-camper yeah-entry alt','registration q4 006-camper yeah-fullsize','registration q4 006-camper yeah-redsize','registration q4 006-camper yeah-small','registration q4 006-camper wagon','registration q4 006-campers amps','registration q4 006-campery','registration q4 006-upper reddle alt','registration q4 006-fires-smooth'))then '006 q4' when("table1"."description" in('registration q1 2021-small amps','registration q1 2021-camper yeah-small','registration q1 2021-upper reddle alt'))then '2021 q1' when("table1"."description" in('registration q2 2021-alternative capacitor devices-stuff sample','registration q2 2021-fullsize amp alt','registration q2 2021-purple device makes-450 ratings','registration q2 2021-waterproof speakers','registration q2 2021-waterproof makes-450 ratings','registration q2 2021-waterproof propane redsize','registration q2 2021-red-junk-waterproof','registration q2 2021-redsize amps','registration q2 2021-near-entry waterproof','registration q2 2021-home theaters-smooth','registration q2 2021-home theaters-fullsize','registration q2 2021-home theaters-fullsize-half full','registration q2 2021-small amps','registration q2 2021-camper yeah-fullsize','registration q2 2021-camper yeah-redsize','registration q2 2021-camper yeah-small','registration q2 2021-camper wagon','registration q2 2021-campers amps','registration q2 2021-campery','registration q2 2021-fires-smooth'))then '2021 q2' else "table1"."description" end)as "study_quarter/year",count(distinct "table2"."id")as "ctd:id:ok" from "schema1"."table2" "table2" inner join "schema1"."table1" "table1" on("table2"."id"="table1"."id")where(((case when("table1"."description" in('registration q2 001-alternative capacitor devices-stuff sample','registration q2 002-alternative capacitor devices-stuff sample','registration q2 005-alternative capacitor devices-stuff sample','registration q2 2021-alternative capacitor devices-stuff sample','registration q4 000-alternative capacitor devices','registration q4 001-alternative capacitor devices-stuff sample','registration q4 002-alternative capacitor devices-stuff sample','registration q4 006-alternative capacitor devices-stuff sample'))then 'alternative capacitor devices' when("table1"."description" in('registration q2 000-entry alt propane','registration q2 001-entry alt propane','registration q2 002-entry alt propane','registration q4 000-entry alt propane','registration q4 001-entry alt propane','registration q4 002-entry alt propane','registration q4 005-camper yeah-entry alt'))then 'entry alt propane' when("table1"."description" in('registration q1 000-entry amps','registration q2 000-entry amps','registration q2 001-entry amps','registration q2 002-entry amps','registration q2 005-entry amps','registration q4 000-entry amps','registration q4 001-entry amps','registration q4 002-entry amps','registration q4 005-entry amps','registration q4 006-entry amps'))then 'entry amps' when("table1"."description" in('registration q2 000-fullsize amp alt','registration q2 001-fullsize amp alt','registration q2 002-fullsize amp alt','registration q2 005-fullsize amp alt','registration q2 2021-fullsize amp alt','registration q4 000-fullsize amp alt','registration q4 001-fullsize amp alt','registration q4 002-fullsize amp alt','registration q4 005-fullsize amp alt','registration q4 006-fullsize amp alt'))then 'fullsize amp alt' when("table1"."description" in('registration q1 000-purple device makes-450 ratings','registration q2 000-purple device makes-450 ratings','registration q2 001-purple device makes-450 ratings','registration q2 002-purple device makes-450 ratings','registration q2 005-purple device makes-450 ratings','registration q2 2021-purple device makes-450 ratings','registration q4 000-purple device makes-450 ratings','registration q4 001-purple device makes-450 ratings','registration q4 002-purple device makes-450 ratings','registration q4 005-purple device makes-450 ratings','registration q4 006-purple device makes-450 ratings'))then 'purple device makes' when("table1"."description" in('registration q2 000-purple device makes non-waterproof-any-american','registration q2 001-purple device makes non-waterproof-any-american','registration q2 002-purple device makes non-waterproof-any-american','registration q4 000-purple device makes non-waterproof-any-american'))then 'purple device makes non-waterproof-any-american' when("table1"."description" in('registration q2 000-waterproof speakers','registration q2 001-waterproof speakers','registration q2 002-waterproof speakers','registration q2 005-waterproof speakers','registration q2 2021-waterproof speakers','registration q4 000-waterproof speakers','registration q4 001-waterproof speakers','registration q4 002-waterproof speakers','registration q4 005-waterproof speakers','registration q4 006-waterproof speakers'))then 'waterproof speakers' when("table1"."description" in('registration q2 000-waterproof makes-450 ratings','registration q2 001-waterproof makes-450 ratings','registration q2 002-waterproof makes-450 ratings','registration q2 005-waterproof makes-450 ratings','registration q2 2021-waterproof makes-450 ratings','registration q4 000-waterproof makes-450 ratings','registration q4 001-waterproof makes-450 ratings','registration q4 002-waterproof makes-450 ratings','registration q4 005-waterproof makes-450 ratings','registration q4 006-waterproof makes-450 ratings'))then 'waterproof makes' when("table1"."description" in('registration q2 001-waterproof propane redsize','registration q2 002-waterproof propane redsize','registration q2 005-waterproof propane redsize','registration q2 2021-waterproof propane redsize','registration q4 001-waterproof propane redsize','registration q4 002-waterproof propane redsize','registration q4 005-waterproof propane redsize','registration q4 006-waterproof propane redsize'))then 'waterproof propane redsize' when("table1"."description" in('registration q2 001-waterproof propane small','registration q2 002-waterproof propane small','registration q2 005-waterproof propane small','registration q4 001-waterproof propane small','registration q4 002-waterproof propane small','registration q4 005-waterproof propane small','registration q4 006-waterproof propane small'))then 'waterproof propane small' when("table1"."description" in('registration q2 000-red-junk-waterproof','registration q2 001-red-junk-waterproof','registration q2 002-red-junk-waterproof','registration q2 005-red-junk-waterproof','registration q2 2021-red-junk-waterproof','registration q4 000-red-junk-waterproof','registration q4 001-red-junk-waterproof','registration q4 002-red-junk-waterproof','registration q4 005-red-junk-waterproof','registration q4 006-red-junk-waterproof'))then 'red-junk waterproof' when("table1"."description"='registration q2 2021-redsize amps')then 'redsize amps' when("table1"."description" in('registration q2 000-near-entry waterproof','registration q2 001-near-entry waterproof','registration q2 002-near-entry waterproof','registration q2 005-near-entry waterproof','registration q2 2021-near-entry waterproof','registration q4 000-near-entry waterproof','registration q4 001-near-entry waterproof','registration q4 002-near-entry waterproof','registration q4 005-near-entry waterproof','registration q4 006-near-entry waterproof'))then 'near-entry waterproof' when("table1"."description" in('registration q2 000-home theaters-smooth','registration q2 001-home theaters-smooth','registration q2 002-home theaters-smooth','registration q2 005-home theaters-smooth','registration q2 2021-home theaters-smooth','registration q4 000-home theaters-smooth','registration q4 001-home theaters-smooth','registration q4 002-home theaters-smooth','registration q4 005-home theaters-smooth','registration q4 006-home theaters-smooth'))then 'home theaters-smooth' when("table1"."description" in('registration q2 000-home theaters-fullsize','registration q2 001-home theaters-fullsize','registration q2 002-home theaters-fullsize','registration q2 005-home theaters-fullsize','registration q2 2021-home theaters-fullsize','registration q4 000-home theaters-fullsize','registration q4 001-home theaters-fullsize','registration q4 002-home theaters-fullsize','registration q4 005-home theaters-fullsize','registration q4 006-home theaters-fullsize'))then 'home theaters-fullsize' when("table1"."description" in('registration q2 000-home theaters-fullsize-half full','registration q2 001-home theaters-fullsize-half full','registration q2 002-home theaters-fullsize-half full','registration q2 005-home theaters-fullsize-half full','registration q2 2021-home theaters-fullsize-half full','registration q4 000-home theaters-fullsize-half full','registration q4 001-home theaters-fullsize-half full','registration q4 002-home theaters-fullsize-half full','registration q4 005-home theaters-fullsize-half full'))then 'home theaters-fullsize-half full' when("table1"."description" in('registration q1 000-small amps','registration q1 001-small amps','registration q1 002-small amps','registration q1 005-small amps','registration q1 006-small amps','registration q1 2021-small amps','registration q2 000-small amps','registration q2 001-small amps','registration q2 002-small amps','registration q2 005-small amps','registration q2 2021-small amps','registration q3 000-small amps','registration q3 001-small amps','registration q3 002-small amps','registration q3 006-small amps','registration q4 000-small amps','registration q4 001-small amps','registration q4 002-small amps','registration q4 005-small amps','registration q4 006-small amps'))then 'small amps' when("table1"."description" in('registration q2 000-small-entry amps-any-american','registration q2 001-small-entry amps-any-american','registration q2 002-small-entry amps-any-american','registration q4 000-small-entry amps-any-american'))then 'small-entry amps-any-american' when("table1"."description" in('registration q2 005-camper yeah-entry alt','registration q4 006-camper yeah-entry alt'))then 'camper yeah-entry alt' when("table1"."description" in('registration q2 2021-camper yeah-fullsize','registration q4 006-camper yeah-fullsize'))then 'camper yeah-fullsize' when("table1"."description" in('registration q2 000-camper yeah-redsize','registration q2 001-camper yeah-redsize','registration q2 002-camper yeah-redsize','registration q2 005-camper yeah-redsize','registration q2 2021-camper yeah-redsize','registration q4 000-camper yeah-redsize','registration q4 001-camper yeah-redsize','registration q4 002-camper yeah-redsize','registration q4 005-camper yeah-redsize','registration q4 006-camper yeah-redsize'))then 'camper yeah-redsize' when("table1"."description" in('registration q1 000-camper yeah-small','registration q1 001-camper yeah-small','registration q1 002-camper yeah-small','registration q1 005-camper yeah-small','registration q1 006-camper yeah-small','registration q1 2021-camper yeah-small','registration q2 000-camper yeah-small','registration q2 001-camper yeah-small','registration q2 002-camper yeah-small','registration q2 005-camper yeah-small','registration q2 2021-camper yeah-small','registration q3 000-camper yeah-small','registration q3 001-camper yeah-small','registration q3 002-camper yeah-small','registration q3 006-camper yeah-small','registration q4 000-camper yeah-small','registration q4 001-camper yeah-small','registration q4 002-camper yeah-small','registration q4 005-camper yeah-small','registration q4 006-camper yeah-small'))then 'camper yeah-small' when("table1"."description" in('registration q2 000-camper yeah-small-any-american','registration q2 001-camper yeah-small-any-american','registration q2 002-camper yeah-small-any-american','registration q4 000-camper yeah-small-any-american'))then 'camper yeah-small-any-american' when("table1"."description" in('registration q2 000-camper yeah-premium redsize alt','registration q4 000-camper yeah-premium redsize alt'))then 'camper yeah premium redsize alt' when("table1"."description" in('registration q2 002-camper wagon','registration q2 005-camper wagon','registration q2 2021-camper wagon','registration q4 002-camper wagon','registration q4 005-camper wagon','registration q4 006-camper wagon'))then 'camper wagon' when("table1"."description" in('registration q2 005-campers amps','registration q2 2021-campers amps','registration q4 005-campers amps','registration q4 006-campers amps'))then 'campers amps' when("table1"."description" in('registration q2 000-campery','registration q2 001-campery','registration q2 002-campery','registration q2 005-campery','registration q2 2021-campery','registration q4 000-campery','registration q4 001-campery','registration q4 002-campery','registration q4 005-campery','registration q4 006-campery'))then 'campery' when("table1"."description" in('registration q1 000-upper reddle alt','registration q1 001-upper reddle alt','registration q1 002-upper reddle alt','registration q1 005-upper reddle alt','registration q1 006-upper reddle alt','registration q1 2021-upper reddle alt','registration q2 000-upper reddle alt','registration q2 001-upper reddle alt','registration q2 002-upper reddle alt','registration q2 005-upper reddle alt','registration q3 000-upper reddle alt','registration q3 001-upper reddle alt','registration q3 002-upper reddle alt','registration q3 006-upper reddle alt','registration q4 000-upper reddle alt','registration q4 001-upper reddle alt','registration q4 002-upper reddle alt','registration q4 005-upper reddle alt','registration q4 006-upper reddle alt'))then 'upper reddle alt' when("table1"."description" in('registration q2 000-upper reddle alt-any-american','registration q2 001-upper reddle alt-any-american','registration q2 002-upper reddle alt-any-american','registration q4 000-upper reddle alt-any-american'))then 'upper reddle alt-any-american' when("table1"."description" in('registration q2 000-fires-smooth','registration q2 001-fires-smooth','registration q2 002-fires-smooth','registration q2 005-fires-smooth','registration q2 2021-fires-smooth','registration q4 000-fires-smooth','registration q4 001-fires-smooth','registration q4 002-fires-smooth','registration q4 005-fires-smooth','registration q4 006-fires-smooth'))then 'fires-smooth' else "table1"."description" end)>='alternative capacitor devices')and((case when("table1"."description" in('registration q2 001-alternative capacitor devices-stuff sample','registration q2 002-alternative capacitor devices-stuff sample','registration q2 005-alternative capacitor devices-stuff sample','registration q2 2021-alternative capacitor devices-stuff sample','registration q4 000-alternative capacitor devices','registration q4 001-alternative capacitor devices-stuff sample','registration q4 002-alternative capacitor devices-stuff sample','registration q4 006-alternative capacitor devices-stuff sample'))then 'alternative capacitor devices' when("table1"."description" in('registration q2 000-entry alt propane','registration q2 001-entry alt propane','registration q2 002-entry alt propane','registration q4 000-entry alt propane','registration q4 001-entry alt propane','registration q4 002-entry alt propane','registration q4 005-camper yeah-entry alt'))then 'entry alt propane' when("table1"."description" in('registration q1 000-entry amps','registration q2 000-entry amps','registration q2 001-entry amps','registration q2 002-entry amps','registration q2 005-entry amps','registration q4 000-entry amps','registration q4 001-entry amps','registration q4 002-entry amps','registration q4 005-entry amps','registration q4 006-entry amps'))then 'entry amps' when("table1"."description" in('registration q2 000-fullsize amp alt','registration q2 001-fullsize amp alt','registration q2 002-fullsize amp alt','registration q2 005-fullsize amp alt','registration q2 2021-fullsize amp alt','registration q4 000-fullsize amp alt','registration q4 001-fullsize amp alt','registration q4 002-fullsize amp alt','registration q4 005-fullsize amp alt','registration q4 006-fullsize amp alt'))then 'fullsize amp alt' when("table1"."description" in('registration q1 000-purple device makes-450 ratings','registration q2 000-purple device makes-450 ratings','registration q2 001-purple device makes-450 ratings','registration q2 002-purple device makes-450 ratings','registration q2 005-purple device makes-450 ratings','registration q2 2021-purple device makes-450 ratings','registration q4 000-purple device makes-450 ratings','registration q4 001-purple device makes-450 ratings','registration q4 002-purple device makes-450 ratings','registration q4 005-purple device makes-450 ratings','registration q4 006-purple device makes-450 ratings'))then 'purple device makes' when("table1"."description" in('registration q2 000-purple device makes non-waterproof-any-american','registration q2 001-purple device makes non-waterproof-any-american','registration q2 002-purple device makes non-waterproof-any-american','registration q4 000-purple device makes non-waterproof-any-american'))then 'purple device makes non-waterproof-any-american' when("table1"."description" in('registration q2 000-waterproof speakers','registration q2 001-waterproof speakers','registration q2 002-waterproof speakers','registration q2 005-waterproof speakers','registration q2 2021-waterproof speakers','registration q4 000-waterproof speakers','registration q4 001-waterproof speakers','registration q4 002-waterproof speakers','registration q4 005-waterproof speakers','registration q4 006-waterproof speakers'))then 'waterproof speakers' when("table1"."description" in('registration q2 000-waterproof makes-450 ratings','registration q2 001-waterproof makes-450 ratings','registration q2 002-waterproof makes-450 ratings','registration q2 005-waterproof makes-450 ratings','registration q2 2021-waterproof makes-450 ratings','registration q4 000-waterproof makes-450 ratings','registration q4 001-waterproof makes-450 ratings','registration q4 002-waterproof makes-450 ratings','registration q4 005-waterproof makes-450 ratings','registration q4 006-waterproof makes-450 ratings'))then 'waterproof makes' when("table1"."description" in('registration q2 001-waterproof propane redsize','registration q2 002-waterproof propane redsize','registration q2 005-waterproof propane redsize','registration q2 2021-waterproof propane redsize','registration q4 001-waterproof propane redsize','registration q4 002-waterproof propane redsize','registration q4 005-waterproof propane redsize','registration q4 006-waterproof propane redsize'))then 'waterproof propane redsize' when("table1"."description" in('registration q2 001-waterproof propane small','registration q2 002-waterproof propane small','registration q2 005-waterproof propane small','registration q4 001-waterproof propane small','registration q4 002-waterproof propane small','registration q4 005-waterproof propane small','registration q4 006-waterproof propane small'))then 'waterproof propane small' when("table1"."description" in('registration q2 000-red-junk-waterproof','registration q2 001-red-junk-waterproof','registration q2 002-red-junk-waterproof','registration q2 005-red-junk-waterproof','registration q2 2021-red-junk-waterproof','registration q4 000-red-junk-waterproof','registration q4 001-red-junk-waterproof','registration q4 002-red-junk-waterproof','registration q4 005-red-junk-waterproof','registration q4 006-red-junk-waterproof'))then 'red-junk waterproof' when("table1"."description"='registration q2 2021-redsize amps')then 'redsize amps' when("table1"."description" in('registration q2 000-near-entry waterproof','registration q2 001-near-entry waterproof','registration q2 002-near-entry waterproof','registration q2 005-near-entry waterproof','registration q2 2021-near-entry waterproof','registration q4 000-near-entry waterproof','registration q4 001-near-entry waterproof','registration q4 002-near-entry waterproof','registration q4 005-near-entry waterproof','registration q4 006-near-entry waterproof'))then 'near-entry waterproof' when("table1"."description" in('registration q2 000-home theaters-smooth','registration q2 001-home theaters-smooth','registration q2 002-home theaters-smooth','registration q2 005-home theaters-smooth','registration q2 2021-home theaters-smooth','registration q4 000-home theaters-smooth','registration q4 001-home theaters-smooth','registration q4 002-home theaters-smooth','registration q4 005-home theaters-smooth','registration q4 006-home theaters-smooth'))then 'home theaters-smooth' when("table1"."description" in('registration q2 000-home theaters-fullsize','registration q2 001-home theaters-fullsize','registration q2 002-home theaters-fullsize','registration q2 005-home theaters-fullsize','registration q2 2021-home theaters-fullsize','registration q4 000-home theaters-fullsize','registration q4 001-home theaters-fullsize','registration q4 002-home theaters-fullsize','registration q4 005-home theaters-fullsize','registration q4 006-home theaters-fullsize'))then 'home theaters-fullsize' when("table1"."description" in('registration q2 000-home theaters-fullsize-half full','registration q2 001-home theaters-fullsize-half full','registration q2 002-home theaters-fullsize-half full','registration q2 005-home theaters-fullsize-half full','registration q2 2021-home theaters-fullsize-half full','registration q4 000-home theaters-fullsize-half full','registration q4 001-home theaters-fullsize-half full','registration q4 002-home theaters-fullsize-half full','registration q4 005-home theaters-fullsize-half full'))then 'home theaters-fullsize-half full' when("table1"."description" in('registration q1 000-small amps','registration q1 001-small amps','registration q1 002-small amps','registration q1 005-small amps','registration q1 006-small amps','registration q1 2021-small amps','registration q2 000-small amps','registration q2 001-small amps','registration q2 002-small amps','registration q2 005-small amps','registration q2 2021-small amps','registration q3 000-small amps','registration q3 001-small amps','registration q3 002-small amps','registration q3 006-small amps','registration q4 000-small amps','registration q4 001-small amps','registration q4 002-small amps','registration q4 005-small amps','registration q4 006-small amps'))then 'small amps' when("table1"."description" in('registration q2 000-small-entry amps-any-american','registration q2 001-small-entry amps-any-american','registration q2 002-small-entry amps-any-american','registration q4 000-small-entry amps-any-american'))then 'small-entry amps-any-american' when("table1"."description" in('registration q2 005-camper yeah-entry alt','registration q4 006-camper yeah-entry alt'))then 'camper yeah-entry alt' when("table1"."description" in('registration q2 2021-camper yeah-fullsize','registration q4 006-camper yeah-fullsize'))then 'camper yeah-fullsize' when("table1"."description" in('registration q2 000-camper yeah-redsize','registration q2 001-camper yeah-redsize','registration q2 002-camper yeah-redsize','registration q2 005-camper yeah-redsize','registration q2 2021-camper yeah-redsize','registration q4 000-camper yeah-redsize','registration q4 001-camper yeah-redsize','registration q4 002-camper yeah-redsize','registration q4 005-camper yeah-redsize','registration q4 006-camper yeah-redsize'))then 'camper yeah-redsize' when("table1"."description" in('registration q1 000-camper yeah-small','registration q1 001-camper yeah-small','registration q1 002-camper yeah-small','registration q1 005-camper yeah-small','registration q1 006-camper yeah-small','registration q1 2021-camper yeah-small','registration q2 000-camper yeah-small','registration q2 001-camper yeah-small','registration q2 002-camper yeah-small','registration q2 005-camper yeah-small','registration q2 2021-camper yeah-small','registration q3 000-camper yeah-small','registration q3 001-camper yeah-small','registration q3 002-camper yeah-small','registration q3 006-camper yeah-small','registration q4 000-camper yeah-small','registration q4 001-camper yeah-small','registration q4 002-camper yeah-small','registration q4 005-camper yeah-small','registration q4 006-camper yeah-small'))then 'camper yeah-small' when("table1"."description" in('registration q2 000-camper yeah-small-any-american','registration q2 001-camper yeah-small-any-american','registration q2 002-camper yeah-small-any-american','registration q4 000-camper yeah-small-any-american'))then 'camper yeah-small-any-american' when("table1"."description" in('registration q2 000-camper yeah-premium redsize alt','registration q4 000-camper yeah-premium redsize alt'))then 'camper yeah premium redsize alt' when("table1"."description" in('registration q2 002-camper wagon','registration q2 005-camper wagon','registration q2 2021-camper wagon','registration q4 002-camper wagon','registration q4 005-camper wagon','registration q4 006-camper wagon'))then 'camper wagon' when("table1"."description" in('registration q2 005-campers amps','registration q2 2021-campers amps','registration q4 005-campers amps','registration q4 006-campers amps'))then 'campers amps' when("table1"."description" in('registration q2 000-campery','registration q2 001-campery','registration q2 002-campery','registration q2 005-campery','registration q2 2021-campery','registration q4 000-campery','registration q4 001-campery','registration q4 002-campery','registration q4 005-campery','registration q4 006-campery'))then 'campery' when("table1"."description" in('registration q1 000-upper reddle alt','registration q1 001-upper reddle alt','registration q1 002-upper reddle alt','registration q1 005-upper reddle alt','registration q1 006-upper reddle alt','registration q1 2021-upper reddle alt','registration q2 000-upper reddle alt','registration q2 001-upper reddle alt','registration q2 002-upper reddle alt','registration q2 005-upper reddle alt','registration q3 000-upper reddle alt','registration q3 001-upper reddle alt','registration q3 002-upper reddle alt','registration q3 006-upper reddle alt','registration q4 000-upper reddle alt','registration q4 001-upper reddle alt','registration q4 002-upper reddle alt','registration q4 005-upper reddle alt','registration q4 006-upper reddle alt'))then 'upper reddle alt' when("table1"."description" in('registration q2 000-upper reddle alt-any-american','registration q2 001-upper reddle alt-any-american','registration q2 002-upper reddle alt-any-american','registration q4 000-upper reddle alt-any-american'))then 'upper reddle alt-any-american' when("table1"."description" in('registration q2 000-fires-smooth','registration q2 001-fires-smooth','registration q2 002-fires-smooth','registration q2 005-fires-smooth','registration q2 2021-fires-smooth','registration q4 000-fires-smooth','registration q4 001-fires-smooth','registration q4 002-fires-smooth','registration q4 005-fires-smooth','registration q4 006-fires-smooth'))then 'fires-smooth' else "table1"."description" end)<='fires-smooth')and((case when("table1"."description" in('registration q1 000-entry amps','registration q1 000-purple device makes-450 ratings','registration q1 000-small amps','registration q1 000-camper yeah-small','registration q1 000-upper reddle alt'))then '000 q1' when("table1"."description" in('registration q2 000-entry alt propane','registration q2 000-entry amps','registration q2 000-fullsize amp alt','registration q2 000-purple device makes-450 ratings','registration q2 000-purple device makes non-waterproof-any-american','registration q2 000-waterproof speakers','registration q2 000-waterproof makes-450 ratings','registration q2 000-red-junk-waterproof','registration q2 000-near-entry waterproof','registration q2 000-home theaters-smooth','registration q2 000-home theaters-fullsize','registration q2 000-home theaters-fullsize-half full','registration q2 000-small amps','registration q2 000-small-entry amps-any-american','registration q2 000-camper yeah-redsize','registration q2 000-camper yeah-premium redsize alt','registration q2 000-camper yeah-small','registration q2 000-camper yeah-small-any-american','registration q2 000-campery','registration q2 000-upper reddle alt','registration q2 000-upper reddle alt-any-american','registration q2 000-fires-smooth'))then '000 q2' when("table1"."description" in('registration q3 000-small amps','registration q3 000-camper yeah-small','registration q3 000-upper reddle alt'))then '000 q3' when("table1"."description" in('registration q4 000-alternative capacitor devices','registration q4 000-entry alt propane','registration q4 000-entry amps','registration q4 000-fullsize amp alt','registration q4 000-purple device makes-450 ratings','registration q4 000-purple device makes non-waterproof-any-american','registration q4 000-waterproof speakers','registration q4 000-waterproof makes-450 ratings','registration q4 000-red-junk-waterproof','registration q4 000-near-entry waterproof','registration q4 000-home theaters-smooth','registration q4 000-home theaters-fullsize','registration q4 000-home theaters-fullsize-half full','registration q4 000-small amps','registration q4 000-small-entry amps-any-american','registration q4 000-camper yeah-redsize','registration q4 000-camper yeah-premium redsize alt','registration q4 000-camper yeah-small','registration q4 000-camper yeah-small-any-american','registration q4 000-campery','registration q4 000-upper reddle alt','registration q4 000-upper reddle alt-any-american','registration q4 000-fires-smooth'))then '000 q4' when("table1"."description" in('registration q1 001-small amps','registration q1 001-camper yeah-small','registration q1 001-upper reddle alt'))then '001 q1' when("table1"."description" in('registration q2 001-alternative capacitor devices-stuff sample','registration q2 001-entry alt propane','registration q2 001-entry amps','registration q2 001-fullsize amp alt','registration q2 001-purple device makes-450 ratings','registration q2 001-purple device makes non-waterproof-any-american','registration q2 001-waterproof speakers','registration q2 001-waterproof makes-450 ratings','registration q2 001-waterproof propane redsize','registration q2 001-waterproof propane small','registration q2 001-red-junk-waterproof','registration q2 001-near-entry waterproof','registration q2 001-home theaters-smooth','registration q2 001-home theaters-fullsize','registration q2 001-home theaters-fullsize-half full','registration q2 001-small amps','registration q2 001-small-entry amps-any-american','registration q2 001-camper yeah-redsize','registration q2 001-camper yeah-small','registration q2 001-camper yeah-small-any-american','registration q2 001-campery','registration q2 001-upper reddle alt','registration q2 001-upper reddle alt-any-american','registration q2 001-fires-smooth'))then '001 q2' when("table1"."description" in('registration q3 001-small amps','registration q3 001-camper yeah-small','registration q3 001-upper reddle alt'))then '001 q3' when("table1"."description" in('registration q4 001-alternative capacitor devices-stuff sample','registration q4 001-entry alt propane','registration q4 001-entry amps','registration q4 001-fullsize amp alt','registration q4 001-purple device makes-450 ratings','registration q4 001-waterproof speakers','registration q4 001-waterproof makes-450 ratings','registration q4 001-waterproof propane redsize','registration q4 001-waterproof propane small','registration q4 001-red-junk-waterproof','registration q4 001-near-entry waterproof','registration q4 001-home theaters-smooth','registration q4 001-home theaters-fullsize','registration q4 001-home theaters-fullsize-half full','registration q4 001-small amps','registration q4 001-camper yeah-redsize','registration q4 001-camper yeah-small','registration q4 001-campery','registration q4 001-upper reddle alt','registration q4 001-fires-smooth'))then '001 q4' when("table1"."description" in('registration q1 002-small amps','registration q1 002-camper yeah-small','registration q1 002-upper reddle alt'))then '002 q1' when("table1"."description" in('registration q2 002-alternative capacitor devices-stuff sample','registration q2 002-entry alt propane','registration q2 002-entry amps','registration q2 002-fullsize amp alt','registration q2 002-purple device makes-450 ratings','registration q2 002-purple device makes non-waterproof-any-american','registration q2 002-waterproof speakers','registration q2 002-waterproof makes-450 ratings','registration q2 002-waterproof propane redsize','registration q2 002-waterproof propane small','registration q2 002-red-junk-waterproof','registration q2 002-near-entry waterproof','registration q2 002-home theaters-smooth','registration q2 002-home theaters-fullsize','registration q2 002-home theaters-fullsize-half full','registration q2 002-small amps','registration q2 002-small-entry amps-any-american','registration q2 002-camper yeah-redsize','registration q2 002-camper yeah-small','registration q2 002-camper yeah-small-any-american','registration q2 002-camper wagon','registration q2 002-campery','registration q2 002-upper reddle alt','registration q2 002-upper reddle alt-any-american','registration q2 002-fires-smooth'))then '002 q2' when("table1"."description" in('registration q3 002-small amps','registration q3 002-camper yeah-small','registration q3 002-upper reddle alt'))then '002 q3' when("table1"."description" in('registration q4 002-alternative capacitor devices-stuff sample','registration q4 002-entry alt propane','registration q4 002-entry amps','registration q4 002-fullsize amp alt','registration q4 002-purple device makes-450 ratings','registration q4 002-waterproof speakers','registration q4 002-waterproof makes-450 ratings','registration q4 002-waterproof propane redsize','registration q4 002-waterproof propane small','registration q4 002-red-junk-waterproof','registration q4 002-near-entry waterproof','registration q4 002-home theaters-smooth','registration q4 002-home theaters-fullsize','registration q4 002-home theaters-fullsize-half full','registration q4 002-small amps','registration q4 002-camper yeah-redsize','registration q4 002-camper yeah-small','registration q4 002-camper wagon','registration q4 002-campery','registration q4 002-upper reddle alt','registration q4 002-fires-smooth'))then '002 q4' when("table1"."description" in('registration q1 005-small amps','registration q1 005-camper yeah-small','registration q1 005-upper reddle alt'))then '005 q1' when("table1"."description" in('registration q2 005-alternative capacitor devices-stuff sample','registration q2 005-entry amps','registration q2 005-fullsize amp alt','registration q2 005-purple device makes-450 ratings','registration q2 005-waterproof speakers','registration q2 005-waterproof makes-450 ratings','registration q2 005-waterproof propane redsize','registration q2 005-waterproof propane small','registration q2 005-red-junk-waterproof','registration q2 005-near-entry waterproof','registration q2 005-home theaters-smooth','registration q2 005-home theaters-fullsize','registration q2 005-home theaters-fullsize-half full','registration q2 005-small amps','registration q2 005-camper yeah-entry alt','registration q2 005-camper yeah-redsize','registration q2 005-camper yeah-small','registration q2 005-camper wagon','registration q2 005-campers amps','registration q2 005-campery','registration q2 005-upper reddle alt','registration q2 005-fires-smooth'))then '005 q2' when("table1"."description" in('registration q4 005-entry amps','registration q4 005-fullsize amp alt','registration q4 005-purple device makes-450 ratings','registration q4 005-waterproof speakers','registration q4 005-waterproof makes-450 ratings','registration q4 005-waterproof propane redsize','registration q4 005-waterproof propane small','registration q4 005-red-junk-waterproof','registration q4 005-near-entry waterproof','registration q4 005-home theaters-smooth','registration q4 005-home theaters-fullsize','registration q4 005-home theaters-fullsize-half full','registration q4 005-small amps','registration q4 005-camper yeah-entry alt','registration q4 005-camper yeah-redsize','registration q4 005-camper yeah-small','registration q4 005-camper wagon','registration q4 005-campers amps','registration q4 005-campery','registration q4 005-upper reddle alt','registration q4 005-fires-smooth'))then '005 q4' when("table1"."description" in('registration q1 006-small amps','registration q1 006-camper yeah-small','registration q1 006-upper reddle alt'))then '006 q1' when("table1"."description" in('registration q3 006-small amps','registration q3 006-camper yeah-small','registration q3 006-upper reddle alt'))then '006 q3' when("table1"."description" in('registration q4 006-alternative capacitor devices-stuff sample','registration q4 006-entry amps','registration q4 006-fullsize amp alt','registration q4 006-purple device makes-450 ratings','registration q4 006-waterproof speakers','registration q4 006-waterproof makes-450 ratings','registration q4 006-waterproof propane redsize','registration q4 006-waterproof propane small','registration q4 006-red-junk-waterproof','registration q4 006-near-entry waterproof','registration q4 006-home theaters-smooth','registration q4 006-home theaters-fullsize','registration q4 006-small amps','registration q4 006-camper yeah-entry alt','registration q4 006-camper yeah-fullsize','registration q4 006-camper yeah-redsize','registration q4 006-camper yeah-small','registration q4 006-camper wagon','registration q4 006-campers amps','registration q4 006-campery','registration q4 006-upper reddle alt','registration q4 006-fires-smooth'))then '006 q4' when("table1"."description" in('registration q1 2021-small amps','registration q1 2021-camper yeah-small','registration q1 2021-upper reddle alt'))then '2021 q1' when("table1"."description" in('registration q2 2021-alternative capacitor devices-stuff sample','registration q2 2021-fullsize amp alt','registration q2 2021-purple device makes-450 ratings','registration q2 2021-waterproof speakers','registration q2 2021-waterproof makes-450 ratings','registration q2 2021-waterproof propane redsize','registration q2 2021-red-junk-waterproof','registration q2 2021-redsize amps','registration q2 2021-near-entry waterproof','registration q2 2021-home theaters-smooth','registration q2 2021-home theaters-fullsize','registration q2 2021-home theaters-fullsize-half full','registration q2 2021-small amps','registration q2 2021-camper yeah-fullsize','registration q2 2021-camper yeah-redsize','registration q2 2021-camper yeah-small','registration q2 2021-camper wagon','registration q2 2021-campers amps','registration q2 2021-campery','registration q2 2021-fires-smooth'))then '2021 q2' else "table1"."description" end)='2021 q2')and((case when("table1"."code"='no answer')then 0 else 1 end)<>0)and("table1"."description"='familiar with(g1)'))group by 1,2,3
;

-- testPerformanceIssue1438
select*from table_1 t1 where(((t1.col1='value2')and(t1.cal2='value2'))and(((1=1)and((((((t1.id in(940550,940600,940650,940700,940750,940800,940850,940900,940950,941000,941050,941100,941150,941200,941250,941300,941350,941400,941450,941500,941550,941600,941650,941700,941750,941800,941850,941900,941950,942000,942050,942100,942150,942200,942250,942300,942350,942400,942450,942500,942550,942600,942650,942700,942750,942800,942850,942900,942950,943000,943050,943100,943150,943200,943250,943300,943350,943400,943450,943500,943550,943600,943650,943700,943750,943800,943850,943900,943950,944000,944050,944100,944150,944200,944250,944300,944350,944400,944450,944500,944550,944600,944650,944700,944750,944800,944850,944900,944950,945000,945050,945100,945150,945200,945250,945300))or(t1.id in(945350,945400,945450,945500,945550,945600,945650,945700,945750,945800,945850,945900,945950,946000,946050,946100,946150,946200,946250,946300,946350,946400,946450,946500,946550,946600,946650,946700,946750,946800,946850,946900,946950,947000,947050,947100,947150,947200,947250,947300,947350,947400,947450,947500,947550,947600,947650,947700,947750,947800,947850,947900,947950,948000,948050,948100,948150,948200,948250,948300,948350,948400,948450,948500,948550,948600,948650,948700,948750,948800,948850,948900,948950,949000,949050,949100,949150,949200,949250,949300,949350,949400,949450,949500,949550,949600,949650,949700,949750,949800,949850,949900,949950,950000,950050,950100)))or(t1.id in(950150,950200,950250,950300,950350,950400,950450,950500,950550,950600,950650,950700,950750,950800,950850,950900,950950,951000,951050,951100,951150,951200,951250,951300,951350,951400,951450,951500,951550,951600,951650,951700,951750,951800,951850,951900,951950,952000,952050,952100,952150,952200,952250,952300,952350,952400,952450,952500,952550,952600,952650,952700,952750,952800,952850,952900,952950,953000,953050,953100,953150,953200,953250,953300,953350,953400,953450,953500,953550,953600,953650,953700)))or(t1.id in(953750,953800,953850,953900,953950,954000,954050,954100,954150,954200,954250,954300,954350,954400,954450,954500,954550,954600,954650,954700,954750,954800,954850,954900,954950,955000,955050,955100,955150,955200,955250,955300,955350,955400,955450,955500,955550,955600,955650,955700,955750,955800,955850,955900,955950,956000,956050,956100,956150,956200,956250,956300,956350,956400,956450,956500,956550,956600,956650,956700,956750,956800,956850,956900,956950,957000,957050,957100,957150,957200,957250,957300)))or(t1.id in(944100,944150,944200,944250,944300,944350,944400,944450,944500,944550,944600,944650,944700,944750,944800,944850,944900,944950,945000)))or(t1.id in(957350,957400,957450,957500,957550,957600,957650,957700,957750,957800,957850,957900,957950,958000,958050,958100,958150,958200,958250,958300,958350,958400,958450,958500,958550,958600,958650,958700,958750,958800,958850,958900,958950,959000,959050,959100,959150,959200,959250,959300,959350,959400,959450,959500,959550,959600,959650,959700,959750,959800,959850,959900,959950,960000,960050,960100,960150,960200,960250,960300,960350,960400,960450,960500,960550,960600,960650,960700,960750,960800,960850,960900,960950,961000,961050,961100,961150,961200,961250,961300,961350,961400,961450,961500,961550,961600,961650,961700,961750,961800,961850,961900,961950,962000,962050,962100))))or(t1.id in(962150,962200,962250,962300,962350,962400,962450,962500,962550,962600,962650,962700,962750,962800,962850,962900,962950,963000,963050,963100,963150,963200,963250,963300,963350,963400,963450,963500,963550,963600,963650,963700,963750,963800,963850,963900,963950,964000,964050,964100,964150,964200,964250,964300,964350,964400,964450,964500,964550,964600,964650,964700,964750,964800,964850,964900,964950,965000,965050,965100,965150,965200,965250,965300,965350,965400,965450,965500))))and t1.col3 in(select t2.col3 from table_6 t6,table_1 t5,table_4 t4,table_3 t3,table_1 t2 where(((((((t5.cal3=t6.id)and(t5.cal5=t6.cal5))and(t5.cal1=t6.cal1))and(t3.cal1 in(108500)))and(t5.id=t2.id))and not((t6.cal6 in('value'))))and((t2.id=t3.cal2)and(t4.id=t3.cal3))))order by t1.id asc
;

-- testSqlContainIsNullFunctionShouldBeParsed3
SELECT name, age FROM person WHERE NOT ISNULL(home, 'earn more money')
;

-- testWithUnionProblem3
WITH test AS ((SELECT mslink, CAST(tablea.fname AS varchar) FROM tablea INNER JOIN tableb ON tablea.mslink = tableb.mslink AND tableb.deleted = 0 WHERE tablea.fname IS NULL AND 1 = 0) UNION ALL (SELECT mslink FROM tableb)) SELECT * FROM tablea WHERE mslink IN (SELECT mslink FROM test)
;

-- testWithUnionProblem4
WITH hist AS ((SELECT gl.mslink, ba.gl_name AS txt, ba.gl_nummer AS nr, 0 AS level, CAST(gl.mslink AS VARCHAR) AS path, ae.feature FROM tablea AS gl INNER JOIN tableb AS ba ON gl.mslink = ba.gl_mslink INNER JOIN tablec AS ae ON gl.mslink = ae.mslink AND ae.deleted = 0 WHERE gl.parent IS NULL AND gl.mslink <> 0) UNION ALL (SELECT gl.mslink, ba.gl_name AS txt, ba.gl_nummer AS nr, hist.level + 1 AS level, CAST(hist.path + '.' + CAST(gl.mslink AS VARCHAR) AS VARCHAR) AS path, ae.feature FROM tablea AS gl INNER JOIN tableb AS ba ON gl.mslink = ba.gl_mslink INNER JOIN tablec AS ae ON gl.mslink = ae.mslink AND ae.deleted = 0 INNER JOIN hist ON gl.parent = hist.mslink WHERE gl.mslink <> 0)) SELECT mslink, space(level * 4) + txt AS txt, nr, feature, path FROM hist WHERE EXISTS (SELECT feature FROM tablec WHERE mslink = 0 AND ((feature IN (1, 2) AND hist.feature = 3) OR (feature IN (4) AND hist.feature = 2)))
;

-- testWithUnionProblem5
WITH hist AS ((SELECT gl.mslink, ba.gl_name AS txt, ba.gl_nummer AS nr, 0 AS level, CAST(gl.mslink AS VARCHAR) AS path, ae.feature FROM tablea AS gl INNER JOIN tableb AS ba ON gl.mslink = ba.gl_mslink INNER JOIN tablec AS ae ON gl.mslink = ae.mslink AND ae.deleted = 0 WHERE gl.parent IS NULL AND gl.mslink <> 0) UNION ALL (SELECT gl.mslink, ba.gl_name AS txt, ba.gl_nummer AS nr, hist.level + 1 AS level, CAST(hist.path + '.' + CAST(gl.mslink AS VARCHAR) AS VARCHAR) AS path, 5 AS feature FROM tablea AS gl INNER JOIN tableb AS ba ON gl.mslink = ba.gl_mslink INNER JOIN tablec AS ae ON gl.mslink = ae.mslink AND ae.deleted = 0 INNER JOIN hist ON gl.parent = hist.mslink WHERE gl.mslink <> 0)) SELECT * FROM hist
;

-- testOracleDBLink
select*from tablename@dblink
;

-- testIssue1062_2
SELECT * FROM mytable WHERE temperature.timestamp <= @until AND temperature.timestamp >= @from
;

-- testGroupingSets1
SELECT COL_1, COL_2, COL_3, COL_4, COL_5, COL_6 FROM TABLE_1 GROUP BY GROUPING SETS ((COL_1, COL_2, COL_3, COL_4), (COL_5, COL_6))
;

-- testGroupingSets2
SELECT COL_1 FROM TABLE_1 GROUP BY GROUPING SETS (COL_1)
;

-- testGroupingSets3
SELECT COL_1 FROM TABLE_1 GROUP BY GROUPING SETS (COL_1, ())
;

-- testPartitionByWithBracketsIssue865
SELECT subject_id, student_id, sum(mark) OVER (PARTITION BY subject_id, student_id ) FROM marks
;

-- testPartitionByWithBracketsIssue865
SELECT subject_id, student_id, sum(mark) OVER (PARTITION BY (subject_id, student_id) ) FROM marks
;

-- testIssue582NumericConstants
SELECT x'009fd'
;

-- testIssue582NumericConstants
SELECT X'009fd'
;

-- testMultiColumnAliasIssue849_2
SELECT * FROM crosstab('select rowid, attribute, value from ct where attribute = ''att2'' or attribute = ''att3'' order by 1,2') AS ct(row_name text, category_1 text, category_2 text, category_3 text)
;

-- testValues2
SELECT * FROM (VALUES 1, 2, 3, 4) AS test
;

-- testValues3
SELECT * FROM (VALUES 1, 2, 3, 4) AS test(a)
;

-- testValues4
SELECT * FROM (VALUES (1, 2), (3, 4)) AS test(a, b)
;

-- testValues5
SELECT X, Y FROM (VALUES (0, 'a'), (1, 'b')) AS MY_TEMP_TABLE(X, Y)
;

-- testDB2SpecialRegisterDateTimeIssue1249
select*from test.abc where col>current_time
;

-- testDB2SpecialRegisterDateTimeIssue1249
select*from test.abc where col>current time
;

-- testDB2SpecialRegisterDateTimeIssue1249
select*from test.abc where col>current_timestamp
;

-- testDB2SpecialRegisterDateTimeIssue1249
select*from test.abc where col>current timestamp
;

-- testDB2SpecialRegisterDateTimeIssue1249
select*from test.abc where col>current_date
;

-- testDB2SpecialRegisterDateTimeIssue1249
select*from test.abc where col>current date
;

-- testMysqlIndexHints
SELECT column FROM testtable AS t0 USE INDEX (index1)
;

-- testMysqlIndexHints
SELECT column FROM testtable AS t0 IGNORE INDEX (index1)
;

-- testMysqlIndexHints
SELECT column FROM testtable AS t0 FORCE INDEX (index1)
;

-- testSignedKeywordIssue1100
SELECT signed, unsigned FROM mytable
;

-- testKeywordTableIssue261
SELECT column_value FROM table(VARCHAR_LIST_TYPE())
;

-- testTopExpressionIssue243_2
SELECT TOP (CAST(? AS INT)) * FROM MyTable
;

-- testProblemKeywordCommitIssue341
SELECT id, commit FROM table1
;

-- testCaseThenCondition
SELECT * FROM mytable WHERE CASE WHEN a = 'c' THEN a IN (1, 2, 3) END = 1
;

-- testGroupBy
SELECT * FROM tab1 WHERE a > 34 GROUP BY tab1.b
;

-- testGroupBy
SELECT * FROM tab1 WHERE a > 34 GROUP BY 2, 3
;

-- testWithInsideWithIssue1186
with teststmt1 as(with teststmt2 as(select*from my_table2)select col1,col2 from teststmt2)select*from teststmt
;

-- testIssue567KeywordPrimary
SELECT primary, secondary FROM info
;

-- testOrderKeywordIssue932_2
SELECT group FROM tmp3
;

-- testOrderKeywordIssue932_2
SELECT tmp3.group FROM tmp3
;

-- testNestedFunctionCallIssue253
SELECT (replace_regex(replace_regex(replace_regex(get_json_string(a_column, 'value'), '\n', ' '), '\r', ' '), '\\', '\\\\')) FROM a_table WHERE b_column = 'value'
;

-- testProblemIssue437Index
select count(id)from p_custom_data ignore index(pri)where tenant_id=28257 and entity_id=92609 and delete_flg=0 and((dbc_relation_2=52701)and(dbc_relation_2 in(select id from a_order where tenant_id=28257 and 1=1)))order by id desc,id desc
;

-- testProblemInNotInProblemIssue379
SELECT rank FROM DBObjects WHERE rank NOT IN (0, 1)
;

-- testProblemInNotInProblemIssue379
SELECT rank FROM DBObjects WHERE rank IN (0, 1)
;

-- testProblematicDeparsingIssue1183_2
SELECT ARRAY_AGG(ID ORDER BY ID) OVER (ORDER BY ID)
;

-- testMultiPartColumnNameWithTableName
SELECT tableName.columnName FROM tableName
;

-- testTableSpaceKeyword
select ddf.tablespace tablespace_name,maxtotal/1024/1024 "max_mb",(total-free)/1024/1024 "used_mb",(maxtotal-(total-free))/1024/1024 "available_mb",total/1024/1024 "allocated_mb",free/1024/1024 "allocated_free_mb",((total-free)/maxtotal*100)"used_perc",cnt "file_count" from(select tablespace_name tablespace,sum(bytes)total,sum(greatest(maxbytes,bytes))maxtotal,count(*)cnt from dba_data_files group by tablespace_name)ddf,(select tablespace_name tablespace,sum(bytes)free,max(bytes)maxf from dba_free_space group by tablespace_name)dfs where ddf.tablespace=dfs.tablespace order by 1 desc
;

-- testMultiPartTableNameWithDatabaseName
SELECT columnName FROM databaseName..tableName
;

-- testLimitOffsetIssue462
SELECT * FROM mytable LIMIT ?1
;

-- testBooleanValue
SELECT col FROM t WHERE a
;

-- testTableFunctionWithParams
SELECT f2 FROM SOME_FUNCTION(1, 'val')
;

-- testTimezoneExpressionWithTwoTransformations
SELECT DATE(date1 AT TIME ZONE 'UTC' AT TIME ZONE 'australia/sydney') AS another_date
;

-- testCollateExprIssue164
SELECT u.name COLLATE Latin1_General_CI_AS AS User FROM users u
;

-- testEmptyDoubleQuotes
SELECT * FROM mytable WHERE col = ""
;

-- testLimitSqlServer1
SELECT * FROM mytable WHERE mytable.col = 9 ORDER BY mytable.id OFFSET 3 ROWS FETCH NEXT 5 ROWS ONLY
;

-- testLimitSqlServer2
SELECT * FROM mytable WHERE mytable.col = 9 ORDER BY mytable.id OFFSET 3 ROW FETCH FIRST 5 ROW ONLY
;

-- testLimitSqlServer3
SELECT * FROM mytable WHERE mytable.col = 9 ORDER BY mytable.id OFFSET 3 ROWS
;

-- testLimitSqlServer4
SELECT * FROM mytable WHERE mytable.col = 9 ORDER BY mytable.id FETCH NEXT 5 ROWS ONLY
;

-- testIsNotFalse
SELECT col FROM tbl WHERE col IS NOT FALSE
;

-- testDistinctWithFollowingBrackets
SELECT DISTINCT (phone), name FROM admin_user
;

-- testPivotFunction
SELECT to_char((SELECT col1 FROM (SELECT times_purchased, state_code FROM customers t) PIVOT (count(state_code) FOR state_code IN ('NY', 'CT', 'NJ', 'FL', 'MO')) ORDER BY times_purchased)) FROM DUAL
;

-- testStartKeyword
SELECT c0_.start AS start_5 FROM mytable
;

-- testMultiPartTableNameWithServerNameAndDatabaseName
SELECT columnName FROM [server-name\server-instance].databaseName..tableName
;

-- testConcatProblem2_1
SELECT TO_CHAR(SPA.AANLEVERPERIODEVOLGNR, 'FM09'::VARCHAR) FROM testtable
;

-- testConcatProblem2_2
SELECT MAX((SPA.SOORTAANLEVERPERIODE)::VARCHAR (2) || (VARCHAR(SPA.AANLEVERPERIODEJAAR))::VARCHAR (4)) AS GESLACHT_TMP FROM testtable
;

-- testConcatProblem2_3
SELECT TO_CHAR((10000 - SPA.VERSCHIJNINGSVOLGNR), 'FM0999'::VARCHAR) FROM testtable
;

-- testConcatProblem2_4
SELECT (SPA.GESLACHT)::VARCHAR (1) FROM testtable
;

-- testConcatProblem2_5
SELECT max((a || b) || c) FROM testtable
;

-- testConcatProblem2_6
SELECT max(a || b || c) FROM testtable
;

-- testUnPivotWithMultiColumn
SELECT * FROM sale_stats UNPIVOT ((quantity, rank) FOR product_code IN ((product_a, product_1) AS 'A', (product_b, product_2) AS 'B', (product_c, product_3) AS 'C'))
;

-- testMultiPartColumnNameWithDatabaseNameAndSchemaNameAndTableName
SELECT databaseName.schemaName.tableName.columnName FROM tableName
;

-- testMultiPartColumnNameWithDatabaseNameAndTableName
SELECT databaseName..tableName.columnName FROM tableName
;

-- testNamedWindowDefinitionIssue1581_2
SELECT sum(salary) OVER w1, avg(salary) OVER w2 FROM empsalary WINDOW w1 AS (PARTITION BY depname ORDER BY salary DESC), w2 AS (PARTITION BY depname2 ORDER BY salary2)
;

-- testChangeKeywordIssue859
SELECT * FROM CHANGE.TEST
;

-- testSignedKeywordIssue995
SELECT leading FROM prd_reprint
;

-- testPostgresNaturalJoinIssue1559
select t1.id,t1.name,t2.did,t2.name from table1 as t1 natural right join table2 as t2
;

-- testPostgresNaturalJoinIssue1559
select t1.id,t1.name,t2.did,t2.name from table1 as t1 natural right join table2 as t2
;

-- testSelectInnerWithAndUnionIssue1084_2
WITH actor AS (SELECT 'b' aid FROM DUAL) SELECT aid FROM actor UNION SELECT aid FROM actor2
;

-- testIssue230_cascadeKeyword
SELECT t.cascade AS cas FROM t
;

-- testReplaceAsFunction
SELECT REPLACE(a, 'b', c) FROM tab1
;

-- testSelectJPQLPositionalParameter
SELECT email FROM users WHERE (type LIKE 'B') AND (username LIKE ?1)
;

-- testAndOperator
SELECT name FROM customers WHERE name = 'John' && lastname = 'Doh'
;

-- testSelectForUpdate2
SELECT * FROM emp WHERE empno = ? FOR UPDATE
;

-- testKeyWorkReplaceIssue393
SELECT replace("aaaabbb", 4, 4, "****")
;

-- testSimilarToIssue789_2
SELECT * FROM mytable WHERE (w_id NOT SIMILAR TO '/foo/__/bar/(left|right)/[0-9]{4}-[0-9]{2}-[0-9]{2}(/[0-9]*)?')
;

-- testKeywordSkipIssue1136
SELECT skip
;

-- testIssue217_keywordSeparator
SELECT Separator
;

-- testNotWithoutParenthesisIssue234
SELECT count(*) FROM "Persons" WHERE NOT "F_NAME" = 'John'
;

-- testSelectWithinGroup
SELECT LISTAGG(col1, '##') WITHIN GROUP (ORDER BY col1) FROM table1
;

-- testMultiPartNamesForFunctionsIssue944
SELECT pg_catalog.now()
;

-- testNotEqualsTo
SELECT * FROM foo WHERE a != b
;

-- testNotEqualsTo
SELECT * FROM foo WHERE a <> b
;

-- testSelectStatementWithoutForUpdateAndSkipLockedTokens
SELECT * FROM test
;

-- testSelectForUpdate
SELECT * FROM user_table FOR UPDATE
;

-- testSelectStatementWithForUpdateButWithoutSkipLockedTokens
SELECT * FROM test FOR UPDATE
;

-- testMultiPartColumnNameWithDatabaseNameAndSchemaName
SELECT databaseName.schemaName..columnName FROM tableName
;

-- testGroupByProblemIssue482
SELECT SUM(orderTotalValue) AS value, MONTH(invoiceDate) AS month, YEAR(invoiceDate) AS year FROM invoice.Invoices WHERE projectID = 1 GROUP BY MONTH(invoiceDate), YEAR(invoiceDate) ORDER BY YEAR(invoiceDate) DESC, MONTH(invoiceDate) DESC
;

-- testIssue266KeywordTop
SELECT @top
;

-- testIssue266KeywordTop
SELECT @TOP
;

-- testProblemSqlAnalytic10Lag
SELECT a, lag(a, 1) OVER (PARTITION BY c ORDER BY a, b) AS n FROM table1
;

-- testProblemSqlAnalytic11Lag
SELECT a, lag(a, 1, 0) OVER (PARTITION BY c ORDER BY a, b) AS n FROM table1
;

-- testMatches
SELECT * FROM team WHERE team.search_column @@ to_tsquery('new & york & yankees')
;

-- testSeveralColumnsFullTextSearchMySQL
SELECT MATCH (col1,col2,col3) AGAINST ('test' IN NATURAL LANGUAGE MODE) relevance FROM tbl
;

-- testAnalyticFunctionProblem1
SELECT last_value(s.revenue_hold) OVER (PARTITION BY s.id_d_insertion_order, s.id_d_product_ad_attr, trunc(s.date_id, 'mm') ORDER BY s.date_id) AS col FROM s
;

-- testStraightJoinInSelect
SELECT STRAIGHT_JOIN col, col2 FROM tbl INNER JOIN tbl2 ON tbl.id = tbl2.id
;

-- testIssue77_singleQuoteEscape2
select 'test\'' from dual
;

-- testUnionWithBracketsAndOrderBy
(SELECT a FROM tbl ORDER BY a) UNION DISTINCT (SELECT a FROM tbl ORDER BY a)
;

-- testDateArithmentic10
select current_date+case when cast(rand()*3 as integer)=1 then 100 else 0 end day as new_date from mytable
;

-- testDateArithmentic11
select current_date+(dayofweek(my_due_date)+5)day from mytable
;

-- testDateArithmentic12
select case when cast(rand()*3 as integer)=1 then null else current_date+(month_offset month)end from mytable
;

-- testDateArithmentic13
SELECT INTERVAL 5 MONTH MONTH FROM mytable
;

-- testJoinerExpressionIssue596
SELECT * FROM a JOIN (b JOIN c ON b.id = c.id) ON a.id = c.id
;

-- testFunctions
SELECT MAX(id) AS max FROM mytable WHERE mytable.col = 9
;

-- testFunctions
SELECT substring(id, 2, 3), substring(id from 2 for 3), substring(id from 2), trim(BOTH ' ' from 'foo bar '), trim(LEADING ' ' from 'foo bar '), trim(TRAILING ' ' from 'foo bar '), trim(' ' from 'foo bar '), position('foo' in 'bar'), overlay('foo' placing 'bar' from 1), overlay('foo' placing 'bar' from 1 for 2) FROM my table
;

-- testFunctions
SELECT MAX(id), AVG(pro) AS myavg FROM mytable WHERE mytable.col = 9 GROUP BY pro
;

-- testFunctions
SELECT MAX(a, b, c), COUNT(*), D FROM tab1 GROUP BY D
;

-- testFunctions
SELECT {fn MAX(a, b, c)}, COUNT(*), D FROM tab1 GROUP BY D
;

-- testFunctions
SELECT ab.MAX(a, b, c), cd.COUNT(*), D FROM tab1 GROUP BY D
;

-- testIssue371SimplifiedCase2
SELECT CASE col > 4 WHEN true THEN 1 ELSE 0 END
;

-- testKeywordAtIssue1414
SELECT * FROM table1 at
;

-- testReservedKeywordsIssue1352
select system from b1.system
;

-- testReservedKeywordsIssue1352
select query from query.query
;

-- testReservedKeywordsIssue1352
select fulltext from fulltext.fulltext
;

-- testIssue563MultiSubJoin
SELECT c FROM ((SELECT a FROM t) JOIN (SELECT b FROM t2) ON a = B JOIN (SELECT c FROM t3) ON b = c)
;

-- testNotVariantIssue850
SELECT * FROM mytable WHERE id = 1 AND ! (id = 1 AND id = 2)
;

-- testProblemIsIssue331
select c_doctype.c_doctype_id,null,coalesce(c_doctype_trl.name,c_doctype.name)as name,c_doctype.isactive from c_doctype left join c_doctype_trl on(c_doctype.c_doctype_id=c_doctype_trl.c_doctype_id and c_doctype_trl.ad_language='es_ar')where c_doctype.ad_client_id=1010016 and c_doctype.ad_client_id in(0,1010016)and c_doctype.c_doctype_id in(select c_doctype2.c_doctype_id from c_doctype as c_doctype2 where substring(c_doctype2.printname,6,length(c_doctype2.printname))=(select letra from c_letra_comprobante as clc where clc.c_letra_comprobante_id=1010039))and((1010094!=0 and c_doctype.ad_org_id=1010094)or 1010094=0)order by 3 limit 2000
;

-- testTableFunctionInExprIssue923
SELECT * FROM mytable WHERE func(a) IN func(b)
;

-- testProblemFunction2
SELECT sysdate FROM testtable
;

-- testProblemFunction3
SELECT TRUNCATE(col) FROM testtable
;

-- testNotRegexpMySQLIssue887_2
SELECT * FROM mytable WHERE NOT first_name REGEXP '^Ste(v|ph)en$'
;

-- testSelectOrderHaving
SELECT units, count(units) AS num FROM currency GROUP BY units HAVING count(units) > 1 ORDER BY num
;

-- testAnalyticFunction12
SELECT SUM(a) OVER (PARTITION BY b ORDER BY c) FROM tab1
;

-- testAnalyticFunction13
SELECT SUM(a) OVER () FROM tab1
;

-- testAnalyticFunction14
SELECT SUM(a) OVER (PARTITION BY b ) FROM tab1
;

-- testAnalyticFunction15
SELECT SUM(a) OVER (ORDER BY c) FROM tab1
;

-- testAnalyticFunction16
SELECT SUM(a) OVER (ORDER BY c NULLS FIRST) FROM tab1
;

-- testAnalyticFunction17
SELECT AVG(sal) OVER (PARTITION BY deptno ORDER BY sal ROWS BETWEEN 0 PRECEDING AND 0 PRECEDING) AS avg_of_current_sal FROM emp
;

-- testAnalyticFunction18
SELECT AVG(sal) OVER (PARTITION BY deptno ORDER BY sal RANGE CURRENT ROW) AS avg_of_current_sal FROM emp
;

-- testAnalyticFunction19
SELECT count(DISTINCT CASE WHEN client_organic_search_drop_flag = 1 THEN brand END) OVER (PARTITION BY client, category_1, category_2, category_3, category_4 ) AS client_brand_org_drop_count FROM sometable
;

-- testFunctionOrderBy
SELECT array_agg(DISTINCT s ORDER BY b)[1] FROM t
;

-- testMultiPartTableNameWithServerNameAndDatabaseNameAndSchemaName
SELECT columnName FROM [server-name\server-instance].databaseName.schemaName.tableName
;

-- testMultiValueIn_withAnd
SELECT * FROM mytable WHERE (SSN, SSM) IN (('11111111111111', '22222222222222')) AND 1 = 1
;

-- testLoclTimezone1471
SELECT TO_CHAR(CAST(SYSDATE AS TIMESTAMP WITH LOCAL TIME ZONE), 'HH:MI:SS AM TZD') FROM DUAL
;

-- testEscapedBackslashIssue253
SELECT replace_regex('test', '\\', '\\\\')
;

-- testCastToSigned
SELECT CAST(contact_id AS SIGNED) FROM contact WHERE contact_id = 20
;

-- testBrackets
SELECT table_a.name AS [Test] FROM table_a
;

-- testWithStatement
WITH test AS (SELECT mslink FROM feature) SELECT * FROM feature WHERE mslink IN (SELECT mslink FROM test)
;

-- testIssue154_2
SELECT r.id, r.uuid, r.name, r.system_role FROM role r WHERE r.merchant_id = ? AND r.deleted_time IS NULL ORDER BY r.id DESC
;

-- testIssue512_2
SELECT * FROM $tab1
;

-- testIssue512_2
SELECT * FROM #$tab#tab1
;

-- testIssue512_2
SELECT * FROM #$tab1#
;

-- testIssue512_2
SELECT * FROM $#tab1#
;

-- testIssue522_2
SELECT -1 * SIGN(mr.quantity_issued) FROM mytable
;

-- testIssue522_3
select case sign(mr.required_quantity)when-1*sign(mr.quantity_issued)then mr.required_quantity-mr.quantity_issued else 5 end quantity_open from mytable
;

-- testIssue522_4
SELECT CASE a + b WHEN -1 * 5 THEN 1 ELSE CASE b + c WHEN -1 * 6 THEN 2 ELSE 3 END END
;

-- testIssue842_2
SELECT INTERVAL a.repayment_period DAY
;

-- testIssue848_2
SELECT IF(USER_ID > 10, 1, 0)
;

-- testIssue848_3
SELECT c1, multiset(SELECT * FROM mytable WHERE cond = 10) FROM T1 WHERE cond2 = 20
;

-- testIssue848_4
select c1 from t1 where somefunc(select f1 from t2 where t2.id=t1.key)=10
;

-- testChainedunctions
SELECT func('').func2('') AS foo FROM some_tables
;

-- testTimestamp
SELECT * FROM tab1 WHERE a > {ts '2004-04-30 04:05:34.56'}
;

-- testProblemSqlAnalytic5AggregateColumnValue
SELECT a, sum(b) OVER () AS n FROM table1
;

-- testNotRegexpMySQLIssue887
SELECT * FROM mytable WHERE first_name NOT REGEXP '^Ste(v|ph)en$'
;

-- testMultiPartTableNameWithColumnName
SELECT columnName FROM tableName
;

-- testFunctionComplexExpressionParametersIssue1644
select test(1=1,'a','b')
;

-- testFunctionComplexExpressionParametersIssue1644
select if(instr('avc','a')=0,'avc','aaa')
;

-- testTryCastInTryCast2
SELECT TRY_CAST('test' + TRY_CAST(assertEqual AS numeric) AS varchar) FROM tabelle1
;

-- testFuncConditionParameter
SELECT if(a < b)
;

-- testCastVarCharMaxIssue245
SELECT CAST('foo' AS NVARCHAR (MAX))
;

-- testFunctionLeft
SELECT left(table1.col1, 4) FROM table1
;

-- testSelectRowElement
SELECT (t.tup).id, (tup).name FROM t WHERE (t.tup).id IN (1, 2, 3)
;

-- testIssue1062
SELECT * FROM mytable WHERE temperature.timestamp <= @to AND temperature.timestamp >= @from
;

-- testIssue1068
SELECT t2.c AS div
;

-- testIssue1595
SELECT [id] FROM [guest].[12tableName]
;

-- testCharacterSetClause
SELECT DISTINCT CAST(`view0`.`nick2` AS CHAR (8000) CHARACTER SET utf8) AS `v0` FROM people `view0` WHERE `view0`.`nick2` IS NOT NULL
;

-- testJsonExpressionWithCastExpression
SELECT id FROM tbl WHERE p.company::json->'info'->>'country' = 'test'
;

-- testKeywordSynonymIssue1211
select businessdate as "bd",synonym as "synonym" from sc.tab
;

-- testComplexUnion1
(SELECT 'abc-' || coalesce(mytab.a::varchar, '') AS a, mytab.b, mytab.c AS st, mytab.d, mytab.e FROM mytab WHERE mytab.del = 0) UNION (SELECT 'cde-' || coalesce(mytab2.a::varchar, '') AS a, mytab2.b, mytab2.bezeichnung AS c, 0 AS d, 0 AS e FROM mytab2 WHERE mytab2.del = 0)
;

-- testCastToRowConstructorIssue1267
select cast(row(dataid,value,calcmark)as row(datapointid char,value char,calcmark char))as datapoints
;

-- testSqlServerHintsWithIndexIssue915_2
SELECT 1 FROM tableName1 AS t1 WITH (INDEX (idx1)) JOIN tableName2 AS t2 WITH (INDEX (idx2)) ON t1.id = t2.id
;

-- testProblemSqlFuncParamIssue605
select p.id,pt.name,array_to_string(array(select pc.name from product_category pc),',')as categories from product p
;

-- testQuotedCastExpression
SELECT col FROM test WHERE status = CASE WHEN anothercol = 5 THEN 'pending'::"enum_test" END
;

-- testProblemSqlIssue330
select count(*)from c_invoice where issotrx='y' and(processed='n' or updated>(current_timestamp-cast('90 days' as interval)))and c_invoice.ad_client_id in(0,1010016)and c_invoice.ad_org_id in(0,1010053,1010095,1010094)
;

-- testProblemSqlIssue352
select @rowno from(select @rowno from dual)r
;

-- testProblemSqlIssue603
SELECT CASE WHEN MAX(CAST(a.jobNum AS INTEGER)) IS NULL THEN '1000' ELSE MAX(CAST(a.jobNum AS INTEGER)) + 1 END FROM user_employee a
;

-- testProblemSqlAnalytic6AggregateColumnValue
SELECT a, sum(b + 5) OVER (ORDER BY a) AS n FROM table1
;

-- testOracleJoinIssue318
SELECT * FROM TBL_A, TBL_B, TBL_C WHERE TBL_A.ID(+) = TBL_B.ID AND TBL_C.ROOM(+) = TBL_B.ROOM
;

-- testSelectConditionsIssue720And991
SELECT column IS NOT NULL FROM table
;

-- testSelectConditionsIssue720And991
SELECT 0 IS NULL
;

-- testSelectConditionsIssue720And991
SELECT 1 + 2
;

-- testSelectConditionsIssue720And991
SELECT 1 < 2
;

-- testSelectConditionsIssue720And991
SELECT 1 > 2
;

-- testSelectConditionsIssue720And991
SELECT 1 + 2 AS a, 3 < 4 AS b
;

-- testSelectConditionsIssue720And991
SELECT 1 < 2 AS a, 0 IS NULL AS b
;

-- testCaseThenCondition2
SELECT * FROM mytable WHERE CASE WHEN a = 'c' THEN a IN (1, 2, 3) END
;

-- testCaseThenCondition3
SELECT CASE WHEN a > 0 THEN b + a ELSE 0 END p FROM mytable
;

-- testCaseThenCondition4
SELECT * FROM col WHERE CASE WHEN a = 'c' THEN a IN (SELECT id FROM mytable) END
;

-- testCaseThenCondition5
SELECT * FROM col WHERE CASE WHEN a = 'c' THEN a IN (SELECT id FROM mytable) ELSE b IN (SELECT id FROM mytable) END
;

-- testTopKeyWord2
SELECT top.date
;

-- testTopKeyWord3
SELECT * FROM mytable top
;

-- testExpressionsInCaseBeforeWhen
SELECT a FROM tbl1 LEFT JOIN tbl2 ON CASE tbl1.col1 WHEN tbl1.col1 = 1 THEN tbl1.col2 = tbl2.col2 ELSE tbl1.col3 = tbl2.col3 END
;

-- testAnalyticFunctionFilterIssue866
SELECT COUNT(*) FILTER (WHERE name = 'Raj') OVER (PARTITION BY name ) FROM table
;

-- testAnalyticFunctionFilterIssue934
SELECT COUNT(*) FILTER (WHERE name = 'Raj') FROM table
;

-- testDistinct
SELECT DISTINCT ON (myid) myid, mycol FROM mytable WHERE mytable.col = 9
;

-- testSizeKeywordIssue867
SELECT size FROM mytable
;

-- testTopExpressionIssue243
SELECT TOP (? + 1) * FROM MyTable
;

-- testMultiPartColumnNameWithSchemaNameAndTableName
SELECT schemaName.tableName.columnName FROM tableName
;

-- testMultiPartTableNameWithServerName
SELECT columnName FROM [server-name\server-instance]...tableName
;

-- testProblemSqlAnalytic7Count
SELECT count(*) OVER () AS n FROM table1
;

-- testSelectFunction
SELECT 1 + 2 AS sum
;

-- testIssue160_signedParameter
SELECT start_date WHERE start_date > DATEADD(HH, -?, GETDATE())
;

-- testAllColumnsFromTable
SELECT tableName.* FROM tableName
;

-- testTableFunctionWithAlias
SELECT f2 FROM SOME_FUNCTION() AS z
;

-- testSelectMultidimensionalArrayStatement
SELECT f1, f2[1][1], f3[1][2][3] FROM test
;

-- testFunctionDateTimeValues
SELECT * FROM tab1 WHERE a > TIMESTAMP '2004-04-30 04:05:34.56'
;

-- testProblemSqlCombinedSets
(SELECT * FROM a) INTERSECT (SELECT * FROM b) UNION (SELECT * FROM c)
;

-- testIgnoreNullsForWindowFunctionsIssue1429
SELECT lag(mydata) IGNORE NULLS OVER (ORDER BY sortorder) AS previous_status FROM mytable
;

-- testBetweenDate
SELECT * FROM mytable WHERE col BETWEEN {d '2015-09-19'} AND {d '2015-09-24'}
;

-- testGroupByComplexExpressionIssue1308
select*from dual group by case when 1=1 then 'x' else 'y' end,column1
;

-- testGroupByComplexExpressionIssue1308
select*from dual group by(case when 1=1 then 'x' else 'y' end,column1)
;

-- testGroupByComplexExpressionIssue1308
select*from dual group by(case when 1=1 then 'x' else 'y' end),column1
;

-- testArrayDeclare
SELECT ARRAY[1, f1], ARRAY[[1, 2], [3, f2 + 1]], ARRAY[]::text[] FROM t1
;

-- testInterval1
SELECT 5 + INTERVAL '3 days'
;

-- testInterval2
SELECT to_timestamp(to_char(now() - INTERVAL '45 MINUTE', 'YYYY-MM-DD-HH24:')) AS START_TIME FROM tab1
;

-- testInterval3
SELECT 5 + INTERVAL '3' day
;

-- testInterval4
SELECT '2008-12-31 23:59:59' + INTERVAL 1 SECOND
;

-- testKeywordUnsignedIssue961
SELECT COLUMN1, COLUMN2, CASE WHEN COLUMN1.DATA NOT IN ('1', '3') THEN CASE WHEN CAST(COLUMN2 AS UNSIGNED) IN ('1', '2', '3') THEN 'Q1' ELSE 'Q2' END END AS YEAR FROM TESTTABLE
;

-- testOptimizeForIssue348
SELECT * FROM EMP ORDER BY SALARY DESC OPTIMIZE FOR 20 ROWS
;

-- testCaseElseExpressionIssue1375
select*from t1 where case when 1=1 then c1='a' else c2='b' and c4='d' end
;

-- testVariableAssignment2
SELECT @var = 1
;

-- testVariableAssignment3
SELECT @varname := @varname + 1 AS counter
;

-- testIssue371SimplifiedCase
SELECT CASE col + 4 WHEN 2 THEN 1 ELSE 0 END
;

-- testConcat
SELECT a || b || c + 4 FROM t
;

-- testCount2
SELECT count(ALL col1 + col2) FROM mytable
;

-- testCount3
SELECT count(UNIQUE col) FROM mytable
;

-- testIssue588NotNull
SELECT * FROM mytable WHERE col1 ISNULL
;

-- testSignedColumns
SELECT -columnName, +columnName, +(columnName), -(columnName) FROM tableName
;

-- testDouble
SELECT 1e2, * FROM mytable WHERE mytable.col = 9
;

-- testDouble
SELECT * FROM mytable WHERE mytable.col = 1.e2
;

-- testDouble
SELECT * FROM mytable WHERE mytable.col = 1.2e2
;

-- testDouble
SELECT * FROM mytable WHERE mytable.col = 2e2
;

-- testProblemSqlMinus
(SELECT * FROM a) MINUS (SELECT * FROM b)
;

-- testProblemSqlMinus
SELECT * FROM a MINUS SELECT * FROM b
;

-- testNotNotIssue
SELECT VALUE1, VALUE2 FROM FOO WHERE NOT BAR LIKE '*%'
;

-- testSetOperationWithParenthesisIssue1094
SELECT * FROM ((SELECT A FROM tbl) UNION DISTINCT (SELECT B FROM tbl2)) AS union1
;

-- testCharNotParsedIssue718
SELECT a FROM x WHERE a LIKE '%' + char(9) + '%'
;

-- testMysqlIndexHintsWithJoins
SELECT column FROM table0 t0 INNER JOIN table1 t1 USE INDEX (index1)
;

-- testMysqlIndexHintsWithJoins
SELECT column FROM table0 t0 INNER JOIN table1 t1 IGNORE INDEX (index1)
;

-- testMysqlIndexHintsWithJoins
SELECT column FROM table0 t0 INNER JOIN table1 t1 FORCE INDEX (index1)
;

-- testMultiPartTableNameWithServerProblem
SELECT * FROM LINK_100.htsac.dbo.t_transfer_num a
;

-- testGroupByExpression
SELECT col1, col2, col1 + col2, sum(col8) FROM table1 GROUP BY col1, col2, col1 + col2
;

-- testMysqlMultipleIndexHints
SELECT column FROM testtable AS t0 USE INDEX (index1,index2)
;

-- testMysqlMultipleIndexHints
SELECT column FROM testtable AS t0 IGNORE INDEX (index1,index2)
;

-- testMysqlMultipleIndexHints
SELECT column FROM testtable AS t0 FORCE INDEX (index1,index2)
;

-- testOuterApplyIssue930
SELECT * FROM mytable D OUTER APPLY (SELECT * FROM mytable2 E WHERE E.ColID = D.ColID) A
;

-- testHaving
SELECT MAX(tab1.b) FROM tab1 WHERE a > 34 GROUP BY tab1.b HAVING MAX(tab1.b) > 56
;

-- testHaving
SELECT MAX(tab1.b) FROM tab1 WHERE a > 34 HAVING MAX(tab1.b) IN (56, 32, 3, ?)
;

-- testIssue154
SELECT d.id, d.uuid, d.name, d.amount, d.percentage, d.modified_time FROM discount d LEFT OUTER JOIN discount_category dc ON d.id = dc.discount_id WHERE merchant_id = ? AND deleted = ? AND dc.discount_id IS NULL AND modified_time < ? AND modified_time >= ? ORDER BY modified_time
;

-- testIssue512
SELECT * FROM #tab1
;

-- testIssue512
SELECT * FROM tab#tab1
;

-- testIssue514
SELECT listagg(c1, ';') WITHIN GROUP (PARTITION BY 1 ORDER BY 1) col FROM dual
;

-- testIssue522
select case mr.required_quantity-mr.quantity_issued when 0 then null else case sign(mr.required_quantity)when-1*sign(mr.quantity_issued)then mr.required_quantity-mr.quantity_issued else case sign(abs(mr.required_quantity)-abs(mr.quantity_issued))when-1 then null else mr.required_quantity-mr.quantity_issued end end end quantity_open from mytable
;

-- testIssue554
SELECT T.INDEX AS INDEX133_ FROM myTable T
;

-- testIssue842
select a.id lendid,a.lend_code lendcode,a.amount,a.remaining_principal remainingprincipal,a.interest_rate interestrate,date_add(a.lend_time,interval a.repayment_period day)lendendtime,a.lend_time lendtime from risk_lend a where a.loan_id=1
;

-- testIssue848
SELECT IF(USER_ID > 10 AND SEX = 1, 1, 0)
;

-- testDistinctTop
SELECT DISTINCT TOP 5 myid, mycol FROM mytable WHERE mytable.col = 9
;

-- testSelectAllOperatorIssue1140_2
SELECT * FROM table t0 WHERE t0.id != all(?::uuid[])
;

-- testIsNotDistinctFrom
SELECT name FROM tbl WHERE name IS NOT DISTINCT FROM foo
;

-- testSelectUserVariable
SELECT @col FROM t1
;

-- testSubQueryAliasIssue754
SELECT C0 FROM T0 INNER JOIN T1 ON C1 = C0 INNER JOIN (SELECT W1 FROM T2) S1 ON S1.W1 = C0 ORDER BY C0
;

-- testIsNot2
SELECT * FROM test WHERE NOT a IS NULL
;

-- testIsTrue
SELECT col FROM tbl WHERE col IS TRUE
;

-- testIssue862CaseWhenConcat
SELECT c1, CASE c1 || c2 WHEN '091' THEN '2' ELSE '1' END AS c11 FROM T2
;

-- testTopWithParenthesis
SELECT TOP (5) PERCENT alias.columnName1, alias.columnName2 FROM schemaName.tableName alias ORDER BY alias.columnName2 DESC
;

-- testSelectOracleColl
SELECT * FROM the_table tt WHERE TT.COL1 = lines(idx).COL1
;

-- testOracleHint
select*from mytable
;

-- testOracleHint
select*from mytable
;

-- testOracleHint
select/*+more hints possible*/*from mytable
;

-- testOracleHint
select c,b from mytable
;

-- testOracleHint
select/*+ordered index(b,jl_br_balances_n1)use_nl(j b)use_nl(glcc glf)use_merge(gp gsb)*/b.application_id from jl_br_journals j,po_vendors p
;

-- testOracleHint
select/*+index(patients sex_index)use sex_index because there are few male patients*/name,height,weight from patients where sex='m'
;

-- testOracleHint
select*from emp where sal<50000 and hiredate<'01-jan-1990'
;

-- testOracleHint
select emp.ename,deptno from emp,dept where deptno=10 and emp.deptno=dept.deptno
;

-- testOracleHint
(select*from t1)union(select*from dual)
;

-- testOracleHint
(select*from t1)union(select*from dual)union(select*from dual)
;

-- testOracleHint
(select*from t1)union(select*from dual)
;

-- testOracleJoin
SELECT * FROM tabelle1, tabelle2 WHERE tabelle1.a = tabelle2.b(+)
;

-- testInnerWithBlock
select 1 from(with mytable1 as(select 2)select 3 from mytable1)first
;

-- testUnPivotWithAlias
SELECT simulation_id, un_piv_alias.signal, un_piv_alias.val AS value FROM (SELECT simulation_id, convert(numeric(18, 2), sum(convert(int, init_on))) DosingOnStatus_TenMinutes_sim, convert(numeric(18, 2), sum(CASE WHEN pump_status = 0 THEN 10 ELSE 0 END)) AS DosingOffDurationHour_Hour_sim FROM ft_simulation_result WHERE simulation_id = 210 AND data_timestamp BETWEEN convert(datetime, '2021-09-14', 120) AND convert(datetime, '2021-09-18', 120) GROUP BY simulation_id) sim_data UNPIVOT (val FOR signal IN (DosingOnStatus_TenMinutes_sim, DosingOnDuration_Hour_sim)) un_piv_alias
;

-- testSqlServerHintsWithIndexIssue915
SELECT 1 FROM tableName1 WITH (INDEX (idx1), NOLOCK)
;

-- testPostgreSQLRegExpCaseSensitiveMatch2
SELECT a, b FROM foo WHERE a ~* '[help].*'
;

-- testPostgreSQLRegExpCaseSensitiveMatch3
SELECT a, b FROM foo WHERE a !~ '[help].*'
;

-- testPostgreSQLRegExpCaseSensitiveMatch4
SELECT a, b FROM foo WHERE a !~* '[help].*'
;

-- testMultiColumnAliasIssue849
SELECT * FROM mytable AS mytab2(col1, col2)
;

-- testFunctionIssue284
SELECT NVL((SELECT 1 FROM DUAL), 1) AS A FROM TEST1
;

-- testSqlContainIsNullFunctionShouldBeParsed
SELECT name, age, ISNULL(home, 'earn more money') FROM person
;

-- testRowConstructor1
SELECT * FROM t1 WHERE (col1, col2) = (SELECT col3, col4 FROM t2 WHERE id = 10)
;

-- testRowConstructor2
SELECT * FROM t1 WHERE ROW(col1, col2) = (SELECT col3, col4 FROM t2 WHERE id = 10)
;

-- testLimit2
SELECT * FROM mytable WHERE mytable.col = 9 LIMIT ? OFFSET 3
;

-- testLimit2
SELECT * FROM mytable WHERE mytable.col = 9 LIMIT NULL OFFSET 3
;

-- testLimit2
SELECT * FROM mytable WHERE mytable.col = 9 LIMIT ALL OFFSET 5
;

-- testLimit2
SELECT * FROM mytable WHERE mytable.col = 9 LIMIT 0 OFFSET 3
;

-- testLimit2
SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?
;

-- testLimit2
(SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?) UNION (SELECT * FROM mytable2 WHERE mytable2.col = 9 OFFSET ?) LIMIT 4 OFFSET 3
;

-- testLimit2
(SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?) UNION ALL (SELECT * FROM mytable2 WHERE mytable2.col = 9 OFFSET ?) UNION ALL (SELECT * FROM mytable3 WHERE mytable4.col = 9 OFFSET ?) LIMIT 4 OFFSET 3
;

-- testLimitOffsetKeyWordAsNamedParameter2
SELECT * FROM mytable LIMIT :limit OFFSET :offset
;

-- testTableCrossJoin
SELECT * FROM taba CROSS JOIN tabb
;

-- testWhereIssue241KeywordEnd
SELECT l.end FROM lessons l
;

-- testNotExists
SELECT * FROM tab1 WHERE NOT EXISTS (SELECT * FROM tab2)
;

-- testProblemSqlAnalytic2
SELECT a, row_number() OVER (ORDER BY a, b) AS n FROM table1
;

-- testProblemSqlAnalytic3
SELECT a, row_number() OVER (PARTITION BY c ORDER BY a, b) AS n FROM table1
;

-- testProblemSqlAnalytic9CommaListPartition
SELECT a, row_number() OVER (PARTITION BY c, d ORDER BY a, b) AS n FROM table1
;

-- testKeyWordCreateIssue941
SELECT b.create FROM table b WHERE b.id = 1
;

-- testArrayIssue377
select 'yelp'::name as pktable_cat,n2.nspname as pktable_schem,c2.relname as pktable_name,a2.attname as pkcolumn_name,'yelp'::name as fktable_cat,n1.nspname as fktable_schem,c1.relname as fktable_name,a1.attname as fkcolumn_name,i::int2 as key_seq,case ref.confupdtype when 'c' then 0::int2 when 'n' then 2::int2 when 'd' then 4::int2 when 'r' then 1::int2 else 3::int2 end as update_rule,case ref.confdeltype when 'c' then 0::int2 when 'n' then 2::int2 when 'd' then 4::int2 when 'r' then 1::int2 else 3::int2 end as delete_rule,ref.conname as fk_name,cn.conname as pk_name,case when ref.condeferrable then case when ref.condeferred then 5::int2 else 6::int2 end else 7::int2 end as deferrablity from((((((((select cn.oid,conrelid,conkey,confrelid,confkey,generate_series(array_lower(conkey,1),array_upper(conkey,1))as i,confupdtype,confdeltype,conname,condeferrable,condeferred from pg_catalog.pg_constraint cn,pg_catalog.pg_class c,pg_catalog.pg_namespace n where contype='f' and conrelid=c.oid and relname='business' and n.oid=c.relnamespace and n.nspname='public')ref inner join pg_catalog.pg_class c1 on c1.oid=ref.conrelid)inner join pg_catalog.pg_namespace n1 on n1.oid=c1.relnamespace)inner join pg_catalog.pg_attribute a1 on a1.attrelid=c1.oid and a1.attnum=conkey[i])inner join pg_catalog.pg_class c2 on c2.oid=ref.confrelid)inner join pg_catalog.pg_namespace n2 on n2.oid=c2.relnamespace)inner join pg_catalog.pg_attribute a2 on a2.attrelid=c2.oid and a2.attnum=confkey[i])left outer join pg_catalog.pg_constraint cn on cn.conrelid=ref.confrelid and cn.contype='p')order by ref.oid,ref.i
;

-- testArrayIssue378
select ta.attname,ia.attnum,ic.relname,n.nspname,tc.relname from pg_catalog.pg_attribute ta,pg_catalog.pg_attribute ia,pg_catalog.pg_class tc,pg_catalog.pg_index i,pg_catalog.pg_namespace n,pg_catalog.pg_class ic where tc.relname='business' and n.nspname='public' and tc.oid=i.indrelid and n.oid=tc.relnamespace and i.indisprimary='t' and ia.attrelid=i.indexrelid and ta.attrelid=i.indrelid and ta.attnum=i.indkey[ia.attnum-1]and(not ta.attisdropped)and(not ia.attisdropped)and ic.oid=i.indexrelid order by ia.attnum
;

-- testArrayIssue489
SELECT name[1] FROM MYTABLE
;

-- testArrayIssue638
SELECT PAYLOAD[0] FROM MYTABLE
;

-- testArrayIssue648
select*from a join b on a.id=b.id[1]
;

-- testSelectWithoutFrom
SELECT footable.foocolumn
;

-- testOracleHierarchicalQuery
SELECT last_name, employee_id, manager_id FROM employees CONNECT BY employee_id = manager_id ORDER BY last_name
;

-- testParenthesisAroundFromItem
SELECT * FROM (mytable)
;

-- testPivot1
SELECT * FROM mytable PIVOT (count(a) FOR b IN ('val1'))
;

-- testPivot2
SELECT * FROM mytable PIVOT (count(a) FOR b IN (10, 20, 30))
;

-- testPivot3
SELECT * FROM mytable PIVOT (count(a) AS vals FOR b IN (10 AS d1, 20, 30 AS d3))
;

-- testPivot4
SELECT * FROM mytable PIVOT (count(a), sum(b) FOR b IN (10, 20, 30))
;

-- testPivot5
SELECT * FROM mytable PIVOT (count(a) FOR (b, c) IN ((10, 'a'), (20, 'b'), (30, 'c')))
;

-- testSelectWithBrackets
(SELECT 1 FROM mytable)
;

-- testAnalyticFunctionProblem1b
SELECT last_value(s.revenue_hold) OVER (PARTITION BY s.id_d_insertion_order, s.id_d_product_ad_attr, trunc(s.date_id, 'mm') ORDER BY s.date_id ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS col FROM s
;

-- testDeparser
SELECT a.OWNERLASTNAME, a.OWNERFIRSTNAME FROM ANTIQUEOWNERS AS a, ANTIQUES AS b WHERE b.BUYERID = a.OWNERID AND b.ITEM = 'Chair'
;

-- testDeparser
SELECT count(DISTINCT f + 4) FROM a
;

-- testDeparser
SELECT count(DISTINCT f, g, h) FROM a
;

-- testPivotXmlSubquery1
SELECT * FROM (SELECT times_purchased, state_code FROM customers t) PIVOT (count(state_code) FOR state_code IN ('NY', 'CT', 'NJ', 'FL', 'MO')) ORDER BY times_purchased
;

-- testIssue167_singleQuoteEscape2
select '\'''
;

-- testIssue167_singleQuoteEscape2
select '\\\''
;

-- testOracleHierarchicalQuery2
SELECT employee_id, last_name, manager_id FROM employees CONNECT BY PRIOR employee_id = manager_id
;

-- testOracleHierarchicalQuery3
SELECT last_name, employee_id, manager_id, LEVEL FROM employees START WITH employee_id = 100 CONNECT BY PRIOR employee_id = manager_id ORDER SIBLINGS BY last_name
;

-- testOracleHierarchicalQuery4
SELECT last_name, employee_id, manager_id, LEVEL FROM employees CONNECT BY PRIOR employee_id = manager_id START WITH employee_id = 100 ORDER SIBLINGS BY last_name
;

-- testIsFalse
SELECT col FROM tbl WHERE col IS FALSE
;

-- testExtractFrom1
SELECT EXTRACT(month FROM datecolumn) FROM testtable
;

-- testExtractFrom2
SELECT EXTRACT(year FROM now()) FROM testtable
;

-- testExtractFrom3
SELECT EXTRACT(year FROM (now() - 2)) FROM testtable
;

-- testExtractFrom4
SELECT EXTRACT(minutes FROM now() - '01:22:00') FROM testtable
;

-- testProblemSqlAnalytic4EmptyOver
SELECT a, row_number() OVER () AS n FROM table1
;

-- testTimezoneExpression
SELECT creation_date AT TIME ZONE 'UTC'
;

-- testContionItemsSelectedIssue1077
SELECT 1 > 0
;

-- testCastTypeProblem
SELECT CAST(col1 AS varchar (256)) FROM tabelle1
;

-- testSelContraction
SELECT name, age FROM person
;

-- testUnion2
SELECT * FROM mytable WHERE mytable.col = 9 UNION SELECT * FROM mytable3 WHERE mytable3.col = ? UNION SELECT * FROM mytable2 LIMIT 3 OFFSET 4
;

-- testSelectKeywordPercent
SELECT percent FROM MY_TABLE
;

-- testExistsKeywordIssue1076
SELECT EXISTS (4)
;

-- testValues
SELECT * FROM (VALUES (1, 2), (3, 4)) AS test
;

-- testEscapedFunctionsIssue647
SELECT {fn test(0)} AS COL
;

-- testEscapedFunctionsIssue647
SELECT {fn concat(a, b)} AS COL
;

-- testEscapedFunctionsIssue753
SELECT fn FROM fn
;

-- testFunctionWithComplexParameters_Issue1190
select to_char(a='3')from dual
;

-- testLimitPR404
SELECT * FROM mytable WHERE mytable.col = 9 LIMIT ?1
;

-- testLimitPR404
SELECT * FROM mytable WHERE mytable.col = 9 LIMIT :param_name
;

-- testDateArithmentic2
SELECT CURRENT_DATE + 1 DAY AS NEXT_DATE FROM SYSIBM.SYSDUMMY1
;

-- testDateArithmentic3
SELECT CURRENT_DATE + 1 DAY NEXT_DATE FROM SYSIBM.SYSDUMMY1
;

-- testDateArithmentic4
SELECT CURRENT_DATE - 1 DAY + 1 YEAR - 1 MONTH FROM SYSIBM.SYSDUMMY1
;

-- testDateArithmentic5
SELECT CASE WHEN CURRENT_DATE BETWEEN (CURRENT_DATE - 1 DAY) AND ('2019-01-01') THEN 1 ELSE 0 END FROM SYSIBM.SYSDUMMY1
;

-- testDateArithmentic6
SELECT CURRENT_DATE + HOURS_OFFSET HOUR AS NEXT_DATE FROM SYSIBM.SYSDUMMY1
;

-- testDateArithmentic7
SELECT CURRENT_DATE + MINUTE_OFFSET MINUTE AS NEXT_DATE FROM SYSIBM.SYSDUMMY1
;

-- testDateArithmentic8
SELECT CURRENT_DATE + SECONDS_OFFSET SECOND AS NEXT_DATE FROM SYSIBM.SYSDUMMY1
;

-- testDateArithmentic9
SELECT CURRENT_DATE + (RAND() * 12 MONTH) AS new_date FROM mytable
;

-- testValues6BothVariants
SELECT I FROM (VALUES 1, 2, 3) AS MY_TEMP_TABLE(I) WHERE I IN (SELECT * FROM (VALUES 1, 2) AS TEST)
;

-- testTryCastTypeProblem
SELECT TRY_CAST(col1 AS varchar (256)) FROM tabelle1
;

-- testSubjoinWithJoins
SELECT COUNT(DISTINCT `tbl1`.`id`) FROM (`tbl1`, `tbl2`, `tbl3`)
;

-- testProblemSqlExcept
(SELECT * FROM a) EXCEPT (SELECT * FROM b)
;

-- testProblemSqlExcept
SELECT * FROM a EXCEPT SELECT * FROM b
;

-- testWithValueListWithOutExtraBrackets1135
with sample_data("day")as(values 0,1,2)select "day" from sample_data
;

-- testWithValueListWithOutExtraBrackets1135
with sample_data(day,value)as(values(0,13),(1,12),(2,15),(3,4),(4,8),(5,16))select day,value from sample_data
;

-- testPreserveAndOperator_2
SELECT * FROM mytable WHERE (field_1 && ?)
;

-- testMultiPartTableNameWithServerNameAndSchemaName
SELECT columnName FROM [server-name\server-instance]..schemaName.tableName
;

-- testConditionsWithExtraBrackets_Issue1194
select(col is null)from tbl
;

-- testProblemSqlServer_Modulo
SELECT convert(varchar(255), DATEDIFF(month, year1, abc_datum) / 12) + ' year, ' + convert(varchar(255), DATEDIFF(month, year2, abc_datum) % 12) + ' month' FROM test_table
;

-- testReservedKeyword
SELECT cast, do, extract, first, following, last, materialized, nulls, partition, range, row, rows, siblings, value, xml FROM tableName
;

-- testLimitSqlServerJdbcParameters
SELECT * FROM mytable WHERE mytable.col = 9 ORDER BY mytable.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
;

-- testSelectJoin
SELECT pg_class.relname, pg_attribute.attname, pg_constraint.conname FROM pg_constraint JOIN pg_class ON pg_class.oid = pg_constraint.conrelid JOIN pg_attribute ON pg_attribute.attrelid = pg_constraint.conrelid WHERE pg_constraint.contype = 'u' AND (pg_attribute.attnum = ANY(pg_constraint.conkey)) ORDER BY pg_constraint.conname
;

-- testSelectKeep
SELECT col1, min(col2) KEEP (DENSE_RANK FIRST ORDER BY col3), col4 FROM table1 GROUP BY col5 ORDER BY col3
;

-- testCaseElseAddition
SELECT CASE WHEN 1 + 3 > 20 THEN 0 ELSE 1000 + 1 END AS d FROM dual
;

-- testOrderByWithComplexExpression
SELECT col FROM tbl tbl_alias ORDER BY tbl_alias.id = 1 DESC
;

-- testXorCondition
SELECT * FROM mytable WHERE field = value XOR other_value
;

-- testTopKeyWord
SELECT top.date AS mycol1 FROM mytable top WHERE top.myid = :myid AND top.myid2 = 123
;

-- testCheckColonVariable
SELECT * FROM mytable WHERE (col1, col2) IN ((:qp0, :qp1), (:qp2, :qp3))
;

-- testKeywordSizeIssue880
select b.pattern_size_id,b.pattern_id,b.variation,b.measure_remark,b.pake_name,b.ident_size,concat(group_concat(a.size))as 'title',concat('[',group_concat('{"patternsizedetailid":',a.pattern_size_detail_id,',"patternsizeid":',a.pattern_size_id,',"size":"',a.size,'","sizevalue":',a.size_value separator '},'),'}]')as 'designpatternsizedetailjson' from design_pattern_size_detail a left join design_pattern_size b on a.pattern_size_id=b.pattern_size_id where b.pattern_id=792679713905573986 group by b.pake_name,b.pattern_size_id
;

-- testDateArithmentic
SELECT CURRENT_DATE + (1 DAY) FROM SYSIBM.SYSDUMMY1
;

-- testIssue151_tableFunction
SELECT * FROM tables a LEFT JOIN getdata() b ON a.id = b.id
;

-- testNamedParameter2
SELECT * FROM mytable WHERE a = :param OR a = :param2 AND b = :param3
;

-- testNamedParameter3
SELECT * FROM t WHERE c = :from
;

-- testLogicalExpressionSelectItemIssue1381
select(1+1)=(1+2)
;

-- testLogicalExpressionSelectItemIssue1381
select(1=1)=(1=2)
;

-- testLogicalExpressionSelectItemIssue1381
select((1=1)and(1=2))
;

-- testLogicalExpressionSelectItemIssue1381
select(1=1)and(1=2)
;

-- testBooleanFunction1
SELECT * FROM mytable WHERE test_func(col1)
;

-- testCaseWhenExpressionIssue200
SELECT * FROM t1, t2 WHERE CASE WHEN t1.id = 1 THEN t2.name = 'Marry' WHEN t1.id = 2 THEN t2.age = 10 END
;

-- testCaseWhenExpressionIssue262
SELECT X1, (CASE WHEN T.ID IS NULL THEN CASE P.WEIGHT * SUM(T.QTY) WHEN 0 THEN NULL ELSE P.WEIGHT END ELSE SUM(T.QTY) END) AS W FROM A LEFT JOIN T ON T.ID = ? RIGHT JOIN P ON P.ID = ?
;

-- testSelectCastProblemIssue1248
SELECT CAST(t1.sign2 AS Nullable (char))
;

-- testSpeedTestIssue235
select*from tbl where(round((((((period_diff(date_format(tbl.cd,'%y%m'),date_format(subtime(current_timestamp(),25200),'%y%m'))+month(subtime(current_timestamp(),25200)))-month('2012-02-01'))-1)/3)-round((((month(subtime(current_timestamp(),25200))-month('2012-02-01'))-1)/3))))=-3)
;

-- testNamedParametersIssue612
SELECT a FROM b LIMIT 10 OFFSET :param
;

-- testKeywordCostsIssue1185
WITH costs AS (SELECT * FROM MY_TABLE1 AS ALIAS_TABLE1) SELECT * FROM TESTSTMT
;

-- testCastInCast2
SELECT CAST('test' + CAST(assertEqual AS numeric) AS varchar) FROM tabelle1
;

-- testNamedParameter
SELECT * FROM mytable WHERE b = :param
;

