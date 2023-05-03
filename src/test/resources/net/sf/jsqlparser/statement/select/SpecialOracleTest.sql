-- testAllSqlsParseDeparse
insert into p(a1,b2,c3,d4,e5,f6,g7,h8)values(:b1,:b2,:b3,:b4,:5,:6,:7,:8)
;

-- testAllSqlsParseDeparse
select*from a where a=:3 and b=:4 and c=:5 and:a=:b
;

-- testAllSqlsParseDeparse
select title from table(select courses from department where name='history')where name like '%etruscan%'
;

-- testAllSqlsParseDeparse
select 1,cursor(select 1 from dual)c1,cursor(select 2,3 from dual)as c2 from table(select 1 from dual)
;

-- testAllSqlsParseDeparse
select*from table(function_name())
;

-- testAllSqlsParseDeparse
select*from table(cast(f_int_date_varchar2()as table_int_date_varchar2))
;

-- testAllSqlsParseDeparse
update table_name set row=array_of_records(i)where 1=1
;

-- testAllSqlsParseDeparse
select deptno,collect(ename)as emps from emp group by deptno
;

-- testAllSqlsParseDeparse
select deptno,set(cast(collect(job)as varchar2_ntt))as distinct_jobs from emp group by deptno
;

-- testAllSqlsParseDeparse
select owner,object_type,set(cast(collect(object_name)as varchar2_ntt))as object_names from all_objects group by owner,object_type
;

-- testAllSqlsParseDeparse
select*from table(powermultiset_by_cardinality(varchar2_ntt('a','b','c','d','d'),3))
;

-- testAllSqlsParseDeparse
select*from table(set(complex_ntt(complex_ot('data','more data',1),complex_ot('data','some data',2),complex_ot('data','dupe data',3),complex_ot('data','dupe data',3))))
;

-- testAllSqlsParseDeparse
select a,b,a d,ddd as ddd,ddd as "dfdf",x as x from dual
;

-- testAllSqlsParseDeparse
select*from t where((((((((((((((((type='2')or(type='3'))and(t.cde<20))and(t.se='xxx'))and(t.id='000000000002'))and((t.attr_1 is null)or(t.attr_1='*')))and((t.attr_2 is null)or(t.attr_2='*')))and((t.attr_3 is null)or(t.attr_3='*')))and((t.attr_4 is null)or(t.attr_4='*')))and((t.attr_5 is null)or(t.attr_5='*')))and((t.itype is null)or(t.itype='*')))and((t.inbr is null)or(t.inbr='*')))and((t.stat='01')or(t.stat='*')))and((t.orgn is null)or(t.orgn='*')))and(t.mbr='0000000000001'))and(t.nbr is null))
;

-- testAllSqlsParseDeparse
select department_id,last_name,salary from employees x where 1=1 and((hi)>(.1*t.rowcnt))
;

-- testAllSqlsParseDeparse
select*from v.e where cid<>rid and rid not in((select distinct rid from v.s)union(select distinct rid from v.p))and "timestamp"<=1298505600000
;

-- testAllSqlsParseDeparse
select*from employees where(salary,salary)>=some(1400,3000)order by employee_id
;

-- testAllSqlsParseDeparse
select*from employees where(salary,salary)>=some(select 1,2 from dual)order by employee_id
;

-- testAllSqlsParseDeparse
select timestamp '2009-10-29 01:30:00' from dual
;

-- testAllSqlsParseDeparse
select date '1900-01-01' from dual
;

-- testAllSqlsParseDeparse
select*from dual where sysdate>date '2013-04-10'
;

-- testAllSqlsParseDeparse
select employee_id from(select*from employees)for update of employee_id
;

-- testAllSqlsParseDeparse
select employee_id from(select employee_id+1 as employee_id from employees)for update
;

-- testAllSqlsParseDeparse
select employee_id from(select employee_id+1 as employee_id from employees)for update of employee_id
;

-- testAllSqlsParseDeparse
select employee_id from(select employee_id+1 as employee_id from employees)for update of employee_id nowait
;

-- testAllSqlsParseDeparse
select employee_id from(select employee_id+1 as employee_id from employees)for update of employee_id wait 10
;

-- testAllSqlsParseDeparse
select decode(decode(decode((select count(1)from dual),a,1,0),0,1),1,0)from dual
;

-- testAllSqlsParseDeparse
select set(x)from dual
;

-- testAllSqlsParseDeparse
select:b3 as l_snap_id,:b2 as p_dbid,:b1 as p_instance_number,nvl(pid,-9)pid,nvl(serial#,-9)serial#,decode(pid,null,null,max(spid))spid,decode(pid,null,null,max(program))program,decode(pid,null,null,max(background))background,sum(pga_used_mem)pga_used_mem,sum(pga_alloc_mem)pga_alloc_mem,sum(pga_freeable_mem)pga_freeable_mem,max(pga_alloc_mem)max_pga_alloc_mem,max(pga_max_mem)max_pga_max_mem,decode(pid,null,avg(pga_alloc_mem),null)avg_pga_alloc_mem,decode(pid,null,stddev(pga_alloc_mem),null)stddev_pga_alloc_mem,decode(pid,null,count(pid),null)num_processes from v$process where program!='pseudo' group by grouping sets((pid,serial#),())
;

-- testAllSqlsParseDeparse
select decode(pid,null,null,max(program))program,decode(pid,null,null,max(background))background,sum(pga_used_mem)pga_used_mem,sum(pga_alloc_mem)pga_alloc_mem,sum(pga_freeable_mem)pga_freeable_mem,max(pga_alloc_mem)max_pga_alloc_mem,max(pga_max_mem)max_pga_max_mem,decode(pid,null,avg(pga_alloc_mem),null)avg_pga_alloc_mem,decode(pid,null,stddev(pga_alloc_mem),null)stddev_pga_alloc_mem,decode(pid,null,count(pid),null)num_processes from v$process where program!='pseudo' group by grouping sets((),((pid+1),serial#))
;

-- testAllSqlsParseDeparse
select*from x group by grouping sets(a,1)
;

-- testAllSqlsParseDeparse
select*from x group by grouping sets((a),1)
;

-- testAllSqlsParseDeparse
select*from x group by grouping sets(((a),b),((a),b))
;

-- testAllSqlsParseDeparse
select fact_1_id,fact_2_id,sum(sales_value)as sales_value,grouping_id(fact_1_id,fact_2_id)as grouping_id,group_id()as group_id from dimension_tab group by grouping sets(fact_1_id,cube(fact_1_id,fact_2_id))order by fact_1_id,fact_2_id
;

-- testAllSqlsParseDeparse
select fact_1_id,fact_2_id,fact_3_id,sum(sales_value)as sales_value,grouping_id(fact_1_id,fact_2_id,fact_3_id)as grouping_id from dimension_tab group by cube(fact_1_id,fact_2_id,fact_3_id)order by fact_1_id,fact_2_id,fact_3_id
;

-- testAllSqlsParseDeparse
select fact_1_id,fact_2_id,fact_3_id,sum(sales_value)as sales_value,grouping_id(fact_1_id,fact_2_id,fact_3_id)as grouping_id from dimension_tab group by grouping sets((fact_1_id,fact_2_id),(fact_1_id,fact_3_id))order by fact_1_id,fact_2_id,fact_3_id
;

-- testAllSqlsParseDeparse
select fact_1_id,fact_2_id,fact_3_id,sum(sales_value)as sales_value,grouping_id(fact_1_id,fact_2_id,fact_3_id)as grouping_id from dimension_tab group by cube(fact_1_id,fact_2_id,fact_3_id)order by fact_1_id,fact_2_id,fact_3_id
;

-- testAllSqlsParseDeparse
select fact_1_id,fact_2_id,fact_3_id,sum(sales_value)as sales_value,grouping_id(fact_1_id,fact_2_id,fact_3_id)as grouping_id from dimension_tab group by cube((fact_1_id,fact_2_id),fact_3_id)order by fact_1_id,fact_2_id,fact_3_id
;

-- testAllSqlsParseDeparse
select fact_1_id,fact_2_id,sum(sales_value)as sales_value,grouping_id(fact_1_id,fact_2_id)as grouping_id from dimension_tab group by grouping sets(fact_1_id,fact_2_id)order by fact_1_id,fact_2_id
;

-- testAllSqlsParseDeparse
select fact_3_id,fact_4_id,sum(sales_value)as sales_value,grouping_id(fact_3_id,fact_4_id)as grouping_id from dimension_tab group by grouping sets(fact_3_id,fact_4_id)order by fact_3_id,fact_4_id
;

-- testAllSqlsParseDeparse
insert into t values trec
;

-- testAllSqlsParseDeparse
select interval '42' day from dual
;

-- testAllSqlsParseDeparse
select interval '20' day-interval '240' hour from dual
;

-- testAllSqlsParseDeparse
select*from t1 join t2 tt2 using(c)join t3 tt3 using(d)join t3 using(d)
;

-- testAllSqlsParseDeparse
select*from dual t1,(dual left outer join(select*from dual)tt2 using(dummy))
;

-- testAllSqlsParseDeparse
select*from t1,(t2 left outer join t3 using(dummy))
;

-- testAllSqlsParseDeparse
select*from dual,(dual left outer join tt2 using(dummy))
;

-- testAllSqlsParseDeparse
select*from t1,((((t2 left outer join t3 using(dummy)))))
;

-- testAllSqlsParseDeparse
select*from dual t1,(((dual t2 join dual t3 using(dummy))left outer join dual t4 using(dummy))left outer join dual t5 using(dummy))
;

-- testAllSqlsParseDeparse
select*from dual t1,(dual t2 join dual t3 using(dummy))left outer join dual t4 using(dummy)
;

-- testAllSqlsParseDeparse
select*from dual t1,dual t2 join dual t3 using(dummy)left outer join dual t4 using(dummy)left outer join dual t5 using(dummy)
;

-- testAllSqlsParseDeparse
select d1.*,d2.*from dual d1 cross join dual d2
;

-- testAllSqlsParseDeparse
select d1.*,d2.*from dual cross join dual
;

-- testAllSqlsParseDeparse
select*from sys.dual natural join sys.dual
;

-- testAllSqlsParseDeparse
select timestamp,avg,cume_dist from nulls
;

-- testAllSqlsParseDeparse
select exception from exception
;

-- testAllSqlsParseDeparse
select 'a'||'b' from dual
;

-- testAllSqlsParseDeparse
select tbl$or$idx$part$num("sys"."wrh$_seg_stat",0,4,0,"rowid")as c1 from t1
;

-- testAllSqlsParseDeparse
select tbl$or$idx$part$num("sys"."wrh:_seg_stat",0,4,0,"rowid")as c1 from t1
;

-- testAllSqlsParseDeparse
select last_name from employees where last_name like '%a\_b%' escape '\' order by last_name
;

-- testAllSqlsParseDeparse
select*from pivot_table unpivot(yearly_total for order_mode in(store as 'direct',internet as 'online'))order by year,order_mode
;

-- testAllSqlsParseDeparse
select*from(select times_purchased as "puchase frequency",state_code from customers t)pivot xml(count(state_code)for state_code in(select state_code from preferred_states))order by 1
;

-- testAllSqlsParseDeparse
select*from(select times_purchased as "purchase frequency",state_code from customers t)pivot xml(count(state_code)for state_code in(any))order by 1
;

-- testAllSqlsParseDeparse
select value from((select 'a' v1,'e' v2,'i' v3,'o' v4,'u' v5 from dual)unpivot(value for value_type in(v1,v2,v3,v4,v5)))
;

-- testAllSqlsParseDeparse
select*from(select customer_id,product_code,quantity from pivot_test)pivot xml(sum(quantity)as sum_quantity for product_code in(select distinct product_code from pivot_test))
;

-- testAllSqlsParseDeparse
select*from(select product_code,quantity from pivot_test)pivot xml(sum(quantity)as sum_quantity for product_code in(select distinct product_code from pivot_test where id<10))
;

-- testAllSqlsParseDeparse
select value from((select customer_id,product_code,quantity from pivot_test)pivot(sum(quantity)as sum_quantity for product_code in('a' as a,'b' as b,'c' as c)))
;

-- testAllSqlsParseDeparse
select*from(select product_code,quantity from pivot_test)pivot(sum(quantity)as sum_quantity for product_code in('a' as a,'b' as b,'c' as c))
;

-- testAllSqlsParseDeparse
select value from((select 'a' v1,'e' v2,'i' v3,'o' v4,'u' v5 from dual)unpivot include nulls(value for value_type in(v1,v2,v3,v4,v5)))
;

-- testAllSqlsParseDeparse
select nt,set(nt)as nt_set from(select varchar2_ntt('a','b','c','c')as nt from dual)
;

-- testAllSqlsParseDeparse
select employee_id from(select employee_id+1 as employee_id from employees)for update
;

-- testAllSqlsParseDeparse
select employee_id from(select employee_id+1 as employee_id from employees)for update of employee_id
;

-- testAllSqlsParseDeparse
select*from((select*from dual)unpivot(value for value_type in(dummy)))
;

-- testAllSqlsParseDeparse
select*from(select*from a unpivot(value for value_type in(dummy)))
;

-- testAllSqlsParseDeparse
select*from((select*from dual))a
;

-- testAllSqlsParseDeparse
select*from dual for update of dual
;

-- testAllSqlsParseDeparse
select a,b,c,d,e,1,2,f(a,b,c,1+1)from dual
;

-- testAllSqlsParseDeparse
select a||last_name,employee_id from employees start with job_id='ad_vp' connect by prior employee_id=manager_id
;

-- testAllSqlsParseDeparse
select a as over from over
;

-- testAllSqlsParseDeparse
select a.*from dual
;

-- testAllSqlsParseDeparse
select*from(dual),(dual d),(dual)d
;

-- testAllSqlsParseDeparse
(select 'a' obj,'b' link from dual)union all(select 'a','c' from dual)union all(select 'c','d' from dual)union all(select 'd','c' from dual)union all(select 'd','e' from dual)union all(select 'e','e' from dual)
;

-- testAllSqlsParseDeparse
(select distinct job_id from hr.jobs)union all(select distinct job_id from hr.job_history)
;

-- testAllSqlsParseDeparse
(select distinct job_id from hr.jobs)union all(select distinct job_id from hr.job_history)
;

-- testAllSqlsParseDeparse
(select distinct job_id from hr.jobs)union all(select distinct job_id from hr.job_history union all((((select distinct job_id from hr.job_history union all(select distinct job_id from hr.job_history))))union all select distinct job_id from hr.job_history))union all(select distinct job_id from hr.job_history union all(select distinct job_id from hr.job_history union all(select distinct job_id from hr.job_history)))union all(select distinct job_id from hr.job_history union all select distinct job_id from hr.job_history)union all select distinct job_id from hr.job_history union all select distinct job_id from hr.job_history union all select distinct job_id from hr.job_history union all select distinct job_id from hr.job_history
;

-- testAllSqlsParseDeparse
(select*from dual)union all(select*from dual)union all(select*from dual)union all(select*from dual)union all(select*from dual)union all(select*from dual)union all(select*from dual)union all(select*from dual)order by 1 asc,2 asc
;

-- testAllSqlsParseDeparse
select*from dual where exists((select*from dual)union all(select*from dual))
;

-- testAllSqlsParseDeparse
select*from(select row_.*from(select*from(select results.*,1 rn from((select dummy from dual where 1=1)union(select dummy from dual where 1=1))results)where rn=1 order by dummy desc)row_ where rownum<=1)where rownum>=1
;

-- testAllSqlsParseDeparse
select((select 'y' from dual where exists(select 1 from dual where 1=0))union(select 'n' from dual where not exists(select 1 from dual where 1=0)))as yes_no from dual
;

-- testAllSqlsParseDeparse
select xmlelement("other_attrs",xmlelement("parsing_user_id",parsing_user_id)).getclobval()other from f
;

-- testAllSqlsParseDeparse
select lnnvl(2>1)from dual
;

-- testAllSqlsParseDeparse
select*from(s join d using(c))pivot(max(c_c_p)as max_ccp,max(d_c_p)max_dcp,max(d_x_p)dxp,count(1)cnt for(i,p)in((1,1)as one_one,(1,2)as one_two,(1,3)as one_three,(2,1)as two_one,(2,2)as two_two,(2,3)as two_three))where d_t='p'
;

-- testAllSqlsParseDeparse
select*from s pivot(max(c_c_p)as max_ccp,max(d_c_p)max_dcp,max(d_x_p)dxp,count(1)cnt for(i,p)in((1,1)as one_one,(1,2)as one_two,(1,3)as one_three,(2,1)as two_one,(2,2)as two_two,(2,3)as two_three))join d using(c)where d_t='p'
;

-- testAllSqlsParseDeparse
select last_name "employee",connect_by_root last_name "manager",level-1 "pathlen",sys_connect_by_path(last_name,'/')"path" from employees where level>1 and department_id=110 connect by prior employee_id=manager_id order by "employee","manager","pathlen","path"
;

-- testAllSqlsParseDeparse
select count(*)from employees where lnnvl(commission_pct>=.2)
;

-- testAllSqlsParseDeparse
select 'yes' from dual where(sysdate-5,sysdate)overlaps(sysdate-2,sysdate-1)
;

-- testAllSqlsParseDeparse
with codes2codelocales as(select t6.cdl_name as cod_name,t7.cdl_name as cod_cod_name,t14.cod_oid from servicedesk.itsm_codes t14 left outer join servicedesk.itsm_codes_locale t6 on(t6.cdl_cod_oid=t14.cod_oid)left outer join servicedesk.itsm_codes_locale t7 on(t7.cdl_cod_oid=t14.cod_cod_oid)),incident as(select t1.*,c2cl1.cod_name as "closure code",c2cl1.cod_cod_name as "closure code parent",c2cl2.cod_name as "reason caused code",c2cl2.cod_cod_name as "reason caused code parent",t11.cdl_name "severity",t13.cdl_name "business impact",t16.cdl_name "priority",t2.rct_name "status",t12.rct_name "category",t99.rct_name "folder" from servicedesk.itsm_incidents t1 join servicedesk.itsm_codes_locale t11 on(t1.inc_sev_oid=t11.cdl_cod_oid)join servicedesk.itsm_codes_locale t13 on(t1.inc_imp_oid=t13.cdl_cod_oid)join servicedesk.itsm_codes_locale t16 on(t1.inc_pri_oid=t16.cdl_cod_oid)join servicedeskrepo.rep_codes_text t2 on(t1.inc_sta_oid=t2.rct_rcd_oid)join servicedeskrepo.rep_codes_text t12 on(t1.inc_cat_oid=t12.rct_rcd_oid)join servicedeskrepo.rep_codes_text t99 on(t1.inc_poo_oid=t99.rct_rcd_oid)left outer join codes2codelocales c2cl1 on(t1.inc_clo_oid=c2cl1.cod_oid)left outer join codes2codelocales c2cl2 on(t1.inc_cla_oid=c2cl2.cod_oid)where t1."reg_created" between sysdate-1 and sysdate),workgrouphistory as(select i.inc_id,max(t101.hin_subject)keep(dense_rank first order by(t101.reg_created))as "first",max(t101.hin_subject)keep(dense_rank last order by(t101.reg_created))as "last" from servicedesk.itsm_historylines_incident t101 join incident i on(t101.hin_inc_oid=i.inc_oid)where t101.hin_subject like 'to workgroup from%' group by i.inc_id)select incident.inc_id "id",incident."status",incident.inc_description "description",t4.wog_searchcode "workgroup",t5.per_searchcode "person",incident.inc_solution "solution",incident."closure code",incident."closure code parent",incident."reason caused code",incident."reason caused code parent",t10.cit_searchcode "ci",incident."severity",incident."category",incident."business impact",incident."priority",to_char(incident."reg_created",'dd-mm-yy hh24:mi:ss')"registered",to_char(incident."inc_deadline",'dd-mm-yy hh24:mi:ss')"deadline",to_char(incident."inc_actualfinish",'dd-mm-yy hh24:mi:ss')"finish",t3.icf_incshorttext3 "message group",t3.icf_incshorttext4 "application",t3.icf_incshorttext2 "msg id",incident."folder",workgrouphistory."first" "first wg",workgrouphistory."last" "last wg",t102.hin_subject "frirst pri" from incident join servicedesk.itsm_inc_custom_fields t3 on(incident.inc_oid=t3.icf_inc_oid)join servicedesk.itsm_workgroups t4 on(incident.inc_assign_workgroup=t4.wog_oid)join workgrouphistory on(incident.inc_id=workgrouphistory.inc_id)left outer join servicedesk.itsm_persons t5 on(incident.inc_assign_person_to=t5.per_oid)left outer join servicedesk.itsm_configuration_items t10 on(incident.inc_cit_oid=t10.cit_oid)left outer join servicedesk.itsm_historylines_incident t102 on(incident.inc_oid=t102.hin_inc_oid and t102.hin_subject like 'priority set to%')
;

-- testAllSqlsParseDeparse
select trim(both ' ' from ' a ')from dual where trim(:a)is not null
;

-- testAllSqlsParseDeparse
select object_name,object_id,decode(status,'invalid','true','false')invalid,'true' runnable,plsql_debug from sys.dba_objects o,dba_plsql_object_settings s where o.owner=:schema and s.owner=:schema and s.name=o.object_name and s.type='package' and object_type='package' and subobject_name is null and object_id not in(select purge_object from recyclebin)and upper(object_name)in upper(:name)
;

-- testAllSqlsParseDeparse
select staleness,osize,obj#,type#,case when staleness>.5 then 128 when staleness>.1 then 256 else 0 end+aflags aflags,status,sid,serial#,part#,bo#,case when is_full_events_history=1 then src.bor_last_status_time else case greatest(nvl(wp.bor_last_stat_time,date '1900-01-01'),nvl(src.bor_last_status_time,date '1900-01-01'))when date '1900-01-01' then null when wp.bor_last_stat_time then wp.bor_last_stat_time when src.bor_last_status_time then src.bor_last_status_time else null end end,case greatest(nvl(wp.bor_last_stat_time,date '1900-01-01'),nvl(src.bor_last_status_time,date '1900-01-01'))when date '1900-01-01' then null when wp.bor_last_stat_time then wp.bor_last_stat_time when src.bor_last_status_time then src.bor_last_status_time else null end from x
;

-- testAllSqlsParseDeparse
select case(status)when 'n' then 1 when 'b' then 2 when 'a' then 3 end as state from value where kid=:b2 and rid=:b1
;

-- testAllSqlsParseDeparse
select t1.department_id,t2.*from hr_info t1,table(cast(multiset(select t3.last_name,t3.department_id,t3.salary from people t3 where t3.department_id=t1.department_id)as people_tab_typ))t2
;

-- testAllSqlsParseDeparse
select e1.last_name from employees e1 where f(cursor(select e2.hire_date from employees e2 where e1.employee_id=e2.manager_id),e1.hire_date)=1 order by last_name
;

-- testAllSqlsParseDeparse
update customers_demo cd set cust_address2_ntab=cast(multiset(select cust_address from customers c where c.customer_id=cd.customer_id)as cust_address_tab_typ)
;

-- testAllSqlsParseDeparse
select deptno,cast(collect(ename order by ename)as varchar2_ntt)as ordered_emps from emp group by deptno
;

-- testAllSqlsParseDeparse
select deptno,cast(collect(ename order by hiredate)as varchar2_ntt)as ordered_emps from emp group by deptno
;

-- testAllSqlsParseDeparse
select deptno,cast(collect(job)as varchar2_ntt)as jobs from emp group by deptno
;

-- testAllSqlsParseDeparse
select deptno,cast(collect(all job)as varchar2_ntt)as distinct_jobs from emp group by deptno
;

-- testAllSqlsParseDeparse
select deptno,cast(collect(distinct job order by job)as varchar2_ntt)as distinct_ordered_jobs from emp group by deptno
;

-- testAllSqlsParseDeparse
select deptno,cast(collect(empsal_ot(ename,sal)order by sal)as empsal_ntt)as empsals from emp group by deptno
;

-- testAllSqlsParseDeparse
select deptno,cast(collect(empsal_ot(ename,sal)order by empsal_ot(ename,sal))as empsal_ntt)as empsals from emp group by deptno
;

-- testAllSqlsParseDeparse
select deptno,cast(collect(distinct empsal_ot(ename,sal))as empsal_ntt)as empsals from emp group by deptno
;

-- testAllSqlsParseDeparse
select cast(collect(distinct empsal_ot(ename,sal))as empsal_ntt)as empsals from emp
;

-- testAllSqlsParseDeparse
select e.deptno,cast(multiset(select e2.ename from emp e2 where e2.deptno=e.deptno order by e2.hiredate)as varchar2_ntt)as ordered_emps from emp e group by e.deptno order by e.deptno
;

-- testAllSqlsParseDeparse
select deptno,cast(set(collect(job))as varchar2_ntt)as distinct_jobs from emp group by deptno
;

-- testAllSqlsParseDeparse
select owner,object_type,cast(collect(distinct object_name)as varchar2_ntt)as object_names from all_objects group by owner,object_type
;

-- testAllSqlsParseDeparse
select cast(powermultiset(varchar2_ntt('a','b','c'))as varchar2_ntts)as pwrmltset from dual
;

-- testAllSqlsParseDeparse
select deptno,avg(sal)avg_sal,cast(collect(ename)as ename_type)enames from emp group by deptno
;

-- testAllSqlsParseDeparse
select department_id,last_name,salary from employees x where salary>(select avg(salary)from employees where x.department_id=department_id)order by department_id
;

-- testAllSqlsParseDeparse
select*from employees x where salary>(select avg(salary)from x)and 1=1 and hiredate=sysdate and to_yminterval('01-00')<sysdate and to_yminterval('01-00')+x<sysdate
;

-- testAllSqlsParseDeparse
select*from employees x where salary>(select avg(salary)from x)and 1=1 and hiredate=sysdate and to_yminterval('01-00')<sysdate and to_yminterval('01-00')+x<sysdate or a=b and d=e
;

-- testAllSqlsParseDeparse
select*from t where(t.type='2')or(t.type='3')and t.cde<20 and t.se='xxx' and t.id='000000000002' and((t.sku_attr_1 is null)or(t.sku_attr_1='*'))and((t.sku_attr_2 is null)or(t.sku_attr_2='*'))and((t.sku_attr_3 is null)or(t.sku_attr_3='*'))and((t.sku_attr_4 is null)or(t.sku_attr_4='*'))and((t.sku_attr_5 is null)or(t.sku_attr_5='*'))and((t.itype is null)or(t.itype='*'))and((t.bnbr is null)or(t.bnbr='*'))and((t.stat='01')or(t.stat='*'))and((t.orgn is null)or(t.orgn='*'))and(t.mbr='0000000000001')and(t.nbr is null)
;

-- testAllSqlsParseDeparse
select*from "p" where(("p"."id" in 231092))
;

-- testAllSqlsParseDeparse
select sum(nvl(pl.qty,0))from oline ol,pline pl,blocation bl where ol.id=pl.id and pl.no=pl.no and bl.id=pl.id and((select count(*)from la.sa where pl.id like sa.bid)>0 or(select count(*)from la.sa where bl.id like sa.id)>0)
;

-- testAllSqlsParseDeparse
select*from dual where trim(sxhnode_key)is not null
;

-- testAllSqlsParseDeparse
select lpad(' ',2*(level-1))||last_name org_chart,employee_id,manager_id,job_id from employees start with job_id='ad_pres' connect by prior employee_id=manager_id and level<=2
;

-- testAllSqlsParseDeparse
select lpad(' ',2*(level-1))||last_name org_chart,employee_id,manager_id,job_id from employees where job_id!='fi_mgr' start with job_id='ad_vp' connect by prior employee_id=manager_id
;

-- testAllSqlsParseDeparse
select lpad(' ',2*(level-1))||last_name org_chart,employee_id,manager_id,job_id from employees start with job_id='ad_vp' connect by prior employee_id=manager_id
;

-- testAllSqlsParseDeparse
select t.*,connect_by_root id from test t start with t.id=1 connect by prior t.id=t.parent_id order siblings by t.some_text
;

-- testAllSqlsParseDeparse
select from_tz(cast(to_date('1999-12-01 11:00:00','yyyy-mm-dd hh:mi:ss')as timestamp),'america/new_york')at time zone 'america/los_angeles' "west coast time" from dual
;

-- testAllSqlsParseDeparse
select dbin.db_name,dbin.instance_name,dbin.version,case when s1.startup_time=s2.startup_time then 0 else 1 end as bounce,cast(s1.end_interval_time as date)as begin_time,cast(s2.end_interval_time as date)as end_time,round((cast((case when s2.end_interval_time>s1.end_interval_time then s2.end_interval_time else s1.end_interval_time end)as date)-cast(s1.end_interval_time as date))*86400)as int_secs,case when(s1.status<>0 or s2.status<>0)then 1 else 0 end as err_detect,round(greatest((extract(day from s2.flush_elapsed)*86400)+(extract(hour from s2.flush_elapsed)*3600)+(extract(minute from s2.flush_elapsed)*60)+extract(second from s2.flush_elapsed),(extract(day from s1.flush_elapsed)*86400)+(extract(hour from s1.flush_elapsed)*3600)+(extract(minute from s1.flush_elapsed)*60)+extract(second from s1.flush_elapsed),0))as max_flush_secs from wrm$_snapshot s1,wrm$_database_instance dbin,wrm$_snapshot s2 where s1.dbid=:dbid and s2.dbid=:dbid and s1.instance_number=:inst_num and s2.instance_number=:inst_num and s1.snap_id=:bid and s2.snap_id=:eid and dbin.dbid=s1.dbid and dbin.instance_number=s1.instance_number and dbin.startup_time=s1.startup_time
;

-- testAllSqlsParseDeparse
select su.ttype,su.cid,su.s_id,sessiontimezone from sku su where(nvl(su.up,'n')='n' and su.ttype=:b0)for update of su.up order by su.d
;

-- testAllSqlsParseDeparse
select fact_1_id,fact_2_id,sum(sales_value)as sales_value,grouping_id(fact_1_id,fact_2_id)as grouping_id,group_id()as group_id from dimension_tab group by grouping sets(fact_1_id,cube(fact_1_id,fact_2_id))having group_id()=0 order by fact_1_id,fact_2_id
;

-- testAllSqlsParseDeparse
select channels.channel_desc,countries.country_iso_code,to_char(sum(amount_sold),'9,999,999,999')sales$ from sales,customers,times,channels,countries where sales.time_id=times.time_id and sales.cust_id=customers.cust_id and sales.channel_id=channels.channel_id and channels.channel_desc in('direct sales','internet')and times.calendar_month_desc='2000-09' and customers.country_id=countries.country_id and countries.country_iso_code in('us','fr')group by cube(channels.channel_desc,countries.country_iso_code)
;

-- testAllSqlsParseDeparse
select channels.channel_desc,calendar_month_desc,countries.country_iso_code,to_char(sum(amount_sold),'9,999,999,999')sales$ from sales,customers,times,channels,countries where sales.time_id=times.time_id and sales.cust_id=customers.cust_id and customers.country_id=countries.country_id and sales.channel_id=channels.channel_id and channels.channel_desc in('direct sales','internet')and times.calendar_month_desc in('2000-09','2000-10')and countries.country_iso_code in('gb','us')group by rollup(channels.channel_desc,calendar_month_desc,countries.country_iso_code)
;

-- testAllSqlsParseDeparse
select channel_desc,calendar_month_desc,countries.country_iso_code,to_char(sum(amount_sold),'9,999,999,999')sales$ from sales,customers,times,channels,countries where sales.time_id=times.time_id and sales.cust_id=customers.cust_id and sales.channel_id=channels.channel_id and customers.country_id=countries.country_id and channels.channel_desc in('direct sales','internet')and times.calendar_month_desc in('2000-09','2000-10')and countries.country_iso_code in('gb','us')group by cube(channel_desc,calendar_month_desc,countries.country_iso_code)
;

-- testAllSqlsParseDeparse
select channel_desc,calendar_month_desc,country_iso_code,to_char(sum(amount_sold),'9,999,999,999')sales$,grouping(channel_desc)ch,grouping(calendar_month_desc)mo,grouping(country_iso_code)co from sales,customers,times,channels,countries where sales.time_id=times.time_id and sales.cust_id=customers.cust_id and customers.country_id=countries.country_id and sales.channel_id=channels.channel_id and channels.channel_desc in('direct sales','internet')and times.calendar_month_desc in('2000-09','2000-10')and country_iso_code in('gb','us')group by cube(channel_desc,calendar_month_desc,country_iso_code)having(grouping(channel_desc)=1 and grouping(calendar_month_desc)=1 and grouping(country_iso_code)=1)or(grouping(channel_desc)=1 and grouping(calendar_month_desc)=1)or(grouping(country_iso_code)=1 and grouping(calendar_month_desc)=1)
;

-- testAllSqlsParseDeparse
select d.department_id as d_dept_id,e.department_id as e_dept_id,e.last_name from departments d full outer join employees e on d.department_id=e.department_id order by d.department_id,e.last_name
;

-- testAllSqlsParseDeparse
select department_id as d_e_dept_id,e.last_name from departments d full outer join employees e using(department_id)order by department_id,e.last_name
;

-- testAllSqlsParseDeparse
select d.department_id,e.last_name from departments d,employees e where d.department_id=e.department_id(+)order by d.department_id,e.last_name
;

-- testAllSqlsParseDeparse
select*from dual d1 join dual d2 on(d1.dummy=d2.dummy)join dual d3 on(d1.dummy=d3.dummy)join dual on(d1.dummy=dual.dummy)
;

-- testAllSqlsParseDeparse
select*from dual t1 left outer join(select*from dual)tt2 using(dummy)left outer join(select*from dual)using(dummy)left outer join(select*from dual)d on(d.dummy=tt3.dummy)inner join(select*from dual)tt2 using(dummy)inner join(select*from dual)using(dummy)inner join(select*from dual)d on(d.dummy=t1.dummy)
;

-- testAllSqlsParseDeparse
select*from hdr a inner join sh s inner join ca c on c.id=s.id on a.va=s.va
;

-- testAllSqlsParseDeparse
select m.model from model
;

-- testAllSqlsParseDeparse
select:1,:x,:1+1,1+:2 from a where a=:3 and b=:4 and c=:5 and:a=:b
;

-- testAllSqlsParseDeparse
merge into bonuses d using(select employee_id.*from employees)s on(employee_id=a)when matched then update set d.bonus=bonus delete where(salary>8000)when not matched then insert(d.employee_id,d.bonus)values(s.employee_id,s.salary)where(s.salary<=8000)
;

-- testAllSqlsParseDeparse
with reports_to_101(eid,emp_last,mgr_id,reportlevel)as((select employee_id,last_name,manager_id,0 reportlevel from employees where employee_id=101)union all(select e.employee_id,e.last_name,e.manager_id,reportlevel+1 from reports_to_101 r,employees e where r.eid=e.manager_id))select eid,emp_last,mgr_id,reportlevel from reports_to_101 where reportlevel<=1 order by reportlevel,eid
;

-- testAllSqlsParseDeparse
with dept_costs as(select department_name,sum(salary)dept_total from employees e,departments d where e.department_id=d.department_id group by department_name),avg_cost as(select sum(dept_total)/count(*)avg from dept_costs)select*from dept_costs where dept_total>(select avvg from avg_cost)order by department_name
;

-- testAllSqlsParseDeparse
with x1 as(select*from t1),x2 as(select*from t2 join t3 on(t2.a2=t3.a3))select*from x1 join x2 on(x1.a1=x2.a2)join t4 on(x1.a1=t4.a4)
;

-- testAllSqlsParseDeparse
with col_generator as(select t1.batch_id,decode(t1.action,'sent',t1.actdate)sent,decode(t2.action,'recv',t2.actdate)received from test t1,test t2 where t2.batch_id(+)=t1.batch_id)select batch_id,max(sent)sent,max(received)received from col_generator group by batch_id order by 1
;

-- testAllSqlsParseDeparse
select nt.column_value as distinct_element from table(set(varchar2_ntt('a','b','c','c')))nt
;

-- testAllSqlsParseDeparse
select+1,t2.division_name as aaaa,a.*,sum(t3.amount)from dual
;

-- testAllSqlsParseDeparse
((select "x"."r_no","x"."i_id","x"."ind","x"."item",'0' "o" from "x" where("x"."r_no"=:a))minus(select "y"."r_no","y"."i_id","y"."ind","y"."item",'0' "o" from "y" where("y"."r_no"=:a)))union((select "y"."r_no","y"."i_id","y"."ind","y"."item",'1' "o" from "y" where("y"."r_no"=:a))minus(select "x"."r_no","x"."i_id","x"."ind","x"."item",'1' "o" from "x" where("x"."r_no"=:a)))order by 4,3,1
;

-- testAllSqlsParseDeparse
select cast(collect(cattr(aname,op,to_char(val),support,confidence))as cattrs)cl_attrs from a
;

-- testAllSqlsParseDeparse
select cast(powermultiset(cust_address_ntab)as cust_address_tab_tab_typ)from customers_demo
;

-- testAllSqlsParseDeparse
select deptno,cast(collect(ename)as varchar2_ntt)as emps from emp group by deptno
;

-- testAllSqlsParseDeparse
select deptno,cast(collect(distinct job)as varchar2_ntt)as distinct_jobs from emp group by deptno
;

-- testAllSqlsParseDeparse
select deptno,cast(collect(unique job)as varchar2_ntt)as distinct_jobs from emp group by deptno
;

-- testAllSqlsParseDeparse
select deptno,cast(collect(empsal_ot(ename,sal))as empsal_ntt)as empsals from emp group by deptno
;

-- testAllSqlsParseDeparse
select cast(collect(distinct empsal_ot(ename,sal))as empsal_ntt)as empsals from emp
;

-- testAllSqlsParseDeparse
select*from append where(length(w.numer)>=8)
;

-- testAllSqlsParseDeparse
select last_name,department_name from employees@remote,departments where employees.department_id=departments.department_id
;

-- testAllSqlsParseDeparse
select prod_category,prod_subcategory,country_id,cust_city,count(*)from products,sales,customers where sales.prod_id=products.prod_id and sales.cust_id=customers.cust_id and sales.time_id='01-oct-00' and customers.cust_year_of_birth between 1960 and 1970 group by grouping sets((prod_category,prod_subcategory,country_id,cust_city),(prod_category,prod_subcategory,country_id),(prod_category,prod_subcategory),country_id)order by prod_category,prod_subcategory,country_id,cust_city
;

-- testAllSqlsParseDeparse
select fact_1_id,fact_2_id,sum(sales_value)as sales_value,grouping(fact_1_id)as f1g,grouping(fact_2_id)as f2g from dimension_tab group by cube(fact_1_id,fact_2_id)having grouping(fact_1_id)=1 or grouping(fact_2_id)=1 order by grouping(fact_1_id),grouping(fact_2_id)
;

-- testAllSqlsParseDeparse
select channel_desc,calendar_month_desc,country_iso_code,to_char(sum(amount_sold),'9,999,999,999')sales$,grouping(channel_desc)as ch,grouping(calendar_month_desc)as mo,grouping(country_iso_code)as co from sales,customers,times,channels,countries where sales.time_id=times.time_id and sales.cust_id=customers.cust_id and customers.country_id=countries.country_id and sales.channel_id=channels.channel_id and channels.channel_desc in('direct sales','internet')and times.calendar_month_desc in('2000-09','2000-10')and countries.country_iso_code in('gb','us')group by rollup(channel_desc,calendar_month_desc,countries.country_iso_code)
;

-- testAllSqlsParseDeparse
select d.department_id,e.last_name from m.departments d right outer join n.employees e on d.department_id=e.department_id order by d.department_id,e.last_name
;

-- testAllSqlsParseDeparse
select*from dual t1 join(select*from dual)tt2 using(dummy)join(select*from dual)using(dummy)join(select*from dual)d on(d.dummy=tt3.dummy)inner join(select*from dual)tt2 using(dummy)inner join(select*from dual)using(dummy)inner join(select*from dual)d on(d.dummy=t1.dummy)
;

-- testAllSqlsParseDeparse
select department_id as d_e_dept_id,e.last_name from departments full outer join employees on(a=b)left outer join employees on(a=b)right outer join employees on(a=b)join employees on(a=b)inner join employees on(a=b)cross join employees natural join employees
;

-- testAllSqlsParseDeparse
merge into bonuses d using(select employee_id.*from employees)s on(employee_id=a)when not matched then insert(d.employee_id,d.bonus)values(s.employee_id,s.salary)where(s.salary<=8000)when matched then update set d.bonus=bonus delete where(salary>8000)
;

-- testAllSqlsParseDeparse
with reports_to_101(eid,emp_last,mgr_id,reportlevel)as((select employee_id,last_name,manager_id,0 reportlevel from employees where employee_id=101)union all(select e.employee_id,e.last_name,e.manager_id,reportlevel+1 from reports_to_101 r,employees e where r.eid=e.manager_id))select eid,emp_last,mgr_id,reportlevel from reports_to_101 r,auto a where r.c1=a.c2 order by reportlevel,eid
;

-- testAllSqlsParseDeparse
(select bs.keep keep,bs.keep_until keep_until from v$backup_set bs)union all(select null keep,null keep_until from v$backup_piece bp)
;

-- testAllSqlsParseDeparse
select employee_id from(select employee_id+1 as employee_id from employees)for update of employee_id skip locked
;

-- testAllSqlsParseDeparse
select manager_id,last_name,hire_date,count(*)over(partition by manager_id order by hire_date range numtodsinterval(100,'day')preceding)as t_count from employees
;

-- testAllSqlsParseDeparse
select listagg(column_value,',')within group(order by column_value)from table(cast(multiset(select 'one' from dual union all select 'two' from dual)as t_str))
;

-- testAllSqlsParseDeparse
select staleness,osize,obj#,type#,row_number()over(partition by bo# order by staleness,osize,obj#),case when row_number()over(partition by bo# order by staleness,osize,obj#)=1 then 64 else 0 end+case when row_number()over(partition by(select tcp0.bo# from tabcompart$ tcp0 where tcp0.obj#=st0.bo#)order by staleness,osize,obj#)=1 then 32 else 0 end aflags,0 status,:b5 sid,:b4 serial#,part#,bo#,loc_stale_pct from a
;

-- testAllSqlsParseDeparse
select root,lev,obj,link,path,cycle,case when(lev-lead(lev)over(order by ord))<0 then 0 else 1 end is_leaf from t
;

-- testAllSqlsParseDeparse
select case when row_number()over(partition by bo# order by staleness,osize,obj#)=1 then 32 else 0 end+64 aflags from f
;

-- testAllSqlsParseDeparse
select staleness,osize,obj#,type#,case when row_number()over(partition by bo# order by staleness,osize,obj#)=1 then 64 else 0 end+case when row_number()over(partition by(select tcp0.bo# from tabcompart$ tcp0 where tcp0.obj#=st0.bo#)order by staleness,osize,obj#)=1 then 32 else 0 end aflags,0 status,:b3 sid,:b2 serial#,part#,bo# from st0
;

-- testAllSqlsParseDeparse
select*from(select row_.*,rownum rownum_ from(select*from(select results.*,row_number()over(partition by results.object_id order by results.gmt_modified desc)rn from((select sus.id id,sus.gmt_create gmt_create,sus.gmt_modified gmt_modified,sus.company_id company_id,sus.object_id object_id,sus.object_type object_type,sus.confirm_type confirm_type,sus.operator operator,sus.filter_type filter_type,sus.member_id member_id,sus.member_fuc_q member_fuc_q,sus.risk_type risk_type,'y' is_draft from f_u_c_ sus,a_b_c_draft p,member m where 1=1 and p.company_id=m.company_id and m.login_id=? and p.sale_type in(?)and p.id=sus.object_id)union(select sus.id id,sus.gmt_create gmt_create,sus.gmt_modified gmt_modified,sus.company_id company_id,sus.object_id object_id,sus.object_type object_type,sus.confirm_type confirm_type,sus.operator operator,sus.filter_type filter_type,sus.member_id member_id,sus.member_fuc_q member_fuc_q,sus.risk_type risk_type,'n' is_draft from f_u_c_ sus,a_b_c p,member m where 1=1 and p.company_id=m.company_id and m.login_id=? and p.sale_type in(?)and p.id=sus.object_id))results)where rn=1 order by gmt_modified desc)row_ where rownum<=?)where rownum_>=?
;

-- testAllSqlsParseDeparse
select deptno,ename,hiredate,listagg(ename,',')within group(order by hiredate)over(partition by deptno)as employees from emp
;

-- testAllSqlsParseDeparse
with timegrouped_rawdata as(select sh.metric_id as metric_id,ot.bsln_guid as bsln_guid,ot.timegroup as timegroup,sh.value as obs_value from dba_hist_snapshot sn,dba_hist_database_instance di,sys.wrh$_sysmetric_history sh,bsln_metric_defaults md,table(:b1)ot where sn.dbid=:b6 and sn.snap_id between:b5 and:b4 and di.dbid=sn.dbid and di.instance_number=sn.instance_number and di.startup_time=sn.startup_time and di.instance_name=:b3 and sh.snap_id=sn.snap_id and sh.dbid=sn.dbid and sh.instance_number=sn.instance_number and sh.group_id=2 and sh.metric_id=md.metric_id and md.status=:b2 and ot.obs_time=trunc(sh.end_time,'hh24'))(select bsln_statistics_t(bsln_guid,metric_id,:b11,:b10,timegroup,sample_count,average,minimum,maximum,sdev,pctile_25,pctile_50,pctile_75,pctile_90,pctile_95,pctile_99,est_sample_count,est_slope,est_intercept,case when est_slope=0 then 0 else greatest(0,nvl(100-(25*power((1-est_mu1/est_slope),2)*(est_sample_count-1)),0))end,ln(1000)*est_slope+est_intercept,ln(10000)*est_slope+est_intercept)from(select metric_id,bsln_guid,timegroup,est_mu as est_slope,est_mu*ln(alpha)+x_m as est_intercept,to_number(null)as est_fit_quality,case when count_below_x_j>0 then(sum_below_x_j+(n-m+1)*(x_j-x_m))/count_below_x_j-x_j else to_number(null)end as est_mu1,est_sample_count,n as sample_count,average,minimum,maximum,sdev,pctile_25,pctile_50,pctile_75,pctile_90,pctile_95,pctile_99 from(select metric_id,bsln_guid,timegroup,max(n)as n,count(rrank)as est_sample_count,case when count(rrank)>3 then(sum(obs_value)+(max(n)-max(rrank))*max(obs_value)-(max(n)-min(rrank)+1)*min(obs_value))/(count(rrank)-1)else to_number(null)end as est_mu,(max(n)-min(rrank)+1)/(max(n)+1)as alpha,min(obs_value)as x_m,max(obs_value)as x_l,max(rrank)as l,min(rrank)as m,max(mid_tail_value)as x_j,sum(case when obs_value<mid_tail_value then obs_value else 0 end)as sum_below_x_j,sum(case when cume_dist<:b7 then 1 else 0 end)as count_below_x_j,max(max_val)as maximum,max(min_val)as minimum,max(avg_val)as average,max(sdev_val)as sdev,max(pctile_25)as pctile_25,max(pctile_50)as pctile_50,max(pctile_75)as pctile_75,max(pctile_90)as pctile_90,max(pctile_95)as pctile_95,max(pctile_99)as pctile_99 from(select metric_id,bsln_guid,timegroup,obs_value as obs_value,cume_dist()over(partition by metric_id,bsln_guid,timegroup order by obs_value)as cume_dist,count(1)over(partition by metric_id,bsln_guid,timegroup)as n,row_number()over(partition by metric_id,bsln_guid,timegroup order by obs_value)as rrank,percentile_disc(:b7)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as mid_tail_value,max(obs_value)over(partition by metric_id,bsln_guid,timegroup)as max_val,min(obs_value)over(partition by metric_id,bsln_guid,timegroup)as min_val,avg(obs_value)over(partition by metric_id,bsln_guid,timegroup)as avg_val,stddev(obs_value)over(partition by metric_id,bsln_guid,timegroup)as sdev_val,percentile_cont(0.25)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as pctile_25,percentile_cont(0.5)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as pctile_50,percentile_cont(0.75)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as pctile_75,percentile_cont(0.90)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as pctile_90,percentile_cont(0.95)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as pctile_95,percentile_cont(0.99)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as pctile_99 from timegrouped_rawdata d)x where x.cume_dist>=:b9 and x.cume_dist<=:b8 group by metric_id,bsln_guid,timegroup)))
;

-- testAllSqlsParseDeparse
select metric_id,bsln_guid,timegroup,obs_value as obs_value,cume_dist()over(partition by metric_id,bsln_guid,timegroup order by obs_value)as cume_dist,count(1)over(partition by metric_id,bsln_guid,timegroup)as n,row_number()over(partition by metric_id,bsln_guid,timegroup order by obs_value)as rrank,percentile_disc(:b7)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as mid_tail_value,max(obs_value)over(partition by metric_id,bsln_guid,timegroup)as max_val,min(obs_value)over(partition by metric_id,bsln_guid,timegroup)as min_val,avg(obs_value)over(partition by metric_id,bsln_guid,timegroup)as avg_val,stddev(obs_value)over(partition by metric_id,bsln_guid,timegroup)as sdev_val,percentile_cont(0.25)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as pctile_25,percentile_cont(0.5)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as pctile_50,percentile_cont(0.75)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as pctile_75,percentile_cont(0.90)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as pctile_90,percentile_cont(0.95)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as pctile_95,percentile_cont(0.99)within group(order by obs_value asc)over(partition by metric_id,bsln_guid,timegroup)as pctile_99 from timegrouped_rawdata d
;

-- testAllSqlsParseDeparse
with o as(select 'a' obj,'b' link from dual union all select 'a','c' from dual union all select 'c','d' from dual union all select 'd','c' from dual union all select 'd','e' from dual union all select 'e','e' from dual)select connect_by_root obj root,level,obj,link,sys_connect_by_path(obj||'->'||link,','),connect_by_iscycle,connect_by_isleaf from o connect by nocycle obj=prior link start with obj='a'
;

-- testAllSqlsParseDeparse
with liste as(select substr(:liste,instr(','||:liste||',',',',1,rn),instr(','||:liste||',',',',1,rn+1)-instr(','||:liste||',',',',1,rn)-1)valeur from(select rownum rn from dual connect by level<=length(:liste)-length(replace(:liste,',',''))+1))select trim(valeur)from liste
;

-- testAllSqlsParseDeparse
select*from dual order by 1
;

-- testAllSqlsParseDeparse
select*from dual order by 1 asc
;

-- testAllSqlsParseDeparse
select*from dual order by m.year,m.title,f(a)
;

-- testAllSqlsParseDeparse
select*from dual order by a nulls first,b nulls last
;

-- testAllSqlsParseDeparse
select*from dual order siblings by a nulls first,b nulls last,c nulls last,d nulls last,e nulls last
;

-- testAllSqlsParseDeparse
with a as(select*from dual order by 1)select*from a
;

-- testAllSqlsParseDeparse
select*from(select customer_id,product_code,quantity from pivot_test)pivot(sum(quantity)as sum_quantity for product_code in('a' as a,'b' as b,'c' as c))order by customer_id
;

-- testAllSqlsParseDeparse
with reports_to_101(eid,emp_last,mgr_id,reportlevel,mgr_list)as((select employee_id,last_name,manager_id,0 reportlevel,cast(manager_id as varchar2(2000))from employees where employee_id=101)union all(select e.employee_id,e.last_name,e.manager_id,reportlevel+1,cast(mgr_list||','||manager_id as varchar2(2000))from reports_to_101 r,employees e where r.eid=e.manager_id))select eid,emp_last,mgr_id,reportlevel,mgr_list from reports_to_101 order by reportlevel,eid
;

-- testAllSqlsParseDeparse
with rn as(select rownum rn from dual connect by level<=(select max(cases)from t1))select pname from t1,rn where rn<=cases order by pname
;

-- testAllSqlsParseDeparse
with days as(select(select trunc(sysdate,'month')from dual)+rownum-1 as d from dual connect by rownum<31)select d from days where(trunc(d)-trunc(d,'iw')+1)not in(6,7)and d<=last_day(sysdate)
;

-- testAllSqlsParseDeparse
select c.constraint_name,max(r.constraint_name)as r_constraint_name,max(c.owner)as owner,max(c.table_name)as table_name,c.column_name as column_name,max(r.owner)as r_owner,max(r.table_name)as r_table_name,max(r.column_name)as r_column_name,max(a.constraint_type)from sys.all_constraints a join sys.all_cons_columns c on(c.constraint_name=a.constraint_name and c.owner=a.owner)join sys.all_cons_columns r on(r.constraint_name=a.r_constraint_name and r.owner=a.r_owner and r.position=c.position)where a.r_owner=:f1 and a.constraint_type='r' group by c.constraint_name,rollup(c.column_name)
;

-- testAllSqlsParseDeparse
select ind.index_owner,ind.index_name,ind.uniqueness,ind.status,ind.index_type,ind.temporary,ind.partitioned,ind.funcidx_status,ind.join_index,ind.columns,ie.column_expression,ind.index_name sdev_link_name,'index' sdev_link_type,ind.index_owner sdev_link_owner from(select index_owner,table_owner,index_name,uniqueness,status,index_type,temporary,partitioned,funcidx_status,join_index,max(decode(position,1,column_name))||max(decode(position,2,','||column_name))||max(decode(position,3,','||column_name))||max(decode(position,4,','||column_name))||max(decode(position,5,','||column_name))||max(decode(position,6,','||column_name))||max(decode(position,7,','||column_name))||max(decode(position,8,','||column_name))||max(decode(position,9,','||column_name))||max(decode(position,10,','||column_name))columns from(select di.owner index_owner,dc.table_owner,dc.index_name,di.uniqueness,di.status,di.index_type,di.temporary,di.partitioned,di.funcidx_status,di.join_index,dc.column_name,dc.column_position position from all_ind_columns dc,all_indexes di where dc.table_owner=:object_owner and dc.table_name=:object_name and dc.index_name=di.index_name and dc.index_owner=di.owner)group by index_owner,table_owner,index_name,uniqueness,status,index_type,temporary,partitioned,funcidx_status,join_index)ind,all_ind_expressions ie where ind.index_name=ie.index_name(+)and ind.index_owner=ie.index_owner(+)
;

-- testOperatorsWithSpaces
select something from sometable where somefield>=somevalue and somefield<=somevalue and somefield<>somevalue and somefield!=somevalue
;

-- testOperatorsWithSpaces
select something from sometable where somefield>=somevalue and somefield<=somevalue and somefield<>somevalue
;

-- testOperatorsWithSpaces
select something from sometable where somefield>=somevalue and somefield<=somevalue and somefield<>somevalue
;

-- debugSpecificSql
select value from((select 'a' v1,'e' v2,'i' v3,'o' v4,'u' v5 from dual)unpivot(value for value_type in(v1,v2,v3,v4,v5)))
;

