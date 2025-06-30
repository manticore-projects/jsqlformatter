SELECT *
FROM (  SELECT /*+ PARALLEL(4) DYNAMIC_SAMPLING(0) FIRST_ROWS(100) */
            id_instrument
            , id_transaction
            , value_date
            , settlement_date
            , face_value
        FROM cfe.transaction
        WHERE id_instrument IN (    SELECT DISTINCT
                                        id_instrument
                                    FROM Unnest( String_Split( '1307462
,1307975
,1399843
,1399842
,1393008
,1307025
,1307960
,1307461
,1310585
,1309079
,1307974
,1309073
,1308175
,1375768
,1379248
,1377522
,1399844
,1399865
,1399847
,1401285
,1311369
,1311361
,1346393
,1346400
,1307029
,1307026
,1307027
' ) ) AS t(id_transaction)
                                        INNER JOIN cfe.transaction
                                                USING ( id_transaction ) )
        QUALIFY Sum( face_value )
                    OVER (PARTITION BY ID_INSTRUMENT ) <> 0
        UNION ALL
        SELECT /*+ PARALLEL(4) DYNAMIC_SAMPLING(0) FIRST_ROWS(100) */
            id_instrument
                 || ' Total:'
            , '' id_transaction
            , NULL value_date
            , NULL settlement_date
            , Sum( face_value ) face_value
        FROM cfe.transaction
        WHERE id_instrument IN (    SELECT DISTINCT
                                        id_instrument
                                    FROM Unnest( String_Split( '1307462
,1307975
,1399843
,1399842
,1393008
,1307025
,1307960
,1307461
,1310585
,1309079
,1307974
,1309073
,1308175
,1375768
,1379248
,1377522
,1399844
,1399865
,1399847
,1401285
,1311369
,1311361
,1346393
,1346400
,1307029
,1307026
,1307027
' ) ) AS t(id_transaction)
                                        INNER JOIN cfe.transaction
                                                USING ( id_transaction ) )
        GROUP BY id_instrument
        HAVING Sum( face_value ) <> 0 )
ORDER BY    1
            , 3 NULLS LAST
            , 4 NULLS LAST
;