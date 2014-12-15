package net.rbcode.dbaile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Borja on 15/12/2014.
 */
public class ListaPersonalizadaComentarios extends ArrayAdapter<String> {
    private Activity context;
    private String[] userName;
    private String[] fechas;
    private String[] comentario;

    private ImageView imageView;

    public ListaPersonalizadaComentarios(Activity context, String[] userName, String[] comentario, String[] fechas) {
        super(context, R.layout.lista_personalizada_comentario, userName);
        this.context = context;
        this.comentario = comentario;
        this.userName = userName;
        this.fechas = fechas;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.lista_personalizada_comentario, null, true);

        TextView txtUser = (TextView) rowView.findViewById(R.id.txtUser);
        TextView txtFecha = (TextView) rowView.findViewById(R.id.txtFecha);
        TextView txtComentario = (TextView) rowView.findViewById(R.id.txtComentario);

        txtUser.setText(userName[position]);
        txtFecha.setText(fechas[position]);
        txtComentario.setText(comentario[position]);

        return rowView;
    }
}
