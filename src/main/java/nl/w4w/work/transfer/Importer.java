//package wikidot.xmlrpc.importer;
//
//import org.apache.xmlrpc.XmlRpcException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import sun.misc.BASE64Encoder;
//import wikidot.xmlrpc.client.WikidotClient;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import java.net.MalformedURLException;
//import java.util.*;
//
///**
// * Class that imports pages into a wikidot site from text files on local disk.
// *
// * @author Paul Bakker
// */
//public class Importer {
//
//    private static Logger LOG = LoggerFactory.getLogger(Importer.class);
//
//    private static final int BUFFER_SIZE = 4096;
//
//    private WikidotClient wikidotClient;
//    private File sourceDirectory;
//    private File filesDirectory;
//
//    /**
//     * Constructor of this class
//     *
//     * @param wikidotClient A {@link WikidotClient} object, targeting the wikidot site into which to import pages and/or files
//     * @param sourceDirectory A {@link File} object representing the directory that contains the pages to import, as txt files.
//     * @param filesDirectory A {@link File} object representing  the directory that contains the binary files to add to the pages.
//     * @throws MalformedURLException An exception that is thrown in case of a bad URL.
//     */
//    public Importer(WikidotClient wikidotClient, File sourceDirectory, File filesDirectory) throws MalformedURLException {
//        this.wikidotClient = wikidotClient;
//        this.sourceDirectory = sourceDirectory;
//        this.filesDirectory = filesDirectory;
//    }
//
//    /**
//     * Main method that actually puts the pages on a wikidot.
//     * Using the compiled version of this application, and specifying the command line arguments, i.e.
//     * java -jar app.jar hiddenmeanings /tmp/wikidot/sources
//     * results in pages on the wikidat that have wrongly encoded characters.
//     *
//     * @param args An array of command line arguments
//     * @throws MalformedURLException
//     * @throws FileNotFoundException
//     * @throws XmlRpcException
//     */
//    public static void main(String[] args) throws IOException, XmlRpcException {
//
//        String sourceDirectory = null;
//        String wikidotName = null;
//        String filesDirectory = null;
//        if (args.length > 1) {
//            wikidotName = args[0];
//            sourceDirectory = args[1];
//            filesDirectory = args[2];
//        }
//
//        Locale emptyLocale = new Locale("", "", "");
//        ResourceBundle bundle = ResourceBundle.getBundle("importer", emptyLocale);
//        if (wikidotName == null) {
//            wikidotName = bundle.getString("site.name");
//        }
//        if (sourceDirectory == null) {
//            sourceDirectory = bundle.getString("source.dir");
//        }
//        if (filesDirectory == null) {
//            filesDirectory = bundle.getString("files.dir");
//        }
//
//        LOG.debug("Starting the importer for wikidot [" + wikidotName + "],");
//        LOG.debug("and source directory [" + sourceDirectory + "]");
//        LOG.debug("and files directory [" + filesDirectory + "]");
//        Importer importer = new Importer(
//                new WikidotClient(wikidotName),
//                new File(sourceDirectory),
//                new File(filesDirectory));
//        importer.importFiles();
//    }
//
//    /**
//     * Reads page files from the input source directory and creates a wikidot page for each.
//     * If there are corresponding binary files in the input files directory, those are uploaded as attachments to the page.
//     *
//     * @throws FileNotFoundException Exception thrown when the file cannot be found.
//     */
//    protected void importFiles() throws IOException, XmlRpcException {
//        if (this.sourceDirectory.isDirectory()) {
//            File[] textFilesToImport = this.sourceDirectory.listFiles(new FileFilter() {
//                public boolean accept(File file) {
//                    return file.getName().endsWith("txt");
//                }
//            });
//            LOG.debug("Need to import " + textFilesToImport.length + " files");
//            int count = 0;
//            HashMap<String, List<File>> filesHash = getFilesHash(textFilesToImport);
//            for (File textFile : textFilesToImport) {
//                count++;
//                String fileContent = readFile(textFile.getAbsolutePath());
//                String pageName = getPageNameForTextFile(textFile);
//                LOG.debug("Importing file [" + textFile.getName() + "] (" + count + "/" + textFilesToImport.length + ")");
//                wikidotClient.addPage(pageName, fileContent);
//                if (filesHash.containsKey(pageName)) {
//                    for (File binaryFile:filesHash.get(pageName)) {
//                        String base64Encoded = readImageIntoBase64String(binaryFile.getAbsolutePath());
//                        LOG.debug("Attaching binary  [" + binaryFile.getName() + "] to ["  + pageName + "]");
//                        wikidotClient.addFile(getPageNameForTextFile(textFile), binaryFile.getName(), base64Encoded);
//                    }
//                }
//            }
//        }
//        LOG.debug("All done.");
//    }
//
//    private HashMap<String, List<File>> getFilesHash(File[] textFilesToImport) {
//        HashMap<String, List<File>> filesHash = new HashMap<String, List<File>>();
//        if (!filesDirectory.isDirectory()) {
//            return filesHash;
//        }
//        for (File textFile : textFilesToImport) {
//            String pageName = getPageNameForTextFile(textFile);
//            File possibleSubDirectory = createFile(filesDirectory + "/" + pageName);
//            if (possibleSubDirectory.isDirectory()) {
//                File[] binaryFiles = possibleSubDirectory.listFiles(new FileFilter() {
//                    public boolean accept(File file) {
//                        return file.isFile();
//                    }
//                });
//                List<File> fileList = new ArrayList<File>();
//                for (File binaryFile : binaryFiles) {
//                    fileList.add(binaryFile);
//                }
//                filesHash.put(pageName, fileList);
//            }
//        }
//        return filesHash;
//    }
//
//    /**
//     * Helper method for testing purposes.
//     * It's hard to test code in which new objects are created.
//     * See http://code.google.com/p/mockito/wiki/MockingObjectCreation
//     *
//     * @param path The path for which to create a file.
//     * @return A new {@link File}.
//     */
//    protected File createFile(String path) {
//        return new File(path);
//    }
//
//    private boolean existsSubDirectory(File filesDirectory, String pageName) {
//        boolean directoryFound = false;
//        File[] filesSubDirectories = filesDirectory.listFiles(new FileFilter() {
//            public boolean accept(File file) {
//                return file.isDirectory();
//            }
//        });
//        for (File subDirectory : filesSubDirectories) {
//            if (subDirectory.getName().equals(pageName)) {
//                directoryFound = true;
//            }
//        }
//        return directoryFound;
//    }
//
//    private String getPageNameForTextFile(File textFile) {
//        return textFile.getName().replace(".txt", "");
//    }
//
//    /**
//     * Reads the contents of a file into a {@link String}
//     *
//     * @param filePath The full path to the file.
//     * @return A {@link String} representing the contents of the file.
//     * @throws FileNotFoundException An exception that occurs when the file cannot be found.
//     */
//    String readFile(String filePath) throws FileNotFoundException {
//        StringBuilder textBuilder = new StringBuilder();
//        String newLine = System.getProperty("line.separator");
//        Scanner scanner = new Scanner(new FileInputStream(filePath));
//        try {
//            while (scanner.hasNextLine()) {
//                textBuilder.append(scanner.nextLine() + newLine);
//            }
//        } finally {
//            scanner.close();
//        }
//        return textBuilder.toString();
//    }
//
//    /**
//     * Encodes an image to a string using base64 encoding.
//     *
//     * @param image The {@link BufferedImage} to encode
//     * @param type A {@link String} representing the type of the image: jpg, bmp, png,...
//     * @return The base64 encoded string
//     */
//    private String encodeToString(BufferedImage image, String type) {
//        String imageString = null;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        try {
//            ImageIO.write(image, type, baos);
//            byte[] imageBytes = baos.toByteArray();
//
//            BASE64Encoder encoder = new BASE64Encoder();
//            imageString = encoder.encode(imageBytes);
//
//            baos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return imageString;
//    }
//
//    /**
//     * Given the filename of an image file, returns the base64 encoded version as a {@link String}
//     *
//     * @param fileName The full path the image file to encode, as a {@link String}
//     * @return A {@link String} representation of the image.
//     * @throws IOException A possible exception when the image cannot be read.
//     */
//    String readImageIntoBase64String(String fileName) throws IOException {
//        BufferedImage image = ImageIO.read(new File(fileName));
//        return encodeToString(image, "png");
//    }
//}
