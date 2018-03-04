package com.portablecollections.portablecollections;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


public class CollectableProvider extends ContentProvider {

    private static final String TAG = CollectableProvider.class.getName();
    public static final String AUTHORITY = CollectableProvider.class.getPackage().getName();
    public static final Uri URI_COLLECTABLES = Uri.parse(
            "content://" + AUTHORITY + "/" + "collectables");
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
            MATCHER.addURI(AUTHORITY, "collectables", 1);
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int code = MATCHER.match(uri);
        if(code == 1) {
            final Context context = getContext();
            if(context == null) {
                return null;
            }
            CollectableDao collectableDao = CollectableDatabase.getInstance(context).collectableDao();
            final Cursor cursor;
            if(code == 1) {
                cursor = collectableDao.selectAll();
            } else {
                Log.e(TAG, "code is not 1");
            }
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;



        } else {
            Log.e(TAG, "unknown URI");
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }


        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case 1:
                return URI_COLLECTABLES;
        }
        default:
            Log.e(TAG, "getType illegal arguments");
            throw new IllegalArgumentException("Unknown uri: " + uri);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        switch(MATCHER.match(uri)) {
            case 1:
                final Context context = getContext();
                if (context == null) {
                    return null;
                }
                final int id = CollectableDatabase.getInstance(context).collectableDao()
                        .insert(Collectable.fromContentValues(contentValues));
                context.getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                Log.e(TAG, "illegal arguments, insert");
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        switch(MATCHER.match(uri)) {
            case 2:
                final Context context = getContext();
                if(context == null) {
                    return 0;
                }
                final int count = CollectableDatabase.getInstance(context).collectableDao()
                        .deleteById(ContentUris.parseId(uri));
                context.getContentResolver().notifyChange(uri, null);
                return count;

            default:
                Log.e(TAG, "invalid arguments, delete");
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        switch(MATCHER.match(uri)) {
            case 2:
                final Context context = getContext();
                if(context == null) {
                    return 0;
                }
                final Collectable collectable = Collectable.fromContentValues(contentValues);
                collectable.setId(ContentUris.parseId());
                final int count = CollectableDatabase.getInstance(context).collectableDao()
                        .update(collectable);
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                Log.e(TAG, "invalid arguments, update");
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
