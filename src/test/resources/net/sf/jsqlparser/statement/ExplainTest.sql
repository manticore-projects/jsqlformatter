-- testAnalyze
EXPLAIN ANALYZE SELECT * FROM mytable
;

-- testCosts
EXPLAIN COSTS SELECT * FROM mytable
;

-- testMultiOptions_orderPreserved
EXPLAIN VERBOSE ANALYZE BUFFERS COSTS SELECT * FROM mytable
;

-- testBuffers
EXPLAIN BUFFERS SELECT * FROM mytable
;

-- testVerbose
EXPLAIN VERBOSE SELECT * FROM mytable
;

-- testFormat
EXPLAIN FORMAT XML SELECT * FROM mytable
;

-- testDescribe
EXPLAIN SELECT * FROM mytable
;

