
-- BRACKETS 1
SELECT columnname
FROM [server-name\\server-instance]..schemaname.tablename
;

-- BRACKETS 2
SELECT columnname
FROM [server-name\\server-instance]..[schemaName].[table Name]
;

-- BRACKETS 3
SELECT columnname
FROM [server-name\\server-instance]..[schemaName].[table-Name]
;

-- BRACKETS 4
SELECT columnname
FROM [schemaName].[tableName]
;

-- BRACKETS 5
SELECT columnname
FROM schemaname.[tableName]
;

-- BRACKETS 6
SELECT columnname
FROM [schemaName].tablename
;

-- READ INSTRUMENT TRANSACTIONS WITH COLLATERAL ONLY
SELECT a.*
FROM [cfe].[TRANSACTION] a
    INNER JOIN cfe.instrument b
        ON a.id_instrument = b.id_instrument
WHERE a.id_instrument >= ?
    AND a.id_instrument <= ?
    AND EXISTS (    SELECT 1
                    FROM cfe.instrument_ref b
                        INNER JOIN cfe.instrument_collateral_hst c
                            ON b.id_instrument_ref = c.id_instrument_ref
                    WHERE b.id_instrument = a.id_instrument )
;
