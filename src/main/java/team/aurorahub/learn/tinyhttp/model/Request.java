package team.aurorahub.learn.tinyhttp.model;

import java.io.*;
import java.util.*;

import static team.aurorahub.learn.tinyhttp.tinyUtils.IoTools.*;

/**
 * This class is able to descript a http request.
 * 
 * @author Chanth Miao
 * @version 1.0
 * @see team.aurorahub.learn.tinyhttp.model.TinyHttpMsg
 */
public class Request extends TinyHttpMsg {
    protected String tinyUri;
    protected String tinyMethod;
    protected String httpVer;
    protected String tinyQuery;
    protected int queryNums;
    protected HashMap<String, String> queryParams;

    /**
     * Construct an instance that is able to descript a http request from specific
     * {@code InputStream}.
     * 
     * @param newInput The specific {@code InputStream}.
     */
    public Request(InputStream newInput) {
        super(newInput);
        queryParams = new HashMap<String, String>();
        tinyUri = null;
        tinyQuery = null;
        tinyMethod = null;
        httpVer = null;
    }

    /**
     * Read the whole http msg in {@code byte[]} and get all header fields. Note!
     * this method works only once for the same instance.
     * 
     * @return The value of Content-Length. {@code -1} if {@link IOException}
     *         occurs. 0, if the field Content-Length does not exist.
     */
    @Override
    public int readAllBytesNow() {
        if (contentLen > -1) {
            return contentLen;
        }
        InputStreamReader tinySocketReader;
        try {
            tinySocketReader = new InputStreamReader(inSocket, "utf-8");
        } catch (UnsupportedEncodingException e) {
            tinySocketReader = new InputStreamReader(inSocket);
            e.printStackTrace();
            System.err.println("Unexpected error, use default charset instead.");
        }
        String tmp = null;
        String[] fields = null;
        try {
            tmp = safeHeaderLineReader(tinySocketReader);
        } catch (OutOfMemoryError e) {
            contentLen = -1;
            return contentLen;
        }
        if (tmp == null) {
            System.err.println("Not a http request, ignore it.");
            contentLen = -1;
            return contentLen;
        }
        fields = tmp.split(BLANK);
        if (fields.length == 3) {
            tinyMethod = fields[0];
            tinyUri = fields[1];
            httpVer = fields[2];
        } else {
            System.err.println("Not a http request, ignore it.");
            contentLen = -1;
            try {
                if (contentLen > 0) {
                    inSocket.transferTo(tinyBody);
                }
            } catch (IOException e) {
                contentLen = -1;
                System.err.println("Error when try to ignore the rest ditry data.");
                e.printStackTrace();
            }
            return contentLen;
        }
        // Format it.
        if (tinyUri.endsWith("/") == false) {
            tinyUri = tinyUri + "/";
        }
        if (tinyUri.indexOf('?') != -1) {
            fields = tinyUri.split("\\?");
            tinyUri = fields[0];
            tinyQuery = fields[1];
            queryNums = getURLParams(fields[1], queryParams);
        }
        tmp = safeHeaderLineReader(tinySocketReader);
        while (tmp.length() > 0) {
            fields = tmp.split(":");
            headerFields.put(fields[0].trim(), fields[1].trim());
            tmp = safeHeaderLineReader(tinySocketReader);
        }
        String contentLenStr = headerFields.get("Content-Length");
        if (contentLenStr != null) {
            contentLen = Integer.parseInt(contentLenStr);
        } else {
            contentLen = 0;
        }
        try {
            if (contentLen > 0) {
                inSocket.transferTo(tinyBody);
            }
        } catch (IOException e) {
            contentLen = -1;
            System.err.println("Error when try to save the http body content.");
            e.printStackTrace();
        }
        return contentLen;
    }

    /**
     * This private method is invoked by public method
     * {@link #sendTo(OutputStream out)}
     * 
     * @return Http header in {@code String}
     */
    @Override
    protected String rebuildHeader() {
        if (contentLen == -1) {
            return null;
        } else {
            StringBuilder newHeader = new StringBuilder();
            String firstLine;
            if (queryNums > 0) {
                firstLine = tinyMethod + BLANK + tinyUri + "?" + tinyQuery + BLANK + httpVer + CRLF;
            } else {
                firstLine = tinyMethod + BLANK + tinyUri + BLANK + httpVer + CRLF;
            }
            newHeader.append(firstLine);
            Set<String> keys = headerFields.keySet();
            String oneLine;
            for (String key : keys) {
                oneLine = key + ": " + headerFields.get(key) + CRLF;
                newHeader.append(oneLine);
            }
            newHeader.append(CRLF);
            return newHeader.toString();
        }
    }

    /**
     * Get the URI {@code String}. Meanless to call it before
     * {@link #readAllBytesNow()} method is completed.
     * 
     * @return uri
     */
    public String getUri() {
        return tinyUri;
    }

    /**
     * Get the Http method {@code String}.
     * 
     * @return http method.
     */
    public String getHttpMethod() {
        return tinyMethod;
    }

    /**
     * Get the Http version {@code String}
     * 
     * @return http version.
     */
    public String getHttpVer() {
        return httpVer;
    }
}