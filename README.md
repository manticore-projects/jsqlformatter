# manticore JSQLFormatter
Java SQL Formatter, Beautifier and Pretty Printer, for more details please visit our [Website](https://manticore-projects.github.io/jsqlformatter)

[![Java CI with Maven](https://github.com/manticore-projects/jsqlformatter/actions/workflows/maven.yml/badge.svg)](https://github.com/manticore-projects/jsqlformatter/actions/workflows/maven.yml)

## Features
* based on [JSQLParser](https://github.com/JSQLParser/JSqlParser)
* supports complex SELECT, INSERT INTO, MERGE, UPDATE, DELETE, CREATE, ALTER statements
* ANSI syntax highlighting
* Formatting Options for Indent Width, Comma Before or After, Upper/Lower/Camel Case spelling
![image](https://user-images.githubusercontent.com/18080123/115190509-53106a00-a112-11eb-88f0-6ee693d6e4d3.png)

* Command Line Option (CLI) and SQL Inline Options
```
java -jar JSQLFormatter.jar [-i <arg>] [-o <arg>] [-f <arg> |
       --ansi | --html]   [-t <arg> | -2 | -8]   [--keywordSpelling <arg>]
       [--functionSpelling <arg>] [--objectSpelling <arg>] [--separation
       <arg>]
 -i,--input-file <arg>         The input SQL file or folder.
 -o,--output-file <arg>        The out SQL file for the formatted
                               statements.
 -f,--format <arg>             The output-format.
                               [PLAIN* ANSI HTML RTF]
    --ansi                     Output ANSI annotated text.
    --html                     Output HTML annotated text.
 -t,--indent <arg>             The indent width.
                               [2 4* 8]
 -2                            Indent with 2 characters.
 -8                            Indent with 8 characters.
    --keywordSpelling <arg>    Keyword spelling.
                               [UPPER*, LOWER, CAMEL, KEEP]
    --functionSpelling <arg>   Function name spelling.
                               [UPPER, LOWER, CAMEL*, KEEP]
    --objectSpelling <arg>     Object name spelling.
                               [UPPER, LOWER*, CAMEL, KEEP]
    --separation <arg>         Position of the field separator.
                               [BEFORE*, AFTER]
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
	  <version>0.1.4</version>
	</dependency>
	```
	

* Multi Platform Binaries:
	- JVM: [jsqlformatter-0.1.5.jar](https://repo1.maven.org/maven2/com/manticore-projects/jsqlformatter/jsqlformatter/0.1.5/jsqlformatter-0.1.5.jar)
(585kb, [signature(.asc)](https://repo1.maven.org/maven2/com/manticore-projects/jsqlformatter/jsqlformatter/0.1.5/jsqlformatter-0.1.5.jar.asc)
, checksum: [SHA-1](https://repo1.maven.org/maven2/com/manticore-projects/jsqlformatter/jsqlformatter/0.1.5/jsqlformatter-0.1.5.jar.sha1))
	- Linux: [JSQLFormatter](https://github.com/manticore-projects/jsqlformatter/releases/download/0.1.5/JSQLFormatter) (2.8 MB, ELF 64-bit LSB pie executable)
	- Windows: [JSQLFormatter.exe](https://github.com/manticore-projects/jsqlformatter/releases/download/0.1.5/JSQLFormatter.exe) (2.8 MB, PE32+ executable)
	- MacOS is planned

* RDBMS agnostic, works with Oracle, MS SQL Server, Postgres, H2 etc.
* tested against hundreds of complex, real life SQL statements of the [Manticore IFRS Accounting Software](http://manticore-projects.com)

## Todo/Planned
* add support for SELECT INTO statements
* detect and quote reserved keyword names
* beautify complex Functions()
* export or copy to Java, XML/HTML, RTF
* implement Plugins for Netbeans, Eclipse, JEdit, DBeaver, Squirrel SQL

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

