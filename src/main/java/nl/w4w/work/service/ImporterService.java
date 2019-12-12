    package nl.w4w.work.service;

import nl.w4w.work.client.WikidotClient;
import nl.w4w.work.repository.ImporterRepository;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ImporterService {

    private static Logger LOG = LoggerFactory.getLogger(ImporterService.class);

    @Value("${wikidotName}")
    private String wikidotName;
    @Value("${apiKey}")
    private String apiKey;
    @Value("${sourcesDir}")
    private String sourcesDirectoryName;
    @Value("${filesDir}")
    private String filesDirectoryName;

    @Autowired
    private final ImporterRepository importerRepository;

    public ImporterService(ImporterRepository importerRepository) throws IOException {
        this.importerRepository = importerRepository;
    }

    public void importData() throws IOException, XmlRpcException {
        // Get list of source names as Strings
        List<String> sources = importerRepository.getSources();
        LOG.info("Need to import " + sources.size() + " files");

        // Get lists of file names (binary attachments) as Strings mapped from each source name
        Map<String, List<String>> files = importerRepository.getFiles(sources);
        int count = 0;
        WikidotClient wikidotClient = new WikidotClient(this.wikidotName, this.apiKey);
        for (String source : sources) {
            count++;
            String sourceContents = importerRepository.readSourceContents(source);
            LOG.info("Importing file [" + source + ".txt] (" + count + "/" + sources.size() + ")");
            wikidotClient.addPage(source, sourceContents);
            if (files.containsKey(source)) {
                for (String file:files.get(source)) {
                    String base64Encoded = importerRepository.readFileContents(source, file);
                        LOG.info("Attaching binary  [" + file + "] to ["  + source + "]");
                        wikidotClient.addFile(source, file, base64Encoded);
                }
            }
        }

    }
}
