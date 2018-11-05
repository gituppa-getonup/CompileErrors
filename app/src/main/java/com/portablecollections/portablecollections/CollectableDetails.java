package com.portablecollections.portablecollections;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CollectableDetails extends AppCompatActivity {

    private static final String TAG = CollectableDetails.class.getName();

    Bitmap detailsBitmap;
    private Collectable collectable;
    private CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper(this);
    private int adapterPosition = -1;

    Uri uri;

    final ContentValues contentValues = new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        // define the views:
        ImageView detailsImage = findViewById(R.id.detailsImage);
        EditText textItemName = findViewById(R.id.textItemName);
        EditText textCountry = findViewById(R.id.textCountry);
        EditText textCity = findViewById(R.id.textCity);
        CheckBox checkWantIt = findViewById(R.id.checkWantIt);
        CheckBox checkGotIt = findViewById(R.id.checkGotIt);
        EditText textDescription = findViewById(R.id.textDescription);
        Button detailsDoneButton = findViewById(R.id.details_done);

        // retrieve collectable from intent:
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        collectable = (Collectable) extras.getParcelable("collectable");
        adapterPosition = extras.getInt("adapterPosition");
        uri = ContentUris.withAppendedId(CollectableProvider.URI_COLLECTABLES, collectable.getId());

        // pre-fill the contentValues to prevent erasure of fields:
        contentValues.put("id", collectable.getId());
        contentValues.put("imageUri", collectable.getImageUri());
        contentValues.put("name", collectable.getName());
        contentValues.put("description", collectable.getDescription());
        contentValues.put("country", collectable.getCountry());
        contentValues.put("city", collectable.getCity());
        contentValues.put("wantIt", collectable.getWantIt());
        contentValues.put("gotIt", collectable.getGotIt());

        // fill the views:
        detailsBitmap = pictureHelper.getBitmapFromString(collectable.getImageUri());
        detailsImage.setImageBitmap(detailsBitmap);

        textItemName.setText(collectable.getName());
        textCountry.setText(collectable.getCountry());
        textCity.setText(collectable.getCity());
        checkWantIt.setChecked(collectable.getWantIt());
        checkGotIt.setChecked(collectable.getGotIt());
        textDescription.setText(collectable.getDescription());


        textItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                contentValues.put("name", s.toString());
                collectable.setName(s.toString());
                ExecutorService es = Executors.newSingleThreadExecutor();
                Future future = es.submit(new UpdateData());
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "something went wrong updating the Room object in a separate thread");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int j, int k) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int i, int j, int k) {
            }
        });

        textDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                contentValues.put("description", s.toString());
                collectable.setDescription(s.toString());
                ExecutorService es = Executors.newSingleThreadExecutor();
                Future future = es.submit(new UpdateData());
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "something went wrong updating the Room object in a separate thread");
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int j, int k) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int i, int j, int k) {
            }
        });

        textCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                contentValues.put("country", s.toString());
                collectable.setCountry(s.toString());
                ExecutorService es = Executors.newSingleThreadExecutor();
                Future future = es.submit(new UpdateData());
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "something went wrong updating the Room object in a separate thread");
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int j, int k) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int i, int j, int k) {
            }
        });

        textCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                contentValues.put("city", s.toString());
                collectable.setCity(s.toString());
                ExecutorService es = Executors.newSingleThreadExecutor();
                Future future = es.submit(new UpdateData());
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "something went wrong updating the Room object in a separate thread");
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int j, int k) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int i, int j, int k) {
            }
        });

        textDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                contentValues.put("description", s.toString());
                collectable.setDescription(s.toString());
                ExecutorService es = Executors.newSingleThreadExecutor();
                Future future = es.submit(new UpdateData());
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "something went wrong updating the Room object in a separate thread");
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int j, int k) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int i, int j, int k) {
            }
        });

        checkWantIt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                   @Override
                                                   public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                       contentValues.put("wantIt", b);
                                                       collectable.setWantIt(b);
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
                                                      collectable.setGotIt(b);
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

        detailsDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                backToMainActivity.putExtra("collectable", collectable);
                startActivity(backToMainActivity);
            }
        });

        if (textItemName.getText().length() == 0) {
            textItemName.setText("name");
        }

        if (textCity.getText().length() == 0) {
            textCity.setText("city");
        }

        if (textCountry.getText().length() == 0) {
            textCountry.setText("country");
        }

        if (textDescription.getText().length() == 0) {
            textDescription.setText("description");
        }


    }

    private class UpdateData implements Runnable {
        @Override
        public void run() {
            int updatedRecords = getContentResolver().update(uri, contentValues, null, null);
        }
    }

}