
-- GET OBSERVATION PERIOD FOR ID_PORTFOLIO AND ID_RISK_INDICATOR

WITH p
     AS (SELECT DISTINCT value_date
         FROM   risk.migration_data_v
         WHERE  (:ID_PORTFOLIO IS NULL OR :ID_PORTFOLIO='' OR id_portfolio = Decode (:ID_PORTFOLIO, '001', 'IFRS1',
                                                      '002', 'IFRS2',
                                                      '003', 'IFRS3',
                                                      '004', 'IFRS4',
                                                      '005', 'IFRS5',
                                                      '006', 'IFRS6',
                                                      :ID_PORTFOLIO))
                AND value_date >= :FROM
                AND value_date <= :TO),
     ri
     AS (SELECT a.id_risk_indicator
                , Greatest((SELECT Min(value_date)
                            FROM   p
                            WHERE  value_date >= :FROM), (SELECT Max(value_date)
                                                          FROM   risk.risk_indicator_value
                                                          WHERE  id_risk_indicator_ref = a.id_risk_indicator_ref
                                                                 AND value_date <= :FROM)) min_ri_date
                , Greatest((SELECT Min(value_date)
                            FROM   p
                            WHERE  value_date >= :FROM), (SELECT Min(value_date)
                                                          FROM   risk.risk_indicator_value
                                                          WHERE  id_risk_indicator_ref = a.id_risk_indicator_ref
                                                                 AND value_date >= :FROM)) min_ri_date_1
                , Least((SELECT Max(value_date)
                         FROM   p
                         WHERE  value_date <= :TO), (SELECT Min(value_date)
                                                     FROM   risk.risk_indicator_value
                                                     WHERE  id_risk_indicator_ref = a.id_risk_indicator_ref
                                                            AND value_date >= :TO))        max_ri_date
                , Least((SELECT Max(value_date)
                         FROM   p
                         WHERE  value_date <= :TO), (SELECT Max(value_date)
                                                     FROM   risk.risk_indicator_value
                                                     WHERE  id_risk_indicator_ref = a.id_risk_indicator_ref
                                                            AND value_date <= :TO))        max_ri_date_1
         FROM   risk.risk_indicator a
         WHERE  a.id_status = 'C'
                AND a.id_risk_indicator_ref = (SELECT Max(id_risk_indicator_ref)
                                               FROM   risk.risk_indicator
                                               WHERE  id_status = 'C'
                                                      AND id_risk_indicator = a.id_risk_indicator)
                AND ( ( :id_risk_indicator IS NULL
                        OR id_risk_indicator = :id_risk_indicator )
                        ))
SELECT /*+parallel*/ ri.id_risk_indicator
       , Min(p.value_date) "Min. Observation Date"
       , Max(p.value_date) "Max. Observation Date"
FROM   ri
       , p
WHERE  p.value_date >= Nvl(ri.min_ri_date, ri.min_ri_date_1)
       AND p.value_date <= Nvl(ri.max_ri_date, ri.max_ri_date_1)
GROUP  BY ri.id_risk_indicator
order by 1; 


INSERT INTO cfe.ext_eab
SELECT /*+ parallel driving_site(a) */ a.*
FROM TBAADM.EOD_ACCT_BAL_TABLE@finnacle a
WHERE END_EOD_DATE >= add_months(   To_date(    :VALUE_DATE, 'mm/dd/yy' ), - 4 )
;