package com.backingapp.ayman.newsbites;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.MobileAds;
import com.squareup.leakcanary.LeakCanary;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration;

import io.fabric.sdk.android.Fabric;

public class NewsBitesApplication extends Application {

    private boolean runLeakCanary = false;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG && runLeakCanary) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);
        }

        Stetho.initializeWithDefaults(this);

        Fabric.with(this, new Crashlytics());

        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));

        ConnectionBuddyConfiguration networkInspectorConfiguration = new ConnectionBuddyConfiguration.Builder(this).build();
        ConnectionBuddy.getInstance().init(networkInspectorConfiguration);

    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(base);
//    }
}
