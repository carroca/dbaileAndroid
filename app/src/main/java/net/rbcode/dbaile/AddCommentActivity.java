package net.rbcode.dbaile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class AddCommentActivity extends Activity implements View.OnFocusChangeListener {

    SharedPreferences wp;

    public String session_name;
    public String session_id;
    public String token;
    public String uid;
    public String nid;
    public String username;

    private EditText editComentario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        wp =  getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        session_id = wp.getString("session_id", null);
        session_name = wp.getString("session_name", null);
        token = wp.getString("token", null);
        uid = wp.getString("uid", "0");
        username = wp.getString("userName", null);

        Bundle datosPasados = getIntent().getExtras();
        if(datosPasados != null) {
            nid = Integer.toString(datosPasados.getInt("nid"));
        }

        editComentario = (EditText) findViewById(R.id.editCommentContent);
        editComentario.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String liganame = editComentario.getText().toString();

        if(liganame.length() == 0) {
            if(editComentario.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_add_comment, menu);
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

    //Se ejecuta al pulsar el boton de enviar comentario
    public void addComment(View v){
        new LoginProcess().execute();
    }

    private class LoginProcess extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://dbaile.com/service/comment");

            try {

                //get title and body UI elements
                EditText txtComment = (EditText) findViewById(R.id.editCommentContent);

                //extract text from UI elements and remove extra spaces
                String comment = txtComment.getText().toString().trim();

                //add raw json to be sent along with the HTTP POST request
                StringEntity se = new StringEntity( "{name:\""+ username +"\",\"uid\":\""+uid+"\",\"nid\":\""+nid+"\",\"comment_body\":{\"und\":[{\"value\":\""+comment+"\",\"format\": \"filtered_html\",}]}}}");
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "x-www-form-urlencoded"));
                httppost.setEntity(se);
                httppost.setHeader("X-CSRF-Token", token);

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

                httpclient.execute(httppost,mHttpContext);

                return 0;

            }catch (Exception e) {
                Log.v("Error adding comment",e.getMessage());
            }

            return 0;
        }

        protected void onPostExecute(Integer result) {
            finish();
        }
    }
}
