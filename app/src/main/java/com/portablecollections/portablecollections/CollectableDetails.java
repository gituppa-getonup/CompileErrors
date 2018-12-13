package com.portablecollections.portablecollections;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CollectableDetails extends AppCompatActivity implements DeleteCollectableDialogFragment.NoticeDialogListener, AddCollectableDialogFragment.NoticeDialogListener {

    private static final String TAG = CollectableDetails.class.getName();

    Bitmap detailsBitmap;
    private Collectable collectable;
    private CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper(this);
    private int adapterPosition = -1;
    public final static int TAKE_PHOTO = 1;
    public final static int PICK_IMAGE_GALLERY = 2;
    private String imageUriString;

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
        Button detailsDeleteButton = findViewById(R.id.details_delete);
        EditText textNumber = findViewById(R.id.number);

        // retrieve collectable from intent:
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        collectable = (Collectable) extras.getParcelable("collectable");
        adapterPosition = extras.getInt("adapterPosition");
        uri = ContentUris.withAppendedId(CollectableProvider.URI_COLLECTABLES, collectable.getId());

        // pre-fill the contentValues to prevent erasure of fields:
        fillContentValues();

        // fill the views:
        detailsBitmap = pictureHelper.getBitmapFromString(collectable.getImageUri());
        detailsImage.setImageBitmap(detailsBitmap);
        //detailsBitmap.recycle();

        textItemName.setText(collectable.getName());
        textCountry.setText(collectable.getCountry());
        textCity.setText(collectable.getCity());
        checkWantIt.setChecked(collectable.getWantIt());
        checkGotIt.setChecked(collectable.getGotIt());
        textDescription.setText(collectable.getDescription());
        textNumber.setText(String.valueOf(collectable.getNumber()));


        detailsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });

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

        textNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s == null || s.toString().equalsIgnoreCase("")) {
                    return;
                }
                contentValues.put("number", s.toString());
                collectable.setNumber(Integer.valueOf(s.toString()));
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

        detailsDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                backToMainActivity.putExtra("collectable", collectable);
                startActivity(backToMainActivity);
            }
        });

        detailsDeleteButton.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View view) {
                                                       showDeleteDialog();
                                                   }
                                               }
        );

    }

    private void fillContentValues() {
        contentValues.put("id", collectable.getId());
        contentValues.put("imageUri", collectable.getImageUri());
        contentValues.put("name", collectable.getName());
        contentValues.put("description", collectable.getDescription());
        contentValues.put("country", collectable.getCountry());
        contentValues.put("city", collectable.getCity());
        contentValues.put("wantIt", collectable.getWantIt());
        contentValues.put("gotIt", collectable.getGotIt());
        contentValues.put("number", collectable.getNumber());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("collectable", collectable);
        savedInstanceState.putInt("adapterPosition", adapterPosition);
        savedInstanceState.putString("uri", uri.toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        collectable = savedInstanceState.getParcelable("collectable");
        adapterPosition = savedInstanceState.getInt("adapterPosition");
        uri = Uri.parse(savedInstanceState.getString("uri"));
        fillContentValues();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent takePictureIntent) {
        if (takePictureIntent == null || resultCode != Activity.RESULT_OK) {
            return;
        }

        ImageView detailsImage = findViewById(R.id.detailsImage);

        switch(requestCode) {
            case TAKE_PHOTO:
                try {
                    Uri imageUri = Uri.parse(pictureHelper.imageFilePath);
                    imageUriString = imageUri.toString();
                    detailsBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to read the bitmap of the taken picture");
                }

                byte[] takenPictureArray = pictureHelper.getByteArrayFromBitmap(detailsBitmap);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                Bitmap takenPicture = BitmapFactory.decodeByteArray(takenPictureArray, 0, takenPictureArray.length, options);
                int imageWidth = options.outWidth;
                int imageHeight = options.outHeight;
                int inSampleSize = 1;
                detailsImage.setImageBitmap(takenPicture);
                break;

            case PICK_IMAGE_GALLERY:
                Uri imageUri = takePictureIntent.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(imageUri
                        , filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Bitmap chosenPicture = BitmapFactory.decodeFile(picturePath);
                detailsImage.setImageBitmap(chosenPicture);
                imageUriString = Uri.fromFile(new File(picturePath)).toString();
                break;
        }
        contentValues.put("imageUri", imageUriString);
        collectable.setImageUri(imageUriString);
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future future = es.submit(new UpdateData());
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "something went wrong updating the Room object in a separate thread");
        }



    }

    public void showAddDialog() {
        DialogFragment dialog = new AddCollectableDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("source","details");
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "AddCollectableDialogFragment");
    }

    @Override
    public void onDialogTakePhotoClick(DialogFragment dialog) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = pictureHelper.createImageFile(getApplicationContext());
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(cameraIntent, TAKE_PHOTO);
    }

    @Override
    public void onDialogPickImageClick(DialogFragment dialog) {

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_GALLERY);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE_GALLERY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case PICK_IMAGE_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

    public void showDeleteDialog() {
        DialogFragment dialog = new DeleteCollectableDialogFragment();
        dialog.show(getSupportFragmentManager(), "DeleteCollectableDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future future = es.submit(new DeleteData());
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "something went wrong deleting the Room object in a separate thread");
        }
        Intent backToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(backToMainActivity);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }


    private class UpdateData implements Runnable {
        @Override
        public void run() {
            int updatedRecords = getContentResolver().update(uri, contentValues, null, null);
        }
    }

    private class DeleteData implements Runnable {
        @Override
        public void run() {
            int deletedRecords = getContentResolver().delete(uri, null, null);
        }
    }

}