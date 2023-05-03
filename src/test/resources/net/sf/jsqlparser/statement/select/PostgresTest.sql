-- testJSonExpressionIssue1696
select '{"key":"value"}'::json->'key' as x
;

-- testExtractFunction
select extract(hour from timestamp '2001-02-16 20:38:40')
;

-- testExtractFunction
select extract('hour' from timestamp '2001-02-16 20:38:40')
;

-- testExtractFunction
select extract('hours' from timestamp '2001-02-16 20:38:40')
;

-- testExtractFunctionIssue1582
select t0.operatienr,case when case when(t0.vc_begintijd_operatie is null or lpad((extract('hours' from t0.vc_begintijd_operatie::timestamp))::text,2,'0')||':'||lpad(extract('minutes' from t0.vc_begintijd_operatie::timestamp)::text,2,'0')='00:00')then null else(greatest(((extract('hours' from(t0.vc_eindtijd_operatie::timestamp-t0.vc_begintijd_operatie::timestamp))*60+extract('minutes' from(t0.vc_eindtijd_operatie::timestamp-t0.vc_begintijd_operatie::timestamp)))/60)::numeric(12,2),0))*60 end=0 then null else '25. meer dan 4 uur' end as snijtijd_interval
;

