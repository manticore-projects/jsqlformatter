-- PREPARE TABLE
CREATE TABLE sample_data (
    "DAY"        INT
    , "VALUE"    INT
)
;

-- SIMPLE EXPRESSION LIST WITH BRACKETS
WITH sample_data ( "DAY" )
    AS ( VALUES ( 0, 1, 2 ) )
SELECT "DAY"
FROM sample_data
;

-- MULTIPLE EXPRESSION LIST WITH BRACKETS
WITH sample_data ( "DAY", "VALUE" )
    AS ( VALUES ( ( 0, 13 ), ( 1, 12 ), ( 2, 15 )
                    , ( 3, 4 ), ( 4, 8 ), ( 5, 16 ) ) )
SELECT  "DAY"
        , "VALUE"
FROM sample_data
;

-- SIMPLE EXPRESSION LIST WITHOUT BRACKETS
WITH sample_data ( "DAY" )
    AS ( VALUES 0, 1, 2 )
SELECT "DAY"
FROM sample_data
;

-- MULTIPLE EXPRESSION LIST WITHOUT BRACKETS
WITH sample_data ( "DAY", "VALUE" )
    AS ( VALUES ( 0, 13 ), ( 1, 12 ), ( 2, 15 )
                , ( 3, 4 ), ( 4, 8 ), ( 5, 16 ) )
SELECT  "DAY"
        , "VALUE"
FROM sample_data
;

-- VALUE LIST UNION SELECT
WITH split (    word
                , str
                , hascomma ) AS (
        VALUES ( '', 'Auto,A,1234444', 1 )
        UNION ALL
        SELECT  Substr( str, 0, CASE
                        WHEN Instr( str, ',' )
                            THEN Instr( str, ',' )
                        ELSE Length( str ) + 1
                    END )
                , Ltrim( Substr( str, Instr( str, ',' ) ), ',' )
                , Instr( str, ',' )
        FROM split
        WHERE hascomma )
SELECT Trim( word )
FROM split
WHERE word != ''
;