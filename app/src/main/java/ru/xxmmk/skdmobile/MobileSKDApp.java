package ru.xxmmk.skdmobile;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MobileSKDApp extends Application {
    private static MobileSKDApp instance;
    public MobileSKDDB getmDbHelper() {
        Log.d(this.getLOG_TAG(), "MobileSKDApp.getmDbHelper");
        return mDbHelper;
    }

    private MobileSKDDB mDbHelper;
    private String mDataBasicURL = "https://navigator.mmk.ru/getdata.aspx";
    private String mLoginURL = "http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.XXHR_SKD_MOBILE.login";//"https://navigator.mmk.ru/login_kis.aspx";
    public String mDatURL = "http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.MOBILE_SKD_VIEW";//"https://navigator.mmk.ru/login_kis.aspx";
    public String mDatURLCargo = "http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.XXHR_SKD_MOBILE.is_cargo";//"https://navigator.mmk.ru/login_kis.aspx";
    public String mDatURLBlockPerson = "http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.XXHR_SKD_MOBILE.person_info_lock1";//"https://navigator.mmk.ru/login_kis.aspx";
    public String mDatURLPerson = "http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.XXHR_SKD_MOBILE.person_info_all1";//"https://navigator.mmk.ru/login_kis.aspx";

    public String mDatURLUpload = "http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.XXHR_SKD_MOBILE.upload_skd_history";//"https://navigator.mmk.ru/login_kis.aspx";

    public String mDatURL3 = "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxhr_skd_mobile.bar_code_inf";//"https://navigator.mmk.ru/login_kis.aspx";

    public String mDataSKDPeople = "http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.XXHR_SKD_MOBILE.get_skd_people_json";
    public String mDataSKDObj    = "http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.XXHR_SKD_MOBILE.get_skd_dev_json";

    public String ListKPP = "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxhr_skd_mobile.list_kpp";
    public String ListBreach = "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxhr_skd_mobile.breach_type";
    public String BlockCard = "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxhr_skd_mobile.card_block";

    public String SKDPath ="/sdcard/mobSKD/";

    public String SKDOperator="Кто ВЫ?";
    public String SKDKPP="Укажите КПП";
    public String SKDTKPP="Тип КПП не выбран";
    public String SKDRfId;
    public String SKDOperRfId;
    public String SKDRfIdCard;
    public String    SKDStep = "1";
    public String    SKDBarCode = "1";
    public String  SKDBlockBk ="0";
    public Boolean    NetErr = false;
    protected String mResult= "null";

    public Bitmap ShowPhoto (String CholderId)

    {
        File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
        String filename=CholderId+"-1.jpg";
        Bitmap vbitmap = BitmapFactory.decodeFile(SDCardRoot+"/MobileSKD/"+filename);
        if (vbitmap==null)
        {
            vbitmap=BitmapFactory.decodeResource(getResources(), R.drawable.u34);
        }
        return vbitmap;
    }

    public String LoadImage (String CholderId)
    {
        String filepath;
        try
        {
            URL url = new URL("http://neptun.eco.mmk.chel.su:7777/i/skd_photo_resize/"+CholderId+"-1.jpg");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
            String filename=CholderId+"-1.jpg";
            Log.i("Local filename:",""+filename);

            File file = new File(SDCardRoot+"/MobileSKD/",filename);
            if(file.createNewFile())
            {
                file.createNewFile();
            }
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ( (bufferLength = inputStream.read(buffer)) > 0 )
            {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
            //    Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
            }
            fileOutput.close();
            if(downloadedSize==totalSize) filepath=file.getPath();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            filepath=null;
            e.printStackTrace();
        }

        return  CholderId;
    }
    public String CheckImage (String CholderId)
    {
        String filename=CholderId+"-1.jpg";
        java.io.File file = new java.io.File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/MobileSKD/" , filename);
        if (file.exists()) {
           return "Y";
        }
        return "N";
    }
    public String HistoryUpload (String rId,String personId, String rfId , String operator , String kpp ,String dt ,String rest , String kppType)
    {
        try {
            StringBuilder builder = new StringBuilder();
            HttpClient client = getNewHttpClient(); //new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getUploadHistoryURL(personId, rfId , operator , kpp , dt , rest ,  kppType));
            //Log.d("!!!!","!!!!"+getUploadHistoryURL(personId, rfId , operator , kpp , dt , rest ,  kppType));
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
                            mResult = jsonObject.getString("RESULT");
                            if (mResult.equals("Y"))
                            {
                                //Log.d("!!!!Удаление; "+rId+";",mResult.toString());
                                return "Y";
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "N";
                    }
                } else {
                    return "N";
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return "N";
            } catch (IOException e) {
                e.printStackTrace();
                return "N";
            }
        }
        catch (Exception e )
        {
            e.printStackTrace();
            return "N";
        }


        return mResult;
    }

    public String getUploadHistoryURL (String personId, String rfId , String operator , String kpp ,String dt ,String rest , String kppType)
    {
        //(p_person_id varchar2, p_rf_id varchar2, p_operator varchar2, p_kpp varchar2, p_date_time varchar2, p_result varchar2, p_kpp_type
        return this.mDatURLUpload+"?p_person_id="+personId+"&p_rf_id="+rfId
                                 +"&p_operator="+operator+"&p_kpp="+kpp+"&p_date_time="+dt+"&p_result="+rest+"&p_kpp_type="+kppType;
    }

    public String getDataURL(String mCode) {
        return this.mDataBasicURL+"?s="+mCode+"&token="+this.getmHASH();
    }

    public void vibrate() {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }

    public String getToken() {
        return this.getmHASH();
    }

    public String putDataURL(String mObject, String mCode) {
        return "http://161.8.223.166:8020/get_to_oracle.aspx?s=6&P_GEN_OBJECT_ID="+mObject+"&P_NFC_CODE="+mCode+"&P_X_COORD=0&P_Y_COORD=2";
    }

    /*public String getObjectDataURL(String mCode,String mOrgId) {
        return this.mDataBasicURL+"?s="+mCode+"&token="+this.getmHASH()+"&org_id="+mOrgId;
    }
*/
    public String getSKDDataURL(String mCode) {
        Log.d(this.mDataBasicURL+"?s="+mCode+"&token="+this.getmHASH(),"URL");
        return this.mDataBasicURL+"?s="+mCode+"&token="+this.getmHASH();
    }
    public String getLoginDataURL(String rfId) {
        return this.mLoginURL+"?rfid="+rfId;
    }
    public String getisCargoDataURL(String rfId) {
        return this.mDatURLCargo+"?p_card_id="+rfId;
    }

    public String getmHASH() {
        if (mHASH == null || mHASH.isEmpty()) {
            mHASH = mDbHelper.getSettingValue("token");
        }
        return mHASH;
    }

    public void setmHASH(String mHASH) {
        mDbHelper.setSettingValue("token",mHASH);
        this.mHASH = mHASH;
    }

    public void saveUsername(String username) {
        mDbHelper.setSettingValue("username",username);
    }

    private String mHASH;

    public String getLOG_TAG() {
        return LOG_TAG;
    }

    final String LOG_TAG = "myLogs";

    @Override
    public void onCreate() {
        super.onCreate();
        mDbHelper = new MobileSKDDB(getApplicationContext());
        mDbHelper.getWritableDatabase();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mDbHelper.close();
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        mDbHelper.close();
    }

    public static MobileSKDApp getInstance() {
        return instance;
    }

    public MobileSKDApp() {
        instance = this;
    }


    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }



    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }
}