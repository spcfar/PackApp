package com.mi.virtual;

import android.app.Application;
import android.content.Context;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.stub.VASettings;

/**
 * @author Lody
 */
public class XApp extends Application {

    private static final String TAG = "XApp";

    public static final String XPOSED_INSTALLER_PACKAGE = "de.robv.android.xposed.installer";

    private static XApp gApp;
    public static boolean isRunning = false;

    public static XApp getApp() {
        return gApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        gApp = this;
        super.attachBaseContext(base);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            NativeEngine.disableJit(Build.VERSION.SDK_INT);
//        }
        VASettings.ENABLE_IO_REDIRECT = true;
        VASettings.ENABLE_INNER_SHORTCUT = false;
        try {
            VirtualCore virtualCore = VirtualCore.get();
            virtualCore.startup(base);
        } catch (Throwable e) {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
    }

}
