package net.rbcode.dbaile;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SchedulingServiceFavoritos extends IntentService {

    public SchedulingServiceFavoritos() {
        super("SchedulingServiceFavoritos");
    }

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    int ano, mes, dia;

    SharedPreferences wp;
    SQLiteDatabase db;

    @Override
    protected void onHandleIntent(Intent intent) {
        wp = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        //sendNotification("service funcionando");
        DbaileSQLOpenHelper dsoh =
                new DbaileSQLOpenHelper(this, "DBdbaile", null, 1);

        db = dsoh.getWritableDatabase();

        comprobarEventos();
        AlarmReceiver.completeWakefulIntent(intent);
    }

    private void comprobarEventos(){
        Calendar cal = Calendar.getInstance();
        ano = cal.get(Calendar.YEAR);
        mes = cal.get(Calendar.MONTH) + 1;
        dia = cal.get(Calendar.DAY_OF_MONTH);

        String fecha = Integer.toString(ano) + "-";
        fecha = fecha + Integer.toString(mes) + "-";
        fecha = fecha + Integer.toString(dia);

        String[] campos = new String[] {"nid", "titulo"};
        String[] args = new String[] {fecha};

        Cursor c = db.query("favoritos", campos, "fecha=?", args, null, null, null);

        if (c.moveToFirst()) {
            SharedPreferences.Editor editor = wp.edit();
            editor.putInt("serviceUltimoNidObtenido", c.getInt(0));
            editor.apply();
            sendNotification(c.getString(1));
        }
    }
    
    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String msg) {

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mNotificationManager = (NotificationManager)
               this.getSystemService(Context.NOTIFICATION_SERVICE);
    
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, EventoActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("¡Uno de tus eventos favoritos se celebra hoy!")
        .setSound(soundUri)
        .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(msg))
        .setLights(Color.MAGENTA, 500, 500)
        .setContentText(msg)
        //http://stackoverflow.com/questions/2632272/android-notification-doesnt-disappear-after-clicking-the-notifcation
        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}