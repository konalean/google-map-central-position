# Step
1. 到google console將android map api啟用
2. 建立google map的key
3. 到src/main/debug/res/values/google_map_api.xml中把key加入該檔案

# Describe
類似uber叫車，將座標固定在中央

若安裝apk有遇到下列exception
```
Error:Execution failed for task ':mapCentral:transformClassesWithDexForDebug'.
> com.android.build.api.transform.TransformException: com.android.ide.common.process.ProcessException: java.util.concurrent.ExecutionException: com.android.dex.DexIndexOverflowException: method ID not in [0, 0xffff]: 65536
```
因在app中引入多個jar，導致method數超過android內定的65536個，導致無法編譯成dex檔，所以就沒辦法包成apk

解決方法：

在build.gradle中加入

```
defaultConfig {

    ...

    multiDexEnabled true

}
```

```
dependencies {

    ...

    compile 'com.android.support:multidex:1.0.0'

    ...

}
```


如遇到以下Exception:
```
java.lang.RuntimeException: Unable to get provider com.google.firebase.provider.FirebaseInitProvider: java.lang.ClassNotFoundException: Didn't find class "com.google.firebase.provider.FirebaseInitProvider" on path: DexPathList
```
修改AndroidManifest.xml
加入
```
<application
    ...
    android:name="android.support.multidex.MultiDexApplication"
    ...>
</application>
```

# License
MIT
