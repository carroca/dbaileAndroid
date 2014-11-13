package net.rbcode.dbaile;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Borja on 22/09/2014.
 */
public class Taxonomias {

    Map<Integer, String> mapita = new HashMap<Integer,String>();
    private String array[][];
    private int cantidad;
    private boolean finalizado = false;

    public Taxonomias() {
        finalizado = false;
        new FetchItems().execute();
    }

    public Map<Integer, String> obtenerTaxonomiaMapa() {
        return mapita;
    }

    public String[][] obtenerTaxonomiasArray(){
        return array;
    }
    public ArrayList<String> obtenerTaxonomiasArrayList() {
        ArrayList<String> al = new ArrayList<String>();
        for(int x = 0; x < array.length; x++ ){
            al.add(array[x][1]);
        }

        return al;
    }

    public int getCantidad(){
        return this.cantidad;
    }

    public boolean getFinalizado() {
        return finalizado;
    }

    private class FetchItems extends AsyncTask<String, Void, JSONArray> {

        protected JSONArray doInBackground(String... params) {

            String ruta = "http://dbaile.com/service/taxonomy_term?parameters[vid]=3";
            HttpClient httpclient = new DefaultHttpClient();

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
                //return json;

            }catch (Exception e) {
                Log.v("Error adding article", e.getMessage());
            }

            array = new String[(json.length() + 1)][2];

            int i;
            try {
                for(i = 0; i < json.length(); i++){
                    int value = json.getJSONObject(i).getInt("tid");
                    String name = json.getJSONObject(i).getString("name");
                    mapita.put(value,name);
                    array[i][0] = Integer.toString(value);
                    array[i][1] = name;
                    cantidad++;
                }

                array[i][0] = "null";
                array[i][1] = "";
                cantidad++;

            } catch (Exception err) {
                Log.e("Error en la taxonomia", err.getMessage());
                err.printStackTrace();
            }

            finalizado = true;

            return json;
        }

        //executed after the background nodes fetching process is complete
        protected void onPostExecute(JSONArray r) {

        }

    }
}
