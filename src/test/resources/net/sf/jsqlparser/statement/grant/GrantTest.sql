-- testGrantQueryWithPrivileges
GRANT SELECT, INSERT, UPDATE, DELETE ON T1 TO ADMIN_ROLE
;

-- testGrantSchemaParsingIssue1080
GRANT SELECT ON schema_name.table_name TO XYZ
;

-- testGrantQueryWithRole
GRANT ROLE_1 TO TEST_ROLE_1, TEST_ROLE_2
;

