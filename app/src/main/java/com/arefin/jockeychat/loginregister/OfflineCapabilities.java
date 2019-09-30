package com.arefin.jockeychat.loginregister;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
//import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class OfflineCapabilities extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // For firebase database
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // For picasso offline image
        Picasso.Builder builder = new Picasso.Builder(this);
        //builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
