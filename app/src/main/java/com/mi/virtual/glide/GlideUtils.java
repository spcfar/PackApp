package com.mi.virtual.glide;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Windy on 2018/10/25
 */
public class GlideUtils {

    public static void loadInstalledPackageIcon(Context context, String packageName, ImageView target, @DrawableRes int placeHolder) {
        GlideApp.with(context)
                .load(PackageIconResourceLoader.DATA_PACKAGE_PREFIX + packageName)
                .placeholder(placeHolder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(target);
    }

    public static void loadPackageIconFromApkFile(Context context, String apkFilePath, ImageView target, @DrawableRes int placeHolder) {
        GlideApp.with(context)
                .load(PackageIconResourceLoader.DATA_PACKAGE_FILE_PATH_PREFIX + apkFilePath)
                .placeholder(placeHolder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(target);
    }

}
