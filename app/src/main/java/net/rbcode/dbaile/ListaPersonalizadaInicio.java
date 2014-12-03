package net.rbcode.dbaile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ListaPersonalizadaInicio extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] titulos;
    private final String[] fechas;
    private final Bitmap[] bitmapImg;

    private ImageView imageView;

    public ListaPersonalizadaInicio(Activity context, String[] titulos, Bitmap[] bitmapImg, String[] fechas) {
        super(context, R.layout.lista_personalizada_inicio, titulos);
        this.context = context;
        this.titulos = titulos;
        this.bitmapImg = bitmapImg;
        this.fechas = fechas;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        //url = "http://dbaile.com/sites/default/files/styles/medium/public/" + imageURL[position];
        //new FetchItems().execute();

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.lista_personalizada_inicio, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtLista);
        imageView = (ImageView) rowView.findViewById(R.id.imgLista);
        TextView txtFecha = (TextView) rowView.findViewById(R.id.txtFecha);

        txtTitle.setText(titulos[position]);
        txtFecha.setText(fechas[position]);

        try {
            imageView.setImageBitmap(bitmapImg[position]);
        } catch (Exception e) {}


        //imageView.setImageResource(imageURL[position]);
        return rowView;
    }
}