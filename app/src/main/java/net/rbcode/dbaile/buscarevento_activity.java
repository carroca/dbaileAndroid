package net.rbcode.dbaile;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class buscarevento_activity extends Activity {

    Map<Integer, String> mapita = new HashMap<Integer,String>();

    private String arrayDis[][];
    String arrayPro[][];
    private String array_spinner[];
    private int cantidad;
    Taxonomias tax;
    Spinner spiDis, spiPro;
    Switch swiBusFecha;
    DatePicker datFecha;
    Comunidades com;
    SharedPreferences wmbPreference;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tax = new Taxonomias();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscarevento_activity);

        context = getApplicationContext();

        GAnalitycsDbaile gadb = new GAnalitycsDbaile(context, "BuscarEventoActivity");
        gadb.enviarDatos();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        spiDis = (Spinner) findViewById(R.id.spinnerBEventoDisciplina);
        spiPro = (Spinner) findViewById(R.id.spinnerBEventoProvincia);
        datFecha = (DatePicker) findViewById(R.id.datePickerFechaEvento);
        swiBusFecha = (Switch) findViewById(R.id.switchUtilizarBusqueda);

        wmbPreference =  getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

        swiBusFecha.setChecked(wmbPreference.getBoolean("busquedaBuscarFecha", false));

        rellenarProvincias();
        rellenarDisciplinas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start, menu);
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
        }
        return false;
    }

    private void rellenarDisciplinas(){

        while(!tax.getFinalizado()) {
            long tiempo = System.currentTimeMillis();
            while ((System.currentTimeMillis() - tiempo) < 200) {}
        }

        cantidad = tax.getCantidad();
        array_spinner = new String[cantidad];
        arrayDis = new String[cantidad][2];
        arrayDis = tax.obtenerTaxonomiasArray();

        List<String> list = new ArrayList<String>();

        //Se rellena un ArrayList con los elementos para el spinner
        for(int i = 0; i < cantidad; i++){
            list.add(arrayDis[i][1]);
        }
        //Se rellena el spiner con los datos
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiDis.setAdapter(dataAdapter);

        //Se establece la posicion por defecto como la ultima seleccionada
        int ultimaDisciplina = wmbPreference.getInt("busquedaDisciplinaUltima", -1);
        if(ultimaDisciplina != -1) {
            spiDis.setSelection(dataAdapter.getPosition(arrayDis[ultimaDisciplina][1]));
        }
    }

    public void rellenarProvincias(){

        com = new Comunidades();

        arrayPro = new String[50][2];

        arrayPro = com.getArrayES();

        List<String> list = new ArrayList<String>();

        for(int i = 0; i < 50; i++){
            list.add(arrayPro[i][1]);
        }

        //Se rellena el spiner con los datos
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiPro.setAdapter(dataAdapter);

        //Se establece la posicion por defecto como la ultima seleccionada
        int ultimaProvincia = wmbPreference.getInt("busquedaProvinciaUltima", -1);
        if(ultimaProvincia != -1) {
            spiPro.setSelection(dataAdapter.getPosition(arrayPro[ultimaProvincia][1]));
        }
    }

    public void botonBuscarClic(View v){
        int dispos = spiDis.getSelectedItemPosition();
        int propos = spiPro.getSelectedItemPosition();
        String disciplina = arrayDis[dispos][0];
        String provincia = arrayPro[propos][0];
        int dia = datFecha.getDayOfMonth();
        int mes = datFecha.getMonth()+1;
        int ano = datFecha.getYear();

        Intent pantalla = new Intent(this, ListarEventosBuscadosActivity.class);
        pantalla.putExtra("disciplinaid", disciplina);
        pantalla.putExtra("provinciaid", provincia);
        pantalla.putExtra("dia", dia);
        pantalla.putExtra("mes", mes);
        pantalla.putExtra("ano", ano);

        SharedPreferences.Editor editor = wmbPreference.edit();

        if(swiBusFecha.isChecked()) {
            editor.putBoolean("busquedaBuscarFecha", true);
            pantalla.putExtra("busquedaFecha", true);
        } else {
            editor.putBoolean("busquedaBuscarFecha", false);
            pantalla.putExtra("busquedaFecha", false);
        }

        editor.putInt("busquedaProvinciaUltima", propos);
        editor.putInt("busquedaDisciplinaUltima", dispos);
        editor.commit();

        startActivity(pantalla);

    }
}
