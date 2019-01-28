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
output_file_extension,Media,Filters (<filter> / output_filter_name),UI Name,Package Name,Import,Export
txt,,Text (encoded) (StarWriter/GlobalDocument),Text - Choose Encoding (Master Document),Filter1,✓,✓
,,writer_globaldocument_StarOffice_XML_Writer,OpenOffice.org 1.0 Text Document,Filter1,,
,,writer_globaldocument_StarOffice_XML_Writer_GlobalDocument,OpenOffice.org 1.0 Master Document,Filter1,✓,
,,writer_globaldocument_pdf_Export,PDF - Portable Document Format,Filter1,,✓
,,writerglobal8,ODF Master Document,Filter1,✓,✓
,,writerglobal8_template,ODF Master Document Template,Filter1,✓,✓
,,writerglobal8_writer,ODF Text Document,Filter1,,✓
,,writerglobal8_HTML,HTML (Writer/Global),Filter1,,✓
csv,text/plain,generic_Text,Text,Types1,,
txt,text/plain,generic_Text,Text,Types1,,
tab,text/plain,generic_Text,Text,Types1,,
tsv,text/plain,generic_Text,Text,Types1,,
sxw,application/vnd.sun.xml.writer,writer_StarOffice_XML_Writer,OpenOffice.org 1.0 Text Document,Types1,,
sxg,application/vnd.sun.xml.writer.global,writer_globaldocument_StarOffice_XML_Writer_GlobalDocument,Writer 6.0 Master Document,Types1,,
pdf,application/pdf,pdf_Portable_Document_Format,PDF - Portable Document Format,Types1,,
odm,application/vnd.oasis.opendocument.text-master,writerglobal8,Writer 8 Master Document,Types1,,
otm,application/vnd.oasis.opendocument.text-master-template,writerglobal8_template,Writer 8 Master Document Template,Types1,,
,,HTML,HTML Document,Filter2,✓,✓
,,Text (StarWriter/Web),Text (Writer/Web),Filter2,✓,✓
txt,,Text (encoded) (StarWriter/Web),Text - Choose Encoding (Writer/Web),Filter2,✓,✓
,,writer_web_HTML_help,Help content,Filter2,✓,
,,writer_web_StarOffice_XML_Writer,OpenOffice.org 1.0 Text Document (Writer/Web),Filter2,,✓
,,writer_web_StarOffice_XML_Writer_Web_Template,OpenOffice.org 1.0 HTML Template,Filter2,✓,
,,writer_web_pdf_Export,PDF - Portable Document Format,Filter2,,✓
,,writerweb8_writer_template,HTML Document Template,Filter2,✓,✓
,,writerweb8_writer,Text (Writer/Web),Filter2,,✓
html,text/html,generic_HTML,HTML Document,Types2,,
htm,text/html,generic_HTML,HTML Document,Types2,,
csv,text/plain,generic_Text,Text,Types2,,
txt,text/plain,generic_Text,Text,Types2,,
tab,text/plain,generic_Text,Text,Types2,,
tsv,text/plain,generic_Text,Text,Types2,,
,,writer_web_HTML_help,Help content,Types2,,
sxw,application/vnd.sun.xml.writer,writer_StarOffice_XML_Writer,OpenOffice.org 1.0 Text Document,Types2,,
stw,application/vnd.sun.xml.writer.web,writer_web_StarOffice_XML_Writer_Web_Template,Writer/Web 6.0 Template,Types2,,
pdf,application/pdf,pdf_Portable_Document_Format,PDF - Portable Document Format,Types2,,
oth,application/vnd.oasis.opendocument.text-web,writerweb8_writer_template,Writer/Web 8 Template,Types2,,
,,HTML (StarWriter),HTML Document (Writer),Filter3,✓,✓
,,MS WinWord 5,Microsoft WinWord 1/2/5,Filter3,✓,
,,MS WinWord 6.0,Microsoft Word 6.0,Filter3,✓,
,,MS Word 95,Microsoft Word 95,Filter3,✓,
,,MS Word 95 Vorlage,Microsoft Word 95 Template,Filter3,✓,
,,MS Word 97,Microsoft Word 97-2003,Filter3,✓,✓
,,MS Word 97 Vorlage,Microsoft Word 97-2003 Template,Filter3,✓,✓
,,OpenDocument Text Flat XML,Flat XML ODF Text Document,Filter3,✓,✓
,,Rich Text Format,Rich Text,Filter3,✓,✓
,,StarOffice XML (Writer),OpenOffice.org 1.0 Text Document,Filter3,✓,
,,WordPerfect,WordPerfect Document,Filter3,✓,
,,MS_Works,Microsoft Works Document,Filter3,✓,
,,Beagle_Works,BeagleWorks/WordPerfect Works v1 Text Document,Filter3,✓,
,,ClarisWorks,ClarisWorks/AppleWorks Text Document,Filter3,✓,
,,DocMaker,DOCMaker (v4) Document,Filter3,✓,
,,eDoc_Document,eDOC (v2) Document,Filter3,✓,
,,FullWrite_Professional,FullWrite Professional Document,Filter3,✓,
,,Great_Works,GreatWorks Text Document,Filter3,✓,
,,HanMac_Word_J,HanMac Word-J Document,Filter3,✓,
,,HanMac_Word_K,HanMac Word-K Document,Filter3,✓,
,,LightWayText,LightWayText for Mac v4.5,Filter3,✓,
,,Mac_Acta,Acta Mac Classic Document,Filter3,✓,
,,Mac_More,More Mac v2-3 Document,Filter3,✓,
,,Mac_RagTime,RagTime Mac v2-3 Document,Filter3,✓,
,,Mac_Word,Microsoft Word for Mac (v1 - v5),Filter3,✓,
,,Mac_Works,Microsoft Works for Mac Text Document (v1 - v4),Filter3,✓,
,,MacDoc,MacDoc v1 Document,Filter3,✓,
,,MacWrite,MacWrite Document,Filter3,✓,
,,MacWritePro,MacWriteII or MacWritePro Document,Filter3,✓,
,,Mariner_Write,Mariner Write Mac Classic v1.6 - v3.5,Filter3,✓,
,,MindWrite,MindWrite Document,Filter3,✓,
,,Nisus_Writer,Nisus Writer Mac Classic v3.4 - 6.5,Filter3,✓,
,,TeachText,TeachText/SimpleText v1 Document,Filter3,✓,
,,TexEdit,Tex-Edit v2 Document,Filter3,✓,
,,WriteNow,WriteNow Document,Filter3,✓,
,,WriterPlus,WriterPlus Document,Filter3,✓,
,,ZWrite,Z-Write 1.3 Document,Filter3,✓,
,,AbiWord,AbiWord Document,Filter3,✓,
,,T602Document,T602 Document,Filter3,✓,
,,LotusWordPro,Lotus WordPro Document,Filter3,✓,
txt,,Text,Text,Filter3,✓,✓
txt,,Text (encoded),Text - Choose Encoding,Filter3,✓,✓
,,writer_MIZI_Hwp_97,Hangul WP 97,Filter3,✓,
,,writer_StarOffice_XML_Writer_Template,OpenOffice.org 1.0 Text Document Template,Filter3,✓,
,,writer_pdf_Export,PDF - Portable Document Format,Filter3,,✓
,,writer8,ODF Text Document,Filter3,✓,✓
,,writer8_template,ODF Text Document Template,Filter3,✓,✓
,,MS Word 2007 XML,Microsoft Word 2007-2013 XML,Filter3,✓,✓
,,MS Word 2007 XML Template,Microsoft Word 2007-2013 XML Template,Filter3,✓,
,,Office Open XML Text,Office Open XML Text,Filter3,✓,✓
,,Office Open XML Text Template,Office Open XML Text Template,Filter3,✓,
,,writer_layout_dump,Writer Layout XML,Filter3,,✓
,,BroadBand eBook,writer_BroadBand_eBook,Filter3,✓,
,,eReader eBook,eReader eBook,Filter3,✓,
,,FictionBook 2,FictionBook 2.0,Filter3,✓,
,,PalmDoc,PalmDoc eBook,Filter3,✓,
,,Plucker eBook,Plucker eBook,Filter3,✓,
,,TealDoc,TealDoc eBook,Filter3,✓,
,,zTXT,zTXT eBook,Filter3,✓,
,,Apple Pages,Apple Pages 4,Filter3,✓,
htm,text/html,generic_HTML,HTML Document,Types3,,
html,text/html,generic_HTML,HTML Document,Types3,,
xls,application/vnd.ms-excel,calc_MS_Excel_40,Microsoft Excel 4.0,Types3,,
xlw,application/vnd.ms-excel,calc_MS_Excel_40,Microsoft Excel 4.0,Types3,,
xlc,application/vnd.ms-excel,calc_MS_Excel_40,Microsoft Excel 4.0,Types3,,
xlm,application/vnd.ms-excel,calc_MS_Excel_40,Microsoft Excel 4.0,Types3,,
xls,application/vnd.ms-excel,calc_MS_Excel_5095,Microsoft Excel 5.0,Types3,,
xlc,application/vnd.ms-excel,calc_MS_Excel_5095,Microsoft Excel 5.0,Types3,,
xlm,application/vnd.ms-excel,calc_MS_Excel_5095,Microsoft Excel 5.0,Types3,,
xlw,application/vnd.ms-excel,calc_MS_Excel_5095,Microsoft Excel 5.0,Types3,,
xls,application/vnd.ms-excel,calc_MS_Excel_95,Microsoft Excel 95,Types3,,
xlc,application/vnd.ms-excel,calc_MS_Excel_95,Microsoft Excel 95,Types3,,
xlm,application/vnd.ms-excel,calc_MS_Excel_95,Microsoft Excel 95,Types3,,
xlw,application/vnd.ms-excel,calc_MS_Excel_95,Microsoft Excel 95,Types3,,
doc,application/msword,writer_MS_WinWord_5,Microsoft WinWord 1/2/5,Types3,,
doc,application/msword,writer_MS_WinWord_60,Microsoft Word 6.0,Types3,,
doc,application/msword,writer_MS_Word_95,Microsoft Word 95,Types3,,
dot,application/msword,writer_MS_Word_95_Vorlage,MS Word 95 Template,Types3,,
doc,application/msword,writer_MS_Word_97,Microsoft Word 97-2003,Types3,,
dot,application/msword,writer_MS_Word_97_Vorlage,MS Word 97/2000 Template,Types3,,
fodt,application/vnd.oasis.opendocument.text-flat-xml,writer_ODT_FlatXML,OpenDocument Text (Flat XML),Types3,,
odt,application/vnd.oasis.opendocument.text-flat-xml,writer_ODT_FlatXML,OpenDocument Text (Flat XML),Types3,,
xml,application/vnd.oasis.opendocument.text-flat-xml,writer_ODT_FlatXML,OpenDocument Text (Flat XML),Types3,,
rtf,application/rtf,writer_Rich_Text_Format,Rich Text Format,Types3,,
sxw,application/vnd.sun.xml.writer,writer_StarOffice_XML_Writer,OpenOffice.org 1.0 Text Document,Types3,,
wpd,application/vnd.wordperfect,writer_WordPerfect_Document,WordPerfect Document,Types3,,
wps,application/vnd.ms-works,writer_MS_Works_Document,Microsoft Works Document,Types3,,
-,-,writer_Beagle_Works,BeagleWorks/WordPerfect Works Document,Types3,,
cwk,application/clarisworks,writer_ClarisWorks,ClarisWorks/AppleWorks Document,Types3,,
hqx,-,writer_DocMaker,DOCMaker (v4) Document,Types3,,
zip,-,writer_DocMaker,DOCMaker (v4) Document,Types3,,
hqx,-,writer_eDoc_Document,eDOC (v2) Document,Types3,,
zip,-,writer_eDoc_Document,eDOC (v2) Document,Types3,,
-,-,writer_FullWrite_Professional,FullWrite Professional Document,Types3,,
-,-,writer_Great_Works,GreatWorks Document,Types3,,
-,-,writer_HanMac_Word_J,HanMac Word-J Document,Types3,,
-,-,writer_HanMac_Word_K,HanMac Word-K Document,Types3,,
hqx,-,writer_LightWayText,LightWayText for Mac v4.5,Types3,,
zip,-,writer_LightWayText,LightWayText for Mac v4.5,Types3,,
-,-,writer_Mac_Acta,Acta Mac v1-2 Document,Types3,,
-,-,writer_Mac_More,More Mac v2-3 Document,Types3,,
-,-,writer_Mac_RagTime,RagTime Mac v2-3 Document,Types3,,
doc,application/msword,writer_Mac_Word,Microsoft Word for Mac (v1 - v5),Types3,,
wps,application/vnd.ms-works,writer_Mac_Works,Microsoft Works for Mac Document (v1 - v4),Types3,,
hqx,-,writer_MacDoc,MacDoc,Types3,,
zip,-,writer_MacDoc,MacDoc,Types3,,
mw,application/macwriteii,writer_MacWrite,MacWrite Document,Types3,,
mcw,application/macwriteii,writer_MacWrite,MacWrite Document,Types3,,
mw,application/macwriteii,writer_MacWritePro,MacWriteII or MacWritePro Document,Types3,,
mcw,application/macwriteii,writer_MacWritePro,MacWriteII or MacWritePro Document,Types3,,
mwd,-,writer_Mariner_Write,Mariner Write Mac Classic v1.6 - v3.5,Types3,,
-,-,writer_MindWrite,MindWrite Document,Types3,,
hqx,-,writer_Nisus_Writer,Nisus Writer Mac Classic v3.4 - 6.5,Types3,,
zip,-,writer_Nisus_Writer,Nisus Writer Mac Classic v3.4 - 6.5,Types3,,
hqx,-,writer_TeachText,TeachText/SimpleText v1 Document,Types3,,
zip,-,writer_TeachText,TeachText/SimpleText v1 Document,Types3,,
hqx,-,writer_TexEdit,Tex-Edit v2 Document,Types3,,
zip,-,writer_TexEdit,Tex-Edit v2 Document,Types3,,
wn,-,writer_WriteNow,WriteNow Document,Types3,,
nx^d,-,writer_WriteNow,WriteNow Document,Types3,,
-,-,writer_WriterPlus,Writer Plus Document,Types3,,
hqx,-,writer_ZWrite,Z-Write 1.3 Document,Types3,,
zip,-,writer_ZWrite,Z-Write 1.3 Document,Types3,,
abw,application/x-abiword,writer_AbiWord_Document,AbiWord Document,Types3,,
zabw,application/x-abiword,writer_AbiWord_Document,AbiWord Document,Types3,,
‘602,application/x-t602,writer_T602_Document,T602 Document,Types3,,
txt,application/x-t602,writer_T602_Document,T602 Document,Types3,,
lwp,application/vnd.lotus-wordpro,writer_LotusWordPro_Document,LotusWordPro Document,Types3,,
csv,text/plain,generic_Text,Text,Types3,,
tsv,text/plain,generic_Text,Text,Types3,,
tab,text/plain,generic_Text,Text,Types3,,
txt,text/plain,generic_Text,Text,Types3,,
hwp,application/x-hwp,writer_MIZI_Hwp_97,Hangul WP 97,Types3,,
stw,application/vnd.sun.xml.writer.template,writer_StarOffice_XML_Writer_Template,Writer 6.0 Template,Types3,,
pdf,application/pdf,pdf_Portable_Document_Format,PDF - Portable Document Format,Types3,,
ott,application/vnd.oasis.opendocument.text-template,writer8_template,Writer 8 Template,Types3,,
odt,application/vnd.oasis.opendocument.text,writer8,Writer 8,Types3,,
docx,application/msword,writer_MS_Word_2007,Microsoft Word 2007-2013 XML,Types3,,
docm,application/msword,writer_MS_Word_2007,Microsoft Word 2007-2013 XML,Types3,,
dotx,application/msword,writer_MS_Word_2007_Template,Microsoft Word 2007-2013 XML Template,Types3,,
dotm,application/msword,writer_MS_Word_2007_Template,Microsoft Word 2007-2013 XML Template,Types3,,
docx,application/vnd.openxmlformats-officedocument.wordprocessingml.document,writer_OOXML,Office Open XML Text Document,Types3,,
docm,application/vnd.openxmlformats-officedocument.wordprocessingml.document,writer_OOXML,Office Open XML Text Document,Types3,,
dotx,application/vnd.openxmlformats-officedocument.wordprocessingml.template,writer_OOXML_Text_Template,Office Open XML Text Template,Types3,,
dotm,application/vnd.openxmlformats-officedocument.wordprocessingml.template,writer_OOXML_Text_Template,Office Open XML Text Template,Types3,,
xml,-,writer_layout_dump_xml,Writer Layout Dump,Types3,,
lrf,application/x-sony-bbeb,writer_BroadBand_eBook,BroadBand eBook,Types3,,
pdb,application/vnd.palm,writer_eReader_eBook,eReader eBook,Types3,,
fb2,application/x-fictionbook+xml,writer_FictionBook_2,FictionBook 2.0,Types3,,
zip,application/x-fictionbook+xml,writer_FictionBook_2,FictionBook 2.0,Types3,,
pdb,application/x-aportisdoc,writer_PalmDoc,PalmDoc eBook,Types3,,
pdb,application/prs.plucker,writer_Plucker_eBook,Plucker eBook,Types3,,
pdb,application/vnd.palm,writer_TealDoc,TealDoc eBook,Types3,,
pdb,application/vnd.palm,writer_zTXT,zTXT eBook,Types3,,
pages,application/x-iwork-pages-sffpages,writer_ApplePages,Apple Pages,Types3,,


