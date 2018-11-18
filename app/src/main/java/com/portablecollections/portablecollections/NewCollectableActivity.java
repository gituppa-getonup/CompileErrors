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
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NewCollectableActivity extends AppCompatActivity {

    private EditText newNameText;
    private ContentValues contentValues;
    private static final String TAG = NewCollectableActivity.class.getName();
    CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper(this);
    private Uri returnUri = null;
    private String imageUriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_collectable);

        final ImageView newImageView = findViewById(R.id.new_imageview);

        final Intent intent = getIntent();
        if (intent.hasExtra("pictureArray")) {
            byte[] takenPictureArray = intent.getByteArrayExtra("pictureArray");
            Bitmap takenPicture = BitmapFactory.decodeByteArray(takenPictureArray, 0, takenPictureArray.length);
            newImageView.setImageBitmap(takenPicture);
            imageUriString = Uri.fromFile(pictureHelper.imageFile).toString();
        } else if (intent.hasExtra("picturePath")) {
            String filePath = intent.getStringExtra("picturePath");
            Bitmap chosenPicture = BitmapFactory.decodeFile(filePath);
            newImageView.setImageBitmap(chosenPicture);
            imageUriString = Uri.fromFile(new File(filePath)).toString();
        }

        Button done = this.findViewById(R.id.new_done);
        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                newNameText = findViewById(R.id.new_name);
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
}
