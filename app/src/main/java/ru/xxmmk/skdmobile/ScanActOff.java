package ru.xxmmk.skdmobile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


public class ScanActOff extends Activity {

    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    private String mCode;
    private TextView ScanText4;
    private MobileSKDApp mMobileSKDApp;
    private TextView FIO;
    private TextView Doljn;
    private TextView Orgj;
    private TextView Otdel;
    RelativeLayout mlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_act_off);
        mMobileSKDApp = ((MobileSKDApp) this.getApplication());

        ActionBar myAB = getActionBar();
        myAB.setTitle(mMobileSKDApp.SKDOperator);
        myAB.setSubtitle(mMobileSKDApp.SKDKPP);
        myAB.setDisplayShowHomeEnabled(false);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    /*    mlayout= (RelativeLayout)findViewById(R.id.OffScanLayout);
// set the color
        mlayout.setBackgroundColor(Color.rgb(155,255,147));
        mlayout.setBackgroundColor(Color.rgb(255,46,22));*/
        Button Bkbutton=(Button)findViewById(R.id.LoadBk); //возврат
        Bkbutton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    }
        );
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "Отсутсвует поддержка NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "Модуль NFC выключен", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // initialize NFC
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan_act_off, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
//            ScanText4.setText(bytesToHex(myTag.getId()));
            mCode = bytesToHex(myTag.getId());
            Long i= Long.parseLong(mCode, 16);

//            Log.d((String) ScanText4.getText(), "Scantext4");
            Log.d(mCode, "=mCode");
            Log.d(String.valueOf(i), "=mCode");


//**********************

            HashMap h= mMobileSKDApp.getmDbHelper().getSKDaccPeople(String.valueOf(i),"Проходная 1",mMobileSKDApp.SKDOperator, mMobileSKDApp.SKDKPP , mMobileSKDApp.SKDTKPP);

            String emp ="-1";
            emp=(String)h.get("employee_number");

            mlayout= (RelativeLayout)findViewById(R.id.OffScanLayout);

            TextView txtUp = (TextView) this.findViewById(R.id.txtaccup);
            TextView txtDown = (TextView) this.findViewById(R.id.txtaccdown);
            TextView txtEmp = (TextView) this.findViewById(R.id.empT);

            if (emp==null)
            {

                mlayout.setBackgroundColor(Color.rgb(255,46,22));
                txtUp.setText("Карта не найдена\n");
                txtEmp.setText(emp);
                txtDown.setText("Карта не найдена");
            }
            else
            {
                mlayout.setBackgroundColor(Color.rgb(155,255,147));
                txtUp.setText("Доступ разрешен\n");
                txtDown.setText("Доступ разрешен");
                txtEmp.setText("л.н. "+emp);
            }
            Log.d(emp,"emp");
            Log.d("x"+(String) h.get("employee_number")+"x","employee_number");
            Log.d("x"+(String) h.get("acc_level_id")+"x","acc_level_id");
            Log.d("x"+(String) h.get("zz")+"x","zz");

//           ScanText4.setBackgroundColor(0xfff00000);
//            btnSave.setEnabled(false);
            vibrate();
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
}
