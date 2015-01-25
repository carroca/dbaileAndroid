package net.rbcode.dbaile;

import android.content.Context;
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

    SharedPreferences wp;
    SharedPreferences.Editor editor;

    public CheckBox checkBox;

    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkBox = (CheckBox) findViewById(R.id.checkBoxMantenerSesion);

        username= (EditText) findViewById(R.id.editUser);
        password= (EditText) findViewById(R.id.editPassword);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

                Log.e("session_name", session_name);
                Log.e("session_id", session_id);

                Log.e("session_token", jsonObject.getString("token"));

                /*************
                 * Empezar a eliminar
                 */

                /*try {

                    HttpPost userToken = new HttpPost("http://dbaile.com/service/user/token");

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

                    HttpResponse responseToken =  httpclient.execute(userToken,mHttpContext);

                    //read the response from Services endpoint
                    String jsonResponseToken = EntityUtils.toString(responseToken.getEntity());

                    JSONObject jsonObjectToken = new JSONObject(jsonResponseToken);

                    token = jsonObjectToken.getString("token");

                    Log.e("token", jsonObjectToken.getString("token"));

                    return 0;

                }catch (Exception e) {
                    Log.v("Error al bobtener el token",e.getMessage());
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

            wp =  getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            editor = wp.edit();

            if (checkBox.isChecked()) {
                editor.putString("userName", username.getText().toString().trim());
                editor.putString("userPassword", password.getText().toString().trim());
            }

            editor.putString("session_name", session_name);
            editor.putString("session_id", session_id);
            editor.putString("token", token);
            editor.commit();
            //create an intent to start the ListActivity
            //Intent intent = new Intent(LoginActivity.this, StartActivity.class);
            //pass the session_id and session_name to ListActivity
            //intent.putExtra("SESSION_ID", session_id);
            //intent.putExtra("SESSION_NAME", session_name);
            //start the ListActivity
            //startActivity(intent);
        }
    }

    //click listener for doLogin button
    public void doLoginButton_click(View view){

        new LoginProcess().execute();
    }
}
