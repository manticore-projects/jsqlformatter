-- tesLocalWithEq
SET LOCAL cursor_tuple_fraction = 0.05
;

-- testSimpleSet
SET statement_timeout = 0
;

-- testSettingUserVariable
set @flag=1
;

-- testSettingUserVariable
set @@global.time_zone='01:00'
;

-- testListValue
SET v = 1, 3
;

-- tesTimeZone
SET LOCAL Time Zone 'UTC'
;

-- testValueOnIssue927
SET standard_conforming_strings = on
;

-- testMultiValue
SET v = 1, c = 3
;

-- testIssue373_2
SET tester 5
;

-- testIssue373
SET deferred_name_resolution true
;

