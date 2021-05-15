/*
   Manticore JSQLFormater is a SQL Beautifying and Formatting Software.
   Copyright (C) 2021  Andreas Reichel <andreas@manticore-rpojects.com>

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Affero General Public License as published
   by the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.manticore.jsqlformatter;

import static com.manticore.jsqlformatter.CommentMap.COMMENT_PATTERN;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/** @author Andreas Reichel <andreas@manticore-projects.com> */
@RunWith(Parameterized.class)
public class EmptyStatementTest extends SQLFileTest {


  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> getSqlMap() {
    return getSqlMap(EmptyStatementTest.class);
  }

  public static String buildSqlString(final String originalSql, boolean laxDeparsingCheck) {
    String sql = COMMENT_PATTERN.matcher(originalSql).replaceAll("");
    if (laxDeparsingCheck) {
      String s =
          sql.replaceAll("\\s", " ")
              .replaceAll("\\s+", " ")
              .replaceAll("\\s*([!/,()=+\\-*|\\]<>])\\s*", "$1")
              .toLowerCase()
              .trim();
      return s.endsWith(";") ? s.substring(0, s.length() - 1) : s;

    } else {
      return sql;
    }
  }

	public EmptyStatementTest(String input, String expected) {
		super(input, expected);
	}

  /**
   * Test of format method, of class JSQLFormatter.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testFormat() throws Exception {
    String formatted = JSQLFormatter.format(expected);
    System.out.println(formatted);

    assertEquals(expected.trim(), formatted.trim());
  }
}