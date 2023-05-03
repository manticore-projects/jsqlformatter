-- testBlock2
begin update table1 set a='xx' where b='condition1'; update table1 set a='xx' where b='condition2'; end;

-- testGetStatements
begin select*from feature; end;

-- testIfElseBlock
if(a=b)begin update table1 set a='xx' where b='condition1'; update table1 set a='xx' where b='condition2'; end
;

