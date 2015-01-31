package net.rbcode.dbaile;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

public class LoginActivity extends Activity {

    public String session_name;
    public String session_id;
    public String token;
    public String uid;
    public String userName;
    public String userEmail;

    SharedPreferences wp;
    SharedPreferences.Editor editor;

    public CheckBox checkBox;

    private static Context context;

    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        wp =  getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        editor = wp.edit();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        checkBox = (CheckBox) findViewById(R.id.checkBoxMantenerSesion);

        username= (EditText) findViewById(R.id.editUser);
        password= (EditText) findViewById(R.id.editPassword);

        boolean ums = wp.getBoolean("userMantenerSesion", false);

        if(ums == true){
            username.setText(wp.getString("userName", null));
            password.setText(wp.getString("userPassword", null));
            checkBox.setChecked(true);
        } else {
            editor.putString("userName", null);
            editor.putString("userPassword", null);
            editor.putBoolean("userMantenerSesion", false);
            checkBox.setChecked(false);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent pantalla;
        switch (item.getItemId()) {
            case android.R.id.home:
                pantalla = new Intent(this, StartActivity.class);
                startActivity(pantalla);
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoginProcess extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();

            //set the remote endpoint URL
            HttpPost httppost = new HttpPost("http://dbaile.com/service/user/login");

            try {

                //get the UI elements for username and password
                JSONObject json = new JSONObject();
                //extract the username and password from UI elements and create a JSON object
                json.put("username", username.getText().toString().trim());
                json.put("password", password.getText().toString().trim());

                //add serialised JSON object into POST request
                StringEntity se = new StringEntity(json.toString());
                //set request content type
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);

                //send the POST request
                HttpResponse response = httpclient.execute(httppost);

                //read the response from Services endpoint
                String jsonResponse = EntityUtils.toString(response.getEntity());

                JSONObject jsonObject = new JSONObject(jsonResponse);
                //read the session information
                session_name=jsonObject.getString("session_name");
                session_id=jsonObject.getString("sessid");
                token = jsonObject.getString("token");
                uid = jsonObject.getJSONObject("user").getString("uid");
                userName = jsonObject.getJSONObject("user").getString("name");
                userEmail = jsonObject.getJSONObject("user").getString("mail");
                Log.e("session_name", session_name);
                Log.e("session_id", session_id);

                Log.e("session_token", jsonObject.getString("token"));

                /*************
                 * Empezar a eliminar
                 */

                /*try {

                    HttpPost userLogout = new HttpPost("http://dbaile.com/service/user/logout");

                    BasicHttpContext mHttpContext = new BasicHttpContext();
                    CookieStore mCookieStore      = new BasicCookieStore();

                    //create the session cookie
                    BasicClientCookie cookie = new BasicClientCookie(session_name, session_id);
                    cookie.setVersion(0);
                    cookie.setDomain("dbaile.com");
                    cookie.setPath("/");

                    mCookieStore.addCookie(cookie);
                    cookie = new BasicClientCookie("has_js", "1");
                    mCookieStore.addCookie(cookie);
                    mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

                    HttpResponse responseToken =  httpclient.execute(userLogout,mHttpContext);

                    //read the response from Services endpoint
                    String jsonResponseToken = EntityUtils.toString(responseToken.getEntity());

                    JSONObject jsonObjectToken = new JSONObject(jsonResponseToken);

                    return 0;

                }catch (Exception e) {
                    Log.v("Error al cerrar sesion",e.getMessage());
                }

                return 0;*/

                /*****************
                 * Fin de eliminar
                 ****************/

            }catch (Exception e) {
                Log.v("Error al iniciar sesion", e.getMessage());
            }

            return 0;
        }

        protected void onPostExecute(Integer result) {



            if (checkBox.isChecked()) {
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
            editor.commit();

            new AlertDialog.Builder(LoginActivity.this)
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

    //click listener for doLogin button
    public void doLoginButton_click(View view){
        new LoginProcess().execute();
    }

    public void doRegisterButton_click(View v){
        Intent pantalla;
        pantalla = new Intent(context, RegistrarseActivity.class);
        startActivity(pantalla);
        finish();
    }
}
