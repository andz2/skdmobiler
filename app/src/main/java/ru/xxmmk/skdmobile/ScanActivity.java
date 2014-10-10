package ru.xxmmk.skdmobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.nfc.NfcAdapter;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by User on 07.07.2014.
 */
public class ScanActivity extends ActivityGroup {
    private MobileSKDApp mMobileSKDApp;

    private String mDescription;
    private String mCode;
    private String mObjectId;

    private Button btnSave;
    private Button btnCancel;

    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;
    private TextView ScanText4;
    private TextView ScanText2;

    private static final String TAG = ScanActivity.class.getName();

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    private View mProgressView;
    private View mLoginFormView;
    private ProgressDialog progressDialog;
    ProgressDialog pd;


    WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        setContentView(R.layout.activity_scan);

        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        myAB.setDisplayHomeAsUpEnabled(true);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      //  bar.setHomeButtonEnabled(true);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        // получаем TabHost
        //   TabHost tabHost = getTabHost();

        // инициализация была выполнена в getTabHost
        // метод setup вызывать не нужно
        TabHost tabHost = (TabHost) findViewById(R.id.tabhostscan);
        // инициализация
        tabHost.setup(this.getLocalActivityManager());
        //TabHost.TabSpec tabSpec;
        TabHost.TabSpec tabSpec;


        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("Уровни доступа");
        tabSpec.setContent(new Intent(this, ListKpp.class));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Проносимые ценности");
        tabSpec.setContent(new Intent(this, MatIt.class));
        tabHost.addTab(tabSpec);
      /*  progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Тест");
        progressDialog.setCancelable(false);*/

        mWebView = (WebView) findViewById(R.id.webView1);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);

        pd = new ProgressDialog(ScanActivity.this);
        pd.setMessage("Дождитесь окончания загрузки...");
        pd.show();
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mMobileSKDApp.mDatURL+"?p_card_id="+mMobileSKDApp.SKDRfIdCard+"&p_kpp="+mMobileSKDApp.SKDKPP+"&p_op=1");
    }

    public void enableForegroundMode() {
        //Log.d(TAG, "enableForegroundMode");

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        //Log.d(TAG, "disableForegroundMode");

        nfcAdapter.disableForegroundDispatch(this);
    }



 /*   @Override
    protected void onResume() {
        //Log.d(TAG, "onResume");
        super.onResume();
        enableForegroundMode();
        //ShowCardData(mMobileSKDApp.SKDRfIdCard);
    }

    @Override
    protected void onPause() {
        //Log.d(TAG, "onPause");
        super.onPause();
       disableForegroundMode();
    }*/

    private void vibrate() {
        //Log.d(TAG, "vibrate");
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }


    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        /*
        Intent myIntent = new Intent(getApplicationContext(), MyActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivityForResult(myIntent, 0);*/
        return true;

    }


   /* public void ShowCardData (String CardId)
    {
        mWebView = (WebView) findViewById(R.id.webView1);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);

        pd = new ProgressDialog(ScanActivity.this);
        pd.setMessage("Дождитесь окончания загрузки...");
        pd.show();
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mMobileSKDApp.mDatURL+"?p_card_id="+CardId+"&p_kpp="+mMobileSKDApp.SKDKPP+"&p_op=1");
       }*/

   /* final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String bytesToHex(byte[] bytes) {
        byte[] nb ={45,-93, 102, -3};
        char[] hexChars = new char[bytes.length * 2];
       // char[] hexChars = new char[nb.length * 2];
        int v;
        for ( int j = bytes.length-1; j >=0; j-- ) {
            v = bytes[j] & 0xFF;
            hexChars[(bytes.length-1-j) * 2] = hexArray[v >>> 4];
            hexChars[(bytes.length-1-j) * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }*/
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

           // pd.dismiss();
            if (pd.isShowing()) {
                System.out.println("on finish!!!!!");
                pd.dismiss();
            }

        }
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            finish();
            Intent intent = new Intent();
            intent.setClass(ScanActivity.this, ErrorScan.class);
            startActivity(intent);
            //mWebView.loadUrl("file:///android_asset/loaderror.html");
        }
    }
}
