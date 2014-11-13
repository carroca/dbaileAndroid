package net.rbcode.dbaile;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

public class GAnalitycsDbaile {

    private static final String PROPERTY_ID = "xx-xxxxxxxx-x";
    private Tracker tracker;
    private HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    private Context context;
    private String nombre;


    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    private enum TrackerName {
        APP_TRACKER // Tracker used only in this app.
    }

    GAnalitycsDbaile(Context c, String n){
        context = c;
        nombre = n;
    }

    public void enviarDatos(){
        GoogleAnalytics.getInstance(context).newTracker(PROPERTY_ID);
        GoogleAnalytics.getInstance(context).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        tracker = getTracker(TrackerName.APP_TRACKER);
        tracker.setScreenName(nombre);
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    //Obtenido de https://developers.google.com/analytics/devguides/collection/android/v4/?hl=es
    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            com.google.android.gms.analytics.GoogleAnalytics analytics = com.google.android.gms.analytics.GoogleAnalytics.getInstance(context);
            Tracker t = analytics.newTracker(PROPERTY_ID);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
}
