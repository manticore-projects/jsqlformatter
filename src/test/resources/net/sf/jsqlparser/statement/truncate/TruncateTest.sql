-- testTruncateDeparse
TRUNCATE TABLE foo
;

-- testTruncateCascadeDeparse
TRUNCATE TABLE foo CASCADE
;

-- testTruncateOnlyDeparse
TRUNCATE TABLE ONLY foo CASCADE
;

