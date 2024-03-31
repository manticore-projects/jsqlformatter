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
import net.sf.jsqlparser.statement.Statements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.manticore.jsqlformatter.CommentMap.COMMENT_PATTERN;
import static com.manticore.jsqlformatter.JSQLFormatter.SQUARED_BRACKET_QUOTATION_PATTERN;

/**
 * @author Andreas Reichel <andreas@manticore-projects.com>
 */

public class StandardFileTest {
  public final static String TEST_FOLDER_STR =
      "build/resources/test/com/manticore/jsqlformatter/standard";

  public static final FilenameFilter FILENAME_FILTER = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return name.toLowerCase().endsWith(".sql");
    }
  };

  public final static Logger LOGGER = Logger.getLogger(StandardFileTest.class.getName());

  public static Stream<Entry<SQLKeyEntry, String>> getSqlMap() {
    LinkedHashMap<SQLKeyEntry, String> sqlMap = new LinkedHashMap<>();

    for (File file : Objects.requireNonNull(new File(TEST_FOLDER_STR).listFiles(FILENAME_FILTER))) {
      boolean start = false;
      boolean end;

      StringBuilder stringBuilder = new StringBuilder();
      String line;
      String k = "";

      try (FileReader fileReader = new FileReader(file);
          BufferedReader bufferedReader = new BufferedReader(fileReader)) {
        while ((line = bufferedReader.readLine()) != null) {

          if (!start && line.startsWith("--") && !line.startsWith("-- @")) {
            k = line.substring(3).trim().toUpperCase();
          }

          start = start
              || (!line.startsWith("--") || line.startsWith("-- @")) && !line.trim().isEmpty();
          end = start && !line.startsWith("--") && line.trim().endsWith(";");

          if (start) {
            stringBuilder.append(line).append("\n");
          }

          if (end) {
            sqlMap.put(new SQLKeyEntry(file, k), stringBuilder.toString().trim());
            stringBuilder.setLength(0);
            start = false;
          }

        }
      } catch (IOException ex) {
        LOGGER.log(Level.SEVERE, "Failed to read " + file.getAbsolutePath(), ex);
      }
    }

    return sqlMap.entrySet().stream();
  }

  public String buildSqlString(final String originalSql, boolean laxDeparsingCheck) {
    String sql = COMMENT_PATTERN.matcher(originalSql).replaceAll("");
    if (laxDeparsingCheck) {
      String s = sql.replaceAll("\\n*\\s*;", ";").replaceAll("\\s+", " ")
          .replaceAll("\\s*([!/,()=+\\-*|\\{\\}\\[\\]<>:])\\s*", "$1").toLowerCase().trim();
      return !s.endsWith(";") ? s + ";" : s;
    } else {
      return sql;
    }
  }

  /**
   * Test of format method, of class JSQLFormatter.
   *
   */
  @ParameterizedTest(name = "{index} {0}: {1}")
  @MethodSource("getSqlMap")
  void testFormat(Entry<SQLKeyEntry, String> entry) throws Exception {
    String expected = entry.getValue();

    String formatted = JSQLFormatter.format(expected, "indentWidth=4", "keywordSpelling=UPPER",
        "functionSpelling=CAMEL", "objectSpelling=LOWER", "separation=BEFORE");

    // check if the formatted version matches the provided version
    Assertions.assertEquals(expected.trim(), formatted.trim());
  }

  /**
   * Test parsing the provided examples
   *
   */
  @ParameterizedTest(name = "{index} {0}: {1}")
  @MethodSource("getSqlMap")
  void testParser(Entry<SQLKeyEntry, String> entry) {
    String expected = entry.getValue();

    if (expected.length() <= new CommentMap(expected).getLength()) {
      LOGGER.warning("Skip empty statement, when found only comments: " + expected);
    } else {
      // check if the formatted statement still can be parsed and gives the same content
      String sqlStringFromStatement = buildSqlString(expected, true);

      boolean foundSquareBracketQuotes = SQUARED_BRACKET_QUOTATION_PATTERN.matcher(expected).find();
      try {
        Statements parsed = CCJSqlParserUtil.parseStatements(expected,
            parser -> parser.withSquareBracketQuotation(foundSquareBracketQuotes));

        String sqlStringFromDeparser = buildSqlString(parsed.toString(), true);

        Assertions.assertEquals(sqlStringFromStatement, sqlStringFromDeparser);
      } catch (Exception ex) {
        Assertions.fail("Failed to parse: " + expected);
      }
    }
  }

  public static class SQLKeyEntry implements Entry<File, String> {
    File key;
    String value;

    public SQLKeyEntry(File key, String value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public File getKey() {
      return key;
    }

    @Override
    public String getValue() {
      return value;
    }

    @Override
    public String setValue(String value) {
      return this.value = value;
    }

    @Override
    public String toString() {
      return value + " @ " + key.getName();
    }
  }
}
