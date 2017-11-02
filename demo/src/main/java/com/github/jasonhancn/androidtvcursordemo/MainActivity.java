package com.github.jasonhancn.androidtvcursordemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.jasonhancn.tvcursor.TvCursorActivity;

public class MainActivity extends TvCursorActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWebView();
        showCursor();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebView webView = findViewById(R.id.webView);
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("https://muro.deviantart.com/");
        webView.requestFocus();
    }
}
