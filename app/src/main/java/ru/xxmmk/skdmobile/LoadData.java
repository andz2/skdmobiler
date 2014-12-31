package ru.xxmmk.skdmobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LoadData extends Activity {
    private MobileSKDApp mMobileSKDApp;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    private LoadCh mLoadT;
    private LoadPhoto mLoadPhoto;
    Context context;
    ProgressDialog pd;
    int lastId;
    private NotificationManager manager; // Системная утилита, упарляющая уведомлениями
    private static final int NOTIFY_ID = 1; // Уникальный индификатор вашего уведомления в пределах класса

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        int i=0;


        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        while (i<10) {
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "MobileSKD"+ File.separator +i); //попробуем создать каталог для фото
            directory.mkdirs();
            i++;
        }
        Button LPbutton=(Button)findViewById(R.id.loadPhoto); //загрузка фото
        LPbutton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (mLoadPhoto == null ||
                                                    mLoadPhoto.getStatus().equals(AsyncTask.Status.FINISHED)) {
                                                         mLoadPhoto = new LoadPhoto();
                                                         mLoadPhoto.execute();
                                                Toast.makeText(LoadData.this, "Загрузка фотографий начата", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                            else
                                            {
                                                Toast.makeText(LoadData.this, "Дождитесь завершения выгрузки фотографий", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        }
                                    }
        );


        Button Bkbutton=(Button)findViewById(R.id.LoadBk); //возврат
        Bkbutton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (mLoadT == null ||
                                                    mLoadT.getStatus().equals(AsyncTask.Status.FINISHED))
                                            {
                                                    finish();
                                            }
                                            else
                                            {
                                                Toast.makeText(LoadData.this, "Дождитесь завершения загрузки данных", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        }
                                    }
        );
        Button Lbutton=(Button)findViewById(R.id.expBtnE); //загрузка
        Lbutton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                          //  finish();
                                            if (mLoadT == null ||
                                                    mLoadT.getStatus().equals(AsyncTask.Status.FINISHED))
                                            { new AlertDialog.Builder(LoadData.this)
                                                        .setTitle("Запустить процесс загрузки?")
                                                        .setMessage("Процесс загрузки данных может занять продолжительное время (более 15 минут)")
                                                        .setNegativeButton(android.R.string.no, null)
                                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface arg0, int arg1) {
                                                                mLoadT = new LoadCh();
                                                                mLoadT.execute();
                                                                Toast.makeText(LoadData.this, "Загрузка начата", Toast.LENGTH_SHORT)
                                                                        .show();
                                                            }
                                                        }).create().show();
                                            }
                                            else {
                                                Toast.makeText(LoadData.this, "Дождитесь завершения загрузки данных", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        }
                                    }
        );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    class LoadCh extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                createInfoNotification("Идёт синхронизация данных ...","Начало в ","Start");
                Thread.sleep(10); //  TimeUnit.SECONDS.sleep(2);
                //       Log.d("1","!!!!!!!!!!!!!!1");
                //Загрузка данных

                Boolean vStatus = false;
                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = mMobileSKDApp.getNewHttpClient();// new DefaultHttpClient();
                    //MobileTOiRApp app = MobileTOiRApp.getInstance();
                    Log.d(mMobileSKDApp.getLOG_TAG(), "LoadPeople ");
                    HttpGet httpGet = new HttpGet(mMobileSKDApp.mDataSKDPeople);
                    //Log.d("1","http");
                    //Log.d(mMobileSKDApp.getLOG_TAG(), "LoadObjects " );

                    try {
                        HttpResponse response = client.execute(httpGet);
                        StatusLine statusLine = response.getStatusLine();
                        int statusCode = statusLine.getStatusCode();
                        Log.d("2","statusCode!!!!="+statusCode);
                        if (statusCode == 200 )
                        {
                            HttpEntity entity = response.getEntity();
                            InputStream content = entity.getContent();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                            String line;
                            while ((line = reader.readLine()) != null) {

                                builder.append(line);
                            }
                            // Log.d("4","dead?= "+line);
                            String cHolder;
                            mMobileSKDApp.getmDbHelper().loadSKDaccPeople(builder.toString());

                        }
                        else {
                            //Log.d(mMobileTOiRApp.getLOG_TAG(), "LoadObjects Error = " + statusCode);
                            //Toast.makeText(this, "Example action.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (ClientProtocolException e) {
                        e.printStackTrace();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //загрузка уровней доступа
            try {
                StringBuilder builder = new StringBuilder();
                HttpClient client = mMobileSKDApp.getNewHttpClient();// new DefaultHttpClient();
                Log.d(mMobileSKDApp.getLOG_TAG(), "LoadDevice ");
                HttpGet httpGet =  new HttpGet(mMobileSKDApp.mDataSKDObj);
                //Log.d("1","http");
                //Log.d(mMobileSKDApp.getLOG_TAG(), "LoadObjects " );

                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    Log.d("2","statusCode!!!!="+statusCode);
                    if (statusCode == 200 )
                    {
                        HttpEntity entity = response.getEntity();
                        InputStream content = entity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                        String line;
                        while ((line = reader.readLine()) != null) {

                            builder.append(line);
                        }
                        // Log.d("4","dead?= "+line);
                        mMobileSKDApp.getmDbHelper().loadSKDaccLev(builder.toString());
                    }
                    else {
                        //Log.d(mMobileTOiRApp.getLOG_TAG(), "LoadObjects Error = " + statusCode);
                        //Toast.makeText(this, "Example action.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (ClientProtocolException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.sleep(10);
            }

            catch (InterruptedException e) {
                e.printStackTrace();

            }
            //загрузка операторов
            try {
                StringBuilder builder = new StringBuilder();
                HttpClient client = mMobileSKDApp.getNewHttpClient();// new DefaultHttpClient();
                Log.d(mMobileSKDApp.getLOG_TAG(), "LoadDevice ");
                HttpGet httpGet =  new HttpGet(mMobileSKDApp.mDataSKDOper);
                //Log.d("1","http");
                //Log.d(mMobileSKDApp.getLOG_TAG(), "LoadObjects " );

                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    Log.d("2","statusCode!!!!="+statusCode);
                    if (statusCode == 200 )
                    {
                        HttpEntity entity = response.getEntity();
                        InputStream content = entity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                        String line;
                        while ((line = reader.readLine()) != null) {

                            builder.append(line);
                        }
                        // Log.d("4","dead?= "+line);
                        mMobileSKDApp.getmDbHelper().loadSKDOper(builder.toString());
                    }
                    else {
                        //Log.d(mMobileTOiRApp.getLOG_TAG(), "LoadObjects Error = " + statusCode);
                        //Toast.makeText(this, "Example action.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (ClientProtocolException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.sleep(10);
            }

            catch (InterruptedException e) {
                e.printStackTrace();

            }
            //загрузка проходных
            try {
                StringBuilder builder = new StringBuilder();
                HttpClient client = mMobileSKDApp.getNewHttpClient();// new DefaultHttpClient();
                Log.d(mMobileSKDApp.getLOG_TAG(), "LoadDevice ");
                HttpGet httpGet =  new HttpGet(mMobileSKDApp.mDataSKDMobObj);
                //Log.d("1","http");
                //Log.d(mMobileSKDApp.getLOG_TAG(), "LoadObjects " );

                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    Log.d("2","statusCode!!!!="+statusCode);
                    if (statusCode == 200 )
                    {
                        HttpEntity entity = response.getEntity();
                        InputStream content = entity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                        String line;
                        while ((line = reader.readLine()) != null) {

                            builder.append(line);
                        }
                        // Log.d("4","dead?= "+line);
                        mMobileSKDApp.getmDbHelper().loadSKDMobDev(builder.toString());
                    }
                    else {
                        //Log.d(mMobileTOiRApp.getLOG_TAG(), "LoadObjects Error = " + statusCode);
                        //Toast.makeText(this, "Example action.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (ClientProtocolException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.sleep(10);
            }

            catch (InterruptedException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("2","End!!!!!!!!!");
            Toast.makeText(LoadData.this, "Загрузка завершена", Toast.LENGTH_SHORT)
                    .show();
            createInfoNotification("Синхронизация данных завершена","Выполнено в ","Finish");
        }
    }
    class LoadPhoto extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            createInfoNotification("Идёт загрузка фото ...","Начало в ","Start");
            mMobileSKDApp.getmDbHelper().UploadSKDPhoto();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("2","End!!!!!!!!!");
            Toast.makeText(LoadData.this, "Выгрузка фото завершена", Toast.LENGTH_SHORT)
                    .show();
            createInfoNotification("Выгрузка фото завершена","Выполнено в ","Finish");
        }
    }
    public void createInfoNotification(String message, String message1, String Fl){
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String dateString = fmt.format(date);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // Создаем экземпляр менеджера уведомлений
        int icon;
        if (Fl.equals("Finish")) {
            icon  = android.R.drawable.stat_sys_download_done; // Иконка для уведомления, я решил воспользоваться стандартной иконкой для Email
        }
        else {
            icon = android.R.drawable.arrow_up_float; // Иконка для уведомления, я решил воспользоваться стандартной иконкой для Email
        }
        CharSequence tickerText = message; // Подробнее под кодом
        long when = System.currentTimeMillis(); // Выясним системное время
        Notification notification = new Notification(icon, tickerText, when); // Создаем экземпляр уведомления, и передаем ему наши параметры
        Context context = getApplicationContext();
        CharSequence contentTitle = message1+dateString; // Текст заголовка уведомления при развернутой строке статуса
        CharSequence contentText = message; //Текст под заголовком уведомления при развернутой строке статуса
        //Intent notificationIntent = new Intent(this, MyActivity.class); // Создаем экземпляр Intent
        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0); // Подробное описание в UPD к статье
        notification.setLatestEventInfo(context, contentTitle, contentText, null); // Передаем в наше уведомление параметры вида при развернутой строке состояния
        mNotificationManager.notify(NOTIFY_ID, notification); // И наконец показываем наше уведомление через менеджер передав его ID
    }
}
