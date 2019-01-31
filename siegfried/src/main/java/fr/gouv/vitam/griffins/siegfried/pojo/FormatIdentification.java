package fr.gouv.vitam.griffins.siegfried.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FormatIdentification {
    @JsonProperty("FormatLitteral")
    private String formatLitteral;
    @JsonProperty("MimeType")
    private String mimeType;
    @JsonProperty("FormatId")
    private String formatId;

    public FormatIdentification() {
    }

    public FormatIdentification(String formatLitteral, String mimeType, String formatId) {
        this.formatLitteral = formatLitteral;
        this.mimeType = mimeType;
        this.formatId = formatId;
    }

    public String getFormatLitteral() {
        return formatLitteral;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFormatId() {
        return formatId;
    }
}
