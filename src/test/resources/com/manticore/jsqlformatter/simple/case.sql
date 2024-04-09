SELECT  t
        , len
        , Printf( '%s', CASE Typeof( t )
                        WHEN 'VARCHAR'
                            THEN Rpad( t::VARCHAR, len, ' ' )
                        END ) AS rpad
FROM (  SELECT Unnest(  [
                            { t:'abc',len:5 }
                            , ( 'abc', 2 )
                            , ( '例子', 4 )
                        ], recursive => true ) )
;