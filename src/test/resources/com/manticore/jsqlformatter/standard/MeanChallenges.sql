-- SELECT Date Ranges of available data

WITH p
     AS (SELECT DISTINCT value_date
         FROM   risk.migration_data_v
         WHERE  (:1  IS NULL OR :2 ='' OR id_portfolio = Decode (:3 , '001', 'IFRS1',
                                                      '002', 'IFRS2',
                                                      '003', 'IFRS3',
                                                      '004', 'IFRS4',
                                                      '005', 'IFRS5',
                                                      '006', 'IFRS6',
                                                      :4 ))
                AND value_date >= :5 
                AND value_date <= :6 ),
     ri
     AS (SELECT a.id_risk_indicator
                , Greatest((SELECT Min(value_date)
                            FROM   p
                            WHERE  value_date >= :7 ), (SELECT Max(value_date)
                                                          FROM   risk.risk_indicator_value
                                                          WHERE  id_risk_indicator_ref = a.id_risk_indicator_ref
                                                                 AND value_date <= :8 )) min_ri_date
                , Greatest((SELECT Min(value_date)
                            FROM   p
                            WHERE  value_date >= :9 ), (SELECT Min(value_date)
                                                          FROM   risk.risk_indicator_value
                                                          WHERE  id_risk_indicator_ref = a.id_risk_indicator_ref
                                                                 AND value_date >= :10 )) min_ri_date_1
                , Least((SELECT Max(value_date)
                         FROM   p
                         WHERE  value_date <= :11 ), (SELECT Min(value_date)
                                                     FROM   risk.risk_indicator_value
                                                     WHERE  id_risk_indicator_ref = a.id_risk_indicator_ref
                                                            AND value_date >= :12 ))        max_ri_date
                , Least((SELECT Max(value_date)
                         FROM   p
                         WHERE  value_date <= :13 ), (SELECT Max(value_date)
                                                     FROM   risk.risk_indicator_value
                                                     WHERE  id_risk_indicator_ref = a.id_risk_indicator_ref
                                                            AND value_date <= :14 ))        max_ri_date_1
         FROM   risk.risk_indicator a
         WHERE  a.id_status = 'C'
                AND a.id_risk_indicator_ref = (SELECT Max(id_risk_indicator_ref)
                                               FROM   risk.risk_indicator
                                               WHERE  id_status = 'C'
                                                      AND id_risk_indicator = a.id_risk_indicator)
                AND ( ( :15  IS NULL
                        OR id_risk_indicator = :16  )
                        ))
SELECT /*+parallel*/ ri.id_risk_indicator
       , Min(p.value_date) "Min. Observation Date"
       , Max(p.value_date) "Max. Observation Date"
FROM   ri
       , p
WHERE  p.value_date >= Nvl(ri.min_ri_date, ri.min_ri_date_1)
       AND p.value_date <= Nvl(ri.max_ri_date, ri.max_ri_date_1)
GROUP  BY ri.id_risk_indicator
order by 1 

