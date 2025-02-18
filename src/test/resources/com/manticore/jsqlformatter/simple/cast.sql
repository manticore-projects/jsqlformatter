SELECT  Cast( ( SELECT 1 ) AS DECIMAL (1) )
;

SELECT  Cast( CASE
                WHEN Position('(' IN description) > 0
                    THEN Trim( substr(description, 1, POSITION('(' IN description) - 1) )
                ELSE description
                END AS CHAR ) accounting_event
;

SELECT  id
        ,  Try_Cast( v AS JSON ) j
        , '2013-05-08'::DATE AS v1
FROM vartab
ORDER BY id
;