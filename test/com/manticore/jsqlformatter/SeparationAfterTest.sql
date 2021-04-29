
-- INSERT NEW LEDGER ACCOUNTS
-- @JSQLFormatter(indentWidth=4, keywordSpelling=LOWER, functionSpelling=KEEP, objectSpelling=UPPER, separation=AFTER)
select /*+ parallel */
    CFE.ID_ACCOUNT_SEQ.NEXTVAL,
    "a"."code",
    A."id_currency",
    A.ID_FEE_TYPE,
    current_date,
    NULL,
    ID_ACCOUNTING_SCOPE_CODE
from (  select *
        from (  select distinct
                    C.CODE CODE,
                    D.ID_CURRENCY,
                    NULL ID_FEE_TYPE,
                    C1.ID_ACCOUNTING_SCOPE_CODE
                from CFE.LEDGER_BRANCH C
                    inner join CFE.ACCOUNTING_SCOPE C1
                        on C1.ID_ACCOUNTING_SCOPE = C.ID_ACCOUNTING_SCOPE
                            AND C1.ID_STATUS = 'C',
                    COMMON.LEDGER_CURRENCY D
                MINUS 
                select distinct
                    C.CODE,
                    D.ID_CURRENCY,
                    NULL ID_FEE_TYPE,
                    C.ID_ACCOUNTING_SCOPE_CODE
                from CFE.LEDGER_ACCOUNT C
                    inner join COMMON.LEDGER_CURRENCY D
                        on C.ID_CURRENCY = D.ID_CURRENCY )
        UNION 
        select *
        from (  select distinct
                    C.CODE,
                    D.ID_CURRENCY,
                    NULL ID_FEE_TYPE,
                    C1.ID_ACCOUNTING_SCOPE_CODE
                from CFE.LEDGER_BRANCH C
                    inner join CFE.ACCOUNTING_SCOPE C1
                        on C1.ID_ACCOUNTING_SCOPE = C.ID_ACCOUNTING_SCOPE
                            AND C1.ID_STATUS = 'C',
                    COMMON.LEDGER_CURRENCY D
                MINUS 
                select distinct
                    C.CODE,
                    D.ID_CURRENCY,
                    NULL ID_FEE_TYPE,
                    C.ID_ACCOUNTING_SCOPE_CODE
                from CFE.LEDGER_ACCOUNT C
                    inner join COMMON.LEDGER_CURRENCY D
                        on C.ID_CURRENCY = D.ID_CURRENCY )
        UNION 
        select *
        from (  select distinct
                    C.CODE CODE,
                    D.ID_CURRENCY,
                    E.ID_FEE_TYPE,
                    C1.ID_ACCOUNTING_SCOPE_CODE
                from CFE.LEDGER_BRANCH C
                    inner join CFE.ACCOUNTING_SCOPE C1
                        on C1.ID_ACCOUNTING_SCOPE = C.ID_ACCOUNTING_SCOPE
                            AND C1.ID_STATUS = 'C',
                    COMMON.LEDGER_CURRENCY D,
                    CFE.FEE_TYPE E
                MINUS 
                select distinct
                    C.CODE,
                    D.ID_CURRENCY,
                    E.ID_FEE_TYPE,
                    C.ID_ACCOUNTING_SCOPE_CODE
                from CFE.LEDGER_ACCOUNT C
                    inner join COMMON.LEDGER_CURRENCY D
                        on C.ID_CURRENCY = D.ID_CURRENCY
                    inner join CFE.FEE_TYPE E
                        on C.ID_FEE_TYPE = E.ID_FEE_TYPE ) )  A
;

-- INSERT INTO LEDGER BRANCH BALANCE
with SCOPE as (
        select *
        from CFE.ACCOUNTING_SCOPE
        where ID_STATUS = 'C'
            AND ID_ACCOUNTING_SCOPE_CODE = :SCOPE ),
    EX as (
        select *
        from CFE.EXECUTION
        where ID_STATUS = 'R'
            AND VALUE_DATE = (  select Max( VALUE_DATE )
                                from CFE.EXECUTION
                                where ID_STATUS = 'R'
                                    AND ( :VALUE_DATE IS NULL
                                            OR VALUE_DATE <= :VALUE_DATE ) ) ),
    FXR as (
        select  ID_CURRENCY_FROM,
                FXRATE
        from COMMON.FXRATE_HST F
            inner join EX
                on F.VALUE_DATE <= EX.VALUE_DATE
        where F.VALUE_DATE = (  select Max( VALUE_DATE )
                                from COMMON.FXRATE_HST
                                where ID_CURRENCY_FROM = F.ID_CURRENCY_FROM
                                    AND ID_CURRENCY_INTO = F.ID_CURRENCY_INTO
                                    AND VALUE_DATE <= EX.VALUE_DATE )
            AND ID_CURRENCY_INTO = :BOOK_CURRENCY
        UNION ALL 
        select  :BOOK_CURRENCY,
                1
        from DUAL )
select /*+ parallel */
    SCOPE.ID_ACCOUNTING_SCOPE,
    EX.VALUE_DATE,
    EX.POSTING_DATE,
    A.GL_LEVEL,
    A.CODE,
    B.DESCRIPTION,
    C.BALANCE_BC
from EX,
    SCOPE
    inner join CFE.LEDGER_BRANCH_BRANCH A
        on A.ID_ACCOUNTING_SCOPE = SCOPE.ID_ACCOUNTING_SCOPE
            AND A.CODE = A.CODE_INFERIOR
    inner join CFE.LEDGER_BRANCH B
        on B.ID_ACCOUNTING_SCOPE = SCOPE.ID_ACCOUNTING_SCOPE
            AND B.CODE = A.CODE
    inner join (    select  B.CODE,
                            Round( D.AMOUNT * FXR.FXRATE, 2 ) BALANCE_BC
                    from SCOPE
                        inner join CFE.LEDGER_BRANCH_BRANCH B
                            on B.ID_ACCOUNTING_SCOPE = SCOPE.ID_ACCOUNTING_SCOPE
                        inner join CFE.LEDGER_ACCOUNT C
                            on B.CODE_INFERIOR = C.CODE
                                AND C.ID_ACCOUNTING_SCOPE_CODE = SCOPE.ID_ACCOUNTING_SCOPE_CODE
                        inner join (    select  ID_ACCOUNT,
                                                Sum( AMOUNT ) BALANCE
                                        from (  select  ID_ACCOUNT_CREDIT ID_ACCOUNT,
                                                        AMOUNT
                                                from CFE.LEDGER_ACCOUNT_ENTRY
                                                    inner join EX
                                                        on LEDGER_ACCOUNT_ENTRY.POSTING_DATE <= EX.POSTING_DATE
                                                UNION ALL 
                                                select  ID_ACCOUNT_DEBIT,
                                                        - AMOUNT
                                                from CFE.LEDGER_ACCOUNT_ENTRY
                                                    inner join EX
                                                        on LEDGER_ACCOUNT_ENTRY.POSTING_DATE <= EX.POSTING_DATE )
                                        group by ID_ACCOUNT )  D
                            on C.ID_ACCOUNT = D.ID_ACCOUNT
                        inner join FXR
                            on C.ID_CURRENCY = FXR.ID_CURRENCY_FROM
                    group by B.CODE )  C
        on C.CODE = A.CODE
;

-- INSERT COUNTERPARTY COUNTERPARTY RELATIONSHIP
insert into RISK.COUNTERPARTY_COUNTERPARTY
values (    :id_counterparty_ref, :id_counterparty_beneficiary, :id_instrument_guarantee,
            :priority, :type, :description,
            :limit_amout, :id_currency, :end_date )
;

-- INSERT RATIO COLLECTION RATIOS
insert into RISK.COUNTERPARTY_RATIO
values ( ?, ?, ? )
;

-- INSERT TMP_CCF
insert into RISK.TMP_CCF (
    "ID_INSTRUMENT",
    "TENOR",
    "STATUS",
    "OBSERVATION_DATE",
    "BALANCE",
    "LIMIT",
    "DR_BALANCE",
    "OPEN_LIMIT" )
select  '1000042339'       /* ID_INSTRUMENT */,
        0                  /* TENOR */,
        'DEFAULT'          /* STATUS */,
        {d '2020-02-27'}   /* OBSERVATION_DATE */,
        - 142574953.65     /* BALANCE */,
        300000000          /* LIMIT */,
        - 142574953.65     /* DR_BALANCE */,
        157425046.35       /* OPEN_LIMIT */
from DUAL
;

-- APPEND ATTRIBUTE VALUE REF
insert into CFE.ATTRIBUTE_VALUE_REF
select  CFE.ID_ATTRIBUTE_VALUE_REF.NEXTVAL,
        ATTRIBUTE_VALUE
from (  select distinct
            A.ATTRIBUTE_VALUE
        from CFE.INSTRUMENT_ATTRIBUTE A
            left join CFE.ATTRIBUTE_VALUE_REF B
                on A.ATTRIBUTE_VALUE = B.ATTRIBUTE_VALUE
        where B.ATTRIBUTE_VALUE IS NULL )  A
;
