package ru.xxmmk.skdmobile;
        import android.app.ActionBar;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Fragment;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.pm.ActivityInfo;
        import android.nfc.NfcAdapter;

        import android.os.Bundle;
        import android.os.Vibrator;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.AutoCompleteTextView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

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

        import android.nfc.Tag;
        import android.widget.Toast;

        import com.google.zxing.integration.android.IntentIntegrator;
        import com.google.zxing.integration.android.IntentResult;

/**
 * A login screen that offers login via email/password.

 */
public class OperLogin extends Activity /*implements LoaderCallbacks<Cursor>*/{



    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private MobileSKDApp mMobileSKDApp;
    private String mCode;
    private TextView RfIdE;
    public String mCargo;
    public Boolean kShow;


    private static final String TAG = OperLogin.class.getName();




    @Override
    protected void onStart(){
        super.onStart();
        kShow=false;
        Button CnButton = (Button) findViewById(R.id.bkerr);
      /*  showKey();*/
        CnButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               finish();
                                           }
                                       }
        );
        if (mMobileSKDApp.SKDStep=="1")
        {
            finish();
        }
//        mEmailView.setText(mMobileSKDApp.getmDbHelper().getSettingValue("username"));
//        mPasswordView.setText(mMobileSKDApp.getmDbHelper().getSettingValue("password"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mMobileSKDApp = (MobileSKDApp) this.getApplication();
        setContentView(R.layout.operlogin);
        kShow=false;
        ActionBar myAB = getActionBar();
        //myAB.setTitle("Иванов И.И.");
        //myAB.setSubtitle("Проходная 7");
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        /*Button rf = (Button) findViewById(R.id.buttonrf);
        //кнопка для рфидов
        rf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // vibrate();
                showKey();
            }
        });*/
       /* showKey();*/
        if (mMobileSKDApp.SKDStep=="1")
        {
            finish();
        }

        InOutColor ();
    }

    public void scanBarcode(View view) {
        new IntentIntegrator((Activity)this).initiateScan();
    }

    public void scanBarcodeCustomOptions(View view) {
        Toast.makeText(this, "Сканирование штрих кодов запрещено", Toast.LENGTH_LONG).show();
        if (1==2) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.autoWide();
            integrator.initiateScan();
        }
    }

    public void encodeBarcode(View view) {
        new IntentIntegrator(this).shareText("Test Barcode");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        mMobileSKDApp.SKDBarCode=result.getContents();
        /*временно присваиваем штриход в самовывоз*/
        mMobileSKDApp.SKDRfId=result.getContents();
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Сканирование отменено", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent();

                /*Уходим на другой экран*/

             //   intent.setClass(OperLogin.this, CargoA.class);
                intent.setClass(OperLogin.this, BarCodeRes.class);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void enableForegroundMode() {
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        //Log.d(TAG, "onResume");

        super.onResume();
        if (mMobileSKDApp.SKDStep=="1")
        {
            finish();
        }
        enableForegroundMode();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundMode();
    }

    private void vibrate() {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }


    @Override
    protected void onNewIntent(Intent intent) {

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            //mMobileSKDApp.SKDRfId=bytesToHex(myTag.getId());
            mMobileSKDApp.SKDRfIdCard=bytesToHex(myTag.getId());
            Log.d( mMobileSKDApp.SKDRfIdCard, "=mCode");
            //attemptLogin();
            Boolean vStatus = false;
            try {
                StringBuilder builder = new StringBuilder();
                HttpClient client =mMobileSKDApp.getNewHttpClient(); //new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(mMobileSKDApp.getisCargoDataURL(mMobileSKDApp.SKDRfIdCard));
                     Log.d(mMobileSKDApp.getLOG_TAG(), "OperLogin.UserLoginTask " +mMobileSKDApp.getLoginDataURL(mMobileSKDApp.SKDRfIdCard));
                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    if (statusCode == 200 )
                    {
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
                            for (int i=0;i<jsonArray.length();i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                mCargo = jsonObject.getString("cargo");
                                vStatus = true;
                                  // Log.d(mCargo,"*******************************************************************");
                            }
                            //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        //Log.e("Login fail", "Login fail");
                    }
                }
                catch (ClientProtocolException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.sleep(10);
               // Log.d(mCargo,mCargo);
                vStatus = vStatus && mCargo.equals("Y");
                if (vStatus) {
                    Log.d("Cargo way",mCargo);
                    intent.setClass(OperLogin.this, CargoA.class);
                    startActivity(intent);

                } else {
                    if (mMobileSKDApp.SKDTKPP=="Тип КПП не выбран")
                    {
                        new AlertDialog.Builder(this)
                                .setTitle("Не указан тип события")
                                .setMessage("Выберите 'Вход/Выход' и заново сканируйте карту")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        //здесь можно что то сделать
                                    }
                                }).create().show();
                    }
                    else {
                        //intent.setClass(OperLogin.this, ScanActivity.class);
                        intent.setClass(OperLogin.this, ScanActAll.class);
                        startActivity(intent);
                    }
                }

            } catch (InterruptedException e) {

            }
            //vibrate();
            //Log.d("Поехали","action !!!!!!");
/*
            intent.setClass(OperLogin.this, ScanActivity.class);
            startActivity(intent);
*/
        }
    }

    final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String bytesToHex(byte[] bytes) {
        byte[] nb ={45,-93, 102, -3};
        char[] hexChars = new char[bytes.length * 2];
        // char[] hexChars = new char[nb.length * 2];
        int v;
       /* for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }*/
      /*  for ( int j = nb.length-1; j >=0; j-- ) {
            v = nb[j] & 0xFF;
            hexChars[(nb.length-1-j) * 2] = hexArray[v >>> 4];
            hexChars[(nb.length-1-j) * 2 + 1] = hexArray[v & 0x0F];
        }*/
        for ( int j = bytes.length-1; j >=0; j-- ) {
            v = bytes[j] & 0xFF;
            hexChars[(bytes.length-1-j) * 2] = hexArray[v >>> 4];
            hexChars[(bytes.length-1-j) * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    public  static class ScanFragment extends Fragment {
        private String toast;

        public ScanFragment() {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            displayToast();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_scan, container, false);
            Button scan = (Button) view.findViewById(R.id.scan_from_fragment);
            scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanFromFragment();
                }
            });
            return view;
        }

        public void scanFromFragment() {
            IntentIntegrator.forFragment(this).initiateScan();
        }

        private void displayToast() {
            if(getActivity() != null && toast != null) {
                Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
                toast = null;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null) {
                if(result.getContents() == null) {
                    toast = "Cancelled from fragment";
                } else {
                    toast = "Scanned from fragment: " + result.getContents();
                }
                // At this point we may or may not have a reference to the activity
                displayToast();
            }
        }
    }
    public void showKey ()
    {
        RfIdE  =    (EditText) findViewById(R.id.rf);
        RfIdE.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
              //  if (!kShow) {
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(RfIdE, 0);

            }
        },50);
        //kShow=true;
    }
    public void InOutColor ()
    {
        final Button inBtn = (Button) findViewById(R.id.in);
        final Button outBtn = (Button) findViewById(R.id.out);
        if (mMobileSKDApp.SKDTKPP=="Вход")
        {
            inBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_act1)); //setBackgroundResource
            outBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_inact1)); //setBackgroundResource
        }
        if (mMobileSKDApp.SKDTKPP=="Выход")
        {
            outBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_act1)); //setBackgroundResource
            inBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_inact1)); //setBackgroundResource
        }

        inBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_act1)); //setBackgroundResource
                outBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_inact1)); //setBackgroundResource
                mMobileSKDApp.SKDTKPP="Вход";
            }
        });
        outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_act1)); //setBackgroundResource
                inBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_inact1)); //setBackgroundResource
                mMobileSKDApp.SKDTKPP="Выход";
            }
        });
    }
}



