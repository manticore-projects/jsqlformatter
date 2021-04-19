
-- CREATE TABLE CFE.INTEREST_PERIOD
CREATE TABLE cfe.interest_period (
    id_instrument                VARCHAR (40)    NOT NULL
    , id_fixingmode              NUMBER (5)      DEFAULT 0 NOT NULL
    , fixing_date                DATE
    , change_date                DATE
    , base_rate                  VARCHAR (12)
    , base_margin_rate           DECIMAL (12,9)
    , par_rate                   VARCHAR (12)
    , par_margin_rate            DECIMAL (12,9)
    , id_payment_convention      VARCHAR (12)
    , id_day_count_convention    VARCHAR (12)
    , id_day_incl_convention     VARCHAR (12)
    , fix_amount                 DECIMAL (23,5)
    , id_currency_fix_amount     VARCHAR (3)
    , id_script                  VARCHAR (12)
  )
;

-- SIMPLE
CREATE TABLE employees (
    employee_number      int         NOT NULL
    , employee_name      char (50)   NOT NULL
    , department_id      int
    , salary             int
    , PRIMARY KEY ( employee_number )
    , UNIQUE ( employee_name )
    , FOREIGN KEY ( department_id )
        REFERENCES departments ( department_id )
  ) parallel compress nologging
;

-- COMPLEX
CREATE TABLE employees (
    employee_number      int         NOT NULL
    , employee_name      char (50)   NOT NULL
    , department_id      int
    , salary             int
    , CONSTRAINT employees_pk
        PRIMARY KEY ( employee_number )
    , CONSTRAINT fk_departments
        FOREIGN KEY ( department_id )
        REFERENCES departments ( department_id )
  ) parallel compress nologging
;

-- COMPLEX WITH MANY REFERENCES
CREATE TABLE employees (
    employee_number      int         NOT NULL
    , employee_name      char (50)   NOT NULL
    , department_id      int
    , salary             int
    , CONSTRAINT employees_pk
        PRIMARY KEY (   employee_number
                        , employee_name
                        , department_id )
    , CONSTRAINT fk_departments
        FOREIGN KEY (   employee_number
                        , employee_name
                        , department_id )
        REFERENCES departments (    employee_number
                                    , employee_name
                                    , department_id )
  ) parallel compress nologging
;

-- CREATE TABLE CFE.RECONCILIATION_NOMINAL_HST 2
CREATE TABLE cfe.RECONCILIATION_NOMINAL_hst parallel compress nologging
    AS ( SELECT /*+ parallel */ 
            (   SELECT  id_execution_ref
                FROM cfe.execution_ref c 
                    INNER JOIN cfe.execution_v d 
                        ON c.value_date = d.value_date
                            AND c.posting_date = d.posting_date
                            AND d.flag = 'L' ) id_execution_ref 
            , b.ID_INSTRUMENT_REF
            , a.VALUE_DATE
            , a.NOMINAL_BALANCE
        FROM cfe.RECONCILIATION_NOMINAL a 
            INNER JOIN cfe.instrument_ref b 
                ON a.id_instrument = b.id_instrument )
;
