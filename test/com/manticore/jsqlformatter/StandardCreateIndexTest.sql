-- SIMPLE
CREATE INDEX cfe.version_info_idx1
    ON cfe.version_info (   major_version
                            , minor_version
                            , patch_level )
;

-- UNIQUE
CREATE UNIQUE INDEX cfe.interest_period_idx1
    ON cfe.interest_period ( id_instrument, change_date )
;

-- MANY COLUMNS
CREATE UNIQUE INDEX cfe.version_info_idx2
    ON cfe.version_info (   major_version
                            , minor_version
                            , patch_level
                            , major_version
                            , minor_version
                            , patch_level )
;

-- MANY COLUMNS WITH TAIL OPTIONS
CREATE UNIQUE INDEX cfe.version_info_idx2
    ON cfe.version_info (   major_version
                            , minor_version
                            , patch_level
                            , major_version
                            , minor_version
                            , patch_level ) parallel compress nologging
;