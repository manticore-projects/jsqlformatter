
-- VIEW 1
CREATE OR REPLACE VIEW cfe.interest_period_on_value_day
    AS SELECT /*+ PARALLEL */
            a.value_date
            , d.id_instrument
            , d.id_fixingmode
            , d.fixing_date
            , Coalesce( d.change_date, b.end_date ) change_date
            , d.base_rate
            , d.base_margin_rate
            , d.par_rate
            , d.par_margin_rate
            , d.id_payment_convention
            , d.id_day_count_convention
            , d.id_day_incl_convention
            , d.fix_amount
            , d.id_currency_fix_amount
            , d.id_script
        FROM (  SELECT  value_date
                        , posting_date
                FROM cfe.execution e
                WHERE id_status = 'R'
                    AND value_date = (  SELECT Max( value_date )
                                        FROM cfe.execution
                                        WHERE id_status = 'R' )
                    AND posting_date = (    SELECT Max( posting_date )
                                            FROM cfe.execution
                                            WHERE id_status = 'R'
                                                AND value_date = e.value_date ) )  a
            INNER JOIN cfe.instrument_hst b
                ON a.value_date = b.value_date
                    AND a.posting_date = b.posting_date
            INNER JOIN cfe.instrument_ref c
                ON b.id_instrument_ref = c.id_instrument_ref
            INNER JOIN cfe.interest_period d
                ON d.id_instrument = c.id_instrument
                    AND a.value_date <= Coalesce( d.change_date, b.end_date )
        WHERE Coalesce( d.change_date, b.end_date ) = ( SELECT Min( Coalesce( change_date, b.end_date ) )
                                                        FROM cfe.interest_period
                                                        WHERE id_instrument = d.id_instrument
                                                            AND a.value_date <= Coalesce( change_date, b.end_date ) )
        ORDER BY    2
                    , 1
                    , 4
;

-- VIEW 2
CREATE OR REPLACE VIEW cfe.execution_v
    AS SELECT /*+ PARALLEL */
            e.start_timestamp
            , e.end_timestamp
            , EXTRACT( hour FROM
                        e.end_timestamp
                                 - e.start_timestamp ) || ':'
                     || Lpad( EXTRACT( minute FROM e.end_timestamp - e.start_timestamp ), 2, '0' ) duration
            , e.value_date
            , e.posting_date
            , CASE
                    WHEN EXTRACT( year FROM e1.value_date ) > EXTRACT( year FROM e.value_date )
                        THEN 'Y'
                    WHEN To_Char( e1.value_date, 'Q' ) > To_Char( e.value_date, 'Q' )
                        THEN 'Q'
                    WHEN EXTRACT( month FROM e1.value_date ) > EXTRACT( month FROM e.value_date )
                        THEN 'M'
                    WHEN To_Char( e1.value_date, 'IW' ) > To_Char( e.value_date, 'IW' )
                        THEN 'W'
                    WHEN e1.value_date IS NULL
                            AND e.id_status = 'R'
                        THEN 'L'
                END flag
            , e.value_date_p
            , e.value_date_pp
            , e.id_status
        FROM cfe.execution e
            LEFT JOIN cfe.execution e1
                ON e.value_date < e1.value_date
                    AND e.id_status IN ( 'R', 'H' )
                    AND e1.id_status IN ( 'R', 'H' )
        WHERE ( e1.value_date = (   SELECT Min( value_date )
                                    FROM cfe.execution
                                    WHERE id_status IN ( 'R', 'H' )
                                        AND value_date > e.value_date )
                OR e1.value_date IS NULL )
        ORDER BY    e.posting_date DESC
                    , e.value_date DESC
;
