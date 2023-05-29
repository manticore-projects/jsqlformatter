*****************
How to use it
*****************

-----------------
Static Binaries
-----------------

.. tab:: JVM

  .. code:: Bash

     java -jar JSQLFormatter.jar [-i <arg>] [-o <arg>] [-f <arg> | --ansi | --html]   [-t <arg> | -2 | -8]   [--keywordSpelling <arg>] [--functionSpelling <arg>] [--objectSpelling <arg>] [--separation <arg>] [--squareBracketQuotation <arg>] --squareBracketQuotation <arg>

.. tab:: Linux Shell

  .. code:: Bash

     ./JSQLFormatter [-i <arg>] [-o <arg>] [-f <arg> | --ansi | --html]   [-t <arg> | -2 | -8]   [--keywordSpelling <arg>] [--functionSpelling <arg>] [--objectSpelling <arg>] [--separation <arg>] [--squareBracketQuotation <arg>] --squareBracketQuotation <arg>

.. tab:: Windows Power Shell

  .. code:: Bash

     JSQLFormatter.exe [-i <arg>] [-o <arg>] [-f <arg> | --ansi | --html]   [-t <arg> | -2 | -8]   [--keywordSpelling <arg>] [--functionSpelling <arg>] [--objectSpelling <arg>] [--separation <arg>] [--squareBracketQuotation <arg>] --squareBracketQuotation <arg>

..........................
Command Line Options (CLI)
..........................
--inputFile, -i <arg>           The input SQL file or folder.
--outputFile, -o <arg>          The out SQL file for the formatted statements.
--format, -f <arg>              The output-format [PLAIN* ANSI HTML RTF]
--ansi                          Output ANSI annotated text.
--html                          Output HTML annotated text.
--indentWidth, -t <arg>         The Indent Width [2 4* 8]
 -2                             Indent with 2 characters.
 -8                             Indent with 8 characters.
--keywordSpelling <arg>         Spelling of keywords. [UPPER* LOWER CAMEL KEEP]
--objectSpelling <arg>          Spelling of object names. [UPPER* LOWER CAMEL KEEP]
--functionSpelling <arg>        Spelling of function names. [UPPER* LOWER CAMEL KEEP]
--separation <arg>              Position of the field separator. [BEFORE* AFTER]
--squareBracketQuotation <arg>  Interpret Square Brackets "[]" as quotes instead of arrays. [AUTO* YES NO] 
 
.. note::

  You can provide the SQL Statement as an argument to the program, e. g.
   
  .. code:: Bash
        
    java -jar JSQLFormatter.jar "select * from dual;"

.. note::

  You can provide the formatting options as comment in front of the sql statement
   
  .. code:: SQL
        
    -- @JSQLFormatter(indentWidth=8, keywordSpelling=UPPER, functionSpelling=CAMEL, objectSpelling=LOWER, separation=BEFORE)
    SELECT 'something' FROM DUAL;
       
     
.. warning::

  On Windows 10, you will need to active ANSI output first
        
  .. code:: Shell
   
    Set-ItemProperty HKCU:\Console VirtualTerminalLevel -Type DWORD 1     
      
        

-----------------
Dynamic Libraries
-----------------

.. tab:: Java

  .. code:: Java

    import com.manticore.jsqlformatter.JSqlFormatter;

    class Sample {
        public static void main(String[] args) {
            String formattedSql = JSqlFormatter.format("select * fromd dual;");
        }
    }

.. tab:: C++

  .. code:: python

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

