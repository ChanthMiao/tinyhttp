package team.aurorahub.learn.tinyhttp.model;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import static team.aurorahub.learn.tinyhttp.tinyUtils.IoTools.*;

/**
 * This class is able to descripts a http response.
 * 
 * @author Chanth Miao
 * @version 1.0
 * @see team.aurorahub.learn.tinyhttp.model.TinyHttpMsg
 */
public class Response extends TinyHttpMsg {

    protected String httpVer;
    protected int statusCode;
    protected String codeParse;
    protected boolean fromOuter;

    /**
     * Construct a {@code Response} instance from outer {@code InputStream}.
     * 
     * @param newInput The outer data stream.
     */
    public Response(InputStream newInput) {
        super(newInput);
        statusCode = -1;
        httpVer = null;
        codeParse = null;
        fromOuter = true;
    }

    /**
     * Construct a {@code Response} instance with uncompleted content.
     * 
     * @param code The http status code.
     */
    public Response(int code) {
        super(null);
        contentLen = 0;
        statusCode = code;
        httpVer = "HTTP/1.1";
        codeParse = getStatusParse(code);
        fromOuter = false;
        headerFields.put("Server", "tinyhttp");
        DateFormat formatDate = new SimpleDateFormat("EEE, dd MMM yyyy:mm:ss z", new Locale("en"));
        headerFields.put("Date", formatDate.format(new Date()));
        headerFields.put("Cache-Control", "no-cache");
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
        if (contentLen > -1 || fromOuter == false || inSocket == null) {
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
        tmp = safeHeaderLineReader(tinySocketReader);
        fields = tmp.split(BLANK);
        if (fields.length == 3) {
            httpVer = fields[0];
            statusCode = Integer.parseInt(fields[1]);
            codeParse = fields[2];
        } else {
            throw new RuntimeException("Not a http request!");
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
            if (headerFields.get("Connection").equals("close")) {
                inSocket.close();
            }
        } catch (IOException e) {
            contentLen = -1;
            e.printStackTrace();
        }
        return contentLen;
    }

    /**
     * Append {@code byte[]} into http body and update the http header fields
     * 'Content-Length' automatically.
     * 
     * @param src The {@code byte[]} data to write.
     * @param len The data length to append.
     * @return The real lenght of data appended.
     * @apiNote This method is dangerous to call, if you don't understand it's real
     *          effect. Besides, this method won't update 'Content-Type', which
     *          needs setting in advance manually.
     */
    public int writeBytes(byte[] src, int len) {
        if (len > src.length) {
            len = src.length;
        }
        OutputStream out = null;
        String contentEncoding = headerFields.get("Content-Encoding");
        try {
            if (contentEncoding == null) {
                out = tinyBody;
            } else if ((contentEncoding.equals("gzip"))) {
                out = new GZIPOutputStream(tinyBody);
            } else if ((contentEncoding.equals("deflate"))) {
                out = new DeflaterOutputStream(tinyBody);
            } else if ((contentEncoding.equals("identity"))) {
                out = tinyBody;
            } else {
                return -1;
            }
            out.write(src, 0, len);
            out.flush();
            contentLen += len;
            return len;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Append {@code String} param {@code msg} into http body and update the
     * 'Content-Length' automatically, by invoking {@link #writeBytes(byte[], int)}.
     * 
     * @param msg      The {@code String} to append.
     * @param encoding The specific charset.
     * @return The referenc to {@code Response} instance it self.
     * @apiNote This method is does not care about the header field 'Content-Type',
     *          which means it is necessary to set that field manually.
     */
    public Response print(String msg, String encoding) {
        byte[] msgBytes = tinyStrEncoding(msg, encoding);
        writeBytes(msgBytes, msgBytes.length);
        return this;
    }

    /**
     * Almost the same as {@link #print(String, String)}, just with a tailing line
     * wrapping.
     * 
     * @param msg      The {@code String} to append.
     * @param encoding The specific charset.
     * @return The referenc to {@code Response} instance it self.
     * @apiNote This method is does not care about the header field 'Content-Type',
     *          which means it is necessary to set that field manually.
     */
    public Response println(String msg, String encoding) {
        byte[] msgBytes = tinyStrEncoding(msg + CRLF, encoding);
        writeBytes(msgBytes, msgBytes.length);
        return this;
    }

    public Response loadFile(File file) {
        FileInputStream in;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("File not found");
            statusCode = 404;
            httpVer = "HTTP/1.1";
            codeParse = getStatusParse(statusCode);
            contentLen = 0;
            return this;
        }
        try {
            byte[] data = in.readAllBytes();
            writeBytes(data, data.length);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    protected String rebuildHeader() {
        if (contentLen == -1) {
            return null;
        } else {
            StringBuilder newHeader = new StringBuilder();
            String firstLine = httpVer + BLANK + statusCode + BLANK + getStatusParse(statusCode) + CRLF;
            newHeader.append(firstLine);
            // Update Content-length.
            headerFields.put("Content-Length", "" + contentLen);
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
}