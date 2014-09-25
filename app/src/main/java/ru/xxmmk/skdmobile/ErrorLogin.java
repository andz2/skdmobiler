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
public class ErrorLogin extends Activity /*implements LoaderCallbacks<Cursor>*/{



    @Override
    protected void onStart(){
        super.onStart();
        Log.d("here","us");
        Button CnButton = (Button) findViewById(R.id.bk);
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
        setContentView(R.layout.error_l);
        Log.d("here1","us1");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }

}



