package ru.xxmmk.skdmobile;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;

public class Vibra extends OperLogin.ScanFragment {

    public static void vibrate(OperLogin.ScanFragment fragment) {
        Vibrator vibrator = (Vibrator) fragment.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }

}