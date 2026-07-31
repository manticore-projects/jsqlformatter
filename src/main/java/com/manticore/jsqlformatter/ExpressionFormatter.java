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
import net.sf.jsqlparser.expression.AllValue;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnalyticType;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.ArrayConstructor;
import net.sf.jsqlparser.expression.ArrayExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.BooleanValue;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.CollateExpression;
import net.sf.jsqlparser.expression.ConnectByPriorOperator;
import net.sf.jsqlparser.expression.ConnectByRootOperator;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.JsonAggregateFunction;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.JsonFunction;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.KeyExpression;
import net.sf.jsqlparser.expression.LambdaExpression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NextValExpression;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.OracleNamedFunctionParameter;
import net.sf.jsqlparser.expression.OverlapsCondition;
import net.sf.jsqlparser.expression.PostgresNamedFunctionParameter;
import net.sf.jsqlparser.expression.RangeExpression;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.RowGetExpression;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.StructType;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.TimezoneExpression;
import net.sf.jsqlparser.expression.TranscodingFunction;
import net.sf.jsqlparser.expression.TrimFunction;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.VariableAssignment;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.XMLSerializeExpr;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.CosineSimilarity;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExcludesExpression;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.FullTextSearch;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IncludesExpression;
import net.sf.jsqlparser.expression.operators.relational.IsBooleanExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.IsUnknownExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.MemberOfExpression;
import net.sf.jsqlparser.expression.operators.relational.NamedExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FunctionAllColumns;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.Pivot;
import net.sf.jsqlparser.statement.select.PivotXml;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.UnPivot;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ExpressionVisitor that dispatches each expression type to its own visit method.
 *
 * <p>
 * Replaces the legacy {@code appendExpression()} if/instanceof chain. The leading line-break /
 * comma-prefix logic and the trailing alias/comma logic live in {@link Renderer#renderExpression} —
 * each visit method here only emits the expression body itself.
 *
 * <p>
 * {@code visitBinaryExpression} is the catch-all for {@link BinaryExpression} subclasses not
 * specifically overridden (Addition, GreaterThan, Concat, etc.); the legacy chain placed the
 * generic {@code BinaryExpression} branch last so that more-specific subclasses (EqualsTo,
 * LikeExpression, AndExpression, OrExpression) could intercept first. The visitor pattern encodes
 * the same priority by overriding those subclasses explicitly.
 *
 * @author <a href="mailto:andreas@manticore-projects.com">Andreas Reichel</a>
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass"})
final class ExpressionFormatter extends ExpressionVisitorAdapter<Void> {

  private final Renderer renderer;
  private final StringBuilder builder;

  ExpressionFormatter(Renderer renderer) {
    this.renderer = renderer;
    this.builder = renderer.builder;
  }

  // ===================================================================================
  // Identifiers / literals
  // ===================================================================================

  @Override
  public <S> Void visit(Column column, S context) {
    JSQLFormatter.appendObjectName(builder, JSQLFormatter.getOutputFormat(), column.toString(), "",
        "");
    return null;
  }

  @Override
  public <S> Void visit(StringValue stringValue, S context) {
    JSQLFormatter.appendValue(builder, JSQLFormatter.getOutputFormat(), stringValue.toString(), "",
        "");
    return null;
  }

  @Override
  public <S> Void visit(LongValue longValue, S context) {
    JSQLFormatter.appendValue(builder, JSQLFormatter.getOutputFormat(), longValue.toString(), "",
        "");
    return null;
  }

  @Override
  public <S> Void visit(DateValue dateValue, S context) {
    JSQLFormatter.appendValue(builder, JSQLFormatter.getOutputFormat(), dateValue.toString(), "",
        "");
    return null;
  }

  @Override
  public <S> Void visit(DoubleValue doubleValue, S context) {
    JSQLFormatter.appendValue(builder, JSQLFormatter.getOutputFormat(), doubleValue.toString(), "",
        "");
    return null;
  }

  @Override
  public <S> Void visit(NullValue nullValue, S context) {
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), nullValue.toString(), "",
        "");
    return null;
  }

  @Override
  public <S> Void visit(TimeKeyExpression timeKeyExpression, S context) {
    JSQLFormatter.appendValue(builder, JSQLFormatter.getOutputFormat(),
        timeKeyExpression.toString(), "", "");
    return null;
  }

  @Override
  public <S> Void visit(JdbcNamedParameter jdbcNamedParameter, S context) {
    JSQLFormatter.appendValue(builder, JSQLFormatter.getOutputFormat(),
        jdbcNamedParameter.toString(), "", "");
    return null;
  }

  @Override
  public <S> Void visit(JdbcParameter jdbcParameter, S context) {
    JSQLFormatter.appendValue(builder, JSQLFormatter.getOutputFormat(), jdbcParameter.toString(),
        "", "");
    return null;
  }

  // ===================================================================================
  // Boolean operators
  // ===================================================================================

  @Override
  public <S> Void visit(AndExpression andExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    renderer.renderExpression(andExpression.getLeftExpression(),
        ctx.inheritingPosition(BreakLine.AFTER_FIRST));

    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(ctx.indent + 1));
    JSQLFormatter.appendOperator(builder, fmt, "AND", "", " ");

    renderer.renderExpression(andExpression.getRightExpression(),
        ctx.nested().inheritingPosition(BreakLine.AFTER_FIRST));
    return null;
  }

  @Override
  public <S> Void visit(OrExpression orExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    renderer.renderExpression(orExpression.getLeftExpression(),
        ctx.inheritingPosition(BreakLine.AFTER_FIRST));

    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(ctx.indent + 1));
    JSQLFormatter.appendOperator(builder, fmt, "OR", "", " ");

    renderer.renderExpression(orExpression.getRightExpression(),
        ctx.nested().inheritingPosition(BreakLine.AFTER_FIRST));
    return null;
  }

  @Override
  public <S> Void visit(NotExpression notExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    if (notExpression.isExclamationMark()) {
      JSQLFormatter.appendOperator(builder, fmt, "!", "", "");
    } else {
      JSQLFormatter.appendOperator(builder, fmt, "NOT", "", " ");
    }

    renderer.renderExpression(notExpression.getExpression(),
        ctx.nested().inheritingPosition(BreakLine.AFTER_FIRST));
    return null;
  }

  // ===================================================================================
  // Comparison & SQL-binary operators
  // ===================================================================================

  @Override
  public <S> Void visit(EqualsTo equalsTo, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    if (equalsTo.getOraclePriorPosition() == EqualsTo.ORACLE_PRIOR_START) {
      JSQLFormatter.appendOperator(builder, fmt, "PRIOR", "", " ");
    }

    renderer.renderExpression(equalsTo.getLeftExpression(),
        ctx.nested().inheritingPosition(BreakLine.AS_NEEDED));

    if (equalsTo.getOldOracleJoinSyntax() == EqualsTo.ORACLE_JOIN_RIGHT) {
      JSQLFormatter.appendOperator(builder, fmt, "(+)", "", " ");
    }

    JSQLFormatter.appendOperator(builder, fmt, "=", " ", " ");

    if (equalsTo.getOraclePriorPosition() == EqualsTo.ORACLE_PRIOR_END) {
      JSQLFormatter.appendOperator(builder, fmt, "PRIOR", "", " ");
    }

    renderer.renderExpression(equalsTo.getRightExpression(),
        ctx.nested().inheritingPosition(BreakLine.AFTER_FIRST));

    if (equalsTo.getOldOracleJoinSyntax() == EqualsTo.ORACLE_JOIN_LEFT) {
      JSQLFormatter.appendOperator(builder, fmt, "(+)", "", " ");
    }
    return null;
  }

  @Override
  public <S> Void visit(LikeExpression likeExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    renderer.renderExpression(likeExpression.getLeftExpression(),
        ctx.nested().inheritingPosition(BreakLine.AFTER_FIRST));

    if (likeExpression.isNot()) {
      JSQLFormatter.appendOperator(builder, fmt, "NOT", " ", "");
    }

    JSQLFormatter.appendOperator(builder, fmt, "LIKE", " ", " ");

    renderer.renderExpression(likeExpression.getRightExpression(),
        ctx.nested().inheritingPosition(BreakLine.AFTER_FIRST));

    Expression escapeExpression = likeExpression.getEscape();
    if (escapeExpression != null) {
      JSQLFormatter.appendOperator(builder, fmt, "ESCAPE", " ", " ");
      renderer.renderExpression(escapeExpression,
          ctx.nested().inheritingPosition(BreakLine.AS_NEEDED));
    }
    return null;
  }

  @Override
  public <S> Void visit(IsNullExpression isNullExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    renderer.renderExpression(isNullExpression.getLeftExpression(),
        ctx.nested().inheritingPosition(BreakLine.AFTER_FIRST));

    if (isNullExpression.isUseNotNull()) {
      JSQLFormatter.appendOperator(builder, fmt, "NOTNULL", " ", "");
    } else if (isNullExpression.isUseIsNull()) {
      if (isNullExpression.isNot()) {
        JSQLFormatter.appendOperator(builder, fmt, "NOT ISNULL", " ", "");
      } else {
        JSQLFormatter.appendOperator(builder, fmt, "ISNULL", " ", "");
      }
    } else {
      if (isNullExpression.isNot()) {
        JSQLFormatter.appendOperator(builder, fmt, "IS NOT NULL", " ", "");
      } else {
        JSQLFormatter.appendOperator(builder, fmt, "IS NULL", " ", "");
      }
    }
    return null;
  }

  @Override
  public <S> Void visit(InExpression inExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    Expression leftExpression = inExpression.getLeftExpression();
    boolean useNot = inExpression.isNot();
    Expression rightExpression = inExpression.getRightExpression();

    renderer.renderExpression(leftExpression, ctx.inheritingPosition(BreakLine.AS_NEEDED));

    if (inExpression.isGlobal()) {
      JSQLFormatter.appendKeyWord(builder, fmt, "GLOBAL", " ", "");
    }

    if (useNot) {
      JSQLFormatter.appendOperator(builder, fmt, "NOT IN", " ", " ");
    } else {
      JSQLFormatter.appendOperator(builder, fmt, "IN", " ", " ");
    }

    renderer.renderExpression(rightExpression, ctx.inheritingPosition(BreakLine.AS_NEEDED));
    return null;
  }

  @Override
  public <S> Void visit(Between between, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    renderer.renderExpression(between.getLeftExpression(),
        ctx.nested().inheritingPosition(BreakLine.NEVER));

    int subIndent = JSQLFormatter.getSubIndent(builder, false);

    JSQLFormatter.appendKeyWord(builder, fmt, "BETWEEN", between.isNot() ? " NOT " : " ",
        (between.isUsingSymmetric() ? " SYMMETRIC" : "")
            + (between.isUsingAsymmetric() ? " ASYMMETRIC " : " "));

    renderer.renderExpression(between.getBetweenExpressionStart(),
        ctx.nested().inheritingPosition(BreakLine.NEVER));

    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(subIndent + 1));
    JSQLFormatter.appendKeyWord(builder, fmt, "AND", " ", " ");

    renderer.renderExpression(between.getBetweenExpressionEnd(),
        ctx.nested().inheritingPosition(BreakLine.NEVER));
    return null;
  }

  @Override
  public <S> Void visit(ExistsExpression existsExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    if (existsExpression.isNot()) {
      JSQLFormatter.appendOperator(builder, fmt, "NOT EXISTS", "", "");
    } else {
      JSQLFormatter.appendOperator(builder, fmt, "EXISTS", "", " ");
    }

    renderer.renderExpression(existsExpression.getRightExpression(),
        ctx.nested().inheritingPosition(BreakLine.AFTER_FIRST));
    return null;
  }

  @Override
  public <S> Void visit(SignedExpression signedExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    JSQLFormatter.appendOperator(builder, JSQLFormatter.getOutputFormat(),
        String.valueOf(signedExpression.getSign()), "", " ");

    renderer.renderExpression(signedExpression.getExpression(),
        ctx.inheritingPosition(BreakLine.NEVER));
    return null;
  }

  /**
   * Catch-all for {@link BinaryExpression} subclasses not specifically overridden above. Mirrors
   * the legacy "abstract class, call last" branch — Addition, Subtraction, Multiplication, Concat,
   * GreaterThan, MinorThan, etc. funnel here.
   */
  @Override
  protected <S> Void visitBinaryExpression(BinaryExpression binaryExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    renderer.renderExpression(binaryExpression.getLeftExpression(),
        ctx.inheritingPosition(BreakLine.NEVER));

    if ((ctx.i > 0 || ctx.breakLine.equals(BreakLine.ALWAYS))
        && !ctx.breakLine.equals(BreakLine.NEVER)) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(ctx.indent + 1));
    }
    JSQLFormatter.appendOperator(builder, fmt, binaryExpression.getStringExpression(), " ", " ");

    renderer.renderExpression(binaryExpression.getRightExpression(),
        ctx.inheritingPosition(BreakLine.NEVER));
    return null;
  }

  // ===================================================================================
  // CASE / functions / sub-queries / row constructor
  // ===================================================================================

  @Override
  public <S> Void visit(CaseExpression caseExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();
    int subIndent = JSQLFormatter.getSubIndent(builder, false);

    JSQLFormatter.appendKeyWord(builder, fmt, "CASE", "", " ");
    if (caseExpression.getSwitchExpression() != null) {
      renderer.renderExpression(caseExpression.getSwitchExpression(),
          ctx.nested().inheritingPosition(BreakLine.NEVER));
    }

    List<WhenClause> whenClauses = caseExpression.getWhenClauses();
    for (WhenClause whenClause : whenClauses) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(subIndent));
      JSQLFormatter.appendKeyWord(builder, fmt, "WHEN", "", " ");
      renderer.renderExpression(whenClause.getWhenExpression(),
          FormatContext.of(subIndent + 1, 0, 1, false, BreakLine.AFTER_FIRST));

      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(subIndent + 1));
      JSQLFormatter.appendKeyWord(builder, fmt, "THEN", "", " ");
      renderer.renderExpression(whenClause.getThenExpression(),
          FormatContext.of(subIndent + 1, 0, 1, false, BreakLine.AFTER_FIRST));
    }

    Expression elseExpression = caseExpression.getElseExpression();
    if (elseExpression != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(subIndent));
      JSQLFormatter.appendKeyWord(builder, fmt, "ELSE", "", " ");
      renderer.renderExpression(elseExpression,
          FormatContext.of(subIndent + 1, 0, 1, false, BreakLine.AFTER_FIRST));
    }

    JSQLFormatter.appendNormalizedLineBreak(builder);
    builder.append(JSQLFormatter.getIndentString().repeat(subIndent));
    JSQLFormatter.appendKeyWord(builder, fmt, "END", "", "");
    return null;
  }

  @Override
  public <S> Void visit(NextValExpression nextValExpression, S context) {
    OutputFormat fmt = JSQLFormatter.getOutputFormat();
    if (nextValExpression.isUsingNextValueFor()) {
      JSQLFormatter.appendOperator(builder, fmt, "NEXT VALUE FOR", "", " ");
    } else {
      JSQLFormatter.appendOperator(builder, fmt, "NEXTVAL FOR", "", " ");
    }
    int j = 0;
    for (String name : nextValExpression.getNameList()) {
      if (j > 0) {
        builder.append(".");
      }
      JSQLFormatter.appendObjectName(builder, fmt, name, "", "");
      j++;
    }
    return null;
  }

  @Override
  public <S> Void visit(ExtractExpression extractExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    JSQLFormatter.appendKeyWord(builder, fmt, "EXTRACT", "", "( ");
    JSQLFormatter.appendValue(builder, fmt, extractExpression.getName(), "", "");
    JSQLFormatter.appendKeyWord(builder, fmt, "FROM", " ", " ");

    renderer.renderExpression(extractExpression.getExpression(),
        ctx.nested().inheritingPosition(BreakLine.AFTER_FIRST));
    JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, " )");
    return null;
  }

  @Override
  public <S> Void visit(Function function, S context) {
    // NOTE on GROUP_CONCAT: MySQLGroupConcat is a sibling of Function (it extends
    // ASTNodeAccessImpl), never a subclass, so this is not an accept()/static-dispatch
    // problem. The grammar simply stopped emitting MySQLGroupConcat: InternalFunction()
    // now parses the in-call ORDER BY into Function.orderByElements and the trailing
    // `SEPARATOR ','` into Function.keywordArguments. visit(MySQLGroupConcat) below is
    // therefore effectively dead for parsed input (kept for hand-built ASTs and older
    // JSqlParser versions) and appendFunctionExpression() has to handle both clauses.
    return appendFunctionExpression(function, (FormatContext) context);
  }

  /**
   * {@link TrimFunction} does NOT extend {@link Function} in JSqlParser 5.x, and the legacy
   * {@code appendExpression()} chain had no explicit {@code instanceof TrimFunction} branch — so it
   * fell through to the catch-all, which emitted {@code expression.toString()}. Without this
   * override the adapter's default fires, which walks the inner expression and emits nothing for
   * the function wrapper — producing {@code word} instead of {@code Trim( word )}. Mirror the
   * legacy fallback here.
   */
  @Override
  public <S> Void visit(TrimFunction trimFunction, S context) {
    return appendUnhandled(trimFunction);
  }

  /**
   * Mirrors the legacy {@code appendExpression()} unhandled-else branch — but with one upgrade: we
   * route through {@link Renderer#appendWithKeywordSpelling} so that even fall-through expressions
   * honour the configured {@code keywordSpelling} policy (LOWER / UPPER / CAMEL / KEEP). The AST
   * node's {@code toString()} is otherwise preserved verbatim, including identifiers, quoted
   * strings and structural punctuation.
   */
  private Void appendUnhandled(Expression expression) {
    Renderer.appendWithKeywordSpelling(builder, expression);
    return null;
  }

  /**
   * Defensive catch-all for leaf-node {@link Expression}s we haven't explicitly overridden. The
   * adapter routes many leaf nodes (e.g. {@code TimestampValue}, {@code BooleanValue}, {@code
   * HexValue}, {@code OracleHint}, {@code DateTimeLiteralExpression}, …) through {@code
   * applyExpression}, whose default returns {@code null} and emits nothing. The legacy code's
   * unhandled-else branch handled all of these via {@code builder.append(expression)}; we mirror
   * that here so previously-working leaves don't regress.
   */
  @Override
  protected <S> Void applyExpression(Expression expression, S context) {
    return appendUnhandled(expression);
  }

  /**
   * Renders a {@link Function} (or any subclass that should be formatted as a plain function call,
   * e.g. {@link TrimFunction}). Mirrors the legacy {@code instanceof Function} branch of {@code
   * appendExpression()}.
   */
  private Void appendFunctionExpression(Function function, FormatContext ctx) {
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    String name = function.getName();
    ExpressionList<?> parameters = function.getParameters();
    NamedExpressionList<?> namedParameters = function.getNamedParameters();
    boolean distinct = function.isDistinct();
    boolean allColumns = function.isAllColumns();
    boolean escaped = function.isEscaped();
    KeepExpression keep = function.getKeep();
    Object attribute = function.getAttribute();

    if (escaped) {
      JSQLFormatter.appendFunction(builder, fmt, "fn", " {", " ");
    }

    JSQLFormatter.appendFunction(builder, fmt, name, "", "");

    // In-call trailing clauses that JSqlParser 5.x hangs off the plain Function node:
    //
    // GROUP_CONCAT( DISTINCT expr ORDER BY … SEPARATOR ',' )
    // ^ orderByElements ------------^ ^ keywordArguments ---^
    //
    // Since the grammar stopped producing MySQLGroupConcat and started routing
    // GROUP_CONCAT/LISTAGG/STRING_AGG through InternalFunction(), both of these land
    // here. They are rendered on their own lines, aligned to the tab stop immediately
    // after the opening parenthesis — so that alignment has to be captured BEFORE the
    // parameters are appended, otherwise getSubIndent() measures the already-emitted
    // parameter text and pushes the clauses far off to the right.
    List<OrderByElement> functionOrderBy = function.getOrderByElements();
    List<Function.KeywordArgument> keywordArguments = function.getKeywordArguments();
    boolean hasInCallClauses = functionOrderBy != null && !functionOrderBy.isEmpty()
        || keywordArguments != null && !keywordArguments.isEmpty();

    if (parameters != null || namedParameters != null) {
      if (parameters != null) {
        builder.append("( ");

        // Only pad to the tab stop when there actually are clauses to align; plain
        // function calls must keep emitting "Func( a, b )" without extra padding.
        int subIndent = hasInCallClauses ? JSQLFormatter.getSubIndent(builder, true) : ctx.indent;

        if (distinct) {
          JSQLFormatter.appendKeyWord(builder, fmt, "DISTINCT", "", " ");
        } else if (allColumns) {
          JSQLFormatter.appendKeyWord(builder, fmt, "ALL", "", " ");
        }

        if (name.equalsIgnoreCase("Decode")) {
          renderer.appendDecodeExpressionsList(parameters, BreakLine.AS_NEEDED, ctx.indent);
        } else {
          renderer.appendExpressionList(parameters,
              ctx.withIndent(subIndent).withBreakLine(BreakLine.AS_NEEDED));
        }

        if (functionOrderBy != null && !functionOrderBy.isEmpty()) {
          renderer.appendOrderByElements(functionOrderBy, subIndent);
        }

        // Generic `KEYWORD value` arguments: SEPARATOR ',' for GROUP_CONCAT,
        // but also PATH '…', COST MODEL USING …, etc.
        if (keywordArguments != null) {
          for (Function.KeywordArgument keywordArgument : keywordArguments) {
            JSQLFormatter.appendNormalizedLineBreak(builder);
            builder.append(JSQLFormatter.getIndentString().repeat(Math.max(0, subIndent)));
            JSQLFormatter.appendKeyWord(builder, fmt, keywordArgument.getKeyword(), "", " ");
            renderer.renderExpression(keywordArgument.getExpression(),
                FormatContext.of(subIndent).withBreakLine(BreakLine.NEVER));
          }
        }

        JSQLFormatter.appendNormalizingTrailingWhiteSpace(builder, " )");
      } else {
        // @todo: implement this properly and add a test case
        builder.append(namedParameters);
      }
    } else if (allColumns) {
      builder.append("( * )");
    } else {
      builder.append("()");
    }

    if (attribute != null) {
      builder.append(".").append(attribute);
    }

    if (keep != null) {
      builder.append(" ").append(keep);
    }

    if (escaped) {
      builder.append("} ");
    }
    return null;
  }

  @Override
  public <S> Void visit(Select select, S context) {
    FormatContext ctx = (FormatContext) context;
    renderer.renderSelect(select, ctx.withSelectFraming(false, false));
    return null;
  }

  @Override
  public <S> Void visit(RowConstructor<?> rowConstructor, S context) {
    FormatContext ctx = (FormatContext) context;
    renderer.appendRowConstructor(ctx.indent, rowConstructor);
    return null;
  }

  @Override
  public <S> Void visit(MySQLGroupConcat mySQLGroupConcat, S context) {
    OutputFormat fmt = JSQLFormatter.getOutputFormat();
    JSQLFormatter.appendFunction(builder, fmt, "GROUP_CONCAT", "", "( ");

    int subIndent = JSQLFormatter.getSubIndent(builder, true);

    if (mySQLGroupConcat.isDistinct()) {
      JSQLFormatter.appendKeyWord(builder, fmt, "DISTINCT", "", " ");
    }
    renderer.appendExpressionsList(mySQLGroupConcat.getExpressionList(),
        FormatContext.of(subIndent).withBreakLine(BreakLine.AS_NEEDED));
    List<OrderByElement> orderByElements = mySQLGroupConcat.getOrderByElements();
    renderer.appendOrderByElements(orderByElements, subIndent);

    String separator = mySQLGroupConcat.getSeparator();
    if (separator != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      for (int j = 0; j < subIndent; j++) {
        builder.append(JSQLFormatter.getIndentString());
      }
      JSQLFormatter.appendKeyWord(builder, fmt, "SEPARATOR", "", " " + separator);
    }
    builder.append(" )");
    return null;
  }

  // ===================================================================================
  // Lists, AllColumns, AllTableColumns
  // ===================================================================================

  @Override
  public <S> Void visit(ExpressionList<? extends Expression> expressionList, S context) {
    FormatContext ctx = (FormatContext) context;
    renderer.appendExpressionList(expressionList, ctx.withBreakLine(BreakLine.AS_NEEDED));
    return null;
  }

  @Override
  public <S> Void visit(AllColumns allColumns, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();
    JSQLFormatter.appendObjectName(builder, fmt, "*", "", "");

    if (allColumns.getExceptColumns() != null && !allColumns.getExceptColumns().isEmpty()) {
      JSQLFormatter.appendKeyWord(builder, fmt, allColumns.getExceptKeyword(), " ", "( ");
      renderer.appendExpressionsList(allColumns.getExceptColumns(),
          FormatContext.of(ctx.indent).withBreakLine(BreakLine.AS_NEEDED));
      builder.append(" )");
    }

    if (allColumns.getReplaceExpressions() != null
        && !allColumns.getReplaceExpressions().isEmpty()) {
      JSQLFormatter.appendKeyWord(builder, fmt, "REPLACE", " ", "( ");
      int subIndent =
          JSQLFormatter.getSubIndent(builder, allColumns.getReplaceExpressions().size() > 3);
      renderer.appendSelectItemList(allColumns.getReplaceExpressions(),
          FormatContext.of(subIndent, ctx.i, 1, false, BreakLine.AS_NEEDED));
      builder.append(" )");
    }
    return null;
  }

  @Override
  public <S> Void visit(AllTableColumns allTableColumns, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();
    JSQLFormatter.appendObjectName(builder, fmt, allTableColumns.getTable().getFullyQualifiedName(),
        "", ".*");

    if (allTableColumns.getExceptColumns() != null
        && !allTableColumns.getExceptColumns().isEmpty()) {
      JSQLFormatter.appendKeyWord(builder, fmt, allTableColumns.getExceptKeyword(), " ", "( ");
      renderer.appendExpressionsList(allTableColumns.getExceptColumns(),
          FormatContext.of(ctx.indent).withBreakLine(BreakLine.AS_NEEDED));
      builder.append(" )");
    }

    if (allTableColumns.getReplaceExpressions() != null
        && !allTableColumns.getReplaceExpressions().isEmpty()) {
      JSQLFormatter.appendKeyWord(builder, fmt, "REPLACE", " ", "( ");
      int subIndent =
          JSQLFormatter.getSubIndent(builder, allTableColumns.getReplaceExpressions().size() > 3);
      renderer.appendSelectItemList(allTableColumns.getReplaceExpressions(),
          FormatContext.of(subIndent, ctx.i, 1, false, BreakLine.AS_NEEDED));
      builder.append(" )");
    }
    return null;
  }

  // ===================================================================================
  // Type expressions
  // ===================================================================================

  @Override
  public <S> Void visit(IntervalExpression intervalExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();
    if (intervalExpression.isUsingIntervalKeyword()) {
      JSQLFormatter.appendKeyWord(builder, fmt, "INTERVAL", "", " ");
    }
    if (intervalExpression.getExpression() != null) {
      // legacy passes parent's breakLine through
      renderer.renderExpression(intervalExpression.getExpression(),
          ctx.inheritingPosition(ctx.breakLine));
    } else {
      JSQLFormatter.appendValue(builder, fmt, intervalExpression.getParameter(), "", "");
    }
    if (intervalExpression.getIntervalType() != null) {
      JSQLFormatter.appendKeyWord(builder, fmt, intervalExpression.getIntervalType(), " ", "");
    }
    return null;
  }

  @Override
  public <S> Void visit(CastExpression castExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    if (castExpression.isImplicitCast()) {
      JSQLFormatter.appendKeyWord(builder, fmt, castExpression.getColDataType().toString(), "",
          " ");
      renderer.renderExpression(castExpression.getLeftExpression(),
          ctx.inheritingPosition(BreakLine.NEVER));
    } else if (castExpression.isUseCastKeyword()) {
      if (castExpression.getColumnDefinitions().size() > 1) {
        JSQLFormatter.appendFunction(builder, fmt, castExpression.keyword, " ", "( ");
        renderer.renderExpression(castExpression.getLeftExpression(),
            ctx.inheritingPosition(BreakLine.NEVER));
        JSQLFormatter.appendKeyWord(builder, fmt, "AS ROW ( ", " ", " ");
        int j = 0;
        for (net.sf.jsqlparser.statement.create.table.ColumnDefinition columnDefinition : castExpression
            .getColumnDefinitions()) {
          if (j++ > 0) {
            builder.append(", ");
          }
          JSQLFormatter.appendKeyWord(builder, fmt, columnDefinition.toString(), "", "");
        }
      } else {
        JSQLFormatter.appendFunction(builder, fmt, castExpression.keyword, " ", "( ");
        renderer.renderExpression(castExpression.getLeftExpression(),
            ctx.inheritingPosition(BreakLine.NEVER));
        JSQLFormatter.appendKeyWord(builder, fmt,
            "AS " + castExpression.getColDataType().toString(), " ", " )");
      }
    } else {
      renderer.renderExpression(castExpression.getLeftExpression(),
          ctx.inheritingPosition(BreakLine.NEVER));
      JSQLFormatter.appendKeyWord(builder, fmt, castExpression.getColDataType().toString(), "::",
          "");
    }
    return null;
  }

  @Override
  public <S> Void visit(ArrayConstructor arrayConstructor, S context) {
    OutputFormat fmt = JSQLFormatter.getOutputFormat();
    if (arrayConstructor.isArrayKeyword()) {
      JSQLFormatter.appendKeyWord(builder, fmt, "ARRAY", " ", "");
    }

    boolean multiline = false;
    for (Expression p : arrayConstructor.getExpressions()) {
      if (p instanceof ArrayConstructor || p instanceof ExpressionList || p instanceof Select
          || p instanceof StructType) {
        multiline = true;
        break;
      }
    }

    int subIndent = JSQLFormatter.getSubIndent(builder, true);
    builder.append("[ ");
    if (multiline) {
      renderer.appendExpressionList(arrayConstructor.getExpressions(),
          FormatContext.of(subIndent).withBreakLine(BreakLine.ALWAYS));
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(subIndent));
    } else {
      renderer.appendExpressionList(arrayConstructor.getExpressions(),
          FormatContext.of(subIndent).withBreakLine(BreakLine.NEVER));
    }
    builder.append("]");
    return null;
  }

  @Override
  public <S> Void visit(TranscodingFunction transcodingFunction, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();
    JSQLFormatter.appendFunction(builder, fmt, "Convert", "", "(");
    if (transcodingFunction.isTranscodeStyle()) {
      renderer.renderExpression(transcodingFunction.getExpression(),
          ctx.asScalar(BreakLine.AS_NEEDED));
      JSQLFormatter.appendKeyWord(builder, fmt, "USING", " ", "");
      JSQLFormatter.appendKeyWord(builder, fmt, transcodingFunction.getTranscodingName(), " ",
          " )");
    } else {
      JSQLFormatter.appendKeyWord(builder, fmt, transcodingFunction.getColDataType().toString(),
          " ", ", ");
      renderer.renderExpression(transcodingFunction.getExpression(),
          ctx.asScalar(BreakLine.AS_NEEDED));

      String transcodingName = transcodingFunction.getTranscodingName();
      if (transcodingName != null && !transcodingName.isEmpty()) {
        JSQLFormatter.appendKeyWord(builder, fmt, transcodingName, ", ", " )");
      } else {
        builder.append(" )");
      }
    }
    return null;
  }

  @Override
  public <S> Void visit(AnalyticExpression analyticExpression, S context) {
    FormatContext ctx = (FormatContext) context;
    OutputFormat fmt = JSQLFormatter.getOutputFormat();

    int subIndent = JSQLFormatter.getSubIndent(builder, false);
    JSQLFormatter.appendFunction(builder, fmt, analyticExpression.getName(), "", "( ");
    if (analyticExpression.isDistinct()) {
      JSQLFormatter.appendKeyWord(builder, fmt, "DISTINCT", "", " ");
    }

    Expression expr = analyticExpression.getExpression();
    if (expr != null) {
      renderer.renderExpression(expr, ctx.asScalar(BreakLine.NEVER));
      if (analyticExpression.getOffset() != null) {
        builder.append(", ");
        renderer.renderExpression(analyticExpression.getOffset(),
            ctx.asScalar(BreakLine.NEVER).withCommaSeparated(true));
        if (analyticExpression.getDefaultValue() != null) {
          builder.append(", ");
          renderer.renderExpression(analyticExpression.getDefaultValue(),
              ctx.asScalar(BreakLine.NEVER).withCommaSeparated(true));
        }
      }
    } else if (analyticExpression.isAllColumns()) {
      builder.append("*");
    }

    if (analyticExpression.getHavingClause() != null) {
      analyticExpression.getHavingClause().appendTo(builder);
    }

    if (analyticExpression.getNullHandling() != null) {
      switch (analyticExpression.getNullHandling()) {
        case IGNORE_NULLS:
          builder.append(" IGNORE NULLS");
          break;
        case RESPECT_NULLS:
          builder.append(" RESPECT NULLS");
      }
    }

    if (analyticExpression.getFuncOrderBy() != null) {
      builder.append(" ORDER BY ");
      builder.append(analyticExpression.getFuncOrderBy().stream().map(OrderByElement::toString)
          .collect(Collectors.joining(", ")));
    }

    if (analyticExpression.getLimit() != null) {
      builder.append(analyticExpression.getLimit());
    }
    builder.append(" ) ");

    if (analyticExpression.getKeep() != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(subIndent + 1));
      builder.append(analyticExpression.getKeep()).append(" ");
    }

    if (analyticExpression.getFilterExpression() != null) {
      JSQLFormatter.appendNormalizedLineBreak(builder);
      builder.append(JSQLFormatter.getIndentString().repeat(subIndent + 1));
      builder.append("FILTER ( WHERE ");
      builder.append(analyticExpression.getFilterExpression());
      builder.append(" )");
      if (analyticExpression.getType() != AnalyticType.FILTER_ONLY) {
        builder.append(" ");
      }
    }

    if (analyticExpression.isIgnoreNullsOutside()) {
      builder.append("IGNORE NULLS ");
    }

    switch (analyticExpression.getType()) {
      case FILTER_ONLY:
        return null;
      case WITHIN_GROUP:
        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(subIndent + 1));
        builder.append("WITHIN GROUP");
        break;
      case WITHIN_GROUP_OVER:
        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(subIndent + 1));
        builder.append("WITHIN GROUP ( ");
        analyticExpression.getWindowDefinition().getOrderBy().toStringOrderByElements(builder);
        builder.append(" ) OVER ( ");
        analyticExpression.getWindowDefinition().getPartitionBy().toStringPartitionBy(builder);
        builder.append(" )");
        break;
      default:
        JSQLFormatter.appendNormalizedLineBreak(builder);
        builder.append(JSQLFormatter.getIndentString().repeat(subIndent + 1));
        builder.append("OVER");
    }

    if (analyticExpression.getWindowName() != null) {
      builder.append(" ").append(analyticExpression.getWindowName());
    } else if (analyticExpression.getType() != AnalyticType.WITHIN_GROUP_OVER) {
      builder.append(" ");
      builder.append(analyticExpression.getWindowDefinition());
    }
    return null;
  }

  // ════════════════════════════════════════════════════════════════════════════════════════
  // Catch-all overrides — every adapter slot we haven't explicitly handled.
  //
  // The legacy procedural code's appendExpression() chain ended in:
  // } else {
  // LOGGER.warning("Unhandled expression: " + ...);
  // builder.append(expression);
  // }
  // i.e. any unhandled Expression got passed through via its toString().
  //
  // The visitor adapter's defaults DON'T do that — for many slots they walk into child
  // expressions and emit nothing for the wrapper, dropping the function name / parens /
  // operator entirely (the TrimFunction → "word" bug). Below we override every remaining
  // adapter slot to mirror the legacy fallback. If a future JSqlParser version adds a new
  // slot we don't list here, that slot's adapter default will run — to stay safe, keep
  // this list in sync with the {@link ExpressionVisitorAdapter} source whenever JSqlParser
  // is bumped.
  //
  // Slots that route through applyExpression() in the adapter are already covered by our
  // applyExpression override above (most leaf values: HexValue, OracleHint, BooleanValue,
  // TimeValue, TimestampValue, DateTimeLiteralExpression, AnyComparisonExpression,
  // UserVariable, NumericBind, AllValue, FunctionAllColumns, …). They're listed here for
  // belt-and-suspenders since some JSqlParser versions reroute them.
  //
  // Slots that route through visitBinaryExpression in the adapter are already covered by
  // our visitBinaryExpression override (Addition, GreaterThan, Concat, Plus, …). We don't
  // re-list them here.
  // ════════════════════════════════════════════════════════════════════════════════════════

  // ── Leaf values ────────────────────────────────────────────────────────────────────────
  // These all collapse to "render the toString as a value/keyword via the spelling helpers"
  // rather than going through the smart fallback. The AST node's toString IS the literal
  // representation; we just route it through the appropriate spelling channel.

  @Override
  public <S> Void visit(TimeValue timeValue, S context) {
    JSQLFormatter.appendValue(builder, JSQLFormatter.getOutputFormat(), timeValue.toString(), "",
        "");
    return null;
  }

  @Override
  public <S> Void visit(TimestampValue timestampValue, S context) {
    JSQLFormatter.appendValue(builder, JSQLFormatter.getOutputFormat(), timestampValue.toString(),
        "", "");
    return null;
  }

  @Override
  public <S> Void visit(BooleanValue booleanValue, S context) {
    // toString() yields "TRUE" or "FALSE" — both are reserved keywords.
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), booleanValue.toString(),
        "", "");
    return null;
  }

  @Override
  public <S> Void visit(HexValue hexValue, S context) {
    JSQLFormatter.appendValue(builder, JSQLFormatter.getOutputFormat(), hexValue.toString(), "",
        "");
    return null;
  }

  @Override
  public <S> Void visit(OracleHint oracleHint, S context) {
    JSQLFormatter.appendHint(builder, JSQLFormatter.getOutputFormat(), oracleHint.toString(), "",
        "");
    return null;
  }

  @Override
  public <S> Void visit(DateTimeLiteralExpression dt, S context) {
    // toString is "{ts '...'}" / "{d '...'}" / "{t '...'}" / "DATE '...'" etc. — keep verbatim
    // (most of the structural tokens are punctuation, not keywords). Smart fallback applies
    // keyword spelling to the leading DATE/TIME/TIMESTAMP tag if present.
    Renderer.appendWithKeywordSpelling(builder, dt);
    return null;
  }

  @Override
  public <S> Void visit(AnyComparisonExpression any, S context) {
    return appendUnhandled(any);
  }

  @Override
  public <S> Void visit(UserVariable userVariable, S context) {
    return appendUnhandled(userVariable);
  }

  @Override
  public <S> Void visit(NumericBind numericBind, S context) {
    return appendUnhandled(numericBind);
  }

  @Override
  public <S> Void visit(AllValue allValue, S context) {
    JSQLFormatter.appendKeyWord(builder, JSQLFormatter.getOutputFormat(), "ALL", "", "");
    return null;
  }

  @Override
  public <S> Void visit(FunctionAllColumns fac, S context) {
    return appendUnhandled(fac);
  }

  @Override
  public <S> Void visit(RowGetExpression rowGet, S context) {
    return appendUnhandled(rowGet);
  }

  // Wrapper expressions whose adapter default walks the inner expression and silently
  // strips the wrapper. These are the high-risk ones that produced bugs like the
  // TrimFunction → "word" regression.
  @Override
  public <S> Void visit(JsonExpression jsonExpression, S context) {
    return appendUnhandled(jsonExpression);
  }

  @Override
  public <S> Void visit(KeepExpression keepExpression, S context) {
    return appendUnhandled(keepExpression);
  }

  @Override
  public <S> Void visit(CollateExpression collate, S context) {
    return appendUnhandled(collate);
  }

  @Override
  public <S> Void visit(TimezoneExpression tz, S context) {
    return appendUnhandled(tz);
  }

  @Override
  public <S> Void visit(ArrayExpression arrayExpression, S context) {
    return appendUnhandled(arrayExpression);
  }

  @Override
  public <S> Void visit(VariableAssignment va, S context) {
    return appendUnhandled(va);
  }

  @Override
  public <S> Void visit(XMLSerializeExpr xml, S context) {
    return appendUnhandled(xml);
  }

  @Override
  public <S> Void visit(LambdaExpression lambda, S context) {
    return appendUnhandled(lambda);
  }

  @Override
  public <S> Void visit(StructType structType, S context) {
    return appendUnhandled(structType);
  }

  @Override
  public <S> Void visit(KeyExpression key, S context) {
    return appendUnhandled(key);
  }

  @Override
  public <S> Void visit(OracleNamedFunctionParameter onp, S context) {
    return appendUnhandled(onp);
  }

  @Override
  public <S> Void visit(PostgresNamedFunctionParameter pnp, S context) {
    return appendUnhandled(pnp);
  }

  @Override
  public <S> Void visit(ConnectByRootOperator op, S context) {
    return appendUnhandled(op);
  }

  @Override
  public <S> Void visit(ConnectByPriorOperator op, S context) {
    return appendUnhandled(op);
  }

  @Override
  public <S> Void visit(JsonAggregateFunction jaf, S context) {
    return appendUnhandled(jaf);
  }

  @Override
  public <S> Void visit(JsonFunction jf, S context) {
    return appendUnhandled(jf);
  }

  @Override
  public <S> Void visit(WhenClause whenClause, S context) {
    return appendUnhandled(whenClause);
  }

  // Predicate / multi-child expressions — adapter walks the operands, dropping the
  // operator wrapper. Same risk profile as the function wrappers above.
  @Override
  public <S> Void visit(IsBooleanExpression isBool, S context) {
    return appendUnhandled(isBool);
  }

  @Override
  public <S> Void visit(IsUnknownExpression isUnknown, S context) {
    return appendUnhandled(isUnknown);
  }

  @Override
  public <S> Void visit(MemberOfExpression mof, S context) {
    return appendUnhandled(mof);
  }

  @Override
  public <S> Void visit(IncludesExpression includes, S context) {
    return appendUnhandled(includes);
  }

  @Override
  public <S> Void visit(ExcludesExpression excludes, S context) {
    return appendUnhandled(excludes);
  }

  @Override
  public <S> Void visit(FullTextSearch fts, S context) {
    return appendUnhandled(fts);
  }

  @Override
  public <S> Void visit(OracleHierarchicalExpression ohe, S context) {
    return appendUnhandled(ohe);
  }

  @Override
  public <S> Void visit(OverlapsCondition oc, S context) {
    return appendUnhandled(oc);
  }

  @Override
  public <S> Void visit(RangeExpression range, S context) {
    return appendUnhandled(range);
  }

  @Override
  public <S> Void visit(CosineSimilarity cs, S context) {
    return appendUnhandled(cs);
  }

  // Subselect-as-expression. ParenthesedSelect's adapter default delegates to
  // visit((Select)..., ctx) and then recurses into pivot — which loses the parens framing
  // and Manticore-specific indent handling. Route to renderSelect with the outer
  // single-line framing so a scalar subquery prints inline.
  @Override
  public <S> Void visit(ParenthesedSelect parenthesedSelect, S context) {
    FormatContext ctx = (FormatContext) context;
    renderer.renderSelect(parenthesedSelect, ctx.withSelectFraming(false, false));
    return null;
  }

  // PivotVisitor / SelectItemVisitor slots — the adapter implements these too, so the
  // visitor type checker requires them. The legacy code rendered Pivot/UnPivot/PivotXml
  // via their toString() (e.g. builder.append(plainSelect.getPivot())); SelectItems are
  // never dispatched through this slot in our pipeline (renderer.appendSelectItemList
  // iterates them explicitly), but we override defensively. Route through smart fallback
  // so keyword spelling is honoured.
  @Override
  public <S> Void visit(Pivot pivot, S context) {
    Renderer.appendWithKeywordSpelling(builder, pivot);
    return null;
  }

  @Override
  public <S> Void visit(PivotXml pivotXml, S context) {
    Renderer.appendWithKeywordSpelling(builder, pivotXml);
    return null;
  }

  @Override
  public <S> Void visit(UnPivot unPivot, S context) {
    Renderer.appendWithKeywordSpelling(builder, unPivot);
    return null;
  }

  @Override
  public <S> Void visit(SelectItem<? extends Expression> selectItem, S context) {
    // If we ever end up here, the legacy unhandled-else fallback would have called
    // selectItem.toString(), which JSqlParser implements as "<expression> [AS] <alias>".
    // Smart fallback preserves the AS keyword's spelling.
    Renderer.appendWithKeywordSpelling(builder, selectItem);
    return null;
  }
}
