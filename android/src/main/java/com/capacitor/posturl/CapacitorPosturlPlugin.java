package com.capacitor.posturl;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.HashMap;
import java.util.Iterator;

@CapacitorPlugin(name = "CapacitorPosturl")
public class CapacitorPosturlPlugin extends Plugin {

    private CapacitorPosturl implementation = new CapacitorPosturl();

    @PluginMethod
    public void posturl(PluginCall call) {
        JSObject bodyJSObject = call.getObject("body");
        HashMap<String, String> body = jsObjectToStringHashMap(bodyJSObject);

        JSObject headersJSObject = call.getObject("headers", new JSObject());
        HashMap<String, String> headers = jsObjectToStringHashMap(headersJSObject);

        String url = call.getString("url");

        implementation.posturl(bridge.getWebView(), body, headers, url);

        call.resolve();
    }

    private static HashMap<String, String> jsObjectToStringHashMap(JSObject object) {
        HashMap<String, String> map = new HashMap<>();
        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            String value = object.getString(key);
            map.put(key, value);
        }
        return map;
    }
}

