package com.portablecollections.portablecollections;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlanDetailsSQLiteOpenHelper helper = new PlanDetailsSQLiteOpenHelper(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        ArrayList<Collectable> collection = helper.retrieveData(database);
        database.close();
        for (Collectable collectable : collection) {
            System.out.println("name: " + collectable.getName());
            System.out.println("description: " + collectable.getDescription());
            System.out.println("country: " + collectable.getCountry());
            System.out.println("city: " + collectable.getCity());
        }

    }
}