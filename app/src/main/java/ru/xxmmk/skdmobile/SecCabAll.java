package ru.xxmmk.skdmobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class SecCabAll extends Activity {
    private MobileSKDApp mMobileSKDApp;
    WebView mWebView;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec_cab_all);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        myAB.setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mWebView = (WebView) findViewById(R.id.SecCabWV);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
/*
        pd = new ProgressDialog(SecCabAll.this);
        pd.setMessage("Дождитесь окончания загрузки...");*/
      //  pd.show();
        mWebView.setWebViewClient(new WebViewClient()); /*myWebViewClient*/
        mWebView.loadUrl(mMobileSKDApp.mDatURL + "?p_card_id=" + mMobileSKDApp.SKDOperRfId + "&p_kpp=" + mMobileSKDApp.SKDKPP + "&p_op=9");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.item1) {
            Log.d("actionBarIttem", "xxxxxxx");
            Intent intent = new Intent();
            intent.setClass(SecCabAll.this, MapSecCab.class);

            startActivity(intent);
            return true;
        }
        if (id != R.id.item1) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
           view.loadUrl(url);
            if (!pd.isShowing()) {
                pd.show();
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (pd.isShowing()) {
                pd.dismiss();
            }

        }
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            finish();
            Intent intent = new Intent();
            intent.setClass(SecCabAll.this, ErrorScan.class);
            startActivity(intent);
            //mWebView.loadUrl("file:///android_asset/loaderror.html");
        }
    }
}
