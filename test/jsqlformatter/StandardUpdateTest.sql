-- UPDATE COUNTERPARTY
UPDATE /*+PARALLEL*/ risk.counterparty
SET    id_counterparty = :id_counterparty
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
WHERE  id_counterparty_ref = :id_counterparty_ref
;

-- UPDATE COLLATERAL_TYPE
update common.collateral_type
set hair_cut = Least( hair_cut * 1.25, 1.00)
where id_collateral_type_ref in (
select id_collateral_type_ref
from common.collateral_type a
where id_status in ('C', 'H', 'C', 'H', 'C', 'H', 'C', 'H')
and id_collateral_type_ref = ( select max(id_collateral_type_ref)
                                from common.collateral_type
                                where id_status in ('C', 'H')
                                and id_collateral_type = a.id_collateral_type)
);

-- UPDATE COUNTERPARTY_INSTRUMENT
update /*+parallel*/ risk.counterparty_instrument a1
set
    (PRIORITY, TYPE, DESCRIPTION, LIMIT_AMOUT, ID_CURRENCY, END_DATE) =
(select
    a.PRIORITY
    , a.TYPE
    , a.DESCRIPTION
    , a.LIMIT_AMOUT
    , a.ID_CURRENCY
    , a.END_DATE
from risk.imp_counterparty_instrument a
    inner join risk.counterparty b on a.id_counterparty=b.id_counterparty and b.id_status='C'
    inner join risk.instrument c on a.ID_instrument_BENEFICIARY=c.id_instrument and c.id_status='C'
    inner join risk.counterparty_instrument e on b.id_counterparty_ref=e.id_counterparty_ref
        and e.ID_instrument_BENEFICIARY=a.ID_instrument_BENEFICIARY
        and e.ID_INSTRUMENT_GUARANTEE=a.ID_INSTRUMENT_GUARANTEE
where
    e.id_counterparty_ref=a1.id_counterparty_ref
    and e.ID_instrument_BENEFICIARY=a1.ID_instrument_BENEFICIARY
    and e.ID_INSTRUMENT_GUARANTEE=a1.ID_INSTRUMENT_GUARANTEE
) where exists (select
    a.PRIORITY
    , a.TYPE
    , a.DESCRIPTION
    , a.LIMIT_AMOUT
    , a.ID_CURRENCY
    , a.END_DATE
from risk.imp_counterparty_instrument a
    inner join risk.counterparty b on a.id_counterparty=b.id_counterparty and b.id_status='C'
    inner join risk.instrument c on a.ID_instrument_BENEFICIARY=c.id_instrument and c.id_status='C'
    inner join risk.counterparty_instrument e on b.id_counterparty_ref=e.id_counterparty_ref
        and e.ID_instrument_BENEFICIARY=a.ID_instrument_BENEFICIARY
        and e.ID_INSTRUMENT_GUARANTEE=a.ID_INSTRUMENT_GUARANTEE
where
    e.id_counterparty_ref=a1.id_counterparty_ref
    and e.ID_instrument_BENEFICIARY=a1.ID_instrument_BENEFICIARY
    and e.ID_INSTRUMENT_GUARANTEE=a1.ID_INSTRUMENT_GUARANTEE
);

