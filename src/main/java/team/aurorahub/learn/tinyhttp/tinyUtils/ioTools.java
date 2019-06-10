package team.aurorahub.learn.tinyhttp.tinyUtils;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Set;

import org.apache.tika.Tika;

/**
 * The ioTools class offers a tiny tool set to help your i/o operations.
 * 
 * @author Chanth Miao
 * @version 1.0
 */
public class ioTools {

    /** Const String "\r\n" */
    public static final String CRLF = "\r\n";
    /** Const String " " */
    public static final String BLANK = " ";

    /**
     * Read the whole file into a String variable, then return it. It is quite
     * usefull with small files.
     * 
     * @param confPath The path of file to read.
     * @return A {@code String} that contains the content of the file. If The file
     *         to read is within the length limit, interal {@code IOException} will
     *         cause a {@code null} return.
     * @throws IOException When the length in byte of file to read is more than
     *                     {@code Integer.MAX_VALUE}, throw {@link IOException}.
     */
    public static String readToString(String confPath) throws IOException {
        final String encoding = "utf-8";
        File newFile = new File(confPath);
        long fileLength = newFile.length();
        long fileLenLimit = Integer.MAX_VALUE;
        if (fileLength > fileLenLimit) {
            throw new IOException("The file " + confPath + " is too big.");
        }
        byte[] fileContent = new byte[(int) fileLength];
        try {
            FileInputStream in = new FileInputStream(newFile);
            in.read(fileContent);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return new String(fileContent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.out.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decoding tool for query {@code String} It makes it possible to pass un-ascii
     * characters through url safely.
     * 
     * @param url      A String that contains a query {@code String}
     * @param encoding The name of a supported character encoding.
     * @return Decoded query {@code String}.
     */
    public static String tinyURLDecoding(String url, String encoding) {
        try {
            return URLDecoder.decode(url, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            return null;
        }
    }

    /**
     * Encoding tool for query String. It makes it possible to pass un-ascii
     * characters through url safely.
     * 
     * @param url      A {@code String} that contains k-v pairs that not supported
     *                 by ascii.
     * @param encoding The name of a supported character encoding.
     * @return Encoded query {@code String} that can be used in url. {@code null},
     *         if internal {@link #UnsupportedEncodingException} occurs.
     */
    public static String tinyURLEncoding(String origin, String encoding) {
        try {
            return URLEncoder.encode(origin, encoding).toLowerCase();
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            return null;
        }
    }

    /**
     * Try to encoding msg in String into {@code byte[]} with given character set.
     * 
     * @param msg      The msg in {@code String} that to be encoded.
     * @param encoding The name of a supported character encoding.
     * @return {@code byte[]} that converted from msg. {@code null}, if internal
     *         {@link #UnsupportedEncodingException} occurs.
     */
    public static byte[] tinyStrEncoding(String msg, String encoding) {
        try {
            return msg.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query the content type of a file.
     * 
     * @param target A instance of {@code File}.
     * @return The content type in {@code String}.
     */
    public static String getContentType(File target) {
        try {
            Tika tika = new Tika();
            return tika.detect(target);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate a format uri that contains no character '\'
     * 
     * @param originURI The origin uri {@code String}
     * @return formated uri {@code String}
     */
    public static String formatURI(String originURI) {
        return originURI.replace("\\", "/");
    }

    /**
     * Get and store query KV-pairs into HashMap<String, String> KVs
     * 
     * @param queryStr URI query String.
     * @param KVs      {@code HashMap<String, String>} instance used to store
     *                 KV-pairs
     * @return The number of KV-pairs, in {@code int}.
     */
    public static int getURLParams(String queryStr, HashMap<String, String> KVs) {
        String decodedQueryStr = tinyURLDecoding(queryStr, "utf-8");
        String[] params = decodedQueryStr.split("&");
        String[] kvStrs = null;
        for (String paramKV : params) {
            kvStrs = paramKV.split("=");
            if (kvStrs.length == 2) {
                KVs.put(kvStrs[0], kvStrs[1]);
            } else {
                continue;
            }
        }
        return params.length;
    }

    /**
     * This method restores a query in url safe format from KV
     * {@code HashMap<String, String>}.
     * 
     * @param KVs The {@code HashMap<String, String>} that stores KV pairs.
     * @return The encoded query {@code Strng}
     */
    public static String getURIQuery(HashMap<String, String> KVs) {
        StringBuffer origin = new StringBuffer();
        Set<String> keys = KVs.keySet();
        for (String key : keys) {
            origin.append(key + "=" + KVs.get(key) + "&");
        }
        int lastChar = origin.lastIndexOf("&");
        return tinyURLEncoding(origin.substring(0, lastChar), "utf-8");
    }

    /**
     * This method is used to 'safely' read one http header line from a given
     * {@code InputStreamReader}.
     * 
     * @param in An {@code InputStreamReader} used as data source.
     * @return A Line {@code String} without character {@code '\r'} and
     *         {@code '\n'}. A null
     * @apiNote For safety, this method is non-buffed. Beside, the InputStream must
     *          be ended by {@code CRLF}. Otherwise, this method will by blocked.
     */
    public static String safeHeaderLineReader(InputStreamReader in) {
        StringBuffer str = new StringBuffer(256);
        try {
            char[] ch = new char[1];
            in.read(ch, 0, 1);
            while (ch[0] != '\r' && ch[0] != '\n') {
                str.append(ch);
                in.read(ch, 0, 1);
            }
            if (ch[0] == '\r') {
                in.read(ch, 0, 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException occurs while reading http header lines.");
        }
        return str.toString();
    }

    /**
     * Get {@code File} instance by uri.
     * 
     * @param uri  The specific uri, ended with {@code '/'}
     * @param root The web root directory.
     * @return The {@code File} instance relative to uri. {@code null}, if not
     *         exists.
     */
    public static File getFileByUri(String uri, String root) {
        int len = uri.lastIndexOf('/');
        uri = uri.substring(0, len);
        String[] hiddenExt = new String[2];
        hiddenExt[0] = ".html";
        hiddenExt[1] = ".htm";
        String target = root + uri;
        File gotIt = new File(target);
        boolean isExists = gotIt.exists();
        if (isExists == false && target.indexOf('.') == -1) {
            return null;
        } else if (isExists == false) {
            for (String ext : hiddenExt) {
                target = uri + root + ext;
                gotIt = new File(target);
                isExists = gotIt.exists();
                if (isExists) {
                    break;
                }
            }
        }
        if (isExists) {
            return gotIt;
        } else {
            return null;
        }
    }

    /**
     * Get parse {@code String} of specific http status code.
     * 
     * @param code
     * @return
     */
    public static String getStatusParse(int code) {
        String parseStr = null;
        switch (code) {
        case 100:
            parseStr = "Continue";
            break;
        case 101:
            parseStr = "Switching Protocols";
            break;
        case 103:
            parseStr = "Early Hints";
            break;
        case 200:
            parseStr = "OK";
            break;
        case 201:
            parseStr = "Created";
            break;
        case 202:
            parseStr = "Accepted";
            break;
        case 203:
            parseStr = "Non-Authoritative Information";
            break;
        case 204:
            parseStr = "No Content";
            break;
        case 205:
            parseStr = "Reset Content";
            break;
        case 206:
            parseStr = "Partitial Content";
            break;
        case 300:
            parseStr = "Multiple Choices";
            break;
        case 301:
            parseStr = "Moved Permanently";
            break;
        case 302:
            parseStr = "Found";
            break;
        case 303:
            parseStr = "See Other";
            break;
        case 304:
            parseStr = "Not Modified!";
            break;
        case 307:
            parseStr = "Temporay Redirect";
            break;
        case 308:
            parseStr = "Permanent Redirect";
            break;
        case 400:
            parseStr = "Bad Request";
            break;
        case 401:
            parseStr = "Unauthorized";
            break;
        case 403:
            parseStr = "Forbidden";
            break;
        case 404:
            parseStr = "Not Found";
            break;
        case 405:
            parseStr = "Method Not Allowed";
            break;
        case 406:
            parseStr = "Not Acceptable";
            break;
        case 407:
            parseStr = "Proxy Authentication Required";
            break;
        case 408:
            parseStr = "Request Timeout";
            break;
        case 409:
            parseStr = "Conflict";
            break;
        case 410:
            parseStr = "Gone";
            break;
        case 411:
            parseStr = "Length Requred";
            break;
        case 412:
            parseStr = "Preconditon Failed";
            break;
        case 413:
            parseStr = "Payload Too Large";
            break;
        case 414:
            parseStr = "URI Too Long";
            break;
        case 415:
            parseStr = "Unsupported Media Type";
            break;
        case 416:
            parseStr = "Range Not Statisfiable";
            break;
        case 417:
            parseStr = "Expectation Failed";
            break;
        case 418:
            parseStr = "I'm a teapot";
            break;
        case 422:
            parseStr = "Unprocessable Entity";
            break;
        case 425:
            parseStr = "Too Early";
            break;
        case 426:
            parseStr = "Upgrade Required";
            break;
        case 428:
            parseStr = "Precondition Required";
            break;
        case 429:
            parseStr = "Too Many Requests";
            break;
        case 431:
            parseStr = "Request Header Fields Too Large";
            break;
        case 451:
            parseStr = "Unavailable For Legal Reasons";
            break;
        case 500:
            parseStr = "Internal Server Error";
            break;
        case 501:
            parseStr = "Not Implemented";
            break;
        case 502:
            parseStr = "Bad Gateway";
            break;
        case 503:
            parseStr = "Service Unavailable";
            break;
        case 504:
            parseStr = "Gateway Timeout";
            break;
        case 505:
            parseStr = "HTTPVersion Not Supported";
            break;
        case 511:
            parseStr = "Network Authentication Required";
            break;
        default:
            break;
        }
        return parseStr;
    }
}