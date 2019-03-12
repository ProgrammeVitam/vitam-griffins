griffin-verapdf
-------------------
What it is?
-----------
VeraPDF is a java library that can validate PDF/A documents.

http://verapdf.org/home/

How to build
------------
Execute the following command:
- `mvn clean install` it will build the jar and execute the test.

In order to build the RPM package you must have `rpmbuild` tool.

How to run
----------
On a shell run `griffin-verapdf path/to/batch/directory`, it can also be run with `java -jar target/griffin-verapdf-jar-with-dependencies.jar path/to/batch/directory`. A path to the work directory can be specify or nothing if the tool is executed directly in the right place.

How to install
--------------
On centos run `dnf install target/griffin-verapdf-1.0.rpm` and on debian `dpkg -i target/griffin-verapdf-1.0.deb`.
