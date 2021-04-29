-- DELETE LEDGER BRANCH BALANCE
DELETE FROM cfe.LEDGER_BRANCH_BALANCE
WHERE (value_date, posting_date) = (    SELECT  value_date
                                                , posting_date
                                        FROM cfe.execution
                                        WHERE id_status = 'R'
                                            AND value_date = :VALUE_DATE )
;

-- DELETE WITH MANY ITEMS
DELETE FROM cfe.LEDGER_BRANCH_BALANCE
WHERE (value_date, posting_date, something_else ) = (    SELECT  value_date
                                                , posting_date
                                                , something_else
                                        FROM cfe.execution
                                        WHERE id_status = 'R'
                                            AND value_date = :VALUE_DATE )
;

-- DELETE WITH MORE ITEMS
DELETE FROM cfe.LEDGER_BRANCH_BALANCE
WHERE (value_date, posting_date, something_else, value_date ) = (    SELECT  value_date
                                                , posting_date
                                                , something_else
                                                , value_date
                                        FROM cfe.execution
                                        WHERE id_status = 'R'
                                            AND value_date = :VALUE_DATE )
;

-- DELETE WITH EVEN MORE ITEMS
DELETE FROM cfe.LEDGER_BRANCH_BALANCE
WHERE (value_date, posting_date, something_else, value_date, posting_date, something_else ) = (    SELECT  value_date
                                                , posting_date
                                                , something_else
                                                , value_date, posting_date, something_else
                                        FROM cfe.execution
                                        WHERE id_status = 'R'
                                            AND value_date = :VALUE_DATE )
;

-- DELETE INSTRUMENT HST AFTER VALUE_DATE_P
delete /*+PARALLEL index_ffs(a, instrument_hst_idx1)*/ from cfe.instrument_hst a
where (value_date, posting_date) in (
    select value_date, posting_date
    from cfe.execution
    where posting_date > (select max(posting_date) from cfe.execution where id_status='R' and value_date <= :value_date_p)
        or (select max(posting_date) from cfe.execution where id_status='R' and value_date <= :value_date_p) is null
);


-- DELETE REDUNDANT INSTRUMENT COLLATERAL HST 2
DELETE FROM cfe.instrument_collateral_hst t1
WHERE EXISTS (  SELECT  1
                FROM cfe.instrument_collateral a
                    INNER JOIN cfe.collateral_ref b
                        ON a.id_collateral = b.id_collateral
                    INNER JOIN cfe.instrument_ref c
                        ON a.id_instrument = c.id_instrument
                WHERE b.id_collateral_ref = t1.id_collateral_ref
                    AND c.id_instrument_ref = t1.id_instrument_ref
                    AND a.valid_date = t1.valid_date )
;

-- DELETE ACCOUNT ENTRIES AFTER VALUE_DATE_P
DELETE FROM cfe.ledger_account_entry a
WHERE posting_date IN  (    SELECT  posting_date
                            FROM cfe.execution
                            WHERE posting_date > (  SELECT  max( posting_date )
                                                    FROM cfe.execution
                                                    WHERE id_status = 'R'
                                                        AND value_date <= :value_date_p )
                                OR (    SELECT  max( posting_date )
                                        FROM cfe.execution
                                        WHERE id_status = 'R'
                                            AND value_date <= :value_date_p ) IS NULL )
    AND reversed = '0'
;