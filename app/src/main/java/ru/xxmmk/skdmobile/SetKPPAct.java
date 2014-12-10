package ru.xxmmk.skdmobile;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.nfc.NfcAdapter;

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
import java.util.Arrays;
import java.util.List;


    public class SetKPPAct extends Activity {

//        String[] data = {"КПП не указана", "Проходная 1", "Проходная 2", "Проходная 3", "Проходная 4", "Склад 50" , "ЭСПЦ Пост1"};
        String[] data1 = {"КПП не указана", "Проходная 45", "Проходная 46", "Проходная 47"};
        String success;
        ArrayList<String> data = new ArrayList<String>();
        String kpp;
        private MobileSKDApp mMobileSKDApp;
        protected NfcAdapter nfcAdapter;
        protected PendingIntent nfcPendingIntent;

        /**
         * Called when the activity is first created.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mMobileSKDApp = ((MobileSKDApp) this.getApplication());

            ActionBar myAB = getActionBar();
            myAB.setTitle(mMobileSKDApp.SKDOperator);
            myAB.setSubtitle(mMobileSKDApp.SKDKPP);
            myAB.setDisplayShowHomeEnabled(false);

            setContentView(R.layout.activitykpp);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            //инициализируем массив
            GetKpp();
            // адаптер
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner spinner = (Spinner) findViewById(R.id.listkpp);
            spinner.setAdapter(adapter);

            // заголовок
            //spinner.setPrompt("Title");
            // выделяем элемент
            if (mMobileSKDApp.SKDKPP!="Укажите КПП")
            {
                  spinner.setSelection(Arrays.asList(data).indexOf(mMobileSKDApp.SKDKPP));
                //  kpp=spinner.getSelectedItem().toString();
            }
          //  spinner.setSelection(2);
          //  kpp=spinner.getSelectedItem().toString();
           //Toast.makeText(getBaseContext(), "Position = " + spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            Button Okbutton=(Button)findViewById(R.id.setKPP);

            // Button loginButton = (Button) findViewById(R.id.Loginbutton);
            Okbutton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  if (!"КПП не указана".equals(kpp)) {

                                                      mMobileSKDApp.SKDKPP = kpp;
                                                      mMobileSKDApp.SKDStep = "3";
                                                      finish();
                                                  }
                                                  else
                                                  {
                                                      mMobileSKDApp.SKDKPP = "Укажите КПП";
                                                      mMobileSKDApp.SKDStep = "2";
                                                      finish();
                                                  }
                                                  //mMobileSKDApp.SKDKPP=spinner.getSelectedItem().toString();
                                                 /* Intent intent = new Intent(view.getContext(),OperLogin.class);
                                                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                  view.getContext().startActivity(intent);*/
                                              }
                                          }
            );

            // устанавливаем обработчик нажатия
            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    // показываем позиция нажатого элемента
                    //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                    kpp=  data.get(position).toString();
                //    Toast.makeText(getBaseContext(), "Position = " + kpp, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
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
            nfcAdapter.disableForegroundDispatch(this);
        }

        public void GetKpp ()
        {
            Boolean vStatus = false;
            mMobileSKDApp.NetErr=false;

                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = mMobileSKDApp.getNewHttpClient(); //new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(mMobileSKDApp.ListKPP);
                    Log.d(mMobileSKDApp.getLOG_TAG(), mMobileSKDApp.ListKPP);
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
                                    data.add(jsonObject.getString("KPP_NAME"));
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
    }

