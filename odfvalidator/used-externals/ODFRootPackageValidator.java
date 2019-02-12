/**
 * **********************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***********************************************************************
 */
package org.odftoolkit.odfvalidator;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipException;
import javax.xml.validation.Validator;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.OdfSchemaConstraint;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfPackageConstraint;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.odfdom.pkg.ValidationConstraint;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

abstract class ODFRootPackageValidator extends ODFPackageValidator implements ManifestEntryListener {

    private OdfPackage m_aPkg = null;
    private ArrayList<ManifestEntry> m_aSubDocs = null;
    private ODFPackageErrorHandler m_ErrorHandler = null;

    protected ODFRootPackageValidator(Logger.LogLevel nLogLevel, OdfValidatorMode eMode, OdfVersion aVersion, SAXParseExceptionFilter aFilter, ODFValidatorProvider aValidatorProvider) {
        super(nLogLevel, eMode, aVersion, aFilter, aValidatorProvider);
    }

    protected abstract OdfPackage getPackage(ErrorHandler handler) throws Exception;

    protected OdfPackage getPackage(Logger aLogger) {
        if (m_aPkg == null) {
            try {
                m_ErrorHandler = new ODFPackageErrorHandler();
                m_aPkg = getPackage(m_ErrorHandler);
                // for additional mimetype checking, load root document
                try {
                    OdfDocument.loadDocument(m_aPkg, "");
                } catch (Exception e) {
                    // ignore -- the interesting stuff is passed to handler
                }
            } catch (IOException e) {
                if (e.getMessage().startsWith("only DEFLATED entries can have EXT descriptor")) {
                    aLogger.logFatalError("The document is encrypted. Validation of encrypted documents is not supported.");
                } else {
                    aLogger.logFatalError(e.getMessage());
                }
            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                aLogger.logFatalError(e.getMessage() + "\n" + errors.toString());
            }
        }
        return m_aPkg;
    }

    protected String getStreamName(String aEntry) {
        return aEntry;
    }

    protected ODFDetailedResult detailedValidate(PrintStream aOut) throws ODFValidatorException {
        Logger aLogger = new Logger(getLoggerName(), getDocumentPath(), aOut, m_nLogLevel);
        return _detailedValidate(aLogger);
    }

    private boolean PrimeAnalyseErrorHandler(OdfVersion aVersion) {
        int errors = 0;

        for (SAXParseException e : m_ErrorHandler.m_aExceptionList) {
            if (e instanceof OdfValidationException) {
                ValidationConstraint aConstraint = ((OdfValidationException) e).getConstraint();
                if (aConstraint instanceof OdfPackageConstraint) {
                    switch (((OdfPackageConstraint) aConstraint)) {
                        case MANIFEST_DOES_NOT_LIST_FILE:
                            switch (aVersion) {
                                case V1_0:
                                case V1_1:
                                    break;
                                default:
                                    errors++;
                                    break;
                            }
                            break;
                        case MIMETYPE_NOT_FIRST_IN_PACKAGE:
                        case MIMETYPE_NOT_IN_PACKAGE:
                            switch (aVersion) {
                                case V1_0:
                                case V1_1:
                                    break;
                                default:
                                    errors++;
                                    break;
                            }
                            break;
                        case MANIFEST_NOT_IN_PACKAGE:
                        case MANIFEST_LISTS_NONEXISTENT_FILE:
                            errors++;
                            break;
                        case MIMETYPE_IS_COMPRESSED:
                        case MIMETYPE_HAS_EXTRA_FIELD:
                        case MIMETYPE_DIFFERS_FROM_PACKAGE:
                            errors++;
                            break;
                        case MANIFEST_LISTS_DIRECTORY:
                        case MANIFEST_DOES_NOT_LIST_DIRECTORY:
                            break;
                        default:
                            errors++;
                            break;
                    }
                } else if (aConstraint instanceof OdfSchemaConstraint) {
                    switch (((OdfSchemaConstraint) aConstraint)) {
                        case DOCUMENT_WITHOUT_ODF_MIMETYPE:
                        case DOCUMENT_WITHOUT_CONTENT_NOR_STYLES_XML:
                            errors++;
                            break;
                        default:
                            errors++;
                            break;
                    }
                }
            } else {
                errors++;
            }
        }

        return errors>10;
    }

    private ODFDetailedResult _detailedValidate(Logger aLogger) throws ODFValidatorException {
        ODFDetailedResult result = new ODFDetailedResult();

        OdfPackage aPkg = getPackage(aLogger);
        if (aPkg == null) {
            result.m_bfHasErrors = ODFDetailedResult.PRIME_STEP_ERROR;
            return result;
        }

        try {
            String aDocVersion = getVersion(aLogger);
            result.m_DocVersion = aDocVersion;
            if (aDocVersion != null) {
                aLogger.logInfo("ODF version of root document: " + aDocVersion, false);
                mOdfPackageVersion = OdfVersion.valueOf(aDocVersion, true);
            }
            OdfVersion aVersion = m_aConfigVersion == null ? OdfVersion.valueOf(aDocVersion, true) : m_aConfigVersion;

            result.m_bfHasErrors = (PrimeAnalyseErrorHandler(aVersion)?ODFDetailedResult.PRIME_STEP_ERROR:0);

            result.m_bfHasErrors |= (validatePre(aLogger, aVersion) ? ODFDetailedResult.PRE_STEP_ERROR : 0);
            aLogger.logInfo("Media Type: " + m_aResult.getMediaType(), false);

            result.m_bfHasErrors |= (validateMeta(aLogger, getStreamName(OdfDocument.OdfXMLFile.META.getFileName()),
                    aVersion, true) ? ODFDetailedResult.META_STEP_ERROR : 0);
            result.m_bfHasErrors |= (validateEntry(aLogger,
                    getStreamName(OdfDocument.OdfXMLFile.SETTINGS.getFileName()), DOCUMENT_SETTINGS, aVersion)
                    ? ODFDetailedResult.SETTINGS_STEP_ERROR : 0);
            result.m_bfHasErrors |= (validateEntry(aLogger, getStreamName(OdfDocument.OdfXMLFile.STYLES.getFileName()),
                    DOCUMENT_STYLES, aVersion) ? ODFDetailedResult.STYLES_STEP_ERROR : 0);
            if (m_aResult.getMediaType().equals(ODFMediaTypes.FORMULA_MEDIA_TYPE)) {
                result.m_bfHasErrors |= (validateMathML(aLogger,
                        getStreamName(OdfDocument.OdfXMLFile.CONTENT.getFileName()), aVersion)
                        ? ODFDetailedResult.CONTENT_STEP_ERROR : 0);
            } else {
                result.m_bfHasErrors |= (validateEntry(aLogger,
                        getStreamName(OdfDocument.OdfXMLFile.CONTENT.getFileName()), DOCUMENT_CONTENT, aVersion)
                        ? ODFDetailedResult.CONTENT_STEP_ERROR : 0);
            }
            result.m_bfHasErrors |= (validatePost(aLogger, aVersion) ? ODFDetailedResult.POST_STEP_ERROR : 0);
        } catch (ZipException e) {
            aLogger.logFatalError(e.getMessage());
            result.m_bfHasErrors = ODFDetailedResult.PRIME_STEP_ERROR;
        } catch (IOException e) {
            aLogger.logFatalError(e.getMessage());
            result.m_bfHasErrors = ODFDetailedResult.PRIME_STEP_ERROR;
        }

        logSummary(result.m_bfHasErrors != 0, aLogger);

        result.m_MimeType = aPkg.getMediaTypeString();
        result.m_nErrors = aLogger.getErrorCount();
        result.m_nWarnings = aLogger.getWarningCount();

        return result;
    }

    @Override
    protected boolean validatePre(Logger aLogger, OdfVersion aVersion) throws ODFValidatorException, IOException {
        Logger aManifestLogger = new Logger(OdfPackage.OdfFile.MANIFEST.getPath(), aLogger);
        Logger aMimetypeLogger = new Logger("mimetype", aLogger);

        // UGLY: do something that causes ODFDOM to parse the manifest, which
        // may cause m_ErrorHandler to be called
        m_aPkg.getFilePaths();
        // hack: just create logger again, too lazy to create a Pair class
        // and return it from validateMimetype...
        boolean bErrorsFound = m_ErrorHandler.processErrors(aLogger, aManifestLogger,
            aMimetypeLogger, aVersion);

        bErrorsFound |= validateMimetype(aMimetypeLogger, aVersion);
        bErrorsFound |= validateManifest(aManifestLogger, aVersion);
        aMimetypeLogger.logSummaryInfo();

        return bErrorsFound;
    }

    @Override
    protected boolean validatePost(Logger aLogger, OdfVersion aVersion) throws ODFValidatorException, IOException {
        boolean bHasErrors = false;
        if (m_aSubDocs != null) {
            Iterator<ManifestEntry> aIter = m_aSubDocs.iterator();
            while (aIter.hasNext()) {
                ManifestEntry aEntry = aIter.next();
                ODFPackageValidator aPackageValidator
                    = new ODFSubPackageValidator(getPackage(aLogger), getLoggerName(), aEntry.getFullPath(), aEntry.getMediaType(),
                        m_nLogLevel, m_eMode, m_aConfigVersion, m_aFilter, m_aResult.getGenerator(), m_aValidatorProvider);
                bHasErrors |= aPackageValidator.validate(aLogger);
            }
        }

        if (aVersion.compareTo(OdfVersion.V1_2) >= 0) {
            bHasErrors |= validateDSig(aLogger, OdfPackageExt.STREAMNAME_DOCUMENT_SIGNATURES, aVersion);
            bHasErrors |= validateDSig(aLogger, OdfPackageExt.STREAMNAME_MACRO_SIGNATURES, aVersion);
        }

        return bHasErrors;
    }

    @Override
    protected void logSummary(boolean bHasErrors, Logger aLogger) {
        aLogger.logSummaryInfo();
        if ((bHasErrors || aLogger.hasError()) && m_nLogLevel.compareTo(Logger.LogLevel.INFO) < 0) {
            aLogger.logInfo("Generator: " + m_aResult.getGenerator(), true);
        }
    }

    public void foundManifestEntry(ManifestEntry aManifestEntry) {
        if (aManifestEntry.isOpenDocumentMediaType()) {
            if (m_aSubDocs == null) {
                m_aSubDocs = new ArrayList<ManifestEntry>();
            }
            m_aSubDocs.add(aManifestEntry);
        }
    }

    private boolean validateMimetype(Logger aLogger, OdfVersion aVersion) {
        boolean bHasErrors = false;

        String aMimetype = getPackage(aLogger).getMediaTypeString();
        if ((aMimetype == null) || aMimetype.length() == 0) {
            aLogger.logFatalError("file is not a zip file, or has no mimetype.");
            bHasErrors = true;
        } else if (!(aMimetype.equals(ODFMediaTypes.TEXT_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.TEXT_TEMPLATE_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.GRAPHICS_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.GRAPHICS_TEMPLATE_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.PRESENTATION_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.SPREADSHEET_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.SPREADSHEET_TEMPLATE_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.CHART_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.CHART_TEMPLATE_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.IMAGE_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.IMAGE_TEMPLATE_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.FORMULA_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.FORMULA_TEMPLATE_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.TEXT_MASTER_MEDIA_TYPE)
            || aMimetype.equals(ODFMediaTypes.TEXT_WEB_MEDIA_TYPE))) {
            aLogger.logInfo("mimetype is not an ODFMediaTypes mimetype.", false);
            bHasErrors = true;
        }

        return bHasErrors;
    }

    private boolean validateManifest(Logger aLogger, OdfVersion aVersion) throws IOException, ZipException, IllegalStateException, ODFValidatorException {
        boolean bRet;
        ManifestFilter aFilter = new ManifestFilter(aLogger, m_aResult, this);
        Validator aManifestValidator = m_aValidatorProvider.getManifestValidator(aLogger.getOutputStream(), aVersion);
        if (aManifestValidator != null) {
            bRet = validateEntry(aFilter,
                aManifestValidator, aLogger, OdfPackage.OdfFile.MANIFEST.getPath());
        } else {
            aLogger.logInfo("Validation of " + OdfPackage.OdfFile.MANIFEST.getPath() + " skipped.", false);
            bRet = parseEntry(aFilter, aLogger, OdfPackage.OdfFile.MANIFEST.getPath(), false);
        }
        return bRet;
    }

}
