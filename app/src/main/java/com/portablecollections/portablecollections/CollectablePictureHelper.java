package com.portablecollections.portablecollections;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class CollectablePictureHelper {

    private static final String TAG = CollectablePictureHelper.class.getPackage().getName();
    File image;

    public Intent dispatchTakePictureIntent(Context context, String collectableName) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createImageFile(context, collectableName);
            if(photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(context,
                        CollectablePictureHelper.class.getPackage().getName() + CollectablePictureHelper.class.toString(),
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            }
        }
        return takePictureIntent;
    }


    private File createImageFile(Context context, String collectableName) {
        try {
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(collectableName, " .jpg", storageDir);
        } catch (IOException e) {
            Log.e(TAG, "IOException");
        }
        return image;
    }



}
