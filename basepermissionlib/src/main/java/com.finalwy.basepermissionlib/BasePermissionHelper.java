package com.finalwy.basepermissionlib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 权限申请功能的具体实现
 *
 * @author wy
 * @date 2020/2/11.
 */
public class BasePermissionHelper {

    /**
     * 请求权限的code，默认0，不处理请求结果
     */
    private static final int REQUESTCODE_DEFAULT = 0;
    /**
     * 单例对象 mHelper
     */
    private static volatile BasePermissionHelper mHelper;
    /**
     * 单例对象 resultMap
     */
    private static volatile SparseArray<BasePermissionResult> mResults;

    /**
     * 构造函数私有化
     */
    private BasePermissionHelper() {
    }

    /**
     * 获取单例对象 mHelper
     *
     * @return
     */
    public static BasePermissionHelper getInstance() {
        if (mHelper == null) {
            synchronized (BasePermissionHelper.class) {
                if (mHelper == null) {
                    mHelper = new BasePermissionHelper();
                }
            }
        }
        return mHelper;
    }

    /**
     * 获取单例对象 mResults
     *
     * @return
     */
    private static SparseArray<BasePermissionResult> getResults() {
        if (mResults == null) {
            synchronized (BasePermissionHelper.class) {
                if (mResults == null) {
                    mResults = new SparseArray<>(1);
                }
            }
        }
        return mResults;
    }

    /**
     * 检查当前的权限情况
     *
     * @param context
     * @param permissions
     * @return
     */
    protected boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        //6.0以上才需要动态检查
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }


    /**
     * 申请权限但是不需要权限结果回调
     *
     * @param context
     * @param permissions
     * @return
     */
    protected void requestPermission(@NonNull Activity context, @NonNull String... permissions) {
        ActivityCompat.requestPermissions(context,
                permissions, REQUESTCODE_DEFAULT);
    }

    /**
     * 申请权限 需要权限结果回调
     *
     * @param context
     * @param permissions
     * @return
     */
    protected void requestPermission(@NonNull Activity context, int requestCode, BasePermissionResult result, @NonNull String... permissions) {
        //保存结果回调
        if (requestCode != REQUESTCODE_DEFAULT || result != null) {
            getResults().append(requestCode, result);
        }
        ActivityCompat.requestPermissions(context,
                permissions, requestCode);
    }

    /**
     * 在请求回调后可以判断是否被拒绝再次请求
     * Google的原意是：
     * 1，没有申请过权限，申请就是了，所以返回false；
     * 2，申请了用户拒绝了，那你就要提示用户了，所以返回true；
     * 3，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false；
     * 4，已经允许了，不需要申请也不需要提示，所以返回false；
     * 所以单纯的使用shouldShowRequestPermissionRationale去做什么判断，是没用的，只能在请求权限回调后再使用。
     *
     * @param context
     * @param permission
     */
    protected boolean shouldShowRequestPermissionRationale(@NonNull Activity context, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(context, permission);
    }

    /**
     * 被拒绝请求权限，且禁止询问
     * 只有在申请权限的回调中才有用
     *
     * @param context
     * @param permission
     * @return
     */
    protected boolean hasDismissAsk(@NonNull Activity context, @NonNull String permission) {
        return !hasPermission(context, permission)
                && !shouldShowRequestPermissionRationale(context, permission);
    }

    /**
     * 处理权限结果回调
     * 在需要权限回调的activity中重写onRequestPermissionsResult调用此函数
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults, @NonNull Activity context) {
        //不需要回调的情况
        if (requestCode == REQUESTCODE_DEFAULT) {
            return;
        }
        if (mResults == null || mResults.size() == 0) {
            return;
        }
        BasePermissionResult permissionResult = mResults.get(requestCode);
        if (permissionResult == null) {
            return;
        }
        if (permissions.length <= 0) {
            return;
        }
        //以下是需要回调的情况
        //grantResults为空表示权限被禁止询问了但是还调用了申请请求
        if (grantResults == null) {
            //为true不处理onPermissionsDismiss
            boolean onPermissionsDismiss = false;
            //权限被禁止询问
            onPermissionsDismiss = permissionResult.onDismissAsk(requestCode, Arrays.asList(permissions));
            //权限未通过
            if (!onPermissionsDismiss) {
                permissionResult.onPermissionsDismiss(requestCode, Arrays.asList(permissions));
            }
            return;
        }
        if (grantResults.length < permissions.length) {
            return;
        }
        //通过的权限
        List<String> granted = new ArrayList<>();
        //拒绝的权限
        List<String> denied = new ArrayList<>();
        //拒绝的且不能询问的权限
        List<String> noask = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
                //判断不能被询问
                if (!shouldShowRequestPermissionRationale(context, perm)) {
                    noask.add(perm);
                }
            }
        }
        //为true不处理onPermissionsDismiss
        boolean onPermissionsDismiss = false;
        //有权限被禁止询问
        if (!noask.isEmpty()) {
            onPermissionsDismiss = permissionResult.onDismissAsk(requestCode, noask);
        }
        //有权限未通过
        if (!onPermissionsDismiss && !denied.isEmpty()) {
            permissionResult.onPermissionsDismiss(requestCode, denied);
        }
        // 所有权限都通过
        if (!granted.isEmpty() && granted.size() == permissions.length) {
            permissionResult.onPermissionsAccess(requestCode);
        }

    }


    /**
     * 打开 APP 的权限详情设置
     * 在onActivityResult中接收requestCode=2048的权限回调结果，重新执行权限相关逻辑
     *
     * @param context
     * @param permissionShow 权限描述 推荐样式“定位-帮助您推荐上车地点”
     */
    protected void openAppDetails(@NonNull final Activity context, String... permissionShow) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("需要给该应用授权");
        StringBuilder msg = new StringBuilder();
        if (permissionShow != null && permissionShow.length > 0) {
            for (int i = 0; i < permissionShow.length; i++) {
                msg.append(permissionShow[i]);
                msg.append("\n");
            }
        }
        msg.append("\n请到 “应用信息 -> 权限” 中授予！");
        builder.setMessage(msg.toString());
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivityForResult(intent, BasePermission.APP_SETTINGS_RC);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 打开 APP 的权限详情设置
     * 在onActivityResult中接收requestCode=2048的权限回调结果，重新执行权限相关逻辑
     *
     * @param context
     * @param permissionShow 权限描述 推荐样式“定位-帮助您推荐上车地点”
     */
    protected void openAppDetailsForEn(@NonNull final Activity context, String... permissionShow) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("The APP needs to be authorized");
        StringBuilder msg = new StringBuilder();
        if (permissionShow != null && permissionShow.length > 0) {
            for (int i = 0; i < permissionShow.length; i++) {
                msg.append(permissionShow[i]);
                msg.append("\n");
            }
        }
        msg.append("\nGo to \"APP info -> permissions\" to grant!");
        builder.setMessage(msg.toString());
        builder.setPositiveButton("Understand", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivityForResult(intent, BasePermission.APP_SETTINGS_RC);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


}
