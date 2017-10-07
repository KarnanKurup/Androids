package com.madhouse.vpesa;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ClientActivity extends Activity {

    final String url="http://192.168.43.232:85/vpesa/home.html";
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        webView=(WebView)findViewById(R.id.activity_client);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewController());

    }
    public class WebViewController extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String request) {
            view.loadUrl(request);
            return true;
        }
    }
    private boolean exit=false;

    @Override
    public void onBackPressed() {
        if (exit){
            finish();
        }else{
            Toast.makeText(this, "Press Back again to Exit.",Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }
}