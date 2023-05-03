-- testDeleteMultipleModifiers
DELETE LOW_PRIORITY QUICK FROM tablename
;

-- testDeleteMultipleModifiers
DELETE LOW_PRIORITY QUICK IGNORE FROM tablename
;

-- testDeleteReturningIssue1527
delete from t returning*
;

-- testDeleteReturningIssue1527
delete from products where price<=99.99 returning name,price as new_price
;

-- testDeleteWithOrderByAndLimit
DELETE FROM tablename WHERE a = 1 AND b = 1 ORDER BY col LIMIT 10
;

-- testDeleteQuickModifier
DELETE QUICK FROM tablename
;

-- testDeleteQuickModifier
DELETE FROM tablename
;

-- testUsing
DELETE A USING B.C D WHERE D.Z = 1
;

-- testDeleteFromTableUsingInnerJoinToAnotherTableWithAlias
DELETE gc FROM guide_category AS gc LEFT JOIN guide AS g ON g.id_guide = gc.id_guide WHERE g.title IS NULL LIMIT 5
;

-- testWith
with a as(select 1 id_instrument_ref),b as(select 1 id_instrument_ref)delete from cfe.instrument_ref where id_instrument_ref=(select id_instrument_ref from a)
;

-- testDeleteFromTableUsingInnerJoinToAnotherTable
DELETE Table1 FROM Table1 INNER JOIN Table2 ON Table1.ID = Table2.ID
;

-- testDeleteFromTableUsingLeftJoinToAnotherTable
DELETE g FROM Table1 AS g LEFT JOIN Table2 ON Table1.ID = Table2.ID
;

-- testDeleteLowPriority
DELETE LOW_PRIORITY FROM tablename
;

-- testDeleteMultiTableIssue878
DELETE table1, table2 FROM table1, table2
;

-- testDeleteIgnoreModifier
DELETE IGNORE FROM tablename
;

-- testDeleteIgnoreModifier
DELETE FROM tablename
;

-- testDeleteOutputClause
delete sales.shoppingcartitem output deleted.*from sales
;

-- testDeleteOutputClause
delete sales.shoppingcartitem output sales.shoppingcartitem from sales
;

-- testDeleteOutputClause
delete production.productproductphoto output deleted.productid,p.name,p.productmodelid,deleted.productphotoid into @mytablevar from production.productproductphoto as ph join production.product as p on ph.productid=p.productid where p.productmodelid between 120 and 130
;

-- testNoFromWithSchema
DELETE A.B WHERE Z = 1
;

-- testDeleteWhereProblem1
DELETE FROM tablename WHERE a = 1 AND b = 1
;

-- testOracleHint
delete from mytable where mytable.col=9
;

-- testDeleteWithLimit
DELETE FROM tablename WHERE a = 1 AND b = 1 LIMIT 5
;

-- testNoFrom
DELETE A WHERE Z = 1
;

-- testDeleteWithOrderBy
DELETE FROM tablename WHERE a = 1 AND b = 1 ORDER BY col
;

