package team.aurorahub.learn.tinyhttp.config;

import com.alibaba.fastjson.*;

/**
 * This class descripts a location configure that controls the behavior of
 * specific URI.
 * 
 * @author Chanth Miao
 * @version 1.0
 */
public class tinyLocation implements validLC {
    private JSONObject settings;

    /**
     * Constuct an instance of tinyLocation to carry a 'location setting'.
     */
    public tinyLocation() {
        settings = new JSONObject();
    }

    /**
     * @param refJSONObj The outer data to be loaded.
     */
    @Override
    public void load(JSONObject refJSONObj) {
        settings = refJSONObj;
    }

    /**
     * Check the validity of the instance it belongs to.
     * 
     * @return True, if the instance is valid.
     */
    @Override
    public boolean check() {
        if (settings.containsKey("type") == false) {
            return false;
        } else if (settings.containsKey("handler") == false) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check if the location is accessiable.
     * 
     * @return True, if this loaction is accessiable.
     */
    @Override
    public Boolean isAccessiable() {
        if (settings.containsKey("accessiable")) {
            return settings.getBooleanValue("accessiable");
        }
        return true;
    }

    /**
     * Get the handler type of this location.
     * 
     * @return 0 for local static files, 1 for dynamic contents. Others are unknown.
     */
    @Override
    public int getHandlerType() {
        if (settings.containsKey("type")) {
            return settings.getIntValue("type");
        }
        return 0;
    }

    /**
     * Get the handler path of this loaction.
     * 
     * @return A String that descrpts the handler path.
     */
    @Override
    public String getHandlerPath() {
        if (settings.containsKey("handler")) {
            return settings.getString("handler");
        }
        return "plain";
    }

    /**
     * Set the handler type of this location.
     * 
     * @param type 0 for local static files, 1 for dynamic contents. Others are
     *             unknown.
     */
    @Override
    public void setHandlerType(int type) {
        settings.put("type", type);
    }

    /**
     * Set the handler path of this location.
     * 
     * @param handlerPath A String that descrpts the handler path.
     */
    @Override
    public void setHandlerPath(String handlerPath) {
        settings.put("handler", handlerPath);
    }

    /**
     * Set the accessiable of this location.
     * 
     * @param accessiable True, for accessiable.
     */
    @Override
    public void setAccessible(boolean accessiable) {
        settings.put("accessiable", accessiable);
    }
}

interface validLC {
    public void load(JSONObject refJSONObj);

    public boolean check();

    public Boolean isAccessiable();

    public void setAccessible(boolean accessiable);

    public int getHandlerType();

    public void setHandlerType(int type);

    public String getHandlerPath();

    public void setHandlerPath(String handlerPath);
}