package ru.xxmmk.skdmobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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
import java.util.ArrayList;


public class BlockCard extends Activity {
    private MobileSKDApp mMobileSKDApp;
    WebView mWebView;
    WebView mWebViewLock;
    ProgressDialog pd;
    Button button;
    Button buttonLock;
    String success;
    ArrayList<String> data = new ArrayList<String>();
    String kpp;
    String LockMsg;
    String Reason;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_card);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        mMobileSKDApp.SKDBlockBk="1";
        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);
        myAB.setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mWebView = (WebView) findViewById(R.id.scanWV);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);

        pd = new ProgressDialog(BlockCard.this);
        pd.setMessage("Дождитесь окончания загрузки...");
    //    pd.show();

       /* try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            Log.d("wait", "10");
        }*/

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mMobileSKDApp.mDatURLBlockPerson+"?p_card_id="+mMobileSKDApp.SKDRfIdCard+"&p_kpp="+mMobileSKDApp.SKDKPP+"&p_operator="
                +mMobileSKDApp.SKDOperator+"&p_kpp_type="+mMobileSKDApp.SKDTKPP);
        //инициализируем массив
        GetBreach();
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.listBreach);
        spinner.setAdapter(adapter);

        Reason=spinner.getSelectedItem().toString();

        button=(Button)findViewById(R.id.bkscanblock);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();

            }
        });
        buttonLock=(Button)findViewById(R.id.lockcard);
        buttonLock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LockCard ();

            }
        });
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                Reason=  data.get(position).toString();
                //    Toast.makeText(getBaseContext(), "Position = " + kpp, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_act_all, menu);
        return true;
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
    private class MyWebViewClient extends WebViewClient {
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
            intent.setClass(BlockCard.this, NetError.class);
            startActivity(intent);
            //mWebView.loadUrl("file:///android_asset/loaderror.html");
        }
    }
    public void GetBreach ()
    {
        Boolean vStatus = false;
        mMobileSKDApp.NetErr=false;

        try {
            StringBuilder builder = new StringBuilder();
            HttpClient client = mMobileSKDApp.getNewHttpClient(); //new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(mMobileSKDApp.ListBreach);
            Log.d(mMobileSKDApp.getLOG_TAG(), mMobileSKDApp.ListBreach);
            try {
                //       Log.d("kpp_name","1");
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
                            //                    Log.d("kpp_name","2");
//                                    data[i] = jsonObject.getString("KPP_NAME");
                            data.add(jsonObject.getString("MEANING"));
                            //                    Log.d("kpp_name",data[i]);
                            vStatus = true;
                            //   Log.d(jsonObject.getString("oper"),"Tst");

                        }
                        //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                } else {
                    Log.d("not ok","not ok");
                    //Log.e("Login fail", "Login fail");

                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();


            } catch (IOException e) {
                e.printStackTrace();
                mMobileSKDApp.NetErr=true;

            }

            Thread.sleep(10);
            if (vStatus) {
//                        return true;
                Log.d("ok","ok");
            } else {
                Log.d("not ok","not ok");
//                        return false;
            }

        } catch (InterruptedException e) {
            Log.d("not ok","not ok");
//                    return false;
        }

//            return false;
    }
    public void LockCard ()
    {
        Boolean vStatus = false;
        mMobileSKDApp.NetErr=false;

        try {
            StringBuilder builder = new StringBuilder();
            HttpClient client = mMobileSKDApp.getNewHttpClient(); //new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(mMobileSKDApp.BlockCard+"?p_card_id="+mMobileSKDApp.SKDRfIdCard+"&p_kpp="+mMobileSKDApp.SKDKPP+"&p_operator="
                    +mMobileSKDApp.SKDOperator+"&p_reason="+Reason);
            Log.d(mMobileSKDApp.getLOG_TAG(), mMobileSKDApp.BlockCard+"?p_card_id="+mMobileSKDApp.SKDRfIdCard+"&p_kpp="+mMobileSKDApp.SKDKPP+"&p_operator="
                    +mMobileSKDApp.SKDOperator+"&p_reason="+Reason);
            try {
                //       Log.d("kpp_name","1");
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
//                                    data[i] = jsonObject.getString("KPP_NAME");
                            LockMsg=jsonObject.getString("MSG");

                            //                    Log.d("kpp_name",data[i]);
                            vStatus = true;
                            //   Log.d(jsonObject.getString("oper"),"Tst");

                        }
                        //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                } else {
                    Log.d("not ok","not ok");

                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();


            } catch (IOException e) {
                e.printStackTrace();
                mMobileSKDApp.NetErr=true;

            }

            Thread.sleep(10);
            if (vStatus) {
                Log.d("ok","ok");
                finish();
            } else {
                Log.d("not ok","not ok");

            }

        } catch (InterruptedException e) {
            Log.d("not ok","not ok");
//                    return false;
        }

    }
}
