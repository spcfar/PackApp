package com.lody.virtual.client.stub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.remote.StubActivityRecord;

/**
 * @author Lody
 */

public class StubPendingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
        Intent intent = getIntent();
        StubActivityRecord r = new StubActivityRecord(intent);
        if (r.intent == null) {
            return;
        }
        r.intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        Log.d("kkkk", "==StubPendingActivity==666666666666666666==" + System.currentTimeMillis() / 1000);
        VActivityManager.get().startActivity(r.intent, r.userId);
    }
}
