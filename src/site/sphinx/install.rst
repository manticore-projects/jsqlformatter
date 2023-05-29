*****************************
Installation of JSQLFormatter
*****************************

Git
===================
.. code:: Bash

   git clone https://github.com/manticore-projects/jsqlformatter.git
   cd jsqlformatter
   ./gradlew publishToMavenLocal


Maven Repo
===================

.. tab:: Maven Release

    .. code-block:: xml
        :substitutions:

        <dependency>
            <groupId>com.manticore-projects.jsqlformatter</groupId>
            <artifactId>jsqlformatter</artifactId>
            <version>|JSQLFORMATTER_VERSION|</version>
        </dependency>

.. tab:: Maven Snapshot

    .. code-block:: xml
        :substitutions:

        <repositories>
            <repository>
                <id>jsqlformatter-snapshots</id>
                <snapshots>
                    <enabled>true</enabled>
                </snapshots>
                <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
            </repository>
        </repositories>
        <dependency>
            <groupId>com.manticore-projects.jsqlformatter</groupId>
            <artifactId>jsqlformatter</artifactId>
            <version>|JSQLFORMATTER_SNAPSHOT_VERSION|</version>
        </dependency>

.. tab:: Gradle Stable

    .. code-block:: groovy
        :substitutions:

        repositories {
            mavenCentral()
        }

        dependencies {
            implementation 'com.manticore-projects.jsqlformatter:jsqlformatter:|JSQLFORMATTER_VERSION|'
        }

.. tab:: Gradle Snapshot

    .. code-block:: groovy
        :substitutions:

        repositories {
            maven {
                url = uri('https://s01.oss.sonatype.org/content/repositories/snapshots/')
            }
        }

        dependencies {
            implementation 'com.manticore-projects.jsqlformatter:jsqlformatter:|JSQLFORMATTER_SNAPSHOT_VERSION|'
        }



Download
===================

Static Binaries
---------------------------------------------

.. list-table:: Static Binaries Direct Download Links
   :widths: 35 50 15
   :header-rows: 1

   * - Operating System
     - File
     - Size
   * - Java Stable Release
     - |JSQLFORMATTER_STABLE_VERSION_LINK|
     - (80 kB)
   * - Java Development Snapshot
     - |JSQLFORMATTER_SNAPSHOT_VERSION_LINK|
     - (80 kB)
   * - Java Fat JAR Devel. Snapshot
     - |JSQLFORMATTER_FAT_SNAPSHOT_VERSION_LINK|
     - (15 MB)

.. note::

  On MacOS, grant an exception for a blocked app by clicking the Open Anyway button in the General pane of Security & Privacy preferences.

Native Dynamic Libraries
---------------------------------------------

   Coming soon.
