package com.portablecollections.portablecollections;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class CollectablePictureHelper {

    private static CollectablePictureHelper collectablePictureHelper;
    private static final String TAG = CollectablePictureHelper.class.getPackage().getName();
    private File image;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;

    public static CollectablePictureHelper getCollectablePictureHelper() {
        if (collectablePictureHelper == null) {
            collectablePictureHelper = new CollectablePictureHelper();
        }
        return collectablePictureHelper;
    }


    /* when the file was also saved */
    /*
    public Intent dispatchTakePictureIntent(Context context) {

        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {


            File photoFile = null;
            photoFile = createImageFile(context, collectableName);
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(context,
                        "com.portablecollections.portablecollections.CollectablePictureHelper",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            }



        }
        return takePictureIntent;
    }
    */

    public byte[] getByteArrayFromBitmap(Bitmap takenPicture) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        takenPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] takenPictureArray = stream.toByteArray();
        return takenPictureArray;
    }

    public Bitmap receiveImage(int requestCode, int resultCode, Context context, Intent takePictureIntent) {
        if (requestCode == MainActivity.CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (takePictureIntent.getExtras().get("data") != null) {
                mImageBitmap = (Bitmap) takePictureIntent.getExtras().get("data");
            } else {
                Log.e(TAG, "takePictureIntent extras data is null");
            }
        }
        return mImageBitmap;
    }

    public File createImageFile(Context context, String collectableName) {
        try {
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(collectableName, " .jpg", storageDir);
        } catch (IOException e) {
            Log.e(TAG, "IOException");
        }
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

}
