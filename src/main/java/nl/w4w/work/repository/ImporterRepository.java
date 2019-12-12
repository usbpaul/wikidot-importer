package nl.w4w.work.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        return getDirectoryItemsWithFileFilterAndRenaming(
                sourcesDir,
                (f) -> {
                    return f.getName().endsWith("txt");
                },
                name -> name.replaceAll(".txt", "")
        );
    }

    public Map<String, List<String>> getFiles(List<String> sources) {
        File filesDirectory = new File(filesDir);
        return sources.stream()
                .filter(s -> {
                    return new File(filesDirectory + "/" + s).isDirectory();
                })
                .collect(Collectors.toMap(s -> s, s -> getDirectoryItemsWithFileFilterAndRenaming(
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
    public String readSourceContents(String source) throws IOException {
        Path path = Paths.get(sourcesDir + "/" + source + ".txt");
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    /**
     * Reads the contents of a file into a {@link String}
     *
     * @param source The name that identifies a Wikidot source.
     * @param fileName The name that identifies a Wikidot file (binary attachment in Wikidot lingo).
     * @return A {@link String} representing the contents of the file.
     * @throws FileNotFoundException An exception that occurs when the file cannot be found.
     */
    public String readFileContents(String source, String fileName) throws IOException {
        Path path = Paths.get(filesDir + "/" + source + "/" + fileName);
        BufferedImage image = ImageIO.read(path.toUri().toURL());
        return encodeToString(image, "png");
    }

    /**
     * Encodes an image to a string using base64 encoding.
     *
     * @param image The {@link BufferedImage} to encode
     * @param type  A {@link String} representing the type of the image: jpg, bmp, png,...
     * @return The base64 encoded string
     */
    private String encodeToString(BufferedImage image, String type) throws IOException {
        String imageString = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(image, type, baos);
        byte[] imageBytes = baos.toByteArray();

        imageString = Base64.getEncoder().encodeToString(imageBytes);

        baos.close();
        return imageString;
    }

}
