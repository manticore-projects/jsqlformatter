package com.manticore.jsqlformatter;

import org.junit.Test;

public class ASTVisualizationTest {

  @Test
  public void printAsciiAST() throws Exception {
    String sqlString = "select 1 from dual where a=b;";

    System.out.println(JSQLFormatter.formatToTree(sqlString));

    System.out.println(JSQLFormatter.formatToTree(sqlString, "outputFormat=ANSI"));

  }
}
