/**
 * Manticore Projects JSQLFormatter is a SQL Beautifying and Formatting Software.
 * Copyright (C) 2023 Andreas Reichel <andreas@manticore-projects.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.manticore.jsqlformatter;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DebugStatementTest {
  @Test
  public void debugStatementTest() throws Exception {
    String sqlStr = "-- GROUP BY\n" + "SELECT  a\n" + "        , b\n" + "        , c\n"
        + "        , Sum( d )\n" + "FROM t\n" + "GROUP BY    a\n" + "            , b\n"
        + "            , c\n" + "HAVING Sum( d ) > 0\n" + "    AND Count( * ) > 1\n" + ";";
    Statement statement1 =
        CCJSqlParserUtil.parse(sqlStr, parser -> parser.withUnsupportedStatements(false));

    Assertions.assertInstanceOf(Select.class, statement1);

    String formatteredSqlStr = JSQLFormatter.format(sqlStr);
    System.out.println(formatteredSqlStr);
    Statement statement2 = CCJSqlParserUtil.parse(formatteredSqlStr,
        parser -> parser.withUnsupportedStatements(false));

    Assertions.assertInstanceOf(Select.class, statement2);

    Assertions.assertEquals(statement1.toString().toLowerCase(),
        statement2.toString().toLowerCase());
  }
}
