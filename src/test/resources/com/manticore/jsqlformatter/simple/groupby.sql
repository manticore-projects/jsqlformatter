-- GROUP BY resulting in an Unsupported Statement
SELECT  a
        , b
        , c
        , Sum( d )
FROM t
GROUP BY    a
            , b
            , c
HAVING Sum( d ) > 0
    AND Count( * ) > 1
;