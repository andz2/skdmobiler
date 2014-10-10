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

import java.util.Arrays;
import java.util.List;


    public class SetKPPAct extends Activity {

        String[] data = {"КПП не указана", "Проходная 1", "Проходная 2", "Проходная 3", "Проходная 4", "Склад 50" , "ЭСПЦ Пост1"};
        String success;
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

            // адаптер
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            spinner.setAdapter(adapter);

            // заголовок
            spinner.setPrompt("Title");
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
                    kpp=  data[position];
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

    }

