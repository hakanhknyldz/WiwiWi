package wiwiwi.io.wearwithweather.pojo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "HAKKE";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WiDatabase";

    private static final String TABLE_ALL_CLOTHES ="wiAllClothes";
    private static final String CREATE_TABLE_ALL_CLOTHES = "create table wiAllClothes (id INTEGER PRIMARY KEY,wiPath TEXT,wiUrl TEXT, catId INTEGER, genderId INTEGER";

    SQLiteDatabase db;
    Cursor cursor;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ALL_CLOTHES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALL_CLOTHES);
    }

    // DB METHODS

    public boolean insertDictionary(wiClothes wiClothes){
        Log.d(TAG ,"DatabaseHelper => insertDictionary");

        boolean result = false;

        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("wiPath" , wiClothes.getWiPath());
        values.put("wiUrl",wiClothes.getWiUrl());
        values.put("catId",wiClothes.getCatId());
        values.put("genderId",wiClothes.getGenderId());

        long id = db.insert(TABLE_ALL_CLOTHES,null,values);
        if(id > 0)
        {
            result = true;
        }

        db.close();
        return result;
    }

    public boolean updateDictionary(String wiPath, String wiUrl, int id)
    {
        boolean valid = false;
        db = this.getWritableDatabase();

        //updating datas
        ContentValues values = new ContentValues();
        values.put("wiPath", wiPath );
        values.put("wiUrl" , wiUrl);

        //db.update(table_name, ContentValues values , String whereClause , String[] whereArgs)
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};

        int result = db.update(TABLE_ALL_CLOTHES,values,whereClause,whereArgs);

        if(result > 0)
        {
            valid  = true;
        }

        return valid;
    }

    public boolean deleteDictionary(int id)
    {
        boolean valid =false;
        db = this.getWritableDatabase();

        String whereClause = "id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        //delete(String table, String whereClause, String[] whereArgs)
        int result = db.delete(TABLE_ALL_CLOTHES,whereClause,whereArgs);

        if(result > 0 )
        {
            valid = true;
        }

        return valid;
    }


    public ArrayList<wiClothes> getAllClothes(Context context)
    {
        ArrayList<wiClothes> arrayList = new ArrayList<>();


        Log.d(TAG ,"DatabaseHelper => getAllClothes");

        String[] columns = new String[]{"id",   "turkishWord", "englishWord"};

        String query = "SELECT * FROM " + TABLE_ALL_CLOTHES;
        db = this.getReadableDatabase();

        cursor = db.rawQuery(query,null);

        String arr[] = new String[cursor.getCount()];

        int counter=0;
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
            wiClothes wiClothes = new wiClothes();
            wiClothes.setWiPath(cursor.getString(cursor.getColumnIndex("wiPath")));
            wiClothes.setWiUrl(cursor.getString(cursor.getColumnIndex("wiUrl")));
            wiClothes.setGenderId(cursor.getString(cursor.getColumnIndex("genderId")));
            wiClothes.setCatId(cursor.getString(cursor.getColumnIndex("catId")));

            arrayList.add(wiClothes);

        }


        return arrayList;
    }





}
