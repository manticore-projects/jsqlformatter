
##################
Changelog
##################

******************
Latest Changes
******************



**Fix the Build file** - Andreas Reichel 2022-08-18 12:30:41

  * Restore dependencies on INMEMANTLR
  * Task Website depends on JAR Task

**Improve the Changelog** - Andreas Reichel 2022-08-18 12:17:38

  * Write REST and get rid of the M2R2 extension
  * Fix the Gradle task dependencies (finally)

**Merge remote-tracking branch 'origin/master'** - Andreas Reichel 2022-08-18 11:46:29

  * # Conflicts:
  * #	build.gradle
  * #	src/site/sphinx/changelog.rst
  * #	src/site/sphinx/demo.rst
  * #	src/site/sphinx/install.rst

**Add AST Visualization** - Andreas Reichel 2022-08-18 11:34:52

  * Show the Statement&#x27;s Java Objects in a tree hierarchy

**Improve Documentation** - Andreas Reichel 2022-08-18 11:32:29

  * Use the Gradle GIT Changelog Plugin
  * Use the Sphinx C2R2 extension to read the CHANGELOG.mg
  * Fix the Gradle Task dependencies for Changelog, Sphinx and RailRoad Diagrams
  * Update GRAAL-VM dependency
  * Reformat the REST files and adjust some captions

**Update Documentation** - Andreas Reichel 2022-05-24 15:44:29


**Version Maintenance** - Andreas Reichel 2022-05-24 15:36:47

  * Depend on Upstream JSQLParser since all patches have been accepted
  * Explicitly state dependency version numbers for Maven compatibility
  * Fix some syntax hints
  * Add TOC to the RR XHTML file

**Updates** - Andreas Reichel 2022-04-02 03:48:07

  * Build: Update Gradle Download Plugin
  * Documentation: Update JSQLParser Snapshot reference

**Release 0.11.1** - Andreas Reichel 2022-03-06 08:15:34


**Bump version to 4.4-SNAPSHOT** - Andreas Reichel 2021-12-30 08:00:20


**Rework the Parsing Timeout** - Andreas Reichel 2021-12-11 15:18:30



******************
Version 0.1.11
******************


**Adopt JSQLParser 4.3-Snapshot Changes** - Andreas Reichel 2021-12-10 16:10:36

  * Timeout parser after 2 seconds without freezing the application

**Timeout too long-running queries** - Andreas Reichel 2021-12-10 14:17:28


**Fix spelling** - Andreas Reichel 2021-11-24 07:18:13


**Fix NOT LIKE Expression** - Andreas Reichel 2021-11-10 16:11:08


**Adding readme file** - Andreas Reichel 2021-11-09 04:53:59



******************
Version 0.1.10
******************


**Fix UPDATE with JOIN** - Andreas Reichel 2021-11-09 04:49:59

  * Fix UNION with WHERE LIMIT OFFSET

**LIMIT/OFFSET with Expressions** - Andreas Reichel 2021-10-19 07:25:59

  * Oracle Multi Column DROP

**fix the CI** - Andreas Reichel 2021-09-11 07:46:38


**fix the CI** - Andreas Reichel 2021-09-11 07:34:02


**Fix the Maven Build** - Andreas Reichel 2021-09-11 07:26:30

  * Run tests serially with the SURFIRE plugin

**Use only published dependencies** - Andreas Reichel 2021-09-11 07:03:17


**Update Documentation** - Andreas Reichel 2021-09-11 06:53:59


**reformat source code** - Andreas Reichel 2021-09-11 05:08:54


**JSQL Parser 4.2** - Andreas Reichel 2021-09-11 05:07:34

  * Fix Brackets around CREATE ... AS ( SELECT ... )
  * Fix Brackets around UNION
  * Fix Barckets around VALUE LISTS and RowConstructor
  * Implement SubJoins
  * Reformat the Unit Tests

**Run each test in its own instance** - Andreas Reichel 2021-09-11 02:20:34


**JSQLParser 4.2 Compatibility** - Andreas Reichel 2021-09-11 02:04:52

  * Update the JOIN ... ON ... formatting
  * Update the UPDATE ... SET ... formatting

**Improve the Gradle Build** - Andreas Reichel 2021-09-11 02:02:53


**Organize the Unit Tests** - Andreas Reichel 2021-09-11 02:02:33


**Gradle** - Andreas Reichel 2021-09-05 00:08:17

  * JSQL Parser 4.2


******************
Version 0.1.9
******************


**Prepare release 0.1.7** - Andreas Reichel 2021-05-18 03:44:24


**use a more complex sample based on MessageFormat** - Andreas Reichel 2021-05-18 03:27:13


**filter left over \n or \t** - Andreas Reichel 2021-05-18 03:18:09


**Implement toJavaString, toJavaStringBuilder and toJavaMessageFormat** - Andreas Reichel 2021-05-18 02:48:52


**FromItem not mandatory in H2/MySQL and friends, fixes issue #6** - Andreas Reichel 2021-05-18 01:08:46

  * Upper-/Lower-Case spelling of operators, fixes issue #5

**Implement MySQL Group_Concat(), fixes issue #4** - Andreas Reichel 2021-05-16 09:17:53



******************
Version 0.1.7-PRE
******************


**Do not throw an exception on empty statements with comments only, fixes issue #2** - Andreas Reichel 2021-05-15 12:34:04

  * Format LIMIT OFFSET properly, fixes issue #3

**Better WITH VALUES list support** - Andreas Reichel 2021-05-10 07:17:52

  * Insert From Java

**Add WITH statements with SelectItems and Value Expression List** - Andreas Reichel 2021-05-07 03:47:49


**Incorporate Nested WITHs based on Subqueries** - Andreas Reichel 2021-05-06 05:18:09

  * Develop interactive Demo

**re-format code** - Andreas Reichel 2021-05-04 00:15:23


**corrections** - Andreas Reichel 2021-05-01 09:42:34



******************
Version 0.1.6
******************


**Update documentation for 0.1.6** - Andreas Reichel 2021-05-01 09:13:58


**Fix CREATE TABLE with Separation=AFTER** - Andreas Reichel 2021-05-01 08:23:53


**Getter/Setter for the formatting options** - Andreas Reichel 2021-05-01 06:10:32


**get the AST** - Andreas Reichel 2021-05-01 05:54:30


**Avoid calling expensive List methods** - Andreas Reichel 2021-05-01 04:35:28


**Encapsulte the FormatterOptions into an Enum** - Andreas Reichel 2021-05-01 03:21:36


**Cleanup Sphinx documentation** - Andreas Reichel 2021-05-01 00:16:13


**Add explicit Formatting Option for squaredBracketQuotation** - Andreas Reichel 2021-05-01 00:03:28


**Correct MERGE INSERT order and remove whitespaces** - Andreas Reichel 2021-04-30 03:01:21


**fix spelling** - Andreas Reichel 2021-04-30 00:19:37


**fix functions with ALL_COLUMNS parameter** - Andreas Reichel 2021-04-30 00:13:51


**Merge origin/main into main** - Andreas Reichel 2021-04-29 13:17:03


**Finalize documentation** - Andreas Reichel 2021-04-29 13:16:06



******************
Version 0.1.5
******************


**Finalize documentation** - Andreas Reichel 2021-04-29 12:49:02


**Prepare Release 0.1.5** - Andreas Reichel 2021-04-29 12:14:49


**Small white space corrections** - Andreas Reichel 2021-04-29 12:00:45


**Implement Separation BEFORE/AFTER formatting option** - Andreas Reichel 2021-04-29 10:07:40


**Update Tests to reflect the formatting changes** - Andreas Reichel 2021-04-29 07:12:19


**Prepare code for Separation [BEFORE, AFTER] formatting** - Andreas Reichel 2021-04-29 05:46:31


**Add Spelling Options UPPER, LOWER, CAMEL, KEEP** - Andreas Reichel 2021-04-29 04:22:15


**fix the IN Expression** - Andreas Reichel 2021-04-29 01:21:02

  * improve Expression List formatting

**better handling of parameter lists** - Andreas Reichel 2021-04-28 04:09:09


**fix indentation of function parameters** - Andreas Reichel 2021-04-27 15:17:09


**remove unused variables** - Andreas Reichel 2021-04-27 10:04:47


**better way to split statements (ignoring comments and strings)** - Andreas Reichel 2021-04-27 09:52:59


**normalize Whitespace** - Andreas Reichel 2021-04-27 03:25:12


**Stacking right side comments** - Andreas Reichel 2021-04-27 03:24:51


**Improve the Comment formatting for multi-line comments** - Andreas Reichel 2021-04-26 14:37:03



******************
Version v0.1.4
******************


**Update the Readme for 0.1.4** - Andreas Reichel 2021-04-25 06:11:32



******************
Version 0.1.4
******************


**Improve the documentation** - Andreas Reichel 2021-04-25 05:36:57


**Preserve comments** - Andreas Reichel 2021-04-25 05:00:29

  * Support Bracket Quotation (MS SQL Server)

**Write some documentation** - Andreas Reichel 2021-04-22 07:06:53


**Add SPHINX documentation** - Andreas Reichel 2021-04-22 03:40:22

  * Add GitHub Pages deployment

**Add SPHINX documentation** - Andreas Reichel 2021-04-22 03:38:34

  * Add GitHub Pages deployment

**Update README.md** - manticore-projects 2021-04-19 07:08:38


**Update README.md** - manticore-projects 2021-04-19 07:05:52


**Update README.md** - manticore-projects 2021-04-19 07:03:14


**Update README.md** - manticore-projects 2021-04-19 07:02:16



******************
Version 0.1.3
******************


**Update README.md** - manticore-projects 2021-04-19 06:55:56


**Update README.md** - manticore-projects 2021-04-19 06:54:50


**Update README.md** - manticore-projects 2021-04-19 06:51:55


**Update README.md** - manticore-projects 2021-04-19 06:51:35


**Update README.md** - manticore-projects 2021-04-19 06:31:52


**Update POM** - Andreas Reichel 2021-04-19 06:10:47


**Merge branch 'main' of github.com:manticore-projects/jsqlformatter** - Andreas Reichel 2021-04-19 06:10:06


**Add ANSI formatted output** - Andreas Reichel 2021-04-19 06:06:56

  * Add some basic formatting options
  * Improve the general formatting
  * Build Native Image Binaries
  * Bump to 0.1.3

**Support some basic formatting options** - Andreas Reichel 2021-04-17 06:05:36


**Add suport for GraalVM Native Image** - Andreas Reichel 2021-04-16 02:25:38


**Update maven.yml** - manticore-projects 2021-04-12 01:26:36


**Update maven.yml** - manticore-projects 2021-04-12 01:24:30


**Create .coveralls.yml** - manticore-projects 2021-04-12 01:22:45


**Support MergeInsert WHERE clause** - Andreas Reichel 2021-04-12 00:20:33


**Reduce the size for the Ueber-JAR** - Andreas Reichel 2021-04-11 14:28:16



******************
Version 0.1.2
******************


**Update the README** - Andreas Reichel 2021-04-11 13:51:42


**Build Shaded JAR (Ueber JAR)** - Andreas Reichel 2021-04-11 13:35:22


**Support for CREATE TABLE, CREATE INDEX, CREATE VIEW** - Andreas Reichel 2021-04-11 12:01:14

  * Bump to 0.1.2

**Update Readme with Maven Info** - Andreas Reichel 2021-04-10 05:20:58


**Use SonaType plugins** - Andreas Reichel 2021-04-10 03:42:33


**Add MAVEN support** - Andreas Reichel 2021-04-10 02:45:17


**[maven-release-plugin] prepare for next development iteration** - Andreas Reichel 2021-04-10 02:29:16


**[maven-release-plugin] prepare release jsqlformatter-0.1.0** - Andreas Reichel 2021-04-10 02:29:15


**Add MAVEN support** - Andreas Reichel 2021-04-10 02:28:47


**Add MAVEN support** - Andreas Reichel 2021-04-10 02:11:56


**Create maven.yml** - manticore-projects 2021-04-10 01:43:57


**Add MAVEN support** - Andreas Reichel 2021-04-10 01:15:50


**Add MAVEN support** - Andreas Reichel 2021-04-10 01:14:38


**encapsulate some the statements** - Andreas Reichel 2021-04-09 05:25:08

  * make the methods private
  * implement some basic Java Documentation

**remove unused dependencies** - Andreas Reichel 2021-04-09 04:54:09

  * adopt package structure com.manticore.*
  * correct the unit tests

**Update README.md** - manticore-projects 2021-04-09 04:03:46


**First working Version** - Andreas Reichel 2021-04-09 03:39:26

  * Supporting complex SELECT, INSERT, UPDATE, MERGE statements

**Initial commit** - manticore-projects 2021-04-09 03:10:31



