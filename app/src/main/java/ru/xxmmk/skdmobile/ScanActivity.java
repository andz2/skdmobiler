package ru.xxmmk.skdmobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.nfc.NfcAdapter;
import android.widget.Toast;
import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.content.Intent;
import android.net.Uri;
import android.webkit.DownloadListener;



/**
 * Created by User on 07.07.2014.
 */
public class ScanActivity extends Activity {
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

    WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        setContentView(R.layout.activity_scan);

        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        myAB.setDisplayHomeAsUpEnabled(true);

      //  bar.setHomeButtonEnabled(true);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        ShowCardData(mMobileSKDApp.SKDRfIdCard);

/*

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "This device doesn't enabled NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // initialize NFC

        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);*/

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



    @Override
    protected void onResume() {
        //Log.d(TAG, "onResume");

        super.onResume();
        ShowCardData(mMobileSKDApp.SKDRfIdCard);
        //
        // enableForegroundMode();
    }

    @Override
    protected void onPause() {
        //Log.d(TAG, "onPause");

        super.onPause();
        ShowCardData(mMobileSKDApp.SKDRfIdCard);
        //disableForegroundMode();
    }

    private void vibrate() {
        //Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }
    private class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        /*
        Intent myIntent = new Intent(getApplicationContext(), MyActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivityForResult(myIntent, 0);*/
        return true;

    }
    @Override
    protected void onNewIntent(Intent intent) {
        //Log.d(TAG, "onNewIntent");

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            ScanText4.setText(bytesToHex(myTag.getId()));

            mCode = bytesToHex(myTag.getId());
            Log.d((String) ScanText4.getText(),"Scantext4");
            Log.d(mCode, "=mCode");

            //рефреш веб вью mcode

            mWebView = (WebView) findViewById(R.id.webView1);
            mWebView.setVerticalScrollBarEnabled(false);
            mWebView.setHorizontalScrollBarEnabled(false);
            mWebView.setWebViewClient(new MyWebClient());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    finish();
                    Intent intent = new Intent();
                    intent.setClass(ScanActivity.this, ErrorScan.class);
                    startActivity(intent);
                  //  mWebView.loadUrl("file:///android_asset/loaderror.html");

                }
            });

         //   showProgress(true);
            mWebView.loadUrl("http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.MOBILE_SKD_VIEW?p_card_id="+mCode);
            mWebView.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {
         //           showProgress(false);
                }
            });

         //   showProgress(false);
//**********************
            ScanText4.setBackgroundColor(0xfff00000);
//            btnSave.setEnabled(false);
            vibrate();
        }
    }



    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Log.d("1","Ok");
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void ShowCardData (String CardId)
    {
        mWebView = (WebView) findViewById(R.id.webView1);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new MyWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                finish();
                Intent intent = new Intent();
                intent.setClass(ScanActivity.this, ErrorScan.class);
                startActivity(intent);

                //mWebView.loadUrl("file:///android_asset/loaderror.html");

            }
        });

        //showProgress(true);
        mWebView.loadUrl("http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.MOBILE_SKD_VIEW?p_card_id="+CardId);
   /*     mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                showProgress(false);
            }
        }
        );*/
    }

    final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String bytesToHex(byte[] bytes) {
        byte[] nb ={45,-93, 102, -3};
        char[] hexChars = new char[bytes.length * 2];
       // char[] hexChars = new char[nb.length * 2];
        int v;
       /* for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }*/
      /*  for ( int j = nb.length-1; j >=0; j-- ) {
            v = nb[j] & 0xFF;
            hexChars[(nb.length-1-j) * 2] = hexArray[v >>> 4];
            hexChars[(nb.length-1-j) * 2 + 1] = hexArray[v & 0x0F];
        }*/
        for ( int j = bytes.length-1; j >=0; j-- ) {
            v = bytes[j] & 0xFF;
            hexChars[(bytes.length-1-j) * 2] = hexArray[v >>> 4];
            hexChars[(bytes.length-1-j) * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


}
