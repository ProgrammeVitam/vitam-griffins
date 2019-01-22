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
used version 0.8.2
version needed to have the option [-I, --import-filter-name=string set import filter name]
which can be used to specify import filter when needed (even if it's not the case in most cases)

with LibreOffice 6.1.3

has to be adapted to python version installed in libreoffice (python or python3 on first line)

// Export format possible values (taken from unoconv 0.8.2)
// line format is : format name, extension, description, filter name

// TextDocument
                    "bib", "bib", "BibTeX", "BibTeX_Writer"
                    "doc", "doc", "Microsoft Word 97/2000/XP", "MS Word 97"
                    "doc6", "doc", "Microsoft Word 6.0", "MS WinWord 6.0"
                    "doc95", "doc", "Microsoft Word 95", "MS Word 95"
                    "docbook", "xml", "DocBook", "DocBook File"
                    "docx", "docx", "Microsoft Office Open XML", "Office Open XML Text"
                    "docx7", "docx", "Microsoft Office Open XML", "MS Word 2007 XML"
                    "fodt", "fodt", "OpenDocument Text (Flat XML)", "OpenDocument Text Flat XML"
                    "html", "html", "HTML Document (OpenOffice.org Writer)", "HTML (StarWriter)"
                    "latex", "ltx", "LaTeX 2e", "LaTeX_Writer"
                    "mediawiki", "txt", "MediaWiki", "MediaWiki"
                    "odt", "odt", "ODF Text Document", "writer8"
                    "ooxml", "xml", "Microsoft Office Open XML", "MS Word 2003 XML"
                    "ott", "ott", "Open Document Text", "writer8_template"
                    "pdb", "pdb", "AportisDoc (Palm)", "AportisDoc Palm DB"
                    "pdf", "pdf", "Portable Document Format", "writer_pdf_Export"
                    "psw", "psw", "Pocket Word", "PocketWord File"
                    "rtf", "rtf", "Rich Text Format", "Rich Text Format"
                    "sdw", "sdw", "StarWriter 5.0", "StarWriter 5.0"
                    "sdw4", "sdw", "StarWriter 4.0", "StarWriter 4.0"
                    "sdw3", "sdw", "StarWriter 3.0", "StarWriter 3.0"
                    "stw", "stw", "Open Office.org 1.0 Text Document Template", "writer_StarOffice_XML_Writer_Template"
                    "sxw", "sxw", "Open Office.org 1.0 Text Document", "StarOffice XML (Writer)"
                    "text", "txt", "Text Encoded", "Text (encoded)"
                    "txt", "txt", "Text", "Text"
                    "uot", "uot", "Unified Office Format text","UOF text"
                    "vor", "vor", "StarWriter 5.0 Template", "StarWriter 5.0 Vorlage/Template"
                    "vor4", "vor", "StarWriter 4.0 Template", "StarWriter 4.0 Vorlage/Template"
                    "vor3", "vor", "StarWriter 3.0 Template", "StarWriter 3.0 Vorlage/Template"
                    "wps", "wps", "Microsoft Works", "MS_Works"
                    "xhtml", "html", "XHTML Document", "XHTML Writer File"

// WebDocument
                    "etext", "txt", "Text Encoded (OpenOffice.org Writer/Web)", "Text (encoded)"
                    "html10", "html", "OpenOffice.org 1.0 HTML Template", "writer_web_StarOffice_XML_Writer_Web_Template"
                    "html", "html", "HTML Document", "HTML"
                    "html", "html", "HTML Document Template", "writerweb8_writer_template"
                    "mediawiki", "txt", "MediaWiki", "MediaWiki_Web"
                    "pdf", "pdf", "PDF - Portable Document Format", "writer_web_pdf_Export"
                    "sdw3", "sdw", "StarWriter 3.0 (OpenOffice.org Writer/Web)", "StarWriter 3.0 (StarWriter/Web)"
                    "sdw4", "sdw", "StarWriter 4.0 (OpenOffice.org Writer/Web)", "StarWriter 4.0 (StarWriter/Web)"
                    "sdw", "sdw", "StarWriter 5.0 (OpenOffice.org Writer/Web)", "StarWriter 5.0 (StarWriter/Web)"
                    "txt", "txt", "OpenOffice.org Text (OpenOffice.org Writer/Web)", "writerweb8_writer"
                    "text10", "txt", "OpenOffice.org 1.0 Text Document (OpenOffice.org Writer/Web)", "writer_web_StarOffice_XML_Writer"
                    "text", "txt", "Text (OpenOffice.org Writer/Web)", "Text (StarWriter/Web)"
                    "vor4", "vor", "StarWriter/Web 4.0 Template", "StarWriter/Web 4.0 Vorlage/Template"
                    "vor", "vor", "StarWriter/Web 5.0 Template", "StarWriter/Web 5.0 Vorlage/Template"

// Spreadsheet
                    "csv", "csv", "Text CSV", "Text - txt - csv (StarCalc)"
                    "dbf", "dbf", "dBASE", "dBase"
                    "dif", "dif", "Data Interchange Format", "DIF"
                    "fods", "fods", "OpenDocument Spreadsheet (Flat XML)", "OpenDocument Spreadsheet Flat XML"
                    "html", "html", "HTML Document (OpenOffice.org Calc)", "HTML (StarCalc)"
                    "ods", "ods", "ODF Spreadsheet", "calc8"
                    "ooxml", "xml", "Microsoft Excel 2003 XML", "MS Excel 2003 XML"
                    "ots", "ots", "ODF Spreadsheet Template", "calc8_template"
                    "pdf", "pdf", "Portable Document Format", "calc_pdf_Export"
                    "pxl", "pxl", "Pocket Excel", "Pocket Excel"
                    "sdc", "sdc", "StarCalc 5.0", "StarCalc 5.0"
                    "sdc4", "sdc", "StarCalc 4.0", "StarCalc 4.0"
                    "sdc3", "sdc", "StarCalc 3.0", "StarCalc 3.0"
                    "slk", "slk", "SYLK", "SYLK"
                    "stc", "stc", "OpenOffice.org 1.0 Spreadsheet Template", "calc_StarOffice_XML_Calc_Template"
                    "sxc", "sxc", "OpenOffice.org 1.0 Spreadsheet", "StarOffice XML (Calc)"
                    "uos", "uos", "Unified Office Format spreadsheet", "UOF spreadsheet"
                    "vor3", "vor", "StarCalc 3.0 Template", "StarCalc 3.0 Vorlage/Template"
                    "vor4", "vor", "StarCalc 4.0 Template", "StarCalc 4.0 Vorlage/Template"
                    "vor", "vor", "StarCalc 5.0 Template", "StarCalc 5.0 Vorlage/Template"
                    "xhtml", "xhtml", "XHTML", "XHTML Calc File"
                    "xls", "xls", "Microsoft Excel 97/2000/XP", "MS Excel 97"
                    "xls5", "xls", "Microsoft Excel 5.0", "MS Excel 5.0/95"
                    "xls95", "xls", "Microsoft Excel 95", "MS Excel 95"
                    "xlt", "xlt", "Microsoft Excel 97/2000/XP Template", "MS Excel 97 Vorlage/Template"
                    "xlt5", "xlt", "Microsoft Excel 5.0 Template", "MS Excel 5.0/95 Vorlage/Template"
                    "xlt95", "xlt", "Microsoft Excel 95 Template", "MS Excel 95 Vorlage/Template"
                    "xlsx", "xlsx", "Microsoft Excel 2007/2010 XML", "Calc MS Excel 2007 XML"

// Graphics
                    "bmp", "bmp", "Windows Bitmap", "draw_bmp_Export"
                    "emf", "emf", "Enhanced Metafile", "draw_emf_Export"
                    "eps", "eps", "Encapsulated PostScript", "draw_eps_Export"
                    "fodg", "fodg", "OpenDocument Drawing (Flat XML)", "OpenDocument Drawing Flat XML"
                    "gif", "gif", "Graphics Interchange Format", "draw_gif_Export"
                    "html", "html", "HTML Document (OpenOffice.org Draw)", "draw_html_Export"
                    "jpg", "jpg", "Joint Photographic Experts Group", "draw_jpg_Export"
                    "met", "met", "OS/2 Metafile", "draw_met_Export"
                    "odd", "odd", "OpenDocument Drawing", "draw8"
                    "otg", "otg", "OpenDocument Drawing Template", "draw8_template"
                    "pbm", "pbm", "Portable Bitmap", "draw_pbm_Export"
                    "pct", "pct", "Mac Pict", "draw_pct_Export"
                    "pdf", "pdf", "Portable Document Format", "draw_pdf_Export"
                    "pgm", "pgm", "Portable Graymap", "draw_pgm_Export"
                    "png", "png", "Portable Network Graphic", "draw_png_Export"
                    "ppm", "ppm", "Portable Pixelmap", "draw_ppm_Export"
                    "ras", "ras", "Sun Raster Image", "draw_ras_Export"
                    "std", "std", "OpenOffice.org 1.0 Drawing Template", "draw_StarOffice_XML_Draw_Template"
                    "svg", "svg", "Scalable Vector Graphics", "draw_svg_Export"
                    "svm", "svm", "StarView Metafile", "draw_svm_Export"
                    "swf", "swf", "Macromedia Flash (SWF)", "draw_flash_Export"
                    "sxd", "sxd", "OpenOffice.org 1.0 Drawing", "StarOffice XML (Draw)"
                    "sxd3", "sxd", "StarDraw 3.0", "StarDraw 3.0"
                    "sxd5", "sxd", "StarDraw 5.0", "StarDraw 5.0"
                    "sxw", "sxw", "StarOffice XML (Draw)", "StarOffice XML (Draw)"
                    "tiff", "tiff", "Tagged Image File Format", "draw_tif_Export"
                    "vor", "vor", "StarDraw 5.0 Template", "StarDraw 5.0 Vorlage"
                    "vor3", "vor", "StarDraw 3.0 Template", "StarDraw 3.0 Vorlage"
                    "wmf", "wmf", "Windows Metafile", "draw_wmf_Export"
                    "xhtml", "xhtml", "XHTML", "XHTML Draw File"
                    "xpm", "xpm", "X PixMap", "draw_xpm_Export"

// Presentation
                    "bmp", "bmp", "Windows Bitmap", "impress_bmp_Export"
                    "emf", "emf", "Enhanced Metafile", "impress_emf_Export"
                    "eps", "eps", "Encapsulated PostScript", "impress_eps_Export"
                    "fodp", "fodp", "OpenDocument Presentation (Flat XML)", "OpenDocument Presentation Flat XML"
                    "gif", "gif", "Graphics Interchange Format", "impress_gif_Export"
                    "html", "html", "HTML Document (OpenOffice.org Impress)", "impress_html_Export"
                    "jpg", "jpg", "Joint Photographic Experts Group", "impress_jpg_Export"
                    "met", "met", "OS/2 Metafile", "impress_met_Export"
                    "odg", "odg", "ODF Drawing (Impress)", "impress8_draw"
                    "odp", "odp", "ODF Presentation", "impress8"
                    "otp", "otp", "ODF Presentation Template", "impress8_template"
                    "pbm", "pbm", "Portable Bitmap", "impress_pbm_Export"
                    "pct", "pct", "Mac Pict", "impress_pct_Export"
                    "pdf", "pdf", "Portable Document Format", "impress_pdf_Export"
                    "pgm", "pgm", "Portable Graymap", "impress_pgm_Export"
                    "png", "png", "Portable Network Graphic", "impress_png_Export"
                    "potm", "potm", "Microsoft PowerPoint 2007/2010 XML Template", "Impress MS PowerPoint 2007 XML Template"
                    "pot", "pot", "Microsoft PowerPoint 97/2000/XP Template", "MS PowerPoint 97 Vorlage"
                    "ppm", "ppm", "Portable Pixelmap", "impress_ppm_Export"
                    "pptx", "pptx", "Microsoft PowerPoint 2007/2010 XML", "Impress MS PowerPoint 2007 XML"
                    "pps", "pps", "Microsoft PowerPoint 97/2000/XP (Autoplay)", "MS PowerPoint 97 Autoplay"
                    "ppt", "ppt", "Microsoft PowerPoint 97/2000/XP", "MS PowerPoint 97"
                    "pwp", "pwp", "PlaceWare", "placeware_Export"
                    "ras", "ras", "Sun Raster Image", "impress_ras_Export"
                    "sda", "sda", "StarDraw 5.0 (OpenOffice.org Impress)", "StarDraw 5.0 (StarImpress)"
                    "sdd", "sdd", "StarImpress 5.0", "StarImpress 5.0"
                    "sdd3", "sdd", "StarDraw 3.0 (OpenOffice.org Impress)", "StarDraw 3.0 (StarImpress)"
                    "sdd4", "sdd", "StarImpress 4.0", "StarImpress 4.0"
                    "sxd", "sxd", "OpenOffice.org 1.0 Drawing (OpenOffice.org Impress)", "impress_StarOffice_XML_Draw"
                    "sti", "sti", "OpenOffice.org 1.0 Presentation Template", "impress_StarOffice_XML_Impress_Template"
                    "svg", "svg", "Scalable Vector Graphics", "impress_svg_Export"
                    "svm", "svm", "StarView Metafile", "impress_svm_Export"
                    "swf", "swf", "Macromedia Flash (SWF)", "impress_flash_Export"
                    "sxi", "sxi", "OpenOffice.org 1.0 Presentation", "StarOffice XML (Impress)"
                    "tiff", "tiff", "Tagged Image File Format", "impress_tif_Export"
                    "uop", "uop", "Unified Office Format presentation", "UOF presentation"
                    "vor", "vor", "StarImpress 5.0 Template", "StarImpress 5.0 Vorlage"
                    "vor3", "vor", "StarDraw 3.0 Template (OpenOffice.org Impress)", "StarDraw 3.0 Vorlage (StarImpress)"
                    "vor4", "vor", "StarImpress 4.0 Template", "StarImpress 4.0 Vorlage"
                    "vor5", "vor", "StarDraw 5.0 Template (OpenOffice.org Impress)", "StarDraw 5.0 Vorlage (StarImpress)"
                    "wmf", "wmf", "Windows Metafile", "impress_wmf_Export"
                    "xhtml", "xml", "XHTML", "XHTML Impress File"
                    "xpm", "xpm", "X PixMap", "impress_xpm_Export"))

