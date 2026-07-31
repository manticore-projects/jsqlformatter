*****************************
Installation of JSQLFormatter
*****************************

JSQLFormatter is published to Maven Central under the group
``com.manticore-projects.jsqlformatter``. From release 5.4 it is licensed under
the Apache License 2.0.


==========================
As a library
==========================

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
                <url>https://central.sonatype.com/repository/maven-snapshots/</url>
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
                url = uri('https://central.sonatype.com/repository/maven-snapshots/')
            }
        }

        dependencies {
            implementation 'com.manticore-projects.jsqlformatter:jsqlformatter:|JSQLFORMATTER_SNAPSHOT_VERSION|'
        }

.. note::

   The legacy ``s01.oss.sonatype.org`` snapshot host has been decommissioned.
   If your build still points at it, update to the URL above.


==========================
Download
==========================

Static binaries
---------------------------------------------

.. list-table:: Direct download links
   :widths: 35 50 15
   :header-rows: 1

   * - Package
     - File
     - Size
   * - Java stable release
     - |JSQLFORMATTER_STABLE_VERSION_LINK|
     - 80 kB
   * - Java development snapshot
     - |JSQLFORMATTER_SNAPSHOT_VERSION_LINK|
     - 80 kB
   * - Java fat JAR, development snapshot
     - |JSQLFORMATTER_FAT_SNAPSHOT_VERSION_LINK|
     - 15 MB

The slim JARs expect JSQLParser and its dependencies on the classpath. The fat
JAR bundles everything and runs standalone -- use that one for the CLI unless
you are managing dependencies yourself.

.. note::

   On macOS, grant an exception for a blocked app by clicking **Open Anyway**
   in the General pane of Security & Privacy preferences.


Native dynamic libraries
---------------------------------------------

Coming soon.


==========================
Build from source
==========================

.. code-block:: bash

   git clone https://github.com/manticore-projects/jsqlformatter.git
   cd jsqlformatter
   ./gradlew publishToMavenLocal

This installs the artifact into your local Maven repository, where a
``mavenLocal()`` repository declaration will pick it up.