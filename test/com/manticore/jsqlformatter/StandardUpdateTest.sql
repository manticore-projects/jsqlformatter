
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
SET hair_cut = Least
WHERE id_collateral_type_ref IN  (  SELECT  id_collateral_type_ref
                                    FROM common.collateral_type a 
                                    WHERE id_status IN  ( 'C', 'H', 'C'
                                                            , 'H', 'C', 'H'
                                                            , 'C', 'H' ) 
                                        AND id_collateral_type_ref = (  SELECT  max( id_collateral_type_ref )
                                                                        FROM common.collateral_type
                                                                        WHERE id_status IN  ( 'C', 'H' ) 
                                                                            AND id_collateral_type = a.id_collateral_type ) ) 
;

-- UPDATE COUNTERPARTY_INSTRUMENT
UPDATE risk.counterparty_instrument a1 
SET (   PRIORITY
        , TYPE
        , DESCRIPTION
        , LIMIT_AMOUT
        , ID_CURRENCY
        , END_DATE ) = (    SELECT  a.PRIORITY
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
WHERE EXISTS (  SELECT  a.PRIORITY
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
