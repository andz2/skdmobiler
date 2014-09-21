package ru.xxmmk.skdmobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

import ru.xxmmk.skdmobile.R;

public class activity_scan_off extends Activity {
    private static final String TAG = ScanActivity.class.getName();

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    private String mCode;
    private TextView ScanText4;
    private MobileSKDApp mMobileSKDApp;
    private TextView FIO;
    private TextView Doljn;
    private TextView Orgj;
    private TextView Otdel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_scan_off);
        ActionBar bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //ScanText4 = (TextView) this.findViewById(R.id.ScanText4);
        FIO = (TextView) this.findViewById(R.id.TextFio);
        Doljn = (TextView) this.findViewById(R.id.TextDoljn);
        Orgj = (TextView) this.findViewById(R.id.TextOrg);
        Otdel = (TextView) this.findViewById(R.id.TextOtdel);
        Cursor c;
        mMobileSKDApp = (MobileSKDApp) this.getApplication();



        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "This device doesn't enabled NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // initialize NFC
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_scan_off, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        finish();
        return true;
        /*int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);*/
    }
    public void enableForegroundMode() {
        //Log.d(TAG, "enableForegroundMode");

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        //Log.d(TAG, "disableForegroundMode");

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
        //Log.d(TAG, "onPause");

        super.onPause();

        disableForegroundMode();
    }

    private void vibrate() {
        //Log.d(TAG, "vibrate");

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(500);
    }
    public void onNewIntent(Intent intent) {
        //Log.d(TAG, "onNewIntent");

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            ScanText4.setText(bytesToHex(myTag.getId()));

            mCode = bytesToHex(myTag.getId());
            Log.d((String) ScanText4.getText(), "Scantext4");
            Log.d(mCode, "=mCode");

//**********************

            HashMap h= mMobileSKDApp.getmDbHelper().getSKDpeople(mCode);
          //  HashMap h= mMobileSKDApp.getmDbHelper().getSKDpeople((String) toInt(myTag.getId(), 0));
            //Integer.toBinaryString()
            //Log.d("x"+(String) h.get("rf_id")+"x"+(String) h.get("full_name"),"rf_id");
//           int i= Integer.parseInt( mCode,16);
//           Log.d(String.valueOf(i),"dec code");

            if ((String) h.get("rf_id")!=null) {
                Log.d("x"+(String) h.get("rf_id")+"x","rf_id");
                FIO.setText((String) h.get("full_name"));
                Doljn.setText((String) h.get("spec"));
                Orgj.setText((String) h.get("org"));
                Otdel.setText((String) h.get("otdel"));
            }
            else {
                Log.d("y" + (String) h.get("rf_id") + "y", "rf_id");
                FIO.setText("Нет данных");
                Doljn.setText("Информация о данной карте отсутствует в локальной БД");
                Orgj.setText((String) h.get(" "));
                Otdel.setText((String) h.get(" "));
            }

            ScanText4.setBackgroundColor(0xfff00000);
//            btnSave.setEnabled(false);
            vibrate();
        }
    }
    final protected static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static int toInt(byte[] bytes, int offset) {
        int ret = 0;
        for (int i=0; i<4 && i+offset<bytes.length; i++) {
            ret <<= 8;
            ret |= (int)bytes[i] & 0xFF;
        }
        return ret;
    }
}
