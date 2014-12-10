package ru.xxmmk.skdmobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ScanActAll extends Activity {
    private MobileSKDApp mMobileSKDApp;
    WebView mWebView;
    ProgressDialog pd;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    Button button;
    public String mCargo;
    Boolean ScanF = false;
    Intent myIntent;
    private UserNfcTask NfcTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_act_all);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        ActionBar myAB = getActionBar();
        ScanF = false;
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        myAB.setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mWebView = (WebView) findViewById(R.id.scanWV);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        pd = new ProgressDialog(ScanActAll.this);
        pd.setMessage("Дождитесь окончания загрузки...");
        //    pd.show();
      /*  try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            Log.d("wait","10");
        }*/

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mMobileSKDApp.mDatURLPerson+"?p_card_id="+mMobileSKDApp.SKDRfIdCard+"&p_kpp="+mMobileSKDApp.SKDKPP+"&p_operator="
                +mMobileSKDApp.SKDOperator+"&p_kpp_type="+mMobileSKDApp.SKDTKPP);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        button=(Button)findViewById(R.id.bkscan);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.block))
                .withButtonColor(Color.RED)
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 10, 76)
                .create();
        fabButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ScanActAll.this, BlockCard.class);

                startActivity(intent);
            }
        });
    }
    @Override
    protected void  onResume() {
        super.onResume();
        // Log.d("Resume","Resume");
        ScanF = false;
        enableForegroundMode();
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ScanF = false;
        pd = new ProgressDialog(ScanActAll.this);
        pd.setMessage("Дождитесь окончания загрузки...");
        //pd.show();
      /*  try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            Log.d("wait","10");
        }*/
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mMobileSKDApp.mDatURLPerson+"?p_card_id="+mMobileSKDApp.SKDRfIdCard+"&p_kpp="+mMobileSKDApp.SKDKPP+"&p_operator="
                +mMobileSKDApp.SKDOperator+"&p_kpp_type="+mMobileSKDApp.SKDTKPP);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        myIntent=intent;
/*        if (ScanF == false) {
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                //mMobileSKDApp.SKDRfId=bytesToHex(myTag.getId());
                mMobileSKDApp.SKDRfIdCard = bytesToHex(myTag.getId());
                Log.d(mMobileSKDApp.SKDRfIdCard, "=mCode");
                //attemptLogin();
                Boolean vStatus = false;
                ScanF=true;
                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = mMobileSKDApp.getNewHttpClient(); //new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(mMobileSKDApp.getisCargoDataURL(mMobileSKDApp.SKDRfIdCard));
                   // Log.d(mMobileSKDApp.getLOG_TAG(), "OperLogin.UserLoginTask " + mMobileSKDApp.getLoginDataURL(mMobileSKDApp.SKDRfIdCard));
                    try {
                        HttpResponse response = client.execute(httpGet);
                        StatusLine statusLine = response.getStatusLine();
                        int statusCode = statusLine.getStatusCode();
                        if (statusCode == 200) {
                            HttpEntity entity = response.getEntity();
                            InputStream content = entity.getContent();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }
                            try {
                                //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                                JSONArray jsonArray = new JSONArray(builder.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    mCargo = jsonObject.getString("cargo");
                                    vStatus = true;
                                    // Log.d(mCargo,"*******************************************************************");
                                }
                                //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                ScanF = false;
                                e.printStackTrace();
                            }
                        } else {
                            ScanF = false;
                            Log.e("Scan fail", "Scan fail");
                        }
                    } catch (ClientProtocolException e) {
                        ScanF = false;
                        e.printStackTrace();
                    } catch (IOException e) {
                        ScanF = false;
                        e.printStackTrace();
                    }

                    // Log.d(mCargo,mCargo);
                    vStatus = vStatus && mCargo.equals("Y");
                    //Это материальный пропуск
                    if (vStatus) {
                        //    Log.d("Cargo way",mCargo);
                        ScanF = false;
                        intent.setClass(ScanActAll.this, CargoA.class);
                        startActivity(intent);

                    } else {
                        //всё ок, мы идём на перезагрузку вебвью в окне после успешного считывания
                        mWebView.setWebViewClient(new MyWebViewClient());
                        mWebView.loadUrl(mMobileSKDApp.mDatURLPerson + "?p_card_id=" + mMobileSKDApp.SKDRfIdCard + "&p_kpp=" + mMobileSKDApp.SKDKPP + "&p_operator="
                                + mMobileSKDApp.SKDOperator + "&p_kpp_type=" + mMobileSKDApp.SKDTKPP);

                    }
                    Thread.sleep(1000);
                    ScanF = false;

                } catch (InterruptedException e) {
                    ScanF = false;
                }
                vibrate();
                ScanF = false;
                //Log.d("Поехали","action !!!!!!");

//            intent.setClass(OperLogin.this, ScanActivity.class);
//            startActivity(intent);

            }
            ScanF = false;
        }
        else
        {
            try
            {
                Thread.sleep(1000);
                ScanF = false;
            }
            catch (InterruptedException e) {
                Log.d("wait","10");
            }
        }
*/
        if (NfcTask == null ||
                NfcTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
            NfcTask = new UserNfcTask();
            NfcTask.execute();
     //       progressBar.setProgress(0);
            pd = new ProgressDialog(ScanActAll.this);
            pd.setMessage("Дождитесь окончания загрузки...");
            pd.show();
        } else {
            Toast.makeText(ScanActAll.this, "Ждите завершения обработки", Toast.LENGTH_SHORT)
                    .show();
        }

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_act_all, menu);
        return true;
    }
    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            pd.show();
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
            intent.setClass(ScanActAll.this, NetError.class);
            startActivity(intent);
            //mWebView.loadUrl("file:///android_asset/loaderror.html");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String bytesToHex(byte[] bytes) {
        byte[] nb ={45,-93, 102, -3};
        char[] hexChars = new char[bytes.length * 2];
        int v;

        for ( int j = bytes.length-1; j >=0; j-- ) {
            v = bytes[j] & 0xFF;
            hexChars[(bytes.length-1-j) * 2] = hexArray[v >>> 4];
            hexChars[(bytes.length-1-j) * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    private void vibrate() {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }

    public class UserNfcTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean vStatus = false;
/*            myIntent.setClass(ScanActAll.this, CargoA.class);
            startActivity(myIntent);
*/
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(myIntent.getAction())) {
                Tag myTag = myIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                //mMobileSKDApp.SKDRfId=bytesToHex(myTag.getId());
                mMobileSKDApp.SKDRfIdCard = bytesToHex(myTag.getId());
                Log.d(mMobileSKDApp.SKDRfIdCard, "=mCode");
                ScanF=true;
                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = mMobileSKDApp.getNewHttpClient(); //new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(mMobileSKDApp.getisCargoDataURL(mMobileSKDApp.SKDRfIdCard));
                    // Log.d(mMobileSKDApp.getLOG_TAG(), "OperLogin.UserLoginTask " + mMobileSKDApp.getLoginDataURL(mMobileSKDApp.SKDRfIdCard));
                    try {
                        HttpResponse response = client.execute(httpGet);
                        StatusLine statusLine = response.getStatusLine();
                        int statusCode = statusLine.getStatusCode();
                        if (statusCode == 200) {
                            HttpEntity entity = response.getEntity();
                            InputStream content = entity.getContent();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }
                            try {
                                //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                                JSONArray jsonArray = new JSONArray(builder.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    mCargo = jsonObject.getString("cargo");
                                    vStatus = true;
                                    // Log.d(mCargo,"*******************************************************************");
                                }
                                //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                ScanF = false;
                                e.printStackTrace();
                            }
                        } else {
                            ScanF = false;
                            Log.e("Scan fail", "Scan fail");
                        }
                    } catch (ClientProtocolException e) {
                        ScanF = false;
                        e.printStackTrace();
                    } catch (IOException e) {
                        ScanF = false;
                        e.printStackTrace();
                    }

                    // Log.d(mCargo,mCargo);
                    vStatus = vStatus && mCargo.equals("Y");
                    //Это материальный пропуск
                    if (vStatus) {
                        //    Log.d("Cargo way",mCargo);
                        ScanF = false;
                        myIntent.setClass(ScanActAll.this, CargoA.class);
                        startActivity(myIntent);

                    } else {
                        //всё ок, мы идём на перезагрузку вебвью в окне после успешного считывания
                        mWebView.setWebViewClient(new MyWebViewClient());
                        mWebView.loadUrl(mMobileSKDApp.mDatURLPerson + "?p_card_id=" + mMobileSKDApp.SKDRfIdCard + "&p_kpp=" + mMobileSKDApp.SKDKPP + "&p_operator="
                                + mMobileSKDApp.SKDOperator + "&p_kpp_type=" + mMobileSKDApp.SKDTKPP);

                    }
                    Thread.sleep(1000);
                    ScanF = false;

                } catch (InterruptedException e) {
                    ScanF = false;
                }
                vibrate();
                ScanF = false;
                //Log.d("Поехали","action !!!!!!");
/*
            intent.setClass(OperLogin.this, ScanActivity.class);
            startActivity(intent);
*/
            }
            ScanF = false;
            return vStatus;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            /*if (success) {
                finish();
            } else {

            }*/
        }

        @Override
        protected void onCancelled() {
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }


    }
}