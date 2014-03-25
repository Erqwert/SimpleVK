package com.localhost.simplevk.authentication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.localhost.simplevk.R;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;

import org.androidannotations.annotations.EFragment;

import java.net.URLEncoder;

@EFragment(R.layout.fragment_authentication)
public class AuthenticationFragment extends Fragment{

  private static final String APP_ID = "4255934";
  private static final String REDIRECT_URL = "http://oauth.vk.com/blank.html";
  private static final String SCOPE = "friends,wall,offline";

  protected WebView webView;

  VKSdkListener vkSdkListener;

  /**
   * Dynamically adding WebView to container.
   * If we loaded after configuration change - we restore cached page contents from Bundle
   * or loadURL otherwise.
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return
   */
  @SuppressWarnings("deprecation")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    initWebView();
    container.addView(webView);

    if (null == savedInstanceState) {
      webView.loadUrl("http://oauth.vk.com/authorize?client_id=" + APP_ID + "&scope=" + SCOPE + "&redirect_uri="+ URLEncoder.encode(REDIRECT_URL) + "&response_type=token");
    } else {
      webView.restoreState(savedInstanceState);
    }

    return super.onCreateView(inflater, container, savedInstanceState);
  }

  /**
   * Initializing WebView so we can load URLs or restore state, and make it VISIBLE
   */
  @SuppressLint("SetJavaScriptEnabled")
  protected void initWebView(){
    webView = new WebView(getActivity());
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setVerticalScrollBarEnabled(false);
    webView.setHorizontalScrollBarEnabled(false);
    webView.clearCache(true);
    webView.setWebViewClient(new VkWebViewClient());
    webView.setVisibility(View.VISIBLE);
  }

  /**
   * Setter for VKSdkListener
   * @param vkSdkListener
   */
  public void setVkSdkListener(VKSdkListener vkSdkListener) {
    this.vkSdkListener = vkSdkListener;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().setTitle("Авторизация");
  }

  /**
   * onSaveInstanceState we cache page contents to Bundle
   * @param outState
   */
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    webView.saveState(outState);
  }

  /**
   * Our WebView custom class. We start to parse redirect URL after page is finished loading.
   */
  class VkWebViewClient extends WebViewClient {
    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      parseUrl(url);
    }
  }

  /**
   * After we successfully parse URL - we initialize VKSdk and hide WebView.
   * @param url
   */
  private void parseUrl(String url) {
    try {
      if (url == null) {
        return;
      }
      if (url.startsWith(REDIRECT_URL)) {
        if (!url.contains("error")) {
          webView.setVisibility(View.GONE);

          url = url.replace(REDIRECT_URL+"#", "");

          VKSdk.initialize(vkSdkListener, APP_ID, VKAccessToken.tokenFromUrlString(url));
        } else {
          Log.e("AuthFragment", "error");
        }
      } else if (url.contains("error?err")) {
        Log.e("AuthFragment", "error");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Log.e("AuthFragment", "error");

    }
  }
}
