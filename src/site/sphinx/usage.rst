*****************
How to use it
*****************

=================
As a Java library
=================

The entry point is ``com.manticore.jsqlformatter.JSQLFormatter``. A single
static call covers the common case:

.. code-block:: java

    import com.manticore.jsqlformatter.JSQLFormatter;

    class Sample {
        public static void main(String[] args) {
            String formattedSql = JSQLFormatter.format("select * from dual;");
            System.out.println(formattedSql);
        }
    }

Formatting options are passed as trailing ``key=value`` arguments, using the
same names as the CLI flags and the inline directive:

.. code-block:: java

    String formattedSql = JSQLFormatter.format(
            "select * from dual;",
            "indentWidth=2",
            "keywordSpelling=LOWER",
            "separation=AFTER");

.. note::

   The class name is ``JSQLFormatter``, all caps. Earlier revisions of this
   page showed ``JSqlFormatter``, which will not compile.


=======================
From the command line
=======================

.. tab:: JVM

  .. code-block:: bash

     java -jar JSQLFormatterCLI.jar [-i <arg>] [-o <arg>] [-f <arg> | --ansi | --html] [-t <arg> | -2 | -8] [--keywordSpelling <arg>] [--functionSpelling <arg>] [--objectSpelling <arg>] [--separation <arg>] [--squareBracketQuotation <arg>] [--statementTerminator <arg>]

.. tab:: Linux Shell

  .. code-block:: bash

     ./JSQLFormatterCLI [-i <arg>] [-o <arg>] [-f <arg> | --ansi | --html] [-t <arg> | -2 | -8] [--keywordSpelling <arg>] [--functionSpelling <arg>] [--objectSpelling <arg>] [--separation <arg>] [--squareBracketQuotation <arg>] [--statementTerminator <arg>]

.. tab:: Windows PowerShell

  .. code-block:: powershell

     JSQLFormatterCLI.exe [-i <arg>] [-o <arg>] [-f <arg> | --ansi | --html] [-t <arg> | -2 | -8] [--keywordSpelling <arg>] [--functionSpelling <arg>] [--objectSpelling <arg>] [--separation <arg>] [--squareBracketQuotation <arg>] [--statementTerminator <arg>]

Typical invocations:

.. code-block:: bash

   # format a file, ANSI highlighted, straight to the terminal
   java -jar JSQLFormatterCLI.jar -i queries.sql --ansi

   # reformat a whole folder into one target file, 2-space indent, lowercase keywords
   java -jar JSQLFormatterCLI.jar -i ./sql/ -o formatted.sql -2 --keywordSpelling LOWER

   # format a statement passed directly as an argument
   java -jar JSQLFormatterCLI.jar "select * from dual;"


..........................
Command line options
..........................

.. list-table::
   :widths: 28 42 30
   :header-rows: 1

   * - Option
     - Description
     - Values (default marked ``*``)
   * - ``--inputFile``, ``-i``
     - The input SQL file or folder
     - path
   * - ``--outputFile``, ``-o``
     - The output SQL file for the formatted statements
     - path
   * - ``--format``, ``-f``
     - The output format
     - ``PLAIN*`` ``ANSI`` ``HTML`` ``RTF``
   * - ``--ansi``
     - Shorthand for ANSI annotated output
     -
   * - ``--html``
     - Shorthand for HTML annotated output
     -
   * - ``--indentWidth``, ``-t``
     - The indent width
     - ``2`` ``4*`` ``8``
   * - ``-2`` / ``-8``
     - Shorthand for indent width 2 or 8
     -
   * - ``--keywordSpelling``
     - Spelling of keywords
     - ``UPPER*`` ``LOWER`` ``CAMEL`` ``KEEP``
   * - ``--functionSpelling``
     - Spelling of function names
     - ``UPPER`` ``LOWER`` ``CAMEL*`` ``KEEP``
   * - ``--objectSpelling``
     - Spelling of object names
     - ``UPPER`` ``LOWER*`` ``CAMEL`` ``KEEP``
   * - ``--separation``
     - Position of the field separator
     - ``BEFORE*`` ``AFTER``
   * - ``--squareBracketQuotation``
     - Interpret square brackets ``[]`` as quotes instead of arrays
     - ``AUTO*`` ``YES`` ``NO``
   * - ``--statementTerminator``
     - The statement terminator
     - ``SEMICOLON*`` ``NONE`` ``GO`` ``BACKSLASH``

.. warning::

   On Windows 10 you need to enable ANSI output before ``--ansi`` will render:

   .. code-block:: powershell

      Set-ItemProperty HKCU:\Console VirtualTerminalLevel -Type DWORD 1


=======================
Options inside the SQL
=======================

Any statement can carry its own formatting options in a leading comment. This
is the most useful feature for repositories that serve more than one house
style, because different statements in the same file can follow different
conventions.

.. code-block:: sql

    -- @JSQLFormatter(indentWidth=8, keywordSpelling=UPPER, functionSpelling=CAMEL, objectSpelling=LOWER, separation=BEFORE)
    SELECT 'something' FROM DUAL;

The directive accepts the same keys as the CLI flags, and applies to every
statement following it until the next directive.


=========================
From C and other natives
=========================

The GraalVM shared library exposes ``format`` through the standard isolate
API.

.. code-block:: c

    #include <stdlib.h>
    #include <stdio.h>

    #include <libSQLFormatter.h>

    int main(int argc, char **argv) {
        graal_isolate_t *isolate = NULL;
        graal_isolatethread_t *thread = NULL;

        if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
            fprintf(stderr, "graal_create_isolate error\n");
            return 1;
        }

        printf("%s", format(thread, "select * from dual;"));

        if (graal_detach_thread(thread) != 0) {
            fprintf(stderr, "graal_detach_thread error\n");
            return 1;
        }

        return 0;
    }