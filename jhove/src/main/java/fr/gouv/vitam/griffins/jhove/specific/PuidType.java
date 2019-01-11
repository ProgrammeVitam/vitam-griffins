/*******************************************************************************
 * Copyright French Prime minister Office/SGMAP/DINSIC/Vitam Program (2015-2019)
 *
 * contact.vitam@culture.gouv.fr
 *
 * This software is a computer program whose purpose is to implement a digital archiving back-office system managing
 * high volumetry securely and efficiently.
 *
 * This software is governed by the CeCILL 2.1 license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL 2.1 license as
 * circulated by CEA, CNRS and INRIA at the following URL "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license,
 * users are provided only with a limited warranty and the software's author, the holder of the economic rights, and the
 * successive licensors have only limited liability.
 *
 * In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or
 * developing or reproducing the software by the user in light of its specific status of free software, that may mean
 * that it is complicated to manipulate, and that also therefore means that it is reserved for developers and
 * experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling the security of their systems and/or data
 * to be ensured and, more generally, to use and operate it in the same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL 2.1 license and that you
 * accept its terms.
 *******************************************************************************/

package fr.gouv.vitam.griffins.jhove.specific;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class linking formats and any useful information for inner tool.
 */
public class PuidType {
    /**
     * The constant formatTypes and inner tool associated context.
     * <p>
     * Here Jhove module is the context information, and correspondance with formats
     * is taken from http://jhove.openpreservation.org/modules/
     *
     */
    public static final Map<String, String> formatTypes = Collections.unmodifiableMap(
            Stream.of(
  /*
  The PDF-hul module recognizes and validates the following public profiles:
    - PDF version 1.0-1.6 [PDF 1.4, PDF 1.5, PDF 1.6]
    - PDF/X-1 (ISO 15930-1:2001) [PDF/X-1], PDF/X-1a (ISO 15930-4:2003) [PDF/X-1a], PDF/X-2 (ISO 15930-5:2003) [PDF/X-2], and PDF/X-3 (ISO 15930-6:2003) [PDF/X-3]
    - Linearized PDF [PDF 1.4]
    - Tagged PDF [PDF 1.4]
    - PDF/A (ISO/DIS 19005-1) [PDF/A]
   */
                    new SimpleImmutableEntry<>("fmt/18", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/19", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/20", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/95", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/354", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/476", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/477", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/478", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/479", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/480", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/481", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/144", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/145", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/157", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/146", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/147", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/158", "PDF-hul"),
                    new SimpleImmutableEntry<>("fmt/148", "PDF-hul"),
   /*
  The JPEG-hul module recognizes and validates the following public profiles:
    - JPEG (ISO/IEC 10918-1:1994) [JPEG]
    - JFIF 1.02 (JPEG File Interchange Format) [JFIF]
    - Exif 2.0, 2.1 (JEIDA-49-1998) 2.1, and 2.2 (JEITA CP-3451) [Exif 2.1, Exif 2.2,]
    - SPIFF (ISO/IEC 10918-3:1997) [SPIFF]
    - JTIP (ISO/IEC 10918-3:1997) [JTIP]
    - JPEG-LS (ISO/IEC 14495) [JPEG]
  */
                    new SimpleImmutableEntry<>("fmt/41", "JPEG-hul"),
                    new SimpleImmutableEntry<>("fmt/42", "JPEG-hul"),
                    new SimpleImmutableEntry<>("x-fmt/398", "JPEG-hul"),
                    new SimpleImmutableEntry<>("x-fmt/390", "JPEG-hul"),
                    new SimpleImmutableEntry<>("x-fmt/391", "JPEG-hul"),
                    new SimpleImmutableEntry<>("fmt/645", "JPEG-hul"),
                    new SimpleImmutableEntry<>("fmt/43", "JPEG-hul"),
                    new SimpleImmutableEntry<>("fmt/44", "JPEG-hul"),
                    new SimpleImmutableEntry<>("fmt/112", "JPEG-hul"),
  /*
  The JPEG2000-hul module recognizes and validates the following public profiles:
    - JP2 profile (ISO/IEC 15444-1:2000/ITU-T Rec. T.800 (2002)) [JP2, ITU-T T.800]
    - JPX profile (ISO/IEC 15444-2:2004) [JPX]
   */
                    new SimpleImmutableEntry<>("x-fmt/392", "JPEG2000-hul"),
                    new SimpleImmutableEntry<>("fmt/463", "JPEG2000-hul"),
                    new SimpleImmutableEntry<>("fmt/151", "JPEG2000-hul"),
  /*
  The TIFF-hul module recognizes and validates the following public profiles:
    - TIFF version 4.0, 5.0, and 6.0 [TIFF 4.0, TIFF 5.0, TIFF 6.0]
    - Baseline 6.0 bilevel (previously known as 5.0 Class B), grayscale (Class G), palette-color (Class P), and RGB (Class R) [TIFF 6.0]
    - 6.0 extension YCbCr (Class Y) [TIFF 6.0]
    - TIFF/IT (ISO 12639:2003), including file types CT, LW, HC, MP, BP, BL, and FP, and conformance levels P1 and P2 [TIFF/IT]
    - TIFF/EP (ISO 12234-2:2001) [TIFF/EP]
    - Exif 2.0, 2.1 (JEIDA-49-1998), and 2.2 (JEITA CP-3451) [Exif 2.1, Exif 2.2]
    - GeoTIFF 1.0 [GeoTIFF]
    - DLF Benchmark for Faithful Digital Reproductions of Monographs and Serials [DLF]
    - TIFF-FX (RFC 2301), including Profiles C, F, J, L, M, and S [TIFF-FX]
    - Class F (RFC 2306) [Class F, RFC 2306]
    - RFC 1314 [RFC 1314]
    - DNG (Adobe Digital Negative) [DNG]
   */
                    new SimpleImmutableEntry<>("fmt/152", "TIFF-hul"),
                    new SimpleImmutableEntry<>("x-fmt/399", "TIFF-hul"),
                    new SimpleImmutableEntry<>("x-fmt/388", "TIFF-hul"),
                    new SimpleImmutableEntry<>("x-fmt/387", "TIFF-hul"),
                    new SimpleImmutableEntry<>("fmt/155", "TIFF-hul"),
                    new SimpleImmutableEntry<>("fmt/353", "TIFF-hul"),
                    new SimpleImmutableEntry<>("fmt/154", "TIFF-hul"),
                    new SimpleImmutableEntry<>("fmt/153", "TIFF-hul"),
                    new SimpleImmutableEntry<>("fmt/156", "TIFF-hul"),
                    //TODO to complete
  /*
  The GIF-hul module recognizes and validates the following public profiles:
    - GIF version 87a and 89a [GIF87a, GIF89a]
   */
                    new SimpleImmutableEntry<>("fmt/3", "GIF-hul"),
                    new SimpleImmutableEntry<>("fmt/4", "GIF-hul"),
  /*
  The AIFF-hul module recognizes and validates the following public profiles:
    - AIFF 1.3 [AIFF]
    - AIFF-C [AIFF-C]
   */
                    new SimpleImmutableEntry<>("fmt/414", "AIFF-hul"),
                    new SimpleImmutableEntry<>("x-fmt/135", "AIFF-hul"),
                    new SimpleImmutableEntry<>("x-fmt/136", "AIFF-hul"),
  /*
  The WAVE-hul module recognizes and validates the following public profiles:
    - PCMWAVEFORMAT [PCMWAVEFORMAT]
    - WAVEFORMATEX [WAVEFORMATEX]
    - WAVEFORMATEXTENSIBLE [WAVEFORMATEXTENSIBLE]
    - EBU Technical Specification 3285, Broadcast Wave Format (BWF) version 0, 1 and 2 [BWF, BWF Supp 1, BWF Supp 3, BWF Supp 4]
    - EBU Technical Specification 3306, RF64 [RF64]
   */
                    new SimpleImmutableEntry<>("fmt/1", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/706", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/703", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/2", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/707", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/704", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/527", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/708", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/705", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/711", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/6", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/709", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/710", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/141", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/142", "WAVE-hul"),
                    new SimpleImmutableEntry<>("fmt/143", "WAVE-hul"),

  /*
  The XML-hul module recognizes and validates the following public profiles:
    - XML 1.0 [XML]
   */
                    new SimpleImmutableEntry<>("fmt/101", "XML-hul"),
  /*
  The UTF8-hul module recognizes and validates the following public profiles:
    - UTF-8 encoded content streams [Unicode]
   */
                    new SimpleImmutableEntry<>("x-fmt/16", "UTF8-hul"),
  /*
  The HTML-hul module recognizes and validates the following public profiles:
    - HTML 3.2, and 4.0 and 4.01 (Strict, Transitional, and Frameset) [HTML 3.2, HTML 4.0, HTML 4.01]
    - XHTML Basic and 1.0 and 1.1 (Strict, Transitional, and Frameset) [XHTML Basic, XHTML 1.0, XHTML 1.1]
   */
                    new SimpleImmutableEntry<>("fmt/103", "HTML-hul"),
                    new SimpleImmutableEntry<>("fmt/96", "HTML-hul"),
                    new SimpleImmutableEntry<>("fmt/97", "HTML-hul"),
                    new SimpleImmutableEntry<>("fmt/98", "HTML-hul"),
                    new SimpleImmutableEntry<>("fmt/99", "HTML-hul"),
                    new SimpleImmutableEntry<>("fmt/100", "HTML-hul"),
                    new SimpleImmutableEntry<>("fmt/471", "HTML-hul"),
                    new SimpleImmutableEntry<>("fmt/102", "HTML-hul"),
  /*
  The ASCII-hul module recognizes and validates the following public profiles:
    - ASCII (ANSI X3.4-1986, ECMA-6, ISO 646:1991) [ANSI X3.4, ECMA-6, ISO 646,]
   */
                    new SimpleImmutableEntry<>("x-fmt/22", "ASCII-hul"),
  /*
  The PNG-hul module recognizes and validates the following public profiles:
    - PNG (ISO/IEC 15948:2003)
   */
                    new SimpleImmutableEntry<>("fmt/11", "PNG-gdm"),
                    new SimpleImmutableEntry<>("fmt/12", "PNG-gdm"),
                    new SimpleImmutableEntry<>("fmt/13", "PNG-gdm"),
  /*
  Dedicated modules recognizes and validates uncommon formats
   */
                    new SimpleImmutableEntry<>("x-fmt/266", "GZIP-kb"),
                    new SimpleImmutableEntry<>("fmt/289", "WARC-kb")
  //TODO add other existing modules
            ).collect(Collectors.toMap(SimpleImmutableEntry::getKey, SimpleImmutableEntry::getValue))
    );

    private PuidType() {
    }
}
