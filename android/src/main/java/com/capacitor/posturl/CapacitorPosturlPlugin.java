package com.capacitor.posturl;

import android.util.Log;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONException;

@CapacitorPlugin(name = "CapacitorPosturl")
public class CapacitorPosturlPlugin extends Plugin {

    private CapacitorPosturl implementation = new CapacitorPosturl();

    @PluginMethod
    public void posturl(PluginCall call) {
        JSObject bodyJSObject = call.getObject("body");
        HashMap<String, String> body = jsObjectToHashMap(bodyJSObject);
        JSObject headersJSObject = call.getObject("headers", new JSObject());
        HashMap<String, String> headers = jsObjectToHashMap(headersJSObject);
        String url = call.getString("url");
        implementation.posturl(bridge.getWebView(), body, headers, url);
        call.resolve();
    }

    private HashMap<String, String> jsObjectToHashMap(JSObject jsObject) {
        HashMap<String, String> map = new HashMap<>();
        for (Iterator<String> it = jsObject.keys(); it.hasNext();) {
            String key = it.next();
            try {
                map.put(key, jsObject.get(key).toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }
}
