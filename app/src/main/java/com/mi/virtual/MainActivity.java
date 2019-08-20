package com.mi.virtual;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.lody.virtual.client.core.InstallStrategy;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.helper.utils.FileUtils;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.remote.InstalledAppInfo;
import com.mi.virtual.abs.ui.VActivity;
import com.mi.virtual.home.models.AppData;
import com.mi.virtual.home.models.MultiplePackageAppData;
import com.mi.virtual.home.models.PackageAppData;
import com.mi.virtual.home.repo.PackageAppDataStorage;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static com.mi.virtual.XApp.XPOSED_INSTALLER_PACKAGE;

public class MainActivity extends VActivity {
    private static final String PKG_WX = "com.tencent.mm";
    private static final String TAG = "kkkk";
    private Context mContext;
    private Handler mHandler;

//    public static void goHome(Context context) {
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        mContext = this;
        initHandler();
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.READ_PHONE_STATE
                        )
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        new LoadTask().execute();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Toast.makeText(mContext, permissions.toString() + "权限拒绝", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Log.d("kkkk", "==initHandler==000==" + System.currentTimeMillis() / 1000);
                        finish();
                        break;
                }
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class LoadTask extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... objects) {
//            Log.d("kkkk", "==doInBackground==000==" + System.currentTimeMillis()/1000);
            VirtualCore virtualCore = VirtualCore.get();
            if (!virtualCore.isEngineLaunched()) {
                virtualCore.waitForEngine();
            }
//            Log.d("kkkk", "==waitForEngine==000==" + System.currentTimeMillis()/1000);
            installXposed(mContext);
//            Log.d("kkkk", "==installXposed==000==" + System.currentTimeMillis()/1000);
            List<InstalledAppInfo> installedAppInfoList = virtualCore.getInstalledApps(0);
//            Log.d("kkkk", "==installedAppInfoList==000==" + System.currentTimeMillis()/1000);
            boolean blHasWx = false;
            InstalledAppInfo installedAppInfo = null;
            if (installedAppInfoList != null && installedAppInfoList.size() > 0) {
                for (int i = 0; i < installedAppInfoList.size(); i++) {
                    installedAppInfo = installedAppInfoList.get(i);
                    if (PKG_WX.equalsIgnoreCase(installedAppInfo.packageName)) {
                        blHasWx = true;
                    }
                }
            }
            if (!blHasWx) {
                installWx();
            }

            if (installedAppInfo == null)
                return null;
            AppData data = PackageAppDataStorage.get().acquire(installedAppInfo.packageName);
            return data;
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);
//            Log.d("kkkk", "==onPostExecute==000==" + System.currentTimeMillis()/1000);
            if (object != null && object instanceof AppData) {
                AppData appData = (AppData) object;
                openApp(appData);
            } else {
                Toast.makeText(mContext, "系统崩溃,请重新安装本软件", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void installWx() {
//        Log.d("kkkk", "==installWx==000==" + System.currentTimeMillis()/1000);
        InputStream input = null;
        OutputStream output = null;
        try {
            String path = GetFileUtils.getWxDir() + "wx.apk_";
            copyApkFromAssets(mContext, "wx.apk_", path);
            VirtualCore.get().installPackage(path, InstallStrategy.TERMINATE_IF_EXIST);
        } catch (Throwable e) {
            Log.d("kkkk", "==copy file error==000==" + System.currentTimeMillis() / 1000);
        } finally {
            FileUtils.closeQuietly(input);
            FileUtils.closeQuietly(output);
        }
    }

    //拷贝apk文件
    public boolean copyApkFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }

    private void openApp(AppData data) {
//        Log.d("kkkk", "==openApp==000==" + System.currentTimeMillis()/1000);
        try {
            if (data instanceof PackageAppData) {
                PackageAppData appData = (PackageAppData) data;
                launch(mContext, appData.packageName, 0);
            } else if (data instanceof MultiplePackageAppData) {
                MultiplePackageAppData multipleData = (MultiplePackageAppData) data;
                launch(mContext, multipleData.appInfo.packageName, ((MultiplePackageAppData) data).userId);
            }
            mHandler.sendEmptyMessageDelayed(0, 1000);
        } catch (Exception e) {
            Toast.makeText(mContext, "失败" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.d("kkkk", "==openApp=e=" + e);
        }
    }

    public void launch(Context context, String packageName, int userId) {
//        Log.d("kkkk", "==launch==000==" + System.currentTimeMillis()/1000);
        VirtualCore virtualCore = VirtualCore.get();
//        virtualCore.createShortcut(userId, packageName, null);
        Intent intent = virtualCore.getLaunchIntent(packageName, userId);
        if (intent != null) {
            Log.d("kkkk", userId + "==startActivity==VActivityManager==000==" + System.currentTimeMillis() / 1000);
            VActivityManager.get().startActivity(intent, userId);
        }
        Log.d("kkkk", "==over==000==" + System.currentTimeMillis() / 1000);
//        showDialog();
    }

    public void installXposed(Context context) {
        boolean isXposedInstalled = false;
        VirtualCore virtualCore = VirtualCore.get();
        try {
            isXposedInstalled = virtualCore.isAppInstalled(XPOSED_INSTALLER_PACKAGE);
//            File oldXposedInstallerApk = context.getFileStreamPath("XposedInstaller_1_31.apk");
//            if (oldXposedInstallerApk.exists()) {
//                VirtualCore.get().uninstallPackage(XPOSED_INSTALLER_PACKAGE);
//                oldXposedInstallerApk.delete();
//                isXposedInstalled = false;
//                Log.d(TAG, "remove xposed installer success!");
//            }
        } catch (Throwable e) {
            VLog.d(TAG, "remove xposed install failed.", e);
        }

        if (!isXposedInstalled) {
            File xposedInstallerApk = context.getFileStreamPath("XposedInstaller_5_8.apk");
            if (!xposedInstallerApk.exists()) {
                InputStream input = null;
                OutputStream output = null;
                try {
                    input = context.getAssets().open("XposedInstaller_3.1.5.apk_");
                    output = new FileOutputStream(xposedInstallerApk);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = input.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                } catch (Throwable e) {
                    VLog.e(TAG, "copy file error", e);
                } finally {
                    FileUtils.closeQuietly(input);
                    FileUtils.closeQuietly(output);
                }
            }

            if (xposedInstallerApk.isFile()) {
                try {
                    virtualCore.installPackage(xposedInstallerApk.getPath(), InstallStrategy.TERMINATE_IF_EXIST);
                } catch (Throwable ignored) {
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("kkkk", "==onDestroy==000==" + System.currentTimeMillis() / 1000);
//        showDialog();
    }
}
