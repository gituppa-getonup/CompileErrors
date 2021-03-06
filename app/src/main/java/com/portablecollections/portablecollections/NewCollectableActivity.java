package com.portablecollections.portablecollections;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NewCollectableActivity extends AppCompatActivity {

    private ContentValues contentValues;
    private static final String TAG = NewCollectableActivity.class.getName();
    //private CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper(this);
    private Uri returnUri = null;
    private String imageUriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_collectable);

        final ImageView newImageView = findViewById(R.id.new_imageview);

        final Intent intent = getIntent();

        if (intent.hasExtra("takenPictureFilePath")) {
            imageUriString = intent.getStringExtra("takenPictureFilePath");
            Uri imageUri = Uri.parse(imageUriString);
            String pathName = imageUri.getPath();
            CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper(this);
            pictureHelper.setWidthHeight();
            Bitmap takenPicture = pictureHelper.decodeSampledBitmapFromFile(pathName, imageUriString, pictureHelper.width, pictureHelper.height);
            newImageView.setImageBitmap(takenPicture);
        } else if (intent.hasExtra("chosenPictureUri")) {
            imageUriString = intent.getStringExtra("chosenPictureUri");
            Uri imageUri = Uri.parse(imageUriString);
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUri
                    , filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            imageUriString = Uri.fromFile(new File(picturePath)).toString();
            CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper(this);
            pictureHelper.setWidthHeight();
            Bitmap chosenPicture = pictureHelper.decodeSampledBitmapFromFile(picturePath, imageUriString, pictureHelper.width, pictureHelper.height);
            newImageView.setImageBitmap(chosenPicture);
        }

        Button done = this.findViewById(R.id.new_done);
        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText newNameText = findViewById(R.id.new_name);
                String newName = newNameText.getText().toString();

                contentValues = new ContentValues();
                contentValues.put("name", newName);
                contentValues.put("imageUri", imageUriString);

                ExecutorService es = Executors.newSingleThreadExecutor();
                Future future = es.submit(new InsertData());
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "something went wrong creating the Room object in a separate thread");
                }

                long newIdentifier = Long.valueOf(returnUri.getLastPathSegment());

                Collectable collectable = new Collectable();
                collectable.setId(newIdentifier);
                collectable.setName(newName);
                collectable.setImageUri(imageUriString);

                Bundle bundle = new Bundle();
                bundle.putParcelable("collectable", collectable);

                Intent backToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                backToMainActivity.putExtras(bundle);
                startActivity(backToMainActivity);
            }
        });
    }

    private class InsertData implements Runnable {
        @Override
        public void run() {
            returnUri = getContentResolver().insert(CollectableProvider.URI_COLLECTABLES, contentValues);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CollectablePictureHelper.getCollectablePictureHelper(this).unbindDrawables(findViewById(R.id.newCollectableLayout));
    }


}
