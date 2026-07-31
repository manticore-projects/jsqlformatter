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

import com.manticore.jsqlformatter.JSQLFormatter.BreakLine;
import com.manticore.jsqlformatter.JSQLFormatter.OutputFormat;
import net.sf.jsqlparser.expression.AllValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.WindowDefinition;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.Fetch;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.Offset;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;
import net.sf.jsqlparser.statement.select.SetOperation;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.Top;
import net.sf.jsqlparser.statement.select.Values;
import net.sf.jsqlparser.statement.select.WithItem;

import java.util.List;

/**
 * SelectVisitor that dispatches to the appropriate render method per Select sub-type.
 *
 * <p>
 * Replaces the legacy if/instanceof chain in {@code appendSelect()}. Each visit method mirrors one
 * branch of that chain — output is identical to the procedural version. The WITH-clause prelude is
 * handled by {@link Renderer#renderSelect} before dispatch; visit methods only handle the body that
 * begins with SELECT/VALUES/(/UNION etc.
 *
 * @author <a href="mailto:andreas@manticore-projects.com">Andreas Reichel</a>
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength"})
final class SelectFormatter extends SelectVisitorAdapter<Void> {

  private final Renderer renderer;
  private final StringBuilder builder;

  SelectFormatter(Renderer renderer) {
    this.renderer = renderer;
    this.builder = renderer.builder;
  }

  @Override
  @SuppressWarnings("PMD.NcssCount")
  public <S> Void visit(PlainSelect plainSelect, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();
    int indent = ctx.indent;

    int i = 0;
    // Leading line break: needed when caller asked for breakLineBefore OR when a WITH prelude
    // was just emitted (legacy: breakLineBefore || withItems != null && !withItems.isEmpty()).
    List<WithItem<?>> withItems = plainSelect.getWithItemsList();
    boolean withItemsPresent = withItems != null && !withItems.isEmpty();
    if (ctx.breakLineBefore || withItemsPresent) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      for (int j = 0; ctx.indentFirstLine && j < indent; j++) {
        builder.append(JSQLFormatter.getIndentString());
      }
    }

    JSQLFormatter.appendKeyWord(builder, fmt, "SELECT", "", " ");

    OracleHint oracleHint = plainSelect.getOracleHint();
    if (oracleHint != null) {
      JSQLFormatter.appendHint(builder, fmt, oracleHint.toString(), "", " ");
    }

    Top top = plainSelect.getTop();
    if (top != null) {
      JSQLFormatter.appendKeyWord(builder, fmt, "TOP", "", top.hasParenthesis() ? "( " : " ");
      renderer.renderExpression(top.getExpression(),
          FormatContext.of(0, 0, 0, false, BreakLine.AS_NEEDED));
      if (top.hasParenthesis()) {
        builder.append(" )");
      }
      JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, " ");

      if (top.isPercentage()) {
        JSQLFormatter.appendKeyWord(builder, fmt, "PERCENT", "", " ");
      }

      if (top.isWithTies()) {
        JSQLFormatter.appendKeyWord(builder, fmt, "WITH TIES", "", " ");
      }
    }

    Distinct distinct = plainSelect.getDistinct();
    if (distinct != null) {
      if (distinct.isUseUnique()) {
        throw new UnsupportedOperationException("Unique DISTINCT not supported yet.");
      }

      if (distinct.getOnSelectItems() != null && !distinct.getOnSelectItems().isEmpty()) {
        throw new UnsupportedOperationException("DISTINCT on select items are not supported yet.");
      }
      JSQLFormatter.appendKeyWord(builder, fmt, "DISTINCT", "", " ");
    }

    int subIndent = oracleHint != null || distinct != null || top != null ? indent + 1
        : JSQLFormatter.getSubIndent(builder, plainSelect.getSelectItems().size() > 1);
    BreakLine bl = oracleHint != null || distinct != null || top != null ? BreakLine.ALWAYS
        : BreakLine.AFTER_FIRST;

    List<SelectItem<?>> selectItems = plainSelect.getSelectItems();
    JSQLFormatter.appendSelectItemList(selectItems, builder, subIndent, i, bl, indent);

    FromItem fromItem = plainSelect.getFromItem();
    if (fromItem != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) {
        builder.append(JSQLFormatter.getIndentString());
      }
      JSQLFormatter.appendKeyWord(builder, fmt, "FROM", "", " ");
      renderer.renderFromItem(fromItem, FormatContext.of(indent, i, 1, false, BreakLine.NEVER));

      List<Join> joins = plainSelect.getJoins();
      renderer.appendJoins(joins, indent);
    }

    renderer.appendWhere(plainSelect.getWhere(), indent);

    renderer.appendGroupByElement(plainSelect.getGroupBy(), indent);

    renderer.appendHavingExpression(plainSelect.getHaving(), indent);

    renderer.appendQualify(plainSelect.getQualify(), indent);

    // @todo: write-out Windows
    if (plainSelect.getWindowDefinitions() != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) {
        builder.append(JSQLFormatter.getIndentString());
      }
      builder.append("WINDOW ");
      int k = 0;
      for (WindowDefinition w : plainSelect.getWindowDefinitions()) {
        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(indent + 1));
        if (k++ > 0) {
          builder.append(", ");
        }
        if (w.getWindowName() != null) {
          JSQLFormatter.appendObjectName(builder, fmt, w.getWindowName(), "", " ");
          JSQLFormatter.appendKeyWord(builder, fmt, "AS", "", " (");
        } else {
          builder.append("(");
        }

        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(indent + 2));
        w.getPartitionBy().toStringPartitionBy(builder);

        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(indent + 2));
        w.getOrderBy().toStringOrderByElements(builder);

        if (w.getWindowElement() != null) {
          if (w.getOrderBy().getOrderByElements() != null) {
            JSQLFormatter.appendNormalizedLineBreak(builder);
            builder.append(JSQLFormatter.getIndentString().repeat(indent + 2));
          }
          JSQLFormatter.appendKeyWord(builder, fmt, w.getWindowElement().toString(), "", "");
        }

        builder.append(" )");
      }
    }

    // @todo: write-out FOR MODE
    if (plainSelect.getForMode() != null) {
      builder.append(" FOR ");
      builder.append(plainSelect.getForMode().getValue());

      if (plainSelect.getForUpdateTable() != null) {
        builder.append(" OF ").append(plainSelect.getForUpdateTable());
      }
      if (plainSelect.getWait() != null) {
        // wait's toString will do the formatting for us
        builder.append(plainSelect.getWait());
      }
      if (plainSelect.isNoWait()) {
        builder.append(" NOWAIT");
      } else if (plainSelect.isSkipLocked()) {
        builder.append(" SKIP LOCKED");
      }
    }

    if (plainSelect.getPivot() != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent));
      builder.append(plainSelect.getPivot());
    }

    if (plainSelect.getUnPivot() != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent));
      builder.append(plainSelect.getUnPivot());
    }

    // @todo: write-out FOR CLAUSE
    if (plainSelect.getForClause() != null) {
      plainSelect.getForClause().appendTo(builder);
    }

    // @todo: write-out EMIT CHANGES
    if (plainSelect.isEmitChanges()) {
      builder.append(" EMIT CHANGES");
    }

    // @todo: write-out LIMIT BY
    if (plainSelect.getLimitBy() != null) {
      builder.append(plainSelect.getLimitBy());
    }

    List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
    renderer.appendOrderByElements(orderByElements, indent);

    Limit limit = plainSelect.getLimit();
    if (limit != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent));
      JSQLFormatter.appendKeyWord(builder, fmt, "LIMIT", "", " ");

      Expression rowCount = limit.getRowCount();
      if (rowCount instanceof AllValue || rowCount instanceof NullValue) {
        // no offset allowed
        JSQLFormatter.appendKeyWord(builder, fmt, "NULL", "", " ");
      } else {
        if (null != limit.getOffset()) {
          renderer.renderExpression(limit.getOffset(),
              FormatContext.of(indent, 0, 1, false, BreakLine.NEVER));
          builder.append(", ");
        }
        if (null != limit.getRowCount()) {
          renderer.renderExpression(limit.getRowCount(),
              FormatContext.of(indent, 0, 1, false, BreakLine.NEVER));
        }
      }
    }

    Offset offset = plainSelect.getOffset();
    if (offset != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent));
      JSQLFormatter.appendKeyWord(builder, fmt, "OFFSET", "", " ");

      Expression offsetExpression = offset.getOffset();
      renderer.renderExpression(offsetExpression,
          FormatContext.of(indent, 0, 1, false, BreakLine.NEVER));

      String offsetParam = offset.getOffsetParam();
      if (offsetParam != null) {
        renderer.appendString(offsetParam, indent, 0, 1, false, BreakLine.NEVER);
      }
    }

    Fetch fetch = plainSelect.getFetch();
    if (fetch != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent));
      JSQLFormatter.appendKeyWord(builder, fmt, "FETCH", "", "");

      if (fetch.isFetchParamFirst()) {
        JSQLFormatter.appendKeyWord(builder, fmt, "FIRST", " ", "");
      } else {
        JSQLFormatter.appendKeyWord(builder, fmt, "NEXT", " ", "");
      }

      Expression expression = fetch.getExpression();
      if (expression != null) {
        JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, " ");
        renderer.renderExpression(expression,
            FormatContext.of(indent, 0, 1, false, BreakLine.NEVER));
      }

      for (String s : fetch.getFetchParameters()) {
        JSQLFormatter.appendKeyWord(builder, fmt, s, " ", "");
      }

      // @todo: write-out ISOLATION
      if (plainSelect.getIsolation() != null) {
        builder.append(plainSelect.getIsolation());
      }

      // @todo: write-out Optimizer For
      if (plainSelect.getOptimizeFor() != null) {
        builder.append(plainSelect.getOptimizeFor());
      }

      // @todo: write-out FOR XML PATH
      if (plainSelect.getForXmlPath() != null) {
        builder.append(" FOR XML PATH(").append(plainSelect.getForXmlPath()).append(")");
      }

      // @todo: write-out INTO TEMP Table
      if (plainSelect.getIntoTempTable() != null) {
        builder.append(" INTO TEMP ").append(plainSelect.getIntoTempTable());
      }

      // @todo: write-out WITH NO LOG
      if (plainSelect.isUseWithNoLog()) {
        builder.append(" WITH NO LOG");
      }
    }
    return null;
  }

  @Override
  public <S> Void visit(SetOperationList setOperationList, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();
    int indent = ctx.indent;

    List<SetOperation> setOperations = setOperationList.getOperations();

    int k = 0;

    List<Select> selects = setOperationList.getSelects();
    if (selects != null && !selects.isEmpty()) {
      for (Select selectBody1 : selects) {
        if (k > 0 && setOperations != null && setOperations.size() >= k) {
          SetOperation setOperation = setOperations.get(k - 1);
          renderer.appendSetOperation(setOperation, indent);
        }
        // legacy: appendSelect(selectBody1, builder, subIndent, k > 0 || breakLineBefore,
        // k > 0 || breakLineBefore || indentFirstLine); with subIndent == indent
        boolean newBreakLineBefore = k > 0 || ctx.breakLineBefore;
        boolean newIndentFirstLine = k > 0 || ctx.breakLineBefore || ctx.indentFirstLine;
        renderer.renderSelect(selectBody1,
            FormatContext.of(indent).withSelectFraming(newBreakLineBefore, newIndentFirstLine));
        k++;
      }
    }

    List<OrderByElement> orderByElements = setOperationList.getOrderByElements();
    renderer.appendOrderByElements(orderByElements, indent);

    Limit limit = setOperationList.getLimit();
    if (limit != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent));
      JSQLFormatter.appendKeyWord(builder, fmt, "LIMIT", "", " ");

      Expression rowCount = limit.getRowCount();
      if (rowCount instanceof AllValue || rowCount instanceof NullValue) {
        // no offset allowed
        JSQLFormatter.appendKeyWord(builder, fmt, "NULL", "", " ");
      } else {
        if (null != limit.getOffset()) {
          renderer.renderExpression(limit.getOffset(),
              FormatContext.of(indent, 0, 1, false, BreakLine.NEVER));
          builder.append(", ");
        }
        if (null != limit.getRowCount()) {
          renderer.renderExpression(limit.getRowCount(),
              FormatContext.of(indent, 0, 1, false, BreakLine.NEVER));
        }
      }
    }

    Offset offset = setOperationList.getOffset();
    if (offset != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) {
        builder.append(JSQLFormatter.getIndentString());
      }
      JSQLFormatter.appendKeyWord(builder, fmt, "OFFSET", "", " ");

      Expression offsetExpression = offset.getOffset();
      renderer.renderExpression(offsetExpression,
          FormatContext.of(indent, 0, 1, false, BreakLine.NEVER));

      String offsetParam = offset.getOffsetParam();
      if (offsetParam != null) {
        renderer.appendString(offsetParam, indent, 0, 1, false, BreakLine.NEVER);
      }
    }
    return null;
  }

  @Override
  public <S> Void visit(Values values, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();
    int indent = ctx.indent;

    if (ctx.breakLineBefore) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      for (int j = 0; ctx.indentFirstLine && j < indent; j++) {
        builder.append(JSQLFormatter.getIndentString());
      }
    }

    JSQLFormatter.appendKeyWord(builder, fmt, "VALUES", "", " ");
    renderer.appendExpressionList(values.getExpressions(),
        FormatContext.of(indent).withBreakLine(BreakLine.AS_NEEDED));
    return null;
  }

  @Override
  public <S> Void visit(ParenthesedSelect parenthesedSelect, S context) {
    FormatContext ctx = (FormatContext) context;
    int indent = ctx.indent;

    builder.append("( ");
    int subIndent = ctx.breakLineBefore ? indent + 1
        : JSQLFormatter.getSubIndent(builder, !(parenthesedSelect.getSelect() instanceof Values));

    renderer.renderSelect(parenthesedSelect.getSelect(),
        FormatContext.of(subIndent).withSelectFraming(ctx.breakLineBefore, ctx.indentFirstLine));
    builder.append(" )");

    if (parenthesedSelect.getPivot() != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent));
      builder.append(parenthesedSelect.getPivot());
    }

    if (parenthesedSelect.getUnPivot() != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent));
      builder.append(parenthesedSelect.getUnPivot());
    }
    return null;
  }

  // ════════════════════════════════════════════════════════════════════════════════════════
  // Catch-all overrides — every SelectVisitor slot we haven't explicitly handled.
  //
  // The legacy code drove WithItem rendering through Renderer#appendWithItem (still does)
  // and never dispatched it via SelectVisitor.visit, so the adapter's default visit(WithItem)
  // wouldn't fire in our pipeline. We override defensively so any future call site that does
  // dispatch a WithItem through SelectVisitor matches the legacy unhandled-else fallback
  // (builder.append(expression)) instead of the adapter's silent no-op.
  // ════════════════════════════════════════════════════════════════════════════════════════

  @Override
  public <S> Void visit(WithItem<?> withItem, S context) {
    builder.append(withItem);
    return null;
  }
}
