-- testFullTextSearchExpressionWithParameters
select match(name)against(?)as full_text from commodity
;

-- testFullTextSearchExpressionWithParameters
select match(name)against(:parameter)as full_text from commodity
;

-- testIssue1223
select c.*,match(name)against(?)as full_text from commodity c where match(name)against(?)and c.deleted=0 order by full_text desc
;

