
-- MERGE 1
MERGE INTO cfe.impairment imp
    USING ( WITH x AS (
                    SELECT  a.id_instrument
                            , a.id_currency
                            , a.id_instrument_type
                            , b.id_portfolio
                            , c.attribute_value product_code
                            , t.valid_date
                            , t.ccf
                    FROM cfe.instrument a
                        INNER JOIN cfe.impairment b
                            ON a.id_instrument = b.id_instrument
                        LEFT JOIN cfe.instrument_attribute c
                            ON a.id_instrument = c.id_instrument
                                AND c.id_attribute = 'product'
                        INNER JOIN cfe.ext_ccf t
                            ON ( a.id_currency LIKE t.id_currency )
                                AND ( a.id_instrument_type LIKE t.id_instrument_type )
                                AND ( b.id_portfolio LIKE t.id_portfolio
                                        OR ( b.id_portfolio IS NULL
                                                AND t.id_portfolio = '%' ) )
                                AND ( c.attribute_value LIKE t.product_code
                                        OR ( c.attribute_value IS NULL
                                                AND t.product_code = '%' ) ) )
            SELECT /*+ PARALLEL */
                *
            FROM x x1
            WHERE x1.valid_date = ( SELECT max
                                    FROM x
                                    WHERE id_instrument = x1.id_instrument ) ) s
        ON ( imp.id_instrument = s.id_instrument )
WHEN MATCHED THEN
    UPDATE SET  imp.ccf = s.ccf
;

-- MERGE 2
MERGE INTO cfe.instrument_import_measure imp
    USING ( WITH x AS (
                    SELECT  a.id_instrument
                            , a.id_currency
                            , a.id_instrument_type
                            , b.id_portfolio
                            , c.attribute_value product_code
                            , t.valid_date
                            , t.yield
                    FROM cfe.instrument a
                        INNER JOIN cfe.impairment b
                            ON a.id_instrument = b.id_instrument
                        LEFT JOIN cfe.instrument_attribute c
                            ON a.id_instrument = c.id_instrument
                                AND c.id_attribute = 'product'
                        INNER JOIN cfe.ext_yield t
                            ON ( a.id_currency = t.id_currency )
                                AND ( a.id_instrument_type LIKE t.id_instrument_type )
                                AND ( b.id_portfolio LIKE t.id_portfolio
                                        OR ( b.id_portfolio IS NULL
                                                AND t.id_portfolio = '%' ) )
                                AND ( c.attribute_value LIKE t.product_code
                                        OR ( c.attribute_value IS NULL
                                                AND t.product_code = '%' ) ) )
            SELECT /*+ PARALLEL */
                *
            FROM x x1
            WHERE x1.valid_date = ( SELECT max
                                    FROM x
                                    WHERE id_instrument = x1.id_instrument
                                        AND valid_date <= to_date ) ) s
        ON ( imp.id_instrument = s.id_instrument
                AND imp.measure = 'YIELD' )
WHEN MATCHED THEN
    UPDATE SET  imp.value = s.yield
;

-- MERGE 3
MERGE INTO cfe.instrument_import_measure imp
    USING s
        ON ( imp.id_instrument = s.id_instrument
                AND imp.measure = 'YIELD_P'
                AND imp.id_instrument = s.id_instrument
                AND imp.measure = 'YIELD_P' )
WHEN MATCHED THEN
    UPDATE SET  imp.value = s.yield
;

-- MERGE 4
MERGE INTO cfe.instrument_import_measure imp
    USING ( WITH x AS (
                    SELECT  a.id_instrument
                            , a.id_currency
                            , a.id_instrument_type
                            , b.id_portfolio
                            , c.attribute_value product_code
                            , t.valid_date
                            , t.yield
                    FROM cfe.instrument a
                        INNER JOIN cfe.impairment b
                            ON a.id_instrument = b.id_instrument
                        LEFT JOIN cfe.instrument_attribute c
                            ON a.id_instrument = c.id_instrument
                                AND c.id_attribute = 'product'
                        INNER JOIN cfe.ext_yield t
                            ON ( a.id_currency = t.id_currency )
                                AND ( a.id_instrument_type LIKE t.id_instrument_type )
                                AND ( b.id_portfolio LIKE t.id_portfolio
                                        OR ( b.id_portfolio IS NULL
                                                AND t.id_portfolio = '%' ) )
                                AND ( c.attribute_value LIKE t.product_code
                                        OR ( c.attribute_value IS NULL
                                                AND t.product_code = '%' ) ) )
            SELECT /*+ PARALLEL */
                *
            FROM x x1
            WHERE x1.valid_date = ( SELECT max
                                    FROM x
                                    WHERE id_instrument = x1.id_instrument
                                        AND valid_date <= to_date ) ) s
        ON ( imp.id_instrument = s.id_instrument
                AND imp.measure = 'YIELD_PP' )
WHEN MATCHED THEN
    UPDATE SET  imp.value = s.yield
;

-- MERGE DELETE WHERE
MERGE INTO empl_current tar
    USING ( SELECT  empno
                    , ename
                    , CASE
                        WHEN leavedate <= sysdate
                            THEN 'Y'
                        ELSE 'N'
                        END AS delete_flag
            FROM empl ) src
        ON ( tar.empno = src.empno )
WHEN NOT MATCHED THEN
    INSERT ( empno
                , ename )
    VALUES ( src.empno
                , src.ename )
WHEN MATCHED THEN
    UPDATE SET  tar.ename = src.ename
    WHERE   delete_flag = 'N'
    DELETE WHERE    delete_flag = 'Y'
;

-- BOTH CLAUSES PRESENT
MERGE INTO test1 a
    USING all_objects
        ON ( a.object_id = b.object_id )
WHEN NOT MATCHED THEN
    INSERT ( object_id
                , status )
    VALUES ( b.object_id
                , b.status )
WHEN MATCHED THEN
    UPDATE SET  a.status = b.status
    WHERE   b.status != 'VALID'
;

-- BOTH CLAUSES PRESENT 2
MERGE INTO test1 a
    USING all_objects
        ON ( a.object_id = b.object_id )
WHEN NOT MATCHED THEN
    INSERT ( object_id
                , status )
    VALUES ( b.object_id
                , b.status )
    WHERE   b.status != 'VALID'
WHEN MATCHED THEN
    UPDATE SET  a.status = b.status
    WHERE   b.status != 'VALID'
;

-- INSERT WITHOUT COLUMNS
MERGE /*+ PARALLEL */ INTO cfe.tmp_eab a
    USING ( SELECT /*+ PARALLEL DRIVING_SITE(C) */
                c.*
            FROM tbaadm.eab@finnacle c
                INNER JOIN (    SELECT  acid
                                        , eod_date
                                FROM cfe.tmp_eab e
                                WHERE end_eod_date = (  SELECT Max( eod_date )
                                                        FROM cfe.tmp_eab
                                                        WHERE acid = e.acid )
                                    AND end_eod_date < '31-Dec-2099' ) d
                    ON c.acid = d.acid
                        AND c.eod_date >= d.eod_date ) b
        ON ( a.acid = b.acid
                AND a.eod_date = b.eod_date )
WHEN MATCHED THEN
    UPDATE SET  a.tran_date_bal = b.tran_date_bal
                , a.tran_date_tot_tran = b.tran_date_tot_tran
                , a.value_date_bal = b.value_date_bal
                , a.value_date_tot_tran = b.value_date_tot_tran
                , a.end_eod_date = b.end_eod_date
                , a.lchg_user_id = b.lchg_user_id
                , a.lchg_time = b.lchg_time
                , a.rcre_user_id = b.rcre_user_id
                , a.rcre_time = b.rcre_time
                , a.ts_cnt = b.ts_cnt
                , a.eab_crncy_code = b.eab_crncy_code
                , a.bank_id = b.bank_id
WHEN NOT MATCHED THEN
    INSERT VALUES ( b.acid
                    , b.eod_date
                    , b.tran_date_bal
                    , b.tran_date_tot_tran
                    , b.value_date_bal
                    , b.value_date_tot_tran
                    , b.end_eod_date
                    , b.lchg_user_id
                    , b.lchg_time
                    , b.rcre_user_id
                    , b.rcre_time
                    , b.ts_cnt
                    , b.eab_crncy_code
                    , b.bank_id )
;

-- MERGE WITH
WITH wmachine AS (
        SELECT DISTINCT
            projcode
            , plantcode
            , buildingcode
            , floorcode
            , room
        FROM tab_machinelocation
        WHERE Trim( Room ) <> ''
            AND Trim( Room ) <> '-' )
MERGE INTO tab_roomlocation AS troom
    USING wmachine
        ON ( troom.projcode = wmachine.projcode
                AND troom.plantcode = wmachine.plantcode
                AND troom.buildingcode = wmachine.buildingcode
                AND troom.floorcode = wmachine.floorcode
                AND troom.room = wmachine.room )
WHEN NOT MATCHED /* BY TARGET */ THEN
    INSERT ( projcode
                , plantcode
                , buildingcode
                , floorcode
                , room )
    VALUES ( wmachine.projcode
                , wmachine.plantcode
                , wmachine.buildingcode
                , wmachine.floorcode
                , wmachine.room )
OUTPUT  Getdate() AS timeaction
        , $action AS action
        , inserted.projcode
        , inserted.plantcode
        , inserted.buildingcode
        , inserted.floorcode
        , inserted.room
    INTO tab_mergeactions_roomlocation
;
