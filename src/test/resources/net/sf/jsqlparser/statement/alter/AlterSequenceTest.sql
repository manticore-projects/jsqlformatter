-- testAlterSequence_preservesParamOrder
ALTER SEQUENCE my_sec INCREMENT BY 2 START WITH 10
;

-- testAlterSequence_preservesParamOrder
ALTER SEQUENCE my_sec START WITH 2 INCREMENT BY 5 NOCACHE
;

-- testAlterSequence_preservesParamOrder
ALTER SEQUENCE my_sec START WITH 2 INCREMENT BY 5 CACHE 200 CYCLE
;

-- testAlterSequence_withIncrement
ALTER SEQUENCE my_seq INCREMENT BY 1
;

-- testAlterSequence_restartIssue1405WithoutValue
ALTER SEQUENCE my_seq RESTART
;

-- testAlterSequence_withNoMaxValue
ALTER SEQUENCE my_seq NOMAXVALUE
;

-- testAlterSequence_withMinValue
ALTER SEQUENCE my_seq MINVALUE 5
;

-- testAlterSequence_withCache
ALTER SEQUENCE my_seq CACHE 10
;

-- testAlterSequence_withCycle
ALTER SEQUENCE my_seq CYCLE
;

-- testAlterSequence_withOrder
ALTER SEQUENCE my_seq ORDER
;

-- testAlterSequence_withStart
ALTER SEQUENCE my_seq START WITH 10
;

-- testAlterSequence_withSession
ALTER SEQUENCE my_seq SESSION
;

-- testAlterSequence_withNoCache
ALTER SEQUENCE my_seq NOCACHE
;

-- testAlterSequence_withNoCycle
ALTER SEQUENCE my_seq NOCYCLE
;

-- testAlterSequence_withNoOrder
ALTER SEQUENCE my_seq NOORDER
;

-- testAlterSequence_restartIssue1405
ALTER SEQUENCE my_seq RESTART WITH 1
;

-- testAlterSequence_withKeep
ALTER SEQUENCE my_seq KEEP
;

-- testAlterSequence_withGlobal
ALTER SEQUENCE my_seq GLOBAL
;

-- testAlterSequence_withMaxValue
ALTER SEQUENCE my_seq MAXVALUE 5
;

-- testAlterSequence_withNoKeep
ALTER SEQUENCE my_seq NOKEEP
;

-- testAlterSequence_withNoMinValue
ALTER SEQUENCE my_seq NOMINVALUE
;

-- testAlterSequence_noParams
ALTER SEQUENCE my_seq
;

