# basepermissionlib 添加方式: 
android permission request；
根目录添加 **备注：**是在allprojects的repositories添加。

maven {
            url "https://raw.githubusercontent.com/finalwy/basepermissionlib/master" 
        }
	
	
在需要使用该库的build.gradle文件的dependencies添加依赖， 代码如下    

dependencies {
	......省略其它依赖
    implementation 'com.finalwy.basepermissionlib:basepermissionlib:1.0.0'
}
# 功能使用

1.检测权限

只需要调用BasePermission的hasPermission方法,支持多个权限同时传入。

BasePermission.build().hasPermission(this, Manifest.permission.CALL_PHONE);

2.申请权限

如果你在应用启动时需要申请权限，而且并不关注权限的结果，

只需要调用BasePermission的requestPermission方法，支持多个权限传入。

 BasePermission.build().requestPermission(MainActivity.this, Manifest.permission.CALL_PHONE);
 
3.需要权限的结果

如果你需要知道申请权限后用户的选择结果，同时去执行自己的方法myVoid(),

那么在onPermissionsAccess中去做就可以了，

在onPermissionsDismiss是用户拒绝了权限的反馈。

 BasePermission.build()
                .mRequestCode(RC_CODE_CALLPHONE)
                .mContext(NeedReslutActivity.this)
                .mPerms(Manifest.permission.CALL_PHONE)
                .mResult(new BasePermissionResult() {
                    @Override
                    public void onPermissionsAccess(int requestCode) {
                        super.onPermissionsAccess(requestCode);
                        //做你想做的
                    }

                    @Override
                    public void onPermissionsDismiss(int requestCode, @NonNull List<String> permissions) {
                        super.onPermissionsDismiss(requestCode, permissions);
                        //你的权限被用户拒绝了你怎么办
                    }
                }).requestPermission();
		

4.有时用户拒绝了权限，而且禁止了弹出询问，我该怎么办？
同上，只要在onDismissAsk中，就可以得到被禁止的结果，同时你要注意onDismissAsk默认返回false，
如果你自己修改return true，将视为已经处理了禁止结果，将不再回调onPermissionsDismiss这个方法，
调用openAppDetails方法，可以弹窗引导用户去设置界面设置权限，在onActivityResult中检查结果。
 BasePermission basePermission = BasePermission.build()
                .mRequestCode(RC_CODE_CALLPHONE)
                .mContext(DismissAskActivity.this)
                .mPerms(Manifest.permission.CALL_PHONE)
                .mResult(new BasePermissionResult() {
                    @Override
                    public void onPermissionsAccess(int requestCode) {
                        super.onPermissionsAccess(requestCode);
                        //做你想做的
                    }

                    @Override
                    public void onPermissionsDismiss(int requestCode, @NonNull List<String> permissions) {
                        super.onPermissionsDismiss(requestCode, permissions);
                        //你的权限被用户拒绝了你怎么办
                    }

                    @Override
                    public boolean onDismissAsk(int requestCode, @NonNull List<String> permissions) {
                        //你的权限被用户禁止了并且不能请求了你怎么办
                        easyPermission.openAppDetails(DismissAskActivity.this, "Call Phone - Give me the permission to dial the number for you");
                        return true;
                    }
                });
        easyPermission.requestPermission();

        
        @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (BasePermission.APP_SETTINGS_RC == requestCode) {
                    //设置界面返回
                    //Result from system setting
                    if (basePermission.hasPermission(DismissAskActivity.this)) {
                        //做你想做的
                    } else {
                        //从设置回来还是没给你权限
                    }
        
                }
            }
