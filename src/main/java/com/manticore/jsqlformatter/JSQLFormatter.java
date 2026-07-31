/**
 * Manticore Projects JSQLFormatter is a SQL Beautifying and Formatting Software.
 * Copyright (C) 2024 Andreas Reichel <andreas@manticore-projects.com>
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

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Alias.AliasColumn;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.ExplainStatement;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.merge.MergeInsert;
import net.sf.jsqlparser.statement.merge.MergeUpdate;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A powerful Java SQL Formatter based on the JSQLParser.
 *
 * <p>
 * Internally, the formatting work is split between this class (public API + stateless helpers +
 * enums + format-options state), {@link Renderer} (output buffer + cross-cutting helpers),
 * {@link FormatContext} (immutable per-frame context), and four visitor classes
 * ({@link StatementFormatter}, {@link SelectFormatter}, {@link FromItemFormatter},
 * {@link ExpressionFormatter}) that replace the legacy {@code if/instanceof} chains.
 *
 * @author <a href="mailto:andreas@manticore-projects.com">Andreas Reichel</a>
 * @version 0.1
 */
@SuppressWarnings({"PMD.CyclomaticComplexity"})
public class JSQLFormatter {

  public static final Pattern SQUARED_BRACKET_QUOTATION_PATTERN = Pattern.compile(
      "(((?!\\[\\d+])\\[.*]\\.\\.?)|(\\.\\[\\w+( +\\w+)*])|((?!\\s\\[\\d+])\\s\\[\\w+( +\\w+)*]))");

  private static final Logger LOGGER = Logger.getLogger(JSQLFormatter.class.getName());
  public static final AnsiFormat ANSI_FORMAT_LINE_NUMBER =
      new AnsiFormat(Attribute.BRIGHT_BLACK_BACK(), Attribute.DESATURATED());
  public static final AnsiFormat ANSI_FORMAT_KEYWORD =
      new AnsiFormat(Attribute.BLUE_TEXT(), Attribute.BOLD());
  public static final AnsiFormat ANSI_FORMAT_HINT = new AnsiFormat(Attribute.BRIGHT_BLUE_TEXT());
  public static final AnsiFormat ANSI_FORMAT_OPERATOR = new AnsiFormat(Attribute.BLUE_TEXT());
  public static final AnsiFormat ANSI_FORMAT_PARAMETER =
      new AnsiFormat(Attribute.YELLOW_TEXT(), Attribute.DESATURATED());
  public static final AnsiFormat ANSI_FORMAT_ALIAS =
      new AnsiFormat(Attribute.RED_TEXT(), Attribute.BOLD(), Attribute.DESATURATED());
  public static final AnsiFormat ANSI_FORMAT_FUNCTION = new AnsiFormat(Attribute.BRIGHT_RED_TEXT());
  public static final AnsiFormat ANSI_FORMAT_TYPE =
      new AnsiFormat(Attribute.YELLOW_TEXT(), Attribute.DESATURATED());

  private static SquaredBracketQuotation squaredBracketQuotation = SquaredBracketQuotation.AUTO;
  private static Separation separation = Separation.BEFORE;
  private static Spelling keywordSpelling = Spelling.UPPER;
  private static Spelling functionSpelling = Spelling.CAMEL;
  private static Spelling objectSpelling = Spelling.LOWER;
  private static OutputFormat outputFormat = OutputFormat.PLAIN;
  private static ShowLineNumbers showLineNumbers = ShowLineNumbers.NO;
  private static StatementTerminator statementTerminator = StatementTerminator.SEMICOLON;

  private static BackSlashQuoting backSlashQuoting = BackSlashQuoting.NO;
  private static int indentWidth = 4;
  private static String indentString = "    ";
  private static int lineCount = 0;

  // ===================================================================================
  // Configuration getters/setters
  // ===================================================================================

  public static SquaredBracketQuotation getSquaredBracketQuotation() {
    return squaredBracketQuotation;
  }

  public static void setSquaredBracketQuotation(SquaredBracketQuotation squaredBracketQuotation) {
    JSQLFormatter.squaredBracketQuotation = squaredBracketQuotation;
  }

  public static BackSlashQuoting getBackSlashQuoting() {
    return backSlashQuoting;
  }

  public static void setBackSlashQuoting(BackSlashQuoting backSlashQuoting) {
    JSQLFormatter.backSlashQuoting = backSlashQuoting;
  }

  public static StatementTerminator getStatementTerminator() {
    return statementTerminator;
  }

  public static void setStatementTerminator(StatementTerminator statementTerminator) {
    JSQLFormatter.statementTerminator = statementTerminator;
  }

  public static Separation getSeparation() {
    return separation;
  }

  public static void setSeparation(Separation separation) {
    JSQLFormatter.separation = separation;
  }

  public static Spelling getKeywordSpelling() {
    return keywordSpelling;
  }

  public static void setKeywordSpelling(Spelling keywordSpelling) {
    JSQLFormatter.keywordSpelling = keywordSpelling;
  }

  public static Spelling getFunctionSpelling() {
    return functionSpelling;
  }

  public static void setFunctionSpelling(Spelling functionSpelling) {
    JSQLFormatter.functionSpelling = functionSpelling;
  }

  public static Spelling getObjectSpelling() {
    return objectSpelling;
  }

  public static void setObjectSpelling(Spelling objectSpelling) {
    JSQLFormatter.objectSpelling = objectSpelling;
  }

  public static OutputFormat getOutputFormat() {
    return outputFormat;
  }

  public static void setOutputFormat(OutputFormat outputFormat) {
    JSQLFormatter.outputFormat = outputFormat;
  }

  public static int getIndentWidth() {
    return indentWidth;
  }

  public static void setIndentWidth(int indentWidth) {
    JSQLFormatter.indentWidth = indentWidth;

    char[] chars = new char[indentWidth];
    Arrays.fill(chars, ' ');

    indentString = new String(chars);
  }

  public static String getIndentString() {
    return indentString;
  }

  public static void setIndentString(String indentString) {
    JSQLFormatter.indentString = indentString;
  }

  // ===================================================================================
  // Stateless helper methods (package-private so visitor classes can call them).
  // These methods only depend on the static config fields above and the StringBuilder argument.
  // ===================================================================================

  static String toCamelCase(String s) {
    StringBuilder camelCaseString = new StringBuilder();

    String[] nameParts = s.split("_");
    int i = 0;
    for (String part : nameParts) {
      if (i > 0) {
        camelCaseString.append("_");
      }
      camelCaseString.append(part.substring(0, 1).toUpperCase())
          .append(part.substring(1).toLowerCase());
      i++;
    }
    return camelCaseString.toString();
  }

  static StringBuilder appendKeyWord(StringBuilder builder, OutputFormat format, String keyword,
      String before, String after) {

    String s;
    switch (keywordSpelling) {
      case UPPER:
        s = keyword.toUpperCase();
        break;
      case LOWER:
        s = keyword.toLowerCase();
        break;
      case CAMEL:
        s = toCamelCase(keyword);
        break;
      default:
        s = keyword;
    }

    switch (format) {
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_KEYWORD.format(s)).append(after);
        break;
      case HTML:
        builder.append(before).append("<span style=\"color:blue; font-style:bold;\">").append(s)
            .append("</span>").append(after);
        break;
      default:
        builder.append(before).append(s).append(after);
        break;
    }
    return builder;
  }

  static StringBuilder appendNormalizingTrailingWhiteSpace(StringBuilder builder, String s) {
    if (builder.length() > 0) {
      int pos = builder.length() - 1;
      char lastChar = builder.charAt(pos);
      if (lastChar == ' ') {
        while (lastChar == ' ' && pos > 0) {
          pos--;
          lastChar = builder.charAt(pos);
        }
        builder.setLength(pos + 1);
      }
    }
    builder.append(s);
    return builder;
  }

  static StringBuilder appendNormalizedLineBreak(StringBuilder builder) {
    switch (showLineNumbers) {
      case YES:
        lineCount++;

        String lineCountStr = "0000" + lineCount;
        lineCountStr = lineCountStr.substring(lineCountStr.length() - 5);
        lineCountStr += " | ";

        StringBuilder fillerStr = new StringBuilder();
        int adjust = getIndentWidth() - lineCountStr.length() % getIndentWidth();
        fillerStr.append(" ".repeat(Math.max(0, adjust)));

        switch (outputFormat) {
          case ANSI:
            return appendNormalizingTrailingWhiteSpace(builder, Ansi.RESET
                + ANSI_FORMAT_LINE_NUMBER.format("\n" + lineCountStr) + Ansi.RESET + fillerStr);
          case HTML:
            return builder.append("\n")
                .append("<span style=\"color:light-grey; font-size:8pt; font-style:normal;\">")
                .append(lineCountStr).append("</span>").append(fillerStr);
          default:
            return appendNormalizingTrailingWhiteSpace(builder, "\n" + lineCountStr + fillerStr);
        }
      default:
        return appendNormalizingTrailingWhiteSpace(builder, "\n");
    }
  }

  static StringBuilder appendHint(StringBuilder builder, OutputFormat format, String hint,
      String before, String after) {

    String s;
    switch (keywordSpelling) {
      case UPPER:
        s = hint.toUpperCase();
        break;
      case LOWER:
        s = hint.toLowerCase();
        break;
      case CAMEL:
        s = toCamelCase(hint);
        break;
      default:
        s = hint;
    }

    switch (format) {
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_HINT.format(s)).append(after);
        break;
      case HTML:
        builder.append(before).append("<span style=\"color:light-blue; font-style:thin\">")
            .append(s).append("</span>").append(after);
        break;
      default:
        builder.append(before).append(s).append(after);
        break;
    }
    return builder;
  }

  static StringBuilder appendOperator(StringBuilder builder, OutputFormat format, String operator,
      String before, String after) {

    String s;
    switch (keywordSpelling) {
      case UPPER:
        s = operator.toUpperCase();
        break;
      case LOWER:
        s = operator.toLowerCase();
        break;
      case CAMEL:
        s = toCamelCase(operator);
        break;
      default:
        s = operator;
    }

    switch (format) {
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_OPERATOR.format(s)).append(after);
        break;
      case HTML:
        builder.append(before).append("<span style=\"color:blue; font-style:normal;\">").append(s)
            .append("</span>").append(after);
        break;
      default:
        builder.append(before).append(s).append(after);
        break;
    }
    return builder;
  }

  static StringBuilder appendValue(StringBuilder builder, OutputFormat format, String value,
      String before, String after) {
    switch (format) {
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_PARAMETER.format(value)).append(after);
        break;
      case HTML:
        builder.append(before).append("<span style=\"color:yellow; font-style:normal;\">")
            .append(value).append("</span>").append(after);
        break;
      default:
        builder.append(before).append(value).append(after);
        break;
    }
    return builder;
  }

  static StringBuilder appendAlias(StringBuilder builder, OutputFormat format, String alias,
      String before, String after) {

    String s;
    if (alias.trim().startsWith("\"") || alias.trim().startsWith("[")) {
      s = alias;
    } else {
      switch (objectSpelling) {
        case UPPER:
          s = alias.toUpperCase();
          break;
        case LOWER:
          s = alias.toLowerCase();
          break;
        case CAMEL:
          s = toCamelCase(alias);
          break;
        default:
          s = alias;
      }
    }

    switch (format) {
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_ALIAS.format(s)).append(after);
        break;
      case HTML:
        builder.append(before).append("<span style=\"color:red; font-style:bold;\">").append(s)
            .append("</span>").append(after);
        break;
      default:
        builder.append(before).append(s).append(after);
        break;
    }
    return builder;
  }

  static StringBuilder appendAlias(StringBuilder builder, OutputFormat format, Alias alias,
      String before, String after) {
    if (alias != null) {
      builder.append(before);

      if (alias.isUseAs()) {
        appendKeyWord(builder, outputFormat, "AS", "", " ");
      }

      String s;
      if (alias.getName().trim().startsWith("\"") || alias.getName().trim().startsWith("[")) {
        s = alias.getName();
      } else {
        switch (objectSpelling) {
          case UPPER:
            s = alias.getName().toUpperCase();
            break;
          case LOWER:
            s = alias.getName().toLowerCase();
            break;
          case CAMEL:
            s = toCamelCase(alias.getName());
            break;
          default:
            s = alias.getName();
        }
      }

      switch (format) {
        case ANSI:
          builder.append(ANSI_FORMAT_ALIAS.format(s));
          break;
        case HTML:
          builder.append("<span style=\"color:red; font-style:bold;\">").append(s)
              .append("</span>");
          break;
        default:
          builder.append(s);
          break;
      }

      if (alias.getAliasColumns() != null && !alias.getAliasColumns().isEmpty()) {
        int i = 0;
        builder.append("(");
        for (AliasColumn col : alias.getAliasColumns()) {
          appendObjectName(builder, outputFormat, col.name, i++ > 0 ? ", " : "", "");
          if (col.colDataType != null) {
            appendKeyWord(builder, outputFormat, col.colDataType.toString(), " ", "");
          }
        }
        builder.append(")");
      }
      builder.append(after);
    }

    return builder;
  }

  static StringBuilder appendObjectName(StringBuilder builder, OutputFormat format,
      String objectName, String before, String after) {

    StringBuilder nameBuilder = new StringBuilder();

    int j = 0;
    String[] parts = objectName.contains(".") ? objectName.split("\\.") : new String[] {objectName};
    for (String w : parts) {
      if (j > 0) {
        nameBuilder.append(".");
      }
      if (w.trim().startsWith("\"") || w.trim().startsWith("[")) {
        nameBuilder.append(w);
      } else {
        switch (objectSpelling) {
          case UPPER:
            nameBuilder.append(w.toUpperCase());
            break;
          case LOWER:
            nameBuilder.append(w.toLowerCase());
            break;
          case CAMEL:
            nameBuilder.append(toCamelCase(w));
            break;
        }
      }
      j++;
    }

    switch (format) {
      default:
        builder.append(before).append(nameBuilder).append(after);
        break;
    }
    return builder;
  }

  static StringBuilder appendFunction(StringBuilder builder, OutputFormat format, String function,
      String before, String after) {

    String s;
    switch (functionSpelling) {
      case UPPER:
        s = function.toUpperCase();
        break;
      case LOWER:
        s = function.toLowerCase();
        break;
      case CAMEL:
        s = toCamelCase(function);
        break;
      default:
        s = function;
    }

    switch (format) {
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_FUNCTION.format(s)).append(after);
        break;
      case HTML:
        builder.append(before).append("<span style=\"color:light-red; font-style:italic;\">")
            .append(s).append("</span>").append(after);
        break;
      default:
        builder.append(before).append(s).append(after);
        break;
    }
    return builder;
  }

  static StringBuilder appendType(StringBuilder builder, OutputFormat format, String type,
      String before, String after) {

    String s;
    switch (keywordSpelling) {
      case UPPER:
        s = type.toUpperCase();
        break;
      case LOWER:
        s = type.toLowerCase();
        break;
      case CAMEL:
        s = toCamelCase(type);
        break;
      default:
        s = type;
    }

    switch (format) {
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_TYPE.format(s)).append(after);
        break;
      case HTML:
        builder.append(before).append("<span style=\"color:yellow; font-style:normal;\">").append(s)
            .append("</span>").append(after);
        break;
      default:
        builder.append(before).append(s).append(after);
        break;
    }
    return builder;
  }

  static int getLastLineLength(StringBuilder builder) {
    String lastLine = builder.substring(builder.lastIndexOf("\n") + 1);
    lastLine = lastLine.replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "");

    return lastLine.length();
  }

  static int getSubIndent(StringBuilder builder, boolean moveToTab) {
    int lastLineLength = getLastLineLength(builder);

    int subIndent = lastLineLength / indentWidth + (lastLineLength % indentWidth > 0 ? 1 : 0);

    for (int i = lastLineLength; moveToTab && i < subIndent * indentWidth; i++) {
      builder.append(" ");
    }

    return subIndent;
  }

  // ===================================================================================
  // Public file-path helpers
  // ===================================================================================

  public static File getAbsoluteFile(String filename) {
    String homePath = new File(System.getProperty("user.home")).toURI().getPath();

    String resolvedName = filename.replaceFirst("~", Matcher.quoteReplacement(homePath))
        .replaceFirst("\\$\\{user.home}", Matcher.quoteReplacement(homePath));

    File f = new File(resolvedName);
    if (!f.isAbsolute()) {
      Path basePath = Paths.get("").toAbsolutePath();

      Path resolvedPath = basePath.resolve(filename);
      Path absolutePath = resolvedPath.normalize();
      f = absolutePath.toFile();
    }
    return f;
  }

  public static String getAbsoluteFileName(String filename) {
    return getAbsoluteFile(filename).getAbsolutePath();
  }

  // ===================================================================================
  // Public formatting API
  // ===================================================================================

  @SuppressWarnings({"PMD.CyclomaticComplexity"})
  public static ArrayList<Exception> verify(String sqlStr, String... options) {
    ArrayList<Exception> exceptions = new ArrayList<>();

    applyFormattingOptions(options);

    Pattern SEMICOLON_PATTERN = Pattern.compile(";|$");
    Matcher m = SEMICOLON_PATTERN.matcher(sqlStr);
    ArrayList<Integer> semicolons = new ArrayList<>();

    while (m.find()) {
      semicolons.add(m.start());
    }

    m = CommentMap.COMMENT_PATTERN.matcher(sqlStr);
    while (m.find()) {
      int start = m.start();
      int end = m.end();

      int n = semicolons.size();
      for (int i = n - 1; i >= 0; i--) {
        int pos = semicolons.get(i);
        if (start <= pos && pos < end) {
          semicolons.remove(i);
        }
      }
    }

    int pos = 0;
    int length = sqlStr.length();
    int n = semicolons.size();
    for (int i = 0; i < n; i++) {
      int semicolonPos = semicolons.get(i);

      if (semicolonPos > pos) {
        String statementSql = sqlStr.substring(pos, Integer.min(semicolonPos + 1, length));
        pos = semicolonPos + 1;

        // we are at the end and find only remaining whitespace
        if (statementSql.trim().isEmpty()) {
          break;
        }

        boolean useSquareBracketQuotation;
        switch (squaredBracketQuotation) {
          case YES:
            useSquareBracketQuotation = true;
            LOGGER.log(Level.FINE, "Square Bracket Quotation set as {0}.",
                useSquareBracketQuotation);
            break;
          case NO:
            useSquareBracketQuotation = false;
            LOGGER.log(Level.FINE, "Square Bracket Quotation set as {0}.",
                useSquareBracketQuotation);
            break;
          case AUTO:
          default:
            useSquareBracketQuotation =
                SQUARED_BRACKET_QUOTATION_PATTERN.matcher(statementSql).find();
            LOGGER.log(Level.FINE, "Square Bracket Quotation auto-detected as {0}.",
                useSquareBracketQuotation);
        }
        try {
          CCJSqlParserUtil.parse(statementSql,
              parser -> parser.withSquareBracketQuotation(useSquareBracketQuotation));

        } catch (Exception ex1) {
          exceptions.add(new Exception("Cannot parse the Statement:\n" + statementSql, ex1));
        }
      } else {
        break;
      }
    }
    return exceptions;
  }

  @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength"})
  public static String format(String sqlStr, String... options) throws Exception {
    applyFormattingOptions(options);

    StringBuilder builder = new StringBuilder();

    int indent = 0;
    lineCount = 0;

    Pattern SEMICOLON_PATTERN = Pattern.compile(";|$|\\n\\n\\n");
    Matcher m = SEMICOLON_PATTERN.matcher(sqlStr);
    ArrayList<Integer> semicolons = new ArrayList<>();

    while (m.find()) {
      semicolons.add(m.start());
    }

    m = CommentMap.COMMENT_PATTERN.matcher(sqlStr);
    while (m.find()) {
      int start = m.start();
      int end = m.end();

      int n = semicolons.size();
      for (int i = n - 1; i >= 0; i--) {
        int pos = semicolons.get(i);
        if (start <= pos && pos < end) {
          semicolons.remove(i);
        }
      }
    }

    int pos = 0;
    int length = sqlStr.length();
    int n = semicolons.size();
    for (int i = 0; i < n; i++) {
      int semicolonPos = semicolons.get(i);

      if (semicolonPos > pos) {
        String statementSql = sqlStr.substring(pos, Integer.min(semicolonPos + 1, length));
        pos = semicolonPos + 1;

        // we are at the end and find only remaining whitespace or comments
        if (statementSql.trim().isEmpty() || i == n - 1 && statementSql.trim().startsWith("--")
            || i == n - 1 && statementSql.trim().startsWith("/*")) {
          break;
        }

        StringBuilder statementBuilder = new StringBuilder();

        boolean useSquareBracketQuotation;
        switch (squaredBracketQuotation) {
          case YES:
            useSquareBracketQuotation = true;
            LOGGER.log(Level.FINE, "Square Bracket Quotation set as {0}.",
                useSquareBracketQuotation);
            break;
          case NO:
            useSquareBracketQuotation = false;
            LOGGER.log(Level.FINE, "Square Bracket Quotation set as {0}.",
                useSquareBracketQuotation);
            break;
          case AUTO:
          default:
            useSquareBracketQuotation =
                SQUARED_BRACKET_QUOTATION_PATTERN.matcher(statementSql).find();
            LOGGER.log(Level.FINE, "Square Bracket Quotation auto-detected as {0}.",
                useSquareBracketQuotation);
        }

        boolean useBackSlashQuoting;
        if (Objects.requireNonNull(backSlashQuoting) == BackSlashQuoting.YES) {
          useBackSlashQuoting = true;
          LOGGER.log(Level.FINE, "Back Slash Quoting set as {0}.", true);
        } else {
          useBackSlashQuoting = false;
          LOGGER.log(Level.FINE, "Back Slash Quoting set as {0}.", false);
        }

        CommentMap commentMap = new CommentMap(statementSql);

        Pattern DIRECTIVE_PATTERN = Pattern.compile("@JSQLFormatter\\s?\\((.*)\\)");
        for (Comment comment : commentMap.values()) {
          Matcher m1 = DIRECTIVE_PATTERN.matcher(comment.text);
          if (m1.find()) {
            String[] keyValuePairs = m1.group(1).split(",");
            applyFormattingOptions(keyValuePairs);
          }
        }

        try {
          Statement statement =
              CCJSqlParserUtil.parse(CCJSqlParserUtil.sanitizeSingleSql(statementSql),
                  parser -> parser.withSquareBracketQuotation(useSquareBracketQuotation)
                      .withBackslashEscapeCharacter(useBackSlashQuoting).withTimeOut(60000)
                      .withUnsupportedStatements(false));

          // Single dispatch via the visitor framework, replacing the legacy if/instanceof chain.
          // Quirk preserved: ExplainStatement writes directly to the OUTER builder (not
          // statementBuilder); all other statement types go through statementBuilder so their
          // output is wrapped by the comment-map insertion below.
          if (isHandledStatement(statement)) {
            StringBuilder target =
                (statement instanceof ExplainStatement) ? builder : statementBuilder;
            Renderer renderer = new Renderer(target);
            renderer.renderStatement(statement, FormatContext.of(indent));
          } else if (statement != null) {
            try {
              statementBuilder.append("\n").append(statement);
            } catch (Exception ex) {
              throw new UnsupportedOperationException(
                  "The " + statement.getClass().getName() + " Statement is not supported yet.");
            }
          }

          switch (statementTerminator) {
            case SEMICOLON:
              appendNormalizedLineBreak(statementBuilder).append(";\n");
              break;
            case NONE:
              appendNormalizedLineBreak(statementBuilder).append("\n\n");
              break;
            case GO:
              appendNormalizedLineBreak(statementBuilder).append("GO\n");
              break;
            case BACKSLASH:
              appendNormalizedLineBreak(statementBuilder).append("\\\n");
              break;
          }

          builder.append(commentMap.isEmpty() ? statementBuilder
              : commentMap.insertComments(statementBuilder, outputFormat));

        } catch (Exception ex1) {
          if (statementSql.trim().length() <= commentMap.getLength()) {
            LOGGER.info("Found only comments, but no SQL code.");
            builder.append(statementSql);
          } else {

            LOGGER.log(Level.WARNING, "Failed for format statement between \n" + statementSql, ex1);

            builder.append("-- failed to format start\n").append(statementSql)
                .append("\n-- failed to format end\n").append("\n");
          }
        }
      } else {
        break;
      }
    }

    if (outputFormat == OutputFormat.HTML) {
      builder = new StringBuilder().append("<html>\n").append("<head>\n")
          .append("<title>SQL Statement's Java Object Tree</title>\n").append("</head>\n")
          .append("<body>\n").append("<pre style=\"font-size:-2;background-color:#EFEFEF;\">\n")
          .append(builder).append("\n</pre>\n").append("</body>\n").append("</html>");
    }

    return builder.toString().trim();
  }

  /** True iff our visitor framework knows how to dispatch this statement type. */
  private static boolean isHandledStatement(Statement statement) {
    return statement instanceof Select || statement instanceof Update || statement instanceof Insert
        || statement instanceof Merge || statement instanceof Delete
        || statement instanceof Truncate || statement instanceof CreateTable
        || statement instanceof CreateIndex || statement instanceof CreateView
        || statement instanceof Alter || statement instanceof ExplainStatement;
  }

  public static StringBuilder formatToJava(String sqlStr, int indent, String... options)
      throws Exception {
    String formatted = format(sqlStr, options);
    StringReader stringReader = new StringReader(formatted);
    BufferedReader bufferedReader = new BufferedReader(stringReader);
    String line;
    StringBuilder builder = new StringBuilder();
    int i = 0;
    while ((line = bufferedReader.readLine()) != null) {
      if (i > 0) {
        builder.append(" ".repeat(Math.max(0, indent - 2)));
        builder.append("+ ");
      } else {
        builder.append(" ".repeat(Math.max(0, indent)));
      }
      builder.append("\"").append(line).append("\"\n");
      i++;
    }
    return builder;
  }

  @SuppressWarnings({"PMD.CyclomaticComplexity"})
  public static void applyFormattingOptions(String[] options) {
    // set the formatting options
    if (options != null) {
      for (String s : options) {
        String[] o = s.split("=");
        if (o.length == 2) {
          LOGGER.log(Level.FINE, "Found Formatting Option {0} = {1}", o);

          String key = o[0].trim();
          String value = o[1].trim();

          if (key.equalsIgnoreCase(FormattingOption.OUTPUT_FORMAT.toString())) {
            try {
              outputFormat = OutputFormat.valueOf(value.toUpperCase());
            } catch (Exception ex) {
              LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
            }

          } else if (key.equalsIgnoreCase(FormattingOption.KEYWORD_SPELLING.toString())) {
            try {
              keywordSpelling = Spelling.valueOf(value.toUpperCase());
            } catch (Exception ex) {
              LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
            }

          } else if (key.equalsIgnoreCase(FormattingOption.FUNCTION_SPELLING.toString())) {
            try {
              functionSpelling = Spelling.valueOf(value.toUpperCase());
            } catch (Exception ex) {
              LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
            }

          } else if (key.equalsIgnoreCase(FormattingOption.OBJECT_SPELLING.toString())) {
            try {
              objectSpelling = Spelling.valueOf(value.toUpperCase());
            } catch (Exception ex) {
              LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
            }

          } else if (key.equalsIgnoreCase(FormattingOption.SEPARATION.toString())) {
            try {
              separation = Separation.valueOf(value.toUpperCase());
            } catch (Exception ex) {
              LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
            }

          } else if (key.equalsIgnoreCase(FormattingOption.SQUARE_BRACKET_QUOTATION.toString())) {
            try {
              squaredBracketQuotation = SquaredBracketQuotation.valueOf(value.toUpperCase());
            } catch (Exception ex) {
              LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
            }

          } else if (key.equalsIgnoreCase(FormattingOption.BACKSLASH_QUOTING.toString())) {
            try {
              backSlashQuoting = BackSlashQuoting.valueOf(value.toUpperCase());
            } catch (Exception ex) {
              LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
            }

          } else if (key.equalsIgnoreCase(FormattingOption.SHOW_LINE_NUMBERS.toString())) {
            try {
              showLineNumbers = ShowLineNumbers.valueOf(value.toUpperCase());
            } catch (Exception ex) {
              LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
            }

          } else if (key.equalsIgnoreCase(FormattingOption.INDENT_WIDTH.toString())) {
            try {
              indentWidth = Integer.parseInt(value);

              char[] chars = new char[indentWidth];
              Arrays.fill(chars, ' ');

              indentString = new String(chars);
            } catch (Exception ex) {
              LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
            }
          } else if (key.equalsIgnoreCase(FormattingOption.STATEMENT_TERMINATOR.toString())) {
            try {
              statementTerminator = StatementTerminator.valueOf(value.toUpperCase());
            } catch (Exception ex) {
              LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
            }

          } else {
            LOGGER.log(Level.WARNING, "Unknown Formatting Option {0} = {1} ", o);
          }

        } else {
          LOGGER.log(Level.WARNING, "Invalid Formatting Option {0}", s);
        }
      }
    }
  }

  // ===================================================================================
  // Public select-item helpers (kept with their original signatures for backward
  // compatibility — they delegate to a fresh Renderer wrapping the supplied builder).
  // ===================================================================================

  public static void appendSelectItemList(List<SelectItem<?>> selectItems, StringBuilder builder,
      int subIndent, int i, BreakLine bl, int indent) throws UnsupportedOperationException {
    Renderer renderer = new Renderer(builder);
    int j = i;
    for (SelectItem<?> selectItem : selectItems) {
      Alias alias = selectItem.getAlias();
      Expression expression = selectItem.getExpression();
      renderer.renderExpression(expression,
          FormatContext.of(subIndent, j++, selectItems.size(), true, bl).withAlias(alias));
    }
  }

  public static void appendColumnSelectItemList(List<SelectItem<Column>> selectItems,
      StringBuilder builder, int subIndent, int i, BreakLine bl, int indent)
      throws UnsupportedOperationException {
    Renderer renderer = new Renderer(builder);
    int j = i;
    for (SelectItem<?> selectItem : selectItems) {
      Alias alias = selectItem.getAlias();
      Expression expression = selectItem.getExpression();
      renderer.renderExpression(expression,
          FormatContext.of(subIndent, j++, selectItems.size(), true, bl).withAlias(alias));
    }
  }

  public static void appendMergeUpdate(MergeUpdate update, StringBuilder builder, int indent) {
    new Renderer(builder).appendMergeUpdate(update, indent);
  }

  public static void appendMergeInsert(MergeInsert insert, StringBuilder builder, int indent,
      int i) {
    new Renderer(builder).appendMergeInsert(insert, indent, i);
  }

  // ===================================================================================
  // Enums
  // ===================================================================================

  public enum OutputFormat {
    PLAIN, ANSI, HTML, RTF, XSLFO
  }

  public enum Spelling {
    UPPER, LOWER, CAMEL, KEEP
  }

  public enum Separation {
    BEFORE, AFTER
  }

  public enum BreakLine {
    NEVER // keep all arguments on one line
    , AS_NEEDED // only when more than 3 arguments
    , AFTER_FIRST // break all after the first argument
    , ALWAYS // break all arguments to a new line
  }

  public enum SquaredBracketQuotation {
    AUTO, YES, NO
  }

  public enum ShowLineNumbers {
    YES, NO
  }

  public enum BackSlashQuoting {
    YES, NO
  }

  public enum StatementTerminator {
    SEMICOLON, NONE, GO, BACKSLASH
  }

  public enum FormattingOption {
    SQUARE_BRACKET_QUOTATION("squareBracketQuotation")

    , BACKSLASH_QUOTING("backSlashQuoting")

    , OUTPUT_FORMAT("outputFormat")

    , KEYWORD_SPELLING("keywordSpelling")

    , FUNCTION_SPELLING("functionSpelling")

    , OBJECT_SPELLING("objectSpelling")

    , SEPARATION("separation")

    , INDENT_WIDTH("indentWidth")

    , SHOW_LINE_NUMBERS("showLineNumbers")

    , STATEMENT_TERMINATOR("statementTerminator")

    ;

    public final String optionName;

    FormattingOption(String optionName) {
      this.optionName = optionName;
    }

    @Override
    public String toString() {
      return optionName;
    }

    public void addFormatterOption(String value, ArrayList<String> formatterOptions) {
      formatterOptions.add(optionName + "=" + value);
    }
  }
}
