Tesseract
----------
What it is?
-----------
tesseract is an OCR tool which can convert tiff, jpg, png... (best use tesseract 4.0)

https://github.com/tesseract-ocr/tesseract

How to build
------------
Execute the following command:
- `mvn clean install` it will build the jar and execute the test.

In order to build the RPM package you must have `rpmbuild` tool.

How to run
----------
On a shell run `griffin path/to/batch/directory`, it can also be run with `java -jar target/tesseract-jar-with-dependencies.jar path/to/batch/directory`. A path to the work directory can be specify or nothing if the tool is executed directly in the right place.

How to install
--------------
On centos run `dnf install target/griffon-tesseract-1.0.rpm` and on debian `dpkg -i target/griffon-tesseract-1.0.deb`.
