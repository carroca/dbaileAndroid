package net.rbcode.dbaile;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.*;
import android.widget.*;
import android.content.Intent;

import com.facebook.AppEventsLogger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.apache.http.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class StartActivity extends Activity {

    protected int[] nid;
    String[] titulos;
    String[] uriImagenes;
    String[] nombreImagenes;
    String[] fechas;
    Bitmap[] bitmapImg;
    int pagina = 0;

    ProgressDialog pDialog;

    GridView lst;
    ArrayList<String> listItems;

    SharedPreferences wp;

    private static Context context;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        wp = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        if(getIntent().getIntExtra("pagina", 0) != 0){
            pagina = getIntent().getExtras().getInt("pagina");
        } else {
            pagina = 0;
        }

        new FetchItems().execute();

        context = getApplicationContext();

        //Carga analitycs
        GAnalitycsDbaile gadb = new GAnalitycsDbaile(context, "StartActivity");
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

    public void nuevaActividad(int pos) {
        Intent pantalla = new Intent(this, EventoActivity.class);
        pantalla.putExtra("NID", nid[pos]);
        startActivity(pantalla);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start, menu);
        MenuInflater menuComun = getMenuInflater();
        menuComun.inflate(R.menu.comun, menu);
        //return super.onCreateOptionsMenu(menu);
        //getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent pantalla;
        switch (item.getItemId()) {
            case R.id.action_BuscarEvento:
                pantalla = new Intent(this, buscarevento_activity.class);
                startActivity(pantalla);
                return true;
            case R.id.action_configuracion:
                pantalla = new Intent(this, ConfiguracionActivity.class);
                startActivity(pantalla);
                return true;
            case R.id.action_ver_favoritos:
                pantalla = new Intent(this, VerFavoritosActivity.class);
                startActivity(pantalla);
                return true;
            case R.id.action_siguiente:
                pantalla = new Intent(this, StartActivity.class);
                pantalla.putExtra("pagina", (pagina + 1));
                startActivity(pantalla);
                this.finish();
                return true;
            case R.id.action_anterior:
                if(pagina != 0) {
                    pantalla = new Intent(this, StartActivity.class);
                    pantalla.putExtra("pagina", (pagina - 1));
                    startActivity(pantalla);
                    this.finish();
                    return true;
                }
                return false;
            case R.id.action_login:
                pantalla = new Intent(this, LoginActivity.class);
                startActivity(pantalla);
                this.finish();
                return true;
            /*case R.id.action_help:
                pantalla = new Intent(this, AyudaActivity.class);
                startActivity(pantalla);
                return true;*/
        }
        return false;
    }

    private class FetchItems extends AsyncTask<String, Void, JSONArray> {

        //Muestra un cartel que indica que se estan cargando los eventos antes de comenzar
        // a obtenerlos
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StartActivity.this);
            pDialog.setMessage("Cargando pagina " + (pagina + 1) + "....");
            pDialog.show();
        }

        protected JSONArray doInBackground(String... params) {

            boolean enPortada = wp.getBoolean("opcionesFiltroEnPortada", false);

            HttpClient httpclient = new DefaultHttpClient();
            String ruta = "http://dbaile.com/service/evento";

            ruta = ruta + "?language=es";
            ruta = ruta + "&page=" + pagina;

            if(enPortada) {
                //disciplina = wp.getString("opcionesAlarmaSpinnerDisciplinaNombre", "");
                String comunidad = wp.getString("opcionesPortadaSpinnerProvinciaNombre", "");

                /***
                 * Para enviar varios datos a drupal, hay que hacerlo asi:
                 * url?field_disciplina_target_id[]=64&field_disciplina_target_id[]=65
                 * Cada valor como si fuera un valor de un array
                 */

                String disciplinasMultiples = wp.getString("opcionesPortadaDialogoDisciplinaIds", "");

                String[] disciplinasArray = disciplinasMultiples.split("-");

                if (wp.getBoolean("opcionesPortadaDisciplina", false)) {
                    if (!disciplinasMultiples.equals("null")) {
                        for (int x = 0; x < disciplinasArray.length; x++) {
                            ruta = ruta + "&field_disciplina_target_id[]=" + disciplinasArray[x];
                        }
                    }
                }

                if (wp.getBoolean("opcionesPortadaProvincias", false)) {
                    if (!comunidad.equals("null")) {
                        ruta = ruta + "&province=" + comunidad;
                    }
                }
            }

            HttpGet httpget = new HttpGet(ruta);
            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");

            JSONArray json = new JSONArray();

            try {
                HttpResponse response = httpclient.execute(httpget);
                //read the response and convert it into JSON array
                json = new JSONArray(EntityUtils.toString(response.getEntity()));
                //return the JSON array for post processing to onPostExecute function
                //return json;

            }catch (Exception e) {
                Log.v("Error al obtener los nodos",e.getMessage());
            }

            //get the ListView UI element
            GridView lst = (GridView) findViewById(R.id.ListEventos);
            //create the ArrayList to store the titles of nodes
            ArrayList<String> listItems=new ArrayList<String>();

            String titulo;

            nid = new int[json.length()];

            titulos = new String[json.length()];
            uriImagenes = new String[json.length()];
            nombreImagenes = new String[json.length()];
            bitmapImg = new Bitmap[json.length()];
            fechas = new String[json.length()];

            //iterate through JSON to read the title of nodes
            for(int i = 0; i < json.length(); i++){
                try {
                    titulo = json.getJSONObject(i).getString("title");

                    if(titulo.length() > 40){
                        titulo = titulo.substring(0,40);
                    }
                    titulos[i] = titulo;
                    uriImagenes[i] = "http://dbaile.com/sites/default/files/styles/medium/public" + json.getJSONObject(i).getJSONObject("cartel").getString("uri").substring(8);
                    nombreImagenes[i] = json.getJSONObject(i).getJSONObject("cartel").getString("filename");
                    fechas[i] = json.getJSONObject(i).getJSONObject("fecha_evento").getString("value").substring(0,10);
                    bitmapImg[i] = obtenerArchivos(nombreImagenes[i]);

                    if(bitmapImg[i] == null) {
                        try {
                            bitmapImg[i] = downloadImage(uriImagenes[i], nombreImagenes[i]);
                        } catch (Exception e) {
                            Log.e("descargar imagen: ", e.getMessage());
                        }
                    }

                    listItems.add(titulo);
                    nid[i] = json.getJSONObject(i).getInt("nid");

                    //bitmapImg[i] = Bitmap.createBitmap(bitmapImg[i], 0, 0, 280, 280);
                } catch (Exception e) {
                    Log.v("Error al sacar datos de los nodos", e.getMessage());
                }
            }

            return json;
        }

        //executed after the background nodes fetching process is complete
        protected void onPostExecute(JSONArray result) {

            /*************** Añadido para poner las imagenes junto a los nombres ****************/

            GridPersonalizadaInicio adapter = new
                    GridPersonalizadaInicio(StartActivity.this, titulos, bitmapImg, fechas);
            lst=(GridView)findViewById(R.id.ListEventos);
            lst.setAdapter(adapter);

             /*******************************/

            //create array adapter and give it our list of nodes, pass context, layout and list of items
            //ArrayAdapter ad= new ArrayAdapter(StartActivity.this, android.R.layout.simple_list_item_1,listItems);

            //give adapter to ListView UI element to render
            //lst.setAdapter(ad);

            //String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

            lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    nuevaActividad(position);
                }
            });

            //Elimina el dialogo que aparece de cargando eventos
            //pDialog.dismiss();
            if ((pDialog != null) && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }

        private Bitmap downloadImage(String url, String nombreImagen) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;
            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                //stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            saveImageToLocalStore(bitmap, nombreImagen);
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString) throws IOException {
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
    //http://stackoverflow.com/questions/18073260/save-load-image-to-from-local-storage
    //http://developer.android.com/training/basics/data-storage/files.html
    private Bitmap obtenerArchivos(String nombre){
        Bitmap bitmap = null;
        // http://stackoverflow.com/questions/17546718/android-getting-external-storage-absolute-path
        String dirname = Environment.getExternalStorageDirectory().toString() + "/dbaile/evento";
        File sddir = new File(dirname);
        if (sddir.exists() && isExternalStorageReadable()) {
            try {
                Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/dbaile/evento/" + nombre);
                bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
            } catch (Exception e) {
                Log.e("GetIMG:", e.toString());
            }
        } else {
            return null;
        }
        return bitmap;
    }

    private void saveImageToLocalStore(Bitmap finalBitmap, String imgName) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/dbaile/evento");
        if(myDir.mkdirs() || myDir.exists()) {
            String fname = imgName;
            File file = new File(myDir, fname);
            if (!file.exists() || isExternalStorageWritable()) {
                try {

                    FileOutputStream out = new FileOutputStream(file);
                    finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
                            .parse("file://"
                                    + Environment.getExternalStorageDirectory() + "/dbaile/evento/" + imgName)));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}