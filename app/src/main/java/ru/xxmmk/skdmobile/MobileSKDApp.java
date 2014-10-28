package ru.xxmmk.skdmobile;

        import android.app.Application;
        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.os.Vibrator;
        import android.util.Log;

        import org.apache.http.HttpVersion;
        import org.apache.http.client.HttpClient;
        import org.apache.http.conn.ClientConnectionManager;
        import org.apache.http.conn.scheme.PlainSocketFactory;
        import org.apache.http.conn.scheme.Scheme;
        import org.apache.http.conn.scheme.SchemeRegistry;
        import org.apache.http.impl.client.DefaultHttpClient;

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
    public String mDatURLPerson = "http://neptun.eco.mmk.chel.su:7777/pls/apex/XXOTA_APEX.XXHR_SKD_MOBILE.person_info_all";//"https://navigator.mmk.ru/login_kis.aspx";
    public String mDatURL3 = "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxhr_skd_mobile.bar_code_inf";//"https://navigator.mmk.ru/login_kis.aspx";
    public String ListKPP = "http://neptun.eco.mmk.chel.su:7777/pls/apex/xxota_apex.xxhr_skd_mobile.list_kpp";
    public String SKDOperator="Кто ВЫ?";
    public String SKDKPP="Укажите КПП";
    public String SKDTKPP="Тип КПП не выбран";
    public String SKDRfId;
    public String SKDOperRfId;
    public String SKDRfIdCard;
    public String    SKDStep = "1";
    public String    SKDBarCode = "1";
    public Boolean    NetErr = false;


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
