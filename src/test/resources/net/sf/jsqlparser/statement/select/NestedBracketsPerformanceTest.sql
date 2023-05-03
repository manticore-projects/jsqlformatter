-- testNestedCaseWhenWithBracketsIssue1162
create view view_name1 as select case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else(case when wdgfld.porttype=1 then 'input port' else '0' end)end)end)end)end)end)end)end)end)end)end)end)end)end columnalias from table1
;

-- doIncreaseOfParseTimeTesting
select if(1=1,1,2)from mytbl
;

-- doIncreaseOfParseTimeTesting
select if(1=1,if(1=1,1,2),2)from mytbl
;

-- doIncreaseOfParseTimeTesting
select if(1=1,if(1=1,if(1=1,1,2),2),2)from mytbl
;

-- doIncreaseOfParseTimeTesting
select if(1=1,if(1=1,if(1=1,if(1=1,1,2),2),2),2)from mytbl
;

-- doIncreaseOfParseTimeTesting
select if(1=1,if(1=1,if(1=1,if(1=1,if(1=1,1,2),2),2),2),2)from mytbl
;

-- doIncreaseOfParseTimeTesting
select if(1=1,if(1=1,if(1=1,if(1=1,if(1=1,if(1=1,1,2),2),2),2),2),2)from mytbl
;

-- doIncreaseOfParseTimeTesting
select if(1=1,if(1=1,if(1=1,if(1=1,if(1=1,if(1=1,if(1=1,1,2),2),2),2),2),2),2)from mytbl
;

-- doIncreaseOfParseTimeTesting
select if(1=1,if(1=1,if(1=1,if(1=1,if(1=1,if(1=1,if(1=1,if(1=1,1,2),2),2),2),2),2),2),2)from mytbl
;

-- testIssue1013_2
SELECT * FROM ((((((((((((((((tblA))))))))))))))))
;

-- testIssue1013_3
SELECT * FROM (((tblA)))
;

-- testIssue1013_4
SELECT * FROM (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((tblA)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
;

-- testNestedCaseWhenWithoutBracketsIssue1162
create view view_name1 as select case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else case when wdgfld.porttype=1 then 'input port' else '0' end end end end end end end end end end end end end end columnalias from table1
;

-- testIssue766_2
SELECT concat(concat(concat('1', '2'), '3'), '4'), col1 FROM tbl t1
;

-- testIssue1013
SELECT ((((((((((((((((tblA)))))))))))))))) FROM mytable
;

-- testIssue1103
select round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(round(0,0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0),0)
;

-- testIssue235
select case when(case when(case when(case when(1)then 0 end)then 0 end)then 0 end)then 0 end from a
;

-- testIssue766
select concat(concat(concat(concat(concat(concat(concat(concat(concat(concat(concat(concat(concat(concat(concat(concat(concat(concat(concat(concat('1','2'),'3'),'4'),'5'),'6'),'7'),'8'),'9'),'10'),'11'),'12'),'13'),'14'),'15'),'16'),'17'),'18'),'19'),'20'),'21'),col1 from tbl t1
;

-- testIssue856
SELECT if(month(today()) = 3, sum("Table5"."Month 002"), if(month(today()) = 3, sum("Table5"."Month 002"), if(month(today()) = 3, sum("Table5"."Month 002"), if(month(today()) = 3, sum("Table5"."Month 002"), if(month(today()) = 3, sum("Table5"."Month 002"), if(month(today()) = 3, sum("Table5"."Month 002"), 0)))))) FROM mytbl
;

