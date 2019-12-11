package nl.w4w.work.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class ImporterRepository {

    @Value("${sourcesDir}")
    private String sourcesDir;
    @Value("${filesDir}")
    private String filesDir;

    public List<String> getSources() {
        return getDirectoryItemsWithFileFilterAndRenaming (
                sourcesDir,
                (f) -> {return f.getName().endsWith("txt");},
                name -> name.replaceAll(".txt", "")
        );
    }

    public Map<String, List<String>> getFiles(List<String> sources) {
        File filesDirectory = new File(filesDir);
        return sources.stream()
        .filter(s -> {return new File(filesDirectory + "/" + s).isDirectory();})
                .collect(Collectors.toMap(s->s, s->getDirectoryItemsWithFileFilterAndRenaming(
                        filesDir + "/" + s,
                        File::isFile,
                        name -> name
                )));
    }

    public List<String> getDirectoryItemsWithFileFilterAndRenaming(String directoryPath,
                                                                   FileFilter filePredicate,
                                                                   Function<String, String> renameMapping) {
        File sourcesDirectory = new File(directoryPath);
        return Arrays.stream(Objects.requireNonNull(sourcesDirectory.listFiles(
                filePredicate
                )
        )).map(File::getName)
                .map(renameMapping)
                .collect(Collectors.toList());
    }

    /**
     * Reads the contents of a source into a {@link String}
     *
     * @param source The name that identifies a Wikidot source.
     * @return A {@link String} representing the contents of the file.
     * @throws FileNotFoundException An exception that occurs when the file cannot be found.
     */
    public String readSourceContents(String source) throws FileNotFoundException {
        return null;
    }

    /**
     * Reads the contents of a file into a {@link String}
     *
     * @param file The name that identifies a Wikidot file (binary attachment in Wikidot lingo).
     * @return A {@link String} representing the contents of the file.
     * @throws FileNotFoundException An exception that occurs when the file cannot be found.
     */
    public String readFileContents(String file) throws FileNotFoundException {
        return "Hee het werkt";
    }
}
