package ru.xxmmk.skdmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class ListKpp extends Activity {
    private MobileSKDApp mMobileSKDApp;
     WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        setContentView(R.layout.activity_list_kpp);

        ShowCardData(mMobileSKDApp.SKDRfIdCard);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_kpp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public void ShowCardData (String CardId)
    {
        mWebView = (WebView) findViewById(R.id.kpp);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new MyWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                finish();
                Intent intent = new Intent();
                intent.setClass(ListKpp.this, ErrorScan.class);
                startActivity(intent);
                //mWebView.loadUrl("file:///android_asset/loaderror.html");
            }
        });

        //showProgress(true);
        mWebView.loadUrl(mMobileSKDApp.mDatURL+"?p_card_id="+CardId+"&p_kpp="+mMobileSKDApp.SKDKPP+"&p_op=5");
   /*     mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                showProgress(false);
            }
        }
        );*/
    }
}
