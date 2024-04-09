-- LISTAGG 1
SELECT Listagg( name, ', ' )
            WITHIN GROUP (ORDER BY ID)
;

-- LISTAGG 2
SELECT Listagg( Coalesce( name, 'null' ), ', ' )
            WITHIN GROUP (ORDER BY ID)
;

-- LISTAGG 3
/* Unsupported: SELECT LISTAGG(ID, ', ') WITHIN GROUP (ORDER BY ID) OVER (ORDER BY ID); */
SELECT 1
FROM dual
;

-- ARRAY_AGG 1
SELECT Array_Agg( name )
;

-- ARRAY_AGG 2
SELECT Array_Agg( name ORDER BY ID )
            FILTER ( WHERE NAME IS NOT NULL )
;

-- ARRAY_AGG 3
/* SELECT ARRAY_AGG(ID ORDER BY ID) OVER (ORDER BY ID); */
SELECT 1
FROM dual
;