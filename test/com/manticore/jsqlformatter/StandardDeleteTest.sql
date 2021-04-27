-- DELETE LEDGER BRANCH BALANCE
DELETE FROM cfe.LEDGER_BRANCH_BALANCE
WHERE (value_date, posting_date) = (    SELECT  value_date
                                                , posting_date
                                        FROM cfe.execution
                                        WHERE id_status = 'R'
                                            AND value_date = :VALUE_DATE )
;

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