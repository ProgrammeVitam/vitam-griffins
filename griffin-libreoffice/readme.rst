griffin-libreoffice
-------------------
What it is?
-----------
LibreOffice is an application which can be used to convert office files or generate pdf from them.

https://fr.libreoffice.org/

How to build
------------
Execute the following command:
- `mvn clean install` it will build the jar and execute the test.

In order to build the RPM package you must have `rpmbuild` tool.

How to run
----------
On a shell run `griffin-libreoffice path/to/batch/directory`, it can also be run with `java -jar target/griffin-libreoffice-jar-with-dependencies.jar path/to/batch/directory`. A path to the work directory can be specify or nothing if the tool is executed directly in the right place.

How to install
--------------
On centos run `dnf install target/griffin-libreoffice-1.0.rpm` and on debian `dpkg -i target/griffin-libreoffice-1.0.deb`.
