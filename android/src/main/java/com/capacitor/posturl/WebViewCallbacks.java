package com.capacitor.posturl;

public interface WebViewCallbacks {
  public void urlChangeEvent(String url);

  public void closeEvent(String url);

  public void pageLoaded();

  public void pageLoadError();
}
