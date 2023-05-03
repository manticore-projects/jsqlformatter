-- testObjectMySQL
select json_object('person',tp.person,'account',tp.account)obj
;

-- testObjectWithExpression
select json_object(key 'foo' value cast(bar as varchar(40)),key 'foo' value bar)from dual
;

-- testObjectWithExpression
select json_arrayagg(obj)from(select trt.relevance_id,json_object('id',cast(trt.id as char),'taskname',trt.task_name,'openstatus',trt.open_status,'tasksort',trt.task_sort)as obj from tb_review_task trt order by trt.task_sort asc)
;

-- testArray
select json_array((select*from dual))from dual
;

-- testArray
select json_array(1,2,3)from dual
;

-- testArray
select json_array("v0")from dual
;

-- testObjectAgg
select json_objectagg(key foo value bar)from dual
;

-- testObjectAgg
select json_objectagg(foo:bar)from dual
;

-- testObjectAgg
select json_objectagg(foo:bar format json)from dual
;

-- testObjectAgg
select json_objectagg(key foo value bar null on null)from dual
;

-- testObjectAgg
select json_objectagg(key foo value bar absent on null)from dual
;

-- testObjectAgg
select json_objectagg(key foo value bar with unique keys)from dual
;

-- testObjectAgg
select json_objectagg(key foo value bar without unique keys)from dual
;

-- testObjectAgg
select json_objectagg(key foo value bar null on null with unique keys)from dual
;

-- testObjectAgg
select json_objectagg(key foo value bar null on null with unique keys)filter(where name='raj')from dual
;

-- testObjectAgg
select json_objectagg(key foo value bar null on null with unique keys)over(partition by name)from dual
;

-- testObjectAgg
select json_objectagg(key foo value bar null on null with unique keys)filter(where name='raj')over(partition by name)from dual
;

-- testArrayAgg
select json_arrayagg(a)from dual
;

-- testArrayAgg
select json_arrayagg(a order by a)from dual
;

-- testArrayAgg
select json_arrayagg(a null on null)from dual
;

-- testArrayAgg
select json_arrayagg(a format json)from dual
;

-- testArrayAgg
select json_arrayagg(a format json null on null)from dual
;

-- testArrayAgg
select json_arrayagg(a format json absent on null)from dual
;

-- testArrayAgg
select json_arrayagg(a format json absent on null)filter(where name='raj')from dual
;

-- testArrayAgg
select json_arrayagg(a format json absent on null)over(partition by name)from dual
;

-- testArrayAgg
select json_arrayagg(a format json absent on null)filter(where name='raj')over(partition by name)from dual
;

-- testArrayAgg
select json_arrayagg(json_array("v0")order by "t"."v0")from dual
;

-- testObjectIssue1504
select json_object(key 'person' value tp.account)obj
;

-- testObjectIssue1504
select json_object(key 'person' value tp.account,key 'person' value tp.account)obj
;

-- testObjectIssue1504
select json_object('person':tp.account)obj
;

-- testObjectIssue1504
select json_object('person':tp.account,'person':tp.account)obj
;

-- testObjectIssue1504
select json_object('person':'1','person':'2')obj
;

-- testObjectIssue1504
select json_object('person' value tp.person,'account' value tp.account)obj
;

-- testIssue1260
select cast((select coalesce(json_arrayagg(json_array("v0")order by "t"."v0"),json_array(null on null))from(select 2 "v0" union select 4 "id")"t")as text)
;

-- testIssue1260
select(select coalesce(cast(('['||listagg(json_object(key 'v0' value "v0"),',')||']')as varchar(32672)),json_array())from(select cast(null as timestamp)"v0" from sysibm.dual union all select timestamp '2000-03-15 10:15:00.0' "a" from sysibm.dual)"t")from sysibm.dual
;

-- testIssue1371
select json_object('{a,1,b,2}')
;

-- testIssue1371
select json_object('{{a,1},{b,2}}')
;

-- testIssue1371
select json_object('{a,b}','{1,2 }')
;

-- testObject
select json_object(key 'foo' value bar,key 'foo' value bar)from dual
;

-- testObject
select json_object('foo':bar,'foo':bar)from dual
;

-- testObject
select json_object('foo':bar,'foo':bar format json)from dual
;

-- testObject
select json_object(key 'foo' value bar,'foo':bar format json,'foo':bar null on null)from dual
;

-- testObject
select json_object(key 'foo' value bar format json,'foo':bar,'foo':bar absent on null)from dual
;

-- testObject
select json_object(key 'foo' value bar format json,'foo':bar,'foo':bar absent on null with unique keys)from dual
;

-- testObject
select json_object(key 'foo' value bar format json,'foo':bar,'foo':bar absent on null without unique keys)from dual
;

