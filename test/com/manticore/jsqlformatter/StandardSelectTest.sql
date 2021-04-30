
-- INSERT NEW LEDGER ACCOUNTS
SELECT /*+ PARALLEL */
    cfe.id_account_seq.nextval
    , "a"."code"
    , a."id_currency"
    , a.id_fee_type
    , current_date
    , NULL
    , id_accounting_scope_code
FROM (  SELECT *
        FROM (  SELECT DISTINCT
                    c.code code
                    , d.id_currency
                    , NULL id_fee_type
                    , c1.id_accounting_scope_code
                FROM cfe.ledger_branch c
                    INNER JOIN cfe.accounting_scope c1
                        ON c1.id_accounting_scope = c.id_accounting_scope
                            AND c1.id_status = 'C'
                    , common.ledger_currency d
                MINUS 
                SELECT DISTINCT
                    c.code
                    , d.id_currency
                    , NULL id_fee_type
                    , c.id_accounting_scope_code
                FROM cfe.ledger_account c
                    INNER JOIN common.ledger_currency d
                        ON c.id_currency = d.id_currency )
        UNION 
        SELECT *
        FROM (  SELECT DISTINCT
                    c.code
                    , d.id_currency
                    , NULL id_fee_type
                    , c1.id_accounting_scope_code
                FROM cfe.ledger_branch c
                    INNER JOIN cfe.accounting_scope c1
                        ON c1.id_accounting_scope = c.id_accounting_scope
                            AND c1.id_status = 'C'
                    , common.ledger_currency d
                MINUS 
                SELECT DISTINCT
                    c.code
                    , d.id_currency
                    , NULL id_fee_type
                    , c.id_accounting_scope_code
                FROM cfe.ledger_account c
                    INNER JOIN common.ledger_currency d
                        ON c.id_currency = d.id_currency )
        UNION 
        SELECT *
        FROM (  SELECT DISTINCT
                    c.code code
                    , d.id_currency
                    , e.id_fee_type
                    , c1.id_accounting_scope_code
                FROM cfe.ledger_branch c
                    INNER JOIN cfe.accounting_scope c1
                        ON c1.id_accounting_scope = c.id_accounting_scope
                            AND c1.id_status = 'C'
                    , common.ledger_currency d
                    , cfe.fee_type e
                MINUS 
                SELECT DISTINCT
                    c.code
                    , d.id_currency
                    , e.id_fee_type
                    , c.id_accounting_scope_code
                FROM cfe.ledger_account c
                    INNER JOIN common.ledger_currency d
                        ON c.id_currency = d.id_currency
                    INNER JOIN cfe.fee_type e
                        ON c.id_fee_type = e.id_fee_type ) ) a
;

-- INSERT INTO LEDGER BRANCH BALANCE
WITH scope AS (
        SELECT *
        FROM cfe.accounting_scope
        WHERE id_status = 'C'
            AND id_accounting_scope_code = :SCOPE )
    , ex AS (
        SELECT *
        FROM cfe.execution
        WHERE id_status = 'R'
            AND value_date = (  SELECT Max( value_date )
                                FROM cfe.execution
                                WHERE id_status = 'R'
                                    AND ( :VALUE_DATE IS NULL
                                            OR value_date <= :VALUE_DATE ) ) )
    , fxr AS (
        SELECT  id_currency_from
                , fxrate
        FROM common.fxrate_hst f
            INNER JOIN ex
                ON f.value_date <= ex.value_date
        WHERE f.value_date = (  SELECT Max( value_date )
                                FROM common.fxrate_hst
                                WHERE id_currency_from = f.id_currency_from
                                    AND id_currency_into = f.id_currency_into
                                    AND value_date <= ex.value_date )
            AND id_currency_into = :BOOK_CURRENCY
        UNION ALL 
        SELECT  :BOOK_CURRENCY
                , 1
        FROM dual )
SELECT /*+ PARALLEL */
    scope.id_accounting_scope
    , ex.value_date
    , ex.posting_date
    , a.gl_level
    , a.code
    , b.description
    , c.balance_bc
FROM ex
    , scope
    INNER JOIN cfe.ledger_branch_branch a
        ON a.id_accounting_scope = scope.id_accounting_scope
            AND a.code = a.code_inferior
    INNER JOIN cfe.ledger_branch b
        ON b.id_accounting_scope = scope.id_accounting_scope
            AND b.code = a.code
    INNER JOIN (    SELECT  b.code
                            , Round( d.amount * fxr.fxrate, 2 ) balance_bc
                    FROM scope
                        INNER JOIN cfe.ledger_branch_branch b
                            ON b.id_accounting_scope = scope.id_accounting_scope
                        INNER JOIN cfe.ledger_account c
                            ON b.code_inferior = c.code
                                AND c.id_accounting_scope_code = scope.id_accounting_scope_code
                        INNER JOIN (    SELECT  id_account
                                                , Sum( amount ) balance
                                        FROM (  SELECT  id_account_credit id_account
                                                        , amount
                                                FROM cfe.ledger_account_entry
                                                    INNER JOIN ex
                                                        ON ledger_account_entry.posting_date <= ex.posting_date
                                                UNION ALL 
                                                SELECT  id_account_debit
                                                        , - amount
                                                FROM cfe.ledger_account_entry
                                                    INNER JOIN ex
                                                        ON ledger_account_entry.posting_date <= ex.posting_date )
                                        GROUP BY id_account ) d
                            ON c.id_account = d.id_account
                        INNER JOIN fxr
                            ON c.id_currency = fxr.id_currency_from
                    GROUP BY b.code ) c
        ON c.code = a.code
;

-- INSERT INTO LEDGER BRANCH BALANCE NEW
WITH scope AS (
        SELECT *
        FROM cfe.accounting_scope
        WHERE id_status = 'C'
            AND id_accounting_scope_code = :SCOPE )
    , ex AS (
        SELECT *
        FROM cfe.execution
        WHERE id_status = 'R'
            AND value_date = (  SELECT Max( value_date )
                                FROM cfe.execution
                                WHERE id_status = 'R'
                                    AND ( :VALUE_DATE IS NULL
                                            OR value_date <= :VALUE_DATE ) ) )
    , fxr AS (
        SELECT  id_currency_from
                , fxrate
        FROM common.fxrate_hst f
            INNER JOIN ex
                ON f.value_date <= ex.value_date
        WHERE f.value_date = (  SELECT Max( value_date )
                                FROM common.fxrate_hst
                                WHERE id_currency_from = f.id_currency_from
                                    AND id_currency_into = f.id_currency_into
                                    AND value_date <= ex.value_date )
            AND id_currency_into = :BOOK_CURRENCY
        UNION ALL 
        SELECT  :BOOK_CURRENCY
                , 1
        FROM dual )
SELECT /*+ PARALLEL */
    scope.id_accounting_scope
    , ex.value_date
    , ex.posting_date
    , a.gl_level
    , a.code
    , b.description
    , c.balance_bc
FROM ex
    , scope
    INNER JOIN cfe.ledger_branch_branch a
        ON a.id_accounting_scope = scope.id_accounting_scope
            AND a.code = a.code_inferior
    INNER JOIN cfe.ledger_branch b
        ON b.id_accounting_scope = scope.id_accounting_scope
            AND b.code = a.code
    INNER JOIN (    SELECT  b.code
                            , Round( d.amount * fxr.fxrate, 2 )
                    FROM scope
                        INNER JOIN cfe.ledger_branch_branch b
                            ON b.id_accounting_scope = scope.id_accounting_scope
                        INNER JOIN cfe.ledger_account c
                            ON b.code_inferior = c.code
                                AND c.id_accounting_scope_code = scope.id_accounting_scope_code
                        INNER JOIN (    SELECT  id_account
                                                , Sum( amount )
                                        FROM (  SELECT  id_account_credit
                                                        , amount
                                                FROM cfe.ledger_account_entry
                                                    INNER JOIN ex
                                                        ON ledger_account_entry.posting_date <= ex.posting_date
                                                UNION ALL 
                                                SELECT  id_account_debit
                                                        , - amount
                                                FROM cfe.ledger_account_entry
                                                    INNER JOIN ex
                                                        ON ledger_account_entry.posting_date <= ex.posting_date ) ) d
                            ON c.id_account = d.id_account
                        INNER JOIN fxr
                            ON c.id_currency = fxr.id_currency_from ) c
        ON c.code = a.code
;

-- APPEND COLLATERAL REF
SELECT /*+ PARALLEL */
    cfe.id_collateral_ref.nextval
    , id_collateral
FROM (  SELECT DISTINCT
            a.id_collateral
        FROM cfe.collateral a
            LEFT JOIN cfe.collateral_ref b
                ON a.id_collateral = b.id_collateral
        WHERE b.id_collateral_ref IS NULL )
;

-- APPEND COUNTER PARTY REF
SELECT /*+ PARALLEL */
    cfe.id_counter_party_ref.nextval
    , id_counter_party
FROM (  SELECT DISTINCT
            a.id_counter_party
        FROM cfe.collateral a
            LEFT JOIN cfe.counter_party_ref b
                ON a.id_counter_party = b.id_counter_party
        WHERE a.id_counter_party IS NOT NULL
            AND b.id_counter_party_ref IS NULL )
;

-- APPEND COLLATERAL HST
SELECT /*+ PARALLEL */
    b.id_collateral_ref
    , c.id_counter_party_ref
    , coalesce valid_date
    , a.description
    , d.id_collateral_type_ref
    , a.fair_value
    , a.forced_sale_value
    , a.id_currency
    , a.appraisal_date
FROM cfe.collateral a
    INNER JOIN cfe.collateral_ref b
        ON a.id_collateral = b.id_collateral
    LEFT JOIN cfe.counter_party_ref c
        ON a.id_counter_party = c.id_counter_party
    INNER JOIN (    SELECT *
                    FROM common.collateral_type d1
                    WHERE id_status IN ( 'C', 'H' )
                        AND id_collateral_type_ref = (  SELECT Max( id_collateral_type_ref )
                                                        FROM common.collateral_type
                                                        WHERE id_status IN ( 'C', 'H' )
                                                            AND id_collateral_type = d1.id_collateral_type ) ) d
        ON a.id_collateral_type = d.id_collateral_type
;

-- SELECT WITH COMPLEX ORDER
WITH ex AS (
        SELECT  value_date
                , posting_date
        FROM cfe.execution x
        WHERE id_status IN ( 'R', 'H' )
            AND value_date = (  SELECT Max( value_date )
                                FROM cfe.execution
                                WHERE id_status IN ( 'R', 'H' ) )
            AND posting_date = (    SELECT Max( posting_date )
                                    FROM cfe.execution
                                    WHERE id_status IN ( 'R', 'H' )
                                        AND value_date = x.value_date ) )
    , fxr AS (
        SELECT  id_currency_from
                , fxrate
        FROM common.fxrate_hst f
        WHERE f.value_date <= ( SELECT value_date
                                FROM ex )
            AND f.value_date = (    SELECT Max( value_date )
                                    FROM common.fxrate_hst
                                    WHERE id_currency_from = f.id_currency_from
                                        AND id_currency_into = f.id_currency_into )
            AND id_currency_into = 'NGN'
        UNION ALL 
        SELECT  'NGN'
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
        SELECT  b.code
                , Round( d.amount * fxr.fxrate, 2 ) balance_bc
        FROM scope
            INNER JOIN cfe.ledger_branch_branch b
                ON b.id_accounting_scope = scope.id_accounting_scope
            INNER JOIN cfe.ledger_account c
                ON b.code_inferior = c.code
                    AND c.id_accounting_scope_code = scope.id_accounting_scope_code
            INNER JOIN (    SELECT  id_account_credit id_account
                                    , amount
                            FROM cfe.ledger_account_entry
                                INNER JOIN ex
                                    ON ledger_account_entry.posting_date <= ex.posting_date
                            UNION ALL 
                            SELECT  id_account_debit
                                    , - amount
                            FROM cfe.ledger_account_entry
                                INNER JOIN ex
                                    ON ledger_account_entry.posting_date <= ex.posting_date ) d
                ON c.id_account = d.id_account
            INNER JOIN fxr
                ON c.id_currency = fxr.id_currency_from
        GROUP BY b.code )
    , c1 AS (
        SELECT  b.code
                , Round( d.amount * fxr.fxrate, 2 ) balance_bc
        FROM scope1
            INNER JOIN cfe.ledger_branch_branch b
                ON b.id_accounting_scope = scope1.id_accounting_scope
            INNER JOIN cfe.ledger_account c
                ON b.code_inferior = c.code
                    AND c.id_accounting_scope_code = scope1.id_accounting_scope_code
            INNER JOIN (    SELECT  id_account_credit id_account
                                    , amount
                            FROM cfe.ledger_account_entry
                                INNER JOIN ex
                                    ON ledger_account_entry.posting_date <= ex.posting_date
                            UNION ALL 
                            SELECT  id_account_debit
                                    , - amount
                            FROM cfe.ledger_account_entry
                                INNER JOIN ex
                                    ON ledger_account_entry.posting_date <= ex.posting_date ) d
                ON c.id_account = d.id_account
            INNER JOIN fxr
                ON c.id_currency = fxr.id_currency_from
        GROUP BY b.code )
SELECT /*+ PARALLEL */
    a.code code
    , Lpad( ' ', 4 * ( a.gl_level - 1 ), ' ' )
             || a.code format_code
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
ORDER BY    (   SELECT code
                FROM cfe.ledger_branch_branch
                WHERE id_accounting_scope = a.id_accounting_scope
                    AND code_inferior = a.code
                    AND gl_level = 1 ) NULLS FIRST
            , ( SELECT code
                FROM cfe.ledger_branch_branch
                WHERE id_accounting_scope = a.id_accounting_scope
                    AND code_inferior = a.code
                    AND gl_level = 2 ) ASC NULLS FIRST
            , ( SELECT code
                FROM cfe.ledger_branch_branch
                WHERE id_accounting_scope = a.id_accounting_scope
                    AND code_inferior = a.code
                    AND gl_level = 3 ) DESC NULLS FIRST
            , ( SELECT code
                FROM cfe.ledger_branch_branch
                WHERE id_accounting_scope = a.id_accounting_scope
                    AND code_inferior = a.code
                    AND gl_level = 4 ) DESC
            , ( SELECT code
                FROM cfe.ledger_branch_branch
                WHERE id_accounting_scope = a.id_accounting_scope
                    AND code_inferior = a.code
                    AND gl_level = 5 ) ASC
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

-- ALL COLUMNS FROM TABLE
SELECT a.*
FROM cfe.instrument a
;
