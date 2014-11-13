package net.rbcode.dbaile;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SchedulingService extends IntentService {

    public SchedulingService() {
        super("SchedulingService");
    }

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    SharedPreferences wp;
    String disciplina, comunidad;

    @Override
    protected void onHandleIntent(Intent intent) {
        wp = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        //sendNotification("service funcionando");
        new FetchItems().execute();
        AlarmReceiver.completeWakefulIntent(intent);
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
        .setContentTitle("Tienes un nuevo evento")
        .setSound(soundUri)
        .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(msg))
        .setLights(Color.MAGENTA, 500, 500)
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private class FetchItems extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... params) {

            //disciplina = wp.getString("opcionesAlarmaSpinnerDisciplinaNombre", "");
            comunidad = wp.getString("opcionesAlarmaSpinnerProvinciaNombre", "");

            HttpClient httpclient = new DefaultHttpClient();
            String ruta = "http://dbaile.com/service/evento";

            ruta = ruta + "?language=es";

            /***
             * Para enviar varios datos a drupal, hay que hacerlo asi:
             * url?field_disciplina_target_id[]=64&field_disciplina_target_id[]=65
             * Cada valor como si fuera un valor de un array
             */

            /************************************
             * Esto se utiliza para poder notificar por varias disciplinas
             */

            String disciplinasMultiples = wp.getString("opcionesAlarmaDialogoDisciplinaIds", "");

            String[] disciplinasArray = disciplinasMultiples.split("-");

            if(wp.getBoolean("opcionesAlarmaDisciplina", false)) {
                if(!disciplinasMultiples.equals("null")){

                    for(int x = 0; x < disciplinasArray.length; x++) {
                        ruta = ruta + "&field_disciplina_target_id[]=" + disciplinasArray[x];
                    }
                }
            }

            /*********************************
             * Aqui finaliza la notificacion por varias disciplinas y solo admite una
             */

            /*if(wp.getBoolean("opcionesAlarmaDisciplina", false)) {
                if(!disciplina.equals("null")){
                    ruta = ruta + "&field_disciplina_target_id[]=" + disciplina;
                }
            }*/

            if(wp.getBoolean("opcionesAlarmaProvincias", false)){
                if(!comunidad.equals("null")){
                    ruta = ruta + "&province=" + comunidad;
                }
            }

            HttpGet httpget = new HttpGet(ruta);

            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");

            JSONArray json = new JSONArray();

            try {
                HttpResponse response = httpclient.execute(httpget);
                json = new JSONArray(EntityUtils.toString(response.getEntity()));
                return json;

            }catch (Exception e) {
                Log.v("Error al obtener los nodos en el servicio:", e.getMessage());
            }

            return json;
        }

        protected void onPostExecute(JSONArray result) {
            int nidObtenido = -1;
            String titulo = "";

            try {
                nidObtenido = result.getJSONObject(0).getInt("nid");
                titulo = result.getJSONObject(0).getString("title");

                if(nidObtenido != wp.getInt("serviceUltimoNidObtenido", -1)){
                    SharedPreferences.Editor editor = wp.edit();
                    editor.putInt("serviceUltimoNidObtenido", nidObtenido);
                    editor.apply();
                    sendNotification(titulo);
                }
            } catch (Exception e) {
                Log.v("Error al obtener el nid y titulo en el servicio:", e.getMessage());
            }

        }
    }
}