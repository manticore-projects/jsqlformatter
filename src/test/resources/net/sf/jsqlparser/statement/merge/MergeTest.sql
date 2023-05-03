-- testInsertMergeWhere
merge into test1 a using all_objects b on(a.object_id=b.object_id)when matched then update set a.status=b.status where b.status!='valid' when not matched then insert(object_id,status)values(b.object_id,b.status)where b.status!='valid'
;

-- testWith
with a as(select 1 id_instrument_ref),b as(select 1 id_instrument_ref)merge into cfe.instrument_ref b using a on(b.id_instrument_ref=a.id_instrument_ref)when matched then update set b.id_instrument='a'
;

-- testMergeUpdateInsertOrderIssue401
MERGE INTO a USING dual ON (col3 = ? AND col1 = ? AND col2 = ?) WHEN NOT MATCHED THEN INSERT (col1, col2, col3, col4) VALUES (?, ?, ?, ?) WHEN MATCHED THEN UPDATE SET col4 = col4 + ?
;

-- testMergeIssue232
merge into xyz using dual on(custom_id=?)when matched then update set abc=sysdate when not matched then insert(custom_id)values(?)
;

-- testMergeIssue676
merge into m_kc21 using(select aaa,bbb from i_kc21 where i_kc21.aaa='li_kun')temp on(temp.aaa=m_kc21.aaa)when matched then update set m_kc21.bbb=6 where enterprise_id in(0,1)when not matched then insert values(temp.aaa,temp.bbb)
;

-- testOracleMergeIntoStatement
merge into bonuses b using(select employee_id,salary from employee where dept_no=20)e on(b.employee_id=e.employee_id)when matched then update set b.bonus=e.salary*0.1 when not matched then insert(b.employee_id,b.bonus)values(e.employee_id,e.salary*0.05)
;

-- testMergeUpdateInsertOrderIssue401_2
MERGE INTO a USING dual ON (col3 = ? AND col1 = ? AND col2 = ?) WHEN MATCHED THEN UPDATE SET col4 = col4 + ? WHEN NOT MATCHED THEN INSERT (col1, col2, col3, col4) VALUES (?, ?, ?, ?)
;

-- testOracleHint
merge into bonuses b using(select employee_id,salary from employee where dept_no=20)e on(b.employee_id=e.employee_id)when matched then update set b.bonus=e.salary*0.1 when not matched then insert(b.employee_id,b.bonus)values(e.employee_id,e.salary*0.05)
;

-- testComplexOracleMergeIntoStatement
merge into destinationvalue dest using(select themonth,identifyingkey,sum(netprice)netprice,sum(netdeductionprice)netdeductionprice,max(case rownumbermain when 1 then qualityindicator else null end)qualityindicatormain,max(case rownumberdeduction when 1 then qualityindicator else null end)qualityindicatordeduction from(select pd.themonth,coalesce(pd.identifyingkey,0)identifyingkey,coalesce(case pd.isdeduction when 1 then null else convertedcalculatedvalue end,0)netprice,coalesce(case pd.isdeduction when 1 then convertedcalculatedvalue else null end,0)netdeductionprice,pd.qualityindicator,row_number()over(partition by pd.themonth,pd.identifyingkey order by coalesce(pd.qualitymonth,to_date('18991230','yyyymmdd'))desc)rownumbermain,null rownumberdeduction from pricingdata pd where pd.thingskey in(:thingskeys)and pd.themonth>=:startdate and pd.themonth<=:enddate and pd.isdeduction=0 union all select pd.themonth,coalesce(pd.identifyingkey,0)identifyingkey,coalesce(case pd.isdeduction when 1 then null else convertedcalculatedvalue end,0)netprice,coalesce(case pd.isdeduction when 1 then convertedcalculatedvalue else null end,0)netdeductionprice,pd.qualityindicator,null rownumbermain,row_number()over(partition by pd.themonth,pd.identifyingkey order by coalesce(pd.qualitymonth,to_date('18991230','yyyymmdd'))desc)rownumberdeduction from pricingdata pd where pd.thingskey in(:thingskeys)and pd.themonth>=:startdate and pd.themonth<=:enddate and pd.isdeduction<>0)group by themonth,identifyingkey)data on(dest.themonth=data.themonth and coalesce(dest.identifyingkey,0)=data.identifyingkey)when matched then update set netprice=round(data.netprice,pricedecimalscale),deductionprice=round(data.netdeductionprice,pricedecimalscale),subtotalprice=round(data.netprice+(data.netdeductionprice*dest.hasdeductions),pricedecimalscale),qualityindicator=case dest.hasdeductions when 0 then data.qualityindicatormain else case when coalesce(data.checkmonth1,to_date('18991230','yyyymmdd'))>coalesce(data.checkmonth2,to_date('18991230','yyyymmdd'))then data.qualityindicatormain else data.qualityindicatordeduction end end,recuser=:recuser,recdate=:recdate where 1=1 and isimportant=1 and coalesce(data.someflag,-1)<>coalesce(round(something,1),-1)delete where isimportant=0 or coalesce(data.someflag,-1)=coalesce(round(something,1),-1)when not matched then insert(themonth,thingskey,isdeduction,createdat)values(data.themonth,data.thingskey,data.isdeduction,sysdate)
;

