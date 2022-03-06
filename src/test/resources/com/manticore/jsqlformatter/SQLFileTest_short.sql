DELETE FROM cfe.ledger_branch_balance
WHERE ( value_date, posting_date, something_else
        , value_date, posting_date, something_else ) = (    SELECT  value_date
                                                                    , posting_date
                                                                    , something_else
                                                                    , value_date
                                                                    , posting_date
                                                                    , something_else
                                                            FROM cfe.execution
                                                            WHERE id_status = 'R'
                                                                AND value_date = :VALUE_DATE )
;
