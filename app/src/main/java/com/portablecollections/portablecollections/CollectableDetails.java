package com.portablecollections.portablecollections;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CollectableDetails extends AppCompatActivity {

    private static final String TAG = CollectableDetails.class.getName();

    /*
    private ImageView detailsImage;
    private TextView textItemName;
    private TextView textCountry;
    private TextView textCity;
    private CheckBox checkWantIt;
    private CheckBox checkGotIt;
    private EditText textDescription;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        long identifier = extras.getLong("identifier", 0L);
        EditText textDescription = findViewById(R.id.textDescription);
        String test = Long.toString(identifier);
        textDescription.setText(test);

        //todo : add stuff to fill all the fields based on the retrieved identifier

    }
}