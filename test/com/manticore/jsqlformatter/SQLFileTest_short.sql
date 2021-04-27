/* Please do not find this query:
select * from start_comment;
*/


SELECT  osuser
        , sl.sql_id
        , sl.sql_hash_value
        , opname
        , target
        , elapsed_seconds
        , time_remaining
FROM v$session_longops sl
    INNER JOIN v$session s
        ON sl.SID = s.SID
            AND sl.SERIAL# = s.SERIAL#
WHERE time_remaining > 0
/* Also not this query:
select * from end_comment;
*/
;