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
- `EXTRACT_AU` like extract text content from a picture.

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
  "RequestId": "4f6ae8d7-cab7-4f8d-b5e1-d5c0a1ea5793",
  "Id": "1479591e-d325-456f-8409-697f3a757bf7",
  "Debug": false,
  "Actions": [
    {"Type": "GENERATE", "Values": {"extension": "GIF", "Args": ["-thumbnail", "100x100"]}},
    {"Type": "ANALYSE"},
    {"Type": "EXTRACT", "Values": { "Args": [], "FilteredExtractedObjectGroupData" : [ "ALL_METADATA" ], "FilteredExtractedUnitData" : ["ALL_METADATA"]    }},
    {"Type": "EXTRACT", "Values": { "Args": [], "FilteredExtractedObjectGroupData" : [ "geometry", "compression", "resolution" ], "FilteredExtractedUnitData" : ["ALL_METADATA"]    }},
    {"Type": "EXTRACT", "Values": { "Args": [], "FilteredExtractedObjectGroupData" : [ "RAW_METADATA" ], "FilteredExtractedUnitData" : ["ALL_METADATA"]    }}
  ],
  "Inputs": [
    {"Name": "test.jpg", "FormatId": "fmt/41"}
  ]
}
```

### `result.json` example
```json
{
  "RequestId": "4f6ae8d7-cab7-4f8d-b5e1-d5c0a1ea5793",
  "Id": "1479591e-d325-456f-8409-697f3a757bf7",
  "Outputs": {
    "Test.jpg": [
      {
        "Input": {"name": "test.jpg", "formatId": "fmt/41"},
        "OutputName": "GENERATE-test.jpg.GIF",
        "Status": "OK",
        "Action": "GENERATE"
      },
      {
        "Input": {"name": "test.jpg", "formatId": "fmt/41"},
        "Status": "OK",
        "AnalyseResult": "VALID_ALL",
        "Action": "ANALYSE"
      },
      {
        "Input": {"name": "test.jpg", "formatId": "fmt/41"},
        "OutputName": "EXTRACT-test.jpg.json",
        "Status": "OK",
        "Action": "EXTRACT",
        "ExtractedMetadata": {
            "OtherMetadata": {
              "name": [
                "test.jpg"
              ]
            },
            "RawMetadata": "[{\"image\": { \"name\": \"test.jpg\"}]"
          }
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
Example of a tesseract griffin can be found [here](cookbook/griffin_referential/griffins-tesseract.json), and a preservation scenario can be found [here](cookbook/preservation_referential/preservation-generate-tesseract.json), all can be used with [this SIP](cookbook/sip_test/sip_tesseract.zip).
Tesseract griffin can only do  `GENERATE`, `EXTRACT` and `EXTRACT_AU` actions, and a batch example can be found [here](tesseract/src/test/resources/batch-reference).

### Siegfried
[Siegfried](https://github.com/richardlehane/siegfried) is a tool that identifies files.
Example of a Siegfried griffin can be found [here](cookbook/griffin_referential/griffins-siegfried.json), and a preservation scenario can be found [here](cookbook/preservation_referential/preservation-identification.json), all can be used with [this SIP](cookbook/sip_test/sip_images.zip).
Siegfried griffin can only do `IDENTIFY` actions, and a batch example can be found [here](siegfried/src/test/resources/batch-reference).

### odfvalidator
[ODFValidator](https://incubator.apache.org/odftoolkit/conformance/ODFValidator.html) is a java library that can validate Open Document Formats.
Example of a odfvalidator griffin can be found [here](cookbook/griffin_referential/griffins-odfvalidator.json), and a preservation scenario can be found [here](cookbook/preservation_referential/preservation-analyse-odfvalidator.json), all can be used with [this SIP](cookbook/sip_test/sip_libreoffice.zip).
odfvalidator griffin can only do `ANALYSE` actions, and a batch example can be found [here](odfvalidator/src/test/resources/batch-reference).
NOTE: First you have to install
      odfvalidator 1.2.0-incubating-vitam from vitam branch of vitam/odftoolkit

### libreoffice
[LibreOffice](https://fr.libreoffice.org/) is an application which can be used to convert office files or generate pdf from them.
Example of a Libreoffice griffin can be found [here](cookbook/griffin_referential/griffins-libreoffice.json), and a preservation scenario can be found [here](cookbook/preservation_referential/preservation-generate-libreoffice.json), all can be used with [this SIP](cookbook/sip_test/sip_libreoffice.zip).
Libreoffice griffin can only do `GENERATE` actions, and a batch example can be found [here](libreoffice/src/test/resources/batch-reference).

A list of all `FilterName` can be found bellow:
- `ADO_rowset_XML`.
- `AbiWord`.
- `AppleKeynote`.
- `AppleNumbers`.
- `ApplePages`.
- `BMP___MS_Windows`.
- `BroadBand_eBook`.
- `CGM___Computer_Graphics_Metafile`.
- `ClarisWorks`.
- `ClarisWorks_Calc`.
- `ClarisWorks_Draw`.
- `ClarisWorks_Impress`.
- `Claris_Resolve_Calc`.
- `CorelDrawDocument`.
- `CorelPresentationExchange`.
- `DIF`.
- `DXF___AutoCAD_Interchange`.
- `DocBook_File`.
- `DosWord`.
- `EMF___MS_Windows_Metafile`.
- `EPS___Encapsulated_PostScript`.
- `EPUB`.
- `FictionBook_2`.
- `FreehandDocument`.
- `GIF___Graphics_Interchange`.
- `HTML`.
- `HTML_MasterDoc`.
- `HTML__StarCalc`_.
- `HTML__StarWriter`_.
- `JPG___JPEG`.
- `Lotus`.
- `LotusWordPro`.
- `MET___OS_2_Metafile`.
- `MS_Excel_2003_XML`.
- `MS_Excel_2003_XML_Orcus`.
- `MS_Excel_4_0`.
- `MS_Excel_4_0_Vorlage_Template`.
- `MS_Excel_5_0_95`.
- `MS_Excel_5_0_95_Vorlage_Template`.
- `MS_Excel_95`.
- `MS_Excel_95_Vorlage_Template`.
- `MS_Excel_97`.
- `MS_Excel_97_Vorlage_Template`.
- `MS_Multiplan`.
- `MS_PowerPoint_97`.
- `MS_PowerPoint_97_AutoPlay`.
- `MS_PowerPoint_97_Vorlage`.
- `MS_WinWord_5`.
- `MS_WinWord_6_0`.
- `MS_Word_2003_XML`.
- `MS_Word_2007_XML`.
- `MS_Word_2007_XML_Template`.
- `MS_Word_2007_XML_VBA`.
- `MS_Word_95`.
- `MS_Word_95_Vorlage`.
- `MS_Word_97`.
- `MS_Word_97_Vorlage`.
- `MS_Works`.
- `MS_Works_Calc`.
- `MS_Write`.
- `MWAW_Bitmap`.
- `MWAW_Database`.
- `MWAW_Drawing`.
- `MWAW_Presentation`.
- `MWAW_Spreadsheet`.
- `MWAW_Text_Document`.
- `MacWrite`.
- `Mac_Word`.
- `Mac_Works`.
- `Mac_Works_Calc`.
- `Mariner_Write`.
- `MathML_XML__Math`_.
- `MathType_3_x`.
- `ODG_FlatXML`.
- `ODP_FlatXML`.
- `ODS_FlatXML`.
- `ODT_FlatXML`.
- `OOXML_Text`.
- `OOXML_Text_Template`.
- `PBM___Portable_Bitmap`.
- `PCT___Mac_Pict`.
- `PCX___Zsoft_Paintbrush`.
- `PGM___Portable_Graymap`.
- `PNG___Portable_Network_Graphic`.
- `PPM___Portable_Pixelmap`.
- `PSD___Adobe_Photoshop`.
- `PageMakerDocument`.
- `PalmDoc`.
- `Palm_Text_Document`.
- `Plucker_eBook`.
- `PowerPoint3`.
- `PublisherDocument`.
- `QPro`.
- `QXPDocument`.
- `RAS___Sun_Rasterfile`.
- `Rich_Text_Format`.
- `Rich_Text_Format__StarCalc`_.
- `SVG___Scalable_Vector_Graphics`.
- `SVG___Scalable_Vector_Graphics_Draw`.
- `SVM___StarView_Metafile`.
- `SYLK`.
- `StarBaseReport`.
- `StarBaseReportChart`.
- `StarOffice_Drawing`.
- `StarOffice_Presentation`.
- `StarOffice_Spreadsheet`.
- `StarOffice_Writer`.
- `StarOffice_XML__Base`_.
- `StarOffice_XML__Calc`_.
- `StarOffice_XML__Chart`_.
- `StarOffice_XML__Draw`_.
- `StarOffice_XML__Impress`_.
- `StarOffice_XML__Math`_.
- `StarOffice_XML__Writer`_.
- `T602Document`.
- `TGA___Truevision_TARGA`.
- `TIF___Tag_Image_File`.
- `Text`.
- `Text__StarWriter_Web`_.
- `Text___txt___csv__StarCalc`_.
- `Text__encoded`_.
- `Text__encoded___StarWriter_GlobalDocument`_.
- `Text__encoded___StarWriter_Web`_.
- `UOF_presentation`.
- `UOF_spreadsheet`.
- `UOF_text`.
- `VisioDocument`.
- `WMF___MS_Windows_Metafile`.
- `WPS_Lotus_Calc`.
- `WPS_QPro_Calc`.
- `WordPerfect`.
- `WordPerfectGraphics`.
- `WriteNow`.
- `XBM___X_Consortium`.
- `XHTML_Calc_File`.
- `XHTML_Draw_File`.
- `XHTML_Impress_File`.
- `XHTML_Writer_File`.
- `XPM`.
- `ZMFDocument`.
- `calc8`.
- `calc8_template`.
- `calc_Gnumeric`.
- `calc_HTML_WebQuery`.
- `calc_MS_Excel_2007_Binary`.
- `calc_MS_Excel_2007_VBA_XML`.
- `calc_MS_Excel_2007_XML`.
- `calc_MS_Excel_2007_XML_Template`.
- `calc_OOXML`.
- `calc_OOXML_Template`.
- `calc_StarOffice_XML_Calc_Template`.
- `calc_jpg_Export`.
- `calc_pdf_Export`.
- `calc_png_Export`.
- `calc_svg_Export`.
- `chart8`.
- `dBase`.
- `draw8`.
- `draw8_template`.
- `draw_PCD_Photo_CD_Base`.
- `draw_PCD_Photo_CD_Base16`.
- `draw_PCD_Photo_CD_Base4`.
- `draw_StarOffice_XML_Draw_Template`.
- `draw_bmp_Export`.
- `draw_emf_Export`.
- `draw_eps_Export`.
- `draw_flash_Export`.
- `draw_gif_Export`.
- `draw_html_Export`.
- `draw_jpg_Export`.
- `draw_pdf_Export`.
- `draw_png_Export`.
- `draw_svg_Export`.
- `draw_tif_Export`.
- `draw_wmf_Export`.
- `impress8`.
- `impress8_draw`.
- `impress8_template`.
- `impress_MS_PowerPoint_2007_XML`.
- `impress_MS_PowerPoint_2007_XML_AutoPlay`.
- `impress_MS_PowerPoint_2007_XML_Template`.
- `impress_MS_PowerPoint_2007_XML_VBA`.
- `impress_OOXML`.
- `impress_OOXML_AutoPlay`.
- `impress_OOXML_Template`.
- `impress_StarOffice_XML_Draw`.
- `impress_StarOffice_XML_Impress_Template`.
- `impress_bmp_Export`.
- `impress_emf_Export`.
- `impress_eps_Export`.
- `impress_flash_Export`.
- `impress_gif_Export`.
- `impress_html_Export`.
- `impress_jpg_Export`.
- `impress_pdf_Export`.
- `impress_png_Export`.
- `impress_svg_Export`.
- `impress_tif_Export`.
- `impress_wmf_Export`.
- `math8`.
- `math_pdf_Export`.
- `mov__MOV`.
- `writer8`.
- `writer8_template`.
- `writer_MIZI_Hwp_97`.
- `writer_StarOffice_XML_Writer_Template`.
- `writer_globaldocument_StarOffice_XML_Writer`.
- `writer_globaldocument_StarOffice_XML_Writer_GlobalDocument`.
- `writer_globaldocument_pdf_Export`.
- `writer_jpg_Export`.
- `writer_layout_dump`.
- `writer_pdf_Export`.
- `writer_png_Export`.
- `writer_svg_Export`.
- `writer_web_HTML_help`.
- `writer_web_StarOffice_XML_Writer`.
- `writer_web_StarOffice_XML_Writer_Web_Template`.
- `writer_web_jpg_Export`.
- `writer_web_pdf_Export`.
- `writer_web_png_Export`.
- `writerglobal8`.
- `writerglobal8_HTML`.
- `writerglobal8_template`.
- `writerglobal8_writer`.
- `writerweb8_writer`.
- `writerweb8_writer_template`.

Table of FilterName:

| Nr. | Localized Name                                                 | API Name                                                     | IMPORT | EXPORT | Doc Name               |
|-----|----------------------------------------------------------------|--------------------------------------------------------------|--------|--------|------------------------|
| 1   | "OpenOffice.org 1.0 Text Document"                             | "StarOffice XML (Writer)"                                    | X      | X      | TextDocument           |
| 2   | "StarWriter 4.0"                                               | "StarWriter 4.0"                                             | X      | X      | TextDocument           |
| 3   | "HTML Document (OpenOffice.org Writer)"                        | "HTML (StarWriter)"                                          | X      | X      | TextDocument           |
| 4   | "StarWriter 3.0 Template"                                      | "StarWriter 3.0 Vorlage/Template"                            | X      | X      | TextDocument           |
| 5   | "StarWriter 4.0 Template"                                      | "StarWriter 4.0 Vorlage/Template"                            | X      | X      | TextDocument           |
| 6   | "StarWriter 5.0 Template"                                      | "StarWriter 5.0 Vorlage/Template"                            | X      | X      | TextDocument           |
| 7   | "Microsoft Word 95 Template"                                   | "MS Word 95 Vorlage"                                         | X      | -      | TextDocument           |
| 8   | "Microsoft Word 97/2000/XP Template"                           | "MS Word 97 Vorlage"                                         | X      | -      | TextDocument           |
| 9   | "OpenOffice.org 1.0 Text Document Template"                    | "writer_StarOffice_XML_Writer_Template"                      | X      | X      | TextDocument           |
| 10  | "ODF Text Document"                                            | "writer8"                                                    | X      | X      | TextDocument           |
| 11  | "Microsoft Word 2003 XML"                                      | "MS Word 2003 XML"                                           | X      | X      | TextDocument           |
| 12  | "Microsoft Excel 95 (OpenOffice.org Writer)"                   | "MS Excel 95 (StarWriter)"                                   | X      | -      | TextDocument           |
| 13  | "Lotus 1-2-3 1.0 WIN (OpenOffice.org Writer)"                  | "Lotus 1-2-3 1.0 (WIN) (StarWriter)"                         | X      | -      | TextDocument           |
| 14  | "Lotus 1-2-3 1.0 DOS (OpenOffice.org Writer)"                  | "Lotus 1-2-3 1.0 (DOS) (StarWriter)"                         | X      | -      | TextDocument           |
| 15  | "WordPerfect Document"                                         | "WordPerfect"                                                | X      | -      | TextDocument           |
| 16  | "Rich Text Format"                                             | "Rich Text Format"                                           | X      | X      | TextDocument           |
| 17  | "StarWriter 1.0"                                               | "StarWriter 1.0"                                             | X      | -      | TextDocument           |
| 18  | "PDF - Portable Document Format"                               | "writer_pdf_Export"                                          | -      | X      | TextDocument           |
| 19  | "Microsoft Word 2007 XML Template"                             | "MS Word 2007 XML Template"                                  | X      | -      | TextDocument           |
| 20  | "StarWriter 3.0"                                               | "StarWriter 3.0"                                             | X      | X      | TextDocument           |
| 21  | "ODF Text Document Template"                                   | "writer8_template"                                           | X      | X      | TextDocument           |
| 22  | "BibTeX"                                                       | "BibTeX_Writer"                                              | -      | X      | TextDocument           |
| 23  | "StarWriter 5.0"                                               | "StarWriter 5.0"                                             | X      | X      | TextDocument           |
| 24  | "Microsoft Word 6.0"                                           | "MS WinWord 6.0"                                             | X      | X      | TextDocument           |
| 25  | "StarWriter DOS"                                               | "StarWriter DOS"                                             | X      | -      | TextDocument           |
| 26  | "Text Encoded"                                                 | "Text (encoded)"                                             | X      | X      | TextDocument           |
| 27  | "Unified Office Format text"                                   | "UOF text"                                                   | X      | X      | TextDocument           |
| 28  | "Microsoft Word 95"                                            | "MS Word 95"                                                 | X      | X      | TextDocument           |
| 29  | "Microsoft Word 97/2000/XP"                                    | "MS Word 97"                                                 | X      | X      | TextDocument           |
| 30  | "Hangul WP 97"                                                 | "writer_MIZI_Hwp_97"                                         | X      | -      | TextDocument           |
| 31  | "LaTeX 2e"                                                     | "LaTeX_Writer"                                               | -      | X      | TextDocument           |
| 32  | "Microsoft Excel 5.0 (OpenOffice.org Writer)"                  | "MS Excel 5.0 (StarWriter)"                                  | X      | -      | TextDocument           |
| 33  | "XHTML"                                                        | "XHTML Writer File"                                          | -      | X      | TextDocument           |
| 34  | "Text"                                                         | "Text"                                                       | X      | X      | TextDocument           |
| 35  | "Microsoft Word 2007 XML"                                      | "MS Word 2007 XML"                                           | X      | -      | TextDocument           |
| 36  | "T602 Document"                                                | "T602Document"                                               | X      | -      | TextDocument           |
| 37  | "MediaWiki"                                                    | "MediaWiki"                                                  | -      | X      | TextDocument           |
| 38  | "Microsoft Excel 4.0 (OpenOffice.org Writer)"                  | "MS Excel 4.0 (StarWriter)"                                  | X      | -      | TextDocument           |
| 39  | "DocBook"                                                      | "DocBook File"                                               | X      | X      | TextDocument           |
| 40  | "Microsoft WinWord 5"                                          | "MS WinWord 5"                                               | X      | -      | TextDocument           |
| 41  | "StarWriter 2.0"                                               | "StarWriter 2.0"                                             | X      | -      | TextDocument           |
| 42  | "Help content"                                                 | "writer_web_HTML_help"                                       | X      | -      | WebDocument            |
| 43  | "HTML Document"                                                | "HTML"                                                       | X      | X      | WebDocument            |
| 44  | "StarWriter 3.0 (OpenOffice.org Writer/Web)"                   | "StarWriter 3.0 (StarWriter/Web)"                            | -      | X      | WebDocument            |
| 45  | "StarWriter 4.0 (OpenOffice.org Writer/Web)"                   | "StarWriter 4.0 (StarWriter/Web)"                            | -      | X      | WebDocument            |
| 46  | "StarWriter 5.0 (OpenOffice.org Writer/Web)"                   | "StarWriter 5.0 (StarWriter/Web)"                            | -      | X      | WebDocument            |
| 47  | "StarWriter/Web 4.0 Template"                                  | "StarWriter/Web 4.0 Vorlage/Template"                        | X      | X      | WebDocument            |
| 48  | "StarWriter/Web 5.0 Template"                                  | "StarWriter/Web 5.0 Vorlage/Template"                        | X      | X      | WebDocument            |
| 49  | "Text (OpenOffice.org Writer/Web)"                             | "Text (StarWriter/Web)"                                      | X      | X      | WebDocument            |
| 50  | "MediaWiki"                                                    | "MediaWiki_Web"                                              | -      | X      | WebDocument            |
| 51  | "PDF - Portable Document Format"                               | "writer_web_pdf_Export"                                      | -      | X      | WebDocument            |
| 52  | "OpenOffice.org 1.0 HTML Template"                             | "writer_web_StarOffice_XML_Writer_Web_Template"              | X      | X      | WebDocument            |
| 53  | "OpenOffice.org Text (OpenOffice.org Writer/Web)"              | "writerweb8_writer"                                          | -      | X      | WebDocument            |
| 54  | "HTML Document Template"                                       | "writerweb8_writer_template"                                 | X      | X      | WebDocument            |
| 55  | "Text Encoded (OpenOffice.org Writer/Web)"                     | "Text (encoded) (StarWriter/Web)"                            | X      | X      | WebDocument            |
| 56  | "OpenOffice.org 1.0 Text Document (OpenOffice.org Writer/Web)" | "writer_web_StarOffice_XML_Writer"                           | -      | X      | WebDocument            |
| 57  | "OpenOffice.org 1.0 Master Document"                           | "writer_globaldocument_StarOffice_XML_Writer_GlobalDocument" | X      | X      | GlobalDocument         |
| 58  | "Text Encoded (OpenOffice.org Master Document)"                | "Text (encoded) (StarWriter/GlobalDocument)"                 | X      | X      | GlobalDocument         |
| 59  | "StarWriter 3.0"                                               | "StarWriter 3.0 (StarWriter/GlobalDocument)"                 | -      | X      | GlobalDocument         |
| 60  | "StarWriter 4.0"                                               | "StarWriter 4.0 (StarWriter/GlobalDocument)"                 | -      | X      | GlobalDocument         |
| 61  | "StarWriter 5.0"                                               | "StarWriter 5.0 (StarWriter/GlobalDocument)"                 | -      | X      | GlobalDocument         |
| 62  | "HTML (Writer/Global)"                                         | "writerglobal8_HTML"                                         | -      | X      | GlobalDocument         |
| 63  | "ODF Master Document"                                          | "writerglobal8"                                              | X      | X      | GlobalDocument         |
| 64  | "ODF Text Document"                                            | "writerglobal8_writer"                                       | -      | X      | GlobalDocument         |
| 65  | "StarWriter 4.0 Master Document"                               | "StarWriter 4.0/GlobalDocument"                              | X      | X      | GlobalDocument         |
| 66  | "StarWriter 5.0 Master Document"                               | "StarWriter 5.0/GlobalDocument"                              | X      | X      | GlobalDocument         |
| 67  | "PDF - Portable Document Format"                               | "writer_globaldocument_pdf_Export"                           | -      | X      | GlobalDocument         |
| 68  | "OpenOffice.org 1.0 Text Document"                             | "writer_globaldocument_StarOffice_XML_Writer"                | -      | X      | GlobalDocument         |
| 69  | "Web Page Query (OpenOffice.org Calc)"                         | "calc_HTML_WebQuery"                                         | X      | -      | SpreadsheetDocument    |
| 70  | "OpenOffice.org 1.0 Spreadsheet Template"                      | "calc_StarOffice_XML_Calc_Template"                          | X      | X      | SpreadsheetDocument    |
| 71  | "OpenOffice.org 1.0 Spreadsheet"                               | "StarOffice XML (Calc)"                                      | X      | X      | SpreadsheetDocument    |
| 72  | "Microsoft Excel 2007 XML Template"                            | "Calc MS Excel 2007 XML Template"                            | X      | -      | SpreadsheetDocument    |
| 73  | "Data Interchange Format"                                      | "DIF"                                                        | X      | X      | SpreadsheetDocument    |
| 74  | "Microsoft Excel 97/2000/XP Template"                          | "MS Excel 97 Vorlage/Template"                               | X      | X      | SpreadsheetDocument    |
| 75  | "HTML Document (OpenOffice.org Calc)"                          | "HTML (StarCalc)"                                            | X      | X      | SpreadsheetDocument    |
| 76  | "Microsoft Excel 5.0"                                          | "MS Excel 5.0/95"                                            | X      | X      | SpreadsheetDocument    |
| 77  | "Unified Office Format spreadsheet"                            | "UOF spreadsheet"                                            | X      | X      | SpreadsheetDocument    |
| 78  | "Microsoft Excel 95"                                           | "MS Excel 95"                                                | X      | X      | SpreadsheetDocument    |
| 79  | "StarCalc 4.0"                                                 | "StarCalc 4.0"                                               | X      | X      | SpreadsheetDocument    |
| 80  | "Microsoft Excel 97/2000/XP"                                   | "MS Excel 97"                                                | X      | X      | SpreadsheetDocument    |
| 81  | "Microsoft Excel 4.0 Template"                                 | "MS Excel 4.0 Vorlage/Template"                              | X      | -      | SpreadsheetDocument    |
| 82  | "ODF Spreadsheet Template"                                     | "calc8_template"                                             | X      | X      | SpreadsheetDocument    |
| 83  | "ODF Spreadsheet"                                              | "calc8"                                                      | X      | X      | SpreadsheetDocument    |
| 84  | "Text CSV"                                                     | "Text - txt - csv (StarCalc)"                                | X      | X      | SpreadsheetDocument    |
| 85  | "Rich Text Format (OpenOffice.org Calc)"                       | "Rich Text Format (StarCalc)"                                | X      | -      | SpreadsheetDocument    |
| 86  | "StarCalc 3.0 Template"                                        | "StarCalc 3.0 Vorlage/Template"                              | X      | X      | SpreadsheetDocument    |
| 87  | "StarCalc 4.0 Template"                                        | "StarCalc 4.0 Vorlage/Template"                              | X      | X      | SpreadsheetDocument    |
| 88  | "StarCalc 5.0 Template"                                        | "StarCalc 5.0 Vorlage/Template"                              | X      | X      | SpreadsheetDocument    |
| 89  | "Microsoft Excel 95 Template"                                  | "MS Excel 95 Vorlage/Template"                               | X      | X      | SpreadsheetDocument    |
| 90  | "dBASE"                                                        | "dBase"                                                      | X      | X      | SpreadsheetDocument    |
| 91  | "Microsoft Excel 2003 XML"                                     | "MS Excel 2003 XML"                                          | X      | X      | SpreadsheetDocument    |
| 92  | "Microsoft Excel 2007 XML"                                     | "Calc MS Excel 2007 XML"                                     | X      | -      | SpreadsheetDocument    |
| 93  | "Microsoft Excel 2007 Binary"                                  | "Calc MS Excel 2007 Binary"                                  | X      | -      | SpreadsheetDocument    |
| 94  | "XHTML"                                                        | "XHTML Calc File"                                            | -      | X      | SpreadsheetDocument    |
| 95  | "StarCalc 1.0"                                                 | "StarCalc 1.0"                                               | X      | -      | SpreadsheetDocument    |
| 96  | "Microsoft Excel 5.0 Template"                                 | "MS Excel 5.0/95 Vorlage/Template"                           | X      | X      | SpreadsheetDocument    |
| 97  | "StarCalc 3.0"                                                 | "StarCalc 3.0"                                               | X      | X      | SpreadsheetDocument    |
| 98  | "Quattro Pro 6.0"                                              | "Quattro Pro 6.0"                                            | X      | -      | SpreadsheetDocument    |
| 99  | "StarCalc 5.0"                                                 | "StarCalc 5.0"                                               | X      | X      | SpreadsheetDocument    |
| 100 | "Microsoft Excel 4.0"                                          | "MS Excel 4.0"                                               | X      | -      | SpreadsheetDocument    |
| 101 | "Lotus 1-2-3"                                                  | "Lotus"                                                      | X      | -      | SpreadsheetDocument    |
| 102 | "PDF - Portable Document Format"                               | "calc_pdf_Export"                                            | -      | X      | SpreadsheetDocument    |
| 103 | "SYLK"                                                         | "SYLK"                                                       | X      | X      | SpreadsheetDocument    |
| 104 | "PSD - Adobe Photoshop"                                        | "PSD - Adobe Photoshop"                                      | X      | -      | DrawingDocument        |
| 105 | "PNG - Portable Network Graphic"                               | "draw_png_Export"                                            | -      | X      | DrawingDocument        |
| 106 | "JPEG - Joint Photographic Experts Group"                      | "draw_jpg_Export"                                            | -      | X      | DrawingDocument        |
| 107 | "WMF - Windows Metafile"                                       | "WMF - MS Windows Metafile"                                  | X      | -      | DrawingDocument        |
| 108 | "PPM - Portable Pixelmap"                                      | "draw_ppm_Export"                                            | -      | X      | DrawingDocument        |
| 109 | "ODF Drawing"                                                  | "draw8"                                                      | X      | X      | DrawingDocument        |
| 110 | "RAS - Sun Raster Image"                                       | "RAS - Sun Rasterfile"                                       | X      | -      | DrawingDocument        |
| 111 | "WMF - Windows Metafile"                                       | "draw_wmf_Export"                                            | -      | X      | DrawingDocument        |
| 112 | "XPM - X PixMap"                                               | "XPM"                                                        | X      | -      | DrawingDocument        |
| 113 | "SGV - StarDraw 2.0"                                           | "SGV - StarDraw 2.0"                                         | X      | -      | DrawingDocument        |
| 114 | "PGM - Portable Graymap"                                       | "draw_pgm_Export"                                            | -      | X      | DrawingDocument        |
| 115 | "EMF - Enhanced Metafile"                                      | "EMF - MS Windows Metafile"                                  | X      | -      | DrawingDocument        |
| 116 | "TIFF - Tagged Image File Format"                              | "draw_tif_Export"                                            | -      | X      | DrawingDocument        |
| 117 | "PBM - Portable Bitmap"                                        | "draw_pbm_Export"                                            | -      | X      | DrawingDocument        |
| 118 | "EMF - Enhanced Metafile"                                      | "draw_emf_Export"                                            | -      | X      | DrawingDocument        |
| 119 | "SVM - StarView Metafile"                                      | "SVM - StarView Metafile"                                    | X      | -      | DrawingDocument        |
| 120 | "SGF - StarWriter Graphics Format"                             | "SGF - StarOffice Writer SGF"                                | X      | -      | DrawingDocument        |
| 121 | "JPEG - Joint Photographic Experts Group"                      | "JPG - JPEG"                                                 | X      | -      | DrawingDocument        |
| 122 | "XPM - X PixMap"                                               | "draw_xpm_Export"                                            | -      | X      | DrawingDocument        |
| 123 | "ODF Drawing Template"                                         | "draw8_template"                                             | X      | X      | DrawingDocument        |
| 124 | "BMP - Windows Bitmap"                                         | "draw_bmp_Export"                                            | -      | X      | DrawingDocument        |
| 125 | "PCD - Kodak Photo CD (384x256)"                               | "draw_PCD_Photo_CD_Base4"                                    | X      | -      | DrawingDocument        |
| 126 | "Macromedia Flash (SWF)"                                       | "draw_flash_Export"                                          | -      | X      | DrawingDocument        |
| 127 | "PPM - Portable Pixelmap"                                      | "PPM - Portable Pixelmap"                                    | X      | -      | DrawingDocument        |
| 128 | "DXF - AutoCAD Interchange Format"                             | "DXF - AutoCAD Interchange"                                  | X      | -      | DrawingDocument        |
| 129 | "OpenOffice.org 1.0 Drawing"                                   | "StarOffice XML (Draw)"                                      | X      | X      | DrawingDocument        |
| 130 | "TIFF - Tagged Image File Format"                              | "TIF - Tag Image File"                                       | X      | -      | DrawingDocument        |
| 131 | "PDF - Portable Document Format"                               | "draw_pdf_Export"                                            | -      | X      | DrawingDocument        |
| 132 | "PCX - Zsoft Paintbrush"                                       | "PCX - Zsoft Paintbrush"                                     | X      | -      | DrawingDocument        |
| 133 | "GIF - Graphics Interchange Format"                            | "draw_gif_Export"                                            | -      | X      | DrawingDocument        |
| 134 | "RAS - Sun Raster Image"                                       | "draw_ras_Export"                                            | -      | X      | DrawingDocument        |
| 135 | "BMP - Windows Bitmap"                                         | "BMP - MS Windows"                                           | X      | -      | DrawingDocument        |
| 136 | "EPS - Encapsulated PostScript"                                | "EPS - Encapsulated PostScript"                              | X      | -      | DrawingDocument        |
| 137 | "XBM - X Bitmap"                                               | "XBM - X-Consortium"                                         | X      | -      | DrawingDocument        |
| 138 | "StarDraw 3.0 Template"                                        | "StarDraw 3.0 Vorlage"                                       | X      | X      | DrawingDocument        |
| 139 | "StarDraw 5.0 Template"                                        | "StarDraw 5.0 Vorlage"                                       | X      | X      | DrawingDocument        |
| 140 | "HTML Document (OpenOffice.org Draw)"                          | "draw_html_Export"                                           | -      | X      | DrawingDocument        |
| 141 | "PCD - Kodak Photo CD (192x128)"                               | "draw_PCD_Photo_CD_Base16"                                   | X      | -      | DrawingDocument        |
| 142 | "PCD - Kodak Photo CD (768x512)"                               | "draw_PCD_Photo_CD_Base"                                     | X      | -      | DrawingDocument        |
| 143 | "StarDraw 3.0"                                                 | "StarDraw 3.0"                                               | X      | X      | DrawingDocument        |
| 144 | "PCT - Mac Pict"                                               | "draw_pct_Export"                                            | -      | X      | DrawingDocument        |
| 145 | "PCT - Mac Pict"                                               | "PCT - Mac Pict"                                             | X      | -      | DrawingDocument        |
| 146 | "MET - OS/2 Metafile"                                          | "draw_met_Export"                                            | -      | X      | DrawingDocument        |
| 147 | "StarDraw 5.0"                                                 | "StarDraw 5.0"                                               | X      | X      | DrawingDocument        |
| 148 | "XHTML"                                                        | "XHTML Draw File"                                            | -      | X      | DrawingDocument        |
| 149 | "TGA - Truevision Targa"                                       | "TGA - Truevision TARGA"                                     | X      | -      | DrawingDocument        |
| 150 | "GIF - Graphics Interchange Format"                            | "GIF - Graphics Interchange"                                 | X      | -      | DrawingDocument        |
| 151 | "EPS - Encapsulated PostScript"                                | "draw_eps_Export"                                            | -      | X      | DrawingDocument        |
| 152 | "PGM - Portable Graymap"                                       | "PGM - Portable Graymap"                                     | X      | -      | DrawingDocument        |
| 153 | "SVG - Scalable Vector Graphics"                               | "draw_svg_Export"                                            | -      | X      | DrawingDocument        |
| 154 | "MET - OS/2 Metafile"                                          | "MET - OS/2 Metafile"                                        | X      | -      | DrawingDocument        |
| 155 | "PNG - Portable Network Graphic"                               | "PNG - Portable Network Graphic"                             | X      | -      | DrawingDocument        |
| 156 | "OpenOffice.org 1.0 Drawing Template"                          | "draw_StarOffice_XML_Draw_Template"                          | X      | X      | DrawingDocument        |
| 157 | "PBM - Portable Bitmap"                                        | "PBM - Portable Bitmap"                                      | X      | -      | DrawingDocument        |
| 158 | "SVM - StarView Metafile"                                      | "draw_svm_Export"                                            | -      | X      | DrawingDocument        |
| 159 | "StarDraw 3.0 Template (OpenOffice.org Impress)"               | "StarDraw 3.0 Vorlage (StarImpress)"                         | X      | X      | PresentationDocument   |
| 160 | "StarDraw 5.0 Template (OpenOffice.org Impress)"               | "StarDraw 5.0 Vorlage (StarImpress)"                         | X      | X      | PresentationDocument   |
| 161 | "Microsoft PowerPoint 97/2000/XP Template"                     | "MS PowerPoint 97 Vorlage"                                   | X      | X      | PresentationDocument   |
| 162 | "Unified Office Format presentation"                           | "UOF presentation"                                           | X      | X      | PresentationDocument   |
| 163 | "OpenOffice.org 1.0 Presentation Template"                     | "impress_StarOffice_XML_Impress_Template"                    | X      | X      | PresentationDocument   |
| 164 | "StarImpress 5.0"                                              | "StarImpress 5.0"                                            | X      | X      | PresentationDocument   |
| 165 | "StarImpress 5.0 Packed"                                       | "StarImpress 5.0 (packed)"                                   | X      | -      | PresentationDocument   |
| 166 | "StarDraw 5.0 (OpenOffice.org Impress)"                        | "StarDraw 5.0 (StarImpress)"                                 | X      | X      | PresentationDocument   |
| 167 | "ODF Presentation"                                             | "impress8"                                                   | X      | X      | PresentationDocument   |
| 168 | "XPM - X PixMap"                                               | "impress_xpm_Export"                                         | -      | X      | PresentationDocument   |
| 169 | "WMF - Windows Metafile"                                       | "impress_wmf_Export"                                         | -      | X      | PresentationDocument   |
| 170 | "TIFF - Tagged Image File Format"                              | "impress_tif_Export"                                         | -      | X      | PresentationDocument   |
| 171 | "SVM - StarView Metafile"                                      | "impress_svm_Export"                                         | -      | X      | PresentationDocument   |
| 172 | "SVG - Scalable Vector Graphics"                               | "impress_svg_Export"                                         | -      | X      | PresentationDocument   |
| 173 | "BMP - Windows Bitmap"                                         | "impress_bmp_Export"                                         | -      | X      | PresentationDocument   |
| 174 | "EMF - Enhanced Metafile"                                      | "impress_emf_Export"                                         | -      | X      | PresentationDocument   |
| 175 | "EPS - Encapsulated PostScript"                                | "impress_eps_Export"                                         | -      | X      | PresentationDocument   |
| 176 | "GIF - Graphics Interchange Format"                            | "impress_gif_Export"                                         | -      | X      | PresentationDocument   |
| 177 | "JPEG - Joint Photographic Experts Group"                      | "impress_jpg_Export"                                         | -      | X      | PresentationDocument   |
| 178 | "MET - OS/2 Metafile"                                          | "impress_met_Export"                                         | -      | X      | PresentationDocument   |
| 179 | "PBM - Portable Bitmap"                                        | "impress_pbm_Export"                                         | -      | X      | PresentationDocument   |
| 180 | "PCT - Mac Pict"                                               | "impress_pct_Export"                                         | -      | X      | PresentationDocument   |
| 181 | "PDF - Portable Document Format"                               | "impress_pdf_Export"                                         | -      | X      | PresentationDocument   |
| 182 | "PGM - Portable Graymap"                                       | "impress_pgm_Export"                                         | -      | X      | PresentationDocument   |
| 183 | "PNG - Portable Network Graphic"                               | "impress_png_Export"                                         | -      | X      | PresentationDocument   |
| 184 | "PPM - Portable Pixelmap"                                      | "impress_ppm_Export"                                         | -      | X      | PresentationDocument   |
| 185 | "RAS - Sun Raster Image"                                       | "impress_ras_Export"                                         | -      | X      | PresentationDocument   |
| 186 | "Microsoft PowerPoint 2007 XML Template"                       | "Impress MS PowerPoint 2007 XML Template"                    | X      | -      | PresentationDocument   |
| 187 | "ODF Drawing (Impress)"                                        | "impress8_draw"                                              | X      | X      | PresentationDocument   |
| 188 | "PWP - PlaceWare"                                              | "placeware_Export"                                           | -      | X      | PresentationDocument   |
| 189 | "OpenOffice.org 1.0 Drawing (OpenOffice.org Impress)"          | "impress_StarOffice_XML_Draw"                                | X      | X      | PresentationDocument   |
| 190 | "Microsoft PowerPoint 2007 XML"                                | "Impress MS PowerPoint 2007 XML"                             | X      | -      | PresentationDocument   |
| 191 | "XHTML"                                                        | "XHTML Impress File"                                         | -      | X      | PresentationDocument   |
| 192 | "Macromedia Flash (SWF)"                                       | "impress_flash_Export"                                       | -      | X      | PresentationDocument   |
| 193 | "CGM - Computer Graphics Metafile"                             | "CGM - Computer Graphics Metafile"                           | X      | -      | PresentationDocument   |
| 194 | "Microsoft PowerPoint 97/2000/XP"                              | "MS PowerPoint 97"                                           | X      | X      | PresentationDocument   |
| 195 | "StarImpress 4.0"                                              | "StarImpress 4.0"                                            | X      | X      | PresentationDocument   |
| 196 | "ODF Presentation Template"                                    | "impress8_template"                                          | X      | X      | PresentationDocument   |
| 197 | "StarImpress 4.0 Template"                                     | "StarImpress 4.0 Vorlage"                                    | X      | X      | PresentationDocument   |
| 198 | "StarImpress 5.0 Template"                                     | "StarImpress 5.0 Vorlage"                                    | X      | X      | PresentationDocument   |
| 199 | "OpenOffice.org 1.0 Presentation"                              | "StarOffice XML (Impress)"                                   | X      | X      | PresentationDocument   |
| 200 | "StarDraw 3.0 (OpenOffice.org Impress)"                        | "StarDraw 3.0 (StarImpress)"                                 | X      | X      | PresentationDocument   |
| 201 | "HTML Document (OpenOffice.org Impress)"                       | "impress_html_Export"                                        | -      | X      | PresentationDocument   |
| 202 | "StarMath 3.0"                                                 | "StarMath 3.0"                                               | X      | -      | FormulaProperties      |
| 203 | "StarMath 5.0"                                                 | "StarMath 5.0"                                               | X      | X      | FormulaProperties      |
| 204 | "OpenOffice.org 1.0 Formula"                                   | "StarOffice XML (Math)"                                      | X      | X      | FormulaProperties      |
| 205 | "MathType3.x"                                                  | "MathType 3.x"                                               | X      | X      | FormulaProperties      |
| 206 | "StarMath 2.0"                                                 | "StarMath 2.0"                                               | X      | -      | FormulaProperties      |
| 207 | "StarMath 4.0"                                                 | "StarMath 4.0"                                               | X      | -      | FormulaProperties      |
| 208 | "PDF - Portable Document Format"                               | "math_pdf_Export"                                            | -      | X      | FormulaProperties      |
| 209 | "MathML 1.01"                                                  | "MathML XML (Math)"                                          | X      | X      | FormulaProperties      |
| 210 | "ODF Formula"                                                  | "math8"                                                      | X      | X      | FormulaProperties      |
| 211 | "ODF Database"                                                 | "StarOffice XML (Base)"                                      | X      | X      | OfficeDatabaseDocument |

[PDF exports](http://specs.openoffice.org/appwide/pdf_export/PDFExportDialog.odt) `FilterData` information can be found in the link or bellow:
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

PNG export known `FilterData`:
- `Compression` of type `int` like 5.
- `Interlaced` of type `int` like 0.
- `Translucent` of type `int` like 0.
- `PixelWidth` of type `int` like 816.
- `PixelHeight` of type `int` like 1056.
- `LogicalWidth` of type `int` like 14315.
- `LogicalHeight` of type `int` like 18526.

JPG export known `FilterData`:
- `ColorMode` of type `int` like 1.
- `Quality` of type `int` like 60.
- `PixelWidth` of type `int` like 816.
- `PixelHeight` of type `int` like 1056.
- `LogicalWidth` of type `int` like 14315.
- `LogicalHeight` of type `int` like 18526.

EPUB export known `FilterData`:
- `RVNGDate` of type `string` like "2019-02-21T11:51:44".
- `RVNGLanguage` of type `string` like "fr-FR".
- `RVNGTitle` of type `string` like "title".
- `EPUBVersion` of type `int` like 20.
- `RVNGCoverImage` of type `string` like "file:///tmp/test.png".
- `RVNGIdentifier` of type `string` like "identifier".
- `EPUBLayoutMethod` of type `int` like 1.
- `RVNGInitialCreator` of type `string` like "author".
- `EPUBSplitMethod` of type `int` like 0.
- `RVNGMediaDir` of type `string` like "file:///tmp".

`FilterOptions` for CSV parsing can be found [here](https://wiki.openoffice.org/wiki/Documentation/DevGuide/Spreadsheets/Filter_Options). For example if you want to export the table with comma separator (44 in ascii) and with a double quote as text delimiter (34 in ascii) and with character encoding UTF-8 just use: `44,34,UTF8`.

`FilterOptions` for Text parsing can be used to choose the text encoding, first option select the text encoding and second option select the separator. Example: `IBM_857,CRLF,,,`

Here is an example of different possible generations:
- `"ActionDetail": [{"Type": "GENERATE", "Values": {"Extension": "pdf", "Args": ["FilterData:SelectPdfVersion=1", "FilterData:Zoom=100", "FilterData:UseLosslessCompression=true"]}}`
- `"ActionDetail": [{"Type": "GENERATE", "Values": {"Extension": "csv", "Args": ["FilterName:Text___txt___csv__StarCalc", "FilterOptions:45,34,UTF8"]}}`
- `"ActionDetail": [{"Type": "GENERATE", "Values": {"Extension": "jpg", "Args": ["FilterName:writer_jpg_Export", "FilterData:ColorMode=0", "FilterData:Quality=50"]}}`
- `"ActionDetail": [{"Type": "GENERATE", "Values": {"Extension": "pdf", "Args": ["FilterName:writer_pdf_Export", "FilterData:SelectPdfVersion=1", "FilterData:Zoom=100"]}}`

### jhove
[Jhove](http://jhove.openpreservation.org/) is a java library that can validate different files format.
Example of a jhove griffin can be found [here](cookbook/griffin_referential/griffins-jhove.json), and a preservation scenario can be found [here](cookbook/preservation_referential/preservation-analyse-jhove.json), all can be used with [this SIP](cookbook/sip_test/sip_libreoffice.zip).
jhove griffin can only do `ANALYSE` actions, and a batch example can be found [here](jhove/src/test/resources/batch-reference).

### Imagemagick
[Imagemagick](https://www.imagemagick.org/) is a took that can analyze, generate, or extract data from a picture.
Example of a Imagemagick griffin can be found [here](cookbook/griffin_referential/griffins-imagemagick.json), and a preservation scenario can be found [here](cookbook/preservation_referential/preservation-tranformation-webp.json), all can be used with [this SIP](cookbook/sip_test/sip_images.zip).
Imagemagick griffin can only do `ANALYSE | EXTRACT | GENERATE` actions, and a batch example can be found [here](imagemagick/src/test/resources/vitam-imagemagick-griffin/batch-reference).

## Dependencies
All inner tools dependencies that cannot be get with the usual dependence tool (like yum for centos and apt for debian) are in:
- `deb/` directory for Debian distribution.
- `rpm/` directory for Centos distribution.

## Environment
Griffins can be test in a specific environment and can be deployed separately.
