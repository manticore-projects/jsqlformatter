-- testLeftSemiJoin
select something from sometable left semi join othertable
;

-- testLeftSemiJoin
select something from sometable left semi join othertable
;

-- testGroupByGroupingSets
select c1,c2,c3,max(value)from sometable group by c1,c2,c3 grouping sets((c1,c2),(c1,c2,c3),())
;

