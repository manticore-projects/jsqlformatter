-- testComplexWithQueryIssue561
WITH split (word, str, hascomma) AS (VALUES ('', 'Auto,A,1234444', 1) UNION ALL SELECT substr(str, 0, CASE WHEN instr(str, ',') THEN instr(str, ',') ELSE length(str) + 1 END), ltrim(substr(str, instr(str, ',')), ','), instr(str, ',') FROM split WHERE hascomma) SELECT trim(word) FROM split WHERE word != ''
;

-- testDuplicateKey
VALUES (1, 2, 'test')
;

