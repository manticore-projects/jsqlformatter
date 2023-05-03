-- testXmlAgg1
SELECT xmlserialize(xmlagg(xmltext(COMMENT_LINE) ORDER BY COMMENT_SEQUENCE) AS varchar (1024)) FROM mytable GROUP BY COMMENT_NUMBER
;

-- testXmlAgg2
SELECT xmlserialize(xmlagg(xmltext(COMMENT_LINE) ORDER BY COMMENT_SEQUENCE, COMMENT_LINE) AS varchar (1024)) FROM mytable GROUP BY COMMENT_NUMBER
;

-- testXmlAgg3
SELECT xmlserialize(xmlagg(xmltext(COMMENT_LINE) ORDER BY COMMENT_SEQUENCE) AS varchar (1024))
;

-- testXmlAgg4
SELECT xmlserialize(xmlagg(xmltext(COMMENT_LINE_PREFIX || COMMENT_LINE) ORDER BY COMMENT_SEQUENCE) AS varchar (1024))
;

-- testXmlAgg5
SELECT xmlserialize(xmlagg(xmltext(CONCAT(', ', TRIM(SOME_COLUMN))) ORDER BY MY_SEQUENCE) AS varchar (1024))
;

-- testXmlAgg6
SELECT xmlserialize(xmlagg(xmltext(COMMENT_LINE)) AS varchar (1024))
;

