-- testCommentColumnDeparseIssue696
COMMENT ON COLUMN hotels.hotelid IS 'Primary key of the table'
;

-- testCommentTableDeparse
COMMENT ON TABLE table1 IS 'comment1'
;

-- testCommentOnView
COMMENT ON VIEW myschema.myView IS 'myComment'
;

-- testCommentColumnDeparse
COMMENT ON COLUMN table1.column1 IS 'comment1'
;

