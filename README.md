# basepermissionlib 使用: 
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
