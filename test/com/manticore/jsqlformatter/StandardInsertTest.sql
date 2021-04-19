
-- INSERT COUNTERPARTY COUNTERPARTY RELATIONSHIP
INSERT INTO risk.counterparty_counterparty
VALUES ( :id_counterparty_ref
            , :id_counterparty_beneficiary
            , :id_instrument_guarantee
            , :priority
            , :type
            , :description
            , :limit_amout
            , :id_currency
            , :end_date ) 
;

-- INSERT RATIO COLLECTION RATIOS
INSERT INTO risk.counterparty_ratio
VALUES ( ?
            , ?
            , ? ) 
;

-- INSERT TMP_CCF
INSERT INTO RISK.TMP_CCF (
    "ID_INSTRUMENT"
    , "TENOR"
    , "STATUS"
    , "OBSERVATION_DATE"
    , "BALANCE"
    , "LIMIT"
    , "DR_BALANCE"
    , "OPEN_LIMIT" ) 
VALUES ( '1000042339'
            , 0
            , 'DEFAULT'
            , {d '2020-02-27'}
            , - 142574953.65
            , 300000000
            , - 142574953.65
            , 157425046.35 ) 
;
