package com.manticore.jsqlformatter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormatTest {
  @Test
  void testStatementTerminator() throws Exception {
    String provided = "Select 1";
    String expected = "SELECT 1";

    String result = JSQLFormatter.format(provided, "statementTerminator=NONE");
    Assertions.assertEquals(expected, result);
  }
}
