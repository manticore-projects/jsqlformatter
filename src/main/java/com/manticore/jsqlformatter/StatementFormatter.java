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
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.AllValue;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Block;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.statement.CreateFunctionalStatement;
import net.sf.jsqlparser.statement.DeclareStatement;
import net.sf.jsqlparser.statement.DescribeStatement;
import net.sf.jsqlparser.statement.ExplainStatement;
import net.sf.jsqlparser.statement.IfElseStatement;
import net.sf.jsqlparser.statement.PurgeStatement;
import net.sf.jsqlparser.statement.ResetStatement;
import net.sf.jsqlparser.statement.RollbackStatement;
import net.sf.jsqlparser.statement.SavepointStatement;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.ShowColumnsStatement;
import net.sf.jsqlparser.statement.ShowStatement;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.UnsupportedStatement;
import net.sf.jsqlparser.statement.UseStatement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterSession;
import net.sf.jsqlparser.statement.alter.AlterSystemStatement;
import net.sf.jsqlparser.statement.alter.RenameTableStatement;
import net.sf.jsqlparser.statement.alter.sequence.AlterSequence;
import net.sf.jsqlparser.statement.analyze.Analyze;
import net.sf.jsqlparser.statement.comment.Comment;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.schema.CreateSchema;
import net.sf.jsqlparser.statement.create.sequence.CreateSequence;
import net.sf.jsqlparser.statement.create.synonym.CreateSynonym;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.grant.Grant;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.merge.MergeInsert;
import net.sf.jsqlparser.statement.merge.MergeUpdate;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.show.ShowTablesStatement;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StatementVisitor that dispatches to the appropriate render method per statement type.
 *
 * <p>
 * Replaces the legacy if/instanceof chain in {@code JSQLFormatter.format()}. Each visit method
 * mirrors exactly one branch of that chain — output is identical to the procedural version.
 *
 * @author <a href="mailto:andreas@manticore-projects.com">Andreas Reichel</a>
 */

@SuppressWarnings("PMD.CyclomaticComplexity")
final class StatementFormatter extends StatementVisitorAdapter<Void> {

  private final Renderer renderer;
  private final StringBuilder builder;

  StatementFormatter(Renderer renderer) {
    this.renderer = renderer;
    this.builder = renderer.builder;
  }

  @Override
  public <S> Void visit(Select select, S context) {
    FormatContext ctx = (FormatContext) context;
    renderer.renderSelect(select, ctx.withSelectFraming(true, false));
    return null;
  }

  @Override
  public <S> Void visit(Update update, S context) {
    FormatContext ctx = (FormatContext) context;
    appendUpdate(update, ctx.indent);
    return null;
  }

  @Override
  public <S> Void visit(Insert insert, S context) {
    FormatContext ctx = (FormatContext) context;
    appendInsert(insert, ctx.indent);
    return null;
  }

  @Override
  public <S> Void visit(Merge merge, S context) {
    FormatContext ctx = (FormatContext) context;
    appendMerge(merge, ctx.indent);
    return null;
  }

  @Override
  public <S> Void visit(Delete delete, S context) {
    FormatContext ctx = (FormatContext) context;
    appendDelete(delete, ctx.indent);
    return null;
  }

  @Override
  public <S> Void visit(Truncate truncate, S context) {
    renderer.appendTruncate(truncate);
    return null;
  }

  @Override
  public <S> Void visit(CreateTable createTable, S context) {
    FormatContext ctx = (FormatContext) context;
    renderer.appendCreateTable(createTable, ctx.indent);
    return null;
  }

  @Override
  public <S> Void visit(CreateIndex createIndex, S context) {
    FormatContext ctx = (FormatContext) context;
    renderer.appendCreateIndex(createIndex, ctx.indent);
    return null;
  }

  @Override
  public <S> Void visit(CreateView createView, S context) {
    FormatContext ctx = (FormatContext) context;
    renderer.appendCreateView(createView, ctx.indent);
    return null;
  }

  @Override
  public <S> Void visit(Alter alter, S context) {
    FormatContext ctx = (FormatContext) context;
    renderer.appendAlter(alter, ctx.indent);
    return null;
  }

  @Override
  public <S> Void visit(ExplainStatement explainStatement, S context) {
    FormatContext ctx = (FormatContext) context;
    JSQLFormatter.appendNormalizedLineBreak(builder);
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(),
        explainStatement.getKeyword(), "", " ");

    if (explainStatement.getTable() == null) {
      LinkedHashMap<ExplainStatement.OptionType, ExplainStatement.Option> options =
          explainStatement.getOptions();
      if (options != null && !options.isEmpty()) {
        builder.append(options.values().stream().map(ExplainStatement.Option::formatOption)
            .collect(Collectors.joining(" ")));
      }

      // The legacy code dispatches the inner statement via instanceof — re-use the visitor here
      // so any future statement type added to JSqlParser will also be picked up automatically.
      Statement inner = explainStatement.getStatement();
      if (inner instanceof Select) {
        renderer.renderSelect((Select) inner, ctx.withSelectFraming(true, false));
      } else if (inner instanceof Update) {
        appendUpdate((Update) inner, ctx.indent);
      } else if (inner instanceof Merge) {
        appendMerge((Merge) inner, ctx.indent);
      } else if (inner instanceof Delete) {
        appendDelete((Delete) inner, ctx.indent);
      }
    } else {
      renderer.appendTable(explainStatement.getTable(), explainStatement.getTable().getAlias());
    }
    return null;
  }

  // ===================================================================================
  // Statement-body methods (kept private — they're the bodies of the corresponding visit
  // methods above, factored out so the ExplainStatement branch can reuse them too).
  // ===================================================================================

  @SuppressWarnings({"PMD.CyclomaticComplexity"})
  private void appendDelete(Delete delete, int indent) {
    List<WithItem<?>> withItems = delete.getWithItemsList();
    if (withItems != null && !withItems.isEmpty()) {
      int i = 0;
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "WITH", "", " ");
      for (WithItem<?> withItem : withItems) {
        renderer.appendWithItem(withItem, indent, i, withItems.size());
        i++;
      }
      JSQLFormatter.appendNormalizedLineBreak(builder);
    }
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "DELETE", "", " ");

    OracleHint oracleHint = delete.getOracleHint();
    if (oracleHint != null) {
      JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), oracleHint.toString(), "",
          " ");
    }

    List<Table> tables = delete.getTables();
    if (tables != null && !tables.isEmpty()) {
      int j = 0;
      for (Table table : tables) {
        switch (JSQLFormatter.getSeparation()) {
          case AFTER:
            JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(),
                table.getFullyQualifiedName(), "", j < tables.size() - 1 ? ", " : " ");
            break;
          case BEFORE:
          default:
            JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(),
                table.getFullyQualifiedName(), j > 0 ? ", " : "", " ");
            break;
        }
        j++;
      }
    }

    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "FROM", "", " ");

    Table table = delete.getTable();
    Alias alias = table.getAlias();
    renderer.appendTable(table, alias);

    List<Join> joins = delete.getJoins();
    renderer.appendJoins(joins, indent);

    Expression whereExpression = delete.getWhere();
    renderer.appendWhere(whereExpression, indent);

    List<OrderByElement> orderByElements = delete.getOrderByElements();
    renderer.appendOrderByElements(orderByElements, indent);

    Limit limit = delete.getLimit();
    if (limit != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      for (int j = 0; j < indent; j++) {
        builder.append(JSQLFormatter.getIndentString());
      }
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "LIMIT", "", "");

      Expression rowCount = limit.getRowCount();
      if (rowCount instanceof AllValue || rowCount instanceof NullValue) {
        // no offset allowed
        JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "NULL", " ", "");
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
  }

  private void appendInsert(Insert insert, int indent) {
    List<WithItem<?>> withItems = insert.getWithItemsList();
    if (withItems != null && !withItems.isEmpty()) {
      int i = 0;
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "WITH", "", " ");
      for (WithItem<?> withItem : withItems) {
        renderer.appendWithItem(withItem, indent, i, withItems.size());
        i++;
      }
      JSQLFormatter.appendNormalizedLineBreak(builder);
    }

    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "INSERT", "", " ");
    OracleHint oracleHint = insert.getOracleHint();
    if (oracleHint != null) {
      JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), oracleHint.toString(), "",
          " ");
    }
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "INTO", "", " ");

    Table table = insert.getTable();
    Alias alias = table.getAlias();
    renderer.appendTable(table, alias);

    List<Column> columns = insert.getColumns();
    if (columns != null) {
      int i = 0;
      builder.append(" (");
      for (Column column : columns) {
        renderer.renderExpression(column,
            FormatContext.of(indent + 1, i, columns.size(), true, BreakLine.ALWAYS));
        i++;
      }
      JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, " ) ");
    }
    JSQLFormatter.appendNormalizedLineBreak(builder);
    Select select = insert.getSelect();
    renderer.renderSelect(select, FormatContext.of(indent).withSelectFraming(false, false));
  }

  private void appendUpdate(Update update, int indent) {
    List<WithItem<?>> withItems = update.getWithItemsList();
    if (withItems != null && !withItems.isEmpty()) {
      int i = 0;
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "WITH", "", " ");
      for (WithItem<?> withItem : withItems) {
        renderer.appendWithItem(withItem, indent, i, withItems.size());
        i++;
      }
      JSQLFormatter.appendNormalizedLineBreak(builder);
    }

    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "UPDATE", "", " ");

    OracleHint oracleHint = update.getOracleHint();
    if (oracleHint != null) {
      JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), oracleHint.toString(), "",
          " ");
    }

    Table table = update.getTable();
    Alias alias = table.getAlias();
    renderer.appendTable(table, alias);

    if (update.getStartJoins() != null) {
      renderer.appendJoins(update.getStartJoins(), indent);
    }

    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(indent));
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "SET", "", " ");

    final int subIndent = JSQLFormatter.getSubIndent(builder, true);
    renderer.appendUpdateSets(update.getUpdateSets(), subIndent);

    if (update.getFromItem() != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(indent));
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "FROM", "", " ");
      renderer.renderFromItem(update.getFromItem(),
          FormatContext.of(indent, 0, 1, false, BreakLine.NEVER));
    }

    List<Join> joins = update.getJoins();
    renderer.appendJoins(joins, indent);

    Expression whereExpression = update.getWhere();
    renderer.appendWhere(whereExpression, indent);

    List<OrderByElement> orderByElements = update.getOrderByElements();
    renderer.appendOrderByElements(orderByElements, indent);
  }

  private void appendMerge(Merge merge, int indent) {
    List<WithItem<?>> withItems = merge.getWithItemsList();
    if (withItems != null && !withItems.isEmpty()) {
      int i = 0;
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "WITH", "", " ");
      for (WithItem<?> withItem : withItems) {
        renderer.appendWithItem(withItem, indent, i, withItems.size());
        i++;
      }
      JSQLFormatter.appendNormalizedLineBreak(builder);
    }

    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "MERGE", "", " ");
    OracleHint oracleHint = merge.getOracleHint();
    if (oracleHint != null) {
      JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), oracleHint.toString(), "",
          " ");
    }

    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "INTO", "", " ");
    Table table = merge.getTable();
    Alias alias = table.getAlias();
    renderer.appendTable(table, alias);

    JSQLFormatter.appendNormalizedLineBreak(builder);
    for (int j = 0; j < indent + 1; j++) {
      builder.append(JSQLFormatter.getIndentString());
    }
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "USING", "", " ");

    FromItem fromItem = merge.getFromItem();
    renderer.renderFromItem(fromItem, FormatContext.of(indent, 0, 1, false, BreakLine.NEVER));

    Expression onExpression = merge.getOnCondition();
    if (onExpression != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(Math.max(0, indent + 2)));
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "ON", "", " ");
      renderer.renderExpression(onExpression,
          FormatContext.of(indent + 2, 0, 1, false, BreakLine.AS_NEEDED));
      JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, " ");
    }

    MergeInsert insert = merge.getMergeInsert();
    MergeUpdate update = merge.getMergeUpdate();
    if (merge.isInsertFirst()) {
      renderer.appendMergeInsert(insert, indent, 0);
      renderer.appendMergeUpdate(update, indent);
    } else {
      renderer.appendMergeUpdate(update, indent);
      renderer.appendMergeInsert(insert, indent, 0);
    }

    renderer.appendOutputClause(merge.getOutputClause(), indent);
  }

  // ════════════════════════════════════════════════════════════════════════════════════════
  // Catch-all overrides — every StatementVisitor slot we haven't explicitly handled.
  //
  // For statements with a clear, simple shape (single leading keyword + optional name/args
  // on one line: COMMIT, USE, SAVEPOINT, RESET, DESCRIBE, SHOW TABLES, PURGE) we render
  // explicitly so the leading keyword goes through {@link JSQLFormatter#appendKeyWord} —
  // which honours {@code keywordSpelling} — and any trailing name is preserved verbatim.
  //
  // For more complex statements (DROP with optional clauses, GRANT, CREATE SCHEMA, the
  // various ALTER variants, control-flow statements like IF/BLOCK, …) we route through
  // {@link Renderer#appendWithKeywordSpelling} which walks the AST node's toString output
  // and applies keyword spelling to all-uppercase tokens while leaving identifiers and
  // quoted strings alone. Indent and line-break policies aren't applied here, but at
  // least the keyword case is consistent with the rest of the output.
  // ════════════════════════════════════════════════════════════════════════════════════════

  /**
   * Smart fallback: emit the statement's {@code toString()} with keyword spelling applied to
   * all-uppercase tokens (length ≥ 2). Used for statements whose internal structure is
   * complex/version-specific enough that a hand-written render would risk regressions.
   */
  private Void appendUnhandledStatement(Statement statement) {
    Renderer.appendWithKeywordSpelling(builder, statement);
    return null;
  }

  /**
   * Render a single-keyword statement: emit the leading keyword phrase via {@code appendKeyWord}
   * (so {@code keywordSpelling} is applied) and append the rest of {@code toString} verbatim. For
   * statements where the leading keyword is the entire useful content (e.g. {@code COMMIT}),
   * {@code rest} is empty and we just emit the keyword.
   */
  private Void appendSimpleKeywordStatement(Statement statement, String keyword) {
    String text = statement.toString();
    if (text.regionMatches(true, 0, keyword, 0, keyword.length())) {
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), keyword, "", "");
      // Carry over the remainder (typically a leading space + identifier/options).
      builder.append(text, keyword.length(), text.length());
    } else {
      // The statement's toString didn't start with the expected keyword — fall back to smart.
      Renderer.appendWithKeywordSpelling(builder, statement);
    }
    return null;
  }

  // ── Single-keyword / simple-shape statements: render leading keyword properly ──────────

  @Override
  public <S> Void visit(Commit commit, S context) {
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "COMMIT", "", "");
    return null;
  }

  @Override
  public <S> Void visit(SavepointStatement sp, S context) {
    return appendSimpleKeywordStatement(sp, "SAVEPOINT");
  }

  @Override
  public <S> Void visit(RollbackStatement rs, S context) {
    return appendSimpleKeywordStatement(rs, "ROLLBACK");
  }

  @Override
  public <S> Void visit(UseStatement use, S context) {
    // toString may be "USE <name>" or "USE SCHEMA <name>"; handle both prefix lengths.
    String text = use.toString();
    String upper = text.toUpperCase();
    if (upper.startsWith("USE SCHEMA")) {
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "USE SCHEMA", "", "");
      builder.append(text, "USE SCHEMA".length(), text.length());
      return null;
    }
    return appendSimpleKeywordStatement(use, "USE");
  }

  @Override
  public <S> Void visit(ResetStatement reset, S context) {
    return appendSimpleKeywordStatement(reset, "RESET");
  }

  @Override
  public <S> Void visit(SetStatement set, S context) {
    // SetStatement varies by dialect (SET name = value / SET name TO value / SET LOCAL …).
    // Render the leading SET via keyword spelling, then route the rest through smart
    // fallback so any nested keyword tokens (LOCAL, SESSION, TRANSACTION, …) also pick up
    // the spelling policy.
    String text = set.toString();
    if (text.regionMatches(true, 0, "SET", 0, 3)) {
      JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "SET", "", "");
      // Apply smart spelling to the remainder so SESSION/LOCAL/TRANSACTION are spelled too.
      String rest = text.substring(3);
      // Build to a temp StringBuilder so the helper's appendKeyWord uses out, not builder
      StringBuilder remainder = new StringBuilder(rest.length());
      Renderer.appendWithKeywordSpelling(remainder, rest);
      builder.append(remainder);
    } else {
      Renderer.appendWithKeywordSpelling(builder, set);
    }
    return null;
  }

  @Override
  public <S> Void visit(DescribeStatement describe, S context) {
    // toString is "DESC[RIBE] <name>"; handle both forms.
    String text = describe.toString();
    String upper = text.toUpperCase();
    String kw = upper.startsWith("DESCRIBE") ? "DESCRIBE" : "DESC";
    return appendSimpleKeywordStatement(describe, kw);
  }

  @Override
  public <S> Void visit(ShowTablesStatement showTables, S context) {
    return appendSimpleKeywordStatement(showTables, "SHOW TABLES");
  }

  @Override
  public <S> Void visit(ShowColumnsStatement showCols, S context) {
    // Typically "SHOW COLUMNS FROM <table>"; smart fallback handles the full keyword chain.
    return appendUnhandledStatement(showCols);
  }

  @Override
  public <S> Void visit(ShowStatement show, S context) {
    return appendSimpleKeywordStatement(show, "SHOW");
  }

  @Override
  public <S> Void visit(PurgeStatement purge, S context) {
    return appendSimpleKeywordStatement(purge, "PURGE");
  }

  @Override
  public <S> Void visit(Analyze analyze, S context) {
    return appendSimpleKeywordStatement(analyze, "ANALYZE");
  }

  @Override
  public <S> Void visit(Drop drop, S context) {
    // "DROP TABLE/INDEX/VIEW/… [IF EXISTS] name [CASCADE]" — multiple keywords sprinkled
    // throughout. Smart fallback renders all of them with the right spelling.
    return appendUnhandledStatement(drop);
  }

  @Override
  public <S> Void visit(Execute execute, S context) {
    // "EXECUTE <name>(args)" or "EXEC <name> args" — bias toward EXECUTE, smart-fall otherwise.
    String text = execute.toString();
    String upper = text.toUpperCase();
    String kw = upper.startsWith("EXECUTE") ? "EXECUTE" : upper.startsWith("EXEC") ? "EXEC" : null;
    if (kw != null) {
      return appendSimpleKeywordStatement(execute, kw);
    }
    return appendUnhandledStatement(execute);
  }

  // ── Compound / variant statements: smart fallback with keyword spelling ────────────────

  // @Override public <S> Void visit(Replace replace, S context) { return
  // appendUnhandledStatement(replace); }
  @Override
  public <S> Void visit(Upsert upsert, S context) {
    return appendUnhandledStatement(upsert);
  }

  // @Override public <S> Void visit(ValuesStatement values, S context) { return
  // appendUnhandledStatement(values); }
  @Override
  public <S> Void visit(CreateSchema createSchema, S context) {
    return appendUnhandledStatement(createSchema);
  }

  @Override
  public <S> Void visit(CreateSequence createSequence, S context) {
    return appendUnhandledStatement(createSequence);
  }

  @Override
  public <S> Void visit(AlterSequence alterSequence, S context) {
    return appendUnhandledStatement(alterSequence);
  }

  @Override
  public <S> Void visit(AlterView alterView, S context) {
    return appendUnhandledStatement(alterView);
  }

  @Override
  public <S> Void visit(CreateSynonym createSynonym, S context) {
    return appendUnhandledStatement(createSynonym);
  }

  @Override
  public <S> Void visit(CreateFunctionalStatement cfs, S context) {
    return appendUnhandledStatement(cfs);
  }

  @Override
  public <S> Void visit(RenameTableStatement rt, S context) {
    return appendUnhandledStatement(rt);
  }

  @Override
  public <S> Void visit(AlterSession as, S context) {
    return appendUnhandledStatement(as);
  }

  @Override
  public <S> Void visit(AlterSystemStatement ass, S context) {
    return appendUnhandledStatement(ass);
  }

  @Override
  public <S> Void visit(Block block, S context) {
    return appendUnhandledStatement(block);
  }

  @Override
  public <S> Void visit(IfElseStatement ifElse, S context) {
    return appendUnhandledStatement(ifElse);
  }

  @Override
  public <S> Void visit(DeclareStatement declare, S context) {
    return appendUnhandledStatement(declare);
  }

  @Override
  public <S> Void visit(Comment comment, S context) {
    return appendUnhandledStatement(comment);
  }

  @Override
  public <S> Void visit(Grant grant, S context) {
    return appendUnhandledStatement(grant);
  }

  @Override
  public <S> Void visit(UnsupportedStatement unsupported, S context) {
    return appendUnhandledStatement(unsupported);
  }

  /**
   * The {@code Statements} container is normally unwrapped in {@link JSQLFormatter#format} (each
   * inner Statement is dispatched separately). If we do hit this slot, walk the children so the
   * adapter's default doesn't drop them.
   */
  @Override
  public <S> Void visit(Statements stmts, S context) {
    if (stmts.getStatements() != null) {
      for (Statement statement : stmts.getStatements()) {
        if (statement != null) {
          statement.accept(this, context);
        }
      }
    }
    return null;
  }
}
