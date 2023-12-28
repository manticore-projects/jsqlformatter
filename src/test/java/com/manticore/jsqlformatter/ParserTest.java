/**
 * Manticore Projects JSQLFormatter is a SQL Beautifying and Formatting Software.
 * Copyright (C) 2022 Andreas Reichel <andreas@manticore-projects.com>
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

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

public class ParserTest {
  Logger LOGGER = Logger.getLogger(ParserTest.class.getName());

  @Test
  public void testExpressionClassInstances() throws JSQLParserException {
    String sqlStr =
        "SELECT * FROM test WHERE aaa = 1 or bbb = 2 and case when ccc = 3 then 4 else 5 end = 6";
    Statement statement = CCJSqlParserUtil.parse(sqlStr);

    if (statement instanceof Select) {
      Select select = (Select) statement;

      if (select instanceof PlainSelect) {
        PlainSelect plainSelect = (PlainSelect) select;
        Expression whereExpression = plainSelect.getWhere();

        if (whereExpression instanceof AndExpression) {
          AndExpression andExpression = (AndExpression) whereExpression;
          LOGGER.info("Found AND Expression: " + andExpression);
        } else if (whereExpression instanceof OrExpression) {
          OrExpression orExpression = (OrExpression) whereExpression;
          LOGGER.info("Found OR Expression: " + orExpression);
        }
      }
    }
  }

  @Test
  public void testExpressionVisitor() throws JSQLParserException {
    String sqlStr =
        "SELECT * FROM test WHERE aaa = 1 or bbb = 2 and case when ccc = 3 then 4 else 5 end = 6";
    Statement statement = CCJSqlParserUtil.parse(sqlStr);

    ExpressionVisitorAdapter expressionVisitorAdapter = new ExpressionVisitorAdapter() {
      @Override
      public void visit(OrExpression orExpression) {
        super.visit(orExpression);
        LOGGER.info("Found OR Expression: " + orExpression);
      }

      @Override
      public void visit(AndExpression andExpression) {
        super.visit(andExpression);
        LOGGER.info("Found AND Expression: " + andExpression);
      }
    };

    SelectVisitorAdapter selectVisitorAdapter = new SelectVisitorAdapter() {
      @Override
      public void visit(PlainSelect plainSelect) {
        plainSelect.getWhere().accept(expressionVisitorAdapter);
      }
    };

    StatementVisitorAdapter statementVisitorAdapter = new StatementVisitorAdapter() {
      @Override
      public void visit(Select select) {
        select.accept(selectVisitorAdapter);
      }
    };

    statement.accept(statementVisitorAdapter);
  }
}
