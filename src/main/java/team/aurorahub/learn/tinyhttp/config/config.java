package team.aurorahub.learn.tinyhttp.config;

import static team.aurorahub.learn.tinyhttp.tinyUtils.ioTools.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import com.alibaba.fastjson.*;

/**
 * This class descripts the configure of tinyHttp application.
 * 
 * @author Chanth Miao
 * @version 1.0
 * @see team.aurorahub.learn.tinyhttp.config.tinyLocation
 */
public class config {
    private String configFilePath;
    private boolean customed;
    private String domain;
    private int port;
    private String root;
    private boolean redirect;
    private String redirectHost;
    private int redirectPort;
    private LinkedList<String> paths;
    private HashMap<String, tinyLocation> locations;

    /**
     * Construct an instance of config with a default content.
     */
    public config() {
        configFilePath = null;
        String webDir = System.getProperty("user.home");
        port = 80;
        root = webDir + "/sampleWeb";
        redirect = false;
        redirectHost = null;
        redirectPort = -1;
        paths = new LinkedList<String>();
        paths.add("/");
        tinyLocation DefaultPath = new tinyLocation();
        DefaultPath.setAccessible(true);
        DefaultPath.setHandlerType(0);
        DefaultPath.setHandlerPath("plain");
        customed = false;
        locations = new HashMap<String, tinyLocation>();
        locations.put(paths.get(0), DefaultPath);
    }

    /**
     * Construct an instance of config with given config file path.
     * 
     * @param confPath The path of customed config file.
     */
    public config(String confPath) {
        this();
        configFilePath = formatURI(confPath);
        String confStr;
        try {
            confStr = readToString(configFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
            // Unreachable
        }
        JSONObject confObject = JSONObject.parseObject(confStr);
        if (confObject != null) {
            customed = true;
            if (customed && confObject.containsKey("domain")) {
                domain = confObject.getString("domain");
            } else {
                customed = false;
            }
            if (customed && confObject.containsKey("port")) {
                port = confObject.getIntValue("port");
            } else {
                customed = false;
            }
            if (customed && confObject.containsKey("root")) {
                root = confObject.getString("root");
                root = formatURI(root);
            } else {
                customed = false;
            }
            if (customed && confObject.containsKey("redirect")) {
                redirect = confObject.getBooleanValue("redirect");
                if (redirect && confObject.containsKey("redirectHost") && confObject.containsKey("redirectPort")) {
                    redirectHost = confObject.getString("redirectHost");
                    redirectPort = confObject.getIntValue("redirectPort");
                } else if (redirect) {
                    customed = false;
                }
            } else {
                customed = false;
            }
            if (customed && confObject.containsKey("paths")) {
                JSONArray pathArrayJ = confObject.getJSONArray("paths");

                String[] pathsArray = pathArrayJ.toArray(new String[0]);
                for (String path : pathsArray) {
                    if (customed && confObject.containsKey(path)) {
                        tinyLocation curr = new tinyLocation();
                        curr.load(confObject.getJSONObject(path));
                        if (curr.check()) {
                            // Update the location settings.
                            path = formatURI(path);
                            locations.put(path, curr);
                            paths.add(path);
                        } else {
                            customed = false;
                        }
                    } else {
                        customed = false;
                    }
                }
                if (customed) {
                    paths.removeFirst();
                }
            } else {
                customed = false;
            }
        } else {
            customed = false;
        }
        if (customed == false) {
            // Restore to default values.
            String webDir = System.getProperty("user.home");
            port = 80;
            root = webDir + "/sampleWeb";
            redirect = false;
            paths = new LinkedList<String>();
            paths.add("/");
            tinyLocation DefaultPath = new tinyLocation();
            DefaultPath.setAccessible(true);
            DefaultPath.setHandlerType(0);
            DefaultPath.setHandlerPath("plain");
            customed = false;
            locations = new HashMap<String, tinyLocation>();
            locations.put(paths.get(0), DefaultPath);
        }
    }

    /**
     * Get the path of used config file.
     * 
     * @return The path of current config file.
     */
    public String getConfigFilePath() {
        return configFilePath;
    }

    /**
     * Get the domain of website.
     * 
     * @return the domain of this website
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Set the domain of this website.
     * 
     * @param newDomain The domain to be setted.
     */
    public void setDomain(String newDomain) {
        domain = newDomain;
    }

    /**
     * Get the listened port of this website.
     * 
     * @return The port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port number.
     * 
     * @param newPort an integer from 0 to 65535
     */
    public void setPort(int newPort) {
        port = newPort;
    }

    /**
     * Get all the location paths in the format of {@code  LinkedList<String>}
     * 
     * @return A {@code  LinkedList<String>} that contains all the location paths.
     */
    public LinkedList<String> getPaths() {
        return paths;
    }

    /**
     * Get specific {@link team.aurorahub.learn.tinyhttp.config.tinyLocation
     * tinyLocation} acroding to given path.
     * 
     * @param path A str that can be used asthe label of tinyLoaction.
     * @return A {@link team.aurorahub.learn.tinyhttp.config.tinyLocation
     *         tinyLocation} instance.
     */
    public tinyLocation getLocation(String path) {
        return locations.get(path);
    }

    /**
     * Add {@link team.aurorahub.learn.tinyhttp.config.tinyLocation tinyLocation}
     * into current config. The paths list will also be updated.
     * 
     * @param newLocation The instance of
     *                    {@link team.aurorahub.learn.tinyhttp.config.tinyLocation
     *                    tinyLocation}
     * @param newPath     The label for new
     *                    {@link team.aurorahub.learn.tinyhttp.config.tinyLocation
     *                    tinyLocation}, in {@code String}.
     */
    public void addLocation(tinyLocation newLocation, String newPath) {
        if (newLocation.check() == false) {
            return;
        }
        String formatPath = formatURI(newPath);
        if (locations.containsKey(formatPath) == false) {
            paths.add(formatPath);
        }
        locations.put(formatPath, newLocation);
    }

    /**
     * Get the root path of this website.
     * 
     * @return The root path of current website.
     */
    public String getRoot() {
        return root;
    }

    /**
     * Set the root path of this website.
     */
    public void setRoot(String newRoot) {
        root = formatURI(newRoot);
    }

    /**
     * Check if this website should be redirect.
     * 
     * @return {@code true} if it is redirected.
     */
    public boolean isRedirect() {
        return redirect;
    }

    /**
     * Set the redirect flag to true or false.
     * 
     * @param NewRedirect The {@code boolean} flag.
     */
    public void setRedirect(boolean NewRedirect) {
        redirect = NewRedirect;
    }

    /**
     * Get the redirect host in {@code String} if it exists.
     * 
     * @return The redirct host. If it does not exist, return {@code null}.
     */
    public String getRedirectHost() {
        return redirectHost;
    }

    /**
     * Set the redirect host, useless when the isRedirect flag is {@code false}.
     * 
     * @param newRedirectHost The new rendirect host.
     */
    public void setRedirectHost(String newRedirectHost) {
        redirectHost = newRedirectHost;
    }

    /**
     * Get the redirect port number of this website.
     * 
     * @return The redirect port number. may be {@code -1} when the isRedirect flag
     *         is {@code false}.
     */
    public int getRedirectPort() {
        return redirectPort;
    }

    /**
     * Set the redirct port number, useless when the isRedirect flag is
     * {@code false}.
     * 
     * @param newRedirectPort The new redirect port.
     */
    public void setRedirectPort(int newRedirectPort) {
        redirectPort = newRedirectPort;
    }
}