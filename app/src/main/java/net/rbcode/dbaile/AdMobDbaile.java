package net.rbcode.dbaile;

import android.content.Context;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * Created by Borja on 21/11/2014.
 */
public class AdMobDbaile {

    private AdView adView;
    private LinearLayout layout;
    private final String uniqueId = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    public AdMobDbaile(Context c, LinearLayout l){
        layout = l;
        adView = new AdView(c);
    }

    public void load(){
        /*adView.setAdUnitId(uniqueId);
        adView.setAdSize(AdSize.BANNER);
        // Añadirle adView.
        layout.addView(adView);
        // Iniciar una solicitud genérica.
        AdRequest adRequest = new AdRequest.Builder().build();
        // Cargar adView con la solicitud de anuncio.
        adView.loadAd(adRequest);*/
    }
}
