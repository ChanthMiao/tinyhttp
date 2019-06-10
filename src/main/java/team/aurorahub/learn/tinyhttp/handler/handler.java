package team.aurorahub.learn.tinyhttp.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

import team.aurorahub.learn.tinyhttp.config.config;
import team.aurorahub.learn.tinyhttp.config.tinyLocation;
import team.aurorahub.learn.tinyhttp.model.request;
import team.aurorahub.learn.tinyhttp.model.response;

import static team.aurorahub.learn.tinyhttp.tinyUtils.ioTools.*;

/**
 * This class responsible for the event handle.
 * 
 * @author Chanth Miao
 * @version 1.0
 */
public class handler implements Runnable {
    private config setting;
    private Socket client;

    /**
     * Generate a Runable instance.
     * 
     * @param myset     The config isntance.
     * @param newClient The new client socket.
     */
    public handler(config myset, Socket newClient) {
        setting = myset;
        client = newClient;
    }

    /**
     * This method is used to find the longest match uri.
     * 
     * @param uri The uri to match.
     * @return The longest match uri. May be {@code null}, if nothing matched.
     */
    private String getRightLocation(String uri) {
        LinkedList<String> paths = setting.getPaths();
        int preLen = 0;
        String rt = null;
        for (String path : paths) {
            if (uri.startsWith(path) && path.length() > preLen) {
                preLen = path.length();
                rt = path;
            }
        }
        return rt;
    }

    /**
     * This method call handle action imediately. Invoked by {@link #run()}
     * 
     * @param in  The InputStream
     * @param out The OutputStream
     * @apiNote This method is uncompleted.
     */
    private void handle(InputStream in, OutputStream out) {
        int success = 0;
        request clientMsg = new request(in);
        clientMsg.readAllBytesNow();
        clientMsg.getHttpVer();
        // We do not support other http version.
        if (clientMsg.getHttpVer().equals("HTTP/1.1") == false) {
            response answer = new response(505);
            answer.println("<html>", "utf-8");
            answer.println("<head>", "utf-8");
            answer.println("<title>505 unsupported http version</title>", "utf-8");
            answer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />", "utf-8");
            answer.println("</head>", "utf-8");
            answer.println("<body bgcolor=\"white\">", "utf-8");
            answer.println("<h1>505 ERROR</h1>", "utf-8");
            answer.println("<p>Sorry, tinyhttp dose not suporrt your http version.</p>", "utf-8");
            answer.println("</head>", "utf-8");
            answer.println("</body>", "utf-8");
            answer.println("</html>", "utf-8");
            answer.setParam("Content-Type", "text/html; charset=utf-8");
            success = answer.sendTo(out);
        }
        String uri = clientMsg.getUri();
        String matchedUri = getRightLocation(uri);
        if (matchedUri == null || setting.getLocation(matchedUri).isAccessiable() == false) {
            response answer = new response(403);
            answer.println("<html>", "utf-8");
            answer.println("<head>", "utf-8");
            answer.println("<title>403 Forbidden</title>", "utf-8");
            answer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />", "utf-8");
            answer.println("</head>", "utf-8");
            answer.println("<body bgcolor=\"white\">", "utf-8");
            answer.println("<h1>403 Forbidden</h1>", "utf-8");
            answer.println("<p>The resources you required is unaccessiable.</p>", "utf-8");
            answer.println("</head>", "utf-8");
            answer.println("</body>", "utf-8");
            answer.println("</html>", "utf-8");
            answer.setParam("Content-Type", "text/html; charset=utf-8");
            success = answer.sendTo(out);
        }
        tinyLocation locationSetting = setting.getLocation(matchedUri);
        String httpMedtod = clientMsg.getHttpMethod();
        String root = setting.getRoot();
        if (httpMedtod.equals("GET")) {
            switch (locationSetting.getHandlerType()) {
            case 0: {
                File target = getFileByUri(uri, root);
                if (target == null) {
                    response answer = new response(404);
                    answer.println("<html>", "utf-8");
                    answer.println("<head>", "utf-8");
                    answer.println("<title>404 Not Found</title>", "utf-8");
                    answer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />",
                            "utf-8");
                    answer.println("</head>", "utf-8");
                    answer.println("<body bgcolor=\"white\">", "utf-8");
                    answer.println("<h1>404 Not Found</h1>", "utf-8");
                    answer.println("<p>The file you request dose not exist.</p>", "utf-8");
                    answer.println("</body>", "utf-8");
                    answer.println("</html>", "utf-8");
                    answer.setParam("Content-Type", "text/html; charset=utf-8");
                    success = answer.sendTo(out);
                } else if (target.isFile()) {
                    response answer = new response(200);
                    answer.loadFile(target);
                    String contentType = getContentType(target);
                    if (contentType.startsWith("text/")) {
                        answer.setParam("Content-Type", contentType + "; charset=utf-8");
                    } else if (contentType.endsWith("/json")) {
                        answer.setParam("Content-Type", contentType + "; charset=utf-8");
                    } else {
                        answer.setParam("Content-Type", contentType);
                    }
                    success = answer.sendTo(out);
                } else {
                    response answer = new response(200);
                    File[] fileList = target.listFiles();
                    answer.println("<html>", "utf-8");
                    answer.println("<head>", "utf-8");
                    answer.println("<title>Index of " + uri + "</title>", "utf-8");
                    answer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />",
                            "utf-8");
                    answer.println("</head>", "utf-8");
                    answer.println("<body bgcolor=\"white\">", "utf-8");
                    answer.println("<h1>Index of " + uri + "</h1>", "utf-8");
                    answer.println("<hr>", "utf-8");
                    answer.println("<pre>", "utf-8");
                    answer.println("<a href=\"..\"/>../</a>", "utf-8");
                    for (File f : fileList) {
                        if (f.isDirectory()) {
                            answer.println("<a href=\"" + f.getName() + "/\">" + f.getName() + "/</a>", "utf-8");
                        } else {
                            answer.println("<a href=\"" + f.getName() + "\">" + f.getName() + "/</a>", "utf-8");
                        }
                    }
                    answer.println("</pre>", "utf-8");
                    answer.println("<hr>", "utf-8");
                    answer.println("</body>", "utf-8");
                    answer.println("</html>", "utf-8");
                    answer.setParam("Content-Type", "text/html; charset=utf-8");
                    success = answer.sendTo(out);
                }
                break;
            }
            case 1: {
                response answer = new response(501);
                answer.println("<html>", "utf-8");
                answer.println("<head>", "utf-8");
                answer.println("<title>501 Not Implemented</title>", "utf-8");
                answer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />", "utf-8");
                answer.println("</head>", "utf-8");
                answer.println("<body bgcolor=\"white\">", "utf-8");
                answer.println("<h1>501 Not Implemented</h1>", "utf-8");
                answer.println("<p>Function not inplemented.</p>", "utf-8");
                answer.println("</body>", "utf-8");
                answer.println("</html>", "utf-8");
                answer.setParam("Content-Type", "text/html; charset=utf-8");
                success = answer.sendTo(out);
                break;
            }
            default: {
                response answer = new response(501);
                answer.println("<html>", "utf-8");
                answer.println("<head>", "utf-8");
                answer.println("<title>501 Not Implemented</title>", "utf-8");
                answer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />", "utf-8");
                answer.println("</head>", "utf-8");
                answer.println("<body bgcolor=\"white\">", "utf-8");
                answer.println("<h1>501 Not Implemented</h1>", "utf-8");
                answer.println("<p>Function not inplemented.</p>", "utf-8");
                answer.println("</body>", "utf-8");
                answer.println("</html>", "utf-8");
                answer.setParam("Content-Type", "text/html; charset=utf-8");
                success = answer.sendTo(out);
                break;
            }
            }
        } else if (httpMedtod.equals("POST")) {
            switch (locationSetting.getHandlerType()) {
            case 0: {
                InputStream input = clientMsg.getInputStream();
                try {
                    FileOutputStream file = new FileOutputStream(root + uri);
                    input.transferTo(file);
                    file.flush();
                    file.close();
                } catch (IOException e) {
                    response answer = new response(500);
                    answer.println("<html>", "utf-8");
                    answer.println("<head>", "utf-8");
                    answer.println("<title>500 Internal Server Error</title>", "utf-8");
                    answer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />",
                            "utf-8");
                    answer.println("</head>", "utf-8");
                    answer.println("<body bgcolor=\"white\">", "utf-8");
                    answer.println("<h1>500 Internal Server Error</h1>", "utf-8");
                    answer.println("<p>We have some unexpected errors.</p>", "utf-8");
                    answer.println("</body>", "utf-8");
                    answer.println("</html>", "utf-8");
                    answer.setParam("Content-Type", "text/html; charset=utf-8");
                    success = answer.sendTo(out);
                    break;
                }
                response answer = new response(200);
                success = answer.sendTo(out);
                break;
            }
            case 1: {
                response answer = new response(501);
                answer.println("<html>", "utf-8");
                answer.println("<head>", "utf-8");
                answer.println("<title>501 Not Implemented</title>", "utf-8");
                answer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />", "utf-8");
                answer.println("</head>", "utf-8");
                answer.println("<body bgcolor=\"white\">", "utf-8");
                answer.println("<h1>501 Not Implemented</h1>", "utf-8");
                answer.println("<p>Function not inplemented.</p>", "utf-8");
                answer.println("</body>", "utf-8");
                answer.println("</html>", "utf-8");
                answer.setParam("Content-Type", "text/html; charset=utf-8");
                success = answer.sendTo(out);
                break;
            }
            default: {
                response answer = new response(501);
                answer.println("<html>", "utf-8");
                answer.println("<head>", "utf-8");
                answer.println("<title>501 Not Implemented</title>", "utf-8");
                answer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />", "utf-8");
                answer.println("</head>", "utf-8");
                answer.println("<body bgcolor=\"white\">", "utf-8");
                answer.println("<h1>501 Not Implemented</h1>", "utf-8");
                answer.println("<p>Function not inplemented.</p>", "utf-8");
                answer.println("</body>", "utf-8");
                answer.println("</html>", "utf-8");
                answer.setParam("Content-Type", "text/html; charset=utf-8");
                success = answer.sendTo(out);
                break;
            }
            }
        } else {
            response answer = new response(405);
            answer.println("<html>", "utf-8");
            answer.println("<head>", "utf-8");
            answer.println("<title>405 Method Not Allowed</title>", "utf-8");
            answer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />", "utf-8");
            answer.println("</head>", "utf-8");
            answer.println("<body bgcolor=\"white\">", "utf-8");
            answer.println("<h1>405 Method Not Allowed</h1>", "utf-8");
            answer.println("<p>The http method you used is not allowed.</p>", "utf-8");
            answer.println("</body>", "utf-8");
            answer.println("</html>", "utf-8");
            answer.setParam("Content-Type", "text/html; charset=utf-8");
            success = answer.sendTo(out);
        }
        String ConnectionFlag = clientMsg.getParam("Connection");
        if (success == -1 || ConnectionFlag != null && ConnectionFlag.trim().equals("close")) {
            try {
                // in.close();
                // out.close();
                client.shutdownInput();
                client.shutdownOutput();
            } catch (IOException e) {
                System.err.println("Error when try to close the stream");
            }
        }
    }

    @Override
    public void run() {
        try {
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            while (client.isInputShutdown() == false && client.isOutputShutdown() == false) {
                handle(in, out);
            }
            if (client.isClosed() == false) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}