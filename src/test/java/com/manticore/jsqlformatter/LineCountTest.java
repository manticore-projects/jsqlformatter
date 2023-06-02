package com.manticore.jsqlformatter;

import org.junit.jupiter.api.Test;

public class LineCountTest {

  @Test
  public void lineCountTest() throws Exception {
    String sqlString = "select 1,2,3 from dual where a=b and b=c;";
    System.out.println(JSQLFormatter.format(sqlString, "showLineNumbers=YES"));
    System.out.println(JSQLFormatter.format(sqlString, "outputFormat=ANSI"));
  }

  @Test
  public void lineCountTestHtml() throws Exception {
    String sqlString = "select 1,2,3 from dual where a=b and b=c;";
    System.out.println(JSQLFormatter.format(sqlString, "showLineNumbers=YES"));
    System.out.println(JSQLFormatter.format(sqlString, "outputFormat=HTML"));
  }
}
