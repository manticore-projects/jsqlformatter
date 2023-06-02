-- Keyword Spelling of NULL, DISTINCT, INTERVAL
-- @JSQLFormatter(keywordSpelling=LOWER)
update sms_queue sq
set sq.gateway_sms_id = null
    , sq.date_sent_sms = null
    , sq.sms_status_id = 1
    , sq.date_added = Now() - interval 1 minute
    , sq.date_should_sent = Now() - interval 1 minute
where sq.sms_id = 2864158
;

select  ccp.product_id
        , Count( distinct ccp.campaign_id )
from campaign_constraint_product ccp
    left join campaign c
        on c.campaign_id = ccp.campaign_id
            and c.status = 1
where product_id in ( 414732, 530729 )
group by ccp.product_id
order by 2 desc
limit 10
;