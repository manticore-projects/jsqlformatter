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
        ON sl.sid = s.sid
            AND sl.serial# = s.serial#
WHERE time_remaining > 0
/* Also not this query:
select * from end_comment;
*/
;
