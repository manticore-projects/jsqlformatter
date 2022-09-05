/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
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
