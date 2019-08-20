package com.mi.virtual.abs.ui;

import android.app.Activity;
import android.content.Context;

import com.mi.virtual.abs.BaseView;

import org.jdeferred.android.AndroidDeferredManager;

/**
 * @author Lody
 */
public class VActivity extends Activity {

    /**
     * Implement of {@link BaseView#getActivity()}
     */
    public Activity getActivity() {
        return this;
    }

    /**
     * Implement of {@link BaseView#getContext()} ()}
     */
    public Context getContext() {
        return this;
    }

    protected AndroidDeferredManager defer() {
        return VUiKit.defer();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
