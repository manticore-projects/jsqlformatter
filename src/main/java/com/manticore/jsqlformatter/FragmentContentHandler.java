/**
 * Manticore Projects JSQLFormatter is a SQL Beautifying and Formatting Software.
 * Copyright (C) 2023 Andreas Reichel <andreas@manticore-projects.com>
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

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class FragmentContentHandler extends DefaultHandler {
  private String xPath = "";
  private final XMLReader xmlReader;
  private FragmentContentHandler parent;
  private final StringBuilder characters = new StringBuilder();
  private final Map<String, Integer> elementNameCount = new HashMap<>();

  private final StringBuilder builder;

  public FragmentContentHandler(XMLReader xmlReader, StringBuilder builder) {
    this.xmlReader = xmlReader;
    this.builder = builder;
  }

  private FragmentContentHandler(String xPath, XMLReader xmlReader, FragmentContentHandler parent,
      StringBuilder builder) {
    this(xmlReader, builder);
    this.xPath = xPath;
    this.parent = parent;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts)
      throws SAXException {
    Integer count = elementNameCount.get(qName);
    if (null == count) {
      count = 1;
    } else {
      count++;
    }
    elementNameCount.put(qName, count);
    String childXPath = xPath + "/" + qName + "[" + count + "]";

    int attsLength = atts.getLength();
    builder.append(childXPath);
    for (int x = 0; x < attsLength; x++) {
      if (!atts.getQName(x).equals("object") && !atts.getQName(x).equals("class")) {
        builder.append("[@").append(atts.getQName(x)).append("='").append(atts.getValue(x))
            .append("']");
      }
    }
    builder.append("\n");

    FragmentContentHandler child = new FragmentContentHandler(childXPath, xmlReader, this, builder);
    xmlReader.setContentHandler(child);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    String value = characters.toString().trim();
    if (value.length() > 0) {
      builder.append(xPath).append("='").append(characters.toString()).append("'").append("\n");
    }
    xmlReader.setContentHandler(parent);
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    characters.append(ch, start, length);
  }

  public static String getXPath(String xml, Object object) throws Exception {
    StringBuilder builder = new StringBuilder();

    SAXParserFactory spf = SAXParserFactory.newInstance();
    SAXParser sp = spf.newSAXParser();
    XMLReader xr = sp.getXMLReader();

    FragmentContentHandler handler = new FragmentContentHandler(xr, builder);
    xr.setContentHandler(handler);

    InputSource inputSource = new InputSource(new StringReader(xml));
    xr.parse(inputSource);

    return builder.toString();
  }

}
