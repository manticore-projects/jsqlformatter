-- createWithReplace
CREATE OR REPLACE SYNONYM TBL_TABLE_NAME FOR SCHEMA.T_TBL_NAME
;

-- createWithDbLink
CREATE PUBLIC SYNONYM emp_table FOR hr.employees@remote.us.oracle.com
;

-- createWithReplacePublic
CREATE OR REPLACE PUBLIC SYNONYM TBL_TABLE_NAME FOR SCHEMA.T_TBL_NAME
;

-- createPublic
CREATE PUBLIC SYNONYM TBL_TABLE_NAME FOR SCHEMA.T_TBL_NAME
;
