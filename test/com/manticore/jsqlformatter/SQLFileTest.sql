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
                ON sl.SID = s.SID
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
SELECT  SUBSTR( V$SESSION.USERNAME, 1, 8 ) USERNAME
        , V$SESSION.OSUSER OSUSER
        , DECODE( V$SESSION.SERVER, 'DEDICATED', 'D', 'SHARED', 'S', 'O' ) SERVER
        , V$SQLAREA.DISK_READS DISK_READS
        , V$SQLAREA.BUFFER_GETS BUFFER_GETS
        , SUBSTR( V$SESSION.LOCKWAIT, 1, 10 ) LOCKWAIT
        , V$SESSION.PROCESS PID
        , V$SESSION_WAIT.EVENT EVENT
        , V$SQLAREA.SQL_TEXT SQL
FROM V$SESSION_WAIT
    , V$SQLAREA
    , V$SESSION
WHERE V$SESSION.SQL_ADDRESS = V$SQLAREA.ADDRESS
    AND V$SESSION.SQL_HASH_VALUE = V$SQLAREA.HASH_VALUE
    AND V$SESSION.SID = V$SESSION_WAIT.SID
    AND V$SESSION.STATUS = 'ACTIVE'
    AND V$SESSION_WAIT.EVENT != 'client message'
ORDER BY    V$SESSION.LOCKWAIT ASC
            , V$SESSION.USERNAME
;

SELECT  'exec DBMS_SHARED_POOL.PURGE (''' || ADDRESS || ',' || HASH_VALUE || ''', ''C'');'
FROM V$SQLAREA
WHERE SQL_ID LIKE '9z1ufprvt2pk2'
;