-- GET OBSERVATION PERIOD FOR ID_PORTFOLIO AND ID_RISK_INDICATOR
WITH p AS (
        SELECT DISTINCT
            value_date
        FROM risk.migration_data_v
        WHERE ( :ID_PORTFOLIO IS NULL
                OR :ID_PORTFOLIO = ''
                OR id_portfolio = Decode( :ID_PORTFOLIO
                                            , '001', 'IFRS1'
                                            , '002', 'IFRS2'
                                            , '003', 'IFRS3'
                                            , '004', 'IFRS4'
                                            , '005', 'IFRS5'
                                            , '006', 'IFRS6'
                                            , :ID_PORTFOLIO ) )
            AND value_date >= :FROM
            AND value_date <= :TO )
    , ri AS (
        SELECT  a.id_risk_indicator
                , Greatest( (   SELECT Min( value_date )
                                FROM p
                                WHERE value_date >= :FROM ), (  SELECT Max( value_date )
                                                                FROM risk.risk_indicator_value
                                                                WHERE id_risk_indicator_ref = a.id_risk_indicator_ref
                                                                    AND value_date <= :FROM ) ) min_ri_date
                , Greatest( (   SELECT Min( value_date )
                                FROM p
                                WHERE value_date >= :FROM ), (  SELECT Min( value_date )
                                                                FROM risk.risk_indicator_value
                                                                WHERE id_risk_indicator_ref = a.id_risk_indicator_ref
                                                                    AND value_date >= :FROM ) ) min_ri_date_1
                , Least( (  SELECT Max( value_date )
                            FROM p
                            WHERE value_date <= :TO ), (    SELECT Min( value_date )
                                                            FROM risk.risk_indicator_value
                                                            WHERE id_risk_indicator_ref = a.id_risk_indicator_ref
                                                                AND value_date >= :TO ) ) max_ri_date
                , Least( (  SELECT Max( value_date )
                            FROM p
                            WHERE value_date <= :TO ), (    SELECT Max( value_date )
                                                            FROM risk.risk_indicator_value
                                                            WHERE id_risk_indicator_ref = a.id_risk_indicator_ref
                                                                AND value_date <= :TO ) ) max_ri_date_1
        FROM risk.risk_indicator a
        WHERE a.id_status = 'C'
            AND a.id_risk_indicator_ref = ( SELECT Max( id_risk_indicator_ref )
                                            FROM risk.risk_indicator
                                            WHERE id_status = 'C'
                                                AND id_risk_indicator = a.id_risk_indicator )
            AND ( ( :id_risk_indicator IS NULL
                        OR id_risk_indicator = :id_risk_indicator ) ) )
SELECT /*+ PARALLEL */
    ri.id_risk_indicator
    , Min( p.value_date ) "Min. Observation Date"
    , Max( p.value_date ) "Max. Observation Date"
FROM ri
    , p
WHERE p.value_date >= Nvl( ri.min_ri_date, ri.min_ri_date_1 )
    AND p.value_date <= Nvl( ri.max_ri_date, ri.max_ri_date_1 )
GROUP BY ri.id_risk_indicator
ORDER BY 1
;

INSERT INTO cfe.ext_eab
SELECT /*+ PARALLEL DRIVING_SITE(A) */ a.*
FROM tbaadm.eod_acct_bal_table@finnacle a
WHERE end_eod_date >= Add_Months( To_Date( :VALUE_DATE, 'mm/dd/yy' ), - 4 )
;

WITH ex AS (
        SELECT  value_date
                , posting_date
        FROM cfe.execution x
        WHERE id_status = 'R'
            AND value_date = (  SELECT Max( value_date )
                                FROM cfe.execution
                                WHERE id_status = 'R' )
            AND posting_date = (    SELECT Max( posting_date )
                                    FROM cfe.execution
                                    WHERE id_status = 'R'
                                        AND value_date = x.value_date ) )
    , fxr AS (
        SELECT  id_currency_from
                , fxrate
        FROM common.fxrate_hst f
        WHERE f.value_date <= ( SELECT value_date
                                FROM ex )
            AND f.value_date = (    SELECT Max( value_date )
                                    FROM common.fxrate_hst
                                    WHERE id_currency_from = f.id_currency_from
                                        AND id_currency_into = f.id_currency_into )
            AND id_currency_into = 'NGN'
        UNION ALL
        SELECT  'NGN'
                , 1
        FROM dual )
    , p AS (
        SELECT t.*
        FROM cfe.portfolio_coll_impairment_hst t
            INNER JOIN ex
                ON Trunc( valid_date ) <= ex.value_date
        WHERE valid_date = (    SELECT Max( valid_date )
                                FROM cfe.portfolio_coll_impairment_hst
                                WHERE id_portfolio = t.id_portfolio
                                    AND Trunc( valid_date ) <= ex.value_date ) )
    , coll AS (
        SELECT  id_instrument_ref
                , 'group_concat(description SEPARATOR CHR(10))' description
        FROM (  SELECT  Concat( Rpad( c.id_collateral, 20 ), ' ', Rpad( Trim( d.description ), 40 )
                                , ' ', Rpad( Substr( Trim( c.description ), 0, 24 ), 24 ), ' '
                                , To_Char( a1.amount, '9,999,999,999' ), ' ', a1.id_currency
                                , ' ', To_Char( d.hair_cut * 100, '9,99' ), '%' ) description
                        , a1.id_instrument_ref
                FROM cfe.instr_coll_recovery_hst a1
                    INNER JOIN ex
                        ON a1.valid_date = ex.value_date
                    INNER JOIN cfe.collateral_ref b
                        ON a1.id_collateral_ref = b.id_collateral_ref
                    INNER JOIN cfe.collateral c
                        ON b.id_collateral = c.id_collateral
                    INNER JOIN common.collateral_type d
                        ON c.id_collateral_type = d.id_collateral_type
                            AND d.id_status = 'C'
                            AND d.id_collateral_type_ref = (    SELECT Max( id_collateral_type_ref )
                                                                FROM common.collateral_type
                                                                WHERE id_collateral_type = d.id_collateral_type
                                                                    AND id_status = 'C' ) ) t
        GROUP BY id_instrument_ref )
    , cp AS (
        SELECT  b.foracid id_instrument
                , b.cif_id id_counterparty
                , Convert( a.description, varchar ) description
        FROM risk.counterparty a
            INNER JOIN cfe.ext_gam b
                ON a.id_counterparty = b.cif_id
                    AND a.id_status = 'C' )
SELECT  Coalesce( cp.id_counterparty, g2.attribute_value ) id_counterparty
        , Coalesce( Convert( cp1.description, varchar ), cp.description, h2.attribute_value ) description
        , b.id_instrument
        , a.id_instrument_type
        , f2.attribute_value product
        , a.start_date
        , a.end_date
        , k2.attribute_value gl_code
        , c.yield
        , a.id_currency
        , c.amortised_cost_dirty
        , c.amortised_cost_dirty_bc
        , c.nominal_balance
                 + c.receivable_principal nominal_balance
        , c.nominal_balance_bc
                 + c.receivable_principal_bc nominal_balance_bc
        , c.open_commitment
        , c.open_commitment_bc
        , coll.description collaterals
        , p1.rate_pd pd_1y
        , CASE
                WHEN c.amortised_cost_dirty_bc < 0
                        OR ( c.amortised_cost_dirty_bc = 0
                                AND c.open_commitment_bc < 0 )
                    THEN Greatest( 1 + Round( Nvl( (    SELECT Sum( a1.amount * fxr.fxrate * Exp( - Nvl( c.yield, 0 ) * d.workout_period / 365 ) ) recovery_amount
                                                        FROM cfe.instr_coll_recovery_hst a1
                                                            INNER JOIN ex
                                                                ON a1.valid_date = ex.value_date
                                                            INNER JOIN fxr
                                                                ON a1.id_currency = fxr.id_currency_from
                                                            INNER JOIN cfe.collateral_ref b
                                                                ON a1.id_collateral_ref = b.id_collateral_ref
                                                            INNER JOIN cfe.collateral c
                                                                ON b.id_collateral = c.id_collateral
                                                            INNER JOIN common.collateral_type d
                                                                ON c.id_collateral_type = d.id_collateral_type
                                                                    AND d.id_status = 'C'
                                                                    AND d.id_collateral_type_ref = (    SELECT Max( id_collateral_type_ref )
                                                                                                        FROM common.collateral_type
                                                                                                        WHERE id_collateral_type = d.id_collateral_type
                                                                                                            AND id_status = 'C' )
                                                        WHERE a1.id_instrument_ref = a.id_instrument_ref ), 0 ), 2 ) / ( Nvl( c.amortised_cost_dirty_bc, 0 ) + Nvl( c.open_commitment_bc, 0 ) * Nvl( e.ccf, 0.25 ) ), 0 )
                ELSE 0
            END lgd_1y
        , p2.rate_pd pd_2y
        , p2.rate_lgd lgd_2y
        , p3.rate_pd pd_3y
        , p3.rate_lgd lgd_3y
        , p10.rate_pd pd_10y
        , p10.rate_lgd lgd_10y
        , CASE
                WHEN e.impairment_stage = 3
                        AND d.impairment_is_specific = '0'
                    THEN 'D'
                ELSE Decode( d.impairment_is_specific
                                , '1', 'S'
                                , '0', 'C' )
            END impairment_is_specific
        , d.impairment
                 + d.impairment_spec impairment
        , d.impairment_bc
                 + d.impairment_spec_bc impairment_bc
        , d.impairment_contingent impairment_contingent
        , d.impairment_contingent_bc impairment_contingent_bc
        , d.unwinding unwinding
        , d.unwinding_bc unwinding_bc
        , d.impairment_spec_d
                 + d.unwinding_d impairment_spec_d
        , d.impairment_spec_d_bc
                 + d.unwinding_d_bc impairment_spec_d_bc
        , d.impairment_d
                 + d.impairment_contingent_d impairment_d
        , d.impairment_d_bc
                 + d.impairment_contingent_d_bc impairment_d_bc
        , d.unwinding_d unwinding_d
        , d.unwinding_d_bc unwinding_d_bc
        , e.id_portfolio
        , e.master_rating
        , Nvl( e.impairment_stage, 1 ) impairment_stage
        , e.overdue_days
        , e.risk_classification
FROM ex
    INNER JOIN cfe.instrument_hst a
        ON ex.value_date = a.value_date
            AND ex.posting_date = a.posting_date
    INNER JOIN cfe.instrument_ref b
        ON a.id_instrument_ref = b.id_instrument_ref
    LEFT JOIN coll
        ON a.id_instrument_ref = coll.id_instrument_ref
    LEFT JOIN cp
        ON b.id_instrument = cp.id_instrument
    INNER JOIN cfe.instrument_measure_balance c
        ON ex.value_date = c.value_date
            AND ex.posting_date = c.posting_date
            AND b.id_instrument_ref = c.id_instrument_ref
            AND c.asset_liability_flag = 'A'
    LEFT JOIN cfe.instrument_measure_impairment d
        ON ex.value_date = d.value_date
            AND ex.posting_date = d.posting_date
            AND b.id_instrument_ref = d.id_instrument_ref
            AND d.asset_liability_flag = '9'
    LEFT JOIN cfe.impairment e
        ON b.id_instrument = e.id_instrument
    LEFT JOIN ( cfe.instrument_attribute_hst2 f
                    INNER JOIN cfe.attribute_ref f1
                        ON f1.id_attribute_ref = f.id_attribute_ref
                            AND f1.id_attribute = 'product'
                    INNER JOIN cfe.attribute_value_ref f2
                        ON f2.id_attribute_value_ref = f.id_attribute_value_ref )
        ON f.value_date = ex.value_date
            AND f.posting_date = ex.posting_date
            AND f.id_instrument_ref = d.id_instrument_ref
    LEFT JOIN ( cfe.instrument_attribute_hst2 g
                    INNER JOIN cfe.attribute_ref g1
                        ON g1.id_attribute_ref = g.id_attribute_ref
                            AND g1.id_attribute = 'cust_id'
                    INNER JOIN cfe.attribute_value_ref g2
                        ON g2.id_attribute_value_ref = g.id_attribute_value_ref )
        ON g.value_date = ex.value_date
            AND g.posting_date = ex.posting_date
            AND g.id_instrument_ref = a.id_instrument_ref
    LEFT JOIN ( cfe.instrument_attribute_hst2 h
                    INNER JOIN cfe.attribute_ref h1
                        ON h1.id_attribute_ref = h.id_attribute_ref
                            AND h1.id_attribute = 'cust_name'
                    INNER JOIN cfe.attribute_value_ref h2
                        ON h2.id_attribute_value_ref = h.id_attribute_value_ref )
        ON h.value_date = ex.value_date
            AND h.posting_date = ex.posting_date
            AND h.id_instrument_ref = a.id_instrument_ref
    LEFT JOIN ( cfe.instrument_attribute_hst2 k
                    INNER JOIN cfe.attribute_ref k1
                        ON k1.id_attribute_ref = k.id_attribute_ref
                            AND k1.id_attribute = 'gl_code'
                    INNER JOIN cfe.attribute_value_ref k2
                        ON k2.id_attribute_value_ref = k.id_attribute_value_ref )
        ON k.value_date = ex.value_date
            AND k.posting_date = ex.posting_date
            AND k.id_instrument_ref = a.id_instrument_ref
    LEFT JOIN risk.counterparty cp1
        ON cp1.id_counterparty = Coalesce( cp.id_counterparty, g2.attribute_value )
            AND cp1.id_status = 'C'
    LEFT JOIN p p1
        ON e.id_portfolio || '_ifrs9' = p1.id_portfolio
            AND ( ( p1.period_scalar = 1
                        AND p1.id_period_type = 'Y' )
                    OR ( p1.period_scalar = 12
                            AND p1.id_period_type = 'M' ) )
    LEFT JOIN p p2
        ON e.id_portfolio || '_ifrs9' = p2.id_portfolio
            AND ( ( p2.period_scalar = 2
                        AND p2.id_period_type = 'Y' )
                    OR ( p2.period_scalar = 24
                            AND p2.id_period_type = 'M' ) )
    LEFT JOIN p p3
        ON e.id_portfolio || '_ifrs9' = p3.id_portfolio
            AND ( ( p3.period_scalar = 3
                        AND p3.id_period_type = 'Y' )
                    OR ( p3.period_scalar = 36
                            AND p3.id_period_type = 'M' ) )
    LEFT JOIN p p10
        ON e.id_portfolio || '_ifrs9' = p10.id_portfolio
            AND ( ( p10.period_scalar = 10
                        AND p10.id_period_type = 'Y' )
                    OR ( p10.period_scalar = 360
                            AND p10.id_period_type = 'M' ) )
WHERE ( amortised_cost_dirty < 0
        OR ( amortised_cost_dirty = 0
                AND open_commitment <= 0 )
        OR impairment != 0
        OR impairment_d != 0
        OR impairment_spec != 0
        OR impairment_spec_d != 0
        OR impairment_contingent != 0
        OR impairment_contingent_d != 0 )
    AND NOT ( id_instrument_type IN ( 'own_acc', 'sec_hft', 'sec_hft_set' )
                OR ( f2.attribute_value IS NOT NULL
                        AND f2.attribute_value IN ( 'GOVBONDS', 'TBILL' /*CBN Issues*/ ) )
                OR ( g2.attribute_value IS NOT NULL
                        AND g2.attribute_value IN ( '271', '7614' ) )  /*CBN*/ )
;


SELECT *
FROM schedule_info info
WHERE ( info.employee_id, info.schedule_date ) IN ( ( 1, '2019-10-01' ), ( 1, '2019-10-02' ) )
;


SELECT *
FROM table_a
WHERE other_id IN ( (   SELECT id
                        FROM table_b
                        WHERE name LIKE '%aa%' ), ( SELECT id
                                                    FROM table_b
                                                    WHERE name LIKE '%bb%' ) )
;


SELECT *
FROM table1
UNION
SELECT *
FROM table2
ORDER BY col
LIMIT 4
OFFSET 5
;


-- ORACLE LONGOPS
SELECT  l.inst_id
        , l.sid
        , l.serial#
        , l.sql_id
        , l.opname
        , l.username
        , l.target
        , l.sofar
        , l.totalwork
        , l.start_time
        , l.last_update_time
        , Round( l.time_remaining / 60, 2 ) "REMAIN MINS"
        , Round( l.elapsed_seconds / 60, 2 ) "ELAPSED MINS"
        , Round( ( l.time_remaining + l.elapsed_seconds ) / 60, 2 ) "TOTAL MINS"
        , Round( l.sofar / l.totalwork * 100, 2 ) "%_COMPLETE"
        , l.message
        , s.sql_text
FROM gv$session_longops l
    LEFT OUTER JOIN v$sql s
        ON s.hash_value = l.sql_hash_value
            AND s.address = l.sql_address
            AND s.child_number = 0
WHERE l.opname NOT LIKE 'RMAN%'
    AND l.opname NOT LIKE '%aggregate%'
    AND l.totalwork != 0
    AND l.sofar <> l.totalwork
    AND l.time_remaining > 0
;


-- Old Oracle Outer Joins (+)
SELECT *
FROM foo
    , bar
WHERE foo.id = bar.id(+)
;


SELECT *
FROM foo
    , bar
WHERE foo.id(+)  = bar.id
;


SELECT  v.lname
        , v.function
        , d.name
FROM (  SELECT  e.dept_id
                , e.lname
                , j.function
        FROM employee e
            , job j
        WHERE e.job_id(+)  = j.job_id ) v
    , department d
WHERE v.dept_id(+)  = d.dept_id
;

