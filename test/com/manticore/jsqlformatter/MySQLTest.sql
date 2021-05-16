-- GROUP_CONCAT
SELECT Group_Concat(    DISTINCT Trim( Concat(  m.title, ' ', m.firstname
                                                , ' ', m.lastname ) )
                        ORDER BY p2m.manufacturer_type_id ASC
                        SEPARATOR ' ' ) AS manufacturer_name
FROM product_to_manufacturer p2m
    LEFT JOIN manufacturer m
        ON m.manufacturer_id = p2m.manufacturer_id
WHERE p2m.product_id = 574768
;