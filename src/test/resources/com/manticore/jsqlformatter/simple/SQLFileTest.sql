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

CREATE VIEW sys.running_sql
    AS SELECT   s.username
                , sl.sid
                , sq.executions
                , sl.last_update_time
                , sl.sql_id
                , sl.sql_hash_value
                , opname
                , target
                , elapsed_seconds
                , time_remaining
                , sq.sql_fulltext
        FROM v$session_longops sl
            INNER JOIN v$sql sq
                ON sq.sql_id = sl.sql_id
            INNER JOIN v$session s
                ON sl.sid = s.sid
                    AND sl.serial# = s.serial#
        WHERE time_remaining > 0
;


SET pagesize 55
;


SET linesize 170
;


/*
something crazy;
something crazy;
something crazy;
something crazy;
*/


-- something crazy;
SELECT  Substr( v$session.username, 1, 8 ) username
        , v$session.osuser osuser
        , Decode( v$session.server
                    , 'DEDICATED', 'D'
                    , 'SHARED', 'S'
                    , 'O' ) server
        , v$sqlarea.disk_reads disk_reads
        , v$sqlarea.buffer_gets buffer_gets
        , Substr( v$session.lockwait, 1, 10 ) lockwait
        , v$session.process pid
        , v$session_wait.event event
        , v$sqlarea.sql_text sql
FROM v$session_wait
    , v$sqlarea
    , v$session
WHERE v$session.sql_address = v$sqlarea.address
    AND v$session.sql_hash_value = v$sqlarea.hash_value
    AND v$session.sid = v$session_wait.sid
    AND v$session.status = 'ACTIVE'
    AND v$session_wait.event != 'client message'
ORDER BY    v$session.lockwait ASC
            , v$session.username
;


SELECT 'exec DBMS_SHARED_POOL.PURGE (''' || address || ',' || hash_value || ''', ''C'');'
FROM v$sqlarea
WHERE sql_id LIKE '9z1ufprvt2pk2'
;
