package ru.xxmmk.skdmobile;
        import android.animation.Animator;
        import android.animation.AnimatorListenerAdapter;
        import android.annotation.TargetApi;
        import android.app.ActionBar;
        import android.app.Activity;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.pm.ActivityInfo;
        import android.nfc.NfcAdapter;
        import android.os.AsyncTask;

        import android.os.Build;
        import android.os.Bundle;
        import android.os.Vibrator;
        import android.util.Log;
        import android.view.View;
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

/**
 * A login screen that offers login via email/password.

 */
public class OperLogin extends Activity /*implements LoaderCallbacks<Cursor>*/{



    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private MobileSKDApp mMobileSKDApp;
    private String mCode;
    private TextView ScanText4;

    private static final String TAG = OperLogin.class.getName();


    @Override
    protected void onStart(){
        super.onStart();
        Button CnButton = (Button) findViewById(R.id.bk);
        CnButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               finish();
                                           }
                                       }
        );
//        mEmailView.setText(mMobileSKDApp.getmDbHelper().getSettingValue("username"));
//        mPasswordView.setText(mMobileSKDApp.getmDbHelper().getSettingValue("password"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mMobileSKDApp = (MobileSKDApp) this.getApplication();
        setContentView(R.layout.operlogin);
        ActionBar myAB = getActionBar();
        //myAB.setTitle("Иванов И.И.");
        //myAB.setSubtitle("Проходная 7");
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            focusView.requestFocus();
        } else {
//            showProgress(true); *************************************************************************подменить на показ окна
            mAuthTask = new UserLoginTask(mMobileSKDApp.SKDRfId);
            mAuthTask.execute((Void) null);
        }
    }
    private boolean isEmailValid(String email) {
        return true; //email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {


        private String mToken = "null";

        UserLoginTask(String rfId) {
                    }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean vStatus = false;
            try {
                StringBuilder builder = new StringBuilder();
                HttpClient client =mMobileSKDApp.getNewHttpClient(); //new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(mMobileSKDApp.getLoginDataURL(mMobileSKDApp.SKDRfId));

                Log.d(mMobileSKDApp.getLOG_TAG(), "OperLogin.UserLoginTask " +mMobileSKDApp.getLoginDataURL(mMobileSKDApp.SKDRfId));

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

                                mToken = jsonObject.getString("token");
                                mMobileSKDApp.SKDOperator = jsonObject.getString("oper");
                                vStatus = true;
                             //   Log.d(jsonObject.getString("oper"),"Tst");

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
                vStatus = vStatus && !mToken.equals("null");
                if (vStatus) {

                       mMobileSKDApp.setmHASH(mToken);
                    //  mMobileSKDApp.getmDbHelper().refreshOrgs(builder.toString());
                    return true;
                } else {
                    return false;
                }

            } catch (InterruptedException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
           // showProgress(false); ******************************************************заменим потом

            if (success) {
                finish();
            } else {
             //   super.onCreate(savedInstanceState);
                Log.d("go error page","way");
                Intent intent = new Intent();
                intent.setClass(OperLogin.this, ErrorLogin.class);

                startActivity(intent);
                finish();
              //  setContentView(R.layout.error_l);

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
         //   showProgress(false);****************************************************тоже
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

        //    Log.d(mMobileSKDApp.SKDOperator,"operator !!!!!!");
            vibrate();
            Log.d("Поехали","action !!!!!!");
        //    Intent intentSt = new Intent(this, MyActivity.class);
        //    startActivityForResult(intentSt, 1);

            intent.setClass(OperLogin.this, ScanActivity.class);
//            intent.setClass(OperLogin.this, ErrorScan.class);
            startActivity(intent);

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

}



