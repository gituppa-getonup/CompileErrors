package com.portablecollections.portablecollections;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class PlanDetailsSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = PlanDetailsSQLiteOpenHelper.class.getName();

    private final Context context;
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "collectables.db";
    private final static String TABLE_NAME = "collectables";


    private boolean createDb = false, upgradeDb = false;

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.i(TAG, "onCreate database");
        createDb = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int DATABASE_VERSION) {
        Log.i(TAG, "onUpgrade database");
        if(oldVersion < DATABASE_VERSION) {
            copyDatabaseFromAssets(database);
        }
        upgradeDb = true;
    }

    @Override
    public void onOpen(SQLiteDatabase database) {
        Log.i(TAG, "onOpen database");
        if(createDb) {
            createDb = false;
        }

        if (upgradeDb) {
            upgradeDb = false;
        }

        copyDatabaseFromAssets(database);


    }

    public PlanDetailsSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.i(TAG, "constructor");
    }

    private void copyDatabaseFromAssets(SQLiteDatabase database) {
        Log.i(TAG, "copyDatabase");
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            myInput = context.getAssets().open(DATABASE_NAME);
            myOutput = new FileOutputStream(database.getPath());

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();

            SQLiteDatabase copiedDb = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
            copiedDb.execSQL("PRAGMA user_version = " + DATABASE_VERSION);
            copiedDb.close();

        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            try {
                if (myInput != null) {
                    myInput.close();
                }
                if (myOutput != null) {
                    myOutput.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }

    }

    public ArrayList<Collectable> retrieveData(SQLiteDatabase database) {
        Log.i(TAG, "retrieveData");
        String[] cols = { "name", "description", "country", "city" };
        Cursor cursor = database.query(TABLE_NAME, cols, null, null, null, null, null, null);
        int rows = cursor.getCount();
        Log.i(TAG, "number of rows in selection: " + rows);
        ArrayList<Collectable> collection = new ArrayList<>();

        while(cursor.moveToNext()) {
            Collectable col = new Collectable();

            col.setName(cursor.getString(cursor.getColumnIndex("name")));
            col.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            col.setCountry(cursor.getString(cursor.getColumnIndex("country")));
            col.setCity(cursor.getString(cursor.getColumnIndex("city")));

            collection.add(col);
        }
        database.close();
        return collection;
    }

}