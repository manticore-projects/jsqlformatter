.. meta::
   :description: Java library and CLI for formatting, beautifying and pretty printing SQL statements
   :keywords: java sql statement format formatter beautifier pretty printing jsqlparser

***************************
Java SQL Formatting Library
***************************

.. image:: https://github.com/manticore-projects/jsqlformatter/actions/workflows/gradle.yml/badge.svg
    :alt: Gradle CI
    :target: https://github.com/manticore-projects/jsqlformatter/actions/workflows/gradle.yml

.. image:: https://badgen.net/maven/v/maven-central/com.manticore-projects.jsqlformatter/jsqlformatter
    :alt: Maven Central
    :target: https://mvnrepository.com/artifact/com.manticore-projects.jsqlformatter/jsqlformatter

.. image:: https://app.codacy.com/project/badge/Grade/80374649d914462ebd6e5b160a1ebdbb
    :alt: Codacy Badge
    :target: https://app.codacy.com/gh/manticore-projects/jsqlformatter/dashboard

.. image:: https://coveralls.io/repos/github/manticore-projects/jsqlformatter/badge.svg
    :alt: Coverage Status
    :target: https://coveralls.io/github/manticore-projects/jsqlformatter

.. image:: https://img.shields.io/badge/License-Apache%202.0-030146
    :alt: License Apache 2.0
    :target: https://www.apache.org/licenses/LICENSE-2.0

.. image:: https://img.shields.io/github/issues/manticore-projects/jsqlformatter
    :alt: Issues
    :target: https://github.com/manticore-projects/jsqlformatter/issues

.. image:: https://img.shields.io/badge/PRs-welcome-brightgreen.svg
    :alt: PRs Welcome
    :target: http://makeapullrequest.com

.. toctree::
   :caption: Documentation
   :maxdepth: 2
   :hidden:

   install
   usage
   samples
   Stable API <javadoc_stable.rst>
   Development API <javadoc_snapshot.rst>
   demo

.. toctree::
   :maxdepth: 1
   :hidden:

   changelog

.. sidebar:: ANSI output

	.. image:: _static/ansi-terminal.png

	Syntax highlighted SQL, straight in the terminal.

Turn unreadable SQL into something you would actually want to review.

JSQLFormatter is a platform independent SQL formatter, beautifier and pretty
printer by Manticore Projects. It is RDBMS agnostic, it preserves your comments,
and it is driven by a real SQL grammar rather than by regular expressions --
so it understands the structure of a statement instead of guessing at it.

Latest stable release: |JSQLFORMATTER_STABLE_VERSION_LINK|

Development version: |JSQLFORMATTER_SNAPSHOT_VERSION_LINK|

`GitHub Repository <https://github.com/manticore-projects/jsqlformatter>`_ ·
`Online Demo <http://jsqlformatter.manticore-projects.com>`_


...................
What's new in 5.4
...................

**Now licensed under Apache 2.0.** Releases up to and including 5.3 were AGPL.
From 5.4 onwards JSQLFormatter is Apache 2.0, which means you can embed it in
commercial products, IDE plugins and internal tooling without the copyleft
obligation.

**A complete Visitor Pattern rewrite.** The formatter used to be one long
procedural pass with roughly seventy ``instanceof`` checks deciding what to
render. Statements and expressions are now dispatched through JSQLParser's own
visitor interfaces:

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Component
     - Responsibility
   * - ``StatementFormatter``
     - Dispatches ``SELECT``, ``INSERT``, ``MERGE``, ``UPDATE``, ``DELETE``,
       DDL and session statements
   * - ``ExpressionFormatter``
     - Renders the full expression tower -- operators, functions, ``CASE``,
       sub-selects, literals
   * - ``Renderer``
     - Shared spelling, indentation and keyword-casing primitives

Two things follow from this. Supporting a new node type is now a single
``visit(...)`` override instead of another branch in a growing ``if`` chain.
And every node type that is *not* explicitly handled still formats sensibly:
a keyword-aware fallback walks the node's own output, applies your configured
keyword spelling to uppercase token runs, and passes quoted literals through
untouched. No more raw, unformatted islands in the middle of an otherwise
clean statement.

**Built on JSQLParser 5.3.242**, which brings massive parsing performance
improvements.

.. note::

   The EBNF and Railroad Diagram pages have been retired. The authoritative
   grammar reference now lives with
   `JSQLParser <https://manticore-projects.com/JSQLParser/index.html>`_ itself.


...................
See it work
...................

.. tab:: Before

    .. code-block:: sql

        update cfe.calendar set year_offset=?, settlement_shift=to_char(?),
        friday_is_holiday=?, saturday_is_holiday=?, sunday_is_holiday=?
        where id_calendar=?;

.. tab:: After

    .. code-block:: sql

        UPDATE cfe.calendar
        SET year_offset = ?
            , settlement_shift = To_Char( ? )
            , friday_is_holiday = ?
            , saturday_is_holiday = ?
            , sunday_is_holiday = ?
        WHERE id_calendar = ?
        ;

.. tab:: Java

    .. code-block:: java

        import com.manticore.jsqlformatter.JSQLFormatter;

        String formattedSql = JSQLFormatter.format("SELECT * FROM table1");

.. tab:: Shell

    .. code-block:: bash

        java -jar JSQLFormatterCLI.jar -i queries.sql --ansi


.........
Features
.........

.. list-table::
   :widths: 30 70
   :header-rows: 0

   * - **Grammar driven**
     - Built on `JSQLParser <https://github.com/JSQLParser/JSqlParser>`_. The
       SQL is genuinely parsed into an AST before anything is printed.
   * - **RDBMS agnostic**
     - Oracle, SQL Server, MySQL and MariaDB, PostgreSQL, H2, DuckDB,
       BigQuery, Redshift, Databricks and Snowflake, from one grammar.
   * - **Broad coverage**
     - Complex ``SELECT``, ``INSERT INTO``, ``MERGE``, ``UPDATE``, ``DELETE``,
       ``CREATE`` and ``ALTER``, including ``WITH`` clauses, nested
       sub-selects, Oracle ``(+)`` joins and PostgreSQL ``::`` casts.
   * - **Comments survive**
     - Comments are anchored to the node they belong to, including comments
       wedged between keywords or inside join conditions. String literals that
       merely look like comments are left alone.
   * - **Syntax highlighting**
     - ANSI for the terminal, HTML and RTF for documents and wikis.
   * - **Round-trips through Java**
     - Import a SQL string out of Java source preserving variables, then export
       it back as a ``String``, ``StringBuilder`` or ``MessageFormat``.
   * - **Configurable**
     - Indent width, comma placement and keyword, function and object casing --
       set per invocation, per file, or per statement.


.........
Platform
.........

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Target
     - Notes
   * - Java library
     - JAR from Maven Central
   * - Command line
     - Executable JAR
   * - Native binary
     - Static binary or shared library for Linux, Windows and macOS
   * - NetBeans plugin
     - Eclipse, JEdit, Squirrel SQL and DBeaver planned


..................
Related projects
..................

* `JSQLParser <https://manticore-projects.com/JSQLParser/index.html>`_ --
  the SQL parser this is built on
* `JSQLTranspiler <https://manticore-projects.com/JSQLTranspiler/index.html>`_ --
  dialect rewriting, column resolution and lineage