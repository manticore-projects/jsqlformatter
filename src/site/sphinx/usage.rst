*********
How to use it
*********

-----------------
Static Binaries
-----------------
.. tabs::

   .. tab:: JVM 

      .. code:: Bash
      
        java -jar JSQLFormatter.jar [-i <arg>] [-o <arg>] [-f <arg>]
            [--ansi] [--html] [--indent <arg>] [-2] [-8]

   .. tab:: Linux Shell

      .. code:: Bash

		./JSQLFormatter [-i <arg>] [-o <arg>] [-f <arg>] [--ansi] [--html] 
		    [--indent <arg>] [-2] [-8]
		    
   .. tab:: Windows Power Shell

      .. code:: Bash

		java -jar JSQLFormatter.jar [-i <arg>] [-o <arg>] [-f <arg> |
       --ansi | --html]   [-t <arg> | -2 | -8]   [--keywordSpelling <arg>]
       [--functionSpelling <arg>] [--objectSpelling <arg>] [--separation
       <arg>]

..........................
Command Line Options (CLI)
..........................
--input-file, -i <arg>      The input SQL file or folder.
--output-file, -o <arg>     The out SQL file for the formatted statements.
--format, -f <arg>          The output-format [PLAIN* ANSI HTML RTF]
--ansi                      Output ANSI annotated text.
--html                      Output HTML annotated text.
--indent <arg>              The indent width [2 4* 8]
 -2                         Indent with 2 characters.
 -8                         Indent with 8 characters.
--keywordSpelling <arg>     Spelling of keywords. [UPPER* LOWER CAMLE KEEP]
--objectSpelling <arg>      Spelling of object names. [UPPER* LOWER CAMLE KEEP]
--functionSpelling <arg>    Spelling of function names. [UPPER* LOWER CAMLE KEEP]
--separation <arg>          Position of the field separator. [BEFORE* AFTER]
 
.. note::

   You can provide the SQL Statement as an argument to the program, e. g.
   
    .. code:: 
        :language: Bash
        
       java -jar JSQLFormatter.jar "select * from dual;"

.. note::

   You can provide the formatting options as comment in front of the sql statement
   
    .. code:: 
        :language: SQL
        
       java -jar JSQLFormatter.jar "select * from dual;"
       
     
.. warning::

   On Windows 10, you will need to active ANSI output first
        
    .. code:: 
        :language: Bash
        
       -- @JSQLFormatter(indentWidth=8, keywordSpelling=UPPER, functionSpelling=CAMEL, objectSpelling=LOWER, separation=BEFORE)
       SELECT 'something' FROM DUAL;
        

-----------------
Dynamic Libraries
-----------------

.. tabs::

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

