package com.github.jasonhancn.androidtvcursordemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.jasonhancn.tvcursor.TvCursorActivity;

public class MainActivity extends TvCursorActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWebView();
        setScrollTargetView(webView);
        showCursor();
        // setCursorSize(200);
        setCursorResource(R.mipmap.hand, 100, 0, 50);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        webView = findViewById(R.id.webView);
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        //webView.loadUrl("https://muro.deviantart.com/");
        webView.loadUrl("http://www.baidu.com");
        webView.requestFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (isShowCursor()) {
                hideCursor();
            } else {
                showCursor();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
