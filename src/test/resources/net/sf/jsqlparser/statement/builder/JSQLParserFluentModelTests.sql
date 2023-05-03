-- testParseAndBuildForXORComplexCondition
SELECT * FROM tab1 AS t1 WHERE a AND b OR c XOR d
;

-- testParseAndBuild
SELECT * FROM tab1 AS t1 JOIN tab2 t2 ON t1.ref = t2.id WHERE (t1.col1 = ? OR t1.col2 = ?) AND t1.col3 IN ('A')
;

-- testParseAndBuildForXORs
SELECT * FROM tab1 AS t1 WHERE a XOR b XOR c
;

-- testParseAndBuildForXOR
SELECT * FROM tab1 AS t1 JOIN tab2 t2 ON t1.ref = t2.id WHERE (t1.col1 XOR t2.col2) AND t1.col3 IN ('B', 'C') XOR t2.col4
;

