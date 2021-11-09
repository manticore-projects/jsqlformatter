package com.manticore.jsqlformatter;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.Assert;
import org.junit.Test;

public class DebugStatementTest {
    @Test
    public void debugStatementTest() throws Exception {
        String sqlStr="SELECT * FROM table1 UNION SELECT * FROM table2 ORDER BY col LIMIT 4 OFFSET 5";
        Statement statement1 = CCJSqlParserUtil.parse(sqlStr);

        String formatteredSqlStr = JSQLFormatter.format(sqlStr);
        Statement statement2 = CCJSqlParserUtil.parse(formatteredSqlStr);

        Assert.assertEquals(statement1.toString(), statement2.toString());

    }
}
