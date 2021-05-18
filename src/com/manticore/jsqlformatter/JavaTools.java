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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snt.inmemantlr.GenericParser;
import org.snt.inmemantlr.listener.DefaultTreeListener;
import org.snt.inmemantlr.tree.ParseTree;
import org.snt.inmemantlr.tree.ParseTreeNode;

/**
 * A powerful Java SQL Formatter based on the JSQLParser.
 *
 * @author <a href="mailto:andreas@manticore-projects.com">Andreas Reichel</a>
 * @version 0.1
 */
public class JavaTools {
  private static final Logger LOGGER = Logger.getLogger(JavaTools.class.getName());

  private static class LocalVariableDeclaration {
    public String label = null;
    public String typeString = null;
    public StringBuilder sqlBuilder = new StringBuilder();
    public ArrayList<String> parameters = new ArrayList<>();
  }

  public static void main(String[] args) {

    /*
      String columnName = "";
      String tableName = "";
      String test2 =
          new StringBuilder("SELECT ")
              .append(columnName)
              .append(" from ")
              .append(tableName)
              .append(";")
              .toString();
      String test = "SELECT " + columnName + " from " + tableName + ";";
    */

    final String[] escaped = {
      "\"SELECT \" + columnName + \" from \" + noVariableAssigned + \";\";",
      "String test = \"SELECT \" + columnName + \" from \" + tableName + \";\";",
      "String test2 = new StringBuilder(\"SELECT \").append(columnName).append(\" from \").append(tableName).append(\";\").toString();",
      "\"SELECT \" + columnName2 + \" from \" + noVariableAssigned2 + \";\";",
      "assertSqlCanBeParsedAndDeparsed(\"WITH split (word, str, hascomma) AS (VALUES ('', 'Auto,A,1234444', 1) UNION ALL SELECT substr(str, 0, CASE WHEN instr(str, ',') THEN instr(str, ',') ELSE length(str) + 1 END), ltrim(substr(str, instr(str, ',')), ','), instr(str, ',') FROM split WHERE hascomma) SELECT trim(word) FROM split WHERE word != ''\");",
      "StringBuilder queryStrBuilder2 = new StringBuilder()\n"
          + "	.append(\"WITH split (    word\\n\")\n"
          + "	.append(\"                , str\\n\")\n"
          + "	.append(\"                , hascomma ) AS (\\n\")\n"
          + "	.append(\"        VALUES ( '', 'Auto,A,1234444', 1 )\\n\")\n"
          + "	.append(\"        UNION ALL\\n\")\n"
          + "	.append(\"        SELECT  Substr( str, 0, CASE\\n\")\n"
          + "	.append(\"                        WHEN Instr( str, ',' )\\n\")\n"
          + "	.append(\"                            THEN Instr( str, ',' )\\n\")\n"
          + "	.append(\"                        ELSE Length( str ) + 1\\n\")\n"
          + "	.append(\"                    END )\\n\")\n"
          + "	.append(\"                , Ltrim( Substr( str, Instr( str, ',' ) ), ',' )\\n\")\n"
          + "	.append(\"                , Instr( str, ',' )\\n\")\n"
          + "	.append(\"        FROM split\\n\")\n"
          + "	.append(\"        WHERE hascomma )\\n\")\n"
          + "	.append(\"SELECT Trim( word )\\n\")\n"
          + "	.append(\"FROM split\\n\")\n"
          + "	.append(\"WHERE word != ''\\n\")\n"
          + "	.append(\";\\n\");"
    };
    for (String s : escaped) {
      try {
        String sql = formatJava(s);
        String javaStr = toJavaMessageFormat(sql);

        System.out.println(javaStr);
      } catch (Exception ex) {
        Logger.getLogger(JavaTools.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public static String formatJava(String javaCode, String... options) throws Exception {
    StringBuilder formatted = new StringBuilder();

    File lexerFile =
        new File(ClassLoader.getSystemResource("com/manticore/antlr/JavaLexer.g4").toURI());
    File parserFile =
        new File(ClassLoader.getSystemResource("com/manticore/antlr/JavaParser.g4").toURI());

    final String className = "TMP" + System.currentTimeMillis();

    final StringBuilder source =
        new StringBuilder()
            .append("public class ")
            .append(className)
            .append("{\n")
            .append("\tpublic static void mock() {")
            .append("\t\t\n");
    source.append("\t\t").append(javaCode).append("\n");
    source.append("\t}\n}\n");

    DefaultTreeListener t = new DefaultTreeListener();
    GenericParser gp = new GenericParser(lexerFile, parserFile);
    gp.setListener(t);
    try {
      gp.compile();
      //gp.writeAntlrAritfactsTo("/tmp/grammar");
      //gp.store("/tmp/grammar/gp.out", true);

      ParseTree parseTree;
      gp.parse(source.toString(), GenericParser.CaseSensitiveType.NONE);
      parseTree = t.getParseTree();

      StringBuilder builder = new StringBuilder();
      int indent = 0;

      ArrayList<LocalVariableDeclaration> declarations = new ArrayList<>();

      ParseTreeNode root = parseTree.getRoot();
      append(builder, root, indent, declarations);

      for (LocalVariableDeclaration declaration : declarations) {
				String unformattedSql = declaration.sqlBuilder.toString();
				unformattedSql= unformattedSql.replace("\\n", " ");
				unformattedSql= unformattedSql.replace("\\t", " ");
				
        formatted
            .append("\n")
            .append(JSQLFormatter.format(unformattedSql, options));
      }
    } catch (Exception ex) {
      throw new Exception("Could not parse Java Code:\n" + javaCode, ex);
    }
    return formatted.toString();
  }

  public static String toJavaString(String sql) {
    Pattern pattern = Pattern.compile("\\W\\$(\\w+)\\$(\\W|\\Z)");
    StringBuilder builder = new StringBuilder("String queryStr = ");
    try {
      StringReader stringReader = new StringReader(sql);
      BufferedReader bufferedReader = new BufferedReader(stringReader);
      String line;
      int i = 0;
      while ((line = bufferedReader.readLine()) != null) {
        Matcher m;
        while ((m = pattern.matcher(line)).find()) {
          String variableName = m.group(1);
          line = line.replaceFirst("\\$" + variableName + "\\$", "\" + " + variableName + " + \"");
        }

        if (line.trim().length() > 0) {
          builder.append("\n\t");
        }
        if (i > 0) {
          builder.append("+ ");
        } else builder.append("  ");

        if (line.trim().length() > 0) {
          builder.append("\"").append(line).append("\\n\"");
          i++;
        }
      }
      builder.append(";");

    } catch (IOException ex) {
      Logger.getLogger(JavaTools.class.getName()).log(Level.SEVERE, null, ex);
    }
    return builder.toString().replace(" + \"\"", "");
  }

  public static String toJavaStringBuilder(String sql) {
    Pattern pattern = Pattern.compile("\\W\\$(\\w+)\\$(\\W|\\Z)");
    StringBuilder builder =
        new StringBuilder("StringBuilder queryStrBuilder = new StringBuilder()");
    try {
      StringReader stringReader = new StringReader(sql);
      BufferedReader bufferedReader = new BufferedReader(stringReader);
      String line;
      int i = 0;
      while ((line = bufferedReader.readLine()) != null) {
        Matcher m;
        while ((m = pattern.matcher(line)).find()) {
          String variableName = m.group(1);
          line =
              line.replaceFirst(
                  "\\$" + variableName + "\\$", "\").append(" + variableName + ").append(\"");
        }

        if (line.trim().length() > 0) {
          builder.append("\n\t");
        }

        if (line.trim().length() > 0) {
          builder.append(".append(\"").append(line).append("\\n\")");
          i++;
        }
      }
      builder.append(";");

    } catch (IOException ex) {
      Logger.getLogger(JavaTools.class.getName()).log(Level.SEVERE, null, ex);
    }
    return builder.toString().replace(".append(\"\")", "");
  }

  public static String toJavaMessageFormat(String sql) {
    Pattern pattern = Pattern.compile("\\W\\$(\\w+)\\$(\\W|\\Z)");
    StringBuilder builder = new StringBuilder("String queryStr = new MessageFormat(");
    try {
      ArrayList<String> variables = new ArrayList<>();

      StringReader stringReader = new StringReader(sql);
      BufferedReader bufferedReader = new BufferedReader(stringReader);
      String line;
      int i = 0;
      int k = 0;
      while ((line = bufferedReader.readLine()) != null) {
        Matcher m;
        while ((m = pattern.matcher(line)).find()) {
          String variableName = m.group(1);
          variables.add(variableName);

          line = line.replaceFirst("\\$" + variableName + "\\$", "{" + k + "}");
          k++;
        }
        if (line.trim().length() > 0) {
          builder.append("\n\t\t\t");
        }
        if (i > 0) {
          builder.append("+");
        }

        if (line.trim().length() > 0) {
          builder.append("\"").append(line).append("\\n\"");
          i++;
        }
      }
      builder.append("\n\t\t).format(");

      builder.append("new Object[]{");
      i = 0;
      for (String v : variables) {
        if (i > 0) builder.append(", ");
        builder.append(v);
        i++;
      }
      builder.append("});\n");

    } catch (IOException ex) {
      Logger.getLogger(JavaTools.class.getName()).log(Level.SEVERE, null, ex);
    }
    return builder.toString().replace(".append(\"\")", "");
  }

  private static void append(
      StringBuilder builder,
      ParseTreeNode p,
      int indent,
      ArrayList<LocalVariableDeclaration> declarations) {
    for (int i = 0; i < indent; i++) builder.append("    ");

    builder.append(p.toString()).append("\n");
    if (p.getRule().equalsIgnoreCase("blockStatement")) {
      declarations.add(new LocalVariableDeclaration());

    } else if (p.getRule().equalsIgnoreCase("localVariableDeclaration")) {
      declarations.get(declarations.size() - 1).typeString = p.getChild(0).getLabel();

    } else if (p.getRule().equalsIgnoreCase("variableDeclaratorId")) {
      declarations.get(declarations.size() - 1).label = p.getLabel();

    } else if (p.getRule().equalsIgnoreCase("primary")) {
      LocalVariableDeclaration declaration = declarations.get(declarations.size() - 1);
      String label = p.getLabel();
      if (p.hasChildren()) {
        declaration.sqlBuilder.append(label.substring(1, label.length() - 1)).append(" ");
      } else {
        declaration.sqlBuilder.append(" $").append(label).append("$");
        declaration.parameters.add(label);
      }
    }

    for (ParseTreeNode n : p.getChildren()) {
      append(builder, n, indent + 1, declarations);
    }
  }
}
