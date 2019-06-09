package team.aurorahub.learn.tinyhttp.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
public class handlerImp implements handler {
    private config setting;

    public handlerImp(config myset) {
        setting = myset;
    }

    /**
     * This method call handle action imediately.
     * 
     * @param in  The InputStream
     * @param out The OutputStream
     * @apiNote This method is uncompleted.
     */
    @Override
    public void handle(InputStream in, OutputStream out) {
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
            answer.sendTo(out);
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
            answer.sendTo(out);
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
                    answer.sendTo(out);
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
                    answer.sendTo(out);
                } else {
                    response answer = new response(200);
                    String[] fileList = target.list();
                    answer.println("<html>", "utf-8");
                    answer.println("<head>", "utf-8");
                    answer.println("<title>Index of " + uri + "</title>", "utf-8");
                    answer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />",
                            "utf-8");
                    answer.println("</head>", "utf-8");
                    answer.println("<body bgcolor=\"white\">", "utf-8");
                    answer.println("<h1>Index of " + uri + "</h1>", "utf-8");
                    answer.println("<hr><pre>", "utf-8");
                    for (String f : fileList) {
                        answer.println("<a href=\"" + f +"\"/>" + f+"/</a>", "utf-8");
                    }
                    answer.println("</pre></hr>", "utf-8");
                    answer.println("</body>", "utf-8");
                    answer.println("</html>", "utf-8");
                    answer.setParam("Content-Type", "text/html; charset=utf-8");
                    answer.sendTo(out);
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
                answer.sendTo(out);
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
                answer.sendTo(out);
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
                    answer.sendTo(out);
                    break;
                }
                response answer = new response(200);
                answer.sendTo(out);
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
                answer.sendTo(out);
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
                answer.sendTo(out);
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
            answer.sendTo(out);
        }
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
}