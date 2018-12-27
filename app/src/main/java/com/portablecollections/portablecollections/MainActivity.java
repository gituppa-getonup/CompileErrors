package com.portablecollections.portablecollections;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements AddCollectableDialogFragment.NoticeDialogListener {

    private final static String TAG = MainActivity.class.getName();
    private final static int LOADER_COLLECTABLES = 1;
    private final static int TAKE_PHOTO = 1;
    private final static int PICK_IMAGE_GALLERY = 2;
    private CollectableAdapter collectableAdapter;
    private boolean emptyDb = true;
    private RecyclerView recycler;
    private int adapterPosition = -1;
    private String query;
    private String searchString;

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
        getSupportLoaderManager().initLoader(LOADER_COLLECTABLES, null, loaderCallback);

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


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = findViewById(R.id.searchView);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String submitString) {
                searchString = submitString;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String changedText) {
                if (changedText.length() == 0) {
                    getSupportLoaderManager().restartLoader(LOADER_COLLECTABLES, null, loaderCallback);
                } else {
                    query = "%" + changedText + "%";
                    getSupportLoaderManager().restartLoader(LOADER_COLLECTABLES, null, loaderSearchCallback);
                }
                searchString = changedText;
                return true;
            }
        });

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchString = intent.getStringExtra(SearchManager.QUERY);
            query = "%" + searchString + "%";
            getSupportLoaderManager().restartLoader(LOADER_COLLECTABLES, null, loaderSearchCallback);
        }

        FloatingActionButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchView searchView = findViewById(R.id.searchView);
                int vis = searchView.getVisibility();
                if (vis == View.GONE) {
                    searchView.setVisibility(View.VISIBLE);
                    searchView.setQuery(searchString, false);
                } else {
                    searchView.setVisibility(View.GONE);
                }
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
        if (resultCode != RESULT_OK) {
            return;
        }

        Intent intent = new Intent(this, NewCollectableActivity.class);
        if (requestCode == TAKE_PHOTO) {
            String imageFilePath = CollectablePictureHelper.getCollectablePictureHelper(this).imageFilePath;
            intent.putExtra("takenPictureFilePath", imageFilePath);
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            Uri imageUri = takePictureIntent.getData();
            String chosenPictureUri = imageUri.toString();
            intent.putExtra("chosenPictureUri", chosenPictureUri);
        }
        startActivity(intent);
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallback =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    switch (id) {
                        case LOADER_COLLECTABLES:
                            return new CursorLoader(getApplicationContext(),
                                    CollectableProvider.URI_COLLECTABLES,
                                    new String[]{"id", "name", "description", "country", "city", "imageUri", "wantIt", "gotIt", "number"},
                                    null,
                                    null,
                                    "name asc"
                            );
                        default:
                            Log.e(TAG, "loaderCallback");
                            throw new IllegalArgumentException();
                    }
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    switch (loader.getId()) {
                        case LOADER_COLLECTABLES:
                            List<Collectable> collectableArrayList = new ArrayList<>();

                            if (cursor.getCount() != 0) {
                                findViewById(R.id.details).setVisibility(View.VISIBLE);
                            }

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
                                collectable.setNumber(cursor.getInt(cursor.getColumnIndexOrThrow("number")));
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

    private LoaderManager.LoaderCallbacks<Cursor> loaderSearchCallback =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    switch (id) {
                        case LOADER_COLLECTABLES:
                            return new CursorLoader(getApplicationContext(),
                                    CollectableProvider.URI_COLLECTABLES,
                                    new String[]{"id", "name", "description", "country", "city", "imageUri", "wantIt", "gotIt", "number"},
                                    "name LIKE ? OR description LIKE ? OR country LIKE ? OR city LIKE ?",
                                    new String[]{query},
                                    "name asc"
                            );
                        default:
                            Log.e(TAG, "loaderSearchCallback");
                            throw new IllegalArgumentException();
                    }
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    switch (loader.getId()) {
                        case LOADER_COLLECTABLES:
                            List<Collectable> collectableArrayList = new ArrayList<>();

                            if (cursor.getCount() == 0) {
                                findViewById(R.id.details).setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Nothing found with this search"
                                        , Toast.LENGTH_LONG).show();
                            }

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
                                collectable.setNumber(cursor.getInt(cursor.getColumnIndexOrThrow("number")));
                                collectableArrayList.add(collectable);
                            }

                            collectableAdapter.clear();
                            collectableAdapter.addAll(collectableArrayList);

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
        bundle.putString("source", "main");
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "AddCollectableDialogFragment");
    }

    @Override
    public void onDialogTakePhotoClick(DialogFragment dialog) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = CollectablePictureHelper.getCollectablePictureHelper(this).createImageFile(getApplicationContext());
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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

    @Override
    protected void onPause() {
        super.onPause();
        CollectablePictureHelper.getCollectablePictureHelper(this).unbindDrawables(findViewById(R.id.porLayout));
        recycler.removeView(recycler);
    }


}