package nl.w4w.work.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class Properties {
    private String wikidotName;
    private String apiKey;
    private String sourcesDirectory;
    private String filesDirectory;

    public Properties(String wikidotName, String apiKey, String sourcesDirectory, String filesDirectory) {
        this.wikidotName = wikidotName;
        this.apiKey = apiKey;
        this.sourcesDirectory = sourcesDirectory;
        this.filesDirectory = filesDirectory;
    }
}
