package com.finalwy.basepermissionlib;

import android.text.TextUtils;
import android.util.Log;

/**
 * 打印日志工具
 *
 * @author wy
 * @date 2020/2/11.
 */
public class BasePermissionLog {
    /**
     * 打印log日志
     * debug级别，BasePermissionConfig.DEBUG=false时不打印
     *
     * @param logString
     */
    public static void d(String logString) {
        if (TextUtils.isEmpty(logString)) {
            return;
        }
        if (BasePermissionConfig.DEBUG) {
            Log.d(BasePermissionConfig.LOG_TAG, logString);
        }
    }

    /**
     * 打印log日志
     * error级别，不管是不是debug模式，都打印
     *
     * @param logString
     */
    public static void e(String logString) {
        if (TextUtils.isEmpty(logString)) {
            return;
        }
        Log.e(BasePermissionConfig.LOG_TAG, logString);
    }
}
