-- testGlobalJoin
select a.*,b.*from lineorder_all as a global left join supplier_all as b on a.lolinenumber=b.ssuppkey
;

