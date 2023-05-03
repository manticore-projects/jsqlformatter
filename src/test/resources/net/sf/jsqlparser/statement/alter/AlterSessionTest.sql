-- testAlterSessionCloseDatabaseLink
alter session close database link mylink
;

-- testAlterSessionSet
alter session set ddl_lock_timeout=7200
;

-- testAlterSessionSet
alter session set ddl_lock_timeout=7200
;

-- testAlterSessionDisable
alter session disable commit in procedure
;

-- testAlterSessionDisable
alter session disable guard
;

-- testAlterSessionDisable
alter session disable parallel dml
;

-- testAlterSessionDisable
alter session disable parallel ddl
;

-- testAlterSessionDisable
alter session disable parallel query
;

-- testAlterSessionForceParallel
alter session force parallel dml
;

-- testAlterSessionForceParallel
alter session force parallel dml parallel 10
;

-- testAlterSessionForceParallel
alter session force parallel ddl
;

-- testAlterSessionForceParallel
alter session force parallel ddl parallel 10
;

-- testAlterSessionForceParallel
alter session force parallel query
;

-- testAlterSessionForceParallel
alter session force parallel query parallel 10
;

-- testAlterSessionResumable
alter session enable resumable
;

-- testAlterSessionResumable
alter session disable resumable
;

-- testAlterSessionAdvise
alter session advise commit
;

-- testAlterSessionAdvise
alter session advise rollback
;

-- testAlterSessionAdvise
alter session advise nothing
;

-- testAlterSessionEnable
alter session enable commit in procedure
;

-- testAlterSessionEnable
alter session enable guard
;

-- testAlterSessionEnable
alter session enable parallel dml
;

-- testAlterSessionEnable
alter session enable parallel dml parallel 10
;

-- testAlterSessionEnable
alter session enable parallel ddl
;

-- testAlterSessionEnable
alter session enable parallel ddl parallel 10
;

-- testAlterSessionEnable
alter session enable parallel query
;

-- testAlterSessionEnable
alter session enable parallel query parallel 10
;

