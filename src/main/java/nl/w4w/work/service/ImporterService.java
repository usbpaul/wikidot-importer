package nl.w4w.work.service;

import nl.w4w.work.client.WikidotClient;
import nl.w4w.work.repository.ImporterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class ImporterService {

    @Value("${wikidotName}")
    private String wikidotName;
    @Value("${sourcesDir}")
    private String sourcesDirectoryName;
    @Value("${filesDir}")
    private String filesDirectoryName;

    @Autowired
    private final WikidotClient wikidotClient;

    @Autowired
    private final ImporterRepository importerRepository;

    public ImporterService(WikidotClient wikidotClient, ImporterRepository importerRepository) {
        this.wikidotClient = wikidotClient;
        this.importerRepository = importerRepository;
    }

    public void importData() {
        // Get list of pagenames as Strings
        List<String> sources = importerRepository.getSources();
        Map<String, List<String>> files = importerRepository.getFiles(sources);
        sources.forEach(System.out::println);

    }
}
