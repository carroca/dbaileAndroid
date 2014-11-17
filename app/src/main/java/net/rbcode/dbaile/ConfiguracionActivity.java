package net.rbcode.dbaile;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;


public class ConfiguracionActivity extends Activity {

    private String arrayDis[][];
    String arrayPro[][];
    private String array_spinner[];
    private int cantidad;
    Taxonomias tax;
    Spinner spiDis, spiAlaProvincia, spiPorProvincia;
    Comunidades com;
    SharedPreferences wp;
    Switch swiAlaAviso, swiAlaProvincia, swiAlaDisciplina, swiActFavoritos,swiFilEnPortada,
            swiFilEnPorDisciplina, swiFilEnPorProvincia;

    ArrayList portadaSelectedItems, avisoSelectedItems;

    AlarmReceiver alarm = new AlarmReceiver();
    AlarmReceiverFavoritos alarmFav = new AlarmReceiverFavoritos();
    private static Context context;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        tax = new Taxonomias();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        context = getApplicationContext();

        GAnalitycsDbaile gadb = new GAnalitycsDbaile(context, "ConfiguracionActivity");
        gadb.enviarDatos();

        wp =  getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        editor = wp.edit();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //spiDis = (Spinner) findViewById(R.id.spinnerOpcionesEventoDisciplina);

        //Opciones de alarmas
        spiAlaProvincia = (Spinner) findViewById(R.id.spinnerOpcionesEventoProvincia);
        swiAlaAviso = (Switch) findViewById(R.id.switchActivarAlerta);
        swiAlaProvincia = (Switch) findViewById(R.id.switchActivarProvincias);
        swiAlaDisciplina = (Switch) findViewById(R.id.switchActivarDisciplina);

        //Opciones de portada
        spiPorProvincia = (Spinner) findViewById(R.id.spinnerOpcionesPortadaProvincia);
        swiFilEnPortada = (Switch) findViewById(R.id.switchActivarOpcionEnPortada);
        swiFilEnPorDisciplina = (Switch) findViewById(R.id.switchActivarPortadaDisciplina);
        swiFilEnPorProvincia = (Switch) findViewById(R.id.switchActivarPortadaProvincias);
        swiActFavoritos = (Switch) findViewById(R.id.switchActivarAlertaFavoritos);

        /*spiDis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int dispos = spiDis.getSelectedItemPosition();
                editor.putInt("opcionesAlarmaSpinnerDisciplina", dispos);
                editor.putString("opcionesAlarmaSpinnerDisciplinaNombre", arrayDis[dispos][0]);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });*/

        spiAlaProvincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int propos = spiAlaProvincia.getSelectedItemPosition();
                editor.putInt("opcionesAlarmaSpinnerProvincia", propos);
                editor.putString("opcionesAlarmaSpinnerProvinciaNombre", arrayPro[propos][0]);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spiPorProvincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int propos = spiPorProvincia.getSelectedItemPosition();
                editor.putInt("opcionesPortadaSpinnerProvincia", propos);
                editor.putString("opcionesPortadaSpinnerProvinciaNombre", arrayPro[propos][0]);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //Inicializar filtros de notificacion
        swiAlaAviso.setChecked(wp.getBoolean("opcionesAlarmaActivada", false));
        swiAlaDisciplina.setChecked(wp.getBoolean("opcionesAlarmaDisciplina", false));
        swiAlaProvincia.setChecked(wp.getBoolean("opcionesAlarmaProvincias", false));

        //Inicializar filtros portada
        swiFilEnPortada.setChecked(wp.getBoolean("opcionesFiltroEnPortada", false));
        swiFilEnPorDisciplina.setChecked(wp.getBoolean("opcionesPortadaDisciplina", false));
        swiFilEnPorProvincia.setChecked(wp.getBoolean("opcionesPortadaProvincias", false));

        swiActFavoritos.setChecked(wp.getBoolean("opcionesAlarmaEventosFavoritos", false));

        rellenarProvincias();
        //rellenarDisciplinas();
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
        int ultimaDisciplina = wp.getInt("opcionesAlarmaSpinnerDisciplina", -1);
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
        spiAlaProvincia.setAdapter(dataAdapter);
        spiPorProvincia.setAdapter(dataAdapter);

        //Se establece la posicion por defecto como la ultima seleccionada
        int alarmaUltimaProvincia = wp.getInt("opcionesAlarmaSpinnerProvincia", -1);
        int portadaUltimaProvincia = wp.getInt("opcionesPortadaSpinnerProvincia", -1);
        if(alarmaUltimaProvincia != -1) {
            spiAlaProvincia.setSelection(dataAdapter.getPosition(arrayPro[alarmaUltimaProvincia][1]));
        }

        if(portadaUltimaProvincia != -1) {
            spiPorProvincia.setSelection(dataAdapter.getPosition(arrayPro[portadaUltimaProvincia][1]));
        }
    }

    public void guardarAvisoEvento(View v){
        if(swiAlaAviso.isChecked() && !wp.getBoolean("opcionesAlarmaActivada", false)) {
            editor.putBoolean("opcionesAlarmaActivada", true);
            alarm.setAlarm(context);
        } else {
            editor.putBoolean("opcionesAlarmaActivada", false);
            alarm.cancelAlarm(context);
        }
        editor.commit();
    }

    public void guardarAvisoEventoDisciplina(View v){
        if(swiAlaDisciplina.isChecked()) {
            editor.putBoolean("opcionesAlarmaDisciplina", true);
        } else {
            editor.putBoolean("opcionesAlarmaDisciplina", false);
        }
        editor.commit();
    }

    public void guardarAvisoEventoProvincias(View v){
        if(swiAlaProvincia.isChecked()) {
            editor.putBoolean("opcionesAlarmaProvincias", true);
        } else {
            editor.putBoolean("opcionesAlarmaProvincias", false);
        }
        editor.commit();
    }

    public void guardarAvisoFavoritos(View v){
        if(swiActFavoritos.isChecked()) {
            editor.putBoolean("opcionesAlarmaEventosFavoritos", true);
            alarmFav.setAlarm(context);
        } else {
            editor.putBoolean("opcionesAlarmaEventosFavoritos", false);
            alarmFav.cancelAlarm(context);
        }
        editor.commit();
    }

    /*********
     * Prepara el dialogo para las alertas
     */
    public void abrirDialogo(View v){

        avisoSelectedItems = new ArrayList();

        String posicionesSeleccionadas = wp.getString("opcionesAlarmaDialogoDisciplinaSeleccionados", "null");

        boolean[] dialogoSeleccioandos = new boolean[tax.getCantidad()];

        //Se inicializa el array booleano que se pasara para las posiciones marcadas
        for (int x = 0; x < dialogoSeleccioandos.length; x++) {
            dialogoSeleccioandos[x] = false;
        }

        if(!posicionesSeleccionadas.equals("null") || !posicionesSeleccionadas.equals("")) {
            String[] posicionesArray = posicionesSeleccionadas.split("-");

            if (posicionesArray.length > 0) {
                //Se añaden las posiciones seleccionadas al array como true, significa que estan seleccionadas
                for (int x = 0; x < posicionesArray.length; x++) {

                    try {
                        int n = Integer.parseInt(posicionesArray[x]);
                        avisoSelectedItems.add(n);
                        dialogoSeleccioandos[n] = true;
                    } catch(NumberFormatException nfe) {
                        Log.v("No se pudo cambiar el tipo","Error: " + nfe);
                    }
                }
            }
        }

        /********
         * Muestra el dialogo para poder seleccionar multiples disciplinas para la alerta
         */
        new AlertDialog.Builder(ConfiguracionActivity.this)
            .setTitle(R.string.dialog_seleccionar_disciplina)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(tax.obtenerTaxonomiasArrayList()
                                .toArray(new String[tax.obtenerTaxonomiasArrayList().size()]), dialogoSeleccioandos,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    avisoSelectedItems.add(which);
                                } else if (avisoSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    avisoSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String seleccionados = "";
                        String seleccionadosPosicion = "";

                        /*******
                         * Si al pulsar en aceptar hay mas de uno pulsado, se realizara
                         * el proceso para sacar las ID de las disciplinas
                         */
                        if (avisoSelectedItems.size() != 0) {

                            arrayDis = new String[cantidad][2];
                            arrayDis = tax.obtenerTaxonomiasArray();

                            /*************
                             * Por cada disciplina seleccionada se realiza un ciclo
                             * para almacenarlas todas
                             */
                            for (int x = 0; x < avisoSelectedItems.size(); x++) {

                                //Se obtiene la id de la disciplina dede la pulsacion realizada
                                String n = "" + avisoSelectedItems.get(x);
                                int d = Integer.parseInt(n);
                                String disciplina = arrayDis[d][0];

                                /*****
                                 * Comprueba si es la primera vez que se añade una disciplina,
                                 * Si no es la primera vez se le añade un guion para separarlas
                                 */
                                if (x == 0) {
                                    seleccionados += disciplina;
                                    seleccionadosPosicion += avisoSelectedItems.get(x);
                                } else {
                                    seleccionados = seleccionados + "-" + disciplina;
                                    seleccionadosPosicion = seleccionadosPosicion + "-" + avisoSelectedItems.get(x);
                                }
                            }
                        }

                        /********
                         * Se almacenan las disciplinas y posiciones pulsadas.
                         */
                        editor.putString("opcionesAlarmaDialogoDisciplinaIds", seleccionados);
                        editor.putString("opcionesAlarmaDialogoDisciplinaSeleccionados", seleccionadosPosicion);
                        editor.commit();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .show();

    }

    /******************
     * Funciones de configuracion de la portada
     *
     */

    public void guardarAvisoEnPortada(View v){
        if(swiFilEnPortada.isChecked() && !wp.getBoolean("opcionesFiltroEnPortada", false)) {
            editor.putBoolean("opcionesFiltroEnPortada", true);
        } else {
            editor.putBoolean("opcionesFiltroEnPortada", false);
        }
        editor.commit();
    }

    public void guardarPortadaDisciplina(View v){
        if(swiFilEnPorDisciplina.isChecked()) {
            editor.putBoolean("opcionesPortadaDisciplina", true);
        } else {
            editor.putBoolean("opcionesPortadaDisciplina", false);
        }
        editor.commit();
    }

    public void guardarPortadaProvincias(View v){
        if(swiFilEnPorProvincia.isChecked()) {
            editor.putBoolean("opcionesPortadaProvincias", true);
        } else {
            editor.putBoolean("opcionesPortadaProvincias", false);
        }
        editor.commit();
    }

    /*********
     * Prepara el dialogo para las alertas
     */
    public void abrirDialogoPortada(View v){

        portadaSelectedItems = new ArrayList();

        String posicionesSeleccionadas = wp.getString("opcionesPortadaDialogoDisciplinaSeleccionados", "null");

        boolean[] dialogoSeleccioandos = new boolean[tax.getCantidad()];

        //Se inicializa el array booleano que se pasara para las posiciones marcadas
        for (int x = 0; x < dialogoSeleccioandos.length; x++) {
            dialogoSeleccioandos[x] = false;
        }

        if(!posicionesSeleccionadas.equals("null") || !posicionesSeleccionadas.equals("")) {
            String[] posicionesArray = posicionesSeleccionadas.split("-");

            if (posicionesArray.length > 0) {
                //Se añaden las posiciones seleccionadas al array como true, significa que estan seleccionadas
                for (int x = 0; x < posicionesArray.length; x++) {

                    try {
                        int n = Integer.parseInt(posicionesArray[x]);
                        portadaSelectedItems.add(n);
                        dialogoSeleccioandos[n] = true;
                    } catch(NumberFormatException nfe) {
                        Log.v("No se pudo cambiar el tipo","Error: " + nfe);
                    }
                }
            }
        }

        /********
         * Muestra el dialogo para poder seleccionar multiples disciplinas para la alerta
         */
        new AlertDialog.Builder(ConfiguracionActivity.this)
                .setTitle(R.string.dialog_seleccionar_disciplina)
                        // Specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(tax.obtenerTaxonomiasArrayList()
                                .toArray(new String[tax.obtenerTaxonomiasArrayList().size()]), dialogoSeleccioandos,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    portadaSelectedItems.add(which);
                                } else if (portadaSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    portadaSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String seleccionados = "";
                        String seleccionadosPosicion = "";

                        /*******
                         * Si al pulsar en aceptar hay mas de uno pulsado, se realizara
                         * el proceso para sacar las ID de las disciplinas
                         */
                        if (portadaSelectedItems.size() != 0) {

                            arrayDis = new String[cantidad][2];
                            arrayDis = tax.obtenerTaxonomiasArray();

                            /*************
                             * Por cada disciplina seleccionada se realiza un ciclo
                             * para almacenarlas todas
                             */
                            for (int x = 0; x < portadaSelectedItems.size(); x++) {

                                //Se obtiene la id de la disciplina dede la pulsacion realizada
                                String n = "" + portadaSelectedItems.get(x);
                                int d = Integer.parseInt(n);
                                String disciplina = arrayDis[d][0];

                                /*****
                                 * Comprueba si es la primera vez que se añade una disciplina,
                                 * Si no es la primera vez se le añade un guion para separarlas
                                 */
                                if (x == 0) {
                                    seleccionados += disciplina;
                                    seleccionadosPosicion += portadaSelectedItems.get(x);
                                } else {
                                    seleccionados = seleccionados + "-" + disciplina;
                                    seleccionadosPosicion = seleccionadosPosicion + "-" + portadaSelectedItems.get(x);
                                }
                            }
                        }

                        /********
                         * Se almacenan las disciplinas y posiciones pulsadas.
                         */
                        editor.putString("opcionesPortadaDialogoDisciplinaIds", seleccionados);
                        editor.putString("opcionesPortadaDialogoDisciplinaSeleccionados", seleccionadosPosicion);
                        editor.commit();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .show();

    }

}
