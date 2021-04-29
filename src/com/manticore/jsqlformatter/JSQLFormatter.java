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

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.ReferentialAction;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterExpression;
import net.sf.jsqlparser.statement.alter.AlterOperation;
import net.sf.jsqlparser.statement.alter.ConstraintState;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.*;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.create.view.ForceOption;
import net.sf.jsqlparser.statement.create.view.TemporaryOption;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.merge.MergeInsert;
import net.sf.jsqlparser.statement.merge.MergeUpdate;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.select.OrderByElement.NullOrdering;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

/**
 * A powerful Java SQL Formatter based on the JSQLParser.
 *
 * @author <a href="mailto:andreas@manticore-projects.com">Andreas Reichel</a>
 * @version 0.1
 */
public class JSQLFormatter {

  public static final Pattern SQUARED_BRACKET_QUOTATION_PATTERN =
      Pattern.compile(
          "(((?!\\[\\d+\\])\\[.*\\]\\.\\.?)|(\\.\\[\\w+( +\\w+)*\\])|((?!\\s\\[\\d+\\])\\s\\[\\w+( +\\w+)*\\]))");

  private static Separation separation = Separation.BEFORE;

  private static Spelling keywordSpelling = Spelling.UPPER;

  private static Spelling functionSpelling = Spelling.CAMEL;

  private static Spelling objectSpelling = Spelling.LOWER;

  private static OutputFormat outputFormat = OutputFormat.PLAIN;

  private static int indentWidth = 4;

  private static String indentString = "    ";

  private static final Logger LOGGER = Logger.getLogger(JSQLFormatter.class.getName());

  private static final AnsiFormat ANSI_FORMAT_KEYWORD =
      new AnsiFormat(Attribute.BLUE_TEXT(), Attribute.BOLD());

  private static final AnsiFormat ANSI_FORMAT_HINT = new AnsiFormat(Attribute.BRIGHT_BLUE_TEXT());

  private static final AnsiFormat ANSI_FORMAT_OPERATOR = new AnsiFormat(Attribute.BLUE_TEXT());

  private static final AnsiFormat ANSI_FORMAT_PARAMETER =
      new AnsiFormat(Attribute.YELLOW_TEXT(), Attribute.DESATURATED());

  private static final AnsiFormat ANSI_FORMAT_ALIAS =
      new AnsiFormat(Attribute.RED_TEXT(), Attribute.BOLD(), Attribute.DESATURATED());

  private static final AnsiFormat ANSI_FORMAT_FUNCTION =
      new AnsiFormat(Attribute.BRIGHT_RED_TEXT());

  private static final AnsiFormat ANSI_FORMAT_TYPE =
      new AnsiFormat(Attribute.YELLOW_TEXT(), Attribute.DESATURATED());

  private static void appendDecodeExpressionsList(
      ExpressionList parameters, BreakLine breakLine, StringBuilder builder, int indent) {
    int subIndent =
        breakLine.equals(BreakLine.NEVER) ? indent : getSubIndent(builder, indent, false);

    List<Expression> expressions = parameters.getExpressions();

    int i = 0;
    for (Expression expression : expressions) {
      switch (breakLine) {
        case AS_NEEDED:
          BreakLine bl = i == 0 || (i - 1) % 2 == 0 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
          appendExpression(expression, null, builder, subIndent, i, expressions.size(), true, bl);
          break;

        default:
          appendExpression(
              expression, null, builder, subIndent, i, expressions.size(), true, breakLine);
      }
      i++;
    }
  }

  static String toCamelCase(String s) {
    StringBuilder camelCaseString = new StringBuilder();

    String[] nameParts = s.split("_");
    int i = 0;
    for (String part : nameParts) {
      if (i > 0) camelCaseString.append("_");
      camelCaseString
          .append(part.substring(0, 1).toUpperCase())
          .append(part.substring(1).toLowerCase());
      i++;
    }
    return camelCaseString.toString();
  }

  private static StringBuilder appendKeyWord(
      StringBuilder builder, OutputFormat format, String keyword, String before, String after) {

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
      case PLAIN:
        builder.append(before).append(s).append(after);
        break;
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_KEYWORD.format(s)).append(after);
        break;
      default:
        builder.append(before).append(s).append(after);
        break;
    }
    return builder;
  }

  private static StringBuilder appendNormalizingTrailingWhiteSpace(
      StringBuilder builder, String s) {
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

  private static StringBuilder appendNormalizedLineBreak(StringBuilder builder) {
    return appendNormalizingTrailingWhiteSpace(builder, "\n");
  }

  private static StringBuilder appendNormalizedSpace(StringBuilder builder) {
    return appendNormalizingTrailingWhiteSpace(builder, " ");
  }

  private static StringBuilder appendHint(
      StringBuilder builder, OutputFormat format, String hint, String before, String after) {

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
      case PLAIN:
        builder.append(before).append(s).append(after);
        break;
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_HINT.format(s)).append(after);
        break;
      default:
        builder.append(before).append(s).append(after);
        break;
    }
    return builder;
  }

  private static StringBuilder appendOperator(
      StringBuilder builder, OutputFormat format, String operator, String before, String after) {
    switch (format) {
      case PLAIN:
        builder.append(before).append(operator).append(after);
        break;
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_OPERATOR.format(operator)).append(after);
        break;
      default:
        builder.append(before).append(operator).append(after);
        break;
    }
    return builder;
  }

  private static StringBuilder appendValue(
      StringBuilder builder, OutputFormat format, String value, String before, String after) {
    switch (format) {
      case PLAIN:
        builder.append(before).append(value).append(after);
        break;
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_PARAMETER.format(value)).append(after);
        break;
      default:
        builder.append(before).append(value).append(after);
        break;
    }
    return builder;
  }

  private static StringBuilder appendAlias(
      StringBuilder builder, OutputFormat format, String alias, String before, String after) {

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
      case PLAIN:
        builder.append(before).append(s).append(after);
        break;
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_ALIAS.format(s)).append(after);
        break;
      default:
        builder.append(before).append(s).append(after);
        break;
    }
    return builder;
  }

  private static StringBuilder appendObjectName(
      StringBuilder builder, OutputFormat format, String objectName, String before, String after) {

    StringBuilder nameBuilder = new StringBuilder();

    int j = 0;
    String[] parts = objectName.contains(".") ? objectName.split("\\.") : new String[] {objectName};
    for (String w : parts) {
      if (j > 0) nameBuilder.append(".");
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
      case PLAIN:
        builder.append(before).append(nameBuilder).append(after);
        break;
      case ANSI:
        builder.append(before).append(nameBuilder).append(after);
        break;
      default:
        builder.append(before).append(nameBuilder).append(after);
        break;
    }
    return builder;
  }

  private static StringBuilder appendFunction(
      StringBuilder builder, OutputFormat format, String function, String before, String after) {

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
      case PLAIN:
        builder.append(before).append(s).append(after);
        break;
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_FUNCTION.format(s)).append(after);
        break;
      default:
        builder.append(before).append(s).append(after);
        break;
    }
    return builder;
  }

  private static StringBuilder appendType(
      StringBuilder builder, OutputFormat format, String type, String before, String after) {

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
      case PLAIN:
        builder.append(before).append(s).append(after);
        break;
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_TYPE.format(s)).append(after);
        break;
      default:
        builder.append(before).append(s).append(after);
        break;
    }
    return builder;
  }

  private static int getLastLineLength(StringBuilder builder) {
    String lastLine = builder.substring(builder.lastIndexOf("\n") + 1);
    lastLine = lastLine.replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "");

    return lastLine.length();
  }

  private static int getSubIndent(StringBuilder builder, int indent, boolean moveToTab) {
    int lastLineLength = getLastLineLength(builder);

    int subIndent = lastLineLength / indentWidth + (lastLineLength % indentWidth > 0 ? 1 : 0);

    for (int i = lastLineLength; moveToTab && i < subIndent * indentWidth; i++) builder.append(" ");

    return subIndent;
  }

  private static void appendDelete(StringBuilder builder, Delete delete, int indent) {
    int i = 0;

    // der.append(com.diogonunes.jcolor.Ansi.generateCode(CLEAR()));
    appendKeyWord(builder, outputFormat, "DELETE", "", " ");

    OracleHint oracleHint = delete.getOracleHint();
    if (oracleHint != null) appendHint(builder, outputFormat, oracleHint.toString(), "", " ");

    List<Table> tables = delete.getTables();

    if (tables != null && tables.size() > 0) {
      int j = 0;
      for (Table table : tables) {
        switch (separation) {
          case AFTER:
            appendObjectName(
                builder,
                outputFormat,
                table.getFullyQualifiedName(),
                "",
                j < tables.size() - 1 ? ", " : " ");
            break;
          case BEFORE:
          default:
            appendObjectName(
                builder, outputFormat, table.getFullyQualifiedName(), j > 0 ? ", " : "", " ");
            break;
        }

        j++;
      }
    }

    appendKeyWord(builder, outputFormat, "FROM", "", " ");

    Table table = delete.getTable();
    Alias alias = table.getAlias();

    appendFromItem(table, alias, builder, indent, 0, 1);

    List<Join> joins = delete.getJoins();
    appendJoins(joins, builder, indent);

    Expression whereExpression = delete.getWhere();
    appendWhere(whereExpression, builder, indent);

    List<OrderByElement> orderByElements = delete.getOrderByElements();
    appendOrderByElements(orderByElements, builder, indent);

    Limit limit = delete.getLimit();
    if (limit != null) {
      // @todo: implement limit in DELETE
      throw new UnsupportedOperationException("Limit in DELETE is not supported yet.");
    }
  }

  public static File getAbsoluteFile(String filename) {
    String homePath = new File(System.getProperty("user.home")).toURI().getPath();

    filename = filename.replaceFirst("~", Matcher.quoteReplacement(homePath));
    filename = filename.replaceFirst("\\$\\{user.home\\}", Matcher.quoteReplacement(homePath));

    File f = new File(filename);

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

  /** @param args The Command Line Parameters. */
  public static void main(String[] args) throws Exception {
    Options options = new Options();

    options.addOption("i", "input-file", true, "The input SQL file or folder.");
    options.addOption("o", "output-file", true, "The out SQL file for the formatted statements.");

    OptionGroup formatOptions = new OptionGroup();
    formatOptions.addOption(
        Option.builder("f")
            .longOpt("format")
            .hasArg()
            .desc("The output-format.\n[PLAIN* ANSI HTML RTF]")
            .build());
    formatOptions.addOption(
        Option.builder(null).longOpt("ansi").desc("Output ANSI annotated text.").build());
    formatOptions.addOption(
        Option.builder(null).longOpt("html").desc("Output HTML annotated text.").build());
    options.addOptionGroup(formatOptions);

    OptionGroup indentOptions = new OptionGroup();
    indentOptions.addOption(
        Option.builder("t").longOpt("indent").hasArg().desc("The indent width.\n[2 4* 8]").build());
    indentOptions.addOption(Option.builder("2").desc("Indent with 2 characters.").build());
    indentOptions.addOption(Option.builder("8").desc("Indent with 8 characters.").build());
    options.addOptionGroup(indentOptions);

    options.addOption(
        Option.builder(null)
            .longOpt("keywordSpelling")
            .hasArg()
            .desc("Keyword spelling.\n[UPPER*, LOWER, CAMEL, KEEP]")
            .build());

    options.addOption(
        Option.builder(null)
            .longOpt("functionSpelling")
            .hasArg()
            .desc("Function name spelling.\n[UPPER, LOWER, CAMEL*, KEEP]")
            .build());

    options.addOption(
        Option.builder(null)
            .longOpt("objectSpelling")
            .hasArg()
            .desc("Object name spelling.\n[UPPER, LOWER*, CAMEL, KEEP]")
            .build());

    options.addOption(
        Option.builder(null)
            .longOpt("separation")
            .hasArg()
            .desc("Position of the field separator.\n[BEFORE*, AFTER]")
            .build());

    // create the parser
    CommandLineParser parser = new DefaultParser();
    try {
      // parse the command line arguments
      CommandLine line = parser.parse(options, args);

      ArrayList<String> formatterOptions = new ArrayList<>();

      if (line.hasOption("ansi")) formatterOptions.add("outputFormat=ANSI");
      if (line.hasOption("html")) formatterOptions.add("outputFormat=HTML");

      if (line.hasOption("2")) formatterOptions.add("indentWidth=2");
      if (line.hasOption("8")) formatterOptions.add("indentWidth=8");

      if (line.hasOption("indent"))
        formatterOptions.add("indentWidth=" + line.getOptionValue("indent"));

      if (line.hasOption("keywordSpelling"))
        formatterOptions.add("keywordSpelling=" + line.getOptionValue("keywordSpelling"));

      if (line.hasOption("functionSpelling"))
        formatterOptions.add("functionSpelling=" + line.getOptionValue("functionSpelling"));

      if (line.hasOption("objectSpelling"))
        formatterOptions.add("objectSpelling=" + line.getOptionValue("objectSpelling"));

      if (line.hasOption("separation"))
        formatterOptions.add("separation=" + line.getOptionValue("separation"));

      if (line.hasOption("help") || (line.getOptions().length == 0 && line.getArgs().length == 0)) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator((Comparator<Option>) null);

        String startupCommand =
            System.getProperty("java.vm.name").equalsIgnoreCase("Substrate VM")
                ? "./JSQLFormatter"
                : "java -jar JSQLFormatter.jar";

        formatter.printHelp(startupCommand, options, true);
        return;
      }

      File inputFile = null;
      if (line.hasOption("input-file")) {
        inputFile = getAbsoluteFile(line.getOptionValue("input-file"));

        if (!inputFile.canRead())
          throw new Exception(
              "Can't read the specified INPUT-FILE " + inputFile.getCanonicalPath());

        try (FileInputStream inputStream = new FileInputStream(inputFile); ) {
          String sqlStr = IOUtils.toString(inputStream, Charset.defaultCharset());
          System.out.println(
              "\n-- FROM "
                  + inputFile.getName()
                  + "\n"
                  + format(sqlStr, formatterOptions.toArray(new String[formatterOptions.size()])));
        } catch (Exception ex) {
          throw new Exception(
              "Error when reading from INPUT FILE " + inputFile.getAbsolutePath(), ex);
        }
      }

      List<String> argsList = line.getArgList();
      if (argsList.isEmpty() && !line.hasOption("input-file"))
        throw new Exception("No SQL statements provided for formatting.");
      else
        for (String s : argsList) {
          try {
            System.out.println(
                "\n-- FROM ARGUMENT LIST\n"
                    + format(s, formatterOptions.toArray(new String[formatterOptions.size()])));
          } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to format statement\n" + s, ex);
          }
        }

    } catch (ParseException ex) {
      LOGGER.log(Level.FINE, "Parsing failed.  Reason: " + ex.getMessage(), ex);

      HelpFormatter formatter = new HelpFormatter();
      formatter.setOptionComparator((Comparator<Option>) null);
      formatter.printHelp("java -jar H2MigrationTool.jar", options, true);

      throw new Exception("Could not parse the Command Line Arguments.", ex);
    }
  }

  /**
   * Format a list of SQL Statements.
   *
   * <p>SELECT, INSERT, UPDATE and MERGE statements are supported.
   *
   * @param thread The
   * @param sql The SQL Statements to beautify.
   * @param options The Formatting Options (List of "key = value" pairs).
   * @return The beautifully formatted SQL Statements, semi-colon separated.
   */
  @CEntryPoint(name = "format")
  public static CCharPointer format(IsolateThread thread, CCharPointer sql, CCharPointer options) {
    String sqlStr = CTypeConversion.toJavaString(sql);

    String[] optionStr = CTypeConversion.toJavaString(options).split(",");
    try {
      sqlStr = format(sqlStr, optionStr);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }

    try (final CTypeConversion.CCharPointerHolder holder = CTypeConversion.toCString(sqlStr)) {
      final CCharPointer result = holder.get();
      return result;
    }
  }

  public static String format(String sqlStr, String... options) throws Exception {
    ArrayList<Exception> exceptions = new ArrayList<>();

    applyFormattingOptions(options);

    StringBuilder builder = new StringBuilder();

    // All Known Implementing Classes:
    // Alter, AlterSequence, AlterView, Block, Comment, Commit, CreateFunction,
    // CreateFunctionalStatement, CreateIndex, CreateProcedure, CreateSchema, CreateSequence,
    // CreateSynonym, CreateTable, CreateView, DeclareStatement, Delete, DescribeStatement, Drop,
    // Execute, ExplainStatement, Grant, Insert, Merge, Replace, Select, SetStatement,
    // ShowColumnsStatement, ShowStatement, ShowTablesStatement, Truncate, Update, Upsert,
    // UseStatement, ValuesStatement

    int indent = 0;

    Pattern SEMICOLON_PATTERN = Pattern.compile(";|$");
    Matcher m = SEMICOLON_PATTERN.matcher(sqlStr);
    LinkedList<Integer> semicolons = new LinkedList<>();

    for (int i = 0; m.find(); i++) {
      semicolons.add(m.start());
    }

    m = CommentMap.COMMENT_PATTERN.matcher(sqlStr);
    while (m.find()) {
      int start = m.start();
      int end = m.end();

      for (int i = semicolons.size() - 1; i >= 0; i--) {
        if (start <= semicolons.get(i) && semicolons.get(i) < end) {
          semicolons.remove(i);
        }
      }
    }

    int pos = 0;
    int length = sqlStr.length();
    for (int i = 0; i < semicolons.size(); i++)
      if (semicolons.get(i) > pos) {
        String statementSql = sqlStr.substring(pos, Integer.min(semicolons.get(i) + 1, length));
        pos = semicolons.get(i) + 1;

        // we are at the end and find only remaining whitespace
        if (statementSql.trim().length() == 0) break;

        StringBuilder statementBuilder = new StringBuilder();

        boolean foundSquareBracketQuotes =
            SQUARED_BRACKET_QUOTATION_PATTERN.matcher(statementSql).find();
        LOGGER.log(
            Level.FINE, "MSQL Server Square Bracket Quations is {0}.", foundSquareBracketQuotes);

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
              CCJSqlParserUtil.parse(
                  statementSql,
                  parser -> parser.withSquareBracketQuotation(foundSquareBracketQuotes));

          if (statement instanceof Select) {
            Select select = (Select) statement;
            appendSelect(select, statementBuilder, indent, true);

          } else if (statement instanceof Update) {
            Update update = (Update) statement;
            appendUpdate(statementBuilder, update, indent);

          } else if (statement instanceof Insert) {
            Insert insert = (Insert) statement;
            appendInsert(statementBuilder, insert, indent);

          } else if (statement instanceof Merge) {
            Merge merge = (Merge) statement;
            appendMerge(statementBuilder, merge, indent);

          } else if (statement instanceof Delete) {
            Delete delete = (Delete) statement;
            appendDelete(statementBuilder, delete, indent);

          } else if (statement instanceof Truncate) {
            Truncate truncate = (Truncate) statement;
            appendTruncate(statementBuilder, truncate, indent);

          } else if (statement instanceof CreateTable) {
            CreateTable createTable = (CreateTable) statement;
            appendCreateTable(statementBuilder, createTable, indent);

          } else if (statement instanceof CreateIndex) {
            CreateIndex createIndex = (CreateIndex) statement;
            appendCreateIndex(statementBuilder, createIndex, indent);

          } else if (statement instanceof CreateView) {
            CreateView createView = (CreateView) statement;
            appendCreateView(statementBuilder, createView, indent);

          } else if (statement instanceof Alter) {
            Alter alter = (Alter) statement;
            appendAlter(statementBuilder, alter, indent);

          } else if (statement != null) {
            try {
              statementBuilder.append("\n").append(statement.toString());
            } catch (Exception ex) {
              throw new UnsupportedOperationException(
                  "The " + statement.getClass().getName() + " Statement is not supported yet.");
            }
          }
          appendNormalizedLineBreak(statementBuilder).append(";\n");
          builder.append(
              commentMap.isEmpty()
                  ? statementBuilder
                  : commentMap.insertComments(statementBuilder, outputFormat));

          appendNormalizedLineBreak(builder);
        } catch (Exception ex1) {

          LOGGER.log(Level.WARNING, "Failed for format statement between \n" + statementSql, ex1);

          exceptions.add(
              new Exception(
                  "Failed for format statement between " + " and " + "\n" + statementSql));
          builder
              .append("-- failed to format start\n")
              .append(statementSql)
              .append("\n-- failed to format end\n")
              .append("\n");
        }
      } else break;

    return builder.toString().trim();
  }

  public static void applyFormattingOptions(String[] options) {
    // set the formatting options
    for (String s : options) {
      String[] o = s.split("=");
      if (o.length == 2) {
        LOGGER.log(Level.FINE, "Found Formatting Option {0} = {1}", o);

        String key = o[0].trim();
        String value = o[1].trim();

        if (key.equalsIgnoreCase("outputFormat")) {
          try {
            outputFormat = OutputFormat.valueOf(value.toUpperCase());
          } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
          }

        } else if (key.equalsIgnoreCase("keywordSpelling")) {
          try {
            keywordSpelling = Spelling.valueOf(value.toUpperCase());
          } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
          }

        } else if (key.equalsIgnoreCase("functionSpelling")) {
          try {
            functionSpelling = Spelling.valueOf(value.toUpperCase());
          } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
          }

        } else if (key.equalsIgnoreCase("objectSpelling")) {
          try {
            objectSpelling = Spelling.valueOf(value.toUpperCase());
          } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
          }

        } else if (key.equalsIgnoreCase("separation")) {
          try {
            separation = Separation.valueOf(value.toUpperCase());
          } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
          }

        } else if (key.equalsIgnoreCase("indentWidth")) {
          try {
            indentWidth = Integer.valueOf(value);

            char[] chars = new char[indentWidth];
            Arrays.fill(chars, ' ');

            indentString = new String(chars);
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

  private static void appendMerge(StringBuilder builder, Merge merge, int indent) {
    int i = 0;

    appendKeyWord(builder, outputFormat, "MERGE", "", " ");
    OracleHint oracleHint = merge.getOracleHint();
    if (oracleHint != null) appendHint(builder, outputFormat, oracleHint.toString(), "", " ");

    appendKeyWord(builder, outputFormat, "INTO", "", " ");

    Table table = merge.getTable();
    Alias alias = table.getAlias();

    appendFromItem(table, alias, builder, indent, 0, 1);

    appendNormalizedLineBreak(builder);
    for (int j = 0; j < indent + 1; j++) builder.append(indentString);
    appendKeyWord(builder, outputFormat, "USING", "", " ");

    SubSelect select = merge.getUsingSelect();
    if (select != null) {
      alias = merge.getUsingAlias();

      int subIndent = getSubIndent(builder, indentWidth, true);

      appendExpression(select, alias, builder, subIndent, 0, 1, false, BreakLine.AFTER_FIRST);
    }

    table = merge.getUsingTable();
    if (table != null) {
      alias = table.getAlias();
      appendFromItem(table, alias, builder, indent, 0, 1);
    }

    Expression onExpression = merge.getOnCondition();
    if (onExpression != null) {
      appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent + 2; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "ON", "", " ( ");

      int subIndent = getSubIndent(builder, indentWidth, false);

      appendExpression(onExpression, null, builder, subIndent, 0, 1, false, BreakLine.AFTER_FIRST);

      appendNormalizingTrailingWhiteSpace(builder, " )");
    }

    MergeInsert insert = merge.getMergeInsert();
    if (insert != null) {
      appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "WHEN", "", " ");
      appendKeyWord(builder, outputFormat, "NOT", "", " ");
      appendKeyWord(builder, outputFormat, "MATCHED", "", " ");
      appendKeyWord(builder, outputFormat, "THEN", "", "\n");

      for (int j = 0; j < indent + 1; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "INSERT", "", " ");

      List<Column> columns = insert.getColumns();
      List<Expression> expressions = insert.getValues();

      if (columns != null) {
        builder.append("( ");
        int subIndent = getSubIndent(builder, indentWidth, false);

        for (Column column : columns) {
          appendExpression(
              column, null, builder, subIndent, i, columns.size(), true, BreakLine.AFTER_FIRST);
          i++;
        }
        appendNormalizingTrailingWhiteSpace(builder, " )\n");
      }

      i = 0;
      for (int j = 0; j < indent + 1; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "VALUES", "", " ( ");

      int subIndent = getSubIndent(builder, indentWidth, false);
      if (expressions != null)
        for (Expression expression : expressions) {
          appendExpression(
              expression,
              null,
              builder,
              subIndent,
              i,
              expressions.size(),
              true,
              BreakLine.AFTER_FIRST);
          i++;
        }
      appendNormalizingTrailingWhiteSpace(builder, " )");

      Expression whereCondition = insert.getWhereCondition();
      if (whereCondition != null) {
        appendNormalizedLineBreak(builder);
        for (int j = 0; j < indent + 1; j++) builder.append(indentString);
        appendKeyWord(builder, outputFormat, "WHERE", "", " ");

        subIndent = getSubIndent(builder, indentWidth, true);

        appendExpression(
            whereCondition, null, builder, subIndent, 0, 1, false, BreakLine.AFTER_FIRST);
      }
    }

    MergeUpdate update = merge.getMergeUpdate();
    if (update != null) {
      appendNormalizedLineBreak(builder);

      i = 0;
      for (int j = 0; j < indent; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "WHEN", "", " ");
      appendKeyWord(builder, outputFormat, "MATCHED", "", " ");
      appendKeyWord(builder, outputFormat, "THEN", "", "\n");

      for (int j = 0; j < indent + 1; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "UPDATE", "", " ");
      appendKeyWord(builder, outputFormat, "SET", "", " ");

      int subIndent = getSubIndent(builder, indentWidth, true);

      List<Column> columns = update.getColumns();
      List<Expression> expressions = update.getValues();

      if (columns != null)
        for (Column column : columns) {
          switch (separation) {
            case AFTER:
              appendExpression(
                  column,
                  null,
                  builder,
                  subIndent,
                  i,
                  columns.size(),
                  false,
                  BreakLine.AFTER_FIRST);
              builder.append(" = ");
              appendExpression(
                  expressions.get(i),
                  null,
                  builder,
                  subIndent,
                  i,
                  columns.size(),
                  true,
                  BreakLine.NEVER);
              break;
            case BEFORE:
              appendExpression(
                  column, null, builder, subIndent, i, columns.size(), true, BreakLine.AFTER_FIRST);
              builder.append(" = ");
              appendExpression(
                  expressions.get(i),
                  null,
                  builder,
                  subIndent,
                  0,
                  columns.size(),
                  false,
                  BreakLine.AFTER_FIRST);
              break;
          }

          i++;
        }

      Expression whereCondition = update.getWhereCondition();
      if (whereCondition != null) {
        appendNormalizedLineBreak(builder);
        for (int j = 0; j < indent + 1; j++) builder.append(indentString);
        appendKeyWord(builder, outputFormat, "WHERE", "", " ");

        subIndent = getSubIndent(builder, indentWidth, true);

        appendExpression(
            whereCondition, null, builder, subIndent, 0, 1, false, BreakLine.AFTER_FIRST);
      }

      Expression deleteWhereCondition = update.getDeleteWhereCondition();
      if (deleteWhereCondition != null) {
        appendNormalizedLineBreak(builder);
        for (int j = 0; j < indent + 1; j++) builder.append(indentString);
        appendKeyWord(builder, outputFormat, "DELETE", "", " ");
        appendKeyWord(builder, outputFormat, "WHERE", "", " ");

        subIndent = getSubIndent(builder, indentWidth, true);

        appendExpression(
            deleteWhereCondition, null, builder, subIndent, 0, 1, false, BreakLine.AFTER_FIRST);
      }
    }
  }

  private static void appendInsert(StringBuilder builder, Insert insert, int indent) {
    int i = 0;

    appendKeyWord(builder, outputFormat, "INSERT", "", " ");
    OracleHint oracleHint = insert.getOracleHint();
    if (oracleHint != null) appendHint(builder, outputFormat, oracleHint.toString(), "", " ");
    appendKeyWord(builder, outputFormat, "INTO", "", " ");

    Table table = insert.getTable();
    Alias alias = table.getAlias();

    appendFromItem(table, alias, builder, indent, 0, 1);

    List<Column> columns = insert.getColumns();
    if (columns != null) {
      builder.append(" (");
      for (Column column : columns) {
        appendExpression(
            column, null, builder, indent + 1, i, columns.size(), true, BreakLine.ALWAYS);
        i++;
      }
      appendNormalizingTrailingWhiteSpace(builder, " ) ");
    }

    if (insert.isUseValues()) {
      appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "VALUES", "", " ( ");

      ItemsList itemsList = insert.getItemsList();
      appendItemsList(
          itemsList,
          builder,
          alias,
          indent,
          columns != null ? BreakLine.AFTER_FIRST : BreakLine.AS_NEEDED);
      appendNormalizingTrailingWhiteSpace(builder, " ) ");

    } else {
      appendNormalizedLineBreak(builder);
      Select select = insert.getSelect();

      appendSelect(select, builder, indent, false);
    }
  }

  private static void appendUpdate(StringBuilder builder, Update update, int indent) {
    appendKeyWord(builder, outputFormat, "UPDATE", "", " ");

    OracleHint oracleHint = update.getOracleHint();
    if (oracleHint != null) appendHint(builder, outputFormat, oracleHint.toString(), "", " ");

    Table table = update.getTable();
    Alias alias = table.getAlias();

    appendFromItem(table, alias, builder, indent, 0, 1);

    int i = 0;
    appendNormalizedLineBreak(builder);
    for (int j = 0; j < indent; j++) builder.append(indentString);
    appendKeyWord(builder, outputFormat, "SET", "", " ");

    if (update.isUseSelect()) {
      builder.append("( ");

      int subIndent = getSubIndent(builder, indentWidth, true);

      List<Column> columns = update.getColumns();
      if (columns != null)
        for (Column column : columns) {
          appendExpression(
              column, null, builder, subIndent, i, columns.size(), true, BreakLine.AFTER_FIRST);
          i++;
        }
      appendNormalizingTrailingWhiteSpace(builder, " ) = ");

      Select select = update.getSelect();
      builder.append("( ");

      subIndent = getSubIndent(builder, indentWidth, true);

      appendSelect(select, builder, subIndent, false);
      appendNormalizingTrailingWhiteSpace(builder, " ) ");
    } else {
      List<Column> columns = update.getColumns();
      List<Expression> expressions = update.getExpressions();

      int subIndent = getSubIndent(builder, indentWidth, true);

      if (columns != null)
        for (Column column : columns) {
          switch (separation) {
            case AFTER:
              appendExpression(
                  column,
                  null,
                  builder,
                  subIndent,
                  i,
                  columns.size(),
                  false,
                  BreakLine.AFTER_FIRST);
              builder.append(" = ");
              appendExpression(
                  expressions.get(i),
                  null,
                  builder,
                  subIndent,
                  i,
                  columns.size(),
                  true,
                  BreakLine.NEVER);
              break;
            case BEFORE:
              appendExpression(
                  column, null, builder, subIndent, i, columns.size(), true, BreakLine.AFTER_FIRST);
              builder.append(" = ");
              appendExpression(
                  expressions.get(i),
                  null,
                  builder,
                  subIndent,
                  0,
                  columns.size(),
                  false,
                  BreakLine.AFTER_FIRST);
              break;
          }

          i++;
        }
    }

    i = 1;
    List<Join> joins = update.getJoins();
    appendJoins(joins, builder, indent);

    Expression whereExpression = update.getWhere();
    appendWhere(whereExpression, builder, indent);

    List<OrderByElement> orderByElements = update.getOrderByElements();
    appendOrderByElements(orderByElements, builder, indent);
  }

  private static void appendSelect(
      Select select, StringBuilder builder, int indent, boolean breakLineBefore) {
    List<WithItem> withItems = select.getWithItemsList();
    if (withItems != null && withItems.size() > 0) {
      int i = 0;
      appendKeyWord(builder, outputFormat, "WITH", "", " ");

      for (WithItem withItem : withItems) {
        appendWithItem(withItem, null, builder, indent, i, withItems.size());
        i++;
      }
    }

    SelectBody selectBody = select.getSelectBody();
    appendSelectBody(selectBody, null, builder, indent, breakLineBefore);
  }

  private static void appendSelectBody(
      SelectBody selectBody,
      Alias alias1,
      StringBuilder builder,
      int indent,
      boolean breakLineBefore) {

    // All Known Implementing Classes: PlainSelect, SetOperationList, ValuesStatement, WithItem
    if (selectBody instanceof PlainSelect) {
      PlainSelect plainSelect = (PlainSelect) selectBody;

      int i = 0;
      if (breakLineBefore) {
        builder.append("\n");
        for (int j = 0; j < indent; j++) builder.append(indentString);
      }
      appendKeyWord(builder, outputFormat, "SELECT", "", " ");

      OracleHint oracleHint = plainSelect.getOracleHint();
      if (oracleHint != null) appendHint(builder, outputFormat, oracleHint.toString(), "", " ");

      Distinct distinct = plainSelect.getDistinct();
      if (distinct != null) {
        if (distinct.isUseUnique()) {
          // @todo: implement Use Unique Distinct clause
          throw new UnsupportedOperationException("Unique DISTINCT not supported yet.");
        }

        if (distinct.getOnSelectItems() != null && distinct.getOnSelectItems().size() > 0) {
          // @todo: implement Use Unique Distinct clause
          throw new UnsupportedOperationException(
              "DISTINCT on select items are not supported yet.");
        }
        appendKeyWord(builder, outputFormat, "DISTINCT", "", " ");
      }

      int subIndent =
          (oracleHint != null || distinct != null)
              ? indent + 1
              : getSubIndent(builder, indent, plainSelect.getSelectItems().size() > 1);
      BreakLine bl =
          (oracleHint != null || distinct != null) ? BreakLine.ALWAYS : BreakLine.AFTER_FIRST;

      List<SelectItem> selectItems = plainSelect.getSelectItems();
      for (SelectItem selectItem : selectItems) {
        // All Known Implementing Classes:
        // AllColumns, AllTableColumns, SelectExpressionItem
        if (selectItem instanceof SelectExpressionItem) {
          SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;

          Alias alias = selectExpressionItem.getAlias();
          Expression expression = selectExpressionItem.getExpression();

          appendExpression(expression, alias, builder, subIndent, i, selectItems.size(), true, bl);
        } else if (selectItem instanceof AllColumns) {
          AllColumns allColumns = (AllColumns) selectItem;
          appendAllColumns(allColumns, builder, indent, i, selectItems.size());
        } else if (selectItem instanceof AllTableColumns) {
          AllTableColumns allTableColumns = (AllTableColumns) selectItem;
          appendAllTableColumns(allTableColumns, builder, indent, i, selectItems.size());
        } else if (selectItem != null) {
          throw new UnsupportedOperationException(selectItem.getClass().getName());
        }

        i++;
      }

      // All Known Implementing Classes: LateralSubSelect, ParenthesisFromItem,
      // SpecialSubSelect, SubJoin, SubSelect, Table, TableFunction, ValuesList
      FromItem fromItem = plainSelect.getFromItem();

      i = 0;
      appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "FROM", "", " ");

      appendFromItem(fromItem, builder, indent, i, 1);

      i++;

      List<Join> joins = plainSelect.getJoins();
      appendJoins(joins, builder, indent);

      Expression whereExpression = plainSelect.getWhere();
      appendWhere(whereExpression, builder, indent);

      GroupByElement groupByElement = plainSelect.getGroupBy();
      appendGroupByElement(groupByElement, builder, indent);

      Expression havingExpression = plainSelect.getHaving();
      appendHavingExpression(havingExpression, builder, indent);

      List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
      appendOrderByElements(orderByElements, builder, indent);
    } else if (selectBody instanceof SetOperationList) {
      SetOperationList setOperationList = (SetOperationList) selectBody;

      List<SetOperation> setOperations = setOperationList.getOperations();

      int k = 0;
      List<SelectBody> selects = setOperationList.getSelects();
      if (selects != null && selects.size() > 0) {
        for (SelectBody selectBody1 : selects) {
          if (k > 0 && setOperations != null && setOperations.size() >= k) {
            SetOperation setOperation = setOperations.get(k - 1);
            appendSetOperation(setOperation, builder, indent);
          }
          appendSelectBody(selectBody1, alias1, builder, indent, k > 0 || breakLineBefore);

          k++;
        }
      }
    } else if (selectBody != null) {
      throw new UnsupportedOperationException(selectBody.getClass().getName());
    }
  }

  private static void appendOrderByElements(
      List<OrderByElement> orderByElements, StringBuilder builder, int indent) {
    if (orderByElements != null) {
      int i = 0;
      appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "ORDER BY", "", " ");

      int subIndent = getSubIndent(builder, indent, orderByElements.size() > 1);

      for (OrderByElement orderByElement : orderByElements) {
        Expression expression = orderByElement.getExpression();
        appendExpression(
            expression,
            null,
            builder,
            subIndent,
            i,
            orderByElements.size(),
            true,
            BreakLine.AFTER_FIRST);

        if (orderByElement.isAscDescPresent()) {
          builder.append(" ");

          if (orderByElement.isAsc()) appendKeyWord(builder, outputFormat, "ASC", "", " ");
          else appendKeyWord(builder, outputFormat, "DESC", "", " ");
        }

        NullOrdering nullOrdering = orderByElement.getNullOrdering();
        if (NullOrdering.NULLS_FIRST.equals(nullOrdering))
          appendKeyWord(
              builder,
              outputFormat,
              "NULLS FIRST",
              orderByElement.isAscDescPresent() ? "" : " ",
              " ");

        if (NullOrdering.NULLS_LAST.equals(nullOrdering))
          appendKeyWord(
              builder,
              outputFormat,
              "NULLS LAST",
              orderByElement.isAscDescPresent() ? "" : " ",
              " ");

        i++;
      }
    }
  }

  private static void appendHavingExpression(
      Expression havingExpression, StringBuilder builder, int indent) {
    if (havingExpression != null) {
      appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "HAVING", "", " ");
      appendExpression(havingExpression, null, builder, indent, 0, 1, false, BreakLine.AFTER_FIRST);
    }
  }

  private static void appendGroupByElement(
      GroupByElement groupByElement, StringBuilder builder, int indent)
      throws UnsupportedOperationException {
    int i;
    if (groupByElement != null) {
      i = 0;
      appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "GROUP BY", "", " ");

      List groupingSets = groupByElement.getGroupingSets();
      List<Expression> groupByExpressions = groupByElement.getGroupByExpressions();

      if (groupingSets != null && groupingSets.size() > 0) {
        throw new UnsupportedOperationException("Grouping Sets are not supported yet.");
      }

      if (groupByExpressions != null && groupByExpressions.size() > 0) {
        for (Expression groupExpression : groupByExpressions) {
          appendExpression(
              groupExpression,
              null,
              builder,
              indent,
              i,
              groupByExpressions.size(),
              true,
              BreakLine.AFTER_FIRST);
        }
      }
    }
  }

  private static void appendWhere(Expression whereExpression, StringBuilder builder, int indent) {
    int i;
    if (whereExpression != null) {
      appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "WHERE", "", " ");
      appendExpression(whereExpression, null, builder, indent, 0, 1, false, BreakLine.AFTER_FIRST);
    }
  }

  private static void appendJoins(List<Join> joins, StringBuilder builder, int indent) {
    if (joins != null)
      for (Join join : joins) {

        if (join.isSimple()) {
          switch (separation) {
            case AFTER:
              builder.append(",");
          }
        }
        appendNormalizedLineBreak(builder);

        if (join.isSimple()) {
          for (int j = 0; j <= indent; j++) builder.append(indentString);

          switch (separation) {
            case BEFORE:
              builder.append(", ");
          }

        } else {
          for (int j = 0; j <= indent; j++) builder.append(indentString);

          if (join.isInner()) appendKeyWord(builder, outputFormat, "INNER", "", " ");
          if (join.isLeft()) appendKeyWord(builder, outputFormat, "LEFT", "", " ");
          if (join.isRight()) appendKeyWord(builder, outputFormat, "RIGHT", "", " ");
          if (join.isNatural()) appendKeyWord(builder, outputFormat, "NATURAL", "", " ");
          if (join.isCross()) appendKeyWord(builder, outputFormat, "CROSS", "", " ");
          if (join.isOuter()) appendKeyWord(builder, outputFormat, "OUTER", "", " ");
          if (join.isFull()) appendKeyWord(builder, outputFormat, "FULL", "", " ");

          appendKeyWord(builder, outputFormat, "JOIN", "", " ");
        }

        FromItem rightFromItem = join.getRightItem();
        appendFromItem(rightFromItem, builder, indent, 0, 1);

        Expression onExpression = join.getOnExpression();
        if (onExpression != null) {
          appendNormalizedLineBreak(builder);
          for (int j = 0; j <= indent + 1; j++) builder.append(indentString);
          appendKeyWord(builder, outputFormat, "ON", "", " ");

          appendExpression(
              onExpression, null, builder, indent + 2, 0, 1, false, BreakLine.AFTER_FIRST);
        }

        List<Column> usingColumns = join.getUsingColumns();
        if (usingColumns != null && usingColumns.size() > 0) {
          appendNormalizedLineBreak(builder);
          for (int j = 0; j <= indent + 2; j++) builder.append(indentString);
          appendKeyWord(builder, outputFormat, "USING", "", " ( ");
          int k = 0;
          for (Column column : usingColumns) {
            appendExpression(
                column,
                null,
                builder,
                indent + 3,
                k,
                usingColumns.size(),
                true,
                BreakLine.AFTER_FIRST);
            k++;
          }
          appendNormalizingTrailingWhiteSpace(builder, " )");
        }
      }
  }

  private static void appendWithItem(
      WithItem withItem, Alias alias, StringBuilder builder, int indent, int i, int n) {

    if (i > 0) {
      appendNormalizedLineBreak(builder);
      for (int j = 0; j <= indent; j++) builder.append(indentString);
    }

    switch (separation) {
      case BEFORE:
        appendAlias(builder, outputFormat, withItem.getName(), i > 0 ? ", " : "", " ");
        break;
      default:
        appendAlias(builder, outputFormat, withItem.getName(), "", " ");
    }

    List<SelectItem> selectItems = withItem.getWithItemList();
    if (selectItems != null && selectItems.size() > 0) {
      builder.append("( ");
      int k = 0;
      for (SelectItem selectItem : selectItems) {
        // @todo: write code for selectItems
        k++;
      }
      builder.append(" ) ");
    }

    appendKeyWord(builder, outputFormat, "AS", "", " (");
    appendSelectBody(withItem.getSelectBody(), null, builder, indent + 2, true);

    switch (separation) {
      case AFTER:
        appendNormalizingTrailingWhiteSpace(builder, i < n - 1 ? " )," : " )");
        break;
      case BEFORE:
        appendNormalizingTrailingWhiteSpace(builder, " )");
        break;
    }
  }

  private static void appendAllColumns(
      AllColumns allColumns, StringBuilder builder, int indent, int i, int n) {
    if (i > 0) {
      appendNormalizedLineBreak(builder);
      for (int j = 0; j <= indent; j++) builder.append(indentString);
    }
    switch (separation) {
      case AFTER:
        builder.append(allColumns.toString());
        appendNormalizingTrailingWhiteSpace(builder, i > n - 1 ? ", " : "");
        break;
      case BEFORE:
        builder.append(i > 0 ? ", " : "");
        builder.append(allColumns.toString());
    }
  }

  private static void appendStringList(
      Collection<String> strings,
      Alias alias,
      StringBuilder builder,
      int indent,
      boolean commaSeparated,
      BreakLine breakLine) {
    int i = 0;
    for (String s : strings) {
      appendString(s, alias, builder, indent, i, strings.size(), commaSeparated, breakLine);
      i++;
    }
  }

  private static void appendString(
      String s,
      Alias alias,
      StringBuilder builder,
      int indent,
      int i,
      int n,
      boolean commaSeparated,
      BreakLine breakLine) {

    if (i > 0 || breakLine.equals(BreakLine.ALWAYS)) {
      if (!breakLine.equals(BreakLine.NEVER)) {
        appendNormalizedLineBreak(builder);
        for (int j = 0; j < indent; j++) builder.append(indentString);
      }
    }

    switch (separation) {
      case AFTER:
        appendObjectName(builder, outputFormat, s, "", "");
        appendNormalizingTrailingWhiteSpace(builder, commaSeparated && i > n - 1 ? ", " : "");
        break;
      case BEFORE:
        builder.append(commaSeparated && i > 0 ? ", " : "");
        appendObjectName(builder, outputFormat, s, "", "");
    }
  }

  private static void appendExpression(
      Expression expression,
      Alias alias,
      StringBuilder builder,
      int indent,
      final int i,
      final int n,
      boolean commaSeparated,
      BreakLine breakLine) {

    if (i > 0 || breakLine.equals(BreakLine.ALWAYS)) {
      if (!breakLine.equals(BreakLine.NEVER)) {
        appendNormalizedLineBreak(builder);
        for (int j = 0; j < indent; j++) builder.append(indentString);
      }
    }

    switch (separation) {
      case BEFORE:
        builder.append(commaSeparated && i > 0 ? ", " : "");
    }

    if (expression instanceof Column) {
      Column column = (Column) expression;
      appendObjectName(builder, outputFormat, column.getFullyQualifiedName(), "", "");

    } else if (expression instanceof AndExpression) {
      AndExpression andExpression = (AndExpression) expression;
      appendExpression(
          andExpression.getLeftExpression(),
          null,
          builder,
          indent,
          i,
          n,
          false,
          BreakLine.AFTER_FIRST);

      appendNormalizedLineBreak(builder);
      for (int j = 0; j <= indent; j++) builder.append(indentString);
      appendOperator(builder, outputFormat, "AND", "", " ");

      appendExpression(
          andExpression.getRightExpression(),
          null,
          builder,
          indent + 1,
          i,
          n,
          false,
          BreakLine.AFTER_FIRST);

    } else if (expression instanceof OrExpression) {
      OrExpression orExpression = (OrExpression) expression;
      appendExpression(
          orExpression.getLeftExpression(),
          null,
          builder,
          indent,
          i,
          n,
          false,
          BreakLine.AFTER_FIRST);

      appendNormalizedLineBreak(builder);
      for (int j = 0; j <= indent; j++) builder.append(indentString);
      appendOperator(builder, outputFormat, "OR", "", " ");

      appendExpression(
          orExpression.getRightExpression(),
          null,
          builder,
          indent + 1,
          i,
          n,
          false,
          BreakLine.AFTER_FIRST);

    } else if (expression instanceof BinaryExpression) {
      BinaryExpression binaryExpression = (BinaryExpression) expression;
      appendExpression(
          binaryExpression.getLeftExpression(),
          null,
          builder,
          indent + 1,
          i,
          n,
          false,
          BreakLine.NEVER);

      if (i > 0 || breakLine.equals(BreakLine.ALWAYS)) {
        if (!breakLine.equals(BreakLine.NEVER)) {
          appendNormalizedLineBreak(builder);
          for (int j = 0; j <= indent + 1; j++) builder.append(indentString);
        }
      }
      appendOperator(builder, outputFormat, binaryExpression.getStringExpression(), " ", " ");

      appendExpression(
          binaryExpression.getRightExpression(),
          null,
          builder,
          indent + 1,
          i,
          n,
          false,
          BreakLine.NEVER);

    } else if (expression instanceof EqualsTo) {
      EqualsTo equalsTo = (EqualsTo) expression;

      builder.append(equalsTo.getLeftExpression());

      appendOperator(builder, outputFormat, "=", " ", " ");

      appendExpression(
          equalsTo.getRightExpression(),
          alias,
          builder,
          indent + 1,
          i,
          n,
          false,
          BreakLine.AFTER_FIRST);

    } else if (expression instanceof Parenthesis) {
      Parenthesis parenthesis = (Parenthesis) expression;
      builder.append("( ");
      appendExpression(
          parenthesis.getExpression(), null, builder, indent + 1, i, n, false, BreakLine.NEVER);
      appendNormalizingTrailingWhiteSpace(builder, " )");

    } else if (expression instanceof CaseExpression) {
      CaseExpression caseExpression = (CaseExpression) expression;
      appendKeyWord(builder, outputFormat, "CASE", "", " ");

      List<WhenClause> whenClauses = caseExpression.getWhenClauses();
      for (WhenClause whenClause : whenClauses) {
        appendNormalizedLineBreak(builder);
        for (int j = 0; j <= indent + 1; j++) builder.append(indentString);
        appendKeyWord(builder, outputFormat, "WHEN", "", " ");
        appendExpression(
            whenClause.getWhenExpression(),
            null,
            builder,
            indent + 3,
            0,
            1,
            false,
            BreakLine.AFTER_FIRST);

        appendNormalizedLineBreak(builder);
        for (int j = 0; j <= indent + 2; j++) builder.append(indentString);
        appendKeyWord(builder, outputFormat, "THEN", "", " ");
        appendExpression(
            whenClause.getThenExpression(),
            null,
            builder,
            indent + 1,
            0,
            1,
            false,
            BreakLine.AFTER_FIRST);
      }

      Expression elseExpression = caseExpression.getElseExpression();
      if (elseExpression != null) {
        appendNormalizedLineBreak(builder);
        for (int j = 0; j <= indent + 1; j++) builder.append(indentString);
        appendKeyWord(builder, outputFormat, "ELSE", "", " ");
        appendExpression(
            elseExpression, null, builder, indent + 1, 0, 1, false, BreakLine.AFTER_FIRST);
      }

      appendNormalizedLineBreak(builder);
      for (int j = 0; j <= indent; j++) builder.append(indentString);
      appendKeyWord(builder, outputFormat, "END", "", " ");

    } else if (expression instanceof StringValue) {
      StringValue stringValue = (StringValue) expression;
      appendValue(builder, outputFormat, stringValue.toString(), "", "");

    } else if (expression instanceof LongValue) {
      LongValue longValue = (LongValue) expression;
      appendValue(builder, outputFormat, longValue.toString(), "", "");

    } else if (expression instanceof DateValue) {
      DateValue dateValue = (DateValue) expression;
      appendValue(builder, outputFormat, dateValue.toString(), "", "");

    } else if (expression instanceof DoubleValue) {
      DoubleValue doubleValue = (DoubleValue) expression;
      appendValue(builder, outputFormat, doubleValue.toString(), "", "");

    } else if (expression instanceof NotExpression) {
      NotExpression notExpression = (NotExpression) expression;
      if (notExpression.isExclamationMark()) appendOperator(builder, outputFormat, "!", "", "");
      else appendOperator(builder, outputFormat, "NOT", "", " ");

      appendExpression(
          notExpression.getExpression(),
          null,
          builder,
          indent + 1,
          i,
          n,
          false,
          BreakLine.AFTER_FIRST);

    } else if (expression instanceof NextValExpression) {
      NextValExpression nextValExpression = (NextValExpression) expression;
      if (nextValExpression.isUsingNextValueFor())
        appendOperator(builder, outputFormat, "NEXT VALUE FOR", "", " ");
      else appendOperator(builder, outputFormat, "NEXTVAL FOR", "", " ");

      int j = 0;
      for (String name : nextValExpression.getNameList()) {
        if (j > 0) {
          builder.append(".");
        }
        appendObjectName(builder, outputFormat, name, "", "");
        j++;
      }

    } else if (expression instanceof ExistsExpression) {
      ExistsExpression existsExpression = (ExistsExpression) expression;
      if (existsExpression.isNot()) appendOperator(builder, outputFormat, "NOT EXISTS", "", "");
      else appendOperator(builder, outputFormat, "EXISTS", "", " ");

      appendExpression(
          existsExpression.getRightExpression(),
          null,
          builder,
          indent + 1,
          i,
          n,
          false,
          BreakLine.AFTER_FIRST);

    } else if (expression instanceof ExtractExpression) {
      ExtractExpression extractExpression = (ExtractExpression) expression;

      // "EXTRACT(" + name + " FROM " + expression + ')';

      appendKeyWord(builder, outputFormat, "EXTRACT", "", "( ");
      appendValue(builder, outputFormat, extractExpression.getName(), "", "");
      appendKeyWord(builder, outputFormat, "FROM", " ", " ");

      appendExpression(
          extractExpression.getExpression(),
          null,
          builder,
          indent + 1,
          i,
          n,
          false,
          BreakLine.AFTER_FIRST);
      appendNormalizingTrailingWhiteSpace(builder, " )");

    } else if (expression instanceof StringValue) {
      StringValue stringValue = (StringValue) expression;
      builder.append(stringValue.toString());

    } else if (expression instanceof JdbcNamedParameter) {
      JdbcNamedParameter jdbcNamedParameter = (JdbcNamedParameter) expression;
      appendValue(builder, outputFormat, jdbcNamedParameter.toString(), "", "");

    } else if (expression instanceof JdbcParameter) {
      JdbcParameter jdbcParameter = (JdbcParameter) expression;
      appendValue(builder, outputFormat, jdbcParameter.toString(), "", "");

    } else if (expression instanceof IsNullExpression) {
      IsNullExpression isNullExpression = (IsNullExpression) expression;
      appendExpression(
          isNullExpression.getLeftExpression(),
          null,
          builder,
          indent + 1,
          i,
          n,
          false,
          BreakLine.AFTER_FIRST);

      if (isNullExpression.isUseIsNull()) {
        if (isNullExpression.isNot()) appendOperator(builder, outputFormat, "NOT ISNULL", " ", "");
        else appendOperator(builder, outputFormat, "ISNULL", " ", "");
      } else {
        if (isNullExpression.isNot()) appendOperator(builder, outputFormat, "IS NOT NULL", " ", "");
        else appendOperator(builder, outputFormat, "IS NULL", " ", "");
      }

    } else if (expression instanceof NullValue) {
      NullValue nullValue = (NullValue) expression;
      appendValue(builder, outputFormat, nullValue.toString(), "", "");

    } else if (expression instanceof TimeKeyExpression) {
      TimeKeyExpression timeKeyExpression = (TimeKeyExpression) expression;
      appendValue(builder, outputFormat, timeKeyExpression.toString(), "", "");

    } else if (expression instanceof InExpression) {
      InExpression inExpression = (InExpression) expression;
      Expression leftExpression = inExpression.getLeftExpression();
      ItemsList leftItemsList = inExpression.getLeftItemsList();
      boolean useNot = inExpression.isNot();

      MultiExpressionList multiExpressionList = inExpression.getMultiExpressionList();
      ItemsList rightItemsList = inExpression.getRightItemsList();
      Expression rightExpression = inExpression.getRightExpression();

      if (leftExpression == null) {
        builder.append("( ");
        appendItemsList(leftItemsList, builder, null, indent, BreakLine.AS_NEEDED);
        builder.append(" )");
      } else {
        appendExpression(leftExpression, null, builder, indent, i, n, false, BreakLine.AS_NEEDED);
      }

      if (useNot) appendOperator(builder, outputFormat, "NOT IN", " ", " ");
      else appendOperator(builder, outputFormat, "IN", " ", " ");

      if (multiExpressionList != null) {
        builder.append("( ");
        appendItemsList(multiExpressionList, builder, null, indent, BreakLine.AS_NEEDED);
        builder.append(" )");
      } else {
        if (rightExpression == null) {
          builder.append("( ");
          appendItemsList(rightItemsList, builder, null, indent, BreakLine.AS_NEEDED);
          builder.append(" )");
        } else {
          appendExpression(
              rightExpression, null, builder, indent, i, n, false, BreakLine.AS_NEEDED);
        }
      }
    } else if (expression instanceof Function) {
      Function function = (Function) expression;

      String name = function.getName();
      ExpressionList parameters = function.getParameters();
      NamedExpressionList namedParameters = function.getNamedParameters();
      boolean distinct = function.isDistinct();
      boolean allColumns = function.isAllColumns();
      boolean escaped = function.isEscaped();
      KeepExpression keep = function.getKeep();
      Expression attribute = function.getAttribute();
      String attributeName = function.getAttributeName();

      if (escaped) appendFunction(builder, outputFormat, "fn", " {", " ");

      appendFunction(builder, outputFormat, name, "", "( ");

      if (parameters != null || namedParameters != null) {
        if (parameters != null) {
          if (distinct) {
            builder.append("(DISTINCT ");
          } else if (allColumns) {
            builder.append("(ALL ");
          }

          if (name.equalsIgnoreCase("Decode")) {
            appendDecodeExpressionsList(parameters, BreakLine.AS_NEEDED, builder, indent);
          } else {
            appendExpressionsList(parameters, BreakLine.AS_NEEDED, builder, indent);
          }

          appendNormalizingTrailingWhiteSpace(builder, " )");
        } else {
          // @todo: implement this properly and add a test case
          builder.append(namedParameters.toString());
        }
      } else if (allColumns) {
        builder.append(" ( * )");
      } else {
        builder.append(" ()");
      }

      if (attribute != null) {
        builder.append(".").append(attribute.toString());
      } else if (attributeName != null) {
        builder.append(".").append(attributeName);
      }

      if (keep != null) {
        builder.append(" ").append(keep.toString());
      }

      if (escaped) builder.append("} ");

    } else if (expression instanceof SignedExpression) {
      SignedExpression signedExpression = (SignedExpression) expression;
      appendOperator(builder, outputFormat, String.valueOf(signedExpression.getSign()), "", " ");

      appendExpression(
          signedExpression.getExpression(), null, builder, indent, i, n, false, BreakLine.NEVER);

    } else if (expression instanceof SubSelect) {
      SubSelect subSelect = (SubSelect) expression;
      builder.append("( ");

      int subIndent = getSubIndent(builder, indentWidth, true);

      List<WithItem> withItems = subSelect.getWithItemsList();
      if (withItems != null && withItems.size() > 0) {
        int j = 0;
        appendKeyWord(builder, outputFormat, "WITH", "", " ");

        for (WithItem withItem : withItems) {
          appendWithItem(withItem, null, builder, subIndent, j, withItems.size());
          j++;
        }
      }

      SelectBody selectBody = subSelect.getSelectBody();
      Alias alias1 = subSelect.getAlias();

      appendSelectBody(selectBody, alias, builder, subIndent, withItems != null);
      appendNormalizingTrailingWhiteSpace(builder, " )");

    } else if (expression instanceof ValueListExpression) {
      ValueListExpression valueListExpression = (ValueListExpression) expression;
      ExpressionList expressionList = valueListExpression.getExpressionList();

      builder.append("( ");
      appendExpressionsList(expressionList, BreakLine.AS_NEEDED, builder, indent);
      builder.append(" )");

    } else {
      LOGGER.warning(
          "Unhandled expression: "
              + expression.getClass().getName()
              + " = "
              + expression.toString());
      builder.append(expression);
    }

    if (alias != null) {
      appendNormalizingTrailingWhiteSpace(builder, " ");
      if (alias.isUseAs()) appendKeyWord(builder, outputFormat, "AS", "", " ");
      appendAlias(builder, outputFormat, alias.getName(), "", " ");
    }

    switch (separation) {
      case AFTER:
        appendNormalizingTrailingWhiteSpace(builder, commaSeparated && i < n - 1 ? ", " : "");
        break;
    }
  }

  private static void appendItemsList(
      ItemsList itemsList, StringBuilder builder, Alias alias, int indent, BreakLine breakLine) {
    // All Known Implementing Classes:
    // ExpressionList, MultiExpressionList, NamedExpressionList, SubSelect
    if (itemsList instanceof ExpressionList) {
      ExpressionList expressionList = (ExpressionList) itemsList;
      appendExpressionsList(expressionList, breakLine, builder, indent);
    } else if (itemsList instanceof MultiExpressionList) {
      MultiExpressionList multiExpressionList = (MultiExpressionList) itemsList;
      builder.append(multiExpressionList.toString());
    } else if (itemsList instanceof NamedExpressionList) {
      NamedExpressionList namedExpressionList = (NamedExpressionList) itemsList;
      builder.append(namedExpressionList.toString());
    } else if (itemsList instanceof SubSelect) {

      SubSelect subSelect = (SubSelect) itemsList;
      appendSubSelect(subSelect, builder, false);
    }
  }

  private static void appendExpressionsList(
      ExpressionList expressionList, BreakLine breakLine, StringBuilder builder, int indent) {
    int size = expressionList.getExpressions().size();
    int subIndent =
        breakLine.equals(BreakLine.NEVER) || size <= 3
            ? indent
            : getSubIndent(builder, indent, size > 3);

    int i = 0;
    List<Expression> expressions = expressionList.getExpressions();
    for (Expression expression : expressions) {
      switch (breakLine) {
        case AS_NEEDED:
          BreakLine bl =
              (size > 3 && size < 5) || (size >= 5 && i % 3 == 0)
                  ? BreakLine.AFTER_FIRST
                  : BreakLine.NEVER;
          appendExpression(expression, null, builder, subIndent, i, expressions.size(), true, bl);
          break;

        default:
          appendExpression(
              expression, null, builder, subIndent, i, expressions.size(), true, breakLine);
      }
      i++;
    }
  }

  private static void appendFromItem(
      FromItem fromItem, StringBuilder builder, int indent, int i, int n) {

    if (i > 0) {
      appendNormalizedLineBreak(builder);
      for (int j = 0; j <= indent; j++) builder.append(indentString);
    }

    switch (separation) {
      case BEFORE:
        builder.append(i > 0 ? ", " : "");
    }

    Alias alias = fromItem.getAlias();

    // All Known Implementing Classes: LateralSubSelect, ParenthesisFromItem,
    // SpecialSubSelect, SubJoin, SubSelect, Table, TableFunction, ValuesList
    if (fromItem instanceof Table) {
      Table table = (Table) fromItem;
      appendFromItem(table, alias, builder, indent, i, n);
    } else if (fromItem instanceof SubSelect) {
      SubSelect subSelect = (SubSelect) fromItem;
      appendSubSelect(subSelect, builder, true);

    } else {
      System.out.println(fromItem.getClass().getName());
    }

    switch (separation) {
      case AFTER:
        appendNormalizingTrailingWhiteSpace(builder, i < n - 1 ? ", " : "");
        break;
    }
  }

  private static void appendSubSelect(
      SubSelect subSelect, StringBuilder builder, boolean useBrackets) {
    if (subSelect.isUseBrackets() && useBrackets) {
      builder.append("( ");
    }

    int subIndent = getSubIndent(builder, indentWidth, true);

    List<WithItem> withItems = subSelect.getWithItemsList();
    if (withItems != null && withItems.size() > 0) {
      int j = 0;
      appendNormalizedLineBreak(builder);
      builder.append("WITH ");

      for (WithItem withItem : withItems) {
        appendWithItem(withItem, null, builder, subIndent, j, withItems.size());
        j++;
      }
    }

    SelectBody selectBody = subSelect.getSelectBody();
    Alias alias1 = subSelect.getAlias();

    appendSelectBody(selectBody, null, builder, subIndent, false);

    if (subSelect.isUseBrackets() && useBrackets) {
      builder.append(" )");
    }

    if (alias1 != null) {
      appendNormalizingTrailingWhiteSpace(builder, " ");
      appendAlias(builder, outputFormat, alias1.toString(), "", "");
    }
  }

  private static void appendFromItem(
      Table table, Alias alias, StringBuilder builder, int indent, int i, int n) {

    appendObjectName(builder, outputFormat, table.getFullyQualifiedName(), "", "");
    if (alias != null) {
      appendNormalizingTrailingWhiteSpace(builder, " ");
      if (alias.isUseAs()) appendKeyWord(builder, outputFormat, "AS", "", " ");

      appendAlias(builder, outputFormat, alias.getName(), "", " ");
    }
  }

  private static void appendSetOperation(
      SetOperation setOperation, StringBuilder builder, int indent) {

    // Direct Known Subclasses:
    // ExceptOp, IntersectOp, MinusOp, UnionOp
    if (setOperation instanceof UnionOp) {
      UnionOp unionOp = (UnionOp) setOperation;

      appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) builder.append(indentString);
      appendOperator(builder, outputFormat, "UNION", "", " ");

      if (unionOp.isAll()) appendOperator(builder, outputFormat, "ALL", "", " ");
    } else if (setOperation instanceof MinusOp) {
      MinusOp minusOp = (MinusOp) setOperation;

      appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) builder.append(indentString);
      appendOperator(builder, outputFormat, "MINUS", "", " ");
    } else if (setOperation instanceof IntersectOp) {
      IntersectOp intersectOp = (IntersectOp) setOperation;

      appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) builder.append(indentString);
      appendOperator(builder, outputFormat, "INTERSECT", "", " ");

    } else if (setOperation instanceof ExceptOp) {
      ExceptOp exceptOp = (ExceptOp) setOperation;

      appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) builder.append(indentString);
      appendOperator(builder, outputFormat, "EXCEPT", "", " ");
    } else if (setOperation != null)
      throw new UnsupportedOperationException(
          setOperation.getClass().getName() + " is not supported yet.");
  }

  private static void appendTruncate(StringBuilder builder, Truncate truncate, int indent) {
    Table table = truncate.getTable();
    boolean cascade = truncate.getCascade();

    appendKeyWord(builder, outputFormat, "TRUNCATE TABLE", "", " ")
        .append(table.getFullyQualifiedName());
    if (cascade) {
      appendOperator(builder, outputFormat, "CASCADE", " ", "");
    }
  }

  private static void appendCreateTable(
      StringBuilder builder, CreateTable createTable, int indent) {

    int i = 0;
    BreakLine breakLine = BreakLine.ALWAYS;
    boolean commaSeparated = true;

    List<String> createOptionsString = createTable.getCreateOptionsStrings();
    String createOps =
        createOptionsString != null && createOptionsString.size() > 0
            ? PlainSelect.getStringList(createOptionsString, false, false)
            : null;

    boolean unlogged = createTable.isUnlogged();
    boolean ifNotExists = createTable.isIfNotExists();

    Table table = createTable.getTable();
    appendKeyWord(builder, outputFormat, "CREATE", "", " ");

    if (unlogged) appendHint(builder, outputFormat, "UNLOGGED", "", " ");

    if (createOps != null) appendHint(builder, outputFormat, createOps, "", " ");

    appendKeyWord(builder, outputFormat, "TABLE", "", " ");

    if (ifNotExists) appendHint(builder, outputFormat, "IF NOT EXISTS", "", " ");

    appendAlias(builder, outputFormat, table.getFullyQualifiedName(), "", "");

    List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
    if (columnDefinitions != null && !columnDefinitions.isEmpty()) {
      builder.append(" (");

      int colWidth = 0;
      int typeWidth = 0;

      for (ColumnDefinition columnDefinition : columnDefinitions) {
        String columnName = columnDefinition.getColumnName();
        // @todo: please get rid of that Replace workaround
        String colDataType = columnDefinition.getColDataType().toString().replace(", ", ",");

        if (colWidth < columnName.length()) colWidth = columnName.length();

        if (typeWidth < colDataType.length()) typeWidth = colDataType.length();
      }

      // int typeIndex = (((indent +1)* indentString.length() + colWidth + 1) /
      // indentString.length()) * ("
      // ".length() + 1);

      int typeIndex = indent + (colWidth / indentString.length()) + 3;

      int specIndex = indent + typeIndex + (typeWidth / indentString.length()) + 1;

      for (ColumnDefinition columnDefinition : columnDefinitions) {
        if (i > 0 || breakLine.equals(BreakLine.ALWAYS)) {
          if (!breakLine.equals(BreakLine.NEVER)) {
            appendNormalizedLineBreak(builder);
            for (int j = 0; j <= indent; j++) builder.append(indentString);
          }
        }

        String columnName = columnDefinition.getColumnName();
        ColDataType colDataType = columnDefinition.getColDataType();
        List<String> columnSpecs = columnDefinition.getColumnSpecs();

        switch (separation) {
          case BEFORE:
            builder.append(i > 0 ? ", " : "");
        }

        appendObjectName(builder, outputFormat, columnName, "", "");

        int lastLineLength = getLastLineLength(builder);

        for (int j = lastLineLength; j <= typeIndex * indentWidth; j++) builder.append(" ");
        // @todo: please get rid of that Replace workaround
        appendType(builder, outputFormat, colDataType.toString().replace(", ", ","), "", "");

        lastLineLength = getLastLineLength(builder);

        if (columnSpecs != null && !columnSpecs.isEmpty()) {
          for (int j = lastLineLength; j <= specIndex * indentWidth; j++) builder.append(" ");
          appendType(
              builder, outputFormat, PlainSelect.getStringList(columnSpecs, false, false), "", "");
        }

        switch (separation) {
          case AFTER:
            appendNormalizingTrailingWhiteSpace(
                builder, i > columnDefinitions.size() - 1 ? ", " : "");
            break;
        }

        i++;
      }

      // Direct Known Subclasses:
      // ExcludeConstraint, NamedConstraint

      // Direct Known Subclasses:
      // CheckConstraint, ForeignKeyIndex

      List<Index> indexes = createTable.getIndexes();
      if (indexes != null && !indexes.isEmpty()) {
        for (Index index : indexes) {
          if (i > 0 || breakLine.equals(BreakLine.ALWAYS)) {
            if (!breakLine.equals(BreakLine.NEVER)) {
              appendNormalizedLineBreak(builder);
              for (int j = 0; j <= indent; j++) builder.append(indentString);
            }
          }

          switch (separation) {
            case BEFORE:
              builder.append(i > 0 ? ", " : "");
          }

          if (index instanceof ForeignKeyIndex) {
            ForeignKeyIndex foreignKeyIndex = (ForeignKeyIndex) index;

            String type = foreignKeyIndex.getType();
            String name = foreignKeyIndex.getName();
            List<String> columnsNames = foreignKeyIndex.getColumnsNames();
            List<Index.ColumnParams> columnParams = foreignKeyIndex.getColumns();
            List<String> idxSpec = foreignKeyIndex.getIndexSpec();
            String idxSpecText = PlainSelect.getStringList(idxSpec, false, false);

            // @todo: beautify the expression
            // @todo: add a test case
            if (name != null) {
              appendKeyWord(builder, outputFormat, "CONSTRAINT", "", " ");
              appendAlias(builder, outputFormat, name, "", "");
              appendNormalizedLineBreak(builder);
            }

            for (int j = 0; name != null && j <= indent + 1; j++) builder.append(indentString);

            appendKeyWord(builder, outputFormat, type, "", " ");

            builder.append("( ");
            int subIndent = getSubIndent(builder, indent, columnsNames.size() > 2);
            BreakLine bl = columnsNames.size() > 2 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
            appendStringList(columnsNames, null, builder, subIndent, true, bl);
            builder.append(" )");

            // @todo: add a test case for this
            if (idxSpec != null && idxSpecText.length() > 0)
              appendHint(builder, outputFormat, idxSpecText, " ", "");

            Table foreignTable = foreignKeyIndex.getTable();
            List<String> referencedColumnNames = foreignKeyIndex.getReferencedColumnNames();

            appendNormalizedLineBreak(builder);
            for (int j = 0; j <= indent + 1; j++) builder.append(indentString);
            appendKeyWord(builder, outputFormat, "REFERENCES", "", " ");
            appendObjectName(builder, outputFormat, foreignTable.getFullyQualifiedName(), "", " ");

            builder.append("( ");
            subIndent = getSubIndent(builder, indent, referencedColumnNames.size() > 2);
            bl = referencedColumnNames.size() > 2 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
            appendStringList(referencedColumnNames, null, builder, subIndent, true, bl);
            builder.append(" )");

            ReferentialAction updateAction =
                foreignKeyIndex.getReferentialAction(ReferentialAction.Type.UPDATE);
            if (updateAction != null) {
              appendNormalizedLineBreak(builder);
              for (int j = 0; j <= indent + 2; j++) builder.append(indentString);
              builder.append(updateAction);
            }

            ReferentialAction deleteAction =
                foreignKeyIndex.getReferentialAction(ReferentialAction.Type.DELETE);
            if (deleteAction != null) {
              appendNormalizedLineBreak(builder);
              for (int j = 0; j <= indent + 2; j++) builder.append(indentString);
              builder.append(deleteAction);
            }

          } else if (index instanceof CheckConstraint) {
            CheckConstraint checkConstraint = (CheckConstraint) index;

            String contraintName = checkConstraint.getName();
            Expression expression = checkConstraint.getExpression();

            appendKeyWord(builder, outputFormat, "CONSTRAINT", "", " ");
            appendAlias(builder, outputFormat, contraintName, "", "");

            appendNormalizedLineBreak(builder);
            for (int j = 0; j <= indent + 1; j++) builder.append(indentString);

            builder.append(" CHECK (").append(expression).append(")");

          } else if (index instanceof NamedConstraint) {
            NamedConstraint namedConstraint = (NamedConstraint) index;

            String type = namedConstraint.getType();
            String name = namedConstraint.getName();
            List<String> columnsNames = namedConstraint.getColumnsNames();
            List<Index.ColumnParams> columnParams = namedConstraint.getColumns();
            List<String> idxSpec = namedConstraint.getIndexSpec();
            String idxSpecText = PlainSelect.getStringList(idxSpec, false, false);

            // @todo: beautify the expression
            // @todo: add a test case
            if (name != null) {
              appendKeyWord(builder, outputFormat, "CONSTRAINT", "", " ");
              appendAlias(builder, outputFormat, name, "", "");
              appendNormalizedLineBreak(builder);
            }

            for (int j = 0; name != null && j <= indent + 1; j++) builder.append(indentString);
            appendKeyWord(builder, outputFormat, type, "", " ");

            builder.append("( ");
            int subIndent = getSubIndent(builder, indent, columnsNames.size() > 2);
            BreakLine bl = columnsNames.size() > 2 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
            appendStringList(columnsNames, null, builder, subIndent, true, bl);
            builder.append(" )");

            // @todo: add a test case for this
            if (idxSpec != null && idxSpecText.length() > 0)
              appendHint(builder, outputFormat, idxSpecText, " ", "");

          } else if (index instanceof ExcludeConstraint) {
            ExcludeConstraint excludeConstraint = (ExcludeConstraint) index;
            Expression expression = excludeConstraint.getExpression();

            // @todo: beautify the expression
            // @todo: add a test case

            builder.append("EXCLUDE WHERE ");
            builder.append("(");
            builder.append(expression);
            builder.append(")");

          } else {
            String type = index.getType();
            String name = index.getName();
            List<Index.ColumnParams> columnParams = index.getColumns();
            List<String> idxSpec = index.getIndexSpec();
            String idxSpecText = PlainSelect.getStringList(idxSpec, false, false);

            builder.append(type);

            appendKeyWord(builder, outputFormat, type, "", " ");
            if (name != null) {
              appendAlias(builder, outputFormat, name, "", "");
            }

            builder
                .append(" ")
                .append(PlainSelect.getStringList(columnParams, true, true))
                .append(!"".equals(idxSpecText) ? " " + idxSpecText : "");
          }

          switch (separation) {
            case AFTER:
              appendNormalizingTrailingWhiteSpace(builder, i > indexes.size() - 1 ? ", " : "");
              break;
          }

          i++;
        }
      }
      appendNormalizedLineBreak(builder).append(")");
    }
    List<String> tableOptionsStrings = createTable.getTableOptionsStrings();
    String options = PlainSelect.getStringList(tableOptionsStrings, false, false);
    if (options != null && options.length() > 0) {
      appendHint(builder, outputFormat, options, " ", "");
    }

    RowMovement rowMovement = createTable.getRowMovement();
    if (rowMovement != null) {
      // @todo: beautify this part
      // @todo: provide test cases
      builder.append(" ").append(rowMovement.getMode()).append(" ROW MOVEMENT");
    }

    Select select = createTable.getSelect();
    boolean selectParenthesis = createTable.isSelectParenthesis();
    if (select != null) {
      appendNormalizedLineBreak(builder);
      for (int j = 0; j <= indent; j++) builder.append(indentString);
      builder.append("AS ");

      if (selectParenthesis) builder.append("( ");

      appendSelect(select, builder, indent + 2, false);

      if (selectParenthesis) builder.append(" )");
    }

    Table likeTable = createTable.getLikeTable();
    if (likeTable != null) {

      builder.append(" AS ");
      if (selectParenthesis) builder.append("( ");

      Alias alias = likeTable.getAlias();

      appendFromItem(likeTable, alias, builder, indent + 1, 0, 1);

      if (selectParenthesis) builder.append(" )");
    }
  }

  private static void appendCreateIndex(
      StringBuilder builder, CreateIndex createIndex, int indent) {

    Index index = createIndex.getIndex();
    Table table = createIndex.getTable();

    List<String> tailParameters = createIndex.getTailParameters();
    List<Index.ColumnParams> columnsParameters = index.getColumns();

    appendKeyWord(builder, outputFormat, "CREATE", "", " ");

    if (index.getType() != null) {
      appendKeyWord(builder, outputFormat, index.getType(), "", " ");
    }

    appendKeyWord(builder, outputFormat, "INDEX", "", " ");
    appendAlias(builder, outputFormat, index.getName(), "", "");

    appendNormalizedLineBreak(builder);
    for (int j = 0; j <= indent; j++) builder.append(indentString);
    appendKeyWord(builder, outputFormat, "ON", "", " ");
    appendObjectName(builder, outputFormat, table.getFullyQualifiedName(), "", "");

    if (index.getUsing() != null) {
      appendKeyWord(builder, outputFormat, "USING", "  ", " ");
      builder.append(index.getUsing());
    }

    if (index.getColumnsNames() != null) {
      builder.append("( ");

      int subIndent = getSubIndent(builder, indentWidth, columnsParameters.size() > 2);
      BreakLine bl = columnsParameters.size() > 2 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;

      int i = 0;
      for (Index.ColumnParams param : columnsParameters) {
        appendString(
            param.getColumnName(), null, builder, subIndent, i, columnsParameters.size(), true, bl);
        i++;
      }

      builder.append(" )");

      if (tailParameters != null) {
        builder.append(" ");
        for (String param : tailParameters) {
          appendHint(builder, outputFormat, param, "", " ");
        }
      }
    }
  }

  private static void appendCreateView(StringBuilder builder, CreateView createView, int indent) {
    boolean isOrReplace = createView.isOrReplace();
    ForceOption force = createView.getForce();
    TemporaryOption temp = createView.getTemporary();
    boolean isMaterialized = createView.isMaterialized();

    Table view = createView.getView();

    List<String> columnNames = createView.getColumnNames();
    Select select = createView.getSelect();
    boolean isWithReadOnly = createView.isWithReadOnly();

    appendKeyWord(builder, outputFormat, "CREATE", "", " ");
    if (isOrReplace) {
      appendKeyWord(builder, outputFormat, "OR REPLACE", "", " ");
    }
    switch (force) {
      case FORCE:
        appendKeyWord(builder, outputFormat, "FORCE", "", " ");
        break;
      case NO_FORCE:
        appendKeyWord(builder, outputFormat, "NO FORCE", "", " ");
        break;
    }

    if (temp != TemporaryOption.NONE) {
      builder.append(temp.name()).append(" ");
    }

    if (isMaterialized) {
      appendKeyWord(builder, outputFormat, "MATERIALIZED", "", " ");
    }
    appendKeyWord(builder, outputFormat, "VIEW", "", " ");
    appendAlias(builder, outputFormat, view.getFullyQualifiedName(), "", "");
    if (columnNames != null) {
      builder.append(PlainSelect.getStringList(columnNames, true, true));
    }

    appendNormalizedLineBreak(builder);
    for (int j = 0; j <= indent; j++) builder.append(indentString);
    appendKeyWord(builder, outputFormat, "AS", "", " ");
    appendSelect(select, builder, indent + 2, false);

    if (isWithReadOnly) {
      builder.append(" WITH READ ONLY");
      appendHint(builder, outputFormat, "WITH READ ONLY", " ", "");
    }
  }

  private static void appendAllTableColumns(
      AllTableColumns allColumns, StringBuilder builder, int indent, int i, int n) {
    if (i > 0) {
      appendNormalizedLineBreak(builder);
      for (int j = 0; j <= indent; j++) builder.append(indentString);
    }

    switch (separation) {
      case AFTER:
        builder.append(allColumns.toString());
        appendNormalizingTrailingWhiteSpace(builder, i > n - 1 ? ", " : "");
        break;
      case BEFORE:
        builder.append(i > 0 ? ", " : "");
        builder.append(allColumns.toString());
    }
  }

  private static void appendAlter(StringBuilder builder, Alter alter, int indent) {
    boolean useOnly = alter.isUseOnly();
    Table table = alter.getTable();
    List<AlterExpression> alterExpressions = alter.getAlterExpressions();

    appendKeyWord(builder, outputFormat, "ALTER TABLE", "", " ");
    if (useOnly) {
      appendKeyWord(builder, outputFormat, "ONLY", "", " ");
    }
    appendObjectName(builder, outputFormat, table.getFullyQualifiedName(), "", "");
    int i = 0;

    if (alterExpressions != null) {
      for (AlterExpression alterExpression : alterExpressions) {
        if (i > 0) {
          appendNormalizedLineBreak(builder);
          for (int j = 0; j <= indent; j++) builder.append(indentString);
        }

        switch (separation) {
          case BEFORE:
            builder.append(i > 0 ? ", " : "");
        }

        AlterOperation operation = alterExpression.getOperation();
        String commentText = alterExpression.getCommentText();
        String columnName = alterExpression.getColumnName();
        String columnOldName = alterExpression.getColumnOldName();

        List<AlterExpression.ColumnDataType> colDataTypeList = alterExpression.getColDataTypeList();
        String optionalSpecifier = alterExpression.getOptionalSpecifier();

        List<AlterExpression.ColumnDropNotNull> columnDropNotNullList =
            alterExpression.getColumnDropNotNullList();

        String constraintName = alterExpression.getConstraintName();
        boolean constraintIfExists = alterExpression.isConstraintIfExists();

        List<String> pkColumns = alterExpression.getPkColumns();
        List<String> ukColumns = alterExpression.getUkColumns();
        String ukName = alterExpression.getUkName();
        boolean uk = alterExpression.getUk();

        List<String> fkColumns = alterExpression.getFkColumns();
        String fkSourceTable = alterExpression.getFkSourceTable();
        List<String> fkSourceColumns = alterExpression.getFkSourceColumns();

        ReferentialAction deleteAction =
            alterExpression.getReferentialAction(ReferentialAction.Type.DELETE);
        ReferentialAction updateAction =
            alterExpression.getReferentialAction(ReferentialAction.Type.UPDATE);

        Index index = alterExpression.getIndex();

        List<ConstraintState> constraints = alterExpression.getConstraints();
        boolean useEqual = alterExpression.getUseEqual();

        List<String> parameters = alterExpression.getParameters();

        appendNormalizedLineBreak(builder);

        for (int j = 0; j <= indent; j++) builder.append(indentString);
        appendKeyWord(builder, outputFormat, operation.name(), "", " ");

        if (commentText != null) {
          if (columnName != null) {
            appendKeyWord(builder, outputFormat, "COMMENT", " ", " ");
          }
          builder.append(commentText);
        } else if (columnName != null) {
          if (alterExpression.hasColumn()) appendKeyWord(builder, outputFormat, "COLUMN", "", " ");
          if (operation == AlterOperation.RENAME) {
            appendObjectName(builder, outputFormat, columnOldName, "", "");
            appendKeyWord(builder, outputFormat, "TO", " ", " ");
          }
          appendObjectName(builder, outputFormat, columnName, "", "");
        } else if (colDataTypeList != null) {

          int colWidth = 0;
          int typeWidth = 0;

          BreakLine breakLine =
              colDataTypeList.size() > 1 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
          boolean commaSeparated = true;

          if (operation == AlterOperation.CHANGE) {
            if (optionalSpecifier != null) {
              builder.append(optionalSpecifier).append(" ");
            }
            appendObjectName(builder, outputFormat, columnOldName, "", " ");
          } else if (colDataTypeList.size() > 1) {

            for (ColumnDefinition columnDefinition : colDataTypeList) {
              String columnName1 = columnDefinition.getColumnName();
              // @todo: please get rid of that Replace workaround
              String colDataType = columnDefinition.getColDataType().toString().replace(", ", ",");

              if (colWidth < columnName1.length()) colWidth = columnName1.length();

              if (typeWidth < colDataType.length()) typeWidth = colDataType.length();
            }

            builder.append("( ");

          } else {
            if (alterExpression.hasColumn())
              appendKeyWord(builder, outputFormat, "COLUMN", "", " ");
          }

          int subIndent = getSubIndent(builder, indent, colDataTypeList.size() > 1);
          int typeIndex = subIndent + (colWidth / indentString.length()) + 1;
          int specIndex = indent + typeIndex + (typeWidth / indentString.length()) + 1;

          for (ColumnDefinition columnDefinition : colDataTypeList) {
            if (i > 0 || breakLine.equals(BreakLine.ALWAYS)) {
              if (!breakLine.equals(BreakLine.NEVER)) {
                appendNormalizedLineBreak(builder);
                for (int j = 0; j < subIndent; j++) builder.append(indentString);
              }
              if (commaSeparated && i > 0) builder.append(", ");
            }

            String columnName1 = columnDefinition.getColumnName();
            ColDataType colDataType = columnDefinition.getColDataType();
            List<String> columnSpecs = columnDefinition.getColumnSpecs();

            appendObjectName(builder, outputFormat, columnName1, "", " ");

            int lastLineLength = getLastLineLength(builder);

            for (int j = lastLineLength; j <= typeIndex * indentWidth; j++) builder.append(" ");
            // @todo: please get rid of that Replace workaround
            appendType(builder, outputFormat, colDataType.toString().replace(", ", ","), "", "");

            lastLineLength = getLastLineLength(builder);

            if (columnSpecs != null && !columnSpecs.isEmpty()) {
              if (colDataTypeList.size() > 1)
                for (int j = lastLineLength; j <= specIndex * indentWidth; j++) builder.append(" ");
              else builder.append(" ");

              appendType(
                  builder,
                  outputFormat,
                  PlainSelect.getStringList(columnSpecs, false, false),
                  "",
                  "");
            }
            i++;
          }

          if (colDataTypeList.size() > 1) {
            builder.append(")");
          }
        } else if (columnDropNotNullList != null) {
          if (operation == AlterOperation.CHANGE) {
            if (optionalSpecifier != null) {
              builder.append(optionalSpecifier).append(" ");
            }
            appendObjectName(builder, outputFormat, columnOldName, "", " ");
          } else if (columnDropNotNullList.size() > 1) {
            builder.append("(");
          } else {
            if (alterExpression.hasColumn())
              appendKeyWord(builder, outputFormat, "COLUMN", "", " ");
          }
          builder.append(PlainSelect.getStringList(columnDropNotNullList));
          if (columnDropNotNullList.size() > 1) {
            builder.append(")");
          }
        } else if (constraintName != null) {
          appendKeyWord(builder, outputFormat, "CONSTRAINT", "", " ");

          if (constraintIfExists) {
            appendKeyWord(builder, outputFormat, "IF EXISTS", "", " ");
          }
          appendObjectName(builder, outputFormat, constraintName, "", "");
        } else if (pkColumns != null) {
          appendKeyWord(builder, outputFormat, "PRIMARY KEY", "", " (");

          builder.append(PlainSelect.getStringList(pkColumns)).append(")");
        } else if (ukColumns != null) {
          appendKeyWord(builder, outputFormat, "UNIQUE", "", "");
          if (ukName != null) {
            if (uk) {
              appendKeyWord(builder, outputFormat, "KEY", " ", " ");
            } else {
              appendKeyWord(builder, outputFormat, "INDEX", " ", " ");
            }
            appendObjectName(builder, outputFormat, ukName, "", "");
          }
          builder.append(" (").append(PlainSelect.getStringList(ukColumns)).append(")");
        } else if (fkColumns != null) {
          appendKeyWord(builder, outputFormat, "FOREIGN KEY", "", " (");
          builder.append(PlainSelect.getStringList(fkColumns)).append(")");

          appendNormalizedLineBreak(builder);
          for (int j = 0; j <= indent + 1; j++) builder.append(indentString);

          appendKeyWord(builder, outputFormat, "REFERENCES", "", " ");

          builder
              .append(fkSourceTable)
              .append(" (")
              .append(PlainSelect.getStringList(fkSourceColumns))
              .append(")");
          // referentialActions.forEach(b::append);
          if (updateAction != null) builder.append(updateAction);

          if (deleteAction != null) builder.append(deleteAction);
        } else if (index != null) {
          builder.append(index);
        }
        if (constraints != null && !constraints.isEmpty()) {
          builder.append(" ").append(PlainSelect.getStringList(constraints, false, false));
        }
        if (useEqual) {
          builder.append("=");
        }
        if (parameters != null && !parameters.isEmpty()) {
          builder.append(" ").append(PlainSelect.getStringList(parameters, false, false));
        }

        switch (separation) {
          case AFTER:
            appendNormalizingTrailingWhiteSpace(
                builder, i < alterExpressions.size() - 1 ? ", " : "");
            break;
        }

        i++;
      }
    }
  }

  public enum OutputFormat {
    PLAIN,
    ANSI,
    HTML,
    RTF
  }

  public enum Spelling {
    UPPER,
    LOWER,
    CAMEL,
    KEEP
  }

  public enum Separation {
    BEFORE,
    AFTER
  }

  private enum BreakLine {
    NEVER // keep all arguments on one line
    ,
    AS_NEEDED // only when more than 3 arguments
    ,
    AFTER_FIRST // break all after the first argument
    ,
    ALWAYS // break all arguments to a new line
  }
}
