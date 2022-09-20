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


import blazing.chain.LZSEncoding;
import net.sf.jsqlparser.schema.Column;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;

public class ASTVisualizationTest {

  @Test
  public void printAsciiAST() throws Exception {
    String sqlString = "select 1 from dual where a=b;";

    System.out.println(JSQLFormatter.formatToTree(sqlString));

    System.out.println(JSQLFormatter.formatToTree(sqlString, "outputFormat=ANSI"));

  }

  @Test
  public void testCollections() throws Exception {
    String sqlString = "select 1,2,3 from dual where a=b and b=c;";

    System.out.println(JSQLFormatter.formatToTree(sqlString));

    System.out.println(JSQLFormatter.formatToTree(sqlString, "outputFormat=ANSI"));

  }

  @Test
  public void testXML() throws Exception {
    String sqlString = "select 1 as b, 2, 3 from dual as tablename where a=b and b=c;";

    System.out.println(JSQLFormatter.formatToXML(sqlString));


  }

  @Test
  public void testGetXPath() throws Exception {
    String sqlString = "select 1 as b, 2, 3 from dual as tablename where a=b and b=c;";
    String xmlStr = JSQLFormatter.formatToXML(sqlString);
    System.out.println(xmlStr);
    System.out.println(FragmentContentHandler.getXPath(xmlStr, null));


  }

  @Test
  public void testXPath() throws Exception {
    String sqlString = "select 1 as b, 2, 3 from dual as tablename where a=b and b=c;";

    // return ANY column of the SELECT statement
    Collection<Column> columns = JSQLFormatter.extract(sqlString, Column.class, "//Column");
    for (Column column : columns) {
      System.out.println("Found ALL column: " + column);
    }

    // return only columns part of the WHERE clause on the Left side of the EQUALSTO
    columns = JSQLFormatter.extract(sqlString, Column.class, "//where/leftExpression/Column");
    for (Column column : columns) {
      System.out.println("Found WHERE column: " + column);
    }

    // return only the C column based on the complete XPath
    columns = JSQLFormatter.extract(sqlString, Column.class,
        "/Statements/selectBody/where/rightExpression/Column[2]");
    for (Column column : columns) {
      System.out.println("Found specific column by complete XPath: " + column);
    }
  }

  @Test
  public void testSerialization() throws IOException, ClassNotFoundException {
    Object object = new BigDecimal("2345.287272");

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutput objectOutput = new ObjectOutputStream(byteArrayOutputStream);
    objectOutput.writeObject(object);
    objectOutput.flush();
    objectOutput.close();
    byteArrayOutputStream.flush();

    String serializedObjectStr =
        new String(byteArrayOutputStream.toByteArray(), StandardCharsets.ISO_8859_1);

    String lzsEncodedBase64 = LZSEncoding.compressToBase64(serializedObjectStr);
    String base64Encoded = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());

    System.out.println(serializedObjectStr + "\n" + lzsEncodedBase64 + "\n" + base64Encoded);

    // verify Base64 Encoder
    byte[] bytes = serializedObjectStr.getBytes(StandardCharsets.ISO_8859_1);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
    Assertions.assertEquals(object, objectInputStream.readObject());
    objectInputStream.close();
    byteArrayInputStream.close();

    // verify LZSEncoder
    bytes =
        LZSEncoding.decompressFromBase64(lzsEncodedBase64).getBytes(StandardCharsets.ISO_8859_1);
    byteArrayInputStream = new ByteArrayInputStream(bytes);
    objectInputStream = new ObjectInputStream(byteArrayInputStream);
    Assertions.assertEquals(object, objectInputStream.readObject());
    objectInputStream.close();
    byteArrayInputStream.close();

  }
}
