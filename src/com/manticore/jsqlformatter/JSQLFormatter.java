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

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.ReferentialAction;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
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

/**
 * A powerful Java SQL Formatter based on the JSQLParser.
 *
 * @author <a href="mailto:andreas@manticore-projects.com">Andreas Reichel</a>
 * @version 0.1
 */
public class JSQLFormatter {

  private static void appendDelete(StringBuilder builder, Delete delete, int indent) {
    int i = 0;

    builder.append("DELETE ");

    /* @todo: activate when PR is accepted
    OracleHint oracleHint = delete.getOracleHint();
    if (oracleHint != null) builder.append(oracleHint.toString()).append(" ");
    * */

    List<Table> tables = delete.getTables();
    if (tables != null && tables.size() > 0) {
      //            b.append(" ");
      //            b.append(tables.stream()
      //                    .map(t -> t.toString())
      //                    .collect(joining(", ")));

      // @todo: implement Table List in DELETE
      throw new UnsupportedOperationException("Table List in DELETE is not supported yet.");
    }

    builder.append("FROM ");

    Table table = delete.getTable();
    Alias alias = table.getAlias();

    appendFromItem(table, alias, builder, indent, 0);

    List<Join> joins = delete.getJoins();
    appendJoins(joins, builder, indent, i);

    Expression whereExpression = delete.getWhere();
    appendWhere(whereExpression, builder, indent);

    List<OrderByElement> orderByElements = delete.getOrderByElements();
    appendOrderByElements(orderByElements, i, builder, indent);

    Limit limit = delete.getLimit();
    if (limit != null) {
      // @todo: implement limit in DELETE
      throw new UnsupportedOperationException("Limit in DELETE is not supported yet.");
    }
  }

  /** @param args The Command Line Parameters. */
  public static void main(String[] args) {
    String sqlStr = "with a as (SELECT 1 FROM b UNION ALL select 1 FROM c) select * from a;";

    try {
      sqlStr = JSQLFormatter.format(sqlStr);
      System.out.println(sqlStr);
    } catch (Exception ex) {
      Logger.getLogger(JSQLFormatter.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Format a list of SQL Statements.
   *
   * <p>SELECT, INSERT, UPDATE and MERGE statements are supported.
   *
   * @param sqlStr The ugly unformatted SQL Statements, semi-colon separated.
   * @return The beautifully formatted SQL Statements, semi-colon separated.
   * @throws Exception Any parsing exception.
   */
  public static String format(String sqlStr) throws Exception {
    StringBuilder builder = new StringBuilder();

    // All Known Implementing Classes:
    // Alter, AlterSequence, AlterView, Block, Comment, Commit, CreateFunction,
    // CreateFunctionalStatement, CreateIndex, CreateProcedure, CreateSchema, CreateSequence,
    // CreateSynonym, CreateTable, CreateView, DeclareStatement, Delete, DescribeStatement, Drop,
    // Execute, ExplainStatement, Grant, Insert, Merge, Replace, Select, SetStatement,
    // ShowColumnsStatement, ShowStatement, ShowTablesStatement, Truncate, Update, Upsert,
    // UseStatement, ValuesStatement

    int indent = 0;

    Statements statements = CCJSqlParserUtil.parseStatements(sqlStr);
    for (Statement statement : statements.getStatements()) {
      if (statement instanceof Select) {
        Select select = (Select) statement;
        appendSelect(select, builder, indent, true);

      } else if (statement instanceof Update) {
        Update update = (Update) statement;
        appendUpdate(builder, update, indent);

      } else if (statement instanceof Insert) {
        Insert insert = (Insert) statement;
        appendInsert(builder, insert, indent);

      } else if (statement instanceof Merge) {
        Merge merge = (Merge) statement;
        appendMerge(builder, merge, indent);

      } else if (statement instanceof Delete) {
        Delete delete = (Delete) statement;
        appendDelete(builder, delete, indent);

      } else if (statement instanceof Truncate) {
        Truncate truncate = (Truncate) statement;
        appendTruncate(builder, truncate, indent);

      } else if (statement instanceof CreateTable) {
        CreateTable createTable = (CreateTable) statement;
        appendCreateTable(builder, createTable, indent);

      } else if (statement instanceof CreateIndex) {
        CreateIndex createIndex = (CreateIndex) statement;
        appendCreateIndex(builder, createIndex, indent);

      } else if (statement instanceof CreateView) {
        CreateView createView = (CreateView) statement;
        appendCreateView(builder, createView, indent);

      } else if (statement != null) {
        try {
          builder.append("\n").append(statement.toString());
        } catch (Exception ex) {
          throw new UnsupportedOperationException(
              "The " + statement.getClass().getName() + " Statement is not supported yet.");
        }
      }
      builder.append("\n;");
    }
    return builder.toString().trim();
  }

  private static void appendMerge(StringBuilder builder, Merge merge, int indent) {
    int i = 0;

    builder.append("MERGE INTO ");

    Table table = merge.getTable();
    Alias alias = table.getAlias();

    appendFromItem(table, alias, builder, indent, 0);

    builder.append("\n");
    for (int j = 0; j < indent + 1; j++) builder.append("    ");
    builder.append("USING ");

    SubSelect select = merge.getUsingSelect();
    if (select != null) {
      alias = merge.getUsingAlias();
      int lastIndex = builder.lastIndexOf("\n");
      int lastLineLength = builder.length() - lastIndex + 1;

      int subIndent = lastLineLength / 4;

      appendExpression(select, alias, builder, subIndent, i, false, BreakLine.AFTER_FIRST);
    }

    table = merge.getUsingTable();
    if (table != null) {
      alias = table.getAlias();
      appendFromItem(table, alias, builder, indent, 0);
    }

    Expression onExpression = merge.getOnCondition();
    if (onExpression != null) {
      builder.append("\n");
      for (int j = 0; j < indent + 2; j++) builder.append("    ");
      builder.append("ON ( ");

      int lastIndex = builder.lastIndexOf("\n");
      int lastLineLength = builder.length() - lastIndex + 1;
      int subIndent = lastLineLength / 4;

      appendExpression(onExpression, null, builder, subIndent, 0, false, BreakLine.AFTER_FIRST);
      builder.append(" ) ");
    }

    MergeInsert insert = merge.getMergeInsert();
    if (insert != null) {
      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("WHEN NOT MATCHED THEN ");

      builder.append("\n");
      for (int j = 0; j < indent + 1; j++) builder.append("    ");
      builder.append("INSERT ");

      List<Column> columns = insert.getColumns();
      List<Expression> expressions = insert.getValues();

      if (columns != null) {
        builder.append("( ");
        int lastIndex = builder.lastIndexOf("\n");
        int lastLineLength = builder.length() - lastIndex + 1;
        int subIndent = lastLineLength / 4;

        for (Column column : columns) {
          appendExpression(column, null, builder, subIndent, i, true, BreakLine.AFTER_FIRST);
          i++;
        }
        builder.append(" ) ");
      }

      i = 0;
      builder.append("\n");
      for (int j = 0; j < indent + 1; j++) builder.append("    ");
      builder.append("VALUES ( ");

      int lastIndex = builder.lastIndexOf("\n");
      int lastLineLength = builder.length() - lastIndex + 1;
      int subIndent = lastLineLength / 4;
      if (expressions != null)
        for (Expression expression : expressions) {
          appendExpression(expression, null, builder, subIndent, i, true, BreakLine.AFTER_FIRST);
          i++;
        }
      builder.append(" ) ");
      
      Expression whereCondition = insert.getWhereCondition();
      if (whereCondition != null) {
        builder.append("\n");
        for (int j = 0; j < indent + 1; j++) builder.append("    ");
        builder.append("WHERE ");

        lastIndex = builder.lastIndexOf("\n");
        lastLineLength = builder.length() - lastIndex + 1;
        subIndent = lastLineLength / 4;

        appendExpression(whereCondition, null, builder, subIndent, 0, false, BreakLine.AFTER_FIRST);
      }
    }

    MergeUpdate update = merge.getMergeUpdate();
    if (update != null) {
      i = 0;

      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("WHEN MATCHED THEN ");

      builder.append("\n");
      for (int j = 0; j < indent + 1; j++) builder.append("    ");
      builder.append("UPDATE SET ");

      int lastIndex = builder.lastIndexOf("\n");
      int lastLineLength = builder.length() - lastIndex + 1;
      int subIndent = lastLineLength / 4;

      List<Column> columns = update.getColumns();
      List<Expression> expressions = update.getValues();

      if (columns != null)
        for (Column column : columns) {
          appendExpression(column, null, builder, subIndent, i, true, BreakLine.AFTER_FIRST);
          builder.append(" = ");
          appendExpression(
              expressions.get(i), null, builder, subIndent, 0, false, BreakLine.AFTER_FIRST);
          i++;
        }

      Expression whereCondition = update.getWhereCondition();
      if (whereCondition != null) {
        builder.append("\n");
        for (int j = 0; j < indent + 1; j++) builder.append("    ");
        builder.append("WHERE ");

        lastIndex = builder.lastIndexOf("\n");
        lastLineLength = builder.length() - lastIndex + 1;
        subIndent = lastLineLength / 4;

        appendExpression(whereCondition, null, builder, subIndent, 0, false, BreakLine.AFTER_FIRST);
      }

      Expression deleteWhereCondition = update.getDeleteWhereCondition();
      if (deleteWhereCondition != null) {
        builder.append("\n");
        for (int j = 0; j < indent + 1; j++) builder.append("    ");
        builder.append("DELETE WHERE ");

        lastIndex = builder.lastIndexOf("\n");
        lastLineLength = builder.length() - lastIndex + 1;
        subIndent = lastLineLength / 4;

        appendExpression(
            deleteWhereCondition, null, builder, subIndent, 0, false, BreakLine.AFTER_FIRST);
      }
    }
  }

  private static void appendInsert(StringBuilder builder, Insert insert, int indent) {
    int i = 0;

    builder.append("INSERT INTO ");

    Table table = insert.getTable();
    Alias alias = table.getAlias();

    appendFromItem(table, alias, builder, indent, 0);

    List<Column> columns = insert.getColumns();
    if (columns != null) {
      builder.append("(");
      for (Column column : columns) {
        appendExpression(column, null, builder, indent, i, true, BreakLine.ALWAYS);
        i++;
      }
      builder.append(" ) ");
    }

    if (insert.isUseValues()) {
      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("VALUES ( ");

      int lastIndex = builder.lastIndexOf("\n");
      int lastLineLength = builder.length() - lastIndex + 1;

      int subIndent = lastLineLength / 4;

      ItemsList itemsList = insert.getItemsList();
      appendItemsList(itemsList, builder, alias, subIndent, BreakLine.AFTER_FIRST);
      builder.append(" ) ");
    } else {
      Select select = insert.getSelect();
      builder.append("( ");
      int lastIndex = builder.lastIndexOf("\n");
      int lastLineLength = builder.length() - lastIndex + 1;

      int subIndent = lastLineLength / 4;

      appendSelect(select, builder, subIndent, false);
      builder.append(") ");
    }
  }

  private static void appendUpdate(StringBuilder builder, Update update, int indent) {
    builder.append("UPDATE ");

    Table table = update.getTable();
    Alias alias = table.getAlias();

    appendFromItem(table, alias, builder, indent, 0);

    int i = 0;
    builder.append("\n");
    for (int j = 0; j < indent; j++) builder.append("    ");
    builder.append("SET ");

    if (update.isUseSelect()) {
      builder.append("( ");
      int lastIndex = builder.lastIndexOf("\n");
      int lastLineLength = builder.length() - lastIndex + 1;

      int subIndent = lastLineLength / 4;

      List<Column> columns = update.getColumns();
      if (columns != null)
        for (Column column : columns) {
          appendExpression(column, null, builder, subIndent, i, true, BreakLine.AFTER_FIRST);
          i++;
        }
      builder.append(" ) = ");

      Select select = update.getSelect();
      builder.append("( ");
      lastIndex = builder.lastIndexOf("\n");
      lastLineLength = builder.length() - lastIndex + 1;

      subIndent = lastLineLength / 4;

      appendSelect(select, builder, subIndent, false);
      builder.append(" ) ");
    } else {
      List<Column> columns = update.getColumns();
      List<Expression> expressions = update.getExpressions();

      if (columns != null)
        for (Column column : columns) {
          appendExpression(column, null, builder, indent, i, true, BreakLine.AFTER_FIRST);
          builder.append(" = ");
          appendExpression(
              expressions.get(i), null, builder, indent, 0, false, BreakLine.AFTER_FIRST);
          i++;
        }
    }

    List<Join> joins = update.getJoins();
    appendJoins(joins, builder, indent, i);

    Expression whereExpression = update.getWhere();
    appendWhere(whereExpression, builder, indent);

    List<OrderByElement> orderByElements = update.getOrderByElements();
    appendOrderByElements(orderByElements, i, builder, indent);
  }

  private static void appendSelect(
      Select select, StringBuilder builder, int indent, boolean breakLineBefore) {
    List<WithItem> withItems = select.getWithItemsList();
    if (withItems != null && withItems.size() > 0) {
      int i = 0;
      builder.append("WITH ");

      for (WithItem withItem : withItems) {
        appendWithItem(withItem, null, builder, indent, i);
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
        for (int j = 0; j < indent; j++) builder.append("    ");
      }
      builder.append("SELECT ");

      OracleHint oracleHint = plainSelect.getOracleHint();
      if (oracleHint != null) builder.append(oracleHint.toString()).append(" ");

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
        builder.append("DISTINCT ");
      }

      for (SelectItem selectItem : plainSelect.getSelectItems()) {
        if (selectItem instanceof SelectExpressionItem) {
          SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;

          Alias alias = selectExpressionItem.getAlias();
          Expression expression = selectExpressionItem.getExpression();

          appendExpression(expression, alias, builder, indent, i, true, BreakLine.AFTER_FIRST);
        } else if (selectItem instanceof AllColumns) {
          AllColumns allColumns = (AllColumns) selectItem;
          appendAllColumns(allColumns, builder, indent, i);
        } else {
          System.out.println(selectItem.getClass().getName());
        }

        i++;
      }

      // All Known Implementing Classes: LateralSubSelect, ParenthesisFromItem,
      // SpecialSubSelect, SubJoin, SubSelect, Table, TableFunction, ValuesList
      FromItem fromItem = plainSelect.getFromItem();

      i = 0;
      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("FROM ");

      appendFromItem(fromItem, builder, indent, i);

      i++;

      List<Join> joins = plainSelect.getJoins();
      appendJoins(joins, builder, indent, i);

      Expression whereExpression = plainSelect.getWhere();
      appendWhere(whereExpression, builder, indent);

      GroupByElement groupByElement = plainSelect.getGroupBy();
      appendGroupByElement(groupByElement, builder, indent);

      Expression havingExpression = plainSelect.getHaving();
      appendHavingExpression(havingExpression, builder, indent);

      List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
      appendOrderByElements(orderByElements, i, builder, indent);
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
    } else {
      System.out.println(selectBody.getClass().getName());
    }
  }

  private static void appendOrderByElements(
      List<OrderByElement> orderByElements, int i, StringBuilder builder, int indent) {
    if (orderByElements != null) {
      i = 0;
      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("ORDER BY ");

      for (OrderByElement orderByElement : orderByElements) {
        Expression expression = orderByElement.getExpression();
        appendExpression(expression, null, builder, indent, i, true, BreakLine.AFTER_FIRST);

        NullOrdering nullOrdering = orderByElement.getNullOrdering();
        if (NullOrdering.NULLS_FIRST.equals(nullOrdering))
          builder.append("NULLS FIRST").append(" ");

        if (NullOrdering.NULLS_LAST.equals(nullOrdering)) builder.append("NULLS LAST").append(" ");

        if (orderByElement.isAscDescPresent())
          builder.append(orderByElement.isAsc() ? "ASC " : "DESC");

        i++;
      }
    }
  }

  private static void appendHavingExpression(
      Expression havingExpression, StringBuilder builder, int indent) {
    int i;
    if (havingExpression != null) {
      i = 0;
      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("HAVING ");

      appendExpression(havingExpression, null, builder, indent, i, false, BreakLine.AFTER_FIRST);
    }
  }

  private static void appendGroupByElement(
      GroupByElement groupByElement, StringBuilder builder, int indent)
      throws UnsupportedOperationException {
    int i;
    if (groupByElement != null) {
      i = 0;
      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("GROUP BY ");

      List groupingSets = groupByElement.getGroupingSets();
      List<Expression> groupByExpressions = groupByElement.getGroupByExpressions();

      if (groupingSets != null && groupingSets.size() > 0) {
        throw new UnsupportedOperationException("Grouping Sets are not supported yet.");
      }

      if (groupByExpressions != null && groupByExpressions.size() > 0) {

        for (Expression groupExpression : groupByExpressions) {
          appendExpression(groupExpression, null, builder, indent, i, true, BreakLine.AFTER_FIRST);
        }
      }
    }
  }

  private static void appendWhere(Expression whereExpression, StringBuilder builder, int indent) {
    int i;
    if (whereExpression != null) {
      i = 0;
      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("WHERE ");

      appendExpression(whereExpression, null, builder, indent, i, false, BreakLine.AFTER_FIRST);
    }
  }

  private static void appendJoins(List<Join> joins, StringBuilder builder, int indent, int i) {
    if (joins != null)
      for (Join join : joins) {
        builder.append("\n");

        if (join.isSimple()) {
          for (int j = 0; j <= indent; j++) builder.append("    ");
          builder.append(", ");
        } else {
          for (int j = 0; j <= indent + 1; j++) builder.append("    ");

          if (join.isInner()) builder.append("INNER ");
          if (join.isLeft()) builder.append("LEFT ");
          if (join.isRight()) builder.append("RIGHT ");
          if (join.isNatural()) builder.append("NATURAL ");
          if (join.isCross()) builder.append("CROSS ");
          if (join.isOuter()) builder.append("OUTER ");
          if (join.isFull()) builder.append("FULL ");

          builder.append("JOIN ");
        }

        FromItem rightFromItem = join.getRightItem();
        appendFromItem(rightFromItem, builder, indent, 0);

        Expression onExpression = join.getOnExpression();
        if (onExpression != null) {
          builder.append("\n");
          for (int j = 0; j <= indent + 2; j++) builder.append("    ");
          builder.append("ON ");

          appendExpression(
              onExpression, null, builder, indent + 3, 0, false, BreakLine.AFTER_FIRST);
        }

        List<Column> usingColumns = join.getUsingColumns();
        if (usingColumns != null && usingColumns.size() > 0) {
          builder.append("\n");
          for (int j = 0; j <= indent + 2; j++) builder.append("    ");
          builder.append("USING ( ");
          int k = 0;
          for (Column column : usingColumns) {
            appendExpression(column, null, builder, indent + 3, k, true, BreakLine.AFTER_FIRST);
            k++;
          }
          builder.append(" )");
        }

        i++;
      }
  }

  private static void appendWithItem(
      WithItem withItem, Alias alias, StringBuilder builder, int indent, int i) {

    if (i > 0) {
      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append(", ");
    }

    builder.append(withItem.getName());

    List<SelectItem> selectItems = withItem.getWithItemList();
    if (selectItems != null && selectItems.size() > 0) {
      builder.append(" (");

      int k = 0;
      for (SelectItem selectItem : selectItems) {
        // @todo: write code for selectItems
        k++;
      }
      builder.append(")");
    }

    builder.append(" AS ( ");
    appendSelectBody(withItem.getSelectBody(), null, builder, indent + 2, true);
    builder.append(" )");
  }

  private static void appendAllColumns(
      AllColumns allColumns, StringBuilder builder, int indent, int i) {
    if (i > 0) {
      builder.append("\n");
      for (int j = 0; j <= indent; j++) builder.append("    ");
      builder.append(", ");
    }
    builder.append(allColumns.toString());
  }
  
  private static void appendStringList(
      Collection<String> strings,
      Alias alias,
      StringBuilder builder,
      int indent,
      boolean commaSeparated,
      BreakLine breakLine) {
    int i = 0;
    for (String s: strings) {
      appendString(s, alias, builder, indent, i, commaSeparated, breakLine);
      i++;
    }
  }
  
  private static void appendString(
      String s,
      Alias alias,
      StringBuilder builder,
      int indent,
      int i,
      boolean commaSeparated,
      BreakLine breakLine) {

    if (i > 0 || breakLine.equals(BreakLine.ALWAYS)) {
      if (!breakLine.equals(BreakLine.NEVER)) {
        builder.append("\n");
        for (int j = 0; j < indent; j++) builder.append("    ");
      }
      if (commaSeparated && i > 0) builder.append(", ");
    }

     builder.append(s);
  }

  private static void appendExpression(
      Expression expression,
      Alias alias,
      StringBuilder builder,
      int indent,
      int i,
      boolean commaSeparated,
      BreakLine breakLine) {

    if (i > 0 || breakLine.equals(BreakLine.ALWAYS)) {
      if (!breakLine.equals(BreakLine.NEVER)) {
        builder.append("\n");
        for (int j = 0; j <= indent; j++) builder.append("    ");
      }
      if (commaSeparated && i > 0) builder.append(", ");
    }

    if (expression instanceof Column) {
      Column column = (Column) expression;
      builder.append(column.getFullyQualifiedName());

    } else if (expression instanceof AndExpression) {
      AndExpression andExpression = (AndExpression) expression;
      appendExpression(
          andExpression.getLeftExpression(),
          null,
          builder,
          indent,
          i,
          false,
          BreakLine.AFTER_FIRST);

      builder.append("\n");
      for (int j = 0; j <= indent; j++) builder.append("    ");
      builder.append("AND ");

      appendExpression(
          andExpression.getRightExpression(),
          null,
          builder,
          indent + 1,
          i,
          false,
          BreakLine.AFTER_FIRST);

    } else if (expression instanceof OrExpression) {
      OrExpression orExpression = (OrExpression) expression;
      appendExpression(
          orExpression.getLeftExpression(), null, builder, indent, i, false, BreakLine.AFTER_FIRST);

      builder.append("\n");
      for (int j = 0; j <= indent; j++) builder.append("    ");
      builder.append("OR ");

      appendExpression(
          orExpression.getRightExpression(),
          null,
          builder,
          indent + 1,
          i,
          false,
          BreakLine.AFTER_FIRST);

    } else if (expression instanceof BinaryExpression) {
      BinaryExpression binaryExpression = (BinaryExpression) expression;
      builder.append(binaryExpression.getLeftExpression()).append(" ");
      builder.append(binaryExpression.getStringExpression()).append(" ");

      appendExpression(
          binaryExpression.getRightExpression(),
          null,
          builder,
          indent + 1,
          i,
          false,
          BreakLine.NEVER);

    } else if (expression instanceof EqualsTo) {
      EqualsTo equalsTo = (EqualsTo) expression;
      builder.append(equalsTo.getLeftExpression());
      builder.append(" = ");
      // builder.append(equalsTo.getRightExpression());
      appendExpression(
          equalsTo.getRightExpression(),
          alias,
          builder,
          indent + 1,
          i,
          false,
          BreakLine.AFTER_FIRST);
    } else if (expression instanceof Parenthesis) {
      Parenthesis parenthesis = (Parenthesis) expression;
      builder.append("( ");
      appendExpression(
          parenthesis.getExpression(), null, builder, indent + 1, i, false, BreakLine.AFTER_FIRST);
      builder.append(" )");
    } else if (expression instanceof CaseExpression) {
      CaseExpression caseExpression = (CaseExpression) expression;
      builder.append("CASE ");

      List<WhenClause> whenClauses = caseExpression.getWhenClauses();
      for (WhenClause whenClause : whenClauses) {
        builder.append("\n");
        for (int j = 0; j <= indent + 1; j++) builder.append("    ");
        builder.append("WHEN ");
        appendExpression(
            whenClause.getWhenExpression(),
            null,
            builder,
            indent + 3,
            0,
            false,
            BreakLine.AFTER_FIRST);

        builder.append("\n");
        for (int j = 0; j <= indent + 2; j++) builder.append("    ");
        builder.append("THEN ");
        appendExpression(
            whenClause.getThenExpression(),
            null,
            builder,
            indent + 1,
            0,
            false,
            BreakLine.AFTER_FIRST);
      }

      Expression elseExpression = caseExpression.getElseExpression();
      if (elseExpression != null) {
        builder.append("\n");
        for (int j = 0; j <= indent + 1; j++) builder.append("    ");
        builder.append("ELSE ");
        appendExpression(
            elseExpression, null, builder, indent + 1, 0, false, BreakLine.AFTER_FIRST);
      }

      builder.append("\n");
      for (int j = 0; j <= indent; j++) builder.append("    ");
      builder.append(" END ");

    } else if (expression instanceof LongValue) {
      LongValue longValue = (LongValue) expression;
      builder.append(longValue.toString());
    } else if (expression instanceof DateValue) {
      DateValue dateValue = (DateValue) expression;
      builder.append(dateValue.toString());
    } else if (expression instanceof DoubleValue) {
      DoubleValue doubleValue = (DoubleValue) expression;
      builder.append(doubleValue.toString());
    } else if (expression instanceof NotExpression) {
      NotExpression notExpression = (NotExpression) expression;
      builder.append(notExpression.isExclamationMark() ? "!" : "NOT ");
      appendExpression(
          notExpression.getExpression(),
          null,
          builder,
          indent + 1,
          i,
          false,
          BreakLine.AFTER_FIRST);
    } else if (expression instanceof ExistsExpression) {
      ExistsExpression existsExpression = (ExistsExpression) expression;
      builder.append(existsExpression.isNot() ? "NOT EXISTS " : "EXISTS ");
      appendExpression(
          existsExpression.getRightExpression(),
          null,
          builder,
          indent + 1,
          i,
          false,
          BreakLine.AFTER_FIRST);
    } else if (expression instanceof StringValue) {
      StringValue stringValue = (StringValue) expression;
      builder.append(stringValue.toString());
    } else if (expression instanceof JdbcNamedParameter) {
      JdbcNamedParameter jdbcNamedParameter = (JdbcNamedParameter) expression;
      builder.append(jdbcNamedParameter.toString());
    } else if (expression instanceof JdbcParameter) {
      JdbcParameter jdbcParameter = (JdbcParameter) expression;
      builder.append(jdbcParameter.toString());
    } else if (expression instanceof Function) {
      Function function = (Function) expression;
      builder.append(function.getName());

    } else if (expression instanceof IsNullExpression) {
      IsNullExpression isNullExpression = (IsNullExpression) expression;
      appendExpression(
          isNullExpression.getLeftExpression(),
          null,
          builder,
          indent + 1,
          i,
          false,
          BreakLine.AFTER_FIRST);

      if (isNullExpression.isUseIsNull()) {
        builder.append(isNullExpression.isNot() ? " NOT" : "").append(" ISNULL");
      } else {
        builder.append(" IS").append(isNullExpression.isNot() ? " NOT" : "").append(" NULL");
      }

    } else if (expression instanceof NullValue) {
      NullValue nullValue = (NullValue) expression;
      builder.append(nullValue.toString());
    } else if (expression instanceof TimeKeyExpression) {
      TimeKeyExpression timeKeyExpression = (TimeKeyExpression) expression;
      builder.append(timeKeyExpression.toString());
    } else if (expression instanceof InExpression) {
      InExpression inExpression = (InExpression) expression;
      builder.append(inExpression.getLeftExpression());
      if (inExpression.isNot()) builder.append(" NOT IN ");
      else builder.append(" IN ");

      ItemsList itemsList = inExpression.getRightItemsList();
      if (itemsList != null) {
        builder.append(" ( ");
        int lastIndex = builder.lastIndexOf("\n");
        int lastLineLength = builder.length() - lastIndex + 1;

        int subIndent = lastLineLength / 4;

        appendItemsList(itemsList, builder, alias, subIndent, BreakLine.AS_NEEDED);
        builder.append(" ) ");

      } else {
        appendExpression(
            inExpression.getRightExpression(),
            alias,
            builder,
            indent + 1,
            i,
            commaSeparated,
            BreakLine.AFTER_FIRST);
      }
    } else if (expression instanceof SignedExpression) {
      SignedExpression signedExpression = (SignedExpression) expression;
      builder.append(signedExpression.toString());
    } else if (expression instanceof SubSelect) {
      SubSelect subSelect = (SubSelect) expression;
      builder.append("( ");

      int lastIndex = builder.lastIndexOf("\n");
      int lastLineLength = builder.length() - lastIndex + 1;

      int subIndent = lastLineLength / 4;

      List<WithItem> withItems = subSelect.getWithItemsList();
      if (withItems != null && withItems.size() > 0) {
        int j = 0;
        builder.append("WITH ");

        for (WithItem withItem : withItems) {
          appendWithItem(withItem, null, builder, subIndent, j);
          j++;
        }
      }

      SelectBody selectBody = subSelect.getSelectBody();
      Alias alias1 = subSelect.getAlias();

      appendSelectBody(selectBody, alias, builder, subIndent, withItems != null);
      builder.append(" ) ");
    } else {
      builder.append(expression);
    }

    if (alias != null) {
      int length = builder.length();

      if (!builder.substring(length - 1).equalsIgnoreCase(" ")) builder.append(" ");

      if (alias.isUseAs()) builder.append("AS ");

      builder.append(alias.getName());
    }
  }

  private static void appendItemsList(
      ItemsList itemsList, StringBuilder builder, Alias alias, int indent, BreakLine breakLine) {
    // All Known Implementing Classes:
    // ExpressionList, MultiExpressionList, NamedExpressionList, SubSelect
    if (itemsList instanceof ExpressionList) {
      ExpressionList expressionList = (ExpressionList) itemsList;
      int i = 0;
      // @todo: pretty print the Expression List Items
      for (Expression expression : expressionList.getExpressions()) {

        switch (breakLine) {
          case AS_NEEDED:
            BreakLine bl = i % 3 == 0 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;

            appendExpression(expression, null, builder, indent, i, true, bl);
            break;
          default:
            appendExpression(expression, null, builder, indent, i, true, breakLine);
        }

        //        BreakLine bl =
        //            BreakLine.AS_NEEDED.equals(breakLine) && i % 3 == 0
        //                ? BreakLine.AFTER_FIRST
        //                : BreakLine.NEVER;
        //
        //        appendExpression(expression, null, builder, indent, i, true, bl);
        i++;
      }
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

  //  private static void appendColumn(Column column, StringBuilder builder, int indent) {
  //    for (int i = 0; i < indent; i++) builder.append("    ");
  //    builder.append(", ").append(column.getFullyQualifiedName());
  //  }

  private static void appendFromItem(FromItem fromItem, StringBuilder builder, int indent, int i) {

    if (i > 0) {
      builder.append("\n");
      for (int j = 0; j <= indent; j++) builder.append("    ");
      builder.append(", ");
    }

    Alias alias = fromItem.getAlias();

    // All Known Implementing Classes: LateralSubSelect, ParenthesisFromItem,
    // SpecialSubSelect, SubJoin, SubSelect, Table, TableFunction, ValuesList
    if (fromItem instanceof Table) {
      Table table = (Table) fromItem;
      appendFromItem(table, alias, builder, indent, i);
    } else if (fromItem instanceof SubSelect) {
      SubSelect subSelect = (SubSelect) fromItem;
      appendSubSelect(subSelect, builder, true);

    } else {
      System.out.println(fromItem.getClass().getName());
    }
  }

  private static void appendSubSelect(
      SubSelect subSelect, StringBuilder builder, boolean useBrackets) {
    if (subSelect.isUseBrackets() && useBrackets) {
      builder.append(" ( ");
    }

    int lastIndex = builder.lastIndexOf("\n");
    int lastLineLength = builder.length() - lastIndex + 1;

    int subIndent = lastLineLength / 4;

    List<WithItem> withItems = subSelect.getWithItemsList();
    if (withItems != null && withItems.size() > 0) {
      int j = 0;
      builder.append("\nWITH ");

      for (WithItem withItem : withItems) {
        appendWithItem(withItem, null, builder, subIndent + 1, j);
        j++;
      }
    }

    SelectBody selectBody = subSelect.getSelectBody();
    Alias alias1 = subSelect.getAlias();

    appendSelectBody(selectBody, null, builder, subIndent + 1, false);

    if (subSelect.isUseBrackets() && useBrackets) {
      builder.append(" )");
    }

    if (alias1 != null) builder.append(" ").append(alias1);
  }

  private static void appendFromItem(
      Table table, Alias alias, StringBuilder builder, int indent, int i) {

    builder.append(table.getFullyQualifiedName());
    if (alias != null) {
      if (alias.isUseAs()) builder.append(" AS");

      builder.append(" ").append(alias.getName());
    }
  }

  private static void appendSetOperation(
      SetOperation setOperation, StringBuilder builder, int indent) {

    // Direct Known Subclasses:
    // ExceptOp, IntersectOp, MinusOp, UnionOp
    if (setOperation instanceof UnionOp) {
      UnionOp unionOp = (UnionOp) setOperation;

      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("UNION ");

      if (unionOp.isAll()) builder.append("ALL ");
    } else if (setOperation instanceof MinusOp) {
      MinusOp minusOp = (MinusOp) setOperation;

      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("MINUS ");
    } else if (setOperation instanceof IntersectOp) {
      IntersectOp intersectOp = (IntersectOp) setOperation;

      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("INTERSECT ");

    } else if (setOperation instanceof ExceptOp) {
      ExceptOp exceptOp = (ExceptOp) setOperation;

      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("EXCEPT ");
    } else if (setOperation != null)
      throw new UnsupportedOperationException(
          setOperation.getClass().getName() + " is not supported yet.");
  }

  private static void appendTruncate(StringBuilder builder, Truncate truncate, int indent) {
    Table table = truncate.getTable();
    boolean cascade = truncate.getCascade();

    builder.append("TRUNCATE TABLE ").append(table.getFullyQualifiedName());
    if (cascade) {
      builder.append(" CASCADE");
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
    builder
        .append("CREATE ")
        .append(unlogged ? "UNLOGGED " : "")
        .append(createOps != null ? createOps + " " : "")
        .append("TABLE ")
        .append(ifNotExists ? "IF NOT EXISTS " : "")
        .append(table.getFullyQualifiedName());

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

      // int typeIndex = (((indent +1)* "    ".length() + colWidth + 1) / "    ".length()) * ("
      // ".length() + 1);

      int typeIndex = indent + (colWidth / "    ".length()) + 3;

      int specIndex = indent + typeIndex + (typeWidth / "    ".length()) + 1;

      for (ColumnDefinition columnDefinition : columnDefinitions) {
        if (i > 0 || breakLine.equals(BreakLine.ALWAYS)) {
          if (!breakLine.equals(BreakLine.NEVER)) {
            builder.append("\n");
            for (int j = 0; j <= indent; j++) builder.append("    ");
          }
          if (commaSeparated && i > 0) builder.append(", ");
        }

        String columnName = columnDefinition.getColumnName();
        ColDataType colDataType = columnDefinition.getColDataType();
        List<String> columnSpecs = columnDefinition.getColumnSpecs();

        builder.append(columnName);

        int lastIndex = builder.lastIndexOf("\n");
        int lastLineLength = builder.length() - lastIndex + 1;

        for (int j = lastLineLength; j <= typeIndex * 4; j++) builder.append(" ");
        // @todo: please get rid of that Replace workaround
        builder.append(colDataType.toString().replace(", ", ","));

        lastLineLength = builder.length() - lastIndex + 1;

        if (columnSpecs != null && !columnSpecs.isEmpty()) {
          for (int j = lastLineLength; j <= specIndex * 4; j++) builder.append(" ");
          builder.append(PlainSelect.getStringList(columnSpecs, false, false));
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
              builder.append("\n");
              for (int j = 0; j <= indent; j++) builder.append("    ");
            }
            if (commaSeparated && i > 0) builder.append(", ");
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
            builder.append((name != null ? "CONSTRAINT " + name + " " : ""));

            builder.append("\n");
            for (int j = 0; j <= indent + 1; j++) builder.append("    ");
            builder.append(
                type
                    + " "
                    + PlainSelect.getStringList(columnsNames, true, true)
                    + (!"".equals(idxSpecText) ? " " + idxSpecText : ""));

            Table foreignTable = foreignKeyIndex.getTable();
            List<String> referencedColumnNames = foreignKeyIndex.getReferencedColumnNames();

            builder.append("\n");
            for (int j = 0; j <= indent + 1; j++) builder.append("    ");
            builder
                .append("REFERENCES ")
                .append(foreignTable)
                .append(PlainSelect.getStringList(referencedColumnNames, true, true));

            ReferentialAction updateAction =
                foreignKeyIndex.getReferentialAction(ReferentialAction.Type.UPDATE);
            if (updateAction != null) {
              builder.append("\n");
              for (int j = 0; j <= indent + 2; j++) builder.append("    ");
              builder.append(updateAction);
            }

            ReferentialAction deleteAction =
                foreignKeyIndex.getReferentialAction(ReferentialAction.Type.DELETE);
            if (deleteAction != null) {
              builder.append("\n");
              for (int j = 0; j <= indent + 2; j++) builder.append("    ");
              builder.append(deleteAction);
            }

          } else if (index instanceof CheckConstraint) {
            CheckConstraint checkConstraint = (CheckConstraint) index;

            String contraintName = checkConstraint.getName();
            Expression expression = checkConstraint.getExpression();

            builder.append("CONSTRAINT " + contraintName);
            builder.append("\n");
            for (int j = 0; j <= indent + 1; j++) builder.append("    ");

            builder.append(" CHECK (" + expression + ")");

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
            builder.append((name != null ? "CONSTRAINT " + name + " " : ""));
            builder.append("\n");
            for (int j = 0; j <= indent + 1; j++) builder.append("    ");
            builder.append(type).append(" ");
            builder.append(
                PlainSelect.getStringList(columnsNames, true, true)
                    + (!"".equals(idxSpecText) ? " " + idxSpecText : ""));

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

            builder.append(
                type
                    + (!name.isEmpty() ? " " + name : "")
                    + " "
                    + PlainSelect.getStringList(columnParams, true, true)
                    + (!"".equals(idxSpecText) ? " " + idxSpecText : ""));
          }
          i++;
        }
      }
      builder.append("\n  )");
    }
    List<String> tableOptionsStrings = createTable.getTableOptionsStrings();
    String options = PlainSelect.getStringList(tableOptionsStrings, false, false);
    if (options != null && options.length() > 0) {
      builder.append(" ").append(options);
    }

    RowMovement rowMovement = createTable.getRowMovement();
    if (rowMovement != null) {
      // @todo: beautify this part
      // @todo: provide test cases
      builder.append(" " + rowMovement.getMode().toString() + " ROW MOVEMENT");
    }

    Select select = createTable.getSelect();
    boolean selectParenthesis = createTable.isSelectParenthesis();
    if (select != null) {
      builder.append("\n");
      for (int j = 0; j <= indent; j++) builder.append("    ");
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

      appendFromItem(likeTable, alias, builder, indent + 1, i);

      if (selectParenthesis) builder.append(" )");
    }
  }

  private static void appendCreateIndex(
      StringBuilder builder, CreateIndex createIndex, int indent) {

    Index index = createIndex.getIndex();
    Table table = createIndex.getTable();

    List<String> tailParameters = createIndex.getTailParameters();

    builder.append("CREATE ");

    if (index.getType() != null) {
      builder.append(index.getType());
      builder.append(" ");
    }

    builder.append("INDEX ");
    builder.append(index.getName());
    
    builder.append("\n");
    for (int j = 0; j <= indent; j++) builder.append("    ");
    builder.append("ON ");
    builder.append(table.getFullyQualifiedName());

    if (index.getUsing() != null) {
      builder.append(" USING ");
      builder.append(index.getUsing());
    }

    if (index.getColumnsNames() != null) {
      builder.append(" ( ");
      
       int lastIndex = builder.lastIndexOf("\n");
      int lastLineLength = builder.length() - lastIndex + 1;

      int subIndent = lastLineLength / 4;
      
      List<Index.ColumnParams> columnsParameters = index.getColumns();
      int i=0;
      for ( Index.ColumnParams param: columnsParameters) {
        appendString(
            param.getColumnName(),
            null,
            builder,
            subIndent,
            i,
            true,
            columnsParameters.size() > 3 
                    ? BreakLine.AFTER_FIRST 
                    : BreakLine.NEVER);
        i++;
      }

      builder.append(" )");

      if (tailParameters != null) {
        for (String param : tailParameters) {
          builder.append(" ").append(param);
        }
      }
    }
  }

  private static void appendCreateView(StringBuilder builder, CreateView createView, int indent) {
    boolean isOrReplace = createView.isOrReplace();
    ForceOption force = createView.getForce();
    TemporaryOption temp= createView.getTemporary();
    boolean isMaterialized = createView.isMaterialized();
    
    Table view = createView.getView();
    
    List<String> columnNames = createView.getColumnNames();
    Select select = createView.getSelect();
    boolean isWithReadOnly = createView.isWithReadOnly();
    
    
    builder.append("CREATE ");
        if (isOrReplace) {
            builder.append("OR REPLACE ");
        }
        switch (force) {
            case FORCE:
                builder.append("FORCE ");
                break;
            case NO_FORCE:
                builder.append("NO FORCE ");
                break;
        }

        if (temp != TemporaryOption.NONE) {
            builder.append(temp.name()).append(" ");
        }

        if (isMaterialized) {
            builder.append("MATERIALIZED ");
        }
        builder.append("VIEW ");
        builder.append(view);
        if (columnNames != null) {
            builder.append(PlainSelect.getStringList(columnNames, true, true));
        }
        
        builder.append("\n");
        for (int j = 0; j <= indent; j++) builder.append("    ");
        builder.append("AS  ");
        appendSelect(select, builder, indent+2, false);
        
        if (isWithReadOnly) {
            builder.append(" WITH READ ONLY");
        }
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
