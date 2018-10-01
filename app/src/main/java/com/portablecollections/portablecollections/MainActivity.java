package com.portablecollections.portablecollections;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    // todo: landscape pics should not block the input in the details view
    // todo: create option to delete a collectable
    // todo: after adding a collectable, return to that one
    // todo: auto sort on name

    private static final String TAG = MainActivity.class.getName();
    private final static int LOADER_COLLECTABLES = 1;
    public final static int CAMERA_REQUEST = 1;
    private Bitmap takenPicture;
    private CollectableAdapter mCollectableAdapter;
    private File imageFile;
    CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper(this);
    private boolean emptyDb = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recycler = findViewById(R.id.recycler);

        recycler.setLayoutManager(new LinearLayoutManager(recycler.getContext(), LinearLayoutManager.HORIZONTAL, false));
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recycler);

        mCollectableAdapter = CollectableAdapter.getCollectableAdapter(this);
        recycler.setAdapter(mCollectableAdapter);
        getSupportLoaderManager().initLoader(1, null, mLoaderCallbacks);

        ExecutorService es = Executors.newSingleThreadExecutor();
        Future future = es.submit(new QueryCount());

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "something went wrong counting the records in a separate thread");
        }

        if (emptyDb == false) {
            FloatingActionButton details = findViewById(R.id.details);
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView = findViewById(R.id.recyclerImageView);
                    Collectable collectable = (Collectable) imageView.getTag();

                    Intent detailsIntent = new Intent(getApplicationContext(), CollectableDetails.class);
                    detailsIntent.putExtra("collectable", collectable);
                    startActivity(detailsIntent);


                }
            });
        } else {
            FloatingActionButton details = findViewById(R.id.details);
            details.setVisibility(View.INVISIBLE);
        }


        FloatingActionButton add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                imageFile = pictureHelper.createImageFile(getApplicationContext());
                if (imageFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureHelper.imageUri);
                }
                cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent takePictureIntent) {
        try {
            Uri imageUri = Uri.parse(pictureHelper.imageFilePath);
            takenPicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            Log.e(TAG, "Unable to read the bitmap of the taken picture");
        }

        byte[] takenPictureArray = pictureHelper.getByteArrayFromBitmap(takenPicture);

        Intent intent = new Intent(this, NewCollectableActivity.class);
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
                                    new String[]{"id", "name", "description", "country", "city", "imageUri", "wantIt", "gotIt"},
                                    null,
                                    null,
                                    "name asc"
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
                            List<Collectable> collectableArrayList = new ArrayList<>();
                            while (cursor.moveToNext()) {
                                Collectable collectable = new Collectable();
                                collectable.setId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("id"))));
                                collectable.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                                collectable.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                                collectable.setCountry(cursor.getString(cursor.getColumnIndexOrThrow("country")));
                                collectable.setCity(cursor.getString(cursor.getColumnIndexOrThrow("city")));
                                collectable.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow("imageUri")));
                                collectable.setWantIt(cursor.getInt(cursor.getColumnIndexOrThrow("wantIt")) == 1);
                                collectable.setGotIt(cursor.getInt(cursor.getColumnIndexOrThrow("gotIt")) == 1);
                                collectableArrayList.add(collectable);
                            }
                            mCollectableAdapter.clear();
                            mCollectableAdapter.addAll(collectableArrayList);
                            break;
                    }
                }


                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    switch (loader.getId()) {
                        case LOADER_COLLECTABLES:
                            mCollectableAdapter.clear();
                            break;
                    }
                }
            };

    private class QueryCount implements Runnable {
        @Override
        public void run() {
            CollectableDao collectableDao = CollectableDatabase.getInstance(getApplicationContext()).collectableDao();
            int count = collectableDao.count();
            emptyDb = count == 0;
        }
    }
}