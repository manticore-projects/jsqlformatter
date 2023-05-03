-- testKSQLBeforeAfterWindowedJoin
select*from table1 t1 inner join table2 t2 within(1 minute,5 minutes)on t1.id=t2.id
;

-- testKSQLBeforeAfterWindowedJoin
select*from table1 t1 inner join table2 t2 within(1 minute,5 minutes)on t1.id=t2.id
;

-- testKSQLEmitChangesWithLimit
SELECT * FROM table1 t1 GROUP BY region.id EMIT CHANGES LIMIT 2
;

-- testKSQLHoppingWindows
select*from table1 t1 window hopping(size 30 seconds,advance by 10 minutes)group by region.id
;

-- testKSQLHoppingWindows
select*from table1 t1 window hopping(size 30 seconds,advance by 10 minutes)group by region.id
;

-- testKSQLEmitChanges
SELECT * FROM table1 t1 GROUP BY region.id EMIT CHANGES
;

-- testKSQLTumblingWindows
select*from table1 t1 window tumbling(size 30 seconds)group by region.id
;

-- testKSQLTumblingWindows
select*from table1 t1 window tumbling(size 30 seconds)group by region.id
;

-- testKSQLWindowedJoin
select*from table1 t1 inner join table2 t2 within(5 hours)on t1.id=t2.id
;

-- testKSQLWindowedJoin
select*from table1 t1 inner join table2 t2 within(5 hours)on t1.id=t2.id
;

-- testKSQLSessionWindows
select*from table1 t1 window session(5 minutes)group by region.id
;

-- testKSQLSessionWindows
select*from table1 t1 window session(5 minutes)group by region.id
;

