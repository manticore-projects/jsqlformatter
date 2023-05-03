-- testAcceptCall
CALL myproc 'a', 2, 'b'
;

-- testAcceptExec
EXEC myproc 'a', 2, 'b'
;

-- testAcceptExecute
EXECUTE myproc 'a', 2, 'b'
;

-- testAcceptCallWithParenthesis
CALL myproc ('a', 2, 'b')
;

-- testAcceptExecNamesParameters
EXEC procedure @param
;

-- testAcceptExecNamesParameters2
EXEC procedure @param = 1
;

-- testAcceptExecNamesParameters3
EXEC procedure @param = 'foo'
;

-- testAcceptExecNamesParameters4
EXEC procedure @param = 'foo', @param2 = 'foo2'
;

-- testCallWithMultiname
CALL BAR.FOO
;

