package ru.xxmmk.skdmobile;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


/**
 * A login screen that offers login via email/password.

 */
public class AccLogin extends Activity /*implements LoaderCallbacks<Cursor>*/{
    private MobileSKDApp mMobileSKDApp;


    @Override
    protected void onStart(){
        super.onStart();
        Log.d("here","us");
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


    }

}



