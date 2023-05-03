-- testAlterView
ALTER VIEW myview AS SELECT * FROM mytab
;

-- testReplaceView
REPLACE VIEW myview(a, b) AS SELECT a, b FROM mytab
;

