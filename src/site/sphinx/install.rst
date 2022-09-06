*****************************
Installation of JSQLFormatter
*****************************

Git
===================
.. code:: Bash

   git clone https://github.com/manticore-projects/jsqlformatter.git
   cd jsqlformatter

.. tabs::

   .. tab:: Maven

     .. code:: Bash

      mvn install

   .. tab:: Gradle

     .. code:: Bash

      gradle build

   .. tab:: Ant

     .. code:: Bash

      ant jar


Maven Repo
===================

.. code-block:: xml
   :substitutions:

   <dependency>
      <groupId>com.manticore-projects.jsqlformatter</groupId>
      <artifactId>jsqlformatter</artifactId>
      <version>|JSQLFORMATTER_VERSION|</version>
   </dependency>

Download
===================

Static Binaries
---------------------------------------------

.. list-table:: Static Binaries Direct Download Links
   :widths: 25 25 25
   :header-rows: 1

   * - Operating System
     - File
     - Size
   * - Java Stable Release
     - |JSQLFORMATTER_STABLE_VERSION_LINK|
     - (700 kB)
   * - Java Development Snapshot
     - |JSQLFORMATTER_SNAPSHOT_VERSION_LINK|
     - (700 kB)
   * - Linux x64 Stable Release
     - |JSQLFORMATTER_STABLE_LINUX_BINARY_LINK|
     - (3 MB)
   * - Windows x64 Stable Release
     - |JSQLFORMATTER_STABLE_WINDOWS_BINARY_LINK|
     - (3 MB)
   * - MacOS Stable Release
     - |JSQLFORMATTER_STABLE_MACOSX_BINARY_LINK|
     - (3 MB)

.. note::

  On MacOS, grant an exception for a blocked app by clicking the Open Anyway button in the General pane of Security & Privacy preferences.

Native Dynamic Libraries
---------------------------------------------

   Coming soon.

Plugins
---------------------------------------------

.. list-table:: Plugins Direct Download Links
   :widths: 25 25 25
   :header-rows: 1

   * - Platform
     - File
     - Size
   * - Netbeans 12
     - `ExternalCodeFormatter-SQL.nbm <0.1.11/externalcodeformatter-sql.nbm>`_
     - (45.2 MB)
