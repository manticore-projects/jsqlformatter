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

import com.manticore.jsqlformatter.JSQLFormatter.BreakLine;
import com.manticore.jsqlformatter.JSQLFormatter.OutputFormat;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.OutputClause;
import net.sf.jsqlparser.statement.ReferentialAction;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterExpression;
import net.sf.jsqlparser.statement.alter.AlterOperation;
import net.sf.jsqlparser.statement.alter.ConstraintState;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CheckConstraint;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.ExcludeConstraint;
import net.sf.jsqlparser.statement.create.table.ForeignKeyIndex;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.create.table.NamedConstraint;
import net.sf.jsqlparser.statement.create.table.RowMovement;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.create.view.ForceOption;
import net.sf.jsqlparser.statement.create.view.TemporaryOption;
import net.sf.jsqlparser.statement.merge.MergeInsert;
import net.sf.jsqlparser.statement.merge.MergeUpdate;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByElement.NullOrdering;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperation;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.UpdateSet;

import java.util.Collection;
import java.util.List;

/**
 * Hub object that owns the {@link StringBuilder} target and the four AST visitors.
 *
 * <p>
 * This class replaces the procedural inter-method coupling of the original {@code JSQLFormatter} by
 * giving every visitor a single place to find peer visitors, the output buffer, and the shared
 * helpers. Methods that don't naturally belong to a single visitor (such as {@link #appendJoins})
 * live here.
 *
 * <p>
 * One {@code Renderer} is constructed per top-level {@code format()} call and discarded afterwards;
 * it carries no formatter-options state of its own (those remain in {@link JSQLFormatter} statics
 * for compatibility) but does carry the {@link StringBuilder} and therefore is not thread-safe.
 *
 * @author <a href="mailto:andreas@manticore-projects.com">Andreas Reichel</a>
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
final class Renderer {
  final StringBuilder builder;
  final ExpressionFormatter expressionFormatter;
  final SelectFormatter selectFormatter;
  final FromItemFormatter fromItemFormatter;
  final StatementFormatter statementFormatter;

  Renderer(StringBuilder builder) {
    this.builder = builder;
    this.expressionFormatter = new ExpressionFormatter(this);
    this.selectFormatter = new SelectFormatter(this);
    this.fromItemFormatter = new FromItemFormatter(this);
    this.statementFormatter = new StatementFormatter(this);
  }

  // ===================================================================================
  // Smart-fallback rendering for AST nodes we don't render explicitly
  // ===================================================================================

  /**
   * Append an AST node's {@code toString()} output with keyword spelling applied to runs of
   * all-uppercase letters (length ≥ 2). Quoted strings (single, double, back) are passed through
   * verbatim, including doubled-quote escapes. Tokens that contain trailing lowercase letters or
   * digits (e.g. {@code MY_table}, {@code IDX1}) are treated as identifiers and emitted unchanged.
   *
   * <p>
   * This is the smart fallback used by {@code appendUnhandled} in each formatter: it preserves the
   * AST's structure exactly while honouring the configured keyword spelling. Function- and
   * object-spelling are not applied here — telling keywords apart from identifiers heuristically is
   * OK; telling functions apart from regular identifiers is not. Callers that need full spelling
   * fidelity must override the relevant {@code visit} method explicitly.
   *
   * <p>
   * Public/package-private and static so {@link ExpressionFormatter}, {@link StatementFormatter},
   * {@link SelectFormatter} and {@link FromItemFormatter} can all share the same implementation.
   */
  @SuppressWarnings("PMD.CyclomaticComplexity")
  static void appendWithKeywordSpelling(StringBuilder out, Object node) {
    if (node == null) {
      return;
    }
    String text = node.toString();
    OutputFormat fmt = JSQLFormatter.getOutputFormat();
    int n = text.length();
    int i = 0;
    while (i < n) {
      char c = text.charAt(i);

      // Pass-through: quoted string (single/double/back-tick), with doubled-quote escape support
      if (c == '\'' || c == '"' || c == '`') {
        char quote = c;
        out.append(c);
        i++;
        while (i < n) {
          char cc = text.charAt(i);
          out.append(cc);
          i++;
          if (cc == quote) {
            // Doubled quote = escape; stay inside the string.
            if (i < n && text.charAt(i) == quote) {
              out.append(text.charAt(i));
              i++;
            } else {
              break;
            }
          }
        }
        continue;
      }

      // Candidate keyword: a run of [A-Z_]
      if (c >= 'A' && c <= 'Z') {
        int start = i;
        while (i < n) {
          char cc = text.charAt(i);
          if ((cc >= 'A' && cc <= 'Z') || cc == '_') {
            i++;
          } else {
            break;
          }
        }

        // If immediately followed by lowercase letter or digit, it's a mixed-case
        // identifier (e.g. "Trim", "VARCHAR2", "MY_table"). Consume the rest of the
        // identifier and emit unchanged.
        if (i < n) {
          char nxt = text.charAt(i);
          if ((nxt >= 'a' && nxt <= 'z') || (nxt >= '0' && nxt <= '9')) {
            while (i < n) {
              char cc2 = text.charAt(i);
              if ((cc2 >= 'A' && cc2 <= 'Z') || (cc2 >= 'a' && cc2 <= 'z')
                  || (cc2 >= '0' && cc2 <= '9') || cc2 == '_') {
                i++;
              } else {
                break;
              }
            }
            out.append(text, start, i);
            continue;
          }
        }

        String tok = text.substring(start, i);
        if (tok.length() >= 2 && tok.charAt(0) != '_') {
          // Multi-letter all-caps run → treat as keyword.
          JSQLFormatter.appendKeyWord(out, fmt, tok, "", "");
        } else {
          // Single letter or starts with underscore: emit verbatim.
          out.append(tok);
        }
        continue;
      }

      out.append(c);
      i++;
    }
  }

  // ===================================================================================
  // Visitor entry points: shared "wrapping" logic that surrounds the type-dispatched body
  // ===================================================================================

  /**
   * Render an expression: emit the line break / leading comma / dispatch / alias / trailing comma.
   *
   * <p>
   * This wraps the per-type body (handled by {@link ExpressionFormatter}) with the same prefix and
   * suffix logic the legacy {@code appendExpression} method had at top-of-method and at
   * end-of-method. Each visit method only needs to render the expression body.
   */
  @SuppressWarnings("PMD.CyclomaticComplexity")
  void renderExpression(Expression expression, FormatContext ctx) {
    if ((ctx.i > 0 || ctx.breakLine.equals(BreakLine.ALWAYS))
        && !ctx.breakLine.equals(BreakLine.NEVER)) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(Math.max(0, ctx.indent)));
    }

    switch (JSQLFormatter.getSeparation()) {
      case BEFORE:
        builder.append(ctx.commaSeparated && ctx.i > 0 ? ", " : "");
        break;
      default:
        break;
    }

    // Dispatch via the visitor — which calls back into renderExpression for sub-expressions.
    expression.accept(expressionFormatter, ctx);

    JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(), ctx.alias, " ", "");

    switch (JSQLFormatter.getSeparation()) {
      case AFTER:
        JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder,
            ctx.commaSeparated && ctx.i < ctx.n - 1 ? ", " : "");
        break;
      default:
        break;
    }
  }

  /**
   * Render a Select. Mirrors the legacy {@code appendSelect} prelude: handles the WITH clause and
   * dispatches the body to {@link SelectFormatter}.
   *
   * <p>
   * The dispatched ctx is left UNMODIFIED so each branch can decide whether a leading line break is
   * needed by checking {@code ctx.breakLineBefore} (and re-querying {@code
   * select.getWithItemsList()} when relevant — PlainSelect does, Values does not). This matches
   * legacy semantics: only PlainSelect's leading-break check OR'd in the with-items presence.
   */
  void renderSelect(Select select, FormatContext ctx) {
    List<WithItem<?>> withItems = select.getWithItemsList();
    if (withItems != null && !withItems.isEmpty()) {
      int i = 0;
      if (ctx.breakLineBefore) {
        JSQLFormatter.appendNormalizedLineBreak(builder);
        for (int j = 0; ctx.indentFirstLine && j < ctx.indent; j++) {
          builder.append(JSQLFormatter.getIndentString());
        }
      }
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "WITH", "", " ");
      for (WithItem<?> withItem : withItems) {
        appendWithItem(withItem, ctx.indent, i, withItems.size());
        i++;
      }
    }

    select.accept(selectFormatter, ctx);
  }

  /** Render a FromItem. */
  void renderFromItem(FromItem fromItem, FormatContext ctx) {
    if (fromItem == null) {
      return;
    }
    if (ctx.i > 0) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(ctx.indent + 1));
    }
    switch (JSQLFormatter.getSeparation()) {
      case BEFORE:
        builder.append(ctx.i > 0 ? ", " : "");
        break;
      default:
        break;
    }

    fromItem.accept(fromItemFormatter, ctx);

    switch (JSQLFormatter.getSeparation()) {
      case AFTER:
        JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, ctx.i < ctx.n - 1 ? ", " : "");
        break;
      default:
        break;
    }
  }

  /** Render a top-level statement. */
  void renderStatement(Statement statement, FormatContext ctx) {
    statement.accept(statementFormatter, ctx);
  }

  // ===================================================================================
  // Cross-cutting helpers (one-of-each-AST-node-type lists, DDL, etc.)
  // ===================================================================================

  void appendExpressionList(ExpressionList<?> expressionList, FormatContext ctx) {
    int subIndent = ctx.indent;
    if (expressionList instanceof ParenthesedExpressionList) {
      builder.append("( ");
      subIndent++;
    }
    appendExpressionsList(expressionList, ctx.withIndent(subIndent));
    if (expressionList instanceof ParenthesedExpressionList) {
      builder.append(" )");
    }
  }

  @SuppressWarnings("PMD.CyclomaticComplexity")
  void appendExpressionsList(List<? extends Expression> expressions, FormatContext ctx) {
    int size = expressions.size();
    BreakLine breakLine = ctx.breakLine;
    int subIndent =
        breakLine.equals(BreakLine.NEVER) || breakLine.equals(BreakLine.AS_NEEDED) && size <= 3
            || size == 1 ? ctx.indent : JSQLFormatter.getSubIndent(builder, true);

    int i = 0;
    for (Expression expression : expressions) {
      switch (breakLine) {
        case AS_NEEDED:
          BreakLine bl =
              size == 4 || size >= 5 && i % 3 == 0 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
          renderExpression(expression, FormatContext.of(subIndent, i, size, true, bl));
          break;

        default:
          renderExpression(expression, FormatContext.of(subIndent, i, size, true, breakLine));
      }
      i++;
    }
  }

  void appendDecodeExpressionsList(ExpressionList<?> parameters, BreakLine breakLine, int indent) {
    int subIndent =
        breakLine.equals(BreakLine.NEVER) ? indent : JSQLFormatter.getSubIndent(builder, false);

    int i = 0;
    for (Expression expression : parameters) {
      switch (breakLine) {
        case AS_NEEDED:
          BreakLine bl = i == 0 || (i - 1) % 2 == 0 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
          renderExpression(expression, FormatContext.of(subIndent, i, parameters.size(), true, bl));
          break;

        default:
          renderExpression(expression,
              FormatContext.of(subIndent, i, parameters.size(), true, breakLine));
      }
      i++;
    }
  }

  void appendRowConstructor(int indent, RowConstructor<?> rowConstructor) {
    if (rowConstructor.getName() != null) {
      JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(), rowConstructor.getName(),
          "", "");
    }
    appendExpressionList(rowConstructor,
        FormatContext.of(indent, 0, 1, false, BreakLine.AS_NEEDED));
  }

  void appendStringList(Collection<String> strings, int indent, boolean commaSeparated,
      BreakLine breakLine) {
    int i = 0;
    if (strings != null) {
      for (String s : strings) {
        appendString(s, indent, i, strings.size(), commaSeparated, breakLine);
        i++;
      }
    }
  }

  @SuppressWarnings({"PMD.CyclomaticComplexity"})
  void appendString(String s, int indent, int i, int n, boolean commaSeparated,
      BreakLine breakLine) {
    if ((i > 0 || breakLine.equals(BreakLine.ALWAYS)) && !breakLine.equals(BreakLine.NEVER)) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent));
    }

    switch (JSQLFormatter.getSeparation()) {
      case AFTER:
        JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(), s, "", "");
        JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder,
            commaSeparated && i < n - 1 ? ", " : "");
        break;
      case BEFORE:
        builder.append(commaSeparated && i > 0 ? ", " : "");
        JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(), s, "", "");
        break;
      default:
        break;
    }
  }

  void appendSelectItemList(List<SelectItem<?>> selectItems, FormatContext ctx) {
    int j = ctx.i;
    for (SelectItem<?> selectItem : selectItems) {
      Alias alias = selectItem.getAlias();
      Expression expression = selectItem.getExpression();
      renderExpression(expression,
          ctx.atPosition(j++, selectItems.size()).withCommaSeparated(true).withAlias(alias));
    }
  }

  // -------------------- ORDER BY / GROUP BY / WHERE / HAVING / QUALIFY ----------------

  @SuppressWarnings({"PMD.CyclomaticComplexity"})
  void appendOrderByElements(List<OrderByElement> orderByElements, int indent) {
    if (orderByElements == null) {
      return;
    }
    int i = 0;
    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(indent));
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "ORDER BY", "", " ");

    int subIndent = JSQLFormatter.getSubIndent(builder, orderByElements.size() > 1);

    for (OrderByElement orderByElement : orderByElements) {
      Expression expression = orderByElement.getExpression();
      renderExpression(expression,
          FormatContext.of(subIndent, i, orderByElements.size(), true, BreakLine.AFTER_FIRST));

      if (orderByElement.isAscDescPresent()) {
        builder.append(" ");
        if (orderByElement.isAsc()) {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "ASC", "", " ");
        } else {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "DESC", "", " ");
        }
      }

      NullOrdering nullOrdering = orderByElement.getNullOrdering();
      if (NullOrdering.NULLS_FIRST.equals(nullOrdering)) {
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "NULLS FIRST",
            orderByElement.isAscDescPresent() ? "" : " ", " ");
      }
      if (NullOrdering.NULLS_LAST.equals(nullOrdering)) {
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "NULLS LAST",
            orderByElement.isAscDescPresent() ? "" : " ", " ");
      }
      i++;
    }
  }

  void appendHavingExpression(Expression havingExpression, int indent) {
    if (havingExpression != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent));
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "HAVING", "", " ");
      renderExpression(havingExpression,
          FormatContext.of(indent, 0, 1, false, BreakLine.AFTER_FIRST));
    }
  }

  @SuppressWarnings({"PMD.CyclomaticComplexity"})
  void appendGroupByElement(net.sf.jsqlparser.statement.select.GroupByElement groupByElement,
      int indent) {
    int i = 0;
    if (groupByElement == null) {
      return;
    }
    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(indent));
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "GROUP BY", "", " ");

    List<?> groupingSets = groupByElement.getGroupingSets();
    ExpressionList<?> groupByExpressions = groupByElement.getGroupByExpressionList();

    if (groupingSets != null && !groupingSets.isEmpty()) {
      throw new UnsupportedOperationException("Grouping Sets are not supported yet.");
    }

    if (groupByExpressions != null && !groupByExpressions.isEmpty()) {
      BreakLine breakLine = BreakLine.AFTER_FIRST;
      int size = groupByExpressions.size();
      int subIndent = JSQLFormatter.getSubIndent(builder, size > 1);

      for (Expression expression : groupByExpressions) {
        switch (breakLine) {
          case AS_NEEDED:
            BreakLine bl =
                size == 4 || size >= 5 && i % 3 == 0 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
            renderExpression(expression, FormatContext.of(subIndent, i, size, true, bl));
            break;
          default:
            renderExpression(expression, FormatContext.of(subIndent, i, size, true, breakLine));
        }
        i++;
      }
    }
  }

  void appendKeywordedExpression(String keyword, Expression expression, int indent) {
    if (expression != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent));
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), keyword, "", " ");
      renderExpression(expression, FormatContext.of(indent, 0, 1, false, BreakLine.AFTER_FIRST));
    }
  }

  void appendWhere(Expression whereExpression, int indent) {
    if (whereExpression != null) {
      appendKeywordedExpression("WHERE", whereExpression, indent);
    }
  }

  void appendQualify(Expression qualifyExpression, int indent) {
    if (qualifyExpression != null) {
      appendKeywordedExpression("QUALIFY", qualifyExpression, indent);
    }
  }

  // -------------------- JOIN, WITH, TABLE, SET-OP -------------------------------------

  @SuppressWarnings({"PMD.CyclomaticComplexity"})
  void appendJoins(List<Join> joins, int indent) {
    if (joins == null) {
      return;
    }
    for (Join join : joins) {
      if (join.isSimple()) {
        switch (JSQLFormatter.getSeparation()) {
          case AFTER:
            builder.append(",");
            break;
          default:
            break;
        }
      }
      JSQLFormatter.appendNormalizedLineBreak(builder);

      if (join.isSimple()) {
        builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
        switch (JSQLFormatter.getSeparation()) {
          case BEFORE:
            builder.append(", ");
            break;
          default:
            break;
        }
      } else {
        builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
        if (join.isInner()) {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "INNER", "", " ");
        }
        if (join.isLeft()) {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "LEFT", "", " ");
        }
        if (join.isRight()) {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "RIGHT", "", " ");
        }
        if (join.isNatural()) {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "NATURAL", "", " ");
        }
        if (join.isCross()) {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "CROSS", "", " ");
        }
        if (join.isOuter()) {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "OUTER", "", " ");
        }
        if (join.isFull()) {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "FULL", "", " ");
        }
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "JOIN", "", " ");
      }

      FromItem rightFromItem = join.getRightItem();
      renderFromItem(rightFromItem, FormatContext.of(indent, 0, 1, false, BreakLine.NEVER));

      for (Expression onExpression : join.getOnExpressions()) {
        if (onExpression != null) {
          JSQLFormatter.appendNormalizedLineBreak(builder);
          builder.append(JSQLFormatter.getIndentString().repeat(indent + 2));
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "ON", "", " ");
          renderExpression(onExpression,
              FormatContext.of(indent + 2, 0, 1, false, BreakLine.AFTER_FIRST));
        }
      }

      List<Column> usingColumns = join.getUsingColumns();
      if (usingColumns != null && !usingColumns.isEmpty()) {
        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(indent + 3));
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "USING", "", " ( ");
        int k = 0;
        for (Column column : usingColumns) {
          renderExpression(column,
              FormatContext.of(indent + 3, k, usingColumns.size(), true, BreakLine.AFTER_FIRST));
          k++;
        }
        JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, " )");
      }
    }
  }

  @SuppressWarnings({"PMD.CyclomaticComplexity"})
  void appendWithItem(WithItem<?> withItem, int indent, int i, int n) {
    if (i > 0) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
    }

    switch (JSQLFormatter.getSeparation()) {
      case BEFORE:
        JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(),
            withItem.getAlias().getName(), i > 0 ? ", " : "", " ");
        break;
      default:
        JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(),
            withItem.getAlias().getName(), "", " ");
        break;
    }

    List<SelectItem<?>> selectItems = withItem.getWithItemList();
    if (selectItems != null && !selectItems.isEmpty()) {
      builder.append("( ");
      int subIndent = JSQLFormatter.getSubIndent(builder, selectItems.size() > 2);
      BreakLine bl = selectItems.size() > 2 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
      appendSelectItemList(selectItems,
          FormatContext.of(subIndent, i, 0, false, bl).withIndent(subIndent));
      builder.append(" ) ");

      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "AS", "", " ");
      renderSelect(withItem.getSelect(),
          FormatContext.of(indent + 1).withSelectFraming(false, false));
    } else {
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "AS", "", " ");
      renderSelect(withItem.getSelect(),
          FormatContext.of(indent + 1).withSelectFraming(true, true));
    }

    switch (JSQLFormatter.getSeparation()) {
      case AFTER:
        JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, i < n - 1 ? "," : "");
        break;
      case BEFORE:
        JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, " ");
        break;
      default:
        break;
    }
  }

  void appendTable(Table table, Alias alias) {
    if (table != null) {
      JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(),
          table.getFullyQualifiedName(), "", "");
      if (alias != null) {
        JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, " ");
        if (alias.isUseAs()) {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "AS", "", " ");
        }
        JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(), alias.getName(), "",
            " ");
      }
    }
  }

  void appendSetOperation(SetOperation setOperation, int indent) {
    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(indent));
    JSQLFormatter.appendOperator(builder, JSQLFormatter.getOutputFormat(), setOperation.toString(),
        "", " ");
  }

  void appendTruncate(Truncate truncate) {
    Table table = truncate.getTable();
    boolean cascade = truncate.getCascade();
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "TRUNCATE TABLE", "", " ")
        .append(table.getFullyQualifiedName());
    if (cascade) {
      JSQLFormatter.appendOperator(builder, JSQLFormatter.getOutputFormat(), "CASCADE", " ", "");
    }
  }

  void appendUpdateSets(List<UpdateSet> updateSets, int subIndent) {
    int i = 0;
    int n = updateSets.size();
    for (UpdateSet updateSet : updateSets) {
      if (i > 0) {
        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(subIndent));
      }
      switch (JSQLFormatter.getSeparation()) {
        case BEFORE:
          builder.append(i > 0 ? ", " : "");
          break;
        default:
          break;
      }
      appendExpressionList(updateSet.getColumns(),
          FormatContext.of(subIndent).withBreakLine(BreakLine.AFTER_FIRST));
      JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, " = ");
      appendExpressionList(updateSet.getValues(),
          FormatContext.of(subIndent).withBreakLine(BreakLine.AFTER_FIRST));

      switch (JSQLFormatter.getSeparation()) {
        case AFTER:
          JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, i < n - 1 ? ", " : "");
          break;
        default:
          break;
      }
      i++;
    }
  }

  // -------------------- MERGE family --------------------------------------------------

  void appendOutputClause(OutputClause outputClause, int indent) {
    if (outputClause == null) {
      return;
    }
    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(Math.max(0, indent)));
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "OUTPUT", "", " ");
    int subIndent =
        JSQLFormatter.getSubIndent(builder, outputClause.getSelectItemList().size() > 3);
    appendSelectItemList(outputClause.getSelectItemList(),
        FormatContext.of(subIndent, 0, 0, false, BreakLine.AS_NEEDED));

    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(Math.max(0, indent + 1)));
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "INTO", "", " ");

    if (outputClause.getOutputTable() != null) {
      Table table = outputClause.getOutputTable();
      appendTable(table, table.getAlias());
      appendStringList(outputClause.getColumnList(), indent + 1, true, BreakLine.AS_NEEDED);
    } else if (outputClause.getTableVariable() != null) {
      JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(),
          outputClause.getTableVariable().toString(), " ", "");
    }
  }

  void appendMergeUpdate(MergeUpdate update, int indent) {
    if (update == null) {
      return;
    }
    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(indent));
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "WHEN", "", " ");
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "MATCHED", "", " ");
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "THEN", "", "\n");

    builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "UPDATE", "", " ");
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "SET", "", " ");

    int subIndent = JSQLFormatter.getSubIndent(builder, true);
    appendUpdateSets(update.getUpdateSets(), subIndent);

    Expression whereCondition = update.getWhereCondition();
    if (whereCondition != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "WHERE", "", " ");
      subIndent = JSQLFormatter.getSubIndent(builder, true);
      renderExpression(whereCondition,
          FormatContext.of(subIndent, 0, 1, false, BreakLine.AFTER_FIRST));
    }

    Expression deleteWhereCondition = update.getDeleteWhereCondition();
    if (deleteWhereCondition != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent + 1; j++) {
        builder.append(JSQLFormatter.getIndentString());
      }
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "DELETE", "", " ");
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "WHERE", "", " ");
      subIndent = JSQLFormatter.getSubIndent(builder, true);
      renderExpression(deleteWhereCondition,
          FormatContext.of(subIndent, 0, 1, false, BreakLine.AFTER_FIRST));
    }
  }

  @SuppressWarnings({"PMD.CyclomaticComplexity"})
  void appendMergeInsert(MergeInsert insert, int indent, int i) {
    if (insert == null) {
      return;
    }
    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(indent));
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "WHEN", "", " ");
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "NOT", "", " ");
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "MATCHED", "", " ");
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "THEN", "", "\n");

    builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "INSERT", "", " ");

    List<Column> columns = insert.getColumns();
    List<Expression> expressions = insert.getValues();

    int k = i;
    if (columns != null && !columns.isEmpty()) {
      builder.append("( ");
      int subIndent = JSQLFormatter.getSubIndent(builder, false);
      for (Column column : columns) {
        renderExpression(column,
            FormatContext.of(subIndent, k++, columns.size(), true, BreakLine.AFTER_FIRST));
      }
      JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, " )\n");
    }

    if (columns != null && !columns.isEmpty()) {
      builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
    }
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "VALUES", "", " ( ");

    int subIndent = JSQLFormatter.getSubIndent(builder, false);
    if (expressions != null) {
      int j = 0;
      for (Expression expression : expressions) {
        renderExpression(expression,
            FormatContext.of(subIndent, j++, expressions.size(), true, BreakLine.AFTER_FIRST));
      }
    }
    JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, " )");

    Expression whereCondition = insert.getWhereCondition();
    if (whereCondition != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "WHERE", "", " ");
      subIndent = JSQLFormatter.getSubIndent(builder, true);
      renderExpression(whereCondition,
          FormatContext.of(subIndent, 0, 1, false, BreakLine.AFTER_FIRST));
    }
  }

  // ===================================================================================
  // DDL: CREATE TABLE / CREATE INDEX / CREATE VIEW / ALTER
  // (called from StatementFormatter.visit; left here as private DDL helpers because the
  // inner AST nodes — Index, AlterExpression, ColumnDefinition — do not have JSqlParser
  // visitor interfaces that warrant another visitor layer)
  // ===================================================================================

  @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength"})
  void appendCreateTable(CreateTable createTable, int indent) {
    int i = 0;
    JSQLFormatter.appendNormalizedLineBreak(builder);

    List<String> createOptionsString = createTable.getCreateOptionsStrings();
    String createOps = createOptionsString != null && !createOptionsString.isEmpty()
        ? PlainSelect.getStringList(createOptionsString, false, false)
        : null;

    boolean unlogged = createTable.isUnlogged();
    boolean ifNotExists = createTable.isIfNotExists();
    Table table = createTable.getTable();

    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "CREATE", "", " ");

    if (unlogged) {
      JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), "UNLOGGED", "", " ");
    }
    if (createOps != null) {
      JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), createOps, "", " ");
    }
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "TABLE", "", " ");
    if (ifNotExists) {
      JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), "IF NOT EXISTS", "", " ");
    }
    JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(),
        table.getFullyQualifiedName(), "", "");

    List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
    List<Index> indexes = createTable.getIndexes();

    if (columnDefinitions != null && !columnDefinitions.isEmpty()) {
      builder.append(" (");

      int colWidth = 0;
      int typeWidth = 0;
      for (ColumnDefinition columnDefinition : columnDefinitions) {
        String columnName = columnDefinition.getColumnName();
        String colDataType = columnDefinition.getColDataType().toString().replace(", ", ",");
        if (colWidth < columnName.length()) {
          colWidth = columnName.length();
        }
        if (typeWidth < colDataType.length()) {
          typeWidth = colDataType.length();
        }
      }

      int typeIndex = indent + (colWidth / JSQLFormatter.getIndentString().length()) + 3;
      int specIndex =
          indent + typeIndex + (typeWidth / JSQLFormatter.getIndentString().length()) + 1;

      for (ColumnDefinition columnDefinition : columnDefinitions) {
        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));

        String columnName = columnDefinition.getColumnName();
        ColDataType colDataType = columnDefinition.getColDataType();
        List<String> columnSpecs = columnDefinition.getColumnSpecs();

        switch (JSQLFormatter.getSeparation()) {
          case BEFORE:
            builder.append(i > 0 ? ", " : "");
            break;
          default:
            break;
        }

        JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(), columnName, "",
            "");

        int lastLineLength = JSQLFormatter.getLastLineLength(builder);
        builder.append(" "
            .repeat(Math.max(0, typeIndex * JSQLFormatter.getIndentWidth() - lastLineLength + 1)));
        // @todo: please get rid of that Replace workaround
        JSQLFormatter.appendType(builder, JSQLFormatter.getOutputFormat(),
            colDataType.toString().replace(", ", ","), "", "");

        lastLineLength = JSQLFormatter.getLastLineLength(builder);

        if (columnSpecs != null && !columnSpecs.isEmpty()) {
          builder.append(" ".repeat(
              Math.max(0, specIndex * JSQLFormatter.getIndentWidth() - lastLineLength + 1)));
          JSQLFormatter.appendType(builder, JSQLFormatter.getOutputFormat(),
              PlainSelect.getStringList(columnSpecs, false, false), "", "");
        }

        switch (JSQLFormatter.getSeparation()) {
          case AFTER:
            JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder,
                i < columnDefinitions.size() + indexes.size() - 1 ? ", " : "");
            break;
          default:
            break;
        }
        i++;
      }

      if (indexes != null && !indexes.isEmpty()) {
        for (Index index : indexes) {
          JSQLFormatter.appendNormalizedLineBreak(builder);
          builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));

          switch (JSQLFormatter.getSeparation()) {
            case BEFORE:
              builder.append(i > 0 ? ", " : "");
              break;
            default:
              break;
          }

          appendCreateTableIndex(index, indent);

          switch (JSQLFormatter.getSeparation()) {
            case AFTER:
              JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder,
                  i < columnDefinitions.size() + indexes.size() - 1 ? ", " : "");
              break;
            default:
              break;
          }
          i++;
        }
      }
      JSQLFormatter.appendNormalizedLineBreak(builder).append(")");
    }

    List<String> tableOptionsStrings = createTable.getTableOptionsStrings();
    String options = PlainSelect.getStringList(tableOptionsStrings, false, false);
    if (!options.isEmpty()) {
      JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), options, " ", "");
    }

    RowMovement rowMovement = createTable.getRowMovement();
    if (rowMovement != null) {
      builder.append(" ").append(rowMovement.getMode()).append(" ROW MOVEMENT");
    }

    Select select = createTable.getSelect();
    if (select != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
      builder.append("AS ");
      renderSelect(select, FormatContext.of(indent).withSelectFraming(false, false));
    }

    Table likeTable = createTable.getLikeTable();
    if (likeTable != null) {
      builder.append(" AS ");
      Alias alias = likeTable.getAlias();
      appendTable(likeTable, alias);
    }
  }

  @SuppressWarnings({"PMD.CyclomaticComplexity"})
  private void appendCreateTableIndex(Index index, int indent) {
    if (index instanceof ForeignKeyIndex) {
      ForeignKeyIndex foreignKeyIndex = (ForeignKeyIndex) index;
      String type = foreignKeyIndex.getType();
      String name = foreignKeyIndex.getName();
      List<String> columnsNames = foreignKeyIndex.getColumnsNames();
      List<String> idxSpec = foreignKeyIndex.getIndexSpec();
      String idxSpecText = PlainSelect.getStringList(idxSpec, false, false);

      if (name != null) {
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "CONSTRAINT", "",
            " ");
        JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(), name, "", "");
        JSQLFormatter.appendNormalizedLineBreak(builder);
      }
      for (int j = 0; name != null && j <= indent + 1; j++) {
        builder.append(JSQLFormatter.getIndentString());
      }
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), type, "", " ");
      builder.append("( ");
      int subIndent = JSQLFormatter.getSubIndent(builder, columnsNames.size() > 2);
      BreakLine bl = columnsNames.size() > 2 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
      appendStringList(columnsNames, subIndent, true, bl);
      builder.append(" )");
      if (idxSpec != null && !idxSpecText.isEmpty()) {
        JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), idxSpecText, " ", "");
      }

      Table foreignTable = foreignKeyIndex.getTable();
      List<String> referencedColumnNames = foreignKeyIndex.getReferencedColumnNames();
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent + 2));
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "REFERENCES", "", " ");
      JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(),
          foreignTable.getFullyQualifiedName(), "", " ");

      builder.append("( ");
      subIndent = JSQLFormatter.getSubIndent(builder, referencedColumnNames.size() > 2);
      bl = referencedColumnNames.size() > 2 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
      appendStringList(referencedColumnNames, subIndent, true, bl);
      builder.append(" )");

      ReferentialAction updateAction =
          foreignKeyIndex.getReferentialAction(ReferentialAction.Type.UPDATE);
      if (updateAction != null) {
        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(indent + 3));
        builder.append(updateAction);
      }
      ReferentialAction deleteAction =
          foreignKeyIndex.getReferentialAction(ReferentialAction.Type.DELETE);
      if (deleteAction != null) {
        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(indent + 3));
        builder.append(deleteAction);
      }

    } else if (index instanceof CheckConstraint) {
      CheckConstraint checkConstraint = (CheckConstraint) index;
      String contraintName = checkConstraint.getName();
      Expression expression = checkConstraint.getExpression();
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "CONSTRAINT", "", " ");
      JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(), contraintName, "", "");
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent + 2));
      builder.append(" CHECK (").append(expression).append(")");

    } else if (index instanceof NamedConstraint) {
      NamedConstraint namedConstraint = (NamedConstraint) index;
      String type = namedConstraint.getType();
      String name = namedConstraint.getName();
      List<String> columnsNames = namedConstraint.getColumnsNames();
      List<String> idxSpec = namedConstraint.getIndexSpec();
      String idxSpecText = PlainSelect.getStringList(idxSpec, false, false);

      if (name != null) {
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "CONSTRAINT", "",
            " ");
        JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(), name, "", "");
        JSQLFormatter.appendNormalizedLineBreak(builder);
      }
      for (int j = 0; name != null && j <= indent + 1; j++) {
        builder.append(JSQLFormatter.getIndentString());
      }
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), type, "", " ");
      builder.append("( ");
      int subIndent = JSQLFormatter.getSubIndent(builder, columnsNames.size() > 2);
      BreakLine bl = columnsNames.size() > 2 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
      appendStringList(columnsNames, subIndent, true, bl);
      builder.append(" )");
      if (idxSpec != null && !idxSpecText.isEmpty()) {
        JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), idxSpecText, " ", "");
      }

    } else if (index instanceof ExcludeConstraint) {
      ExcludeConstraint excludeConstraint = (ExcludeConstraint) index;
      Expression expression = excludeConstraint.getExpression();
      builder.append("EXCLUDE WHERE ");
      builder.append("(");
      builder.append(expression);
      builder.append(")");

    } else if (index != null) {
      String type = index.getType();
      String name = index.getName();
      List<Index.ColumnParams> columnParams = index.getColumns();
      List<String> idxSpec = index.getIndexSpec();
      String idxSpecText = PlainSelect.getStringList(idxSpec, false, false);

      builder.append(type);
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), type, "", " ");
      if (name != null) {
        JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(), name, "", "");
      }
      builder.append(" ").append(PlainSelect.getStringList(columnParams, true, true))
          .append(!idxSpecText.isEmpty() ? " " + idxSpecText : "");
    }
  }

  @SuppressWarnings({"PMD.CyclomaticComplexity"})
  void appendCreateIndex(CreateIndex createIndex, int indent) {
    Index index = createIndex.getIndex();
    Table table = createIndex.getTable();

    List<String> tailParameters = createIndex.getTailParameters();
    List<Index.ColumnParams> columnsParameters = index.getColumns();

    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "CREATE", "", " ");

    if (index.getType() != null) {
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), index.getType(), "",
          " ");
    }

    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "INDEX", "", " ");
    if (createIndex.isUsingIfNotExists()) {
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "IF NOT EXISTS", "",
          " ");
    }
    JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(), index.getName(), "", "");

    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "ON", "", " ");
    JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(),
        table.getFullyQualifiedName(), "", "");

    if (index.getUsing() != null) {
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "USING", "  ", " ");
      builder.append(index.getUsing());
    }

    if (index.getColumnsNames() != null) {
      builder.append("( ");
      int subIndent = JSQLFormatter.getSubIndent(builder, columnsParameters.size() > 2);
      BreakLine bl = columnsParameters.size() > 2 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;

      int i = 0;
      for (Index.ColumnParams param : columnsParameters) {
        appendString(param.getColumnName(), subIndent, i, columnsParameters.size(), true, bl);
        i++;
      }
      builder.append(" )");

      if (tailParameters != null) {
        builder.append(" ");
        for (String param : tailParameters) {
          JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), param, "", " ");
        }
      }
    }
  }

  void appendCreateView(CreateView createView, int indent) {
    boolean isOrReplace = createView.isOrReplace();
    ForceOption force = createView.getForce();
    TemporaryOption temp = createView.getTemporary();
    boolean isMaterialized = createView.isMaterialized();
    Table view = createView.getView();
    ExpressionList<Column> columnNames = createView.getColumnNames();
    Select select = createView.getSelect();
    boolean isWithReadOnly = createView.isWithReadOnly();

    JSQLFormatter.appendNormalizedLineBreak(builder);

    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "CREATE", "", " ");
    if (isOrReplace) {
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "OR REPLACE", "", " ");
    }
    switch (force) {
      case FORCE:
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "FORCE", "", " ");
        break;
      case NO_FORCE:
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "NO FORCE", "", " ");
        break;
      default:
        break;
    }
    if (temp != TemporaryOption.NONE) {
      builder.append(temp.name()).append(" ");
    }
    if (isMaterialized) {
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "MATERIALIZED", "",
          " ");
    }
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "VIEW", "", " ");
    JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(),
        view.getFullyQualifiedName(), "", "");
    if (columnNames != null) {
      builder.append(PlainSelect.getStringList(columnNames, true, true));
    }

    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "AS", "", " ");
    renderSelect(select, FormatContext.of(indent + 2).withSelectFraming(false, false));

    if (isWithReadOnly) {
      builder.append(" WITH READ ONLY");
      JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), "WITH READ ONLY", " ", "");
    }
  }

  @SuppressWarnings({"PMD.NcssCount"})
  void appendAlter(Alter alter, int indent) {
    boolean useOnly = alter.isUseOnly();
    Table table = alter.getTable();
    List<AlterExpression> alterExpressions = alter.getAlterExpressions();

    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "ALTER TABLE", "", " ");
    if (useOnly) {
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "ONLY", "", " ");
    }
    JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(),
        table.getFullyQualifiedName(), "", "");
    int i = 0;

    if (alterExpressions == null) {
      return;
    }
    for (AlterExpression alterExpression : alterExpressions) {
      if (i > 0) {
        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
      }
      switch (JSQLFormatter.getSeparation()) {
        case BEFORE:
          builder.append(i > 0 ? ", " : "");
          break;
        default:
          break;
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
      boolean constraintIfExists = alterExpression.isUsingIfExists();

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

      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));

      if (operation == AlterOperation.RENAME_TABLE) {
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "RENAME TO", "", " ");
        JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(),
            alterExpression.getNewTableName(), "", "");
        break;
      } else {
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), operation.name(), "",
            " ");
      }

      if (commentText != null) {
        if (columnName != null) {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "COMMENT", " ",
              " ");
        }
        builder.append(commentText);
      } else if (columnName != null) {
        if (alterExpression.hasColumn()) {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "COLUMN", "", " ");
        }
        if (operation == AlterOperation.RENAME) {
          JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(), columnOldName,
              "", "");
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "TO", " ", " ");
        }
        JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(), columnName, "",
            "");

      } else if (operation == AlterOperation.DROP && !alterExpression.hasColumn()
          && alterExpression.getPkColumns() != null) {
        List<String> columns = alterExpression.getPkColumns();
        builder.append("(");
        int subIndent = JSQLFormatter.getSubIndent(builder, columns.size() > 3);
        BreakLine bl = columns.size() > 3 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;
        appendStringList(alterExpression.getPkColumns(), subIndent, true, bl);
        builder.append(" )");

      } else if (colDataTypeList != null) {
        appendAlterColumnDataTypeList(alterExpression, colDataTypeList, optionalSpecifier,
            columnOldName, operation, indent);

      } else if (columnDropNotNullList != null) {
        if (operation == AlterOperation.CHANGE) {
          if (optionalSpecifier != null) {
            builder.append(optionalSpecifier).append(" ");
          }
          JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(), columnOldName,
              "", " ");
        } else if (columnDropNotNullList.size() > 1) {
          builder.append("(");
        } else {
          if (alterExpression.hasColumn()) {
            JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "COLUMN", "",
                " ");
          }
        }
        builder.append(PlainSelect.getStringList(columnDropNotNullList));
        if (columnDropNotNullList.size() > 1) {
          builder.append(")");
        }
      } else if (constraintName != null) {
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "CONSTRAINT", "",
            " ");
        if (constraintIfExists) {
          JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "IF EXISTS", "",
              " ");
        }
        JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(), constraintName, "",
            "");
      } else if (pkColumns != null) {
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "PRIMARY KEY", "",
            " (");
        builder.append(PlainSelect.getStringList(pkColumns)).append(")");
      } else if (ukColumns != null) {
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "UNIQUE", "", "");
        if (ukName != null) {
          if (uk) {
            JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "KEY", " ", " ");
          } else {
            JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "INDEX", " ",
                " ");
          }
          JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(), ukName, "", "");
        }
        builder.append(" (").append(PlainSelect.getStringList(ukColumns)).append(")");
      } else if (fkColumns != null) {
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "FOREIGN KEY", "",
            " (");
        builder.append(PlainSelect.getStringList(fkColumns)).append(")");

        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(indent + 2));
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "REFERENCES", "",
            " ");

        builder.append(fkSourceTable).append(" (")
            .append(PlainSelect.getStringList(fkSourceColumns)).append(")");
        if (updateAction != null) {
          builder.append(updateAction);
        }
        if (deleteAction != null) {
          builder.append(deleteAction);
        }
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

      switch (JSQLFormatter.getSeparation()) {
        case AFTER:
          JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder,
              i < alterExpressions.size() - 1 ? ", " : "");
          break;
        default:
          break;
      }
      i++;
    }
  }

  @SuppressWarnings({"PMD.CyclomaticComplexity"})
  private void appendAlterColumnDataTypeList(AlterExpression alterExpression,
      List<AlterExpression.ColumnDataType> colDataTypeList, String optionalSpecifier,
      String columnOldName, AlterOperation operation, int indent) {

    int colWidth = 0;
    int typeWidth = 0;
    BreakLine breakLine = colDataTypeList.size() > 1 ? BreakLine.AFTER_FIRST : BreakLine.NEVER;

    if (operation == AlterOperation.CHANGE) {
      if (optionalSpecifier != null) {
        builder.append(optionalSpecifier).append(" ");
      }
      JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(), columnOldName, "",
          " ");
    } else if (colDataTypeList.size() > 1) {
      for (ColumnDefinition columnDefinition : colDataTypeList) {
        String columnName1 = columnDefinition.getColumnName();
        String colDataType = columnDefinition.getColDataType().toString().replace(", ", ",");
        if (colWidth < columnName1.length()) {
          colWidth = columnName1.length();
        }
        if (typeWidth < colDataType.length()) {
          typeWidth = colDataType.length();
        }
      }
      builder.append("( ");
    } else {
      if (alterExpression.hasColumn()) {
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "COLUMN", "", " ");
      }
    }

    int subIndent = JSQLFormatter.getSubIndent(builder, colDataTypeList.size() > 1);
    int typeIndex = subIndent + (colWidth / JSQLFormatter.getIndentString().length()) + 1;
    int specIndex = indent + typeIndex + (typeWidth / JSQLFormatter.getIndentString().length()) + 1;

    int i = 0;
    for (ColumnDefinition columnDefinition : colDataTypeList) {
      if (i > 0 || breakLine.equals(BreakLine.ALWAYS)) {
        if (!breakLine.equals(BreakLine.NEVER)) {
          JSQLFormatter.appendNormalizedLineBreak(builder);
          builder.append(JSQLFormatter.getIndentString().repeat(subIndent));
        }
        builder.append(", ");
      }
      String columnName1 = columnDefinition.getColumnName();
      ColDataType colDataType = columnDefinition.getColDataType();
      List<String> columnSpecs = columnDefinition.getColumnSpecs();

      JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(), columnName1, "",
          " ");
      int lastLineLength = JSQLFormatter.getLastLineLength(builder);
      builder.append(
          " ".repeat(Math.max(0, typeIndex * JSQLFormatter.getIndentWidth() - lastLineLength + 1)));
      // @todo: please get rid of that Replace workaround
      JSQLFormatter.appendType(builder, JSQLFormatter.getOutputFormat(),
          colDataType.toString().replace(", ", ","), "", "");

      lastLineLength = JSQLFormatter.getLastLineLength(builder);

      if (columnSpecs != null && !columnSpecs.isEmpty()) {
        if (colDataTypeList.size() > 1) {
          builder.append(" ".repeat(
              Math.max(0, specIndex * JSQLFormatter.getIndentWidth() - lastLineLength + 1)));
        } else {
          builder.append(" ");
        }
        JSQLFormatter.appendType(builder, JSQLFormatter.getOutputFormat(),
            PlainSelect.getStringList(columnSpecs, false, false), "", "");
      }
      i++;
    }
    if (colDataTypeList.size() > 1) {
      builder.append(")");
    }
  }
}
