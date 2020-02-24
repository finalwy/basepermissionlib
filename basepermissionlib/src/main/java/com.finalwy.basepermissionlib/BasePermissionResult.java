package com.finalwy.basepermissionlib;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * 请求权限结果回调
 *
 * @author wy
 * @date 2020/2/11.
 */
public abstract class BasePermissionResult {

    /**
     * 权限被允许
     * 执行自己需要的操作
     *
     * @param requestCode 请求的code
     */
    public void onPermissionsAccess(int requestCode) {
        BasePermissionLog.d("onPermissionsAccess：code =" + requestCode);
    }

    /**
     * 权限被询问并拒绝
     *
     * @param requestCode 请求的code
     * @param permissions 被拒绝的权限
     */
    public void onPermissionsDismiss(int requestCode, @NonNull List<String> permissions) {
        BasePermissionLog.d("onPermissionsDismiss：code =" + requestCode + " " + permissions.toString());
    }
    /**
     * 权限被直接拒绝询问
     *
     * @param requestCode 请求的code
     * @param permissions 被拒绝的权限
     * @return 默认返回false，如果返回为true，将只处理onDismissAsk的回调，不再往下继续处理onPermissionsDismiss或者别的权限请求
     */
    public boolean onDismissAsk(int requestCode, @NonNull List<String> permissions) {
        BasePermissionLog.d("onDismissAsk：code =" + requestCode + " " + permissions.toString());
        return false;
    }
}
