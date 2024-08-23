EXPLAIN PLAN FOR
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
;

SUMMARIZE
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
;

EXPLAIN cfe.execution
;

SUMMARIZE cfe.execution
;