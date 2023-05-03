-- testStatement
purge table testtable
;

-- testStatement
purge table cfe.testtable
;

-- testStatement
purge index testtable_idx1
;

-- testStatement
purge index cfe.testtable_idx1
;

-- testStatement
purge recyclebin
;

-- testStatement
purge dba_recyclebin
;

-- testStatement
purge tablespace my_table_space
;

-- testStatement
purge tablespace my_table_space user cfe
;

