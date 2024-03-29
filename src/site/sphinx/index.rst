.. meta::
   :description: Java Software Library for formatting and pretty printing SQL Statements
   :keywords: java sql statement format pretty printing

***************************
Java SQL Formatting Library
***************************

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
