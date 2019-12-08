package nl.w4w.work.service;

import lombok.RequiredArgsConstructor;
import nl.w4w.work.client.WikidotClient;
import nl.w4w.work.config.Properties;
import nl.w4w.work.repository.ImporterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

@Service
public class ImporterService {

    private WikidotClient wikidotClient;

    @Value("${wikidotName}")
    private String wikidotName;

    @Autowired
    private final ImporterRepository importerRepository;

    public ImporterService(ImporterRepository importerRepository) {
        this.importerRepository = importerRepository;
    }

    public void importData() {
        System.out.println(wikidotName);
    }
}
