package com.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Sandip Phatangare, Nextremer solutions India Pvt Ltd  on 22-06-2016.
 */
public class Util {

    // public  static String Server_URL="http://192.168.1.134:5000/".trim();
    public static String Server_URL = "https://stage.peoplemovers.com/mobile/setmobiledata".trim();
   // public static String url = "http://stage.peoplemovers.com/mobile/setmobiledata".trim();
    public static int visibleStoriedThreshold = 5;
    public static int visibleThreshold = 5;
    public static int firstVisibleItem = 1, visibleItemCount = 5, totalItemCount = 0;

    public static String TAG = "Peoplemovers", flagFromSocial = "";
    public static HashMap<String, String> map = new HashMap<String, String>();
    public static ExpandableListAdapter adapter;

    public static boolean checkApp(Context context){
        ActivityManager am = (ActivityManager) context
                .getSystemService(ACTIVITY_SERVICE);

        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = taskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equalsIgnoreCase(context.getPackageName())) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    public static void retrieveContactName(Context cntx) //This Context parameter is nothing but your Activity class's Context
    {
        Cursor cursor = cntx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Integer contactsCount = cursor.getCount(); // get how many contacts you have in your contacts list
        if (contactsCount > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (contactName.toString().toLowerCase().contains("jadhav".toLowerCase())) {
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        //the below cursor will give you details for multiple contacts
                        Cursor pCursor = cntx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        // continue till this cursor reaches to all phone numbers which are associated with a contact in the contact list
                        while (pCursor.moveToNext()) {
                            int phoneType = pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            //String isStarred 		= pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED));
                            String phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            //you will get all phone numbers according to it's type as below switch case.
                            //Logs.e will print the phone number along with the name in DDMS. you can use these details where ever you want.
                            switch (phoneType) {
                                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                    Log.e(contactName + ": TYPE_MOBILE", " " + phoneNo);
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                    Log.e(contactName + ": TYPE_HOME", " " + phoneNo);
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                    Log.e(contactName + ": TYPE_WORK", " " + phoneNo);
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                                    Log.e(contactName + ": TYPE_WORK_MOBILE", " " + phoneNo);
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                    Log.e(contactName + ": TYPE_OTHER", " " + phoneNo);
                                    break;
                                default:
                                    break;
                            }
                        }
                        pCursor.close();
                    }
                }
            }
            cursor.close();
        }
    }


    public static File compressImage(String imageUri, Context context) {
        String fileName=getFilename();
        File file=null;
        String filePath = getRealPathFromURI(imageUri, context);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        //          by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //          you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        Log.e("Actual height","h:-"+actualHeight+" w:-"+actualWidth);
        //          max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 640.0f;
        float maxWidth = 640.0f;
        float imgRatio = (float)actualWidth / actualHeight;
        float maxRatio = (float)maxWidth / maxHeight;

        //          width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        //          setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        //          inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //          this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            //              load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        //          check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(scaledBitmap!=null) {
            try {

                Log.e("scaled height","h:-"+scaledBitmap.getHeight()+" w:-"+scaledBitmap.getWidth());

                file=new File(fileName);
                FileOutputStream out = new FileOutputStream(file);
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;

    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public static  int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private static String getRealPathFromURI(String contentURI, Context context) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }
}
