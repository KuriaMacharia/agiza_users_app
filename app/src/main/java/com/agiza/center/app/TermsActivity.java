package com.agiza.center.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        WebView webView = (WebView) findViewById(R.id.web_terms);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.termsfeed.com/disclaimer/4de9cb2fcf2967b2de8e32b55b422cd8");
    }
}
