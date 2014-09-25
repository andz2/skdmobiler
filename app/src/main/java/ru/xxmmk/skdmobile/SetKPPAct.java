package ru.xxmmk.skdmobile;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


    public class SetKPPAct extends Activity {

        String[] data = {"Проходная 1", "Проходная 2", "Проходная 3", "Проходная 4", "Склад 50" , "ЭСПЦ Пост1"};
        String success;
        String kpp;
        private MobileSKDApp mMobileSKDApp;

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

            // адаптер
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            spinner.setAdapter(adapter);
            // заголовок
            spinner.setPrompt("Title");
            // выделяем элемент
            spinner.setSelection(2);
            kpp=spinner.getSelectedItem().toString();
           //Toast.makeText(getBaseContext(), "Position = " + spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            Button Okbutton=(Button)findViewById(R.id.setKPP);

            // Button loginButton = (Button) findViewById(R.id.Loginbutton);
            Okbutton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  mMobileSKDApp.SKDKPP=kpp;
                                                  mMobileSKDApp.SKDStep="3";
                                                  finish();
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
    }

