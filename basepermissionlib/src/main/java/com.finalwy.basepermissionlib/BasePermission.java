package com.finalwy.basepermissionlib;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * 请求权限的一些功能实现
 * 包括设置参数：code，结果，权限，mContext
 *
 * @author wy
 * @date 2020/2/11.
 */
public class BasePermission {

    /**
     * 从系统设置返回的code
     */
    public static final int APP_SETTINGS_RC = 2048;
    /**
     * 请求code
     */
    private int mRequestCode = 1;
    /**
     * 请求权限的结果回调
     */
    private BasePermissionResult mResult = null;
    /**
     * 当前activity
     */
    private Activity mContext = null;
    /**
     * 请求的权限
     */
    private String[] mPerms = null;

    protected BasePermission() {
    }

    protected Activity getContext() {
        return mContext;
    }

    /**
     * 重设activity
     *
     * @param mContext
     */
    public void setContext(@NonNull Activity mContext) {
        this.mContext = mContext;
    }

    protected String[] getPerms() {
        return mPerms;
    }

    /**
     * 重设请求的权限
     *
     * @param mPerms
     */
    public void setPerms(@NonNull String[] mPerms) {
        this.mPerms = mPerms;
    }

    protected int getRequestCode() {
        return mRequestCode;
    }

    /**
     * 请求的code
     *
     * @param requestCode A value of 0 means no callback is required
     * @return
     */
    public void setRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
    }

    protected BasePermissionResult getResult() {
        return mResult;
    }

    /**
     * 重新设置result
     *
     * @param result Null means no callback is required 空表示不需要回调
     * @return
     */
    public void setResult(BasePermissionResult result) {
        this.mResult = result;
    }

    /**
     * 返回一个EasyPermission的对象
     *
     * @return
     */
    public static BasePermission build() {
        return new BasePermission();
    }

    /**
     * 设置请求的code
     *
     * @param requestCode A value of 0 indicates that no callback processing is required 0表示不需要回调
     * @return
     */
    public BasePermission mRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    /**
     * 设置结果回调 null表示不要回调
     *
     * @param result
     * @return
     */
    public BasePermission mResult(BasePermissionResult result) {
        this.mResult = result;
        return this;
    }

    /**
     * 设置当前activity
     *
     * @param context
     * @return
     */
    public BasePermission mContext(@NonNull Activity context) {
        this.mContext = context;
        return this;
    }

    /**
     * 设置请求的权限
     *
     * @param permissions
     * @return
     */
    public BasePermission mPerms(@NonNull String... permissions) {
        this.mPerms = permissions;
        return this;
    }

    /**
     * 检查当前的权限
     *
     * @param context
     * @param permissions
     * @return
     */
    public boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        return BasePermissionHelper.getInstance().hasPermission(context, permissions);
    }

    /**
     * 权限被拒绝且禁止询问
     * 只有在申请的回调中使用
     *
     * @param context
     * @param permission
     * @return
     */
    protected boolean hasDismissAsk(@NonNull Activity context, @NonNull String permission) {
        return BasePermissionHelper.getInstance().hasDismissAsk(context, permission);
    }

    /**
     * 判断该权限能否询问
     * 需要提示用户权限功能时返回true，其它情况返回false
     * 第一次请求权限前也返回fasle
     *
     * @param context
     * @param permission
     */
    public boolean shouldShowRequestPermissionRationale(@NonNull Activity context, String permission) {
        return BasePermissionHelper.getInstance().shouldShowRequestPermissionRationale(context, permission);
    }

    /**
     * 申请权限
     *
     * @param context
     * @param permissions
     * @return
     */
    public void requestPermission(@NonNull Activity context, @NonNull String... permissions) {
        if (mResult == null) {
            //如果权限都有了，不需要申请
            if (hasPermission(context, permissions)) {
                return;
            }
            //申请权限但是不需要权限结果回调
            BasePermissionHelper.getInstance().requestPermission(context, permissions);
            return;
        }
        //如果权限都有了，直接回调
        if (hasPermission(context, permissions)) {
            mResult.onPermissionsAccess(mRequestCode);
            return;
        }
        this.mContext = context;
        this.mPerms = permissions;
        //执行申请权限
        BasePermissionHelper.getInstance().requestPermission(context, mRequestCode, mResult, mPerms);

    }

    /**
     * 申请权限，使用当前已有的参数
     * 已经设置好相应的参数
     *
     * @return
     */
    public void requestPermission() {
        if (mContext == null) {
            return;
        }
        if (mPerms == null) {
            return;
        }
        requestPermission(mContext, mPerms);
    }

    /**
     * 打开 APP 的权限详情设置
     */
    public void openAppDetails(@NonNull final Activity context, String... permissionShow) {
        BasePermissionHelper.getInstance().openAppDetails(context, permissionShow);
    }

    /**
     * 打开 APP 的权限详情设置
     */
    public void openAppDetailsForEn(@NonNull final Activity context, String... permissionShow) {
        BasePermissionHelper.getInstance().openAppDetailsForEn(context, permissionShow);
    }

}
