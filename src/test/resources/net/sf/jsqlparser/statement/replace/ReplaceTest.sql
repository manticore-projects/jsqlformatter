-- testProblemReplaceParseDeparse
REPLACE a_table (ID, A, B) SELECT A_ID, A, B FROM b_table
;

-- testMultipleValues
REPLACE INTO mytable (col1, col2, col3) VALUES (1, "aaa", now()), (2, "bbb", now())
;

-- testReplaceSyntax1
replace mytable set col1='as',col2=?,col3=565
;

-- testReplaceSyntax2
replace mytable(col1,col2,col3)values('as',?,565)
;

-- testReplaceSyntax3
replace mytable(col1,col2,col3)select*from mytable3
;

-- testProblemMissingIntoIssue389
REPLACE INTO mytable (key, data) VALUES (1, "aaa")
;

