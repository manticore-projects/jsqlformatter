-- testUpsertWithSelect
UPSERT INTO mytable (mycolumn) WITH a AS (SELECT mycolumn FROM mytable) SELECT mycolumn FROM a
;

-- testUpsertWithSelect
UPSERT INTO mytable (mycolumn) (WITH a AS (SELECT mycolumn FROM mytable) SELECT mycolumn FROM a)
;

-- testHexValues2
UPSERT INTO TABLE2 VALUES ('1', "DSDD", 0xEFBFBDC7AB)
;

-- testHexValues3
UPSERT INTO TABLE2 VALUES ('1', "DSDD", 0xabcde)
;

-- testHexValues
UPSERT INTO TABLE2 VALUES ('1', "DSDD", x'EFBFBDC7AB')
;

-- testUpsertMultiRowValue
UPSERT INTO mytable (col1, col2) VALUES (a, b), (d, e)
;

-- testUpsertWithKeywords
UPSERT INTO kvPair (value, key) VALUES (?, ?)
;

-- testSimpleUpsert
UPSERT INTO example (num, name, address, tel) VALUES (1, 'name', 'test ', '1234-1234')
;

-- testUpsertHasSelect
UPSERT INTO mytable (mycolumn) SELECT mycolumn FROM mytable
;

-- testUpsertHasSelect
UPSERT INTO mytable (mycolumn) (SELECT mycolumn FROM mytable)
;

-- testDuplicateKey
UPSERT INTO Users0 (UserId, Key, Value) VALUES (51311, 'T_211', 18) ON DUPLICATE KEY UPDATE Value = 18
;

