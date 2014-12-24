package net.rbcode.dbaile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ListarEventosBuscadosActivity extends Activity {

    protected ListView lst;
    protected int[] nid;
    int cantidadElementos;
    String disciplina, comunidad;
    int dia, mes, ano;
    boolean buscarFecha = false;
    int pagina = 0;

    ProgressDialog pDialog;

    String[] titulos;
    String[] uriImagenes;
    String[] fechas;
    Bitmap[] bitmapImg;
    String[] nombreImagenes;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_eventos_buscados);
        ListView lst = (ListView) findViewById(R.id.ListEventosBuscados);

        context = getApplicationContext();

        GAnalitycsDbaile gadb = new GAnalitycsDbaile(context, "ListarEventosBuscadosActivity");
        gadb.enviarDatos();

        //Carga la publicidad
        LinearLayout layout = (LinearLayout)findViewById(R.id.publicidadLayout);
        AdMobDbaile amd = new AdMobDbaile(context, layout);
        amd.load();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        disciplina = getIntent().getExtras().getString("disciplinaid");
        comunidad = getIntent().getExtras().getString("provinciaid");

        dia = getIntent().getExtras().getInt("dia");
        mes = getIntent().getExtras().getInt("mes");
        ano = getIntent().getExtras().getInt("ano");
        buscarFecha = getIntent().getExtras().getBoolean("busquedaFecha");

        if(getIntent().getIntExtra("pagina", 0) != 0){
            pagina = getIntent().getExtras().getInt("pagina");
        } else {
            pagina = 0;
        }

        new FetchItems().execute();

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
    }

    public void nuevaActividad(int pos) {
        Intent pantalla = new Intent(this, EventoActivity.class);
        pantalla.putExtra("NID", nid[pos]);
        startActivity(pantalla);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuListarEventosBuscados = getMenuInflater();
        menuListarEventosBuscados.inflate(R.menu.listar_eventos_buscados, menu);
        MenuInflater menuComun = getMenuInflater();
        menuComun.inflate(R.menu.comun, menu);

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

            /*case R.id.action_help:
                pantalla = new Intent(this, AyudaActivity.class);
                startActivity(pantalla);
                return true;*/

            case android.R.id.home:
                pantalla = new Intent(this, StartActivity.class);
                startActivity(pantalla);
                this.finish();
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
        }
        return false;
    }

    private class FetchItems extends AsyncTask<String, Void, JSONArray> {

        //Muestra un cartel que indica que se estan cargando los eventos antes de comenzar
        // a obtenerlos
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ListarEventosBuscadosActivity.this);
            pDialog.setMessage(getResources().getString(R.string.dialog_cargando_paginas) + (pagina + 1));
            pDialog.show();
        }

        protected JSONArray doInBackground(String... params) {

            boolean primero = true;

            HttpClient httpclient = new DefaultHttpClient();
            String ruta = "http://dbaile.com/service/evento";

            /***
             * Para enviar varios datos a drupal, hay que hacerlo asi:
             * url?field_disciplina_target_id[]=64&field_disciplina_target_id[]=65
             * Cada valor como si fuera un valor de un array
             */

            ruta = ruta + "?language=es";
            ruta = ruta + "&page=" + pagina;

            // field_evento_fecha_value[value][date]

            if(!disciplina.equals("null")){
                ruta = ruta + "&field_disciplina_target_id[]=" + disciplina;
            }

            if(dia != 0 && mes != 0 && ano != 0 && buscarFecha){
                ruta = ruta + "&field_evento_fecha_value[value][date]=" + mes + "%2F" + dia + "%2F" + ano;
            }

            if(!comunidad.equals("null")){
                ruta = ruta + "&province=" + comunidad;
            }

            HttpGet httpget = new HttpGet(ruta);
            //set header to tell REST endpoint the request and response content types
            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");

            JSONArray json = new JSONArray();

            try {
                HttpResponse response = httpclient.execute(httpget);
                //read the response and convert it into JSON array
                json = new JSONArray(EntityUtils.toString(response.getEntity()));
                //return the JSON array for post processing to onPostExecute function

            }catch (Exception e) {
                Log.v("Error al recibir o enviar la peticion al buscar un evento:", e.getMessage());
            }

            ListView lst = (ListView) findViewById(R.id.ListEventosBuscados);
            //create the ArrayList to store the titles of nodes
            ArrayList<String> listItems=new ArrayList<String>();

            cantidadElementos = json.length();

            if(cantidadElementos == 0) {
                pDialog.dismiss();

            } else {

                String titulo;

                nid = new int[cantidadElementos];

                titulos = new String[cantidadElementos];
                uriImagenes = new String[cantidadElementos];
                bitmapImg = new Bitmap[cantidadElementos];
                nombreImagenes = new String[cantidadElementos];
                fechas = uriImagenes = new String[cantidadElementos];

                //iterate through JSON to read the title of nodes
                for (int i = 0; i < cantidadElementos; i++) {
                    try {
                        titulo = json.getJSONObject(i).getString("title");
                        if (titulo.length() > 70) {
                            titulo = titulo.substring(0, 70);
                        }

                        titulos[i] = titulo;
                        uriImagenes[i] = "http://dbaile.com/sites/default/files/styles/medium/public" + json.getJSONObject(i).getJSONObject("cartel").getString("uri").substring(8);
                        fechas[i] = json.getJSONObject(i).getJSONObject("fecha_evento").getString("value").substring(0,10);
                        nombreImagenes[i] = json.getJSONObject(i).getJSONObject("cartel").getString("filename");
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
                    } catch (Exception e) {
                        Log.v("Error al sacar los datos de la busqueda de eventos", e.getMessage());
                    }
                }
            }

            return json;
        }

        //executed after the background nodes fetching process is complete
        protected void onPostExecute(JSONArray result) {

            //create array adapter and give it our list of nodes, pass context, layout and list of items
            //ArrayAdapter ad= new ArrayAdapter(ListarEventosBuscadosActivity.this, android.R.layout.simple_list_item_1,listItems);

            //give adapter to ListView UI element to render
            //lst.setAdapter(ad);

            if(cantidadElementos != 0) {

                /*************** Añadido para poner las imagenes junto a los nombres ****************/

                ListaPersonalizadaInicio adapter = new
                        ListaPersonalizadaInicio(ListarEventosBuscadosActivity.this, titulos, bitmapImg, fechas);
                lst = (ListView) findViewById(R.id.ListEventosBuscados);
                lst.setAdapter(adapter);

                /*******************************/

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
            } else {

                new AlertDialog.Builder(ListarEventosBuscadosActivity.this)
                        .setTitle(R.string.dialog_eventos_no_encontrados)
                        .setNeutralButton(R.string.back, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();

            }
        }

        private Bitmap downloadImage(String url,String nombreImagen) {
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
