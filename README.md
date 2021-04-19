# jsqlformatter
Java SQL Formatter, Beautifier and Pretty Printer

[![Java CI with Maven](https://github.com/manticore-projects/jsqlformatter/actions/workflows/maven.yml/badge.svg)](https://github.com/manticore-projects/jsqlformatter/actions/workflows/maven.yml)

## Features
* based on [JSQLParser](https://github.com/JSQLParser/JSqlParser)
* supports complex SELECT, INSERT INTO, MERGE, UPDATE, DELETE, CREATE, ALTER statements
* ANSI syntax highlighting
![image](https://user-images.githubusercontent.com/18080123/115190509-53106a00-a112-11eb-88f0-6ee693d6e4d3.png)

* Command Line Option (CLI)
```
are@ryzen ~/d/s/jsqlformatter (main) [1]> ./JSQLFormatter
usage: ./JSQLFormatter [-i <arg>] [-o <arg>] [-f <arg>] [--ansi] [--html]
       [--indent <arg>] [-2] [-8]
 -i,--input-file <arg>    The input SQL file or folder.
 -o,--output-file <arg>   The out SQL file for the formatted statements.
 -f,--format <arg>        The output-format [PLAIN* ANSI HTML RTF]
    --ansi                Output ANSI annotated text.
    --html                Output HTML annotated text.
    --indent <arg>        The indent width [2 4* 8]
 -2                       Indent with 2 characters.
 -8                       Indent with 8 characters.
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
	  <version>0.1.2</version>
	</dependency>
	```
	

* Multi Platform Binaries:
	1) JVM: [jsqlformatter-0.1.2.jar](https://repo1.maven.org/maven2/com/manticore-projects/jsqlformatter/jsqlformatter/0.1.2/jsqlformatter-0.1.2.jar)
(585kb, [signature(.asc)](https://repo1.maven.org/maven2/com/manticore-projects/jsqlformatter/jsqlformatter/0.1.2/jsqlformatter-0.1.2.jar.asc)
, checksum: [SHA-1](https://repo1.maven.org/maven2/com/manticore-projects/jsqlformatter/jsqlformatter/0.1.2/jsqlformatter-0.1.2.jar.sha1))
	2) Native Linux AMD64:
	3) Native Windows AMD64:
	4) Native MacOS is planned

* RDBMS agnostic, works with Oracle, MS SQL Server, Postgres, H2 etc.
* tested against hundreds of complex, real life SQL statements of the [Manticore IFRS Accounting Software](http://manticore-projects.com)

## Todo/Planned
* add support for SELECT INTO statements
* detect and quote reserved keyword names
* add formatting options as per SQL dialect (e. g. Comma first/last, Greedy Spaces, Upper-/Lower-/Camel-Case Names)
* beautify complex Functions()
* export or copy to Java, XML/HTML, RTF
* implement OS native standalone package and a Netbeans Plugin

## Samples

### MERGE statements
```sql
-- MERGE DELETE WHERE
MERGE INTO empl_current tar
    USING ( SELECT empno
                , ename
                , CASE 
                    WHEN leavedate <= SYSDATE
                        THEN 'Y'
                    ELSE 'N'
                 END AS delete_flag
            FROM empl ) src
        ON ( tar.empno = src.empno ) 
WHEN NOT MATCHED THEN 
    INSERT ( empno
                , ename ) 
    VALUES ( src.empno
                , src.ename )
WHEN MATCHED THEN 
    UPDATE SET tar.ename = src.ename
    WHERE delete_flag = 'N'
    DELETE WHERE delete_flag = 'Y'
;
```

### UPDATE statements
```sql
-- UPDATE COUNTERPARTY_INSTRUMENT
UPDATE risk.counterparty_instrument a1
SET ( PRIORITY
            , TYPE
            , DESCRIPTION
            , LIMIT_AMOUT
            , ID_CURRENCY
            , END_DATE ) = ( SELECT a.PRIORITY
                                , a.TYPE
                                , a.DESCRIPTION
                                , a.LIMIT_AMOUT
                                , a.ID_CURRENCY
                                , a.END_DATE
                            FROM risk.imp_counterparty_instrument a
                                    INNER JOIN risk.counterparty b
                                        ON a.id_counterparty = b.id_counterparty
                                            AND b.id_status = 'C'
                                    INNER JOIN risk.instrument c
                                        ON a.ID_instrument_BENEFICIARY = c.id_instrument
                                            AND c.id_status = 'C'
                                    INNER JOIN risk.counterparty_instrument e
                                        ON b.id_counterparty_ref = e.id_counterparty_ref
                                            AND e.ID_instrument_BENEFICIARY = a.ID_instrument_BENEFICIARY
                                            AND e.ID_INSTRUMENT_GUARANTEE = a.ID_INSTRUMENT_GUARANTEE
                            WHERE e.id_counterparty_ref = a1.id_counterparty_ref
                                AND e.ID_instrument_BENEFICIARY = a1.ID_instrument_BENEFICIARY
                                AND e.ID_INSTRUMENT_GUARANTEE = a1.ID_INSTRUMENT_GUARANTEE ) 
WHERE EXISTS ( SELECT a.PRIORITY
                    , a.TYPE
                    , a.DESCRIPTION
                    , a.LIMIT_AMOUT
                    , a.ID_CURRENCY
                    , a.END_DATE
                FROM risk.imp_counterparty_instrument a
                        INNER JOIN risk.counterparty b
                            ON a.id_counterparty = b.id_counterparty
                                AND b.id_status = 'C'
                        INNER JOIN risk.instrument c
                            ON a.ID_instrument_BENEFICIARY = c.id_instrument
                                AND c.id_status = 'C'
                        INNER JOIN risk.counterparty_instrument e
                            ON b.id_counterparty_ref = e.id_counterparty_ref
                                AND e.ID_instrument_BENEFICIARY = a.ID_instrument_BENEFICIARY
                                AND e.ID_INSTRUMENT_GUARANTEE = a.ID_INSTRUMENT_GUARANTEE
                WHERE e.id_counterparty_ref = a1.id_counterparty_ref
                    AND e.ID_instrument_BENEFICIARY = a1.ID_instrument_BENEFICIARY
                    AND e.ID_INSTRUMENT_GUARANTEE = a1.ID_INSTRUMENT_GUARANTEE ) 
;
```

### SELECT statements
```sql
-- SELECT WITH COMPLEX ORDER
WITH ex AS ( 
        SELECT value_date
            , posting_date
        FROM cfe.execution x
        WHERE id_status IN  ( 'R', 'H' ) 
            AND value_date = ( SELECT Max(value_date)
                                FROM cfe.execution
                                WHERE id_status IN  ( 'R', 'H' )  ) 
            AND posting_date = ( SELECT Max(posting_date)
                                FROM cfe.execution
                                WHERE id_status IN  ( 'R', 'H' ) 
                                    AND value_date = x.value_date )  )
, fxr AS ( 
        SELECT id_currency_from
            , fxrate
        FROM common.fxrate_hst f
        WHERE f.value_date <= ( SELECT value_date
                                FROM ex ) 
            AND f.value_date = ( SELECT Max(value_date)
                                FROM common.fxrate_hst
                                WHERE id_currency_from = f.id_currency_from
                                    AND id_currency_into = f.id_currency_into ) 
            AND id_currency_into = 'NGN'
        UNION ALL 
        SELECT 'NGN'
            , 1
        FROM dual )
, scope AS ( 
        SELECT *
        FROM cfe.accounting_scope
        WHERE id_status = 'C'
            AND id_accounting_scope_code = 'INTERN' )
, scope1 AS ( 
        SELECT *
        FROM cfe.accounting_scope
        WHERE id_status = 'C'
            AND id_accounting_scope_code = 'NGAAP' )
, c AS ( 
        SELECT b.code
            , Round(Sum(d.amount * fxr.fxrate), 2) balance_bc
        FROM scope
                INNER JOIN cfe.ledger_branch_branch b
                    ON b.id_accounting_scope = scope.id_accounting_scope
                INNER JOIN cfe.ledger_account c
                    ON b.code_inferior = c.code
                        AND c.id_accounting_scope_code = scope.id_accounting_scope_code
                INNER JOIN  ( SELECT id_account_credit id_account
                                        , amount
                                    FROM cfe.ledger_account_entry
                                            INNER JOIN ex
                                                ON ledger_account_entry.posting_date <= ex.posting_date
                                    UNION ALL 
                                    SELECT id_account_debit
                                        , -amount
                                    FROM cfe.ledger_account_entry
                                            INNER JOIN ex
                                                ON ledger_account_entry.posting_date <= ex.posting_date )  d
                    ON c.id_account = d.id_account
                INNER JOIN fxr
                    ON c.id_currency = fxr.id_currency_from
        GROUP BY b.code )
, c1 AS ( 
        SELECT b.code
            , Round(Sum(d.amount * fxr.fxrate), 2) balance_bc
        FROM scope1
                INNER JOIN cfe.ledger_branch_branch b
                    ON b.id_accounting_scope = scope1.id_accounting_scope
                INNER JOIN cfe.ledger_account c
                    ON b.code_inferior = c.code
                        AND c.id_accounting_scope_code = scope1.id_accounting_scope_code
                INNER JOIN  ( SELECT id_account_credit id_account
                                        , amount
                                    FROM cfe.ledger_account_entry
                                            INNER JOIN ex
                                                ON ledger_account_entry.posting_date <= ex.posting_date
                                    UNION ALL 
                                    SELECT id_account_debit
                                        , -amount
                                    FROM cfe.ledger_account_entry
                                            INNER JOIN ex
                                                ON ledger_account_entry.posting_date <= ex.posting_date )  d
                    ON c.id_account = d.id_account
                INNER JOIN fxr
                    ON c.id_currency = fxr.id_currency_from
        GROUP BY b.code )
SELECT /*+ parallel */ a.code code
    , Lpad(' ', 4 * (a.GL_LEVEL - 1), ' ') ||  a.code format_code
    , b.description
    , c.balance_bc
    , c1.balance_bc
FROM scope
        INNER JOIN cfe.ledger_branch_branch a
            ON a.code = a.code_inferior
                AND a.id_accounting_scope = scope.id_accounting_scope
        INNER JOIN cfe.ledger_branch b
            ON a.id_accounting_scope = b.id_accounting_scope
                AND a.code = b.code
        LEFT JOIN c
            ON a.code = c.code
        LEFT OUTER JOIN c1
            ON a.code = c1.code
WHERE gl_level <= 3
    AND NOT ( c.balance_bc IS NULL
                AND c1.balance_bc IS NULL )
ORDER BY ( SELECT code
            FROM cfe.ledger_branch_branch
            WHERE id_accounting_scope = a.id_accounting_scope
                AND code_inferior = a.code
                AND gl_level = 1 ) NULLS FIRST 
    , ( SELECT code
        FROM cfe.ledger_branch_branch
        WHERE id_accounting_scope = a.id_accounting_scope
            AND code_inferior = a.code
            AND gl_level = 2 ) NULLS FIRST 
    , ( SELECT code
        FROM cfe.ledger_branch_branch
        WHERE id_accounting_scope = a.id_accounting_scope
            AND code_inferior = a.code
            AND gl_level = 3 ) NULLS FIRST 
    , ( SELECT code
        FROM cfe.ledger_branch_branch
        WHERE id_accounting_scope = a.id_accounting_scope
            AND code_inferior = a.code
            AND gl_level = 4 ) NULLS FIRST 
    , ( SELECT code
        FROM cfe.ledger_branch_branch
        WHERE id_accounting_scope = a.id_accounting_scope
            AND code_inferior = a.code
            AND gl_level = 5 ) NULLS FIRST 
    , ( SELECT code
        FROM cfe.ledger_branch_branch
        WHERE id_accounting_scope = a.id_accounting_scope
            AND code_inferior = a.code
            AND gl_level = 6 ) NULLS FIRST 
    , ( SELECT code
        FROM cfe.ledger_branch_branch
        WHERE id_accounting_scope = a.id_accounting_scope
            AND code_inferior = a.code
            AND gl_level = 7 ) NULLS FIRST 
    , code
;
```

