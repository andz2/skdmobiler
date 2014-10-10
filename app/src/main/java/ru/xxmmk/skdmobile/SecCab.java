package ru.xxmmk.skdmobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TabHost;
import android.widget.TextView;
import android.app.TabActivity;

public class SecCab extends  ActivityGroup  {
    private MobileSKDApp mMobileSKDApp;
    WebView mWebView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sec_cab);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextSize(18);
                title.setTextColor(Color.BLACK);
            }
        }
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        myAB.setDisplayHomeAsUpEnabled(true);

        mWebView = (WebView) findViewById(R.id.secinf);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new MyWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);


        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mWebView.loadUrl("file:///android_asset/loaderror.html");

            }
        });

        // получаем TabHost
     //   TabHost tabHost = getTabHost();

        // инициализация была выполнена в getTabHost
        // метод setup вызывать не нужно
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        // инициализация
        tabHost.setup(this.getLocalActivityManager());
        //TabHost.TabSpec tabSpec;
        TabHost.TabSpec tabSpec;


        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("Последние КПП");
        tabSpec.setContent(new Intent(this, LastKpp.class));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Где ВЫ?");
        tabSpec.setContent(new Intent(this, MapSecCab.class));
        tabHost.addTab(tabSpec);



        // создаем вкладку и указываем тег
   //     tabSpec = tabHost.newTabSpec("tag1");
        // название вкладки
        //tabSpec.setIndicator("Последние КПП");
 /*       Intent reusableIntent = new Intent().setClass(this, SecCab.class);
        tabHost.addTab(tabHost.newTabSpec("Forum").setContent(reusableIntent).setIndicator("Последние КПП"));
        reusableIntent = new Intent().setClass(this, Map.class);
        tabHost.addTab(tabHost.newTabSpec("Forum").setContent(reusableIntent).setIndicator("Где ВЫ?"));
*/



        //showProgress(true);
        Log.d("1","**********************************");
        Log.d("2",mMobileSKDApp.mDatURL+"?p_card_id="+mMobileSKDApp.SKDOperRfId +"&p_kpp="+mMobileSKDApp.SKDKPP+"&p_op=4");
        mWebView.loadUrl(mMobileSKDApp.mDatURL + "?p_card_id=" + mMobileSKDApp.SKDOperRfId + "&p_kpp=" + mMobileSKDApp.SKDKPP + "&p_op=4");
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        finish();
        return true;
        /*int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);*/
    }
}
