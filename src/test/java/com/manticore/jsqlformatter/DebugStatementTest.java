package com.manticore.jsqlformatter;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.Assert;
import org.junit.Test;

public class DebugStatementTest {
    @Test
    public void debugStatementTest() throws Exception {
        String sqlStr="-- ORACLE LONGOPS\n" +
                "SELECT l.inst_id\n" +
                "       , l.sid\n" +
                "       , l.serial#\n" +
                "       , l.sql_id\n" +
                "       , l.opname\n" +
                "       , l.username\n" +
                "       , l.target\n" +
                "       , l.sofar\n" +
                "       , l.totalwork\n" +
                "       , l.start_time\n" +
                "       , l.last_update_time\n" +
                "       , Round(l.time_remaining / 60, 2)                         \"REMAIN MINS\"\n" +
                "       , Round(l.elapsed_seconds / 60, 2)                        \"ELAPSED MINS\"\n" +
                "       , Round(( l.time_remaining + l.elapsed_seconds ) / 60, 2) \"TOTAL MINS\"\n" +
                "       , Round(l.SOFAR / l.TOTALWORK * 100, 2)                   \"%_COMPLETE\"\n" +
                "       , l.message\n" +
                "       , s.sql_text\n" +
                "FROM   gv$session_longops l\n" +
                "       left outer join v$sql s\n" +
                "                    ON s.hash_value = l.sql_hash_value\n" +
                "                       AND s.address = l.sql_address\n" +
                "                       AND s.child_number = 0\n" +
                "WHERE  l.OPNAME NOT LIKE 'RMAN%'\n" +
                "       AND l.OPNAME NOT LIKE '%aggregate%'\n" +
                "       AND l.TOTALWORK != 0\n" +
                "       AND l.sofar <> l.totalwork\n" +
                "       AND l.time_remaining > 0 \n";
        Statement statement1 = CCJSqlParserUtil.parse(sqlStr);

        String formatteredSqlStr = JSQLFormatter.format(sqlStr);
        System.out.println( formatteredSqlStr );
        Statement statement2 = CCJSqlParserUtil.parse(formatteredSqlStr);

        Assert.assertEquals(statement1.toString().toLowerCase(), statement2.toString().toLowerCase());

    }
}
