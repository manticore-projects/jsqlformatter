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

package jsqlformatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/** @author Andreas Reichel <andreas@manticore-projects.com> */
@RunWith(Parameterized.class)
public class StandardSelectTest {
  
  public static Iterable<Object[]> getSqlMap(Class<? extends StandardSelectTest> clasz) {
    String resourceName = clasz.getCanonicalName().replace(".", "/").concat(".sql");
    
    LinkedHashMap<String, String> sqlMap = new LinkedHashMap<>();
    boolean start = false;
    boolean end;

    StringBuilder stringBuilder = new StringBuilder();
    InputStream inputStream;

    inputStream = ClassLoader.getSystemResourceAsStream(resourceName);

    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    String line;
    String k = "";
    try {
      while ((line = bufferedReader.readLine()) != null) {
        if (!start && line.startsWith("--")) k = line.substring(3).trim().toUpperCase();
        start = start || (!start && !line.startsWith("--") && line.trim().length() > 0);
        end = start && line.trim().endsWith(";");
        if (start) stringBuilder.append(line).append("\n");
        if (end) {
          sqlMap.put(k, stringBuilder.toString().trim());
          stringBuilder.setLength(0);
          start = false;
        }
      }
    } catch (IOException ex) {
      //          ETLConnection.logger.log(Level.SEVERE, null, ex);
    }

    Object[][] o = new Object[sqlMap.size()][2];
    int i = 0;
    for (Entry<String, String> e : sqlMap.entrySet()) {
      o[i][0] = e.getKey();
      o[i][1] = e.getValue();
      i++;
    }
    return Arrays.asList(o);
  }
  
  @Parameters(name = "{index}: {0}")
  public static Iterable<Object[]> getSqlMap() {
    return getSqlMap(StandardSelectTest.class);
  }

  protected String input;
  protected String expected;

  public StandardSelectTest(String input, String expected) {
    this.input = input;
    this.expected = expected;
  }

  /**
   * Test of format method, of class JSQLFormatter.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testFormat() throws Exception {
    String formatted = JSQLFormatter.format(expected);
    
    System.out.println("\n-- " + input);
    System.out.println(formatted);

    assertEquals(expected, formatted);
  }
}