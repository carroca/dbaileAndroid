package net.rbcode.dbaile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.*;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.*;
import android.widget.*;
import android.content.Intent;

import org.apache.http.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

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
    Bitmap[] bitmapImg;

    ProgressDialog pDialog;

    ListView lst;
    ArrayList<String> listItems;

    SharedPreferences wp;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        wp = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        new FetchItems().execute();

        context = getApplicationContext();

        GAnalitycsDbaile gadb = new GAnalitycsDbaile(context, "StartActivity");
        gadb.enviarDatos();

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
            pDialog.setMessage("Cargando eventos....");
            pDialog.show();
        }

        protected JSONArray doInBackground(String... params) {

            boolean enPortada = wp.getBoolean("opcionesFiltroEnPortada", false);

            HttpClient httpclient = new DefaultHttpClient();
            String ruta = "http://dbaile.com/service/evento";

            ruta = ruta + "?language=es";

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

            //HttpClient httpclient = new DefaultHttpClient();
            //HttpGet httpget = new HttpGet("http://dbaile.com/service/evento?language=es");
            //set header to tell REST endpoint the request and response content types
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
            ListView lst = (ListView) findViewById(R.id.ListEventos);
            //create the ArrayList to store the titles of nodes
            ArrayList<String> listItems=new ArrayList<String>();

            String titulo;

            nid = new int[json.length()];

            titulos = new String[json.length()];
            uriImagenes = new String[json.length()];
            bitmapImg = new Bitmap[json.length()];

            //iterate through JSON to read the title of nodes
            for(int i=0;i<json.length();i++){
                try {
                    titulo = json.getJSONObject(i).getString("title");

                    if(titulo.length() > 70){
                        titulo = titulo.substring(0,70);
                    }
                    titulos[i] = titulo;
                    uriImagenes[i] = "http://dbaile.com/sites/default/files/styles/medium/public" + json.getJSONObject(i).getJSONObject("cartel").getString("uri").substring(8);
                    try {
                        bitmapImg[i] = downloadImage(uriImagenes[i]);
                    } catch (Exception e) {
                        Log.e("descargar imagen: ", e.getMessage());
                    }

                    listItems.add(titulo);
                    nid[i] = json.getJSONObject(i).getInt("nid");
                } catch (Exception e) {
                    Log.v("Error al sacar datos de los nodos", e.getMessage());
                }
            }

            return json;
        }

        //executed after the background nodes fetching process is complete
        protected void onPostExecute(JSONArray result) {

            /*************** Añadido para poner las imagenes junto a los nombres ****************/

            ListaPersonalizadaInicio adapter = new
                    ListaPersonalizadaInicio(StartActivity.this, titulos, uriImagenes, bitmapImg);
            lst=(ListView)findViewById(R.id.ListEventos);
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
            pDialog.dismiss();
        }

        private Bitmap downloadImage(String url) {
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

}