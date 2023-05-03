-- testIssue1373
select 1 limit 1+0
;

-- testIssue1376
select 1 offset '0'
;

-- testIssue933
select*from tmp3 limit '2'
;

-- testIssue933
select*from tmp3 limit(select 2)
;

