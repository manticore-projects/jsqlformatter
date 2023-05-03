-- testRollback
rollback
;

-- testRollback
rollback work
;

-- testRollback
rollback to banda_sal
;

-- testRollback
rollback to savepoint banda_sal
;

-- testRollback
rollback work to banda_sal
;

-- testRollback
rollback work to savepoint banda_sal
;

-- testRollback
rollback force '25.32.87'
;

-- testRollback
rollback work force '25.32.87'
;

-- testSavepoint
savepoint banda_sal
;

-- testCommit
COMMIT
;

