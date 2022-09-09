/**
 * Manticore Projects JSQLFormatter is a SQL Beautifying and Formatting Software.
 * Copyright (C) 2022 Andreas Reichel <andreas@manticore-projects.com>
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

public class ASTVisualizationTest {

  @Test
  public void printAsciiAST() throws Exception {
    String sqlString = "select 1 from dual where a=b;";

    System.out.println(JSQLFormatter.formatToTree(sqlString));

    System.out.println(JSQLFormatter.formatToTree(sqlString, "outputFormat=ANSI"));

  }

  @Test
  public void testCollections() throws Exception {
    String sqlString = "select 1,2,3 from dual where a=b and b=c;";

    System.out.println(JSQLFormatter.formatToTree(sqlString));

    System.out.println(JSQLFormatter.formatToTree(sqlString, "outputFormat=ANSI"));

  }

  @Test
  public void testXML() throws Exception {
    String sqlString = "select 1 as b, 2, 3 from dual as tablename where a=b and b=c;";

    System.out.println(JSQLFormatter.formatToXML(sqlString));


  }
}
