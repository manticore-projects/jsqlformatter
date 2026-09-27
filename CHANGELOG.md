# jsqlformatter changelog

Changelog of jsqlformatter.

## 5.4.2 (2026-09-27)

### Other changes


## 5.4.1 (2026-09-06)

### Bug Fixes

-  **formatter**  render IntervalQualifier so standard interval units survive ([5ea06](https://github.com/manticore-projects/jsqlformatter/commit/5ea068f7b409b54) Andreas Reichel)  
-  **build**  restore Maven Central publishing and pin plugin versions ([6ed7f](https://github.com/manticore-projects/jsqlformatter/commit/6ed7fe58185dd0e) Andreas Reichel)  

## 5.4 (2026-07-31)

### Features

-  Adopt the Visitor pattern ([c0095](https://github.com/manticore-projects/jsqlformatter/commit/c0095cf248bbae5) Andreas Reichel)  

### Bug Fixes

-  Adopt changes of JSQParser-5.3.167 ([13e28](https://github.com/manticore-projects/jsqlformatter/commit/13e2873303eeb20) manticore-projects)  
-  avoid double `Alias` on `EqualsTo` ([d9d1c](https://github.com/manticore-projects/jsqlformatter/commit/d9d1c68834026bf) Andreas Reichel)  

### Other changes


## 5.3 (2025-06-30)

### Features

-  Table Functions ([e073f](https://github.com/manticore-projects/jsqlformatter/commit/e073f268a2aa354) manticore-projects)  
-  Table Functions ([66195](https://github.com/manticore-projects/jsqlformatter/commit/66195c0da09dd96) manticore-projects)  
-  format `QUALIFY` clause ([1686c](https://github.com/manticore-projects/jsqlformatter/commit/1686cf8972cc8c5) manticore-projects)  
-  adopt JSQLParser 5.4 based on JavaCC 8 ([f4654](https://github.com/manticore-projects/jsqlformatter/commit/f4654944c3a02ea) manticore-projects)  
-  support `Pivot` and `UnPivot` ([57a84](https://github.com/manticore-projects/jsqlformatter/commit/57a842dacfe9aff) Andreas Reichel)  
-  support `Pivot` and `UnPivot` ([e6967](https://github.com/manticore-projects/jsqlformatter/commit/e6967e3f5669aeb) Andreas Reichel)  

### Bug Fixes

-  redundant comma before implicit cast expressions ([93193](https://github.com/manticore-projects/jsqlformatter/commit/93193bbab1d06ea) manticore-projects)  
-  support `EXPLAIN` and `SUMMARIZE` statements ([f47e7](https://github.com/manticore-projects/jsqlformatter/commit/f47e79b70727f2d) manticore-projects)  

### Other changes

**Update README.md**

* fix error util class , it should be JSQLFormatter instead of JSqlFormatter 

[76947](https://github.com/manticore-projects/jsqlformatter/commit/76947e4714544ed) wgzhao *2024-10-18 15:03:33*

**Update dependabot.yml**


[48377](https://github.com/manticore-projects/jsqlformatter/commit/4837787c5499fbd) manticore-projects *2024-08-24 18:45:58*

**dependabot.yml**


[0cc95](https://github.com/manticore-projects/jsqlformatter/commit/0cc95c826662f19) manticore-projects *2024-08-24 18:41:38*

**update gradle wrapper**


[080d3](https://github.com/manticore-projects/jsqlformatter/commit/080d32db7a8e192) Andreas Reichel *2024-07-14 06:48:22*

**Release 5.0**


[e62b9](https://github.com/manticore-projects/jsqlformatter/commit/e62b9f02c36f37c) Andreas Reichel *2024-07-01 00:23:49*


## 5.0 (2024-06-27)

### Features

-  define or suppress the statement terminator ([f9a6a](https://github.com/manticore-projects/jsqlformatter/commit/f9a6a7a24953892) Andreas Reichel)  
-  expose FormatOption Name ([d1869](https://github.com/manticore-projects/jsqlformatter/commit/d1869b9b2fbda3d) Andreas Reichel)  
-  expose ANSI Format constants ([2b552](https://github.com/manticore-projects/jsqlformatter/commit/2b552dc734f8510) Andreas Reichel)  
-  split-off the CLI ([b8dfc](https://github.com/manticore-projects/jsqlformatter/commit/b8dfc21c61d2b02) Andreas Reichel)  

### Bug Fixes

-  DuckDB specific `SELECT * EXCLUDE(...)` ([9c7d6](https://github.com/manticore-projects/jsqlformatter/commit/9c7d661607a21ac) Andreas Reichel)  

### Other changes


## 4.9 (2024-04-09)

### Features

-  Support Aggregate Functions (wip) ([b2ffd](https://github.com/manticore-projects/jsqlformatter/commit/b2ffd2b0bb53c91) Andreas Reichel)  
-  Support WINDOW and Implicit Cast ([88123](https://github.com/manticore-projects/jsqlformatter/commit/881231ec74ed4e2) Andreas Reichel)  
-  support `CASE SWITCH` and `ARRAY[..]` ([37622](https://github.com/manticore-projects/jsqlformatter/commit/376224c87d00923) Andreas Reichel)  

### Bug Fixes

-  `Top` clause ([1c76f](https://github.com/manticore-projects/jsqlformatter/commit/1c76f94033498ab) Andreas Reichel)  
-  `Cast`, `ALTER TABLE ... RENAME TO` ([4a49e](https://github.com/manticore-projects/jsqlformatter/commit/4a49e1810ef8e5b) Andreas Reichel)  

### Other changes


## 4.8 (2023-12-30)

### Features

-  MS SQL Server `Merge` `Output` clause ([d5215](https://github.com/manticore-projects/jsqlformatter/commit/d52159c8b57a673) Andreas Reichel)  
-  Clickhouse `GLOBAL IN ...` ([7a11e](https://github.com/manticore-projects/jsqlformatter/commit/7a11ec9489c08c1) Andreas Reichel)  
-  `CREATE INDEX IF NOT EXISTS...` ([dde4c](https://github.com/manticore-projects/jsqlformatter/commit/dde4c6dfaa9b832) Andreas Reichel)  
-  BigQuery Except(..) Replace(..) syntax ([de2fa](https://github.com/manticore-projects/jsqlformatter/commit/de2faf795e4c2e7) Andreas Reichel)  
-  support the Back Slashi Quoting option ([0e773](https://github.com/manticore-projects/jsqlformatter/commit/0e7730d17d47be7) Andreas Reichel)  
-  trim base package name ([5e192](https://github.com/manticore-projects/jsqlformatter/commit/5e1926cb2d09018) Andreas Reichel)  
-  JSQLParser 5.0 Compatibility ([ed723](https://github.com/manticore-projects/jsqlformatter/commit/ed7239e30e2da37) Andreas Reichel)  

### Bug Fixes

-  `CAST` and `AllTableColumns` Expressions ([4e903](https://github.com/manticore-projects/jsqlformatter/commit/4e90365f358964f) Andreas Reichel)  
-  support `FETCH` clause ([b0dd2](https://github.com/manticore-projects/jsqlformatter/commit/b0dd260045adf76) Andreas Reichel)  
-  Postgres NOTNULL expression ([3fc96](https://github.com/manticore-projects/jsqlformatter/commit/3fc965e5ecace3f) Andreas Reichel)  
-  Interval expression ([82874](https://github.com/manticore-projects/jsqlformatter/commit/82874ad6293ea9e) Andreas Reichel)  

### Other changes

**Create gradle.yml**


[294b8](https://github.com/manticore-projects/jsqlformatter/commit/294b80f62aaef25) manticore-projects *2023-06-01 03:39:39*


## 4.6.0 (2023-05-19)

### Features

-  integrated RR Diagrams ([94e6b](https://github.com/manticore-projects/jsqlformatter/commit/94e6bb2e2854754) Andreas Reichel)  
-  add line count to output ([92b1e](https://github.com/manticore-projects/jsqlformatter/commit/92b1e1957c50ade) Andreas Reichel)  
-  add line count to output ([fb218](https://github.com/manticore-projects/jsqlformatter/commit/fb2183103fc5110) Andreas Reichel)  
-  query Statement Objects using XPath ([731af](https://github.com/manticore-projects/jsqlformatter/commit/731afccb9759ef6) Andreas Reichel)  
-  Format to XML Object Tree ([6ce2c](https://github.com/manticore-projects/jsqlformatter/commit/6ce2c78e32f4667) Andreas Reichel)  

### Bug Fixes

-  Support ESCAPE clause of LIKE expression ([5099b](https://github.com/manticore-projects/jsqlformatter/commit/5099bfffade1dfe) Andreas Reichel)  
-  Support ESCAPE clause of LIKE expression ([45982](https://github.com/manticore-projects/jsqlformatter/commit/459823812d016d7) Andreas Reichel)  

### Other changes


## 1.0.1 (2022-09-09)

### Other changes

**Update README.md**


[8263e](https://github.com/manticore-projects/jsqlformatter/commit/8263e8714f2b124) manticore-projects *2022-09-06 20:50:10*

**Update README.md**


[b0448](https://github.com/manticore-projects/jsqlformatter/commit/b044822df556c58) manticore-projects *2022-09-06 16:07:40*

**Update Documentation**


[e1a06](https://github.com/manticore-projects/jsqlformatter/commit/e1a0626a593b5c1) Andreas Reichel *2022-05-24 16:29:03*

**Update Documentation**


[70885](https://github.com/manticore-projects/jsqlformatter/commit/7088508eb8cc18e) Andreas Reichel *2022-05-24 16:26:15*

**Switch to GitHub CodeQL**


[516af](https://github.com/manticore-projects/jsqlformatter/commit/516af95a9ced729) Andreas Reichel *2022-05-24 16:14:17*

**Create codeql-analysis.yml**


[b8dbf](https://github.com/manticore-projects/jsqlformatter/commit/b8dbf5cfdb2165f) manticore-projects *2022-05-17 14:14:17*


## 1.0.0 (2022-09-06)

### Features

-  AST Visualisation ([c2a0f](https://github.com/manticore-projects/jsqlformatter/commit/c2a0f7adf63be8c) Andreas Reichel)  

### Bug Fixes

-  format Old Oracle JOINs `(+)` ([f2909](https://github.com/manticore-projects/jsqlformatter/commit/f2909b209586917) Andreas Reichel)  

### Other changes

**Sphinx Documentation**

* Re-arrange the TOC 
* Commit changelog 

[0a623](https://github.com/manticore-projects/jsqlformatter/commit/0a623fe1811cf47) Andreas Reichel *2022-08-18 13:03:41*

**Fix the Build file**

* Restore dependencies on INMEMANTLR 
* Task Website depends on JAR Task 

[ff154](https://github.com/manticore-projects/jsqlformatter/commit/ff15487be7201d3) Andreas Reichel *2022-08-18 12:30:41*

**Improve the Changelog**

* Write REST and get rid of the M2R2 extension 
* Fix the Gradle task dependencies (finally) 

[9a8fb](https://github.com/manticore-projects/jsqlformatter/commit/9a8fb92fb46de63) Andreas Reichel *2022-08-18 12:17:38*

**Add AST Visualization**

* Show the Statement&#x27;s Java Objects in a tree hierarchy 

[fea8e](https://github.com/manticore-projects/jsqlformatter/commit/fea8e85e2d29db7) Andreas Reichel *2022-08-18 11:34:52*

**Improve Documentation**

* Use the Gradle GIT Changelog Plugin 
* Use the Sphinx C2R2 extension to read the CHANGELOG.mg 
* Fix the Gradle Task dependencies for Changelog, Sphinx and RailRoad Diagrams 
* Update GRAAL-VM dependency 
* Reformat the REST files and adjust some captions 

[3b5bc](https://github.com/manticore-projects/jsqlformatter/commit/3b5bc999e6266c6) Andreas Reichel *2022-08-18 11:32:29*

**Update Documentation**


[d270b](https://github.com/manticore-projects/jsqlformatter/commit/d270bb40ed2473e) Andreas Reichel *2022-05-24 15:44:29*

**Version Maintenance**

* Depend on Upstream JSQLParser since all patches have been accepted 
* Explicitly state dependency version numbers for Maven compatibility 
* Fix some syntax hints 
* Add TOC to the RR XHTML file 

[92505](https://github.com/manticore-projects/jsqlformatter/commit/92505d8c85251b9) Andreas Reichel *2022-05-24 15:36:47*

**Updates**

* Build: Update Gradle Download Plugin 
* Documentation: Update JSQLParser Snapshot reference 

[f73e5](https://github.com/manticore-projects/jsqlformatter/commit/f73e5700ba92936) Andreas Reichel *2022-04-02 03:48:07*

**Release 0.11.1**


[77ad6](https://github.com/manticore-projects/jsqlformatter/commit/77ad6497ed16d67) Andreas Reichel *2022-03-06 08:15:34*

**Bump version to 4.4-SNAPSHOT**


[08906](https://github.com/manticore-projects/jsqlformatter/commit/08906d8c336e214) Andreas Reichel *2021-12-30 08:00:20*

**Rework the Parsing Timeout**


[2acfe](https://github.com/manticore-projects/jsqlformatter/commit/2acfe7ac7050528) Andreas Reichel *2021-12-11 15:18:30*


## 0.1.11 (2021-12-10)

### Other changes

**Adopt JSQLParser 4.3-Snapshot Changes**

* Timeout parser after 2 seconds without freezing the application 

[7d04b](https://github.com/manticore-projects/jsqlformatter/commit/7d04bc873ada0ab) Andreas Reichel *2021-12-10 16:10:36*

**Timeout too long-running queries**


[6a071](https://github.com/manticore-projects/jsqlformatter/commit/6a071c501123b42) Andreas Reichel *2021-12-10 14:17:28*

**Fix spelling**


[c54d1](https://github.com/manticore-projects/jsqlformatter/commit/c54d17374abf220) Andreas Reichel *2021-11-24 07:18:13*

**Fix NOT LIKE Expression**


[9514c](https://github.com/manticore-projects/jsqlformatter/commit/9514cf32b1d45c1) Andreas Reichel *2021-11-10 16:11:08*

**Adding readme file**


[466df](https://github.com/manticore-projects/jsqlformatter/commit/466df3b0ea24b69) Andreas Reichel *2021-11-09 04:53:59*


## 0.1.10 (2021-11-09)

### Other changes

**Fix UPDATE with JOIN**

* Fix UNION with WHERE LIMIT OFFSET 

[9e85e](https://github.com/manticore-projects/jsqlformatter/commit/9e85eaca742fb6a) Andreas Reichel *2021-11-09 04:49:59*

**LIMIT/OFFSET with Expressions**

* Oracle Multi Column DROP 

[1da32](https://github.com/manticore-projects/jsqlformatter/commit/1da32a7e9fd7fd2) Andreas Reichel *2021-10-19 07:25:59*

**fix the CI**


[f3695](https://github.com/manticore-projects/jsqlformatter/commit/f3695861f71ecf1) Andreas Reichel *2021-09-11 07:46:38*

**fix the CI**


[60711](https://github.com/manticore-projects/jsqlformatter/commit/60711a2274c7767) Andreas Reichel *2021-09-11 07:34:02*

**Fix the Maven Build**

* Run tests serially with the SURFIRE plugin 

[5c0e5](https://github.com/manticore-projects/jsqlformatter/commit/5c0e55af0d47103) Andreas Reichel *2021-09-11 07:26:30*

**Use only published dependencies**


[63819](https://github.com/manticore-projects/jsqlformatter/commit/6381944eed35daf) Andreas Reichel *2021-09-11 07:03:17*

**Update Documentation**


[830e5](https://github.com/manticore-projects/jsqlformatter/commit/830e5f6ca26ea89) Andreas Reichel *2021-09-11 06:53:59*

**reformat source code**


[fed95](https://github.com/manticore-projects/jsqlformatter/commit/fed95599bbcf3f5) Andreas Reichel *2021-09-11 05:08:54*

**JSQL Parser 4.2**

* Fix Brackets around CREATE ... AS ( SELECT ... ) 
* Fix Brackets around UNION 
* Fix Barckets around VALUE LISTS and RowConstructor 
* Implement SubJoins 
* Reformat the Unit Tests 

[7119a](https://github.com/manticore-projects/jsqlformatter/commit/7119adf5e12d5fe) Andreas Reichel *2021-09-11 05:07:34*

**Run each test in its own instance**


[77823](https://github.com/manticore-projects/jsqlformatter/commit/7782322f1fabb17) Andreas Reichel *2021-09-11 02:20:34*

**JSQLParser 4.2 Compatibility**

* Update the JOIN ... ON ... formatting 
* Update the UPDATE ... SET ... formatting 

[7e882](https://github.com/manticore-projects/jsqlformatter/commit/7e882bd3cdb355f) Andreas Reichel *2021-09-11 02:04:52*

**Improve the Gradle Build**


[9db5c](https://github.com/manticore-projects/jsqlformatter/commit/9db5cc6db8111ee) Andreas Reichel *2021-09-11 02:02:53*

**Organize the Unit Tests**


[bde6e](https://github.com/manticore-projects/jsqlformatter/commit/bde6e7586fbf795) Andreas Reichel *2021-09-11 02:02:33*

**Gradle**

* JSQL Parser 4.2 

[52e4c](https://github.com/manticore-projects/jsqlformatter/commit/52e4c89e3c1ba91) Andreas Reichel *2021-09-05 00:08:17*


## 0.1.9 (2021-05-18)

### Other changes

**Prepare release 0.1.7**


[4f044](https://github.com/manticore-projects/jsqlformatter/commit/4f044d070a376d4) Andreas Reichel *2021-05-18 03:44:24*

**use a more complex sample based on MessageFormat**


[c9a4c](https://github.com/manticore-projects/jsqlformatter/commit/c9a4c014a7b581b) Andreas Reichel *2021-05-18 03:27:13*

**filter left over \n or \t**


[9d27b](https://github.com/manticore-projects/jsqlformatter/commit/9d27b65ac4e0367) Andreas Reichel *2021-05-18 03:18:09*

**Implement toJavaString, toJavaStringBuilder and toJavaMessageFormat**


[36420](https://github.com/manticore-projects/jsqlformatter/commit/36420490c057e1e) Andreas Reichel *2021-05-18 02:48:52*

**FromItem not mandatory in H2/MySQL and friends, fixes issue #6**

* Upper-/Lower-Case spelling of operators, fixes issue #5 

[b4c44](https://github.com/manticore-projects/jsqlformatter/commit/b4c449a696c8be4) Andreas Reichel *2021-05-18 01:08:46*

**Implement MySQL Group_Concat(), fixes issue #4**


[b46d4](https://github.com/manticore-projects/jsqlformatter/commit/b46d4b072f07c64) Andreas Reichel *2021-05-16 09:17:53*


## 0.1.7-PRE (2021-05-15)

### Other changes

**Do not throw an exception on empty statements with comments only, fixes issue #2**

* Format LIMIT OFFSET properly, fixes issue #3 

[8639f](https://github.com/manticore-projects/jsqlformatter/commit/8639f8c41042ee0) Andreas Reichel *2021-05-15 12:34:04*

**Better WITH VALUES list support**

* Insert From Java 

[0c7d8](https://github.com/manticore-projects/jsqlformatter/commit/0c7d878acf69c42) Andreas Reichel *2021-05-10 07:17:52*

**Add WITH statements with SelectItems and Value Expression List**


[2725c](https://github.com/manticore-projects/jsqlformatter/commit/2725c61dd02e9c3) Andreas Reichel *2021-05-07 03:47:49*

**Incorporate Nested WITHs based on Subqueries**

* Develop interactive Demo 

[ebfa4](https://github.com/manticore-projects/jsqlformatter/commit/ebfa4eefb635717) Andreas Reichel *2021-05-06 05:18:09*

**re-format code**


[795ef](https://github.com/manticore-projects/jsqlformatter/commit/795efd5363d7c35) Andreas Reichel *2021-05-04 00:15:23*

**corrections**


[f787d](https://github.com/manticore-projects/jsqlformatter/commit/f787d06f9c5d7f9) Andreas Reichel *2021-05-01 09:42:34*


## 0.1.6 (2021-05-01)

### Other changes

**Update documentation for 0.1.6**


[73b4b](https://github.com/manticore-projects/jsqlformatter/commit/73b4bebe77820ba) Andreas Reichel *2021-05-01 09:13:58*

**Fix CREATE TABLE with Separation=AFTER**


[63700](https://github.com/manticore-projects/jsqlformatter/commit/63700855beac2a7) Andreas Reichel *2021-05-01 08:23:53*

**Getter/Setter for the formatting options**


[e5803](https://github.com/manticore-projects/jsqlformatter/commit/e58030ab2cdd5b9) Andreas Reichel *2021-05-01 06:10:32*

**get the AST**


[bf357](https://github.com/manticore-projects/jsqlformatter/commit/bf35723648c2a6c) Andreas Reichel *2021-05-01 05:54:30*

**Avoid calling expensive List methods**


[f90af](https://github.com/manticore-projects/jsqlformatter/commit/f90afb0b38c8c2c) Andreas Reichel *2021-05-01 04:35:28*

**Encapsulte the FormatterOptions into an Enum**


[30106](https://github.com/manticore-projects/jsqlformatter/commit/301066447c0cd15) Andreas Reichel *2021-05-01 03:21:36*

**Cleanup Sphinx documentation**


[2d4e4](https://github.com/manticore-projects/jsqlformatter/commit/2d4e4c832c70374) Andreas Reichel *2021-05-01 00:16:13*

**Add explicit Formatting Option for squaredBracketQuotation**


[a9c3d](https://github.com/manticore-projects/jsqlformatter/commit/a9c3d60f8c7cf33) Andreas Reichel *2021-05-01 00:03:28*

**Correct MERGE INSERT order and remove whitespaces**


[0f5ad](https://github.com/manticore-projects/jsqlformatter/commit/0f5ad32aa09e4fb) Andreas Reichel *2021-04-30 03:01:21*

**fix spelling**


[554dc](https://github.com/manticore-projects/jsqlformatter/commit/554dc1ccb67270c) Andreas Reichel *2021-04-30 00:19:37*

**fix functions with ALL_COLUMNS parameter**


[cb47a](https://github.com/manticore-projects/jsqlformatter/commit/cb47aa9e113702d) Andreas Reichel *2021-04-30 00:13:51*

**Finalize documentation**


[b2ca7](https://github.com/manticore-projects/jsqlformatter/commit/b2ca71d777d0c76) Andreas Reichel *2021-04-29 13:16:06*


## 0.1.5 (2021-04-29)

### Other changes

**Finalize documentation**


[52059](https://github.com/manticore-projects/jsqlformatter/commit/5205928dd4ce139) Andreas Reichel *2021-04-29 12:49:02*

**Prepare Release 0.1.5**


[69fc2](https://github.com/manticore-projects/jsqlformatter/commit/69fc2721faf85c1) Andreas Reichel *2021-04-29 12:14:49*

**Small white space corrections**


[1d46f](https://github.com/manticore-projects/jsqlformatter/commit/1d46f6683a7c77d) Andreas Reichel *2021-04-29 12:00:45*

**Implement Separation BEFORE/AFTER formatting option**


[fc9b1](https://github.com/manticore-projects/jsqlformatter/commit/fc9b136f19fb4a3) Andreas Reichel *2021-04-29 10:07:40*

**Update Tests to reflect the formatting changes**


[bc7aa](https://github.com/manticore-projects/jsqlformatter/commit/bc7aa5c30fb6de6) Andreas Reichel *2021-04-29 07:12:19*

**Prepare code for Separation [BEFORE, AFTER] formatting**


[2b828](https://github.com/manticore-projects/jsqlformatter/commit/2b82837558a78c0) Andreas Reichel *2021-04-29 05:46:31*

**Add Spelling Options UPPER, LOWER, CAMEL, KEEP**


[e8ef9](https://github.com/manticore-projects/jsqlformatter/commit/e8ef9b97fcd797e) Andreas Reichel *2021-04-29 04:22:15*

**fix the IN Expression**

* improve Expression List formatting 

[27ee5](https://github.com/manticore-projects/jsqlformatter/commit/27ee5ba2fb9d5f2) Andreas Reichel *2021-04-29 01:21:02*

**better handling of parameter lists**


[dc642](https://github.com/manticore-projects/jsqlformatter/commit/dc6427424e6db53) Andreas Reichel *2021-04-28 04:09:09*

**fix indentation of function parameters**


[289ec](https://github.com/manticore-projects/jsqlformatter/commit/289ec167ebd4aa3) Andreas Reichel *2021-04-27 15:17:09*

**remove unused variables**


[9e9ec](https://github.com/manticore-projects/jsqlformatter/commit/9e9eccd8995c2b0) Andreas Reichel *2021-04-27 10:04:47*

**better way to split statements (ignoring comments and strings)**


[d156c](https://github.com/manticore-projects/jsqlformatter/commit/d156c63df33b4e3) Andreas Reichel *2021-04-27 09:52:59*

**normalize Whitespace**


[91056](https://github.com/manticore-projects/jsqlformatter/commit/910569fc9b474b9) Andreas Reichel *2021-04-27 03:25:12*

**Stacking right side comments**


[2e467](https://github.com/manticore-projects/jsqlformatter/commit/2e467453119b305) Andreas Reichel *2021-04-27 03:24:51*

**Improve the Comment formatting for multi-line comments**


[7f1b0](https://github.com/manticore-projects/jsqlformatter/commit/7f1b0bb139de39a) Andreas Reichel *2021-04-26 14:37:03*


## v0.1.4 (2021-04-25)

### Other changes

**Update the Readme for 0.1.4**


[94236](https://github.com/manticore-projects/jsqlformatter/commit/942366e9e0c2768) Andreas Reichel *2021-04-25 06:11:32*


## 0.1.4 (2021-04-25)

### Other changes

**Improve the documentation**


[99f3f](https://github.com/manticore-projects/jsqlformatter/commit/99f3fe8f9692b5a) Andreas Reichel *2021-04-25 05:36:57*

**Preserve comments**

* Support Bracket Quotation (MS SQL Server) 

[6ec4b](https://github.com/manticore-projects/jsqlformatter/commit/6ec4b7b1a77d11b) Andreas Reichel *2021-04-25 05:00:29*

**Write some documentation**


[5a29d](https://github.com/manticore-projects/jsqlformatter/commit/5a29dd43e90d041) Andreas Reichel *2021-04-22 07:06:53*

**Add SPHINX documentation**

* Add GitHub Pages deployment 

[a1aeb](https://github.com/manticore-projects/jsqlformatter/commit/a1aebbbd4c6a264) Andreas Reichel *2021-04-22 03:40:22*

**Add SPHINX documentation**

* Add GitHub Pages deployment 

[ea72f](https://github.com/manticore-projects/jsqlformatter/commit/ea72f20ffc553c0) Andreas Reichel *2021-04-22 03:38:34*

**Update README.md**


[4be13](https://github.com/manticore-projects/jsqlformatter/commit/4be1366eaa4009e) manticore-projects *2021-04-19 07:08:38*

**Update README.md**


[110b4](https://github.com/manticore-projects/jsqlformatter/commit/110b436d8d2cfb8) manticore-projects *2021-04-19 07:05:52*

**Update README.md**


[823a6](https://github.com/manticore-projects/jsqlformatter/commit/823a6ae8c7ffefc) manticore-projects *2021-04-19 07:03:14*

**Update README.md**


[428f1](https://github.com/manticore-projects/jsqlformatter/commit/428f175ad868573) manticore-projects *2021-04-19 07:02:16*


## 0.1.3 (2021-04-19)

### Other changes

**Update README.md**


[0b833](https://github.com/manticore-projects/jsqlformatter/commit/0b8332e63fa5c4d) manticore-projects *2021-04-19 06:55:56*

**Update README.md**


[c97f6](https://github.com/manticore-projects/jsqlformatter/commit/c97f61597b941a8) manticore-projects *2021-04-19 06:54:50*

**Update README.md**


[20f00](https://github.com/manticore-projects/jsqlformatter/commit/20f003042de6272) manticore-projects *2021-04-19 06:51:55*

**Update README.md**


[0984e](https://github.com/manticore-projects/jsqlformatter/commit/0984e2a1ee17383) manticore-projects *2021-04-19 06:51:35*

**Update README.md**


[7ad87](https://github.com/manticore-projects/jsqlformatter/commit/7ad876700223145) manticore-projects *2021-04-19 06:31:52*

**Update POM**


[afead](https://github.com/manticore-projects/jsqlformatter/commit/afeadb61b43054b) Andreas Reichel *2021-04-19 06:10:47*

**Add ANSI formatted output**

* Add some basic formatting options 
* Improve the general formatting 
* Build Native Image Binaries 
* Bump to 0.1.3 

[01773](https://github.com/manticore-projects/jsqlformatter/commit/017736f40fb254f) Andreas Reichel *2021-04-19 06:06:56*

**Support some basic formatting options**


[236c2](https://github.com/manticore-projects/jsqlformatter/commit/236c265b9ad6f87) Andreas Reichel *2021-04-17 06:05:36*

**Add suport for GraalVM Native Image**


[203af](https://github.com/manticore-projects/jsqlformatter/commit/203af83ed69afad) Andreas Reichel *2021-04-16 02:25:38*

**Update maven.yml**


[b6a12](https://github.com/manticore-projects/jsqlformatter/commit/b6a1297267b1a73) manticore-projects *2021-04-12 01:26:36*

**Update maven.yml**


[45d5a](https://github.com/manticore-projects/jsqlformatter/commit/45d5a0cf7f57441) manticore-projects *2021-04-12 01:24:30*

**Create .coveralls.yml**


[36b24](https://github.com/manticore-projects/jsqlformatter/commit/36b2445654c3609) manticore-projects *2021-04-12 01:22:45*

**Support MergeInsert WHERE clause**


[7e41d](https://github.com/manticore-projects/jsqlformatter/commit/7e41d7b08eec36a) Andreas Reichel *2021-04-12 00:20:33*

**Reduce the size for the Ueber-JAR**


[f67b6](https://github.com/manticore-projects/jsqlformatter/commit/f67b6d4535ff2c6) Andreas Reichel *2021-04-11 14:28:16*


## 0.1.2 (2021-04-11)

### Other changes

**Update the README**


[c25fe](https://github.com/manticore-projects/jsqlformatter/commit/c25feb99b589910) Andreas Reichel *2021-04-11 13:51:42*

**Build Shaded JAR (Ueber JAR)**


[44af4](https://github.com/manticore-projects/jsqlformatter/commit/44af4397332b531) Andreas Reichel *2021-04-11 13:35:22*

**Support for CREATE TABLE, CREATE INDEX, CREATE VIEW**

* Bump to 0.1.2 

[81b16](https://github.com/manticore-projects/jsqlformatter/commit/81b167e6925475f) Andreas Reichel *2021-04-11 12:01:14*

**Update Readme with Maven Info**


[17819](https://github.com/manticore-projects/jsqlformatter/commit/178192fb15a2f17) Andreas Reichel *2021-04-10 05:20:58*

**Use SonaType plugins**


[45a16](https://github.com/manticore-projects/jsqlformatter/commit/45a16d92292090f) Andreas Reichel *2021-04-10 03:42:33*

**Add MAVEN support**


[ae5cd](https://github.com/manticore-projects/jsqlformatter/commit/ae5cd9d3d0339e2) Andreas Reichel *2021-04-10 02:45:17*

**Add MAVEN support**


[ea71e](https://github.com/manticore-projects/jsqlformatter/commit/ea71e3ab65e734c) Andreas Reichel *2021-04-10 02:28:47*

**Add MAVEN support**


[c3f5d](https://github.com/manticore-projects/jsqlformatter/commit/c3f5dbe608abf09) Andreas Reichel *2021-04-10 02:11:56*

**Create maven.yml**


[9f090](https://github.com/manticore-projects/jsqlformatter/commit/9f090701cd2a9fd) manticore-projects *2021-04-10 01:43:57*

**Add MAVEN support**


[b58c8](https://github.com/manticore-projects/jsqlformatter/commit/b58c832340a0935) Andreas Reichel *2021-04-10 01:15:50*

**Add MAVEN support**


[b17ff](https://github.com/manticore-projects/jsqlformatter/commit/b17ff242e7b32a3) Andreas Reichel *2021-04-10 01:14:38*

**encapsulate some the statements**

* make the methods private 
* implement some basic Java Documentation 

[b8167](https://github.com/manticore-projects/jsqlformatter/commit/b81674ba4c382a6) Andreas Reichel *2021-04-09 05:25:08*

**remove unused dependencies**

* adopt package structure com.manticore.* 
* correct the unit tests 

[c7d75](https://github.com/manticore-projects/jsqlformatter/commit/c7d759041504c13) Andreas Reichel *2021-04-09 04:54:09*

**Update README.md**


[d82c5](https://github.com/manticore-projects/jsqlformatter/commit/d82c5674afed954) manticore-projects *2021-04-09 04:03:46*

**First working Version**

* Supporting complex SELECT, INSERT, UPDATE, MERGE statements 

[b989e](https://github.com/manticore-projects/jsqlformatter/commit/b989e307761c24c) Andreas Reichel *2021-04-09 03:39:26*

**Initial commit**


[a30af](https://github.com/manticore-projects/jsqlformatter/commit/a30af09d141895b) manticore-projects *2021-04-09 03:10:31*


