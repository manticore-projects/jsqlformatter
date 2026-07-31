*********
Samples
*********

Every example below is a live test fixture from the JSQLFormatter test suite,
so what you see here is exactly what the current build produces.

.. tip::

   Want to try your own statement? The
   `online demo <http://jsqlformatter.manticore-projects.com>`_ runs the same
   code in the browser.


Select
--------------

Projections, joins, sub-selects and ``WITH`` clauses. Note how the field
separator sits at the start of the line by default, which keeps a long column
list readable and makes diffs line up when a column is added.

.. literalinclude:: resources/standard/StandardSelectTest.sql
  :language: SQL
  :force:
  :linenos:


Insert
--------------

.. literalinclude:: resources/standard/StandardInsertTest.sql
  :language: SQL
  :force:
  :linenos:


Update
--------------

.. literalinclude:: resources/standard/StandardUpdateTest.sql
  :language: SQL
  :force:
  :linenos:


Merge
--------------

``MERGE`` is where regex-based formatters usually give up: the ``WHEN MATCHED``
and ``WHEN NOT MATCHED`` branches each carry a nested statement.

.. literalinclude:: resources/standard/StandardMergeTest.sql
  :language: SQL
  :force:
  :linenos:


Create
--------------

Table and index definitions, including column constraints and storage clauses.

.. literalinclude:: resources/standard/StandardCreateTableTest.sql
  :language: SQL
  :force:
  :linenos:

.. literalinclude:: resources/standard/StandardCreateIndexTest.sql
  :language: SQL
  :force:
  :linenos:


Alter
--------------

.. literalinclude:: resources/standard/StandardAlterTest.sql
  :language: SQL
  :force:
  :linenos:


Comments
--------------

Comments are anchored to the node they belong to rather than to a line number,
so they survive re-indentation -- including comments wedged between keywords or
inside a join condition. String literals that merely *look* like comments are
passed through untouched.

.. literalinclude:: resources/standard/StandardCommentTest.sql
  :language: SQL
  :force:
  :linenos:


MS SQL Server
-------------------

T-SQL uses square brackets as identifier quotes, where other dialects use them
for arrays. ``squareBracketQuotation`` controls the interpretation, and
``AUTO`` infers it from the statement.

.. literalinclude:: resources/standard/BracketQuotationTest.sql
  :language: SQL
  :force:
  :linenos:

.. literalinclude:: resources/standard/MsSqlServerTest.sql
  :language: SQL
  :force:
  :linenos:


Formatting options
-------------------

The same statements rendered under different option sets, showing the effect of
indent width, separator position and the three spelling policies.

.. literalinclude:: resources/standard/FormattingOptionsTest.sql
  :language: SQL
  :force:
  :linenos: