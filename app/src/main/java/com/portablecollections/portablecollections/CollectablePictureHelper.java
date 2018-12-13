package com.portablecollections.portablecollections;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private final static String TAG = CollectablePictureHelper.class.getPackage().getName();
    String imageFilePath;
    Uri imageUri;
    Context context;

    static CollectablePictureHelper getCollectablePictureHelper(Context context) {
        if (collectablePictureHelper == null) {
            collectablePictureHelper = new CollectablePictureHelper(context);
        }
        return collectablePictureHelper;
    }

    private CollectablePictureHelper(Context context) {
        this.context = context;
    }

    byte[] getByteArrayFromBitmap(Bitmap takenPicture) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        takenPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    Bitmap getBitmapFromString(String imageUriString) {
        Bitmap imageBitmap = null;
        Uri imageUri = Uri.parse(imageUriString);
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            Log.e(TAG, "IOException while converting string to uri to bitmap");
        }
        return imageBitmap;
    }

    Uri createImageFile(Context context) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmSS", new Locale("en-US")).format(new Date());
        String fileName = timestamp + ".jpg";
        File storageDir;
        boolean emuSDCard = Environment.isExternalStorageEmulated();
        String state = Environment.getExternalStorageState();
        if (!emuSDCard && Environment.MEDIA_MOUNTED.equals(state)) {
            storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            storageDir = context.getFilesDir();
        }

        File imageFile = new File(storageDir, fileName);
        imageUri = FileProvider.getUriForFile(context,
                "com.portablecollections.portablecollections.fileprovider",
                imageFile);
        imageFilePath = "file:" + imageFile.getAbsolutePath();

        return imageUri;

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }





}
