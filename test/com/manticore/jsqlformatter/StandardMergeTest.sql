
-- MERGE 1
MERGE INTO cfe.impairment imp
    USING ( WITH x AS ( 
                    SELECT a.id_instrument
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
            SELECT /*+ parallel */ *
            FROM x x1
            WHERE x1.valid_date = ( SELECT Max
                                    FROM x
                                    WHERE id_instrument = x1.id_instrument )  ) s
        ON ( imp.id_instrument = s.id_instrument ) 
WHEN MATCHED THEN 
    UPDATE SET imp.ccf = s.ccf
;

-- MERGE 2
MERGE INTO cfe.instrument_import_measure imp
    USING ( WITH x AS ( 
                    SELECT a.id_instrument
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
            SELECT /*+ parallel */ *
            FROM x x1
            WHERE x1.valid_date = ( SELECT Max
                                    FROM x
                                    WHERE id_instrument = x1.id_instrument
                                        AND valid_date <= to_date )  ) s
        ON ( imp.id_instrument = s.id_instrument
                AND imp.measure = 'YIELD' ) 
WHEN MATCHED THEN 
    UPDATE SET imp.value = s.yield
;

-- MERGE 3
MERGE INTO cfe.instrument_import_measure imp
    USING s
        ON ( imp.id_instrument = s.id_instrument
                AND imp.measure = 'YIELD_P'
                AND imp.id_instrument = s.id_instrument
                AND imp.measure = 'YIELD_P' ) 
WHEN MATCHED THEN 
    UPDATE SET imp.value = s.yield
;

-- MERGE 4
MERGE INTO cfe.instrument_import_measure imp
    USING ( WITH x AS ( 
                    SELECT a.id_instrument
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
            SELECT /*+ parallel */ *
            FROM x x1
            WHERE x1.valid_date = ( SELECT Max
                                    FROM x
                                    WHERE id_instrument = x1.id_instrument
                                        AND valid_date <= to_date )  ) s
        ON ( imp.id_instrument = s.id_instrument
                AND imp.measure = 'YIELD_PP' ) 
WHEN MATCHED THEN 
    UPDATE SET imp.value = s.yield
;

-- MERGE DELETE WHERE
MERGE INTO empl_current tar
    USING ( SELECT empno
                , ename
                , CASE 
                    WHEN leavedate <= SYSDATE
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
    UPDATE SET tar.ename = src.ename
    WHERE delete_flag = 'N'
    DELETE WHERE delete_flag = 'Y'
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
    UPDATE SET a.status = b.status
    WHERE b.status != 'VALID'
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
    WHERE b.status != 'VALID'
WHEN MATCHED THEN 
    UPDATE SET a.status = b.status
    WHERE b.status != 'VALID'
;
