package ru.xxmmk.skdmobile;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A login screen that offers login via email/password.

 */
public class AccLogin extends Activity /*implements LoaderCallbacks<Cursor>*/{
    private MobileSKDApp mMobileSKDApp;
    WebView mWebView;

    @Override
    protected void onStart(){
        super.onStart();
     //   Log.d("here","us");
        Button CnButton = (Button) findViewById(R.id.bk);
        mMobileSKDApp.SKDStep="2";
        CnButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acc_l);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());
        Log.d("here1","us1");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageView imgView = (ImageView) findViewById(R.id.photoim);
        String imageUrl ="http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxhr_skd_mobile.skd_display_image?rfid="+ mMobileSKDApp.SKDOperRfId +"&p_mode=Y";  //"http://i.imgur.com/CQzlM.jpg";// "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxhr_skd_mobile.skd_display_image?rfid=1&p_mode=Y";
        Log.d("Photo","http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxhr_skd_mobile.skd_display_image?rfid="+ mMobileSKDApp.SKDOperRfId +"&p_mode=Y");
        imgView.setImageBitmap(getBitmapFromURL(imageUrl));

    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    }



