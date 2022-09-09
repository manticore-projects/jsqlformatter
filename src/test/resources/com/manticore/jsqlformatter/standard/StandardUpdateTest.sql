
-- UPDATE COUNTERPARTY
UPDATE risk.counterparty
SET id_counterparty = :id_counterparty
    , label = :label
    , description = :description
    , id_counterparty_group_type = :id_counterparty_group_type
    , id_counterparty_type = :id_counterparty_type
    , id_counterparty_sub_type = :id_counterparty_sub_type
    , id_country_group = :id_country_group
    , id_country = :id_country
    , id_country_state = :id_country_state
    , id_district = :id_district
    , id_city = :id_city
    , id_industrial_sector = :id_industrial_sector
    , id_industrial_sub_sector = :id_industrial_sub_sector
    , block_auto_update_flag = :block_auto_update_flag
    , id_user_editor = :id_user_editor
    , id_organization_unit = :id_organization_unit
    , id_status = :id_status
    , update_timestamp = current_timestamp
WHERE id_counterparty_ref = :id_counterparty_ref
;

-- UPDATE COLLATERAL_TYPE
UPDATE common.collateral_type
SET hair_cut = least
WHERE id_collateral_type_ref IN (   SELECT id_collateral_type_ref
                                    FROM common.collateral_type a
                                    WHERE id_status IN (    'C', 'H', 'C'
                                                            , 'H', 'C', 'H'
                                                            , 'C', 'H' )
                                        AND id_collateral_type_ref = (  SELECT Max( id_collateral_type_ref )
                                                                        FROM common.collateral_type
                                                                        WHERE id_status IN ( 'C', 'H' )
                                                                            AND id_collateral_type = a.id_collateral_type ) )
;

-- UPDATE COUNTERPARTY_INSTRUMENT
UPDATE risk.counterparty_instrument a1
SET (   priority
        , type
        , description
        , limit_amout
        , id_currency
        , end_date ) = (    SELECT  a.priority
                                    , a.type
                                    , a.description
                                    , a.limit_amout
                                    , a.id_currency
                                    , a.end_date
                            FROM risk.imp_counterparty_instrument a
                                INNER JOIN risk.counterparty b
                                    ON a.id_counterparty = b.id_counterparty
                                        AND b.id_status = 'C'
                                INNER JOIN risk.instrument c
                                    ON a.id_instrument_beneficiary = c.id_instrument
                                        AND c.id_status = 'C'
                                INNER JOIN risk.counterparty_instrument e
                                    ON b.id_counterparty_ref = e.id_counterparty_ref
                                        AND e.id_instrument_beneficiary = a.id_instrument_beneficiary
                                        AND e.id_instrument_guarantee = a.id_instrument_guarantee
                            WHERE e.id_counterparty_ref = a1.id_counterparty_ref
                                AND e.id_instrument_beneficiary = a1.id_instrument_beneficiary
                                AND e.id_instrument_guarantee = a1.id_instrument_guarantee )
WHERE EXISTS (  SELECT  a.priority
                        , a.type
                        , a.description
                        , a.limit_amout
                        , a.id_currency
                        , a.end_date
                FROM risk.imp_counterparty_instrument a
                    INNER JOIN risk.counterparty b
                        ON a.id_counterparty = b.id_counterparty
                            AND b.id_status = 'C'
                    INNER JOIN risk.instrument c
                        ON a.id_instrument_beneficiary = c.id_instrument
                            AND c.id_status = 'C'
                    INNER JOIN risk.counterparty_instrument e
                        ON b.id_counterparty_ref = e.id_counterparty_ref
                            AND e.id_instrument_beneficiary = a.id_instrument_beneficiary
                            AND e.id_instrument_guarantee = a.id_instrument_guarantee
                WHERE e.id_counterparty_ref = a1.id_counterparty_ref
                    AND e.id_instrument_beneficiary = a1.id_instrument_beneficiary
                    AND e.id_instrument_guarantee = a1.id_instrument_guarantee )
;

-- UPDATE SETS ISSUE 1316
UPDATE prpjpaymentbill b
SET (   b.packagecode
        , b.packageremark
        , b.agentcode ) = ( SELECT  p.payrefreason
                                    , p.classcode
                                    , p.riskcode
                            FROM prpjcommbill p
                            WHERE p.policertiid = 'SDDH200937010330006366' )   /* this is supposed to be UpdateSet 1 */
    , b.payrefnotype = '05'                                                    /* this is supposed to be UpdateSet 2 */
    , b.packageunit = '4101170402'                                             /* this is supposed to be UpdateSet 3 */
WHERE b.payrefno = 'B370202091026000005'
;

-- UPDATE START JOINS
UPDATE sc_borrower b
    INNER JOIN sc_credit_apply a
        ON a.borrower_id = b.id
SET b.name = '0.7505105896846266'
    , a.credit_line = a.credit_line + 1
WHERE b.id = 3
;

-- UPDATE JOINS
UPDATE table1
SET columna = 5
FROM table1
    LEFT JOIN table2
        ON col1 = col2
;
