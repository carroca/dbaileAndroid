package net.rbcode.dbaile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class EventoActivity extends Activity {

    protected Evento e = new Evento();
    protected TextView tvtit, tvdes, tvfec, tvpre, tvciu, tvpro, tvdis;
    protected ImageView ivc;
    ProgressDialog pDialog;
    protected Comunidades com = new Comunidades();
    Map<Integer, String> map;
    SharedPreferences wp;

    private Menu mOptionsMenu;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Taxonomias tax = new Taxonomias();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvtit = (TextView) findViewById(R.id.textTitulo);
        tvdes = (TextView) findViewById(R.id.textDescripcion);
        tvfec = (TextView) findViewById(R.id.textFecha);
        tvciu = (TextView) findViewById(R.id.textCiudad);
        //tvpre = (TextView) findViewById(R.id.textPrecio);
        //tvpro = (TextView) findViewById(R.id.textProvincia);
        tvdis = (TextView) findViewById(R.id.textDisciplina);
        ivc = (ImageView) findViewById(R.id.imgCartel);

        if(getIntent().getIntExtra("NID", -1) != -1){
            this.e.setNid(getIntent().getExtras().getInt("NID"));
        } else {
            wp =  getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            this.e.setNid(wp.getInt("serviceUltimoNidObtenido", -1));
        }

        map = tax.obtenerTaxonomiaMapa();
        new FetchItems().execute();

        context = getApplicationContext();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater2 = getMenuInflater();
        inflater2.inflate(R.menu.evento, menu);

        mOptionsMenu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start, menu);

        //MenuInflater inflater2 = getMenuInflater();
        //inflater2.inflate(R.menu.share, menu);

        MenuInflater menuComun = getMenuInflater();
        menuComun.inflate(R.menu.comun, menu);

        DbaileSQLOpenHelper dsoh =
                new DbaileSQLOpenHelper(this, "DBdbaile", null, 1);

        SQLiteDatabase db = dsoh.getWritableDatabase();

        String[] campos = new String[] {"nid"};
        String[] args = new String[] {Integer.toString(e.getNid())};

        Cursor c = db.query("favoritos", campos, "nid=?", args, null, null, null);

        if (c.moveToFirst()) {
            //http://stackoverflow.com/questions/19882443/how-to-change-menuitem-icon-in-actionbar-programatically
            mOptionsMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_favotiros_add));
        } else {
            //http://stackoverflow.com/questions/19882443/how-to-change-menuitem-icon-in-actionbar-programatically
            mOptionsMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_favotiros_del));
        }

        db.close();

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
            case R.id.action_ver_favoritos:
                pantalla = new Intent(this, VerFavoritosActivity.class);
                startActivity(pantalla);
                return true;
            /*case R.id.action_help:
                pantalla = new Intent(this, AyudaActivity.class);
                startActivity(pantalla);
                return true;*/
            case R.id.action_anadir_favorito:
                almacenarEvento();
                return true;
        }
        return false;
    }

    public void establecerCampos(){

        String direccion = "";

        if(e.getImg() != null){
            try {
                ivc.setImageBitmap(e.getImg());
            } catch (Exception e) {}
        }

        if(e.getTitle() != null) {
            tvtit.setText(e.getTitle());
        }

        if(e.getBody() != null) {
            tvdes.setText(Html.fromHtml(e.getBody()));
        }

        /*if(e.getPrecio() != null) {
            tvpre.setText(e.getPrecio());
        }*/

        if(e.getFecha() != null) {
            tvfec.setText(e.getFecha());
        }

        if(e.getDireccion() != null) {
            tvciu.setText(e.getDireccion());
        }

        if(e.getDisciplina() != null) {
            tvdis.setText(e.getDisciplina());
        }
    }

    public void almacenarEvento(){
        ContentValues cv = new ContentValues();
        cv.put("nid", e.getNid());
        cv.put("titulo", e.getTitle());
        cv.put("fecha", e.getFecha().substring(0,10));
        cv.put("nombreImagen", e.getImgName());
        cv.put("rutaImagen", e.getImgUri());

        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        DbaileSQLOpenHelper dsoh =
                new DbaileSQLOpenHelper(this, "DBdbaile", null, 1);

        SQLiteDatabase db = dsoh.getWritableDatabase();

        String[] campos = new String[] {"nid"};
        String[] args = new String[] {Integer.toString(e.getNid())};

        Cursor c = db.query("favoritos", campos, "nid=?", args, null, null, null);

        if (!c.moveToFirst()) {
            db.insert("favoritos", null, cv);

            //http://stackoverflow.com/questions/19882443/how-to-change-menuitem-icon-in-actionbar-programatically
            mOptionsMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_favotiros_add));

            new AlertDialog.Builder(EventoActivity.this)
                    .setTitle(R.string.dialog_eventos_anadido_favoritos)
                    .setNeutralButton(R.string.accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        } else {
           db.delete("favoritos", "nid=?", args);

            //http://stackoverflow.com/questions/19882443/how-to-change-menuitem-icon-in-actionbar-programatically
            mOptionsMenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_favotiros_del));

            new AlertDialog.Builder(EventoActivity.this)
                    .setTitle(R.string.dialog_eventos_eliminado_favoritos)
                    .setNeutralButton(R.string.accept, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }

        db.close();
    }

    public void imagenAmpliarImagen(View v){
        Intent pantalla = new Intent(this, VerImagenActivity.class);
        pantalla.putExtra("imagenNombreImagen", e.getImgUri());
        startActivity(pantalla);
    }

    private class FetchItems extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EventoActivity.this);
            pDialog.setMessage("Cargando el evento....");
            pDialog.show();
        }

        protected JSONObject doInBackground(String... params) {

            String ruta = "http://dbaile.com/service/node/" + e.getNid();
            JSONObject comodin;

            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(ruta);
            //set header to tell REST endpoint the request and response content types
            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");

            JSONObject json = new JSONObject();

            try {
                HttpResponse response = httpclient.execute(httpget);
                //read the response and convert it into JSON array
                json = new JSONObject(EntityUtils.toString(response.getEntity()));
                //return the JSON array for post processing to onPostExecute function
                //return json;

            }catch (Exception e) {
                Log.v("Error obteniendo o enviando datos del evento", e.getMessage());
            }

            Bitmap bitmap = null;
            //Se obtiene el titulo
                try{
                    e.setTitle(json.getString("title"));
                } catch (Exception err){
                    Log.e("Titulo", err.getMessage());
                }
            //Se obtiene la descripcion
                try{
                    e.setBody(json.getJSONObject("field_evento_descripcion")
                            .getJSONArray("und")
                            .getJSONObject(0)
                            .getString("value"));
                } catch (Exception err){
                    Log.e("Descripcion", err.getMessage());
                }
            //Se obtiene la imagen
                try{

                    e.setImgName(json.getJSONObject("field_evento_cartel")
                            .getJSONArray("und")
                            .getJSONObject(0)
                            .getString("filename"));

                    e.setImgUri(json.getJSONObject("field_evento_cartel")
                            .getJSONArray("und")
                            .getJSONObject(0)
                            .getString("uri").substring(8));

                    e.setImgUrl("http://dbaile.com/sites/default/files/styles/cartel_evento_completo/public" + e.getImgUri());

                    bitmap = downloadImage(e.getImgUrl());
                    e.setImg(bitmap);

                } catch (Exception err){
                    Log.e("img", err.getMessage());
                }
            //Se obtiene la fecha
                try{
                    e.setFecha(json.getJSONObject("field_evento_fecha")
                            .getJSONArray("und")
                            .getJSONObject(0)
                            .getString("value"));

                    e.setFecha(e.getFecha().substring(0, e.getFecha().length()-3));
                } catch (Exception err){
                    Log.e("Fecha", err.getMessage());
                }
            //Se obtiene el precio
                try{
                    e.setPrecio(json.getJSONObject("field_evento_precio")
                            .getJSONArray("und")
                            .getJSONObject(0)
                            .getString("value"));
                } catch (Exception err){
                    Log.e("Precio", err.getMessage());
                }
            //Se obtienen las ubicaciones
                try{
                    comodin = json.getJSONObject("field_ubicacion")
                            .getJSONArray("und")
                            .getJSONObject(0);
                    e.setProvincia(comodin.getString("province_name"));
                    e.setCiudad(comodin.getString("city"));
                    e.setCalle(comodin.getString("street"));
                    e.setCodigoPostal(comodin.getString("postal_code"));

                } catch (Exception err){
                    Log.e("Ubicacion", err.getMessage());
                }

            //Se obtienen las disciplinas
                try{
                    e.setDisciplinaJObject(json.getJSONObject("field_disciplina"));
                    generarDisciplinasString();
                } catch (Exception err){
                    Log.e("Disciplina", err.getMessage());
                }

            return json;
        }

        //executed after the background nodes fetching process is complete
        protected void onPostExecute(JSONObject r) {

            if ((pDialog != null) && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            //if(e.getImg() != null){
            establecerCampos();
               // pDialog.dismiss();
            //}else{
            //    pDialog.dismiss();
            //    Toast.makeText(EventoActivity.this, "Ha habido algun problema con el evento o hay un error de conexion a internet", Toast.LENGTH_SHORT).show();
            //}

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

    private void generarDisciplinasString() {

        String disciplinas = "";



        try {
            JSONArray json = e.getDisciplinaJObject().getJSONArray("und");
            for(int i = 0; i < json.length(); i++){
                int name = json.getJSONObject(i).getInt("target_id");
                disciplinas = disciplinas + map.get(name) + ", ";
            }
        } catch (Exception err) {
            Log.e("Error al generar las disciplinas", err.getMessage());
            err.printStackTrace();
        }
        e.setDisciplina(disciplinas);
    }

}
