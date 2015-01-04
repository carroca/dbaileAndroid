package net.rbcode.dbaile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Borja on 04/01/2015.
 */
public class GridPersonalizadaInicio extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] titulos;
    private final String[] fechas;
    private final Bitmap[] bitmapImg;

    public GridPersonalizadaInicio(Activity context, String[] titulos, Bitmap[] bitmapImg, String[] fechas) {
        super(context, R.layout.grid_personalizada_inicio, titulos);
        this.context = context;
        this.titulos = titulos;
        this.bitmapImg = bitmapImg;
        this.fechas = fechas;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.grid_personalizada_inicio, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtLista);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgLista);
        TextView txtFecha = (TextView) rowView.findViewById(R.id.txtFecha);

        txtTitle.setText(titulos[position]);
        txtFecha.setText(fechas[position]);

        try {
            imageView.setImageBitmap(bitmapImg[position]);
        } catch (Exception e) {}

        return rowView;
    }


}
