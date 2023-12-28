-- BETWEEN
SELECT kal_datum BETWEEN Trunc( a.adate )
                     AND (  SELECT Max( Trunc( change_date ) )
                            FROM besch_statusaenderung
                            WHERE beschwerden_id = a.beschwerden_id
                                AND beschstatus_id = 9
                                AND Nvl( inaktiv, 'F' ) != 'T' )
;