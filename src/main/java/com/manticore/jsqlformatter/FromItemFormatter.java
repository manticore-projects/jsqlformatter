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
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.ParenthesedFromItem;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.TableFunction;
import net.sf.jsqlparser.statement.select.Values;

import java.util.List;

/**
 * FromItemVisitor that dispatches to the appropriate render method per concrete {@code FromItem}
 * sub-type.
 *
 * <p>
 * Replaces the legacy if/instanceof chain in {@code appendFromItem()}. The leading line-break /
 * comma-prefix logic and trailing comma logic live in {@link Renderer#renderFromItem} — each visit
 * method here only emits the from-item body itself.
 *
 * <p>
 * JSqlParser 5.x's {@code FromItemVisitor<T>} declares {@code visit(...)} methods for each concrete
 * {@code FromItem} implementation (not for the abstract {@code Select}); the Select-as-FromItem
 * case is handled by the four concrete Select subtypes ({@link PlainSelect},
 * {@link ParenthesedSelect}, {@link SetOperationList}, {@link Values}), each of which dispatches
 * via {@link Renderer#renderSelect} to the {@link SelectFormatter}.
 *
 * @author <a href="mailto:andreas@manticore-projects.com">Andreas Reichel</a>
 */
final class FromItemFormatter extends FromItemVisitorAdapter<Void> {

  private final Renderer renderer;
  private final StringBuilder builder;

  FromItemFormatter(Renderer renderer) {
    this.renderer = renderer;
    this.builder = renderer.builder;
  }

  // ─── Table ─────────────────────────────────────────────────────────────────────────────

  @Override
  public <S> Void visit(Table table, S context) {
    Alias alias = table.getAlias();
    renderer.appendTable(table, alias);
    return null;
  }

  // ─── Select-as-FromItem (4 concrete subtypes share the same body) ─────────────────────

  @Override
  public <S> Void visit(PlainSelect plainSelect, S context) {
    return visitAsSelectFromItem(plainSelect, context);
  }

  @Override
  public <S> Void visit(ParenthesedSelect parenthesedSelect, S context) {
    return visitAsSelectFromItem(parenthesedSelect, context);
  }

  @Override
  public <S> Void visit(SetOperationList setOperationList, S context) {
    return visitAsSelectFromItem(setOperationList, context);
  }

  @Override
  public <S> Void visit(Values values, S context) {
    return visitAsSelectFromItem(values, context);
  }

  /**
   * Common body for the four concrete {@link Select} subtypes appearing as a FromItem.
   *
   * <p>
   * Mirrors the legacy:
   * 
   * <pre>{@code
   * } else if (fromItem instanceof Select) {
   *   Select select = (Select) fromItem;
   *   appendSelect(select, builder, indent, false, true);
   *   appendAlias(builder, outputFormat, alias, " ", "");
   * }
   * }</pre>
   */
  private <S> Void visitAsSelectFromItem(Select select, S context) {
    FormatContext ctx = (FormatContext) context;
    Alias alias = select.getAlias();
    renderer.renderSelect(select, ctx.withSelectFraming(false, true));
    JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(), alias, " ", "");
    return null;
  }

  // ─── ParenthesedFromItem ───────────────────────────────────────────────────────────────

  @Override
  public <S> Void visit(ParenthesedFromItem parenthesedFromItem, S context) {
    FormatContext ctx = (FormatContext) context;
    Alias alias = parenthesedFromItem.getAlias();
    int indent = ctx.indent;

    builder.append("( ");
    int subIndent = JSQLFormatter.getSubIndent(builder, true);

    // legacy: appendFromItem(parenthesedFromItem.getFromItem(), builder, indent, i, n);
    // Indent stays as the outer indent; subIndent is captured for joins only (legacy passes
    // subIndent to appendJoins).
    renderer.renderFromItem(parenthesedFromItem.getFromItem(),
        FormatContext.of(indent, ctx.i, ctx.n, false, BreakLine.NEVER));
    List<Join> joins = parenthesedFromItem.getJoins();
    renderer.appendJoins(joins, subIndent);
    builder.append(" )");

    JSQLFormatter.appendAlias(builder, JSQLFormatter.getOutputFormat(), alias, " ", "");
    return null;
  }

  // ─── TableFunction ─────────────────────────────────────────────────────────────────────

  @Override
  public <S> Void visit(TableFunction tableFunction, S context) {
    FormatContext ctx = (FormatContext) context;
    // legacy: appendExpression(tableFunction.getFunction(), tableFunction.getAlias(), builder,
    // indent, 0, 1, true, BreakLine.AS_NEEDED);
    renderer.renderExpression(tableFunction.getFunction(), ctx.asScalar(BreakLine.AS_NEEDED)
        .withCommaSeparated(true).withAlias(tableFunction.getAlias()));
    return null;
  }

  // ─── LATERAL paths ─────────────────────────────────────────────────────────────────────
  // The legacy code did not have explicit branches for these; they fell through the
  // "FROM Item not covered" else-branch, which delegated to fromItem.appendTo(...).
  // We preserve that exact byte-output behaviour here. (The legacy's WARNING log is a
  // side-effect, not part of the produced SQL, so it is omitted.)

  @Override
  public <S> Void visit(LateralSubSelect lateralSubSelect, S context) {
    return appendUnhandled(lateralSubSelect);
  }

  // @Override
  // public <S> Void visit(LateralView lateralView, S context) {
  // return appendUnhandled(lateralView);
  // }

  /**
   * Mirrors the legacy {@code appendFromItem()} fallback for FromItem types not explicitly handled
   * by the if/instanceof chain:
   * 
   * <pre>{@code
   * fromItem.appendTo(builder, fromItem.getAlias());
   * }</pre>
   */
  private Void appendUnhandled(FromItem fromItem) {
    fromItem.appendTo(builder, fromItem.getAlias());
    return null;
  }
}
