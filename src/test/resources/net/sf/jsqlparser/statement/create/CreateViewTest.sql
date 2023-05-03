-- testCreateTemporaryViewIssue604
CREATE TEMPORARY VIEW myview AS SELECT * FROM mytable
;

-- testCreateTemporaryViewIssue665
CREATE VIEW foo("BAR") AS WITH temp AS (SELECT temp_bar FROM foobar) SELECT bar FROM temp
;

-- testCreateView2
CREATE VIEW myview AS SELECT * FROM mytab
;

-- testCreateView3
CREATE OR REPLACE VIEW myview AS SELECT * FROM mytab
;

-- testCreateView4
CREATE OR REPLACE VIEW view2 AS SELECT a, b, c FROM testtab INNER JOIN testtab2 ON testtab.col1 = testtab2.col2
;

-- testCreateViewAutoRefreshNo
CREATE VIEW myview AUTO REFRESH NO AS SELECT * FROM mytab
;

-- testCreateForceView
CREATE FORCE VIEW view1 AS SELECT a, b FROM testtab
;

-- testCreateViewAutoRefreshYes
CREATE VIEW myview AUTO REFRESH YES AS SELECT * FROM mytab
;

-- testCreateWithReadOnlyViewIssue838
CREATE VIEW v14(c1, c2) AS SELECT c1, C2 FROM t1 WITH READ ONLY
;

-- testCreateViewIfNotExists
CREATE VIEW myview IF NOT EXISTS AS SELECT * FROM mytab
;

-- testCreateTemporaryViewIssue604_2
CREATE TEMP VIEW myview AS SELECT * FROM mytable
;

-- testCreateForceView1
CREATE NO FORCE VIEW view1 AS SELECT a, b FROM testtab
;

-- testCreateForceView2
CREATE OR REPLACE FORCE VIEW view1 AS SELECT a, b FROM testtab
;

-- testCreateForceView3
CREATE OR REPLACE NO FORCE VIEW view1 AS SELECT a, b FROM testtab
;

-- testCreateViewUnion
CREATE VIEW view1 AS (SELECT a, b FROM testtab) UNION (SELECT b, c FROM testtab2)
;

-- testCreateMaterializedView
CREATE MATERIALIZED VIEW view1 AS SELECT a, b FROM testtab
;

-- testCreateViewAutoRefreshNone
CREATE VIEW myview AS SELECT * FROM mytab
;

-- testCreateMaterializedViewIfNotExists
CREATE MATERIALIZED VIEW myview IF NOT EXISTS AS SELECT * FROM mytab
;

-- testCreateViewWithColumnNames1
CREATE OR REPLACE VIEW view1(col1, col2) AS SELECT a, b FROM testtab
;

