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

/** @author are */
public class Comment implements Comparable<Comment> {
  protected boolean newLine;
  protected boolean extraNewLine;
  protected int absolutePosition;
  protected int relativePosition;
  protected String text;

  public Comment(int absolutePosition, String text) {
    this.absolutePosition = absolutePosition;
    this.text = text;
  }

  @Override
  public int compareTo(Comment o) {
    return Integer.compare(absolutePosition, o.absolutePosition);
  }

  @Override
  public String toString() {
    return text;
  }
}
