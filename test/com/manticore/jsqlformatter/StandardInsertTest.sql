
-- INSERT COUNTERPARTY COUNTERPARTY RELATIONSHIP
INSERT INTO risk.counterparty_counterparty
VALUES (    :id_counterparty_ref, :id_counterparty_beneficiary, :id_instrument_guarantee
            , :priority, :type, :description
            , :limit_amout, :id_currency, :end_date )
;

-- INSERT RATIO COLLECTION RATIOS
INSERT INTO risk.counterparty_ratio
VALUES ( ?, ?, ? )
;

-- INSERT TMP_CCF
INSERT INTO risk.tmp_ccf (
    "ID_INSTRUMENT"
    , "TENOR"
    , "STATUS"
    , "OBSERVATION_DATE"
    , "BALANCE"
    , "LIMIT"
    , "DR_BALANCE"
    , "OPEN_LIMIT" )
SELECT  '1000042339'       /* ID_INSTRUMENT */
        , 0                /* TENOR */
        , 'DEFAULT'        /* STATUS */
        , {d '2020-02-27'} /* OBSERVATION_DATE */
        , - 142574953.65   /* BALANCE */
        , 300000000        /* LIMIT */
        , - 142574953.65   /* DR_BALANCE */
        , 157425046.35     /* OPEN_LIMIT */
FROM dual
;

-- APPEND ATTRIBUTE VALUE REF
INSERT INTO cfe.attribute_value_ref
SELECT  cfe.id_attribute_value_ref.nextval
        , attribute_value
FROM (  SELECT DISTINCT
            a.attribute_value
        FROM cfe.instrument_attribute a
            LEFT JOIN cfe.attribute_value_ref b
                ON a.attribute_value = b.attribute_value
        WHERE b.attribute_value IS NULL )  a
;
