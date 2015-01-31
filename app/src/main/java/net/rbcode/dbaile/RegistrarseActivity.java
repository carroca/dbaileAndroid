package net.rbcode.dbaile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class RegistrarseActivity extends Activity {

    EditText username, password, password2, mail;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        context = getApplicationContext();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registrarse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent pantalla;
        switch (item.getItemId()) {
            case android.R.id.home:
                pantalla = new Intent(this, LoginActivity.class);
                startActivity(pantalla);
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doRegisterButton_click(View v){

        username = (EditText) findViewById(R.id.editUser);
        password = (EditText) findViewById(R.id.editPassword1);
        password2 = (EditText) findViewById(R.id.editPassword2);
        mail = (EditText) findViewById(R.id.editEmail);

        if(password2.getText().toString().trim().equals(password.getText().toString().trim())){
            new RegisterProcess().execute();
        }
    }

    private class RegisterProcess extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();

            //set the remote endpoint URL
            HttpPost httppost = new HttpPost("http://dbaile.com/service/user/register");

            try {

                List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                urlParameters.add(new BasicNameValuePair("name", username.getText().toString().trim()));
                urlParameters.add(new BasicNameValuePair("pass", password.getText().toString().trim()));
                urlParameters.add(new BasicNameValuePair("mail", mail.getText().toString().trim()));
                urlParameters.add(new BasicNameValuePair("status", "1"));

                httppost.setEntity(new UrlEncodedFormEntity(urlParameters));

                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");

                //send the POST request
                HttpResponse response = httpclient.execute(httppost);

                /*Log.e("Estatus code", Integer.toString(response.getStatusLine().getStatusCode()));

                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                Log.e("Resultado",result.toString());*/

            }catch (Exception e) {
                Log.v("Error al iniciar sesion", e.getMessage());
            }

            return 0;
        }

        protected void onPostExecute(Integer result) {

            /*if (checkBox.isChecked()) {
                editor.putString("userName", username.getText().toString().trim());
                editor.putString("userPassword", password.getText().toString().trim());
                editor.putBoolean("userMantenerSesion", true);
            } else {
                editor.putBoolean("userMantenerSesion", false);
            }

            editor.putString("session_name", session_name);
            editor.putString("session_id", session_id);
            editor.putString("token", token);
            editor.putString("uid", uid);
            editor.commit();*/

            new AlertDialog.Builder(RegistrarseActivity.this)
                    .setTitle(R.string.dialog_session_iniciada)
                    .setNeutralButton(R.string.back, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent pantalla;
                            pantalla = new Intent(context, StartActivity.class);
                            startActivity(pantalla);
                            finish();
                        }
                    })
                    .show();
        }
    }
}
