-- testAlterTableDropColumnIfExists
ALTER TABLE test DROP COLUMN IF EXISTS name
;

-- testAlterTablePrimaryKeyNotDeferrable
ALTER TABLE animals ADD PRIMARY KEY (id) NOT DEFERRABLE
;

-- testAlterTablePrimaryKeyDeferrableValidate
ALTER TABLE animals ADD PRIMARY KEY (id) DEFERRABLE VALIDATE
;

-- testAlterTableRenameColumn
ALTER TABLE "test_table" RENAME COLUMN "test_column" TO "test_c"
;

-- testAlterTableRenameColumn
ALTER TABLE "test_table" RENAME "test_column" TO "test_c"
;

-- testAlterOnUpdateSetNull
alter table mytab add constraint fk_mytab foreign key(col)references reftab(id)on update set null
;

-- testAlterOnUpdateSetNull
alter table mytab add foreign key(col)references reftab(id)on update set null
;

-- testAlterColumnSetCommitTimestamp1
ALTER TABLE FOCUS_PATIENT ALTER COLUMN UPDATE_DATE_TIME_GMT SET OPTIONS (allow_commit_timestamp=true)
;

-- testAlterTableTableCommentIssue984
ALTER TABLE texto_fichero COMMENT 'This is a sample comment'
;

-- testAlterTableCheckConstraint
ALTER TABLE `Author` ADD CONSTRAINT name_not_empty CHECK (`NAME` <> '')
;

-- testAlterTableAddUniqueConstraint
ALTER TABLE Persons ADD UNIQUE (ID)
;

-- testAlterTableAddConstraintWithConstraintState2
ALTER TABLE RESOURCELINKTYPE ADD CONSTRAINT RESOURCELINKTYPE_PRIMARYKEY PRIMARY KEY (PRIMARYKEY) DEFERRABLE NOVALIDATE
;

-- testAlterOnDeleteNoAction
alter table mytab add constraint fk_mytab foreign key(col)references reftab(id)on delete no action
;

-- testAlterOnDeleteNoAction
alter table mytab add foreign key(col)references reftab(id)on delete no action
;

-- testAlterTableAddColumnSpanner7
alter table order_patient add column first_name_uppercase string(max)as(upper(first_name))stored
;

-- testAlterTableAddColumnSpanner8
alter table order_patient add column names array<string(max)>
;

-- testAlterTableForeignKeyIssue981_2
ALTER TABLE atconfigpro ADD CONSTRAINT atconfigpro_atconfignow_id_foreign FOREIGN KEY (atconfignow_id) REFERENCES atconfignow(id) ON DELETE CASCADE
;

-- testAlterConstraintWithoutFKSourceColumnsIssue929
ALTER TABLE orders ADD CONSTRAINT fk_orders_customers FOREIGN KEY (customer_id) REFERENCES customers
;

-- testAlterTablePrimaryKeyValidate
ALTER TABLE animals ADD PRIMARY KEY (id) VALIDATE
;

-- testAlterOnlyIssue928
ALTER TABLE ONLY categories ADD CONSTRAINT pk_categories PRIMARY KEY (category_id)
;

-- testAlterTableUniqueKey
ALTER TABLE `schema_migrations` ADD UNIQUE KEY `unique_schema_migrations` (`version`)
;

-- testAlterTablePrimaryKeyNoValidate
ALTER TABLE animals ADD PRIMARY KEY (id) NOVALIDATE
;

-- testAlterTableAddColumn2
ALTER TABLE animals ADD (col1 integer, col2 integer)
;

-- testAlterTableAddColumn3
ALTER TABLE mytable ADD COLUMN mycolumn varchar (255)
;

-- testAlterTableAddColumn4
ALTER TABLE mytable ADD COLUMN col1 varchar (255), ADD COLUMN col2 integer
;

-- testAlterTableAddColumn5
ALTER TABLE mytable ADD col1 timestamp (3)
;

-- testAlterTableAddColumn6
ALTER TABLE mytable ADD COLUMN col1 timestamp (3) not null
;

-- testAlterTableDropConstraintIfExists
ALTER TABLE Persons DROP CONSTRAINT IF EXISTS UC_Person
;

-- testAlterTablePrimaryKeyDeferrable
ALTER TABLE animals ADD PRIMARY KEY (id) DEFERRABLE
;

-- testAlterTablePrimaryKey
ALTER TABLE animals ADD PRIMARY KEY (id)
;

-- testAlterTableDropColumn2
ALTER TABLE mytable DROP COLUMN col1, DROP COLUMN col2
;

-- testAlterTableAddColumnWithZone
ALTER TABLE mytable ADD COLUMN col1 timestamp with time zone
;

-- testAlterTableAddColumnWithZone
ALTER TABLE mytable ADD COLUMN col1 timestamp without time zone
;

-- testAlterTableAddColumnWithZone
ALTER TABLE mytable ADD COLUMN col1 date with time zone
;

-- testAlterTableAddColumnWithZone
ALTER TABLE mytable ADD COLUMN col1 date without time zone
;

-- testAlterTableAddConstraint
ALTER TABLE RESOURCELINKTYPE ADD CONSTRAINT FK_RESOURCELINKTYPE_PARENTTYPE_PRIMARYKEY FOREIGN KEY (PARENTTYPE_PRIMARYKEY) REFERENCES RESOURCETYPE(PRIMARYKEY)
;

-- testRowFormatKeywordIssue1033
alter table basic_test_case add column display_name varchar(512)not null default '' after name,add key test_case_status(test_case_status),add key display_name(display_name),row_format=dynamic
;

-- testRowFormatKeywordIssue1033
alter table t1 move tablespace users
;

-- testRowFormatKeywordIssue1033
alter table test_tab move partition test_tab_q2 compress
;

-- testDropColumnRestrictIssue510
ALTER TABLE TABLE1 DROP COLUMN NewColumn CASCADE
;

-- testDropColumnRestrictIssue551
ALTER TABLE table1 DROP NewColumn
;

-- testAlterTableChangeColumn3
ALTER TABLE tb_test CHANGE COLUMN c1 c2 INT (10)
;

-- testAlterTableChangeColumn4
ALTER TABLE tb_test CHANGE c1 c2 INT (10)
;

-- testIssue633_2
CREATE INDEX idx_american_football_action_plays_1 ON american_football_action_plays USING btree (play_type)
;

-- testIssue985_1
alter table texto_fichero add constraint texto_fichero_fichero_id_foreign foreign key(fichero_id)references fichero(id)on delete set default on update cascade,add constraint texto_fichero_texto_id_foreign foreign key(texto_id)references texto(id)on delete set default on update cascade
;

-- testIssue985_1
alter table texto_fichero add foreign key(fichero_id)references fichero(id)on delete set default on update cascade,add foreign key(texto_id)references texto(id)on delete set default on update cascade
;

-- testIssue985_2
alter table texto add constraint texto_autor_id_foreign foreign key(autor_id)references users(id)on update cascade,add constraint texto_tipotexto_id_foreign foreign key(tipotexto_id)references tipotexto(id)on update cascade
;

-- testAlterTableModifyColumn1
ALTER TABLE animals MODIFY (col1 integer, col2 number (8, 2))
;

-- testAlterTableModifyColumn2
ALTER TABLE mytable MODIFY col1 timestamp (6)
;

-- testAlterTableDropConstraintsIssue1342
alter table a drop primary key
;

-- testAlterTableDropConstraintsIssue1342
alter table a drop unique(b,c,d)
;

-- testAlterTableDropConstraintsIssue1342
alter table a drop foreign key(b,c,d)
;

-- testAlterTableDropMultipleColumnsIfExistsWithParams
ALTER TABLE test DROP COLUMN IF EXISTS name CASCADE, DROP COLUMN IF EXISTS surname CASCADE
;

-- testAlterTableAlterColumnDropNotNullIssue918
ALTER TABLE "user_table_t" ALTER COLUMN name DROP NOT NULL
;

-- testAlterTableChangeColumnDropDefault
alter table a modify column b drop default
;

-- testAlterTableChangeColumnDropDefault
alter table a modify(column b drop default,column c drop default)
;

-- testAlterTableChangeColumnDropDefault
alter table a modify(column b drop not null,column b drop default)
;

-- testAlterTableChangeColumnDropDefault
alter table a modify(column b drop default,column b drop not null)
;

-- testAlterTableAddConstraintWithConstraintState
ALTER TABLE RESOURCELINKTYPE ADD CONSTRAINT FK_RESOURCELINKTYPE_PARENTTYPE_PRIMARYKEY FOREIGN KEY (PARENTTYPE_PRIMARYKEY) REFERENCES RESOURCETYPE(PRIMARYKEY) DEFERRABLE DISABLE NOVALIDATE
;

-- testAlterOnUpdateCascade
alter table mytab add constraint fk_mytab foreign key(col)references reftab(id)on update cascade
;

-- testAlterOnUpdateCascade
alter table mytab add foreign key(col)references reftab(id)on update cascade
;

-- testAddConstraintKeyIssue320
ALTER TABLE table1 ADD CONSTRAINT table1_constraint_1 UNIQUE KEY (col1)
;

-- testAddConstraintKeyIssue320
ALTER TABLE table1 ADD CONSTRAINT table1_constraint_1 UNIQUE KEY (col1, col2)
;

-- testAddConstraintKeyIssue320
ALTER TABLE table1 ADD CONSTRAINT table1_constraint_1 UNIQUE KEY (col1, col2), ADD CONSTRAINT table1_constraint_2 UNIQUE KEY (col3, col4)
;

-- testAddConstraintKeyIssue320
ALTER TABLE table1 ADD CONSTRAINT table1_constraint_1 KEY (col1)
;

-- testAddConstraintKeyIssue320
ALTER TABLE table1 ADD CONSTRAINT table1_constraint_1 KEY (col1, col2)
;

-- testAddConstraintKeyIssue320
ALTER TABLE table1 ADD CONSTRAINT table1_constraint_1 KEY (col1, col2), ADD CONSTRAINT table1_constraint_2 KEY (col3, col4)
;

-- testAlterTableColumnCommentIssue984
ALTER TABLE texto_fichero MODIFY id COMMENT 'some comment'
;

-- testAlterOnUpdateRestrict
alter table mytab add constraint fk_mytab foreign key(col)references reftab(id)on update restrict
;

-- testAlterOnUpdateRestrict
alter table mytab add foreign key(col)references reftab(id)on update restrict
;

-- testAlterTablePrimaryKeyDeferrableDisableNoValidate
ALTER TABLE animals ADD PRIMARY KEY (id) DEFERRABLE DISABLE NOVALIDATE
;

-- testAlterTableForeignKeyIssue981
ALTER TABLE atconfigpro ADD CONSTRAINT atconfigpro_atconfignow_id_foreign FOREIGN KEY (atconfignow_id) REFERENCES atconfignow(id) ON DELETE CASCADE, ADD CONSTRAINT atconfigpro_attariff_id_foreign FOREIGN KEY (attariff_id) REFERENCES attariff(id) ON DELETE CASCADE
;

-- testIssue259
ALTER TABLE feature_v2 ADD COLUMN third_user_id int (10) unsigned DEFAULT '0' COMMENT '第三方用户id' after kdt_id
;

-- testIssue633
ALTER TABLE team_phases ADD CONSTRAINT team_phases_id_key UNIQUE (id)
;

-- testIssue679
ALTER TABLE tb_session_status ADD INDEX idx_user_id_name (user_id, user_name(10)), ADD INDEX idx_user_name (user_name)
;

-- testAlterTableAddColumnKeywordTypes
ALTER TABLE mytable ADD COLUMN col1 xml
;

-- testAlterTableAddColumnKeywordTypes
ALTER TABLE mytable ADD COLUMN col1 interval
;

-- testAlterTableAddColumnKeywordTypes
ALTER TABLE mytable ADD COLUMN col1 bit varying
;

-- testAlterOnDeleteSetDefault
alter table mytab add constraint fk_mytab foreign key(col)references reftab(id)on delete set default
;

-- testAlterOnDeleteSetDefault
alter table mytab add foreign key(col)references reftab(id)on delete set default
;

-- testAlterTableChangeColumnDropNotNull
alter table a modify column b drop not null
;

-- testAlterTableChangeColumnDropNotNull
alter table a modify(column b drop not null,column c drop not null)
;

-- testAlterTableDropConstraint
ALTER TABLE test DROP CONSTRAINT YYY
;

-- testAlterTableDropMultipleColumnsIfExists
ALTER TABLE test DROP COLUMN IF EXISTS name, DROP COLUMN IF EXISTS surname
;

-- testAlterTableForeignWithFkSchema
ALTER TABLE test ADD FOREIGN KEY (user_id) REFERENCES my_schema.ra_user (id) ON DELETE SET NULL
;

-- testAlterOnUpdateNoAction
alter table mytab add constraint fk_mytab foreign key(col)references reftab(id)on update no action
;

-- testAlterOnUpdateNoAction
alter table mytab add foreign key(col)references reftab(id)on update no action
;

-- testAlterOnUpdateSetDefault
alter table mytab add constraint fk_mytab foreign key(col)references reftab(id)on update set default
;

-- testAlterOnUpdateSetDefault
alter table mytab add foreign key(col)references reftab(id)on update set default
;

-- testAlterTableForgeignKey
ALTER TABLE test ADD FOREIGN KEY (user_id) REFERENCES ra_user (id) ON DELETE CASCADE
;

-- testAlterTableForeignKey2
ALTER TABLE test ADD FOREIGN KEY (user_id) REFERENCES ra_user (id)
;

-- testAlterTableForeignKey3
ALTER TABLE test ADD FOREIGN KEY (user_id) REFERENCES ra_user (id) ON DELETE RESTRICT
;

-- testAlterTableForeignKey4
ALTER TABLE test ADD FOREIGN KEY (user_id) REFERENCES ra_user (id) ON DELETE SET NULL
;

-- testAlterTableAlterColumn
ALTER TABLE table_name ALTER COLUMN column_name_1 TYPE TIMESTAMP, ALTER COLUMN column_name_2 TYPE BOOLEAN
;

-- testAlterTableFK
ALTER TABLE `Novels` ADD FOREIGN KEY (AuthorID) REFERENCES Author (ID)
;

-- testAlterTablePK
ALTER TABLE `Author` ADD CONSTRAINT `AuthorPK` PRIMARY KEY (`ID`)
;

-- testAlterTableDropColumn
ALTER TABLE test DROP COLUMN YYY
;

-- testAlterTableDefaultValueTrueIssue926
ALTER TABLE my_table ADD some_column BOOLEAN DEFAULT FALSE
;

-- testOnUpdateOnDeleteOrOnDeleteOnUpdate
alter table mytab add constraint fk_mytab foreign key(col)references reftab(id)on update cascade on delete set null
;

-- testOnUpdateOnDeleteOrOnDeleteOnUpdate
alter table mytab add constraint fk_mytab foreign key(col)references reftab(id)on update cascade on delete set null
;

-- testOnUpdateOnDeleteOrOnDeleteOnUpdate
alter table mytab add foreign key(col)references reftab(id)on update cascade on delete set null
;

-- testOnUpdateOnDeleteOrOnDeleteOnUpdate
alter table mytab add foreign key(col)references reftab(id)on update cascade on delete set null
;

