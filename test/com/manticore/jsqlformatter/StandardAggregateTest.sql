-- SIMPLE
SELECT  acid
        , Count( * )
FROM (  SELECT /*+ PARALLEL DRIVING_SITE(A) */
            a.acid
            , a.tran_date_bal
            , a.eod_date
            , a.end_eod_date
            , b.tran_date_bal
            , b.eod_date
            , b.end_eod_date
        FROM cfe.tmp_eab a
            INNER JOIN cfe.ext_eab_20210428 b
                ON a.acid = b.acid
        WHERE a.eod_date <= '28-Apr-2021'
            AND a.end_eod_date >= '28-Apr-2021'
            AND b.eod_date <= '28-Apr-2021'
            AND b.end_eod_date >= '28-Apr-2021'
            AND a.tran_date_bal != b.tran_date_bal )
GROUP BY acid
HAVING Count( * ) > 1
;