package com.capacitor.posturl;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@CapacitorPlugin(name = "CapacitorPosturl")
public class CapacitorPosturlPlugin extends Plugin {

    @PluginMethod
    public void posturl(PluginCall pluginCall) {
        JSObject bodyJSObject = pluginCall.getObject("body");
        JSObject headersJSObject = pluginCall.getObject("headers", new JSObject());
        JSObject webviewOptionsJSObject = pluginCall.getObject("webviewOptions", null);

        String url = pluginCall.getString("url");
        HashMap<String, String> body = jsObjectToStringHashMap(bodyJSObject);
        HashMap<String, String> headers = jsObjectToStringHashMap(headersJSObject);

        getActivity()
            .runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        String userAgent = bridge.getWebView().getSettings().getUserAgentString();
                        headers.put("User-Agent", userAgent);

                        final byte[] postDataBytes = getPostDataBytes(body);
                        Request request = getRequest(headers, url, postDataBytes);
                        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build();

                        client
                            .newCall(request)
                            .enqueue(
                                new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        e.printStackTrace();
                                        pluginCall.reject(e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(Call call, final Response response) throws IOException {
                                        if (!response.isSuccessful()) {
                                            pluginCall.reject("Unexpected code " + response);
                                        } else {
                                            if (webviewOptionsJSObject != null) {
                                                getActivity()
                                                    .runOnUiThread(
                                                        () -> {
                                                            Options options = getOptions(webviewOptionsJSObject);
                                                            options.setPluginCall(pluginCall);
                                                            WebViewDialog webViewDialog = getWebViewDialog(options, getContext());
                                                            WebView webView = webViewDialog.getWebView();
                                                            webView.addJavascriptInterface(
                                                                new Object() {
                                                                    @JavascriptInterface
                                                                    public void closeWindow() {
                                                                        notifyListeners("closeEvent", new JSObject().put("url", url));
                                                                        webViewDialog.dismiss();
                                                                    }
                                                                },
                                                                "AndroidInterface"
                                                            );

                                                            webView.setWebViewClient(
                                                                new WebViewClient() {
                                                                    @Override
                                                                    public void onPageFinished(WebView view, String url) {
                                                                        super.onPageFinished(view, url);
                                                                        view.loadUrl(
                                                                            "javascript:(function() {" +
                                                                            "window.close = function() {" +
                                                                            "    AndroidInterface.closeWindow();" +
                                                                            "};" +
                                                                            "})()"
                                                                        );
                                                                    }
                                                                }
                                                            );

                                                            final String cookieString = getCookieString(response, request);
                                                            CookieManager cookieManager = CookieManager.getInstance();
                                                            cookieManager.setCookie(url, cookieString);
                                                            webView.postUrl(url, postDataBytes);
                                                        }
                                                    );
                                            } else {
                                                WebView webview = bridge.getWebView();
                                                webview.post(
                                                    () -> {
                                                        final String cookieString = getCookieString(response, request);
                                                        CookieManager cookieManager = CookieManager.getInstance();
                                                        cookieManager.setCookie(url, cookieString);
                                                        webview.postUrl(url, postDataBytes);
                                                    }
                                                );
                                            }
                                            pluginCall.resolve();
                                        }
                                    }
                                }
                            );
                    }
                }
            );
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

    private Options getOptions(JSObject data) {
        var options = new Options();
        options.setShowReloadButton(data.getBoolean("showReloadButton", false));
        options.setVisibleTitle(data.getBoolean("visibleTitle", true));
        if (Boolean.TRUE.equals(options.getVisibleTitle())) {
            options.setTitle(data.getString("title", "New Window"));
        } else {
            options.setTitle(data.getString("title", ""));
        }
        options.setToolbarColor(data.getString("toolbarColor", "#ffffff"));
        options.setArrow(Boolean.TRUE.equals(data.getBoolean("showArrow", false)));

        options.setShareDisclaimer(data.getJSObject("shareDisclaimer"));
        options.setShareSubject(data.getString("shareSubject", null));
        options.setToolbarType(data.getString("toolbarType", ""));
        options.setPresentAfterPageLoad(data.getBoolean("isPresentAfterPageLoad", false));
        if (data.getBoolean("closeModal", false)) {
            options.setCloseModal(true);
            options.setCloseModalTitle(data.getString("closeModalTitle", "Close"));
            options.setCloseModalDescription(data.getString("closeModalDescription", "Are you sure ?"));
            options.setCloseModalOk(data.getString("closeModalOk", "Ok"));
            options.setCloseModalCancel(data.getString("closeModalCancel", "Cancel"));
        } else {
            options.setCloseModal(false);
        }
        options.setCallbacks(
            new WebViewCallbacks() {
                @Override
                public void urlChangeEvent(String url) {
                    notifyListeners("urlChangeEvent", new JSObject().put("url", url));
                }

                @Override
                public void closeEvent(String url) {
                    notifyListeners("closeEvent", new JSObject().put("url", url));
                }

                @Override
                public void pageLoaded() {
                    notifyListeners("browserPageLoaded", new JSObject());
                }

                @Override
                public void pageLoadError() {
                    notifyListeners("pageLoadError", new JSObject());
                }
            }
        );
        return options;
    }

    private static byte[] getPostDataBytes(HashMap<String, String> postData) {
        StringBuilder postDataStringBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : postData.entrySet()) {
            if (postDataStringBuilder.length() > 0) {
                postDataStringBuilder.append("&");
            }
            try {
                postDataStringBuilder
                    .append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        final byte[] postDataBytes = postDataStringBuilder.toString().getBytes();
        return postDataBytes;
    }

    @NonNull
    private static Request getRequest(HashMap<String, String> headers, String url, byte[] postDataBytes) {
        RequestBody body = RequestBody.create(postDataBytes, MediaType.parse("application/x-www-form-urlencoded"));
        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            requestBuilder.addHeader(header.getKey(), header.getValue());
        }
        Request request = requestBuilder.build();
        return request;
    }

    @NonNull
    private static String getCookieString(Response response, Request request) {
        List<Cookie> cookies = Cookie.parseAll(request.url(), response.headers());
        StringBuilder cookieString = new StringBuilder();
        for (int i = 0; i < cookies.size(); i++) {
            if (i > 0) {
                cookieString.append("; ");
            }
            cookieString.append(cookies.get(i).name()).append("=").append(cookies.get(i).value());
        }

        final String finalCookieString = cookieString.toString();
        return finalCookieString;
    }

    private WebViewDialog getWebViewDialog(Options options, Context context) {
        WebViewDialog webViewDialog = new WebViewDialog(context, android.R.style.Theme_NoTitleBar, options);
        webViewDialog.presentWebView();
        return webViewDialog;
    }
}
