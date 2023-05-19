package com.capacitor.posturl;

import android.webkit.CookieManager;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CapacitorPosturl {
    public void posturl(WebView webView, HashMap<String,String> postData, HashMap<String,String> headers, String url) {
        String urlParameters = createPostData(postData);
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(urlParameters, mediaType);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            requestBuilder.addHeader(header.getKey(), header.getValue());
        }
        Request request = requestBuilder.build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response.body().string());
                } else {
                    List<Cookie> cookies = Cookie.parseAll(request.url(), response.headers());
                    StringBuilder cookieString = new StringBuilder();
                    for (int i = 0; i < cookies.size(); i++) {
                        if (i > 0) {
                            cookieString.append("; ");
                        }
                        cookieString.append(cookies.get(i).name()).append("=").append(cookies.get(i).value());
                    }

                    final String finalCookieString = cookieString.toString();
                    String urlParameters = createPostData(postData);
                    byte[] postDataBytes = new byte[0];
                    try {
                        postDataBytes = urlParameters.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    byte[] finalPostDataBytes = postDataBytes;
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            CookieManager cookieManager = CookieManager.getInstance();
                            cookieManager.setCookie(url, finalCookieString);
                            webView.postUrl(url, finalPostDataBytes);
                        }
                    });
                }
            }
        });
    }

    private String createPostData(Map<String, String> params) {
        StringBuilder result = new StringBuilder();

        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (result.length() != 0) result.append('&');

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append('=');
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
