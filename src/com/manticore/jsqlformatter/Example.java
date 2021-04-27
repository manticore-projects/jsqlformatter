package com.manticore.jsqlformatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Example {
    public static void main(String[] args) {
        final String regex = "(?s)\\s*((?:'(?:\\\\.|[^\\\\']|''|)'|/\\.*?\\*/|(?:--|#)[^\\r\\n]|[^\\\\'])?)(?:;|$)";
        final String string = "/* Please do not find this query:\n"
	 + "select * from start_comment;\n"
	 + "*/\n\n"
	 + "SELECT  'wedqweqw ;' \"dsfsdf ;\" * ;*\n"
	 + "        , sl.sql_id\n"
	 + "        , sl.sql_hash_value\n"
	 + "        , opname\n"
	 + "        , target\n"
	 + "        , elapsed_seconds\n"
	 + "        , time_remaining\n"
	 + "FROM v$session_longops sl\n"
	 + "    INNER JOIN v$session s\n"
	 + "        ON sl.SID = s.SID\n"
	 + "            AND sl.SERIAL# = s.SERIAL#\n"
	 + "WHERE time_remaining > 0;\n\n"
	 + "/* Also not this query:\n"
	 + "select * from end_comment;\n"
	 + "*/";
        
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(string);
        
        while (matcher.find()) {
            System.out.println("Full match: " + matcher.group(0));
            
//            for (int i = 1; i <= matcher.groupCount(); i++) {
//                System.out.println("Group " + i + ": " + matcher.group(i));
//            }
        }
    }
}
