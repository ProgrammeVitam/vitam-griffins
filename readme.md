# griffins
## Introduction
Griffins are program that are able to apply conversion, transformation or identification on binary files.

## Griffin, batch and inner process
- An inner process is specific execution to apply on a file; like `magick convert -auto-orient test.jpg test.jpg`. Here we want to rotate a picture `test.jpg`.
- A batch is a collection of process or command to execute (like the previous one).
- A griffin is a execution of one batch.

## Status
-  `OK` with exit code 0. It means that every thing goes well.
-  `KO` with exit code 1. It means that the griffins, the batch or the inner process stop abruptly.
-  `WARNING` with exit code 2. It means that something goes wrong on the griffins, batch or the inner process.

## Action
- `ANALYZE` like check if a picture is corrupted or not.
- `IDENTIFY` like identify the file, and find it's PUID.
- `GENERATE` like generate a GIF from a JPG picture.
- `EXTRACT` like extract some EXIF data from a picture.

## Analyze result
- `VALID_ALL` means that the file is the right format and is not corrupted.
- `NOT_VALID` means that the file is the right format but may be corrupted.
- `WRONG_FORMAT` means that the file is in the wrong format.

## Parameters
Parameters are the required variables in order to run a batch in agriffin serialized in a JSON file named `parameters.json`:
- `requestId` of type string, is the vitam request id.
- `id` of typestring, is the batch id.
- `debug` og type boolean, show or not the following thing in `result.json` file: `result`, `error`,`executed`.
- `actions` of type list of object, is the list of action to do on an input. An action is compose of the following:
    * `type` of type string, is one of the following: `ANALYZE`,`IDENTIFY`, `GENERATE`, `EXTRACT`.
    * `values` of type object, containing the necessary values to run the action.
- `inputs` of type list of object, is the list of file to process, an input is compose of:
    * `name` of type string, is the name of the input.
    * `formatId` of type string, is the puid of the input.

## Result
Result of the griffin execution is set in a JSON file named `result.json` with the following information:
- `requestId` of type string, is the vitam request id.
- `id` of type string, is the batch id.
- `outputs` of type list of object, representing the list of results. The output is compose withe the following variables:
    * `result` (show only in `debug` mode) of type string, is the log result of th execution (for example the `stdout` of a command).
    * `error` (show only in `debug` mode) of type string, is the log error of th execution (for example the `stderr` of a command).
    * `executed` (show only in `debug` mode) of type string, is what thing has been executed (for example a command and it’s arguments).
    * `input` of type input, is the input received.
    * `outputName` of type string, is the name of the output file, result of the action.
    * `status` of type string, is the status of the execution, one of `OK`, `ERROR`, `WARNING`
    * `analyseResult` (show only in `ANALYZE`) of type string, one of `VALID_ALL`, `NOT_VALID`, `WRONG_FORMAT`.
    * `action` of type string, is the action executed, one of `ANALYZE`, `IDENTIFY`, `GENERATE`, `EXTRACT`.

## Input files
Input files are set in a directory named `input-files`.

## Output files

Output files are set in a directory named `output-files`.

## Example
Here `f5a5f253-04a5-4567-b88a-c5af7df633df` is the batch id.
- `./imagemagick-griffin-vitam/f5a5f253-04a5-4567-b88a-c5af7df633df/parameters.json` is the file where all parameters are set.
- `./imagemagick-griffin-vitam/f5a5f253-04a5-4567-b88a-c5af7df633df/result.json` is the file where processing results are set.
- `./imagemagick-griffin-vitam/f5a5f253-04a5-4567-b88a-c5af7df633df/input-files/` is the directory where files to process are set.
- `./imagemagick-griffin-vitam/f5a5f253-04a5-4567-b88a-c5af7df633df/output-files/` is the directory where processed files are set.

### `parameters.json` example
```json
{
  "requestId": "4f6ae8d7-cab7-4f8d-b5e1-d5c0a1ea5793",
  "id": "1479591e-d325-456f-8409-697f3a757bf7",
  "debug": false,
  "actions": [
    {"type": "GENERATE", "values": {"extension": "GIF", "args": ["-thumbnail", "100x100"]}},
    {"type": "ANALYSE"},
    {"type": "EXTRACT", "values": {"dataToExtract": {"AU_METADATA_RESOLUTION": "/image/properties/exif:ResolutionUnit", "GOT_METADATA_METHOD": "/image/properties/exif:SensingMethod", "AU_METADATA_DATE": "/image/properties/xmp:ModifyDate"}}}
  ],
  "inputs": [
    {"name": "test.jpg", "formatId": "fmt/41"}
  ]
}
```

### `result.json` example
```json
{
  "requestId": "4f6ae8d7-cab7-4f8d-b5e1-d5c0a1ea5793",
  "id": "1479591e-d325-456f-8409-697f3a757bf7",
  "outputs": {
    "test.jpg": [
      {
        "input": {"name": "test.jpg", "formatId": "fmt/41"},
        "outputName": "GENERATE-test.jpg.GIF",
        "status": "OK",
        "action": "GENERATE"
      },
      {
        "input": {"name": "test.jpg", "formatId": "fmt/41"},
        "status": "OK",
        "analyseResult": "VALID_ALL",
        "action": "ANALYSE"
      },
      {
        "input": {"name": "test.jpg", "formatId": "fmt/41"},
        "outputName": "EXTRACT-test.jpg.json",
        "status": "OK",
        "action": "EXTRACT"
      }
    ]
  }
}
```
## Executable
In a Vitam env, griffins are installed with RPM or DEB files. Griffons are composed of at least one file: the executable file which name is always `griffin`.
If there is an inner tool (like `imagemagick` or `libreoffice` executable), it must be set as a dependency of the RPM/DEB file.
Griffins can come with a conf file or a jar file that must live in the same place with executable.

### example for `helloWorld-griffin`
In this griffin directory there is:
- A `griffin` executable file that will launch Vitam, and that call this command line: `java -Dfile.encoding=UTF-8 -jar /vitam/bin/worker/griffins/helloWorld-griffin/helloWorld-jar-with-dependencies.jar $1 "/vitam/bin/worker/griffins/helloWorld-griffin/config.json"`.
- A jar file `helloWorld-griffin/helloWorld-jar-with-dependencies.jar` where is the actual code of the griffin.
- A configuration file `config.json` that will be use by our griffin in order to make our preservation.

## In Vitam env
All griffin must be installed in:
- `/vitam/bin/worker/griffins/GRIFFIN-NAME` for the executables.
- `/vitam/tmp/worker/griffins/GRIFFIN-NAME` for the batch directory.
- And the executable or library must be installed in the usual directory like `/usr/bin/EXECUTABLE-NAME`

## Building
Griffins are build with maven, in order to build all griffins, run `mvn clean install`.
You must have command line tool installed in order to pass test, otherwise use `mvn clean install -DskipTest`.
In order to build rmp/deb you must have `rpmbuild` tool.

## Cookbook
Available and "real" examples of griffins and preservation scenario can be found in the [cookbook](cookbook/) directory.

## Available Griffins
### Tesseract
[Tesseract](https://github.com/tesseract-ocr/tesseract) is an OCR tool which can convert tiff, jpg, png...
Example of a tesseract griffin can be found [here](cookbook/griffins-tesseract.json), and a preservation scenario can be found [here](cookbook/preservation-generate-tesseract.json), all can be used with [this SIP](cookbook/sip_tesseract.zip).
Tesseract griffin can only do `EXTRACT` actions, and a batch example can be found [here](tesseract/src/test/resources/batch-reference).

### Siegfried
[Siegfried](https://github.com/richardlehane/siegfried) is a tool that identifies files.
Example of a Siegfried griffin can be found [here](cookbook/griffins-siegfried.json), and a preservation scenario can be found [here](cookbook/preservation-identification.json), all can be used with [this SIP](cookbook/sip_images.zip).
Siegfried griffin can only do `IDENTIFY` actions, and a batch example can be found [here](siegfried/src/test/resources/batch-reference).

### odfvalidator
[ODFValidator](https://incubator.apache.org/odftoolkit/conformance/ODFValidator.html) is a java library that can validate Open Document Formats.
Example of a odfvalidator griffin can be found [here](cookbook/griffins-odfvalidator.json), and a preservation scenario can be found [here](cookbook/preservation-analyse-odfvalidator.json), all can be used with [this SIP](cookbook/sip_libreoffice.zip).
odfvalidator griffin can only do `ANALYSE` actions, and a batch example can be found [here](odfvalidator/src/test/resources/batch-reference).
NOTE: First you have to install
      odfvalidator 1.2.0-incubating-vitam from vitam branch of vitam/odftoolkit

### libreoffice
[LibreOffice](https://fr.libreoffice.org/) is an application which can be used to convert office files or generate pdf from them.
Example of a Libreoffice griffin can be found [here](cookbook/griffins-libreoffice.json), and a preservation scenario can be found [here](cookbook/preservation-generate-libreoffice.json), all can be used with [this SIP](cookbook/sip_libreoffice.zip).
Libreoffice griffin can only do `GENERATE` actions, and a batch example can be found [here](libreoffice/src/test/resources/batch-reference).

Notes, filters informations are here [List Filters available](https://github.com/LibreOffice/core/tree/master/filter/source/config/fragments/filters). [PDF exports](http://specs.openoffice.org/appwide/pdf_export/PDFExportDialog.odt) information can be found in the link or bellow:
- `UseLosslessCompression` of type `boolean` and the default value is `false`. Specifies if graphics are exported to PDF using a lossless compression eg. PNG or if they are compressed using the JPEG format.
- `Quality` of type `int` and the default value is `90`. Specifies quality of the JPG export. A higher value results in higher quality and file size.
- `ReduceImageResolution` of type `boolean` and the default value is `false`. Specifies if the resolution of each image is reduced to the resolution specified by the property MaxImageResolution.
- `MaxImageResolution` of type `int` and the default value is `300`. If the property ReduceImageResolution is set to true all images will be reduced to the given value in DPI.
- `UseTaggedPDF` of type `boolean` and the default value is `false`. Determines if PDF are created by using special tags also known as Tagged PDF.
- `ExportNotes` of type `boolean` and the default value is `false`. Specifies if notes are exported to PDF.
- `ExportNotesPages` of type `boolean` and the default value is `false`. Specifies if notes pages are exported to PDF. (Notes pages are available in Impress documents only).
- `ExportBookmarks` of type `boolean` and the default value is `true`. Specifies if bookmarks are exported to PDF.
- `OpenBookmarkLevels` of type `int` and the default value is `-1`. Specifies how many bookmark levels should be opened in the reader application when the PDF gets opened. -1 means all levels, non-negative numbers mean the respective number of levels.1
- `UseTransitionEffects` of type `boolean` and the default value is `true`. Specifies slide transitions are exported to PDF. This option is active only if storing Impress documents.
- `ExportFormFields` of type `boolean` and the default value is `true`. Specifies whether form fields are exported as widgets or only their fixed print representation is exported.
- `FormsType` of type `int` and the default value is `0`. Specifies the submitted format of a PDF form. Values are: "0" for  FDF format,"1" for PDF format, "2" for HTML format, "3" for XML format
- `IsSkipEmptyPages` of type `boolean` and the default value is `false`. Specifies that automatically inserted empty pages are suppressed. This option is active only if storing Writer documents.
- `HideViewerMenubar` of type `boolean` and the default value is `false`. Specifies whether to hide the PDF viewer menubar when the document is active.
- `HideViewerToolbar` of type `boolean` and the default value is `false`. Specifies whether to hide the PDF viewer toolbar when the document is active.
- `HideViewerWindowControls` of type `boolean` and the default value is `false`. Specifies whether to hide the PDF viewer controls when the document is active.
- `ResizeWindowToInitialPage` of type `boolean` and the default value is `false`. Specifies that the PDF viewer window is opened full screen when the document is opened.
- `CenterWindow` of type `boolean` and the default value is `false`. Specifies that the PDF viewer window is centered to the screen when the PDF document is opened.
- `OpenInFullScreenMode` of type `boolean` and the default value is `false`. Specifies that the PDF viewer window is opened full screen, on top of all windows.
- `DisplayPDFDocumentTitle` of type `boolean` and the default value is `true`. Specifies that the title of the document, if present in the document properties, is displayed in the PDF viewer window title bar.
- `FirstPageOnLeft` of type `boolean` and the default value is `false`. Used with the value 3 of the PageLayout property when the first page (odd) should be on the left side of the screen.
- `InitialView` of type `int` and the default value is `0`. Specifies how the PDF document should be displayed when opened. "0" meaning neither outlines or thumbnails, "1" meaning the document is opened with outline pane opened, "2" meaning the document is opened with thumbnail pane opened
- `Magnification` of type `int` and the default value is `0`. Specifies the action to be performed when the PDF document is opened, "0" meaning opens with default zoom magnification, "1" meaning opens magnified to fit the entire page within the window, "2" meaning opens magnified to fit the entire page width within the window, "3" meaning opens magnified to fit the entire width of its boundig box within the window (cuts out margins), “4” means with a zoom level given in the “Zoom” property.
- `Zoom` of type ”xs:int” default: 100: Specifies the zoom level a PDF document is opened with. Only valid if "Magnification" is set to "4".
- `PageLayout` of type `int` and the default value is `0`. Specifies the page layout to be used when the document is opened, "0" meaning display the pages according to the reader configuration, "1" meaning display one page at a time, "2" meaning display the pages in one column, "3" meaning display the pages in two columns odd pages on the right, to have the odd pages on the left the FirstPageOnLeft properties should be used as well.
- `InitialPage` of type `int` and the default value is `1`. Specifies the page on which a PDF document should be opened in the viewer application.
- `SelectPdfVersion` of type `int` and the default value is `0`. Specifies the version of the pdf to use.

### jhove
[Jhove](http://jhove.openpreservation.org/) is a java library that can validate different files format.
Example of a jhove griffin can be found [here](cookbook/griffins-jhove.json), and a preservation scenario can be found [here](cookbook/preservation-analyse-jhove.json), all can be used with [this SIP](cookbook/sip_libreoffice.zip).
jhove griffin can only do `ANALYSE` actions, and a batch example can be found [here](jhove/src/test/resources/batch-reference).

### Imagemagick
[Imagemagick](https://www.imagemagick.org/) is a took that can analyze, generate, or extract data from a picture.
Example of a Imagemagick griffin can be found [here](cookbook/griffins-imagemagick.json), and a preservation scenario can be found [here](cookbook/preservation-tranformation-webp.json), all can be used with [this SIP](cookbook/sip_images.zip).
Imagemagick griffin can only do `ANALYSE | EXTRACT | GENERATE` actions, and a batch example can be found [here](imagemagick/src/test/resources/vitam-imagemagick-griffin/batch-reference).

## Dependencies
All inner tools dependencies that cannot be get with the usual dependence tool (like yum for centos and apt for debian) are in:
- `deb/` directory for Debian distribution.
- `rpm/` directory for Centos distribution.

## Environment
Griffins can be test in the [http://griffins.env.programmevitam.fr/](http://griffins.env.programmevitam.fr/) environments and can be deployed separately.