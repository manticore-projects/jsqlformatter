-- testCreateSequence_noParams
CREATE SEQUENCE my_seq
;

-- testCreateSequence_withCache
CREATE SEQUENCE my_seq CACHE 10
;

-- testCreateSequence_withCycle
CREATE SEQUENCE my_seq CYCLE
;

-- testCreateSequence_withOrder
CREATE SEQUENCE my_seq ORDER
;

-- testCreateSequence_withStart
CREATE SEQUENCE my_seq START WITH 10
;

-- testCreateSequence_withGlobal
CREATE SEQUENCE my_seq GLOBAL
;

-- testCreateSequence_withNoKeep
CREATE SEQUENCE my_seq NOKEEP
;

-- testCreateSequence_withSession
CREATE SEQUENCE my_seq SESSION
;

-- testCreateSequence_withNoCache
CREATE SEQUENCE my_seq NOCACHE
;

-- testCreateSequence_withNoCycle
CREATE SEQUENCE my_seq NOCYCLE
;

-- testCreateSequence_withNoOrder
CREATE SEQUENCE my_seq NOORDER
;

-- testCreateSequence_withNoMinValue
CREATE SEQUENCE my_seq NOMINVALUE
;

-- testCreateSequence_withMinValue
CREATE SEQUENCE my_seq MINVALUE 5
;

-- testCreateSequence_withIncrement
CREATE SEQUENCE db.schema.my_seq INCREMENT BY 1
;

-- testCreateSequence_preservesParamOrder
CREATE SEQUENCE my_sec INCREMENT BY 2 START WITH 10
;

-- testCreateSequence_preservesParamOrder
CREATE SEQUENCE my_sec START WITH 2 INCREMENT BY 5 NOCACHE
;

-- testCreateSequence_preservesParamOrder
CREATE SEQUENCE my_sec START WITH 2 INCREMENT BY 5 CACHE 200 CYCLE
;

-- testCreateSequence_withKeep
CREATE SEQUENCE my_seq KEEP
;

-- testCreateSequence_withNoMaxValue
CREATE SEQUENCE my_seq NOMAXVALUE
;

-- testCreateSequence_withMaxValue
CREATE SEQUENCE my_seq MAXVALUE 5
;

