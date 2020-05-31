package com.example.ma02_20160947.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.ma02_20160947.R;

public class AlcoholDBHelper extends SQLiteOpenHelper {

    private  final static String DB_NAME = "alcohol_db";
    public final static String TABLE_NAME = "alcohol_table";
    public final static String COL_ID = "_id";
    public final static String COL_CATEGORY = "category";
    public final static String COL_DATE = "date";
    public final static String COL_NAME = "name";
    public final static String COL_PATH = "path";
    public final static String COL_CONTENT = "content";
    public final static String COL_ADDRESS = "address";
    public final static String PW_TABLE_NAME = "pw_table";
    public final static String COL_PW_ID = "_pid";
    public final static String COL_PW = "pw";

    public AlcoholDBHelper(Context context) {
        super (context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql = "create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement," + COL_CATEGORY + " TEXT, "
                + COL_DATE +" TEXT, " + COL_NAME + " TEXT, " + COL_PATH + " TEXT, " + COL_CONTENT + " TEXT, " + COL_ADDRESS + " TEXT);";
        db.execSQL(createSql);
        db.execSQL("create table " + PW_TABLE_NAME + " ( " + COL_PW_ID + " integer primary key autoincrement," + COL_PW + " TEXT);");

        //샘플 데이터
        //db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '안주', '2018.09.12', '코다차야', '" + R.mipmap.al01 + "', '김지용 생일', '서울 마포구 와우산로 48');");
        //db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '일기', '2018.07.02', '가족 여행', '" + R.mipmap.al02 + "', '처음 보는 술', '주소');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PW_TABLE_NAME);
        onCreate(db);
    }
}
