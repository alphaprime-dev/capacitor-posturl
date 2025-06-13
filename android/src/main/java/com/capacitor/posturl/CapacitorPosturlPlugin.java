package com.capacitor.posturl;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@CapacitorPlugin(name = "CapacitorPosturl")
public class CapacitorPosturlPlugin extends Plugin {

    @PluginMethod
    public void posturl(final PluginCall pluginCall) {
        JSObject bodyJSObject = pluginCall.getObject("body");
        JSObject headersJSObject = pluginCall.getObject("headers", new JSObject());
        JSObject webviewOptionsJSObject = pluginCall.getObject("webviewOptions", null);

        final String url = pluginCall.getString("url");
        if (url == null) {
            pluginCall.reject("url must be provided.");
            return;
        }
        final HashMap<String, String> body = jsObjectToStringHashMap(bodyJSObject);
        final HashMap<String, String> headers = jsObjectToStringHashMap(headersJSObject);

        if (webviewOptionsJSObject != null) {
            getActivity()
                .runOnUiThread(
                    () -> {
                        final byte[] postDataBytes = getPostDataBytes(body);

                        String userAgent = bridge.getWebView().getSettings().getUserAgentString();
                        headers.put("User-Agent", userAgent);

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
                                private boolean isInitialPostRequestHandled = false;

                                @Override
                                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                                    if (
                                        !isInitialPostRequestHandled &&
                                        request.getMethod().equalsIgnoreCase("POST") &&
                                        request.getUrl().toString().equalsIgnoreCase(url)
                                    ) {
                                        isInitialPostRequestHandled = true;
                                        try {
                                            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).build();
                                            RequestBody requestBody = RequestBody.create(postDataBytes, MediaType.parse("application/x-www-form-urlencoded"));

                                            Request.Builder requestBuilder = new Request.Builder().url(url).post(requestBody);
                                            for (Map.Entry<String, String> header : headers.entrySet()) {
                                                requestBuilder.addHeader(header.getKey(), header.getValue());
                                            }
                                            Request okHttpRequest = requestBuilder.build();

                                            Response response = client.newCall(okHttpRequest).execute();

                                            List<String> cookieHeaders = response.headers("Set-Cookie");
                                            CookieManager cookieManager = CookieManager.getInstance();
                                            for (String cookie : cookieHeaders) {
                                                cookieManager.setCookie(url, cookie);
                                            }
                                            cookieManager.flush();

                                            Map<String, String> responseHeadersMap = new HashMap<>();
                                            for (String name : response.headers().names()) {
                                                responseHeadersMap.put(name, response.headers().get(name));
                                            }

                                            return new WebResourceResponse(
                                                response.header("content-type", "text/html; charset=utf-8").split(";")[0].trim(),
                                                response.header("content-encoding", "utf-8"),
                                                response.code(),
                                                response.message(),
                                                responseHeadersMap,
                                                response.body().byteStream()
                                            );
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            getActivity().runOnUiThread(() -> pluginCall.reject("Request failed: " + e.getMessage()));
                                            return null;
                                        }
                                    }
                                    return super.shouldInterceptRequest(view, request);
                                }

                                @Override
                                public void onPageFinished(WebView view, String finishedUrl) {
                                    super.onPageFinished(view, finishedUrl);
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
                        webView.postUrl(url, postDataBytes);
                    }
                );
        } else {
            getActivity()
                .runOnUiThread(
                    () -> {
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
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        e.printStackTrace();
                                        pluginCall.reject(e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull final Response response) {
                                        if (!response.isSuccessful()) {
                                            pluginCall.reject("Unexpected code " + response);
                                        } else {
                                            List<String> cookieHeaders = response.headers("Set-Cookie");

                                            WebView webview = bridge.getWebView();
                                            webview.post(
                                                () -> {
                                                    CookieManager cookieManager = CookieManager.getInstance();
                                                    for (String cookie : cookieHeaders) {
                                                        cookieManager.setCookie(url, cookie);
                                                    }
                                                    cookieManager.flush();
                                                    webview.postUrl(url, postDataBytes);
                                                }
                                            );
                                            pluginCall.resolve();
                                        }
                                    }
                                }
                            );
                    }
                );
        }
    }

    private static HashMap<String, String> jsObjectToStringHashMap(JSObject object) {
        HashMap<String, String> map = new HashMap<>();
        if (object == null) {
            return map;
        }
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
                    if (options.getPluginCall() != null) {
                        options.getPluginCall().resolve();
                    }
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

        return postDataStringBuilder.toString().getBytes();
    }

    @NonNull
    private static Request getRequest(HashMap<String, String> headers, String url, byte[] postDataBytes) {
        RequestBody body = RequestBody.create(postDataBytes, MediaType.parse("application/x-www-form-urlencoded"));
        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            requestBuilder.addHeader(header.getKey(), header.getValue());
        }
        return requestBuilder.build();
    }

    private WebViewDialog getWebViewDialog(Options options, Context context) {
        WebViewDialog webViewDialog = new WebViewDialog(context, android.R.style.Theme_NoTitleBar, options);
        webViewDialog.presentWebView();
        return webViewDialog;
    }
} 