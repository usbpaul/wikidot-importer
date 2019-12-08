package nl.w4w.work.repository;

import org.springframework.stereotype.Repository;

import java.io.FileNotFoundException;

@Repository
public class ImporterRepository {

    private String sourceDirectory = null;
    private String filesDirectory = null;

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
