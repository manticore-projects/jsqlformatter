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

import org.junit.jupiter.api.Test;

public class LineCountTest {

  @Test
  public void lineCountTest() throws Exception {
    String sqlString = "select 1,2,3 from dual where a=b and b=c;";
    System.out.println(JSQLFormatter.format(sqlString, "showLineNumbers=YES"));
    System.out.println(JSQLFormatter.format(sqlString, "outputFormat=ANSI"));
  }

  @Test
  public void lineCountTestHtml() throws Exception {
    String sqlString = "select 1,2,3 from dual where a=b and b=c;";
    System.out.println(JSQLFormatter.format(sqlString, "showLineNumbers=YES"));
    System.out.println(JSQLFormatter.format(sqlString, "outputFormat=HTML"));
  }
}
