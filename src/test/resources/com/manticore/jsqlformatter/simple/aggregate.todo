SELECT STRING_AGG( FRUIT,' & ' ORDER BY CASE TYPEOF(FRUIT)
                                        WHEN 'VARCHAR' THEN LENGTH(TRY_CAST(FRUIT AS VARCHAR))
                                        WHEN 'BLOB' THEN OCTET_LENGTH(TRY_CAST(FRUIT AS BLOB))
                                        END ) AS STRING_AGG
FROM ( SELECT UNNEST( ['apple','pear','banana','pear'] ) AS FRUIT) AS FRUIT
;