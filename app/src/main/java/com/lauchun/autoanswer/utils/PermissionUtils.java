package com.lauchun.autoanswer.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * @author ：lauchun
 * @date ：Created in 1/26/22
 * @description ：
 * @version: 1.0
 */
public class PermissionUtils {
    private final Context mContext;

    public PermissionUtils(Context context) {
        mContext = context.getApplicationContext();
    }

    public boolean checkPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (lackPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    private boolean lackPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_DENIED;
    }

}
