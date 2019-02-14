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

Notes
-----
[List Filters available](https://github.com/LibreOffice/core/tree/master/filter/source/config/fragments/filters)

PDF EXPORT
----------
[PDF exports](http://specs.openoffice.org/appwide/pdf_export/PDFExportDialog.odt)

- "UseLosslessCompression" oor:type="xs:boolean" default: false: Specifies if graphics are exported to PDF using a lossless compression eg. PNG or if they are compressed using the JPEG format.
- "Quality" oor:type="xs:int" default: 90: Specifies quality of the JPG export. A higher value results in higher quality and file size.
- "ReduceImageResolution" oor:type="xs:boolean" default: false : Specifies if the resolution of each image is reduced to the resolution specified by the property MaxImageResolution.
- "MaxImageResolution" oor:type="xs:int" default: 300: If the property ReduceImageResolution is set to true all images will be reduced to the given value in DPI.
- "UseTaggedPDF" oor:type="xs:boolean" default: false: Determines if PDF are created by using special tags also known as Tagged PDF.
- "ExportNotes" oor:type="xs:boolean" default:false Specifies if notes are exported to PDF.
- "ExportNotesPages" oor:type="xs:boolean" default:false: Specifies if notes pages are exported to PDF. (Notes pages are available in Impress documents only).
- "ExportBookmarks" oor:type="xs:boolean" default:true Specifies if bookmarks are exported to PDF.
- "OpenBookmarkLevels" oor:type="xs:int" default: -1: Specifies how many bookmark levels should be opened in the reader application when the PDF gets opened. -1 means all levels, non-negative numbers mean the respective number of levels.1
- "UseTransitionEffects" oor:type="xs:boolean" default: true: Specifies slide transitions are exported to PDF. This option is active only if storing Impress documents.
- "ExportFormFields" oor:type="xs:boolean" default:true Specifies whether form fields are exported as widgets or only their fixed print representation is exported.
- "FormsType" oor:type="xs:int" default: 0: Specifies the submitted format of a PDF form. Values are: "0" for  FDF format,"1" for PDF format, "2" for HTML format, "3" for XML format
- "IsSkipEmptyPages" oor:type="xs:boolean" default: false: Specifies that automatically inserted empty pages are suppressed. This option is active only if storing Writer documents.
- "HideViewerMenubar" oor:type="xs:boolean" default: false: Specifies whether to hide the PDF viewer menubar when the document is active.
- "HideViewerToolbar" oor:type="xs:boolean" default: false: Specifies whether to hide the PDF viewer toolbar when the document is active.
- "HideViewerWindowControls" oor:type="xs:boolean" default: false: Specifies whether to hide the PDF viewer controls when the document is active.
- "ResizeWindowToInitialPage" oor:type="xs:boolean" default: false: Specifies that the PDF viewer window is opened full screen when the document is opened.
- "CenterWindow" oor:type="xs:boolean" default: false: Specifies that the PDF viewer window is centered to the screen when the PDF document is opened.
- "OpenInFullScreenMode" oor:type="xs:boolean" default: false: Specifies that the PDF viewer window is opened full screen, on top of all windows.
- "DisplayPDFDocumentTitle" oor:type="xs:boolean" default: true: Specifies that the title of the document, if present in the document properties, is displayed in the PDF viewer window title bar.
- "FirstPageOnLeft" oor:type="xs:boolean" default: false: Used with the value 3 of the PageLayout property when the first page (odd) should be on the left side of the screen.
- "InitialView" oor:type="xs:int" default: 0: Specifies how the PDF document should be displayed when opened. "0" meaning neither outlines or thumbnails, "1" meaning the document is opened with outline pane opened, "2" meaning the document is opened with thumbnail pane opened
- "Magnification" oor:type="xs:int" default: 0: Specifies the action to be performed when the PDF document is opened, "0" meaning opens with default zoom magnification, "1" meaning opens magnified to fit the entire page within the window, "2" meaning opens magnified to fit the entire page width within the window, "3" meaning opens magnified to fit the entire width of its boundig box within the window (cuts out margins), “4” means with a zoom level given in the “Zoom” property.
- “Zoom” oor:type=”xs:int” default: 100: Specifies the zoom level a PDF document is opened with. Only valid if "Magnification" is set to "4".
- "PageLayout" oor:type="xs:int" default: 0: Specifies the page layout to be used when the document is opened, "0" meaning display the pages according to the reader configuration, "1" meaning display one page at a time, "2" meaning display the pages in one column, "3" meaning display the pages in two columns odd pages on the right, to have the odd pages on the left the FirstPageOnLeft properties should be used as well.
- "InitialPage" oor:type="xs:int" default: 1: Specifies the page on which a PDF document should be opened in the viewer application.
- "SelectPdfVersion" oor:type="xs:int" default: 0: Specifies the version of the pdf to use.