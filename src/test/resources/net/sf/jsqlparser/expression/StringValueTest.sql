-- testOracleAlternativeQuoting
comment on column emp.name is q'{na'm\e}'
;

-- testOracleAlternativeQuoting
comment on column emp.name is q'(na'm\e)'
;

-- testOracleAlternativeQuoting
comment on column emp.name is q'[na'm\e]'
;

-- testOracleAlternativeQuoting
comment on column emp.name is q''na'm\e]''
;

-- testOracleAlternativeQuoting
select q'{its good!}' from dual
;

-- testOracleAlternativeQuoting
select q'{it's good!}' from dual
;

