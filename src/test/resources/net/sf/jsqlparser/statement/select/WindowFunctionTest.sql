-- testListAggOverIssue1652
select listagg(d.col_to_agg,'/')within group(order by d.col_to_agg)over(partition by d.part_col)as my_listagg from cte_dummy_data d
;

