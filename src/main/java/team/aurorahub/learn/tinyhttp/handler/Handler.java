package team.aurorahub.learn.tinyhttp.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Logger;

import team.aurorahub.learn.tinyhttp.config.Config;
import team.aurorahub.learn.tinyhttp.config.TinyLocation;
import team.aurorahub.learn.tinyhttp.model.Request;
import team.aurorahub.learn.tinyhttp.model.Response;
import team.aurorahub.learn.tinyhttp.tinyUtils.TinyLogger;

import static team.aurorahub.learn.tinyhttp.tinyUtils.IoTools.*;

/**
 * This class responsible for the event handle.
 * 
 * @author Chanth Miao
 * @version 1.0
 */
public class Handler implements Runnable {
    private Config setting;
    private Socket client;

    /**
     * Generate a Runable instance.
     * 
     * @param myset     The {@code Config} isntance.
     * @param newClient The new client socket.
     */
    public Handler(Config myset, Socket newClient) {
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
        Logger myLogger = TinyLogger.getTinyLogger();
        String remote = client.getRemoteSocketAddress().toString();
        int success = 0;
        Request clientMsg = new Request(in);
        int contentLen = clientMsg.readAllBytesNow();
        if (contentLen == -1) {
            // Invalid inputstream, close the stream.
            try {
                client.shutdownInput();
                client.shutdownOutput();
            } catch (IOException e) {
                System.err.println("Error when try to close the stream");
            }
            return;
        }
        String uri = clientMsg.getUri();
        String matchedUri = getRightLocation(uri);
        TinyLocation locationSetting = setting.getLocation(matchedUri);
        String httpMedtod = clientMsg.getHttpMethod();
        String root = setting.getRoot();
        if (clientMsg.getHttpVer().equals("HTTP/1.1") == false) {
            // We do not support other http version.
            Response answer = new Response(505);
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
            myLogger.info(remote + " " + uri + " " + httpMedtod + " " + 505);
        } else if (matchedUri == null || setting.getLocation(matchedUri).isAccessiable() == false) {
            Response answer = new Response(403);
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
            myLogger.info(remote + " " + uri + " " + httpMedtod + " " + 403);
        } else if (httpMedtod.equals("GET")) {
            switch (locationSetting.getHandlerType()) {
            case 0: {
                File target = getFileByUri(uri, root);
                if (target == null) {
                    Response answer = new Response(404);
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
                    myLogger.info(remote + " " + uri + " " + httpMedtod + " " + 404);
                } else if (target.isFile()) {
                    Response answer = new Response(200);
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
                    myLogger.info(remote + " " + uri + " " + httpMedtod + " " + 200);
                } else {
                    Response answer = new Response(200);
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
                    myLogger.info(remote + " " + uri + " " + httpMedtod + " " + 200);
                }
                break;
            }
            case 1: {
                Response answer = new Response(501);
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
                myLogger.info(remote + " " + uri + " " + httpMedtod + " " + 501);
                break;
            }
            default: {
                Response answer = new Response(501);
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
                myLogger.info(remote + " " + uri + " " + httpMedtod + " " + 501);
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
                    Response answer = new Response(500);
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
                    myLogger.info(remote + " " + uri + " " + httpMedtod + " " + 500);
                    break;
                }
                Response answer = new Response(200);
                success = answer.sendTo(out);
                myLogger.info(remote + " " + uri + " " + httpMedtod + " " + 200);
                break;
            }
            case 1: {
                Response answer = new Response(501);
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
                myLogger.info(remote + " " + uri + " " + httpMedtod + " " + 501);
                break;
            }
            default: {
                Response answer = new Response(501);
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
                myLogger.info(remote + " " + uri + " " + httpMedtod + " " + 501);
                break;
            }
            }
        } else {
            Response answer = new Response(405);
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
            myLogger.info(remote + " " + uri + " " + httpMedtod + " " + 405);
        }
        String ConnectionFlag = clientMsg.getParam("Connection");
        if (success == -1 || ConnectionFlag != null && ConnectionFlag.trim().equals("close")) {
            try {
                client.shutdownInput();
                client.shutdownOutput();
            } catch (IOException e) {
                myLogger.warning(remote + "error when closing the stream.");
            }
        }
    }

    @Override
    public void run() {
        Logger myLogger = TinyLogger.getTinyLogger();
        try {
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            while (client.isInputShutdown() == false && client.isOutputShutdown() == false) {
                handle(in, out);
            }
            if (client.isClosed() == false) {
                client.close();
                myLogger.info(client.getRemoteSocketAddress().toString() + " socket closed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            myLogger.warning(client.getRemoteSocketAddress().toString() + " errer when closing the socket.");
        }
    }
}