package nl.w4w.work.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import nl.w4w.work.exception.AuthorizationException;
import nl.w4w.work.exception.SiteNotExistsException;
import nl.w4w.work.rpc.XmlRpcTypeNil;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

/**
 * XML-RPC client that lets you do interesting stuff on a wikidot site.
 * Some more info here: http://www.wikidot.com/doc:api
 * http://xml-api.wikidot.com/
 *
 * @author Paul Bakker
 */
public class WikidotClient {

    private String siteName;
    private String apiKey;

    public static final String METHOD_PAGES_SELECT = "pages.select";
    public static final String METHOD_PAGES_GET_ONE = "pages.get_one";
    public static final String METHOD_SYSTEM_LIST_METHODS = "system.listMethods";
    public static final String METHOD_SYSTEM_METHOD_HELP = "system.methodHelp";
    public static final String METHOD_PAGES_SAVE_ONE = "pages.save_one";
    public static final String METHOD_FILES_SAVE_ONE = "files.save_one";
    public static final String METHOD_PAGES_GET_META = "pages.get_meta";

    private static Logger LOG = LoggerFactory.getLogger(WikidotClient.class);

    private static final String SERVER_ENDPOINT = "https://www.wikidot.com/xml-rpc-api.php";
    XmlRpcClientConfigImpl config;
    XmlRpcClient client;

    public WikidotClient(String siteName, String apiKey) throws IOException {

        this.siteName = siteName;
        this.apiKey = apiKey;
        config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(SERVER_ENDPOINT));
        config.setBasicUserName("btclient");
//        config.setBasicUserName("btclient");
        config.setBasicPassword(this.apiKey);
        config.setEnabledForExtensions(true);

        client = new XmlRpcClient();
        client.setTypeFactory(new XmlRpcTypeNil(client));
        client.setConfig(config);
    }

    public void listPages() throws XmlRpcException {
        Hashtable<String, String> requestHash = new Hashtable<String, String>();
        requestHash.put("site", this.siteName);
        requestHash.put("pagetype", "*");
        Vector<Hashtable> vector = new Vector<Hashtable>();
        vector.addElement(requestHash);

        Object[] pageObjects = (Object[]) client.execute(METHOD_PAGES_SELECT, vector);
        for (Object object:pageObjects) {
            String pageName = (String) object;
            System.out.println(pageName);
        }
    }

    /**
     * Returns the contents of a page on the wikidot.
     *
     * @param pageName The name of the page for which to fetch the content.
     * @return The conten of the page.
     * @throws XmlRpcException An exception that is thrown when the remote procedure fails.
     */
    public String getPageContent(String pageName) throws SiteNotExistsException, XmlRpcException, AuthorizationException {
        HashMap<String, String> pageObjects = null;
        try {
            Hashtable<String, String> requestHash = new Hashtable<String, String>();
            requestHash.put("site", this.siteName);
            requestHash.put("page", pageName);
            Object[] params = new Object[] { requestHash };

            pageObjects = (HashMap) client.execute(METHOD_PAGES_GET_ONE, params);
        } catch (XmlRpcException e) {
            //TODO: Not quite sure about the custom Exceptions
            if (e.getMessage().equals("Site does not exist")) {
                throw new SiteNotExistsException("We cannot find a site called " + this.siteName);
            } else if (e.getMessage().equals("HTTP server returned unexpected status: Unauthorized")) {
                throw new AuthorizationException("Unauthorized to access wikidot [" + this.siteName + "] using api key [" + this.apiKey + "]");

            } else {
                throw e;
            }
        }
        return pageObjects.get("content");
    }

    public Object[] listMethods() throws XmlRpcException {
        return (Object[]) client.execute(METHOD_SYSTEM_LIST_METHODS, new Object[]{});
    }

    public Object getMethodHelp(String methodName) throws AuthorizationException, XmlRpcException {
        try {
            Object[] methodNames = new Object[] {methodName};
            return client.execute(METHOD_SYSTEM_METHOD_HELP, methodNames);
        } catch (XmlRpcException e) {
            //TODO: Not quite sure about the custom Exceptions
            if (e.getMessage().equals("HTTP server returned unexpected status: Unauthorized")) {
                throw new AuthorizationException("Unauthorized to access wikidot [" + this.siteName + "] using api key [" + this.apiKey + "]");
            } else {
                throw e;
            }
        }
    }

    public void attachFileToPage(String pageName, String base64EncodedContent) {

    }

    /**
     * Adds a page to the wikidot site. The name of the page and its content are passed as parameters.
     *
     * @param pageName A {@link String} containing the name of the page to add.
     * @param content A {@link String} containing the content of the page.
     * @throws XmlRpcException An exception that is thrown when the remote procedure fails.
     */
    public void addPage(String pageName, String content) throws XmlRpcException {
        Hashtable<String, String> requestHash = new Hashtable<String, String>();
        requestHash.put("site", this.siteName);
        requestHash.put("page", pageName);
        requestHash.put("content", content);
        Object[] params = new Object[] { requestHash };

        HashMap pageObjects = (HashMap) client.execute(METHOD_PAGES_SAVE_ONE, params);
    }

    /**
     * Adds a binary file to an existing page on the wikidot site. The name of the page, the name of the attachment and
     * the base64 encoded binary content are passed as parameters.
     *
     * @param pageName A {@link String} containing the name of the page to which to add the binary file.
     * @param fileName A {@link String} containing the name of the binary file. Used as a name of the attachment.
     * @param base64EncodedFile A {@link String} containing the base64 encoded version of the binary file.
     * @throws XmlRpcException An exception that is thrown when the remote procedure fails.
     */
    public void addFile(String pageName, String fileName, String base64EncodedFile) throws XmlRpcException {
        Hashtable<String, String> requestHash = new Hashtable<String, String>();
        requestHash.put("site", this.siteName);
        requestHash.put("page", pageName);
        requestHash.put("file", fileName);
        requestHash.put("content", base64EncodedFile);
        Object[] params = new Object[] { requestHash };

        HashMap pageObjects = (HashMap) client.execute(METHOD_FILES_SAVE_ONE, params);
    }

    public HashMap<String, HashMap<String, String>> getMeta(String pageName) throws XmlRpcException {
        String[] pages = new String[] {pageName};
        Hashtable<String, Object> requestHash = new Hashtable<String, Object>();
        requestHash.put("site", this.siteName);
        requestHash.put("pages", pages);
        Object[] params = new Object[] { requestHash };

        return (HashMap<String, HashMap<String, String>>) client.execute(METHOD_PAGES_GET_META, params);
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
