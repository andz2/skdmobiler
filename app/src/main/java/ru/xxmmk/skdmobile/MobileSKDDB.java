package ru.xxmmk.skdmobile;


        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.List;


public class MobileSKDDB  extends SQLiteOpenHelper {
    Context mContext;
    private MobileSKDApp mMobileSKDApp;



    public MobileSKDDB(Context context) {
        // конструктор суперкласса
        super(context, "SKDDB", null, 1);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(((MobileSKDApp)mContext).getLOG_TAG(), "MobileSKDDB.onCreate");
        // создаем таблицу с полями
        db.execSQL("create table settings ("
                + "id integer primary key autoincrement,"
                + "key text,"
                + "value text" + ");");
        db.execSQL("insert into settings (key) values ('username');");
        db.execSQL("insert into settings (key) values ('password');");
        db.execSQL("insert into settings (key) values ('token');");

/*        db.execSQL("create table orgs ("
                + "id integer primary key autoincrement,"
                + "org_id text,"
                + "org_code text" + ");");
        db.execSQL("insert into settings (key) values ('orgs_date');");*/

        //люди и точечные доступы
        db.execSQL("create table skd_people_acc ("
                + "rfid integer ,"
                + "employee_number text,"
                + "acc_level_id integer,"
                + "acc_kpp text,"
                + "nm integer,"
                + "person_id integer,"
                + "cardholder_id integer"
                + ");");

        //кпп и уровни доступа
        db.execSQL("create table xxhr_skd_dev_acc ("
                + "acclev_id integer  primary key,"
                + "dev_str text,"
                + "acc_name text"
                + ");");

        //события
        db.execSQL("create table xxhr_skd_mobile_history ("
                + "person_id integer,"
                + "rf_id text,"
                + "operator text,"
                + "kpp text,"
                + "dt text,"
                + "rest text,"
                + "kpp_type text"
                + ");");

        //карты охранников
        db.execSQL("create table xxhr_skd_operator ("
                + "operator text,"
                + "rf_id text"
                + ");");

        //мобильные проходные
        db.execSQL("create table xxhr_skd_objects ("
                + "kpp_name text,"
                + "skd_kpp text,"
                + "sort_kpp integer"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
/*
        if (newVersion == 2) {
            Log.d(((MobileSKDApp)mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion="+newVersion);
            db.execSQL("create table new_code ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "code text" + ");");
        }

        if (newVersion == 3) {
            Log.d(((MobileSKDApp) mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("create table orgs ("
                    + "id integer primary key autoincrement,"
                    + "org_id text,"
                    + "org_code text" + ");");
            db.execSQL("insert into settings (key) values ('orgs_date');");
        }
        if (newVersion == 4) {
            Log.d(((MobileSKDApp) mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("create table hierarchy ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "sn text,"
                    + "description text,"
                    + "parent_object_id text,"
                    + "up_flag text" + ");");
        }
        if (newVersion == 5) {
            Log.d(((MobileSKDApp) mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("drop table hierarchy;");
            db.execSQL("create table hierarchy ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "sn text,"
                    + "description text,"
                    + "parent_object_id text,"
                    + "up_flag text,"
                    + "org_id text"+ ");");
        }
        if (newVersion == 6) {
            Log.d(((MobileSKDApp) mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("drop table hierarchy;");
            db.execSQL("create table hierarchy ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "sn text,"
                    + "description text,"
                    + "parent_object_id text,"
                    + "up_flag text,"
                    + "code text,"
                    + "child_cnt text,"
                    + "org_id text"+ ");");
        }
        if (newVersion == 7) {
            Log.d(((MobileSKDApp) mContext).getLOG_TAG(), "MobileSKDDB.onUpgrade newVersion=" + newVersion);
            db.execSQL("drop table hierarchy;");
            db.execSQL("create table hierarchy ("
                    + "id integer primary key autoincrement,"
                    + "object_id text,"
                    + "sn text,"
                    + "description text,"
                    + "parent_object_id text,"
                    + "up_flag text,"
                    + "org_id text,"
                    + "code text,"
                    + "child_cnt text);");
        }*/
    }

    public String getSettingValue (String key) {
        String value = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            Cursor c = null;
            c = db.rawQuery("select value from settings where key = ?", new String[] { key });
            c.moveToFirst();
            value = c.getString(c.getColumnIndex("value"));
            c.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return value;
    }

    public void setSettingValue (String key, String newValue) {

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.execSQL("update settings set value=? where key = ?", new String[]{newValue, key});
            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public HashMap<String,String> getSKDaccPeople(String rfID,String pKPP ,String operator, String kpp , String tkpp )
    {
        HashMap<String,String> returnList = new HashMap<String,String>();
        Cursor c=null;
        SQLiteDatabase db = this.getWritableDatabase();

        c = db.rawQuery("select distinct rfid ,employee_number, acc_level_id, acc_kpp, nm ,person_id,cardholder_id,substr(cardholder_id,-1,1) fl from skd_people_acc x , xxhr_skd_dev_acc a "+
                " where " +
                " a.acclev_id=x.acc_level_id " +
                " and x.acc_kpp||';'||a.dev_str like '%"+pKPP+"%'" +
                " and rfid=?", new String[] { rfID } );
        //Log.d("select full_name ,employee_number ,spec ,org ,otdel from skd_people where rf_id="+rfID,"   select ");
        if (c.moveToFirst()) {
            do {
                //c.moveToFirst();
                returnList.put("rfid", c.getString(0));
                returnList.put("employee_number", c.getString(1));
                returnList.put("acc_level_id", c.getString(2));
                returnList.put("acc_kpp", c.getString(3));
                returnList.put("nm", c.getString(4));
                returnList.put("person_id", c.getString(5));
                returnList.put("cardholder_id", c.getString(6));
                returnList.put("fl", c.getString(7)); //каталог


                SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                Date date = new Date();
                String dateString = fmt.format(date);

                db.execSQL("insert into xxhr_skd_mobile_history (person_id, rf_id , operator ,kpp ,dt ,rest ,kpp_type ) values (?,?,?,?,?,?,?);",
                        new String[] {
                                  c.getString(5)  //person_id
                                , c.getString(0)  //rfid
                                , operator        //охранник
                                , kpp             //кпп
                                , dateString      //время
                                , "разрешен"
                                , tkpp           //тип кпп

                                                       });


               /* returnList.put("employee_number", c.getString(1));
                returnList.put("spec", c.getString(2));
                returnList.put("org", c.getString(3));
                returnList.put("otdel", c.getString(4));
                returnList.put("rf_id", c.getString(5));*/
                //  returnList.add(temp);
                //Toast.makeText(this.getBaseContext(),clientID, Toast.LENGTH_LONG).show();
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        db.close();
        return returnList;
    }

    public ArrayList <HashMap<String,String>> getSKDMobDev()
    {
        ArrayList<HashMap<String,String>> returnList = new ArrayList<HashMap<String,String>>();
      //  HashMap<String, String> temp = new HashMap<String, String>();
        Cursor c=null;
        SQLiteDatabase db = this.getWritableDatabase();
        c = db.rawQuery("select kpp_name ,skd_kpp ,sort_kpp from xxhr_skd_objects order by sort_kpp",null);
        if (c.moveToFirst()) {
            do {
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("kpp_name", c.getString(0));
             /*   temp.put("skd_kpp", c.getString(1));
                temp.put("sort_kpp", c.getString(2));*/
                returnList.add(temp);
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        db.close();
        return returnList;
    }
    public HashMap<String,String> getSKDOperator(String rfID)
    {
        HashMap<String,String> returnList =  new HashMap<String,String>();
        Cursor c=null;
        SQLiteDatabase db = this.getWritableDatabase();
        c = db.rawQuery("select operator from xxhr_skd_operator where rf_id=?",new String[] {rfID } );
        if (c.moveToFirst()) {
            do {
                //c.moveToFirst();
                returnList.put("operator", c.getString(0));
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        db.close();
        return returnList;
    }
    public ArrayList<HashMap<String,String>> getListOrgs() {
        ArrayList<HashMap<String,String>> returnList = new ArrayList<HashMap<String,String>>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("orgs Order BY org_code",
                new String[] { "*" }, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Cursor c = null;
                c = db.rawQuery("select value from settings where key = ?", new String[] { "loadOrg"+cursor.getString(1) });
                c.moveToFirst();
                String value = "Нет данных";
                if (c.getCount()!=0) {
                    value = c.getString(c.getColumnIndex("value"));
                }
                c.close();
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("ORG_ID", cursor.getString(1));
                temp.put("ORG_CODE", cursor.getString(2));
                temp.put("ORG_LOAD_TIME", value);
                returnList.add(temp);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return returnList;
    }

    public ArrayList<HashMap<String,String>> getSKDObjects(String rfId) {
        ArrayList<HashMap<String,String>> returnList = new ArrayList<HashMap<String,String>>();
        String selection = null;
        String[] selectionArgs = null;

        SQLiteDatabase db = this.getWritableDatabase();

        selection = "rf_id = ?";
        selectionArgs = new String[] { rfId};

        Cursor cursor = db.query("skd_acc ",
                new String[] { "NAME" }, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("NAME", cursor.getString(0));
                returnList.add(temp);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return returnList;
    }

    public void replaceSettingValue (String key, String newValue) {

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.execSQL("delete from settings where key = ?", new String[]{key});
            db.execSQL("insert into settings (key,value) values(?,?)", new String[]{key,newValue});
            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public void loadSKDaccPeople (String jsonObjects) {

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
          /*  db.execSQL("delete from new_code where object_id in (select object_id from hierarchy where org_id=?)", new String[] {orgId});

            db.execSQL("delete from hierarchy where org_id=?", new String[] {orgId});
*/
            db.execSQL("delete from skd_people_acc");
            //   db.execSQL("delete from skd_acc");
            try {
                //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                JSONArray jsonArray = new JSONArray(jsonObjects);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    db.execSQL("insert into skd_people_acc (rfid , employee_number , acc_level_id , acc_kpp , nm , person_id , cardholder_id) values (?,?,?,?,?,?,?);",
                            new String[] {jsonObject.getString("RFID")
                                    ,jsonObject.getString("EMPLOYEE_NUMBER")
                                    ,jsonObject.getString("ACC_LEVEL_ID")
                                    ,jsonObject.getString("ACC_KPP")
                                    ,jsonObject.getString("NM")
                                    ,jsonObject.getString("PERSON_ID")
                                    ,jsonObject.getString("CARDHOLDER_ID")
                                    //       ,jsonObject.getString("CHILD_CNT")
                            });
                }
            }
            catch (JSONException e) {
                e.printStackTrace();

            }
            replaceSettingValue("loadPeople" , new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
            //db.execSQL("update settings set value=? where key = ?", new String[]{new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()), "orgs_date"});
            db.close();


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void UploadSKDaccHistory () {

        SQLiteDatabase db = this.getWritableDatabase();
        String InsRes;
        String personId;
        String rfId;
        String operator;
        String kpp;
        String dt;
        String rest;
        String kppType;
        String rowId;
        Log.d("","!!!!Начинаем выгрузку");
        try {
            Cursor c = null;
            c = db.rawQuery("select ROWID  ri, person_id, rf_id , operator ,kpp ,dt ,rest ,kpp_type from xxhr_skd_mobile_history", null);
            if (c.moveToFirst()) {
                do {
                    Log.d("","!!!!Запись");
                    rowId=c.getString(c.getColumnIndex("ri"));
                    personId=c.getString(c.getColumnIndex("person_id"));
                    rfId=c.getString(c.getColumnIndex("rf_id"));
                    operator=c.getString(c.getColumnIndex("operator"));
                    kpp=c.getString(c.getColumnIndex("kpp"));
                    dt=c.getString(c.getColumnIndex("dt"));
                    rest=c.getString(c.getColumnIndex("rest"));
                    kppType=c.getString(c.getColumnIndex("kpp_type"));

                    mMobileSKDApp = new MobileSKDApp();

                    InsRes=mMobileSKDApp.HistoryUpload (rowId , personId,rfId  , operator , kpp ,dt ,rest ,kppType );
                    if (InsRes.equals("Y")) {
                        DeleteHistory(rowId); //удаляем запись по rowid
                    }
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void UploadSKDPhoto () { //выгрузка фотографий

        SQLiteDatabase db = this.getWritableDatabase();
        String ChId;
        String fl;

        Log.d("","!!!!Начинаем выгрузку фотографий");
        try {
            Cursor c = null;
            c = db.rawQuery("select cardholder_id,substr(cardholder_id,-1,1) fl from skd_people_acc", null);
            if (c.moveToFirst()) {
                do {
                    ChId=c.getString(c.getColumnIndex("cardholder_id"));
                    fl=c.getString(c.getColumnIndex("fl")); //каталог
                    mMobileSKDApp = new MobileSKDApp();
                    if (mMobileSKDApp.CheckImage(ChId,fl)=="N") {
                        mMobileSKDApp.LoadImage(ChId,fl);
                    }
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void DeleteHistory (String rId) //очистка истории по rowid
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(rId,"!!!!Удаляем: "+rId+";");
        db.execSQL("delete from xxhr_skd_mobile_history where ROWID=" + rId);
        db.close();
    }

    public void loadSKDMobDev (String jsonObjects) {

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.execSQL("delete from xxhr_skd_objects");
            try {
                //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                JSONArray jsonArray = new JSONArray(jsonObjects);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    db.execSQL("insert into xxhr_skd_objects (kpp_name,skd_kpp ,sort_kpp ) values (?,?,?);",
                            new String[] {jsonObject.getString("KPP_NAME")
                                    ,jsonObject.getString("SKD_KPP")
                                    ,jsonObject.getString("SORT_KPP")
                            });
                }
            }
            catch (JSONException e) {
                e.printStackTrace();

            }
//            replaceSettingValue("loadPeople" , new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
            //db.execSQL("update settings set value=? where key = ?", new String[]{new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()), "orgs_date"});
            db.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void loadSKDOper (String jsonObjects) {

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.execSQL("delete from xxhr_skd_operator");
            try {
                //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                JSONArray jsonArray = new JSONArray(jsonObjects);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    db.execSQL("insert into xxhr_skd_operator (operator,rf_id) values (?,?);",
                            new String[] {jsonObject.getString("FIO")
                                    ,jsonObject.getString("CARD")
                            });
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            //replaceSettingValue("loadPeople" , new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
            //db.execSQL("update settings set value=? where key = ?", new String[]{new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()), "orgs_date"});
            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void loadSKDaccLev (String jsonObjects) {

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.execSQL("delete from xxhr_skd_dev_acc");
            try {
                //Toast.makeText(this.getBaseContext(), builder.toString(), Toast.LENGTH_LONG).show();
                JSONArray jsonArray = new JSONArray(jsonObjects);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    db.execSQL("insert into xxhr_skd_dev_acc (acclev_id,dev_str ,acc_name ) values (?,?,?);",
                            new String[] {jsonObject.getString("ACCLEV_ID")
                                    ,jsonObject.getString("DEV_STR")
                                    ,jsonObject.getString("ACC_NAME")
                                    //       ,jsonObject.getString("CHILD_CNT")
                            });
                }
            }
            catch (JSONException e) {
                e.printStackTrace();

            }
            replaceSettingValue("loadPeople" , new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
            //db.execSQL("update settings set value=? where key = ?", new String[]{new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime()), "orgs_date"});
            db.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }



}
