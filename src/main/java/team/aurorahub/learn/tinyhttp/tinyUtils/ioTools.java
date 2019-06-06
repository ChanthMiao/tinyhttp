package team.aurorahub.learn.tinyhttp.tinyUtils;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The ioTools class offers a tiny tool set to help your i/o operations.
 * 
 * @author Chanth Miao
 * @version 1.0
 */
public class ioTools {

    /**
     * Read the whole file into a String variable, then return it. It is quite
     * usefull with small files.
     * 
     * @param confPath The path of file to read.
     * @return A String that contains the content of the file. If The file to read
     *         is within the length limit, interal IOException will cause a null
     *         return.
     * @throws IOException When the length in byte of file to read is more than
     *                     Integer.MAX_VALUE, throw IOException.
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
     * Decoding tool for url. It makes it possible to pass un-ascii characters
     * through url safely.
     * 
     * @param url      A String that contains a url.
     * @param encoding The name of a supported character encoding.
     * @return Eecoded url String.
     */
    public static String tinyURLDecoding(String url, String encoding) {
        try {
            return URLDecoder.decode(url, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Try to encoding msg in String into bytes[] with given character set.
     * 
     * @param msg      The msg in String that to be encoded.
     * @param encoding The name of a supported character encoding.
     * @return Byte array taht converted from msg.
     */
    public static byte[] tinyStrEncoding(String msg, String encoding) {
        try {
            return msg.getBytes(encoding);
        } catch (Exception e) {
            System.out.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query the content type of a file.
     * 
     * @param path The path of file.
     * @return The content type
     */
    public static String getContentType(String path) {
        try {
            Path nioPath = Paths.get(path);
            return Files.probeContentType(nioPath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate a format uri that contains no character '\'
     * 
     * @param originURI The origin uri String.
     * @return formated uri String
     */
    public static String formatURI(String originURI) {
        return (originURI.replace("\\", "/").toLowerCase());
    }
}