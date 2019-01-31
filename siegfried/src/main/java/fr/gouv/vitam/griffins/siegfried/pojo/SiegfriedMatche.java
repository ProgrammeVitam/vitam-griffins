package fr.gouv.vitam.griffins.siegfried.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiegfriedMatche {
    @JsonIgnore
    public static final String PRONOM_NAMESPACE = "pronom";

    @JsonProperty("id")
    private String id;
    @JsonProperty("format")
    private String format;
    @JsonProperty("mime")
    private String mime;
    @JsonProperty("ns")
    private String ns;

    public SiegfriedMatche() {
    }

    public SiegfriedMatche(String id, String format, String mime, String ns) {
        this.id = id;
        this.format = format;
        this.mime = mime;
        this.ns = ns;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    @Override public String toString() {
        return "SiegfriedMatche{" +
            "id='" + id + '\'' +
            ", format='" + format + '\'' +
            ", mime='" + mime + '\'' +
            ", ns='" + ns + '\'' +
            '}';
    }
}
