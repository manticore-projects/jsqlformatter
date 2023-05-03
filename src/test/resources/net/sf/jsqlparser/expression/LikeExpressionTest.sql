-- testLikeWithEscapeExpressionIssue420
select*from dual where a like ?1 escape ?2
;

-- testEscapingIssue578
select*from t1 where upper(t1.tipcor_a8)like ? escape '' order by perfilb2||translate(upper(ap1sol10||' '||ap2sol10||','||nomsol10),'?','a')asc
;

-- testEscapingIssue827
insert into my_table(my_column_1,my_column_2)values('my_value_1\','my_value_2')
;

-- testEscapingIssue832
select*from t1 where(name like ? escape '\')and(description like ? escape '\')
;

-- testEscapingIssue875
insert into standard_table(gmt_create,gmt_modified,config_name,standard_code)values(now(),now(),null,'if @fac.sql_type in [ ''update'',''delete'',''insert'',''insert_select'']then @act.allow_submit end ')
;

-- testEscapingIssue875
insert into standard_table(gmt_create,gmt_modified,config_name,standard_code)values(now(),now(),null,'if @fac.sql_type in [ \'update\',\'delete\',\'insert\',\'insert_select\']then @act.allow_submit end ')
;

-- testEscapingIssue1172
select a alia1,case when b like 'abc\_%' escape '\' then 'def' else 'cccc' end as obj_sub_type from table2
;

-- testEscapingIssue1173
update param_tbl set para_desc=null where para_desc='\' and default_value='\'
;

-- testEscapingIssue1209
insert into "a"."b"("c","d","e")values('c c\','dd','ee\')
;

-- testEscapeExpressionIssue1638
select case when id_portfolio like '%\_1' escape '\' then '1' end
;

