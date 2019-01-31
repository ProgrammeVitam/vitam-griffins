package fr.gouv.vitam.griffins.siegfried.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiegfriedFile {
    @JsonProperty("matches")
    private List<SiegfriedMatche> matches;

    @JsonProperty("filename")
    private String filename;

    public SiegfriedFile() {
    }

    public SiegfriedFile(List<SiegfriedMatche> matches, String filename) {
        this.matches = matches;
        this.filename = filename;
    }

    public List<SiegfriedMatche> getMatches() {
        return matches;
    }

    public void setMatches(List<SiegfriedMatche> matches) {
        this.matches = matches;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override public String toString() {
        return "SiegfriedFile{" +
            "matches=" + matches +
            ", filename='" + filename + '\'' +
            '}';
    }
}