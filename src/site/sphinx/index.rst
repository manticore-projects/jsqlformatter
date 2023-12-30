.. meta::
   :description: Java Software Library for formatting and pretty printing SQL Statements
   :keywords: java sql statement format formatter pretty printing

***************************
Java SQL Formatting Library
***************************

.. image:: https://github.com/manticore-projects/jsqlformatter/actions/workflows/gradle.yml/badge.svg
    :alt: Gradle CI
    :target: https://github.com/manticore-projects/jsqlformatter/actions/workflows/gradle.yml

.. image:: https://badgen.net/maven/v/maven-central/com.manticore-projects.jsqlformatter/jsqlformatter
    :alt: Maven
    :target: https://mvnrepository.com/artifact/com.manticore-projects.jsqlformatter/jsqlformatter
.. image:: https://app.codacy.com/project/badge/Grade/80374649d914462ebd6e5b160a1ebdbb
    :alt: Codacy Badge
    :target: https://app.codacy.com/gh/manticore-projects/jsqlformatter/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade

.. image:: https://coveralls.io/repos/github/manticore-projects/jsqlformatter/badge.svg
    :alt: Coverage Status
    :target: https://coveralls.io/github/manticore-projects/jsqlformatter

.. image:: https://img.shields.io/badge/License-AGPL-blue
    :alt: License
    :target: https://www.gnu.org/licenses/agpl-3.0.en.html#license-text

.. image:: https://img.shields.io/github/issues/manticore-projects/jsqlformatter
    :alt: issues - JSQLFormatter
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
   Syntax   <syntax.rst>
   Java API <javadoc.rst>
   samples
   demo


.. toctree::
   :maxdepth: 1
   :hidden:

   changelog

.. sidebar:: Ansi output

	.. image:: _static/ansi-terminal.png

	Format SQL files in the Terminal Console.

Platform independent SQL Formatter, Beautifier and Pretty Printer by Manticore Projects.

Latest stable release: |JSQLFORMATTER_STABLE_VERSION_LINK|

Development version: |JSQLFORMATTER_SNAPSHOT_VERSION_LINK|

`GitHub Repository <https://github.com/manticore-projects/JSQLFormatter>`_


.........
Features
.........
  * based on `JSQLParser <https://github.com/JSQLParser/>`_
  * supports complex ``SELECT``, ``INSERT INTO``, ``MERGE``, ``UPDATE``, ``DELETE``, ``CREATE``, ``ALTER`` statements
  * Syntax highlighting (ANSI, HTML, RTF)
  * Command Line Options (CLI) and SQL Inline Formatting Options

	* Indent Width
	* Comma Before or After
	* Upper/Lower/Camel-Case Spelling of Keywords, Functions and Object Names

  * Import from Java String or StringBuilder Code preserving variables
  * Export to Java String, StringBuilder or MessageFormat handling variables

.........
Platform
.........
  * Java Library (JAR)
  * Native Static Binary or Dynamic Library for Windows, Linux or MacOS
  * Netbeans Plugin (Other platforms such as Eclipse, JEdit, Squirrel SQL, DBeaver coming soon)
