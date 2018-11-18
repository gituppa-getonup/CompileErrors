package com.portablecollections.portablecollections;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements AddCollectableDialogFragment.NoticeDialogListener {

    private static final String TAG = MainActivity.class.getName();
    private final static int LOADER_COLLECTABLES = 1;
    public final static int TAKE_PHOTO = 1;
    public final static int PICK_IMAGE_GALLERY = 2;
    private Bitmap takenPicture;
    private CollectableAdapter collectableAdapter;
    private File imageFile;
    CollectablePictureHelper pictureHelper = CollectablePictureHelper.getCollectablePictureHelper(this);
    private boolean emptyDb = true;
    private RecyclerView recycler;
    private int adapterPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(recycler.getContext(), LinearLayoutManager.HORIZONTAL, false));

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recycler);

        collectableAdapter = CollectableAdapter.getCollectableAdapter(this);

        recycler.setAdapter(collectableAdapter);
        getSupportLoaderManager().initLoader(1, null, mLoaderCallbacks);

        ExecutorService es = Executors.newSingleThreadExecutor();
        Future future = es.submit(new QueryCount());

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "something went wrong counting the records in a separate thread");
        }

        if (!emptyDb) {
            FloatingActionButton details = findViewById(R.id.details);
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView = findViewById(R.id.recyclerImageView);
                    Collectable collectable = (Collectable) imageView.getTag(R.id.TAG_COLLECTABLE);
                    Intent detailsIntent = new Intent(getApplicationContext(), CollectableDetails.class);
                    detailsIntent.putExtra("collectable", collectable);
                    startActivity(detailsIntent);
                }
            });
        } else {
            findViewById(R.id.details).setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Click + to add your first collectable!"
                    , Toast.LENGTH_LONG).show();
        }


        FloatingActionButton add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("adapterPosition", adapterPosition);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adapterPosition = savedInstanceState.getInt("adapterPosition");
        recycler.scrollToPosition(adapterPosition);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent takePictureIntent) {
        if (takePictureIntent == null || resultCode != Activity.RESULT_OK) {
            return;
        }

        Intent intent = new Intent(this, NewCollectableActivity.class);

        switch(requestCode) {
            case TAKE_PHOTO:
                try {
                    Uri imageUri = Uri.parse(pictureHelper.imageFilePath);
                    takenPicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to read the bitmap of the taken picture");
                }

                byte[] takenPictureArray = pictureHelper.getByteArrayFromBitmap(takenPicture);
                intent.putExtra("pictureArray", takenPictureArray);

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
                intent.putExtra("picturePath", picturePath);
                break;
        }
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

                            collectableAdapter.clear();
                            collectableAdapter.addAll(collectableArrayList);

                            Intent intent = getIntent();
                            if (intent.hasExtra("collectable")) {
                                Collectable collectable = intent.getParcelableExtra("collectable");
                                adapterPosition = collectableAdapter.add(collectable);
                                recycler.scrollToPosition(adapterPosition);
                            }
                    }
                }


                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    switch (loader.getId()) {
                        case LOADER_COLLECTABLES:
                            collectableAdapter.clear();
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

    public void showAddDialog() {
        DialogFragment dialog = new AddCollectableDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("source","main");
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "AddCollectableDialogFragment");
    }

    @Override
    public void onDialogTakePhotoClick(DialogFragment dialog) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        imageFile = pictureHelper.createImageFile(getApplicationContext());
        if (imageFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureHelper.imageUri);
        }
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


}