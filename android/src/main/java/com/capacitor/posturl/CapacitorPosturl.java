package com.capacitor.posturl;

import android.webkit.CookieManager;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.*;


public class CapacitorPosturl {
    public void posturl(WebView webView, HashMap<String, String> postData, HashMap<String, String> headers, String url) {
        final byte[] postDataBytes = getPostDataBytes(postData);
        Request request = getRequest(headers, url, postDataBytes);
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    webView.post(() -> {
                        final String cookieString = getCookieString(response, request);
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.setCookie(url, cookieString);
                        webView.postUrl(url, postDataBytes);
                    });
                }
            }
        });
    }

    private static byte[] getPostDataBytes(HashMap<String, String> postData) {
        StringBuilder postDataStringBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : postData.entrySet()) {
            if (postDataStringBuilder.length() > 0) {
                postDataStringBuilder.append("&");
            }
            try {
                postDataStringBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
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
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);
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


}
