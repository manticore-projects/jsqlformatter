-- APPEND COLLATERAL REF
SELECT /*+ PARALLEL */ 
    cfe.id_collateral_ref.nextval
    , id_collateral
FROM  ( SELECT DISTINCT 
            a.id_collateral
        FROM cfe.collateral a 
            LEFT JOIN cfe.collateral_ref b 
                ON a.id_collateral = b.id_collateral
        WHERE b.id_collateral_ref IS NULL )
;

-- BOTH CLAUSES PRESENT 'with a string' AND "a field"
MERGE /*+ parallel */ INTO test1 /*the target table*/ a 
    USING all_objects /*the source table*/
        ON ( /*joins in()!*/ a.object_id = b.object_id )
-- INSERT CLAUSE 
WHEN /*comments between keywords!*/ NOT MATCHED THEN 
    INSERT ( object_id /*ID Column*/
                , status /*Status Column*/ ) 
    VALUES ( b.object_id
                , b.status )
/* UPDATE CLAUSE
WITH A WHERE CONDITION */ 
WHEN MATCHED THEN 
    UPDATE SET  a.status = '/*this is no comment!*/ and -- this ain''t either'
    WHERE   b."--status" != 'VALID'
;