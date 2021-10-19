-- SIMPLE LIMIT
SELECT p.*
FROM product p
    LEFT JOIN product_description pd
        ON ( p.product_id = pd.product_id )
    LEFT JOIN product_type_google_category ptgc
        ON ( p.product_type_id = ptgc.product_type_id )
    LEFT JOIN product_google_custom_label pgcl
        ON ( p.product_id = pgcl.product_id )
WHERE p.status = 1
    AND pd.language_id = 2
    AND p.product_id IN (   SELECT product_id
                            FROM cj_googleshopping_products )
ORDER BY    date_available DESC
            , p.purchased DESC
LIMIT 200000
;

-- LIMIT OFFSET EXPRESSIONS
SELECT p.*
FROM product p
LIMIT '200000'
OFFSET '5'
;

-- MYSQL LIMIT OFFSET EXPRESSIONS
SELECT p.*
FROM product p
LIMIT 5, 2000
;