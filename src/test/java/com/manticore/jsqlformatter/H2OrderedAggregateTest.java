/*
 * Manticore JSQLFormater is a SQL Beautifying and Formatting Software. Copyright (C) 2021 Andreas
 * Reichel <andreas@manticore-rpojects.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.manticore.jsqlformatter;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/** @author Andreas Reichel <andreas@manticore-projects.com> */
@RunWith(Parameterized.class)
public class H2OrderedAggregateTest extends StandardSelectTest {

  @Parameterized.Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> getSqlMap() {
    return getSqlMap(H2OrderedAggregateTest.class);
  }

  public H2OrderedAggregateTest(String input, String expected) {
    super(input, expected);
  }
}
