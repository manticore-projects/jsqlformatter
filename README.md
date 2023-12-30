# manticore JSQLFormatter
Java SQL Formatter, Beautifier and Pretty Printer. Please visit our [Website](http://manticore-projects.com/JSQLFormatter/index.html) and try the [ONLINE Demo](http://jsqlformatter.manticore-projects.com).

[![Gradle CI](https://github.com/manticore-projects/jsqlformatter/actions/workflows/gradle.yml/badge.svg)](https://github.com/manticore-projects/jsqlformatter/actions/workflows/gradle.yml)
[![Maven](https://badgen.net/maven/v/maven-central/com.manticore-projects.jsqlformatter/jsqlformatter)](https://mvnrepository.com/artifact/com.manticore-projects.jsqlformatter/jsqlformatter)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/80374649d914462ebd6e5b160a1ebdbb)](https://app.codacy.com/gh/manticore-projects/jsqlformatter/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Coverage Status](https://coveralls.io/repos/github/manticore-projects/jsqlformatter/badge.svg)](https://coveralls.io/github/manticore-projects/jsqlformatter)
[![License](https://img.shields.io/badge/License-AGPL-blue)](#LICENSE)
[![issues - JSQLFormatter](https://img.shields.io/github/issues/manticore-projects/jsqlformatter)](https://github.com/manticore-projects/jsqlformatter/issues)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](http://makeapullrequest.com)

![ANSI Output](./src/site/sphinx/_static/ansi-terminal.png)

## Features
* Based on [JSQLParser](https://github.com/JSQLParser/JSqlParser)
* Supports complex SELECT, INSERT INTO, MERGE, UPDATE, DELETE, CREATE, ALTER statements
* RDBMS agnostic and compatible to
* ANSI syntax highlighting
* Formatting Options for Indent Width, Comma Before or After, Upper/Lower/Camel Case spelling
* Import from Java String or StringBuilder, while preserving variables
* Export to Java String, StringBuilder or MessageFormat, while handling parameters
* Command Line Option (CLI) and SQL Inline Options

    ```shell
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
        <version>4.8.0</version>
    </dependency>
    ```


## Samples

### Inline Formatting Options
```sql
-- @JSQLFormatter(indentWidth=8, keywordSpelling=UPPER, functionSpelling=CAMEL, objectSpelling=LOWER, separation=BEFORE)
UPDATE cfe.calendar
SET     year_offset = ?                    /* year offset */
        , settlement_shift = To_Char( ? )  /* settlement shift */
        , friday_is_holiday = ?            /* friday is a holiday */
        , saturday_is_holiday = ?          /* saturday is a holiday */
        , sunday_is_holiday = ?            /* sunday is a holiday */
WHERE id_calendar = ?
;

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

[More Samples](http://manticore-projects.com/JSQLFormatter/samples.html)
