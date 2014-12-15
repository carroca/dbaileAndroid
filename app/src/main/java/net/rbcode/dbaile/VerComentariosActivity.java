package net.rbcode.dbaile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.facebook.AppEventsLogger;
import com.google.android.gms.ads.AdView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class VerComentariosActivity extends Activity {

    private int nid;

    protected int[] cid;
    protected String[] userName;
    protected String[] fechas;
    protected String[] comentario;
    protected int[] uid;

    ProgressDialog pDialog;

    ListView lst;
    SharedPreferences wp;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_comentarios);

        wp = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        nid = getIntent().getExtras().getInt("nid");

        new FetchItems().execute();

        context = getApplicationContext();

        //Carga analitycs
        GAnalitycsDbaile gadb = new GAnalitycsDbaile(context, "VerComentariosActivity");
        gadb.enviarDatos();

        //Carga la publicidad
        LinearLayout layout = (LinearLayout)findViewById(R.id.publicidadLayout);
        AdMobDbaile amd = new AdMobDbaile(context, layout);
        amd.load();
    }

    @Override
    public void onPause() {
        /***
         * Evita que la aplicacion falle al girar el dispositivo
         * elimiando el dialogo
         */
        super.onPause();

        if ((pDialog != null) && pDialog.isShowing())
            pDialog.dismiss();
        pDialog = null;
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ver_comentarios, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FetchItems extends AsyncTask<String, Void, JSONArray> {

        //Muestra un cartel que indica que se estan cargando los eventos antes de comenzar
        // a obtenerlos
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VerComentariosActivity.this);
            pDialog.setMessage("Cargando comentarios ....");
            pDialog.show();
        }

        protected JSONArray doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();
            String ruta = "http://dbaile.com/service/comentario?nid=" + nid;

            HttpGet httpget = new HttpGet(ruta);
            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");

            JSONArray json = new JSONArray();

            try {
                HttpResponse response = httpclient.execute(httpget);
                json = new JSONArray(EntityUtils.toString(response.getEntity()));
            }catch (Exception e) {
                Log.v("Error al obtener los comentarios", e.getMessage());
            }

            //get the ListView UI element
            //ListView lst = (ListView) findViewById(R.id.ListComentarios);
            //create the ArrayList to store the titles of nodes
            //ArrayList<String> listItems=new ArrayList<String>();

            cid = new int[json.length()];
            userName = new String[json.length()];
            fechas = new String[json.length()];
            uid = new int[json.length()];
            comentario = new String[json.length()];

            //Se obtienen todos los comentarios del evento
            for(int i = 0; i < json.length(); i++){
                try {
                    userName[i] = json.getJSONObject(i).getString("autor");

                    long seconds = json.getJSONObject(i).getLong("fecha");
                    long millis = seconds * 1000;
                    Date date = new Date(millis);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a", Locale.ENGLISH);
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    fechas[i] = sdf.format(date);
                    uid[i] = json.getJSONObject(i).getInt("uid");
                    cid[i] = json.getJSONObject(i).getInt("cid");
                    comentario[i] = json.getJSONObject(i).getString("comentario");
                } catch (Exception e) {
                    Log.v("Error al sacar datos de los nodos", e.getMessage());
                }
            }

            return json;
        }

        //executed after the background nodes fetching process is complete
        protected void onPostExecute(JSONArray result) {

            if(result.length() != 0 ){

                /*************** Añadido para poner las imagenes junto a los nombres ****************/

                ListaPersonalizadaComentarios adapter = new
                        ListaPersonalizadaComentarios(VerComentariosActivity.this, userName, comentario, fechas);
                lst = (ListView) findViewById(R.id.ListComentarios);
                lst.setAdapter(adapter);

                /*******************************/

                //Elimina el dialogo que aparece de cargando eventos
                //pDialog.dismiss();
                if ((pDialog != null) && pDialog.isShowing()) {
                    pDialog.dismiss();
                }

            } else {

                new AlertDialog.Builder(VerComentariosActivity.this)
                        .setTitle(R.string.dialog_comentarios_no_encontrados)
                        .setPositiveButton(R.string.nuevo, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .show();

            }
        }

    }
}
