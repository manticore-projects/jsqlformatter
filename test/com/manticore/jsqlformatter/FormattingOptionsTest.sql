-- UPDATE CALENDAR
-- @JSQLFormatter(indentWidth=8, keywordSpelling=UPPER, functionSpelling=CAMEL, objectSpelling=LOWER, separation=BEFORE)
UPDATE cfe.calendar
SET     year_offset = ?                    /* year offset */
        , settlement_shift = To_Char( ? )  /* settlement shift */
        , friday_is_holiday = ?            /* friday is a holiday */
        , saturday_is_holiday = ?          /* saturday is a holiday */
        , sunday_is_holiday = ?            /* sunday is a holiday */
WHERE id_calendar = ?
;


-- UPDATE CALENDAR
-- @JSQLFormatter(indentWidth=2, keywordSpelling=LOWER, functionSpelling=KEEP, objectSpelling=UPPER, separation=AFTER)
update CFE.CALENDAR
set YEAR_OFFSET = ?                    /* year offset */,
    SETTLEMENT_SHIFT = to_char( ? )    /* settlement shift */,
    FRIDAY_IS_HOLIDAY = ?              /* friday is a holiday */,
    SATURDAY_IS_HOLIDAY = ?            /* saturday is a holiday */,
    SUNDAY_IS_HOLIDAY = ?              /* sunday is a holiday */
where ID_CALENDAR = ?
;


-- MERGE DELETE WHERE
merge into EMPL_CURRENT TAR
  using ( select  EMPNO,
                  ENAME,
                  case
                      when LEAVEDATE <= SYSDATE
                        then 'Y'
                      else 'N'
                    end as DELETE_FLAG
          from EMPL ) SRC
    on ( TAR.EMPNO = SRC.EMPNO )
when not matched then
  insert ( EMPNO,
            ENAME )
  values ( SRC.EMPNO,
            SRC.ENAME )
when matched then
  update set  TAR.ENAME = SRC.ENAME
  where DELETE_FLAG = 'N'
  delete where  DELETE_FLAG = 'Y'
;
