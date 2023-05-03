-- testDropIfExists
DROP TABLE IF EXISTS my_table
;

-- testDropFunctionWithNameAndParameterizedType
DROP FUNCTION myFunc(amount integer, name varchar(255))
;

-- testUniqueFunctionDrop
DROP FUNCTION myFunc
;

-- testDropSequence
DROP SEQUENCE mysequence
;

-- testDropMaterializedView
DROP MATERIALIZED VIEW myview
;

-- testDropFunctionWithSimpleType
DROP FUNCTION myFunc(integer, varchar)
;

-- testZeroArgDropFunction
DROP FUNCTION myFunc()
;

-- testDropRestrictIssue510
DROP TABLE TABLE2 RESTRICT
;

-- dropTemporaryTableTestIssue1712
drop temporary table if exists tmp_mwyt8n0z
;

-- testOracleMultiColumnDrop
ALTER TABLE foo DROP (bar, baz) CASCADE
;

-- testDropIndexOnTable
DROP INDEX idx ON abc
;

-- testDropFunctionWithNameAndType
DROP FUNCTION myFunc(amount integer, name varchar)
;

-- testDropSchemaIssue855
DROP SCHEMA myschema
;

-- testDropViewIssue545_2
DROP VIEW IF EXISTS myview
;

-- testDropViewIssue545
DROP VIEW myview
;

