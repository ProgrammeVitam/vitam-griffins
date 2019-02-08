package org.odftoolkit.odfvalidator;

// Result class for extended version of validation to get a detailed validation result step by step
public class ODFDetailedResult {
	int m_bfHasErrors = 0;
	int m_nErrors = 0;
	int m_nWarnings = 0;
	String m_MimeType=null;
	String m_DocVersion;

	static public final int PRIME_STEP_ERROR=1;
	static public final int PRE_STEP_ERROR=2;
	static public final int META_STEP_ERROR=4;
	static public final int SETTINGS_STEP_ERROR=8;
	static public final int STYLES_STEP_ERROR=16;
	static public final int CONTENT_STEP_ERROR=32;
	static public final int POST_STEP_ERROR=64;
	
	public int getHasErrors() {
		return m_bfHasErrors;
	}

	public int getErrorsNb() {
		return m_nErrors;
	}

	public int getWarningsNb() {
		return m_nWarnings;
	}
	
	public String getMimeType() {
		return m_MimeType;
	}
	public String getDocVersion() {
		return m_DocVersion;
	}

	@Override
	public String toString(){
		String s;
		s="ODFDetailedResult={errors="+m_bfHasErrors
				+", logged_errors="+m_nErrors
				+", logged_warnings="+m_nWarnings
				+", mimeType="+m_MimeType
				+", version="+m_DocVersion+"}";
		return s;
	}
}

// VITAM Add-On
// End