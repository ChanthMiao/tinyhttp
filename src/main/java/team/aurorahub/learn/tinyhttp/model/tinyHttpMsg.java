package team.aurorahub.learn.tinyhttp.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.DeflaterInputStream;

import static team.aurorahub.learn.tinyhttp.tinyUtils.ioTools.*;

public abstract class tinyHttpMsg {
    protected int contentLen;
    protected HashMap<String, String> headerFields;
    protected ByteArrayOutputStream tinyBody;
    protected InputStream inSocket;

    /**
     * Construct an instance that is able to descript a http request from specific
     * {@code InputStream}.
     * 
     * @param newInput The specific {@code InputStream}.
     */
    public tinyHttpMsg(InputStream newInput) {
        inSocket = newInput;
        tinyBody = new ByteArrayOutputStream(1024);
        contentLen = -1;
        headerFields = new HashMap<String, String>();
    }

    /**
     * This private method is invoked by public method
     * {@link #sendTo(OutputStream out)}
     * 
     * @return Http header in {@code String}
     */
    protected abstract String rebuildHeader();

    /**
     * Read the whole http msg in {@code byte[]} and get all header fields. Note!
     * this method works only once for the same instance.
     * 
     * @return The value of Content-Length. {@code -1} if {@link IOException}
     *         occurs. 0, if the field Content-Length does not exist.
     */
    public abstract int readAllBytesNow();

    /**
     * Get the Content-Length value.
     * 
     * @return A {@code int} variable that stores the Content-Length.
     */
    public int getContentLength() {
        return contentLen;
    }

    /**
     * Get param value of http header fields, in {@code String}.
     * 
     * @param key The key of specific param.
     * @return The param value, in {@code String}.
     */
    public String getParam(String key) {
        return headerFields.get(key);
    }

    /**
     * Set param value of http header fields, in {@code String}.
     * 
     * @param key The key of specific param.
     * @return The previous value assosiated with the key, in {@code String}.
     */
    public String setParam(String key, String value) {
        return headerFields.put(key, value);
    }

    /**
     * Send the http msg to specific {@code OutputStream} imidiately.
     * 
     * @param out The destination.
     * @return The {@code bytes} length of sended msg. {@code -1}, if it is called
     *         before {@link #readAllBytesNow()} method is completed.
     */
    public int sendTo(OutputStream out) {
        if (contentLen == -1) {
            return -1;
        }
        String newHeader = rebuildHeader();
        byte[] headerBytes = tinyStrEncoding(newHeader, "utf-8");
        try {
            out.write(headerBytes, 0, headerBytes.length);
            if (contentLen > 0) {
                tinyBody.writeTo(out);
            }
            out.flush();
            return headerBytes.length + contentLen;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Transfer the orignal http msg to specific {@code OutputStream} imidiately,
     * without header fields reading.
     * 
     * @param out The destination.
     * @return The {@code bytes} length of sended msg. {@code -1}, if it is called
     *         after {@link #readAllBytesNow()} method is completed.
     */
    public long transferTo(OutputStream out) {
        if (contentLen > -1) {
            return -1;
        }
        try {
            return inSocket.transferTo(out);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Get an InputStream instance that assosiated with the copy of current http
     * body content.
     * 
     * @apiNote If you are sure that the body content can be decoded as text, we
     *          seguesst you to wrap it with a Reader
     * 
     *          <pre>
     *          new InputStreamReader(request.getInputStream());
     *          </pre>
     * 
     * @return The specific instance of {@code InputStream}. null, if the
     *         Content-Encoding is unsupported,
     */
    public InputStream getInputStream() {
        ByteArrayInputStream origin = new ByteArrayInputStream(tinyBody.toByteArray());
        String contentEncoding = headerFields.get("Content-Encoding");
        if (contentEncoding == null) {
            return origin;
        }
        try {
            if ((contentEncoding.equals("gzip"))) {
                return new GZIPInputStream(origin);
            } else if ((contentEncoding.equals("deflate"))) {
                return new DeflaterInputStream(origin);
            } else if ((contentEncoding.equals("identity"))) {
                return origin;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}