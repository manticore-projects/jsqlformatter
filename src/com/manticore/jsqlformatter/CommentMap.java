/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.manticore.jsqlformatter;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import com.manticore.jsqlformatter.JSQLFormatter.OutputFormat;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.jsqlparser.expression.OracleHint;

/** @author are */
public class CommentMap extends TreeMap<Integer, Comment> {
  private static final Logger LOGGER = Logger.getLogger(CommentMap.class.getName());

  public static final Pattern COMMENT_PATTERN =
      Pattern.compile(
          "(\'.*?\'[^'']|\".*?\")" + "|(/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/|--.*?\\r?[\\n])"
          , Pattern.DOTALL | Pattern.MULTILINE | Pattern.UNIX_LINES);


  private static final Pattern STRING_PATTERN = Pattern.compile("(\'.*?\'[^'']|\".*?\")", Pattern.DOTALL | Pattern.MULTILINE | Pattern.UNIX_LINES);

  private static final AnsiFormat ANSI_FORMAT_COMMENT =
      new AnsiFormat(Attribute.CLEAR(), Attribute.BRIGHT_BLACK_TEXT(), Attribute.ITALIC());

  private static StringBuilder appendComment(
      StringBuilder builder, OutputFormat format, String keyword, String before, String after) {
    switch (format) {
      case PLAIN:
        builder.append(before).append(keyword).append(after);
        break;
      case ANSI:
        builder.append(before).append(ANSI_FORMAT_COMMENT.format(keyword)).append(after);
        break;
      default:
        builder.append(before).append(keyword).append(after);
        break;
    }
    return builder;
  }

  public CommentMap(String sqlStr) {
    Matcher matcher = COMMENT_PATTERN.matcher(sqlStr);
    int i = 0;
    while (matcher.find()) {
      i++;
      
      String group = matcher.group(0);
      int start = matcher.start(0);

      if (!STRING_PATTERN.matcher(group).matches()) {
        if (OracleHint.isHintMatch(group))
                LOGGER.log(Level.FINE, "Oracle hint {0}", group);
        else {
          Comment comment = new Comment(start, group);
          if (matcher.start() == 0 || sqlStr.charAt(start - 1) == '\n') {
            comment.newLine = true;
          }

          put(comment.absolutePosition, comment);
        }
      }
    }

    int absolutePosition = 0;
    int relativePosition = 0;
    int totalCommentsLength = 0;

    for (Comment comment : values()) {
      while (absolutePosition < comment.absolutePosition) {
        char c = sqlStr.charAt(absolutePosition);
        if (!Character.isWhitespace(c)) relativePosition++;

        absolutePosition++;
      }
      comment.relativePosition = relativePosition - totalCommentsLength;
      totalCommentsLength += comment.text.replaceAll("\\s", "").length();

      LOGGER.log(
          Level.FINE,
          "Found comment {0} at Position {1} (absolute) {2} (relative).",
          new Object[] {comment.text, comment.absolutePosition, comment.relativePosition});
    }
  }

  public StringBuilder insertComments(
      StringBuilder sqlStrWithoutComments, OutputFormat outputFormat) {

    StringBuilder builder = new StringBuilder();

    Iterator<Comment> commentIteraror = values().iterator();
    if (commentIteraror.hasNext()) {
      Comment next = commentIteraror.next();

      int relativePosition = 0;
      int ansiStarted = -1;

      for (int position = 0; position < sqlStrWithoutComments.length(); position++) {

        String c = sqlStrWithoutComments.substring(position, position + 1);
        
        if (ansiStarted<0)
          if (next.relativePosition <= relativePosition) {

            if (next.newLine) builder.append("\n");
            else if (!c.matches("\\w")) builder.append(" ");

            if (!next.newLine && next.text.startsWith("--")) {
              appendComment(
                  builder, outputFormat, next.text.trim().replaceFirst("--\\s?", "/*"), "", "*/");
            } else {
              appendComment(builder, outputFormat, next.text.trim(), "", "");
            }

            if (next.absolutePosition == 0) builder.append("\n");

            if (commentIteraror.hasNext()) {
              next = commentIteraror.next();
            } else {
              builder.append(sqlStrWithoutComments.substring(position));
              break;
            }
          }
        
        if (ansiStarted < 0
            && sqlStrWithoutComments.substring(position, position + 2).matches("\u001B\\["))
          ansiStarted = position;

        if (ansiStarted >= 0
            && sqlStrWithoutComments
                .substring(ansiStarted, position+1)
                .matches("\u001B\\[[;\\d]*[ -/]*[@-~]")) {
          ansiStarted = -1;
        }

        builder.append(c);

        if (ansiStarted < 0) {
          relativePosition =
              sqlStrWithoutComments
                  .substring(0, position + 1)
                  .replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]|\\s", "")
                  .length();
        }
      }
    }

    return builder;
  }
}
