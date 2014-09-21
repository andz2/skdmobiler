package ru.xxmmk.skdmobile;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AccLevel {
    private Context mContext;
    private MobileSKDApp mMobileSKDApp;
    public static List<HashMap<String,String>> AccList = new ArrayList<HashMap<String,String>>();

    public static AccLevel newInstance() {
        AccLevel mAccLevel = new AccLevel("");

        return mAccLevel;
    }

    public AccLevel(String rfID) {
        mMobileSKDApp = MobileSKDApp.getInstance();
        //Log.d(mMobileTOiRApp.getLOG_TAG(), "Orgs " + mMobileTOiRApp.getmHASH());
        AccList = mMobileSKDApp.getmDbHelper().getSKDObjects(rfID);
    }
}
