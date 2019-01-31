package fr.gouv.vitam.griffins.siegfried.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

import static fr.gouv.vitam.griffins.siegfried.pojo.SiegfriedMatche.PRONOM_NAMESPACE;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiegfriedResponse {

    @JsonProperty("files")
    private List<SiegfriedFile> files;

    public SiegfriedResponse() {
    }

    public SiegfriedResponse(List<SiegfriedFile> files) {
        this.files = files;
    }

    @JsonIgnore
    public FormatIdentification toFormatIdentification(String fileName) {
        Optional<SiegfriedMatche> siegfriedMatche = files.stream()
            .filter(f -> f.getFilename().contains(fileName))
            .findFirst()
            .flatMap(m -> m.getMatches()
                .stream()
                .filter(f -> PRONOM_NAMESPACE.equals(f.getNs()))
                .findFirst()
            );

        if (!siegfriedMatche.isPresent()) {
            throw new RuntimeException(String.format("Does not match any thing %s %s.", files, fileName));
        }

        return new FormatIdentification(siegfriedMatche.get().getFormat(), siegfriedMatche.get().getMime(), siegfriedMatche.get().getId());
    }

    public List<SiegfriedFile> getFiles() {
        return files;
    }

    public void setFiles(List<SiegfriedFile> files) {
        this.files = files;
    }
}
