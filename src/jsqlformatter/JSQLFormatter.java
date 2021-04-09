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
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.merge.MergeInsert;
import net.sf.jsqlparser.statement.merge.MergeUpdate;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.select.OrderByElement.NullOrdering;
import net.sf.jsqlparser.statement.update.Update;

/** @author Andreas Reichel <andreas@manticore-projects.com> */
public class JSQLFormatter {

  public enum BreakLine {
    NEVER // keep all arguments on one line
    ,
    AS_NEEDED // only when more than 3 arguments
    ,
    AFTER_FIRST // break all after the first argument
    ,
    ALWAYS // break all arguments to a new line
  }

  public static void main(String[] args) {
    String sqlStr =
        "-- INSERT INTO LEDGER BRANCH BALANCE\n"
            + "-- INSERT INTO cfe.LEDGER_BRANCH_BALANCE \n\n"
            + "WITH scope\n"
            + "     AS (SELECT *\n"
            + "         FROM   cfe.accounting_scope\n"
            + "         WHERE  id_status = 'C'\n"
            + "                AND id_accounting_scope_code = :SCOPE),\n"
            + "     ex\n"
            + "     AS (SELECT *\n"
            + "         FROM   cfe.execution\n"
            + "         WHERE  id_status = 'R'\n"
            + "                AND value_date = (SELECT Max(value_date)\n"
            + "                                  FROM   cfe.execution\n"
            + "                                  WHERE  id_status = 'R'\n"
            + "                                         AND ( :VALUE_DATE IS NULL\n"
            + "                                                OR value_date <= :VALUE_DATE ))),\n"
            + "     fxr\n"
            + "     AS (SELECT id_currency_from\n"
            + "                , fxrate\n"
            + "         FROM   common.fxrate_hst f\n"
            + "                inner join ex\n"
            + "                    ON f.value_date <= ex.value_date\n"
            + "         WHERE  f.value_date = (SELECT Max(value_date)\n"
            + "                                FROM   common.fxrate_hst\n"
            + "                                WHERE  id_currency_from = f.id_currency_from\n"
            + "                                       AND id_currency_into = f.id_currency_into\n"
            + "                                       AND value_date <= ex.value_date)\n"
            + "                AND id_currency_into = :BOOK_CURRENCY\n"
            + "         UNION ALL\n"
            + "         SELECT :BOOK_CURRENCY\n"
            + "                , 1\n"
            + "         FROM   dual)\n"
            + "SELECT /*+parallel*/ scope.id_accounting_scope\n"
            + "       , ex.value_date\n"
            + "       , ex.posting_date\n"
            + "       , a.GL_LEVEL\n"
            + "       , a.code\n"
            + "       , b.description\n"
            + "       , c.balance_bc\n"
            + "FROM   ex\n"
            + "       , scope\n"
            + "         inner join cfe.ledger_branch_branch a\n"
            + "                 ON a.id_accounting_scope = scope.id_accounting_scope\n"
            + "                    AND a.code = a.code_inferior\n"
            + "         inner join cfe.ledger_branch b\n"
            + "                 ON b.id_accounting_scope = scope.id_accounting_scope\n"
            + "                    AND b.code = a.code\n"
            + "         inner join (SELECT b.code\n"
            + "                            , Round(SUM(d.balance * fxr.fxrate), 2) balance_bc\n"
            + "                     FROM   scope\n"
            + "                            inner join cfe.ledger_branch_branch b\n"
            + "                                    ON b.id_accounting_scope = scope.id_accounting_scope\n"
            + "                            inner join cfe.ledger_account c\n"
            + "                                    ON b.code_inferior = c.code\n"
            + "                                       AND c.id_accounting_scope_code = scope.id_accounting_scope_code\n"
            + "                            inner join (SELECT id_account\n"
            + "                                               , SUM(amount) balance\n"
            + "                                        FROM   (SELECT id_account_credit id_account\n"
            + "                                                       , amount\n"
            + "                                                FROM   cfe.ledger_account_entry\n"
            + "                                                       inner join ex\n"
            + "                                                               ON ledger_account_entry.posting_date <= ex.posting_date\n"
            + "                                                UNION ALL\n"
            + "                                                SELECT id_account_debit\n"
            + "                                                       , -amount\n"
            + "                                                FROM   cfe.ledger_account_entry\n"
            + "                                                       inner join ex\n"
            + "                                                               ON ledger_account_entry.posting_date <= ex.posting_date)\n"
            + "                                        GROUP  BY id_account) d\n"
            + "                                    ON c.id_account = d.id_account\n"
            + "                            inner join fxr\n"
            + "                                    ON c.id_currency = fxr.id_currency_from\n"
            + "                     GROUP  BY b.code) c\n"
            + "                 ON c.code = a.code\n"
            + ";  ";

    // sqlStr = "with a as (SELECT 1 FROM b UNION ALL select 1 FROM c) select * from a;";

    try {
      sqlStr = JSQLFormatter.format(sqlStr);
      System.out.println(sqlStr);
    } catch (Exception ex) {
      Logger.getLogger(JSQLFormatter.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

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
    for (Statement st : statements.getStatements()) {
      if (st instanceof Select) {
        Select select = (Select) st;
        appendSelect(select, builder, indent, true);
      } else if (st instanceof Update) {
        int i = 0;

        Update update = (Update) st;

        builder.append("UPDATE ");

        Table table = update.getTable();
        Alias alias = table.getAlias();

        appendFromItem(table, alias, builder, indent, 0);

        i = 0;
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

      } else if (st instanceof Insert) {
        Insert insert = (Insert) st;

        int i = 0;

        builder.append("INSERT INTO ");

        Table table = insert.getTable();
        Alias alias = table.getAlias();

        appendFromItem(table, alias, builder, indent, 0);

        List<Column> columns = insert.getColumns();
        if (columns != null) {
          builder.append("(");
          for (Column column : columns) {
            appendExpression(column, null, builder, indent , i, true, BreakLine.ALWAYS);
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

      } else if (st instanceof Merge) {
        Merge merge = (Merge) st;

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
              appendExpression(
                  expression, null, builder, subIndent, i, true, BreakLine.AFTER_FIRST);
              i++;
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

            appendExpression(
                whereCondition, null, builder, subIndent, 0, false, BreakLine.AFTER_FIRST);
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

      } else {
        throw new UnsupportedOperationException(
            "The " + st.getClass().getName() + " Statement is not supported yet.");
      }

      builder.append("\n;");
    }

    return builder.toString().trim();
  }

  public static void appendSelect(
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

  public static void appendOrderByElements(
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

  public static void appendHavingExpression(
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

  public static void appendGroupByElement(
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

  public static void appendWhere(Expression whereExpression, StringBuilder builder, int indent) {
    int i;
    if (whereExpression != null) {
      i = 0;
      builder.append("\n");
      for (int j = 0; j < indent; j++) builder.append("    ");
      builder.append("WHERE ");

      appendExpression(whereExpression, null, builder, indent, i, false, BreakLine.AFTER_FIRST);
    }
  }

  public static void appendJoins(List<Join> joins, StringBuilder builder, int indent, int i) {
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

      // if (alias != null) builder.append(alias.toString());

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
          alias,
          builder,
          indent + 1,
          i,
          false,
          BreakLine.AFTER_FIRST);
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
            indent + 1,
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
      builder.append(function.toString());

    } else if (expression instanceof IsNullExpression) {
      IsNullExpression isNullExpression = (IsNullExpression) expression;
      builder.append(isNullExpression.toString());
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
      System.out.println(expression.getClass().getName());
    }

    if (alias != null) {
      int length = builder.length();

      if (!builder.substring(length - 1).equalsIgnoreCase(" ")) builder.append(" ");

      if (alias.isUseAs()) builder.append("AS ");

      builder.append(alias.getName());
    }
  }

  public static void appendItemsList(
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
            BreakLine bl =
              i % 3 == 0
                ? BreakLine.AFTER_FIRST
                : BreakLine.NEVER;

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

  public static void appendSubSelect(
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
}
