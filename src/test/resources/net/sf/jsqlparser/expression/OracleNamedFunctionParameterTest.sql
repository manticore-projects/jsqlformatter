-- testExpression
select r.*,test.numeric_function(p_1=>r.param1,p_2=>r.param2)as resultaat2
;

-- testExpression
exec dbms_stats.gather_schema_stats(ownname=>'common',estimate_percent=>dbms_stats.auto_sample_size,method_opt=>'for all columns size auto',degree=>dbms_stats.default_degree,cascade=>dbms_stats.auto_cascade,options=>'gather auto')
;

