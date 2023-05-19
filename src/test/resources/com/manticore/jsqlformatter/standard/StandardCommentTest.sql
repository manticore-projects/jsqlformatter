------------------------------------------------------------------------------------------------------------------------
-- CONFIGURATION
------------------------------------------------------------------------------------------------------------------------

-- UPDATE CALENDAR
UPDATE cfe.calendar
SET year_offset = ?            /* year offset */
    , settlement_shift = ?     /* settlement shift */
    , friday_is_holiday = ?    /* friday is a holiday */
    , saturday_is_holiday = ?  /* saturday is a holiday */
    , sunday_is_holiday = ?    /* sunday is a holiday */
WHERE id_calendar = ?
;


-- BOTH CLAUSES PRESENT 'with a string' AND "a field"
MERGE /*+ PARALLEL */ INTO test1 /*the target table*/ a
    USING all_objects      /*the source table*/
        ON ( /*joins in()!;*/ a.object_id = b.object_id )
-- INSERT CLAUSE;
WHEN /*comments between keywords!;*/ NOT MATCHED THEN
    INSERT ( object_id     /*ID Column*/
                , status   /*Status Column*/ )
    VALUES ( b.object_id
                , b.status )
/* UPDATE CLAUSE
WITH A WHERE CONDITION */ 
WHEN MATCHED THEN          /* Lets rock */
    UPDATE SET  a.status = '/*this is no comment!*/ and -- this ain''t either;'
    WHERE   b."--status;" != 'VALID'
;
