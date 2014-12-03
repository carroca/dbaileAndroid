package net.rbcode.dbaile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class VerFavoritosActivity extends Activity {

    Evento e;
    ArrayList<Evento> eventoArrayList = new ArrayList<Evento>();

    protected int[] nid;
    String[] titulos;
    String[] urlImagenes;
    String[] nombreImagenes;
    String[] fechas;
    Bitmap[] bitmapImg;

    ListView lst;
    private static Context context;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_favoritos);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();
        lst = (ListView) findViewById(R.id.ListEventos);

        GAnalitycsDbaile gadb = new GAnalitycsDbaile(context, "VerFavoritosActivity");
        gadb.enviarDatos();

        //Carga la publicidad
        LinearLayout layout = (LinearLayout)findViewById(R.id.publicidadLayout);
        AdMobDbaile amd = new AdMobDbaile(context, layout);
        amd.load();

        obtenerEventos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start, menu);*/

        //MenuInflater inflater2 = getMenuInflater();
        //inflater2.inflate(R.menu.share, menu);

        MenuInflater menuComun = getMenuInflater();
        menuComun.inflate(R.menu.comun, menu);

        return super.onCreateOptionsMenu(menu);
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
            case android.R.id.home:
                pantalla = new Intent(this, StartActivity.class);
                startActivity(pantalla);
                this.finish();
                return true;
        }
        return false;
    }

    public void obtenerEventos(){
        DbaileSQLOpenHelper dsoh =
                new DbaileSQLOpenHelper(this, "DBdbaile", null, 1);

        SQLiteDatabase db = dsoh.getWritableDatabase();

        String[] campos = new String[] {"nid", "titulo", "fecha", "nombreImagen", "rutaImagen"};
        Cursor c = db.query("favoritos", campos, null, null, null, null, "fecha DESC", "10");

        if (c.moveToFirst()) {
            nid = new int[c.getCount()];

            titulos = new String[c.getCount()];
            urlImagenes = new String[c.getCount()];
            nombreImagenes = new String[c.getCount()];
            bitmapImg = new Bitmap[c.getCount()];
            fechas = new String[c.getCount()];

            int x = 0;

            do {
                nid[x] = c.getInt(0);

                if(c.getString(1).length() > 70){
                    titulos[x] = c.getString(1).substring(0,70);
                } else {
                    titulos[x] = c.getString(1);
                }

                fechas[x] = c.getString(2);
                urlImagenes[x] = "http://dbaile.com/sites/default/files/styles/medium/public" + c.getString(4);
                nombreImagenes[x] = c.getString(3);
                x++;
            } while (c.moveToNext());
            new FetchItems().execute();
        } else {
            new AlertDialog.Builder(VerFavoritosActivity.this)
                    .setTitle(R.string.dialog_eventos_favoritos_no_encontrados)
                    .setNeutralButton(R.string.back, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }

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

    private class FetchItems extends AsyncTask<String, Bitmap, Bitmap[]> {

        //Muestra un cartel que indica que se estan cargando los eventos antes de comenzar
        // a obtenerlos
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VerFavoritosActivity.this);
            pDialog.setMessage("Cargando eventos....");
            pDialog.show();
        }

        protected Bitmap[] doInBackground(String... params) {
            for (int x = 0; x < bitmapImg.length; x++){

                bitmapImg[x] = obtenerArchivos(nombreImagenes[x]);

                if(bitmapImg[x] == null) {
                    try {
                        bitmapImg[x] = downloadImage(urlImagenes[x], nombreImagenes[x]);
                    } catch (Exception e) {
                        Log.e("descargar imagen: ", e.getMessage());
                    }
                }
            }

            return bitmapImg;
        }

        protected void onPostExecute(Bitmap[] img) {

            /*************** Añadido para poner las imagenes junto a los nombres ****************/

            ListaPersonalizadaInicio adapter = new
                    ListaPersonalizadaInicio(VerFavoritosActivity.this, titulos, bitmapImg, fechas);
            lst=(ListView)findViewById(R.id.ListEventos);
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
