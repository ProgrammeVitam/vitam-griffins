griffin-odfvalidator
-------------------

What it is?
-----------
ODFValidator is a java library that can validate Open Document Formats.

https://incubator.apache.org/odftoolkit/conformance/ODFValidator.html

How to build
------------
First you have to install
odfvalidator 1.2.0-incubating-vitam from vitam branch of vitam/odftoolkit

Execute the following command:
- `mvn clean install` it will build the jar and execute the test.

In order to build the RPM package you must have `rpmbuild` tool.

How to run
----------
On a shell run `griffin-odfvalidator path/to/batch/directory`, it can also be run with `java -jar target/griffin-odfvalidator-jar-with-dependencies.jar path/to/batch/directory`. A path to the work directory can be specify or nothing if the tool is executed directly in the right place.

How to install
--------------
On centos run `dnf install target/griffin-odfvalidator-1.0.rpm` and on debian `dpkg -i target/griffin-odfvalidator-1.0.deb`.
