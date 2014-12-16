package ru.xxmmk.skdmobile;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Exit extends Activity {
    private MobileSKDApp mMobileSKDApp;
    private LoadTask mLoadT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());


        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button Ubutton=(Button)findViewById(R.id.expBtnF); //выгрузка событий
        Ubutton.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           Toast.makeText(Exit.this, "Дождитесь завершения выгрузки данных, выход произойдёт автоматически ", Toast.LENGTH_SHORT)
                                                   .show();
                                           mLoadT = new LoadTask();
                                           mLoadT.execute();
                                       }
                                   }
        );
        Button Exbutton=(Button)findViewById(R.id.exitBtnF); //выход, просто выход
        Exbutton.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           mMobileSKDApp.SKDStep="1";
                                           mMobileSKDApp.SKDKPP="Укажите КПП";
                                           mMobileSKDApp.SKDOperator="Кто ВЫ?";
                                           Intent intent = new Intent(Exit.this, Activity.class); intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                           ;
                                           finish();
                                       }
                                   }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exit, menu);
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

    class LoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mMobileSKDApp.getmDbHelper().UploadSKDaccHistory();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("2","End!!!!!!!!!");
            Toast.makeText(Exit.this, "Загрузка завершена", Toast.LENGTH_SHORT)
                    .show();
            finish();
        }
    }

}
