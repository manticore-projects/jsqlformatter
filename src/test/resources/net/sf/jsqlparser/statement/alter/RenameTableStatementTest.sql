-- testStatement
rename oldtablename to newtablename
;

-- testStatement
rename table old_table to backup_table,new_table to old_table
;

-- testStatement
rename table if exists old_table wait 20 to backup_table,new_table to old_table
;

-- testStatement
rename table if exists old_table nowait to backup_table,new_table to old_table
;

-- testValidator
rename oldtablename to newtablename
;

-- testValidator
alter table public.oldtablename rename to newtablename
;

-- testValidator
alter table if exists public.oldtablename rename to newtablename
;

