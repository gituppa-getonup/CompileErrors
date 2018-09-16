package com.portablecollections.portablecollections;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
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

    private long identifier = 0L;
    Bitmap detailsBitmap;
    private Collectable collectable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        identifier = extras.getLong("identifier", 0L);

        final ContentValues contentValues = new ContentValues();
        contentValues.put("id", identifier);
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future future = es.submit(new QueryData());

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "something went wrong creating the Room object in a separate thread");
        }

        ImageView detailsImage = findViewById(R.id.detailsImage);
        TextView textItemName = findViewById(R.id.textItemName);
        TextView textCountry = findViewById(R.id.textCountry);
        TextView textCity = findViewById(R.id.textCity);
        CheckBox checkWantIt = findViewById(R.id.checkWantIt);
        CheckBox checkGotIt = findViewById(R.id.checkGotIt);
        EditText textDescription = findViewById(R.id.textDescription);


        if(collectable != null) {
            CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper();
            detailsBitmap = pictureHelper.getBitmapFromString(collectable.getImageUri(), this);
            detailsImage.setImageBitmap(detailsBitmap);

            textItemName.setText(collectable.getName());
            textCountry.setText(collectable.getCountry());
            textCity.setText(collectable.getCity());
            checkWantIt.setChecked(collectable.getWantIt());
            checkGotIt.setChecked(collectable.getGotIt());
            textDescription.setText(collectable.getDescription());
        } else {
            Log.e(TAG, "Object is null, wait for the future?");
        }

        // todo: make fields without values visible anyway
        // make fields default read-only, change to editable by clicking on them
        // save fields when leaving them

        /*textItemName.addTextChangedListener(new TextWatcher) {
            @Override
                    public void afterTextChanged(Editable s) {

            }
        };
        */

    }
        private class QueryData implements Runnable {
            @Override
            public void run() {
                Uri uri = ContentUris.withAppendedId(CollectableProvider.URI_COLLECTABLES, identifier);
                String[] projection = {"id", "name", "description", "country", "city", "imageUri", "wantIt", "gotIt"};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                while(cursor.moveToNext()) {
                    collectable = new Collectable();
                    collectable.setId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("id"))));
                    collectable.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    collectable.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    collectable.setCountry(cursor.getString(cursor.getColumnIndexOrThrow("country")));
                    collectable.setCity(cursor.getString(cursor.getColumnIndexOrThrow("city")));
                    collectable.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow("imageUri")));
                    collectable.setWantIt(cursor.getInt(cursor.getColumnIndexOrThrow("wantIt")) == 1);
                    collectable.setGotIt(cursor.getInt(cursor.getColumnIndexOrThrow("gotIt")) == 1);
                }
                cursor.close();
            }
        }
}