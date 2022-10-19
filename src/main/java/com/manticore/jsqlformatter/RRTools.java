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

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.TreeSet;

public class RRTools {
  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      // throw new IllegalArgumentException("No filename provided as parameters ARGS[0]");
      args = new String[] {
          "/home/are/Documents/src/JSQLFormatter/JSQLFormatter/src/site/sphinx/_static/railroad_diagram.xhtml"};
    }

    File file = new File(args[0]);
    if (file.exists() && file.canRead()) {
      // insertTOC(file);
      extractSVG(file);
    } else {
      throw new FileNotFoundException("Can't read file " + args[0]);
    }
  }

  private static String stripTrailing(String s, String suffix) {
    if (s.endsWith(suffix))
      return s.substring(0, s.length() - suffix.length());
    else
      return s;
  }

  public static void insertTOC(File file) throws IOException {
    System.setProperty(W3CDom.XPathFactoryProperty, "net.sf.saxon.xpath.XPathFactoryImpl");

    Document doc = Jsoup.parse(file, "UTF-8", "", Parser.xmlParser());
    Elements elements = doc.selectXpath("//*[local-name()='a' and not(@href) and @name]");

    ArrayList<String> tocEntries = new ArrayList<>();
    TreeSet<String> indexEntries = new TreeSet<>();

    for (Element link : elements) {
      String key = stripTrailing(link.text(), ":");
      tocEntries.add(key);
      indexEntries.add(key);
    }

    Element tocElement = doc.body().prependElement("H1");
    tocElement.text("Table of Content:");
    tocElement.attr("style", "font-size: 14px; font-weight:bold");

    Element pElement = tocElement.appendElement("p");
    pElement.attr("style", "font-size: 11px; font-weight:normal");
    for (String s : tocEntries) {
      pElement.appendElement("a").attr("href", "#" + s).text(s);
      pElement.appendText(" ");
    }

    Element indexElement = doc.body().prependElement("H1");
    indexElement.text("Index:");
    indexElement.attr("style", "font-size: 14px; font-weight:bold");

    pElement = indexElement.appendElement("p");
    pElement.attr("style", "font-size: 11px; font-weight:normal");
    for (String s : indexEntries) {
      pElement.appendElement("a").attr("href", "#" + s).text(s);
      pElement.appendText(" ");
    }

    FileUtils.writeStringToFile(file, doc.outerHtml(), StandardCharsets.UTF_8);
  }

  /*
  @throws Exception
   */
  public static void extractSVG(File file) throws IOException {
    System.setProperty(W3CDom.XPathFactoryProperty, "net.sf.saxon.xpath.XPathFactoryImpl");

    Document doc = Jsoup.parse(file, "UTF-8", "", Parser.xmlParser());
    Elements elements = doc.selectXpath("//svg");
    for (Element svgElement : elements) {
      System.out.println(svgElement.text());
    }
  }
}
