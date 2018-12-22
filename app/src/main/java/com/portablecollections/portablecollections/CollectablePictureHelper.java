package com.portablecollections.portablecollections;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

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
    int width;
    int height;

    static CollectablePictureHelper getCollectablePictureHelper(Context context) {
        if (collectablePictureHelper == null) {
            collectablePictureHelper = new CollectablePictureHelper(context);
        }
        return collectablePictureHelper;
    }

    private CollectablePictureHelper(Context context) {
        this.context = context;
    }

    public void setWidthHeight() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
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

    public static Bitmap decodeSampledBitmapFromFile(String pathName, String imageUriString, int reqWidth, int reqHeight) {

        Uri imageUri = Uri.parse(imageUriString);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(pathName, options);
        bmp = rotateImageIfRequired(bmp, imageUri);
        return bmp;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) {

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(selectedImage.getPath());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

}
