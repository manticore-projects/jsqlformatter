-- testOverlapsCondition
select*from dual where(start_one,end_one)overlaps(start_two,end_two)
;

-- testOverlapsCondition
select*from t1 left join t2 on(t1.start,t1.end)overlaps(t2.start,t2.end)
;

