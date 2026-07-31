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

/**
 * Immutable context object pushed down through the AST during formatting.
 *
 * <p>
 * Replaces the (indent, i, n, commaSeparated, breakLine, alias, ...) parameter chains that the
 * legacy procedural formatter threaded through every render method. A visitor receives a single
 * {@code FormatContext} argument and derives child contexts via the {@code with*} factory methods
 * before recursing.
 *
 * <p>
 * Instances are immutable. {@code with*} methods return new instances; the caller's context is
 * never mutated. This guarantees that sibling AST nodes always see the same context their parent
 * intended.
 *
 * @author <a href="mailto:andreas@manticore-projects.com">Andreas Reichel</a>
 */
public final class FormatContext {

  /**
   * The current indentation depth in tab-stops (each tab is {@code indentString.length()} chars).
   */
  public final int indent;

  /** Position of the current node within its parent list (0-based). For non-list contexts use 0. */
  public final int i;

  /** Total number of siblings in the parent list. For non-list contexts use 1. */
  public final int n;

  /**
   * When {@code true}, the renderer emits commas between siblings according to the global
   * Separation.
   */
  public final boolean commaSeparated;

  /** Line-break policy for the current rendering context. */
  public final BreakLine breakLine;

  /** Alias to render after the expression body (used by select items and similar). */
  public final Alias alias;

  /**
   * For Select rendering: whether to emit a leading line break before the SELECT keyword. Carries
   * the legacy {@code breakLineBefore} parameter through the visitor.
   */
  public final boolean breakLineBefore;

  /**
   * For Select rendering: whether to indent the first line. Carries the legacy
   * {@code indentFirstLine} parameter through the visitor.
   */
  public final boolean indentFirstLine;

  private FormatContext(int indent, int i, int n, boolean commaSeparated, BreakLine breakLine,
      Alias alias, boolean breakLineBefore, boolean indentFirstLine) {
    this.indent = indent;
    this.i = i;
    this.n = n;
    this.commaSeparated = commaSeparated;
    this.breakLine = breakLine;
    this.alias = alias;
    this.breakLineBefore = breakLineBefore;
    this.indentFirstLine = indentFirstLine;
  }

  /** Root context: indent 0, single-element list, no break, no alias. */
  public static FormatContext root() {
    return new FormatContext(0, 0, 1, false, BreakLine.NEVER, null, false, false);
  }

  /** Convenience constructor for a single-element rendering at the given indent. */
  public static FormatContext of(int indent) {
    return new FormatContext(indent, 0, 1, false, BreakLine.NEVER, null, false, false);
  }

  /** Convenience constructor matching the legacy {@code appendExpression} call site. */
  public static FormatContext of(int indent, int i, int n, boolean commaSeparated,
      BreakLine breakLine) {
    return new FormatContext(indent, i, n, commaSeparated, breakLine, null, false, false);
  }

  // -------------------- with-methods (return new instances) ---------------------------

  public FormatContext withIndent(int newIndent) {
    return new FormatContext(newIndent, i, n, commaSeparated, breakLine, alias, breakLineBefore,
        indentFirstLine);
  }

  /** Equivalent to {@code withIndent(indent + 1)}. */
  public FormatContext nested() {
    return withIndent(indent + 1);
  }

  public FormatContext atPosition(int newI, int newN) {
    return new FormatContext(indent, newI, newN, commaSeparated, breakLine, alias, breakLineBefore,
        indentFirstLine);
  }

  public FormatContext withBreakLine(BreakLine newBreakLine) {
    return new FormatContext(indent, i, n, commaSeparated, newBreakLine, alias, breakLineBefore,
        indentFirstLine);
  }

  public FormatContext withCommaSeparated(boolean newCommaSeparated) {
    return new FormatContext(indent, i, n, newCommaSeparated, breakLine, alias, breakLineBefore,
        indentFirstLine);
  }

  public FormatContext withAlias(Alias newAlias) {
    return new FormatContext(indent, i, n, commaSeparated, breakLine, newAlias, breakLineBefore,
        indentFirstLine);
  }

  public FormatContext withSelectFraming(boolean newBreakLineBefore, boolean newIndentFirstLine) {
    return new FormatContext(indent, i, n, commaSeparated, breakLine, alias, newBreakLineBefore,
        newIndentFirstLine);
  }

  /**
   * Returns a context appropriate for rendering an inner expression that should not behave as a
   * list element. Resets position to (0, 1), clears the alias, and disables comma separation.
   */
  public FormatContext asScalar(BreakLine newBreakLine) {
    return new FormatContext(indent, 0, 1, false, newBreakLine, null, breakLineBefore,
        indentFirstLine);
  }

  /**
   * Returns a context appropriate for rendering an inner expression that inherits the parent's list
   * position (i, n) — used by AndExpression, OrExpression, BinaryExpression sub-expressions that
   * need to know whether they are at the head of an outer list.
   */
  public FormatContext inheritingPosition(BreakLine newBreakLine) {
    return new FormatContext(indent, i, n, false, newBreakLine, null, breakLineBefore,
        indentFirstLine);
  }
}
