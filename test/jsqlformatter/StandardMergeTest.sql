-- MERGE 1
merge /*+no_parallel*/ INTO cfe.impairment imp
USING (WITH x
            AS (SELECT a.id_instrument
                       , a.id_currency
                       , a.id_instrument_type
                       , b.id_portfolio
                       , c.attribute_value product_code
                       , t.valid_date
                       , t.ccf
                FROM   cfe.instrument a
                       inner join cfe.impairment b
                               ON a.id_instrument = b.id_instrument
                       left join cfe.instrument_attribute c
                              ON a.id_instrument = c.id_instrument
                                 AND c.id_attribute = 'product'
                       inner join cfe.ext_ccf t
                               ON ( a.id_currency LIKE t.id_currency )
                                  AND ( a.id_instrument_type LIKE t.id_instrument_type )
                                  AND ( b.id_portfolio LIKE t.id_portfolio
                                         OR ( b.id_portfolio IS NULL
                                              AND t.id_portfolio = '%' ) )
                                  AND ( c.attribute_value LIKE t.product_code
                                         OR ( c.attribute_value IS NULL
                                              AND t.product_code = '%' ) ))
       SELECT /*+parallel*/ *
        FROM   x x1
        WHERE  x1.valid_date = (SELECT Max(valid_date)
                                FROM   x
                                WHERE  id_instrument = x1.id_instrument)) s
ON (imp.id_instrument = s.id_instrument)
WHEN matched THEN
  UPDATE SET imp.ccf = s.ccf
;

-- MERGE 2
merge /*+no_parallel*/ INTO cfe.instrument_import_measure imp
USING (WITH x
            AS (SELECT a.id_instrument
                       , a.id_currency
                       , a.id_instrument_type
                       , b.id_portfolio
                       , c.attribute_value product_code
                       , t.valid_date
                       , t.yield
                FROM   cfe.instrument a
                       inner join cfe.impairment b
                               ON a.id_instrument = b.id_instrument
                       left join cfe.instrument_attribute c
                              ON a.id_instrument = c.id_instrument
                                 AND c.id_attribute = 'product'
                       inner join cfe.ext_yield t
                               ON ( a.id_currency = t.id_currency )
                                  AND ( a.id_instrument_type LIKE t.id_instrument_type )
                                  AND ( b.id_portfolio LIKE t.id_portfolio
                                         OR ( b.id_portfolio IS NULL
                                              AND t.id_portfolio = '%' ) )
                                  AND ( c.attribute_value LIKE t.product_code
                                         OR ( c.attribute_value IS NULL
                                              AND t.product_code = '%' ) ))
       SELECT /*+parallel*/ *
        FROM   x x1
        WHERE  x1.valid_date = (SELECT Max(valid_date)
                                FROM   x
                                WHERE  id_instrument = x1.id_instrument
                                and valid_date<=to_date('$VALUE_DATE','MM/DD/YY'))) s
ON (imp.id_instrument = s.id_instrument and imp.measure='YIELD')
WHEN matched THEN
  UPDATE SET imp.value = s.yield
;

-- MERGE 3
merge /*+no_parallel*/ INTO cfe.instrument_import_measure imp
USING s
ON (imp.id_instrument = s.id_instrument and imp.measure='YIELD_P' and imp.id_instrument = s.id_instrument and imp.measure='YIELD_P')
WHEN matched THEN
  UPDATE SET imp.value = s.yield
;

-- MERGE 4
merge /*+no_parallel*/ INTO cfe.instrument_import_measure imp
USING (WITH x
            AS (SELECT a.id_instrument
                       , a.id_currency
                       , a.id_instrument_type
                       , b.id_portfolio
                       , c.attribute_value product_code
                       , t.valid_date
                       , t.yield
                FROM   cfe.instrument a
                       inner join cfe.impairment b
                               ON a.id_instrument = b.id_instrument
                       left join cfe.instrument_attribute c
                              ON a.id_instrument = c.id_instrument
                                 AND c.id_attribute = 'product'
                       inner join cfe.ext_yield t
                               ON ( a.id_currency = t.id_currency )
                                  AND ( a.id_instrument_type LIKE t.id_instrument_type )
                                  AND ( b.id_portfolio LIKE t.id_portfolio
                                         OR ( b.id_portfolio IS NULL
                                              AND t.id_portfolio = '%' ) )
                                  AND ( c.attribute_value LIKE t.product_code
                                         OR ( c.attribute_value IS NULL
                                              AND t.product_code = '%' ) ))
       SELECT /*+parallel*/ *
        FROM   x x1
        WHERE  x1.valid_date = (SELECT Max(valid_date)
                                FROM   x
                                WHERE  id_instrument = x1.id_instrument
                                and valid_date<=to_date('$VALUE_DATE_PP','MM/DD/YY'))) s
ON (imp.id_instrument = s.id_instrument and imp.measure='YIELD_PP')
WHEN matched THEN
  UPDATE SET imp.value = s.yield
;

-- MERGE DELETE WHERE
MERGE INTO empl_current tar
     USING (SELECT empno
                  ,ename
                  ,CASE WHEN leavedate <= SYSDATE
                        THEN 'Y'
                        ELSE 'N'
                   END AS delete_flag
              FROM empl) src ON (tar.empno = src.empno)
WHEN NOT MATCHED THEN INSERT (empno    ,ename)
                      VALUES (src.empno, src.ename)
                      -- WHERE delete_flag = 'N'
WHEN     MATCHED THEN UPDATE SET tar.ename = src.ename
                       WHERE delete_flag = 'N'
                      DELETE WHERE delete_flag = 'Y'
;

-- Both clauses present.
MERGE INTO test1 a
  USING all_objects b
    ON (a.object_id = b.object_id)
  WHEN MATCHED THEN
    UPDATE SET a.status = b.status
    WHERE  b.status != 'VALID'
  WHEN NOT MATCHED THEN
    INSERT (object_id, status)
    VALUES (b.object_id, b.status)
    WHERE  b.status != 'VALID'
;