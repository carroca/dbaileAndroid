package net.rbcode.dbaile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Borja on 24/11/2014.
 */
public class DbaileLib {

    /**
     * Indicates whether the specified app ins installed and can used as an intent. This
     * method checks the package manager for installed packages that can
     * respond to an intent with the specified app. If no suitable package is
     * found, this method returns false.
     *
     * @param context The application's environment.
     * @param appName The name of the package you want to check
     * @return True if app is installed
     */
    public static boolean isAppAvailable(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    //http://stackoverflow.com/questions/18073260/save-load-image-to-from-local-storage
    //http://developer.android.com/training/basics/data-storage/files.html
    public static Bitmap obtenerImagenDeAlmacenamientoLocal(Context context, String nombre, String ruta) {
        Bitmap bitmap = null;
        // http://stackoverflow.com/questions/17546718/android-getting-external-storage-absolute-path
        String dirname = Environment.getExternalStorageDirectory().toString() + ruta; //"/dbaile/evento";
        File sddir = new File(dirname);
        if (sddir.exists() && isExternalStorageReadable()) {
            try {
                Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + ruta + nombre);
                bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
            } catch (Exception e) {
                Log.e("GetIMG:", e.toString());
            }
        } else {
            return null;
        }
        return bitmap;
    }

    public static void saveImageToLocalStore(Bitmap finalBitmap, String imgName, String ruta) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + ruta);
        if (myDir.mkdirs() || myDir.exists()) {
            String fname = imgName;
            File file = new File(myDir, fname);
            if (!file.exists() || isExternalStorageWritable()) {
                try {

                    FileOutputStream out = new FileOutputStream(file);
                    finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap downloadImage(String url, String nombreImagen) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;
        try {
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.
                    decodeStream(stream, null, bmOptions);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bitmap;
    }

    // Makes HttpURLConnection and returns InputStream
    public static InputStream getHttpConnection(String urlString) throws IOException {
        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }

}
