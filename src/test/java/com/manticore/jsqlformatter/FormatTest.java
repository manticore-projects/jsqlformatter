/**
 * Manticore Projects JSQLFormatter is a SQL Beautifying and Formatting Software.
 * Copyright (C) 2026 Andreas Reichel <andreas@manticore-projects.com>
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormatTest {
  @Test
  void testStatementTerminator() throws Exception {
    String provided = "Select 1";
    String expected = "SELECT 1";

    String result = JSQLFormatter.format(provided, "statementTerminator=NONE");
    Assertions.assertEquals(expected, result);
  }
}
