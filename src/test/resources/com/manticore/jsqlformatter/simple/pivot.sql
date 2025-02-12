SELECT DISTINCT
    product
FROM (  SELECT *
        FROM (  SELECT  'kale' AS product
                        , 55 AS q1
                        , 45 AS q2
                UNION ALL
                SELECT  'apple'
                        , 8
                        , 10 )
        UNPIVOT (sales FOR quarter IN (Q1, Q2))
        ORDER BY 1 )
;