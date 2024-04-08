WITH finishers AS (
        SELECT  'Sophia Liu' AS name
                , TIMESTAMP '2016-10-18 2:51:45' AS finish_time
                , 'F30-34' AS division
        UNION ALL
        SELECT  'Lisa Stelzner'
                , TIMESTAMP '2016-10-18 2:54:11'
                , 'F35-39'
        UNION ALL
        SELECT  'Nikki Leith'
                , TIMESTAMP '2016-10-18 2:59:01'
                , 'F30-34'
        UNION ALL
        SELECT  'Lauren Matthews'
                , TIMESTAMP '2016-10-18 3:01:17'
                , 'F35-39'
        UNION ALL
        SELECT  'Desiree Berry'
                , TIMESTAMP '2016-10-18 3:05:42'
                , 'F35-39'
        UNION ALL
        SELECT  'Suzy Slane'
                , TIMESTAMP '2016-10-18 3:06:24'
                , 'F35-39'
        UNION ALL
        SELECT  'Jen Edwards'
                , TIMESTAMP '2016-10-18 3:06:36'
                , 'F30-34'
        UNION ALL
        SELECT  'Meghan Lederer'
                , TIMESTAMP '2016-10-18 3:07:41'
                , 'F30-34'
        UNION ALL
        SELECT  'Carly Forte'
                , TIMESTAMP '2016-10-18 3:08:58'
                , 'F25-29'
        UNION ALL
        SELECT  'Lauren Reasoner'
                , TIMESTAMP '2016-10-18 3:10:14'
                , 'F30-34' )
SELECT  name
        , Strftime( finish_time, '%X' ) AS finish_time
        , division
        , Strftime( fastest_time, '%X' ) AS fastest_time
        , Strftime( second_fastest, '%X' ) AS second_fastest
FROM (  SELECT  name
                , finish_time
                , division
                , finishers
                , First(finish_time) OVER w1 AS fastest_time
                , NTH_VALUE(finish_time, 2) OVER w1 AS second_fastest
        FROM finishers
        WINDOW
            w1 AS (
                PARTITION BY division
                ORDER BY finish_time ASC
                ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING ) )
ORDER BY    3
            , 2
            , 1
;

SELECT  FIRST_VALUE( bc.merch_l1_name IGNORE NULLS )
            OVER (  PARTITION BY sku
                                , channel_id
                    ORDER BY feed_date DESC ) AS merch_l1_name
        , FIRST_VALUE( bc.brand IGNORE NULLS )
            OVER (  PARTITION BY sku
                                , channel_id
                    ORDER BY feed_date DESC) AS brand
FROM temp.abc bc
;