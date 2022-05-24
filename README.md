# manticore JSQLFormatter
Java SQL Formatter, Beautifier and Pretty Printer, for more details please visit our [Website](http://217.160.215.75:8080/jsqlformatter/demo.html)

## Features
* Based on [JSQLParser](https://github.com/JSQLParser/JSqlParser)
* Supports complex SELECT, INSERT INTO, MERGE, UPDATE, DELETE, CREATE, ALTER statements
* [Netbeans Plugin](https://funfried.github.io/externalcodeformatter_for_netbeans/)
* ANSI syntax highlighting
* Formatting Options for Indent Width, Comma Before or After, Upper/Lower/Camel Case spelling
![image](https://user-images.githubusercontent.com/18080123/115190509-53106a00-a112-11eb-88f0-6ee693d6e4d3.png)
* Import from Java String or StringBuilder Code preserving variables
* Export to Java String, StringBuilder or MessageFormat handling variables

* Command Line Option (CLI) and SQL Inline Options

	```bash
	java -jar JSQLFormatter.jar [-i <arg>] [-o <arg>] [-f <arg> |
		   --ansi | --html]   [-t <arg> | -2 | -8]   [--keywordSpelling <arg>]
		   [--functionSpelling <arg>] [--objectSpelling <arg>] [--separation
		   <arg>] [--squareBracketQuotation <arg>]
	 -i,--inputFile <arg>               The input SQL file or folder.
	 -o,--outputFile <arg>              The out SQL file for the formatted
										statements.
	 -f,--outputFormat <arg>            The output-format.
										[PLAIN* ANSI HTML RTF]
		--ansi                          Output ANSI annotated text.
		--html                          Output HTML annotated text.
	 -t,--indent <arg>                  The indent width.
										[2 4* 8]
	 -2                                 Indent with 2 characters.
	 -8                                 Indent with 8 characters.
		--keywordSpelling <arg>         Keyword spelling.
										[UPPER*, LOWER, CAMEL, KEEP]
		--functionSpelling <arg>        Function name spelling.
										[UPPER, LOWER, CAMEL*, KEEP]
		--objectSpelling <arg>          Object name spelling.
										[UPPER, LOWER*, CAMEL, KEEP]
		--separation <arg>              Position of the field separator.
										[BEFORE*, AFTER]
		--squareBracketQuotation <arg>  Position of the field separator.
										[AUTO*, YES, NO]
	```

* simple usage of the Java library
	
  ```java
  import com.manticore.jsqlformatter.JSqlFormatter;
  ...
  String formattedSql = JSqlFormatter.format("SELECT * FROM table1")
  ```

  with Maven Artifact:
   
  ```xml
  <dependency>
    <groupId>com.manticore-projects.jsqlformatter</groupId>
    <artifactId>jsqlformatter</artifactId>
    <version>0.1.12</version>
  </dependency>
  ```

* Multi Platform Binaries:
	- JVM: [jsqlformatter-0.1.12.jar](https://repo1.maven.org/maven2/com/manticore-projects/jsqlformatter/jsqlformatter/0.1.12/jsqlformatter-0.1.12.jar)
(585kb, [signature(.asc)](https://repo1.maven.org/maven2/com/manticore-projects/jsqlformatter/jsqlformatter/0.1.12/jsqlformatter-0.1.12.jar.asc)
, checksum: [SHA-1](https://repo1.maven.org/maven2/com/manticore-projects/jsqlformatter/jsqlformatter/0.1.12/jsqlformatter-0.1.12.jar.sha1))
	- Linux: [JSQLFormatter](https://github.com/manticore-projects/jsqlformatter/releases/download/0.1.12/JSQLFormatter) (3 MB, ELF 64-bit LSB pie executable)
	- Windows: [JSQLFormatter.exe](https://github.com/manticore-projects/jsqlformatter/releases/download/0.1.12/JSQLFormatter.exe) (3 MB, PE32+ executable)
	- MacOS:  [JSQLFormatter.dmg](https://github.com/manticore-projects/jsqlformatter/releases/download/0.1.12/JSQLFormatter.dmg) (3 MB, zlib compressed data)

* RDBMS agnostic, works with Oracle, MS SQL Server, Postgres, H2 etc.
* tested against hundreds of complex, real life SQL statements of the [Manticore IFRS Accounting Software](http://manticore-projects.com)

## Todo/Planned
* add support for SELECT INTO statements
* detect and quote reserved keyword names
* beautify complex Functions()
* export or copy to Java, XML/HTML, RTF
* implement Plugins for Eclipse, JEdit, DBeaver, Squirrel SQL

## Samples

### Inline Formatting Options
```sql
-- UPDATE CALENDAR
-- @JSQLFormatter(indentWidth=8, keywordSpelling=UPPER, functionSpelling=CAMEL, objectSpelling=LOWER, separation=BEFORE)
UPDATE cfe.calendar
SET     year_offset = ?                    /* year offset */
        , settlement_shift = To_Char( ? )  /* settlement shift */
        , friday_is_holiday = ?            /* friday is a holiday */
        , saturday_is_holiday = ?          /* saturday is a holiday */
        , sunday_is_holiday = ?            /* sunday is a holiday */
WHERE id_calendar = ?
;

-- UPDATE CALENDAR
-- @JSQLFormatter(indentWidth=2, keywordSpelling=LOWER, functionSpelling=KEEP, objectSpelling=UPPER, separation=AFTER)
update CFE.CALENDAR
set YEAR_OFFSET = ?                    /* year offset */,
    SETTLEMENT_SHIFT = to_char( ? )    /* settlement shift */,
    FRIDAY_IS_HOLIDAY = ?              /* friday is a holiday */,
    SATURDAY_IS_HOLIDAY = ?            /* saturday is a holiday */,
    SUNDAY_IS_HOLIDAY = ?              /* sunday is a holiday */
where ID_CALENDAR = ?
;
```

### Complex Comments
```sql
------------------------------------------------------------------------------------------------------------------------
-- CONFIGURATION
------------------------------------------------------------------------------------------------------------------------

-- UPDATE CALENDAR
UPDATE cfe.calendar
SET year_offset = ?            /* year offset */
    , settlement_shift = ?     /* settlement shift */
    , friday_is_holiday = ?    /* friday is a holiday */
    , saturday_is_holiday = ?  /* saturday is a holiday */
    , sunday_is_holiday = ?    /* sunday is a holiday */
WHERE id_calendar = ?
;


-- BOTH CLAUSES PRESENT 'with a string' AND "a field"
MERGE /*+ PARALLEL */ INTO test1 /*the target table*/ a
    USING all_objects      /*the source table*/
        ON ( /*joins in()!*/ a.object_id = b.object_id )
-- INSERT CLAUSE 
WHEN /*comments between keywords!*/ NOT MATCHED THEN
    INSERT ( object_id     /*ID Column*/
                , status   /*Status Column*/ )
    VALUES ( b.object_id
                , b.status )
/* UPDATE CLAUSE
WITH A WHERE CONDITION */ 
WHEN MATCHED THEN          /* Lets rock */
    UPDATE SET  a.status = '/*this is no comment!*/ and -- this ain''t either'
    WHERE   b."--status" != 'VALID'
;
```

### Import Java String from Clipboard

```java
String test1 = "SELECT " + columnName + " from " + tableName + ";";

String test2 =
        new StringBuilder("SELECT ")
            .append(columnName)
            .append(" from ")
            .append(tableName)
            .append(";")
            .toString();

String queryStr = new MessageFormat2(
			"WITH split (    word\n"
			+"                , str\n"
			+"                , hascomma ) AS (\n"
			+"        VALUES ( '', 'Auto,A,1234444', 1 )\n"
			+"        UNION ALL\n"
			+"        SELECT  Substr( str, 0, CASE\n"
			+"                        WHEN Instr( str, ',' )\n"
			+"                            THEN Instr( str, ',' )\n"
			+"                        ELSE Length( str ) + 1\n"
			+"                    END )\n"
			+"                , Ltrim( Substr( str, Instr( str, ',' ) ), ',' )\n"
			+"                , Instr( str, ',' )\n"
			+"        FROM split\n"
			+"        WHERE hascomma )\n"
			+"SELECT Trim( word )\n"
			+"FROM split\n"
			+"WHERE word != ''\n"
			+";\n"
		).format(new Object[]{});
```

will format into the following SQL

```sql
SELECT $columnname$
FROM $tablename$
;

SELECT $columnname$
FROM $tablename$
;

WITH split (    word
                , str
                , hascomma ) AS (
        VALUES ( '', 'Auto,A,1234444', 1 )
        UNION ALL
        SELECT  Substr( str, 0, CASE
                        WHEN Instr( str, ',' )
                            THEN Instr( str, ',' )
                        ELSE Length( str ) + 1
                    END )
                , Ltrim( Substr( str, Instr( str, ',' ) ), ',' )
                , Instr( str, ',' )
        FROM split
        WHERE hascomma )
SELECT Trim( word )
FROM split
WHERE word != ''
;
```

### Export formatted Java String to Clipboard

```java
String queryStr1 =   
	  "SELECT " + columnname + "\n"
	+ "FROM " + tablename + "\n"
	+ ";\n";

String queryStr2 =   
	  "WITH split (    word\n"
	+ "                , str\n"
	+ "                , hascomma ) AS (\n"
	+ "        VALUES ( '', 'Auto,A,1234444', 1 )\n"
	+ "        UNION ALL\n"
	+ "        SELECT  Substr( str, 0, CASE\n"
	+ "                        WHEN Instr( str, ',' )\n"
	+ "                            THEN Instr( str, ',' )\n"
	+ "                        ELSE Length( str ) + 1\n"
	+ "                    END )\n"
	+ "                , Ltrim( Substr( str, Instr( str, ',' ) ), ',' )\n"
	+ "                , Instr( str, ',' )\n"
	+ "        FROM split\n"
	+ "        WHERE hascomma )\n"
	+ "SELECT Trim( word )\n"
	+ "FROM split\n"
	+ "WHERE word != ''\n"
	+ ";\n";
```

### Export Java StringBuilder to Clipboard

```java
StringBuilder queryStrBuilder1 = new StringBuilder()
	.append("SELECT ").append(columnname).append("\n")
	.append("FROM ").append(tablename).append("\n")
	.append(";\n");

StringBuilder queryStrBuilder2 = new StringBuilder()
	.append("WITH split (    word\n")
	.append("                , str\n")
	.append("                , hascomma ) AS (\n")
	.append("        VALUES ( '', 'Auto,A,1234444', 1 )\n")
	.append("        UNION ALL\n")
	.append("        SELECT  Substr( str, 0, CASE\n")
	.append("                        WHEN Instr( str, ',' )\n")
	.append("                            THEN Instr( str, ',' )\n")
	.append("                        ELSE Length( str ) + 1\n")
	.append("                    END )\n")
	.append("                , Ltrim( Substr( str, Instr( str, ',' ) ), ',' )\n")
	.append("                , Instr( str, ',' )\n")
	.append("        FROM split\n")
	.append("        WHERE hascomma )\n")
	.append("SELECT Trim( word )\n")
	.append("FROM split\n")
	.append("WHERE word != ''\n")
	.append(";\n");
```

### Export Java MessageFormat to Clipboard

```java
String queryStr = new MessageFormat1(
			"SELECT {0}\n"
			+"FROM {1}\n"
			+";\n"
		).format(new Object[]{columnname, tablename});

String queryStr = new MessageFormat2(
			"WITH split (    word\n"
			+"                , str\n"
			+"                , hascomma ) AS (\n"
			+"        VALUES ( '', 'Auto,A,1234444', 1 )\n"
			+"        UNION ALL\n"
			+"        SELECT  Substr( str, 0, CASE\n"
			+"                        WHEN Instr( str, ',' )\n"
			+"                            THEN Instr( str, ',' )\n"
			+"                        ELSE Length( str ) + 1\n"
			+"                    END )\n"
			+"                , Ltrim( Substr( str, Instr( str, ',' ) ), ',' )\n"
			+"                , Instr( str, ',' )\n"
			+"        FROM split\n"
			+"        WHERE hascomma )\n"
			+"SELECT Trim( word )\n"
			+"FROM split\n"
			+"WHERE word != ''\n"
			+";\n"
		).format(new Object[]{});
```

