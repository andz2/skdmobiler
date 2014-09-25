package ru.xxmmk.skdmobile;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class LastKpp extends Activity {
    private MobileSKDApp mMobileSKDApp;
    WebView mWebView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        setContentView(R.layout.last_kpp);

        mWebView = (WebView) findViewById(R.id.lastkpp);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new MyWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
              /*  finish();
                Intent intent = new Intent();
                intent.setClass(LastKpp.this, ErrorScan.class);
                startActivity(intent);*/
                mWebView.loadUrl("file:///android_asset/loaderror.html");

            }
        });

        //showProgress(true);
        mWebView.loadUrl(mMobileSKDApp.mDatURL+"?p_card_id=1&p_kpp="+mMobileSKDApp.SKDKPP+"&p_op=2");
   /*     mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                showProgress(false);
            }
        }
        );*/
    }
    private class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
