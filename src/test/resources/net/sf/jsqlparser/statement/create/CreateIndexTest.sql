-- testCreateIndex6
CREATE INDEX myindex ON mytab (mycol, mycol2)
;

-- testCreateIndex7
CREATE INDEX myindex1 ON mytab USING GIST (mycol)
;

-- testFullIndexNameIssue936_2
CREATE INDEX "TS"."IDX" ON "TEST" ("ID") TABLESPACE "TS"
;

-- testFullIndexNameIssue936
CREATE INDEX "TS"."IDX" ON "TEST" ("ID" ASC) TABLESPACE "TS"
;

-- testCreateIndexIssue633
CREATE INDEX idx_american_football_action_plays_1 ON american_football_action_plays USING btree (play_type)
;

