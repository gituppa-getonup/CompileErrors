package com.portablecollections.portablecollections;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private final static int LOADER_COLLECTABLES = 1;
    public final static int CAMERA_REQUEST = 1;
    private Bitmap takenPicture;
    private CollectableAdapter mCollectableAdapter;
    private File imageFile;
    CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recycler1 = findViewById(R.id.recycler1);
        recycler1.setLayoutManager(new LinearLayoutManager(recycler1.getContext()));

        mCollectableAdapter = new CollectableAdapter();
        recycler1.setAdapter(mCollectableAdapter);
        getSupportLoaderManager().initLoader(1, null, mLoaderCallbacks);

        Button addCollectableButton = this.findViewById(R.id.addCollectable);
        addCollectableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                imageFile = pictureHelper.createImageFile(getApplicationContext());
                if(imageFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureHelper.imageUri);
                }
                cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    // todo: insert the creation of a temp file here and pass it to the intent.
    // the camera writes to that temp file,
    // the temp file's uri should be retrieved,
    // the uri is stored in Room,
    // the image is broadcasted to the gallery,
    // the recycleview is fed by images from Room's picture uri's,

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent takePictureIntent) {
        try{
            Uri imageUri = Uri.parse(pictureHelper.imageFilePath);
            takenPicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            Log.e(TAG, "Unable to read the bitmap of the taken picture");
        }

        byte[] takenPictureArray = pictureHelper.getByteArrayFromBitmap(takenPicture);

        Intent intent = new Intent(getApplicationContext(), NewCollectableActivity.class);
        intent.putExtra("picture", takenPictureArray);
        startActivity(intent);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    switch (id) {
                        case LOADER_COLLECTABLES:
                            return new CursorLoader(getApplicationContext(),
                                    CollectableProvider.URI_COLLECTABLES,
                                    new String[]{"name", "description", "country", "city", "imageUri"},
                                    null,
                                    null,
                                    null
                            );
                        default:
                            Log.e(TAG, "mLoaderCallbacks");
                            throw new IllegalArgumentException();
                    }
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    switch (loader.getId()) {
                        case LOADER_COLLECTABLES:
                            mCollectableAdapter.setCollectables(cursor);
                            break;
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    switch (loader.getId()) {
                        case LOADER_COLLECTABLES:
                            mCollectableAdapter.setCollectables(null);
                            break;
                    }
                }
            };
}