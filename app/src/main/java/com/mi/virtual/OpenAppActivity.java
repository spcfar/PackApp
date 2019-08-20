package com.mi.virtual;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.remote.InstalledAppInfo;
import com.mi.virtual.abs.ui.VActivity;
import com.mi.virtual.abs.ui.VUiKit;
import com.mi.virtual.glide.GlideUtils;
import com.mi.virtual.home.LoadingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weishu
 * @date 18/2/15.
 */

public class OpenAppActivity extends VActivity {

    private ListView mListView;
    private List<AppOpenInfo> mInstalledApps = new ArrayList<>();
    private AppManageAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_open_app);
        mListView = (ListView) findViewById(R.id.ac_app_open_list);
        mAdapter = new AppManageAdapter();
        mListView.setAdapter(mAdapter);

        VirtualCore virtualCore1 = VirtualCore.get();
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("kkkk", "==onCreate=getLaunchIntent=000==" + System.currentTimeMillis() / 1000);
            AppOpenInfo AppOpenInfo = mInstalledApps.get(position);
//            ActivityInfo info = VirtualCore.get().resolveActivityInfo(intent, userId);
            Intent intent = virtualCore1.getLaunchIntent(AppOpenInfo.pkgName, AppOpenInfo.userId);
//            try {
//                virtualCore1.preOpt(AppOpenInfo.pkgName);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            if (intent != null) {
                try {
                    Log.d("kkkk", "==onCreate==000==" + System.currentTimeMillis() / 1000);
//                    VActivityManager.get().startActivity(intent, AppOpenInfo.userId);
                    LoadingActivity.launch(this, AppOpenInfo.pkgName, AppOpenInfo.userId);
//                    finish();
                    Log.d("kkkk", "==startActivity==000==" + System.currentTimeMillis() / 1000);
                } catch (Throwable e) {
                    Log.d("kkkk", "==onCreate_start_failed==000==" + System.currentTimeMillis() / 1000);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.start_app_failed, AppOpenInfo.name), Toast.LENGTH_SHORT).show();
//                    finish();
                }
            }
        });
        loadAsync();
        VirtualCore virtualCore = virtualCore1;
        if (!virtualCore.isEngineLaunched()) {
            virtualCore.waitForEngine();
        }
    }

    private void loadAsync() {
        VUiKit.defer().when(this::loadApp).done((v) -> mAdapter.notifyDataSetChanged());
    }

    private void loadApp() {

        List<AppOpenInfo> ret = new ArrayList<>();
        List<InstalledAppInfo> installedApps = VirtualCore.get().getInstalledApps(0);
        PackageManager packageManager = getPackageManager();
        for (InstalledAppInfo installedApp : installedApps) {
            int[] installedUsers = installedApp.getInstalledUsers();
            for (int installedUser : installedUsers) {
                AppOpenInfo info = new AppOpenInfo();
                info.userId = installedUser;
                ApplicationInfo applicationInfo = installedApp.getApplicationInfo(installedUser);
                info.name = applicationInfo.loadLabel(packageManager);
//                info.icon = applicationInfo.loadIcon(packageManager);  //Use Glide to load icon async
                info.pkgName = installedApp.packageName;
                info.path = applicationInfo.sourceDir;
                ret.add(info);
            }
        }
        mInstalledApps.clear();
        mInstalledApps.addAll(ret);
    }

    class AppManageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mInstalledApps.size();
        }

        @Override
        public AppOpenInfo getItem(int position) {
            return mInstalledApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder(OpenAppActivity.this, parent);
                convertView = holder.root;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AppOpenInfo item = getItem(position);

            holder.label.setText(item.getName());

            if (VirtualCore.get().isOutsideInstalled(item.pkgName)) {
                GlideUtils.loadInstalledPackageIcon(getContext(), item.pkgName, holder.icon, android.R.drawable.sym_def_app_icon);
            } else {
                GlideUtils.loadPackageIconFromApkFile(getContext(), item.path, holder.icon, android.R.drawable.sym_def_app_icon);
            }

//            holder.button.setOnClickListener(v -> showContextMenu(item, v));

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView label;
        ImageView button;

        View root;

        ViewHolder(Context context, ViewGroup parent) {
            root = LayoutInflater.from(context).inflate(R.layout.item_app_manage, parent, false);
            icon = root.findViewById(R.id.item_app_icon);
            label = root.findViewById(R.id.item_app_name);
            button = root.findViewById(R.id.item_app_button);
        }
    }

    static class AppOpenInfo {
        CharSequence name;
        int userId;
        Drawable icon;
        String pkgName;
        String path;

        CharSequence getName() {
            if (userId == 0) {
                return name;
            } else {
                return name + "[" + (userId + 1) + "]";
            }
        }
    }

}
