package com.portablecollections.portablecollections;


import android.app.Activity;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CollectablePictureHelper extends FileProvider {

    private static CollectablePictureHelper collectablePictureHelper;
    private static final String TAG = CollectablePictureHelper.class.getPackage().getName();
    File imageFile;
    String imageFilePath;
    Uri imageUri;

    static CollectablePictureHelper getCollectablePictureHelper() {
        if (collectablePictureHelper == null) {
            collectablePictureHelper = new CollectablePictureHelper();
        }
        return collectablePictureHelper;
    }

    byte[] getByteArrayFromBitmap(Bitmap takenPicture) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        takenPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    Bitmap getBitmapFromString(String imageUriString, Context context) {
        Bitmap imageBitmap = null;
        Uri imageUri = Uri.parse(imageUriString);
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            Log.e(TAG, "IOException while converting string to uri to bitmap");
        }
        return imageBitmap;
        }

    File createImageFile(Context context) {
        try {
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmSS", new Locale("en-US")).format(new Date());
            imageFile = File.createTempFile(timestamp, ".jpg", storageDir);
            imageUri = FileProvider.getUriForFile(context,
                    "com.portablecollections.portablecollections.fileprovider",
                    imageFile);

        } catch (IOException e) {
            Log.e(TAG, "IOException");
        }
        imageFilePath = "file:" + imageFile.getAbsolutePath();
        return imageFile;
    }

}
