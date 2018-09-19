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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    Uri uri;

    final ContentValues contentValues = new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        identifier = extras.getLong("identifier", 0L);
        uri = ContentUris.withAppendedId(CollectableProvider.URI_COLLECTABLES, identifier);

        contentValues.put("id", identifier);
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future future = es.submit(new QueryData());

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "something went wrong creating the Room object in a separate thread");
        }

        // pre-fill the contentValues to prevent erasure of fields:
        contentValues.put("id", collectable.getId());
        contentValues.put("imageUri", collectable.getImageUri());
        contentValues.put("name", collectable.getName());
        contentValues.put("description", collectable.getDescription());
        contentValues.put("country", collectable.getCountry());
        contentValues.put("city", collectable.getCity());
        contentValues.put("wantIt", collectable.getWantIt());
        contentValues.put("gotIt", collectable.getGotIt());

        // define the views:
        ImageView detailsImage = findViewById(R.id.detailsImage);
        EditText textItemName = findViewById(R.id.textItemName);
        EditText textCountry = findViewById(R.id.textCountry);
        EditText textCity = findViewById(R.id.textCity);
        CheckBox checkWantIt = findViewById(R.id.checkWantIt);
        CheckBox checkGotIt = findViewById(R.id.checkGotIt);
        EditText textDescription = findViewById(R.id.textDescription);

        // fill the views:
        if (collectable != null) {
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

        textItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                contentValues.put("name", s.toString());
                ExecutorService es = Executors.newSingleThreadExecutor();
                Future future = es.submit(new UpdateData());
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "something went wrong updating the Room object in a separate thread");
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int j, int k) { }

            @Override
            public void beforeTextChanged(CharSequence s, int i, int j, int k) { }
        });

        textDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                contentValues.put("description", s.toString());
                ExecutorService es = Executors.newSingleThreadExecutor();
                Future future = es.submit(new UpdateData());
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "something went wrong updating the Room object in a separate thread");
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int j, int k) { }

            @Override
            public void beforeTextChanged(CharSequence s, int i, int j, int k) { }
        });

        textCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                contentValues.put("country", s.toString());
                ExecutorService es = Executors.newSingleThreadExecutor();
                Future future = es.submit(new UpdateData());
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "something went wrong updating the Room object in a separate thread");
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int j, int k) { }

            @Override
            public void beforeTextChanged(CharSequence s, int i, int j, int k) { }
        });

        textCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                contentValues.put("city", s.toString());
                ExecutorService es = Executors.newSingleThreadExecutor();
                Future future = es.submit(new UpdateData());
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "something went wrong updating the Room object in a separate thread");
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int j, int k) { }

            @Override
            public void beforeTextChanged(CharSequence s, int i, int j, int k) { }
        });

        textDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                contentValues.put("description", s.toString());
                ExecutorService es = Executors.newSingleThreadExecutor();
                Future future = es.submit(new UpdateData());
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "something went wrong updating the Room object in a separate thread");
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int j, int k) { }

            @Override
            public void beforeTextChanged(CharSequence s, int i, int j, int k) { }
        });

        checkWantIt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                contentValues.put("wantIt", b);
                ExecutorService es = Executors.newSingleThreadExecutor();
                Future future = es.submit(new UpdateData());
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "something went wrong updating the Room object in a separate thread");
                }
            }
        }
        );

        checkGotIt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                   @Override
                                                   public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                       contentValues.put("gotIt", b);
                                                       ExecutorService es = Executors.newSingleThreadExecutor();
                                                       Future future = es.submit(new UpdateData());
                                                       try {
                                                           future.get();
                                                       } catch (InterruptedException | ExecutionException e) {
                                                           Log.e(TAG, "something went wrong updating the Room object in a separate thread");
                                                       }
                                                   }
                                               }
        );


    }

    private class QueryData implements Runnable {
        @Override
        public void run() {
            String[] projection = {"id", "name", "description", "country", "city", "imageUri", "wantIt", "gotIt"};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            while (cursor.moveToNext()) {
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

    private class UpdateData implements Runnable {
        @Override
        public void run() {
            int updatedRecords = getContentResolver().update(uri, contentValues, null, null);
        }
    }

}