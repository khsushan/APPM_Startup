package com.wso2.bean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ushan on 2/11/15.
 */
public abstract class AbstractRequest {

    public String action;
    private Map parameterMap = new HashMap<String, String>();
    private static final String ACTION_PARAMETER_VALUE = "action";

    public String generateRequestParameters() {
        parameterMap.clear();
        init();
        String requestParams = ACTION_PARAMETER_VALUE + "=" + action;
        Iterator<String> irt = parameterMap.keySet().iterator();
        while (irt.hasNext()) {
            String key = irt.next();
            requestParams = requestParams + "&" + key + "=" + parameterMap.get(key);
        }
        return requestParams;
    }

    public String generateRequestParameters(String actionName) {
        parameterMap.clear();
        setAction();
        init();
        String requestParams = ACTION_PARAMETER_VALUE + "=" + actionName;
        Iterator<String> irt = parameterMap.keySet().iterator();
        while (irt.hasNext()) {
            String key = irt.next();
            requestParams = requestParams + "&" + key + "=" + parameterMap.get(key);
        }
        return requestParams;
    }

    public void addParameter(String key, String value) {
        parameterMap.put(key, value);
    }

    public abstract void setAction();

    public abstract void init();

    public void setAction(String actionName) {
        this.action = actionName;
    }
}
