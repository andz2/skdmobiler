package ru.xxmmk.skdmobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.Button;


public class CargoA extends Activity {
    private MobileSKDApp mMobileSKDApp;
    WebView mWebView;
    private String LASTURL = "";
    ProgressDialog pd;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargo);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());

        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        myAB.setDisplayHomeAsUpEnabled(true);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mWebView = (WebView) findViewById(R.id.cargoweb);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(false);
     //   mWebView.setWebViewClient(new MyWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        pd = new ProgressDialog(CargoA.this);
        pd.setMessage("Дождитесь окончания загрузки...");
        pd.show();
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mMobileSKDApp.mDatURL+"?p_card_id="+mMobileSKDApp.SKDRfId+"&p_kpp="+mMobileSKDApp.SKDKPP+"&p_op=7");
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        /**/
        button=(Button)findViewById(R.id.bksam);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void  onResume() {
        super.onResume();
        // Log.d("Resume","Resume");
        enableForegroundMode();
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("Intent", "NFC!!!");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cargo, menu);
        return true;
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
            System.out.println("on finish");
            if (pd.isShowing()) {
                pd.dismiss();
            }

        }
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mWebView.loadUrl("file:///android_asset/loaderror.html");

        }
    }
}
