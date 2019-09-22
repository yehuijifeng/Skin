package com.admin.skin;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Observable;

/**
 * User: LuHao
 * Date: 2019/9/10 19:15
 * Describe:皮肤管理类。继承观察者，用于在加载完所有view后通知更换皮肤
 */
public class SkinManager extends Observable {
    private Application application;

    private static class OnSkinManager {
        private static SkinManager skinManager = new SkinManager();
    }

    public static SkinManager getInstance() {
        return OnSkinManager.skinManager;
    }

    /**
     * 初始化
     *
     * @param application 当前app的application对象
     */
    public void init(Application application) {
        this.application = application;
        //初始化一个SharedPreferences，用于存储用户使用的皮肤
        SkinPreference.init(application);
        //初始化皮肤资源类
        SkinResources.init(application);
        //注册activity的生命周期回调监听
        application.registerActivityLifecycleCallbacks(new SkinActivityLifecycle());
    }

    /**
     * 加载皮肤，并保存当前使用的皮肤
     *
     * @param skinPath 皮肤路径 如果为空则使用默认皮肤
     */
    public void loadSkin(String skinPath) {
        //如果传递进来的皮肤文件路径是null，则表示使用默认的皮肤
        if (TextUtils.isEmpty(skinPath)) {
            //存储默认皮肤
            SkinPreference.getInstance().setSkin("");
            //清空皮肤资源属性
            SkinResources.getInstance().reset();
        } else {//传递进来的有皮肤包的文件路径则加载
            try {
                if (!new File(skinPath).exists()) {
                    Toast.makeText(application, "文件不存在", Toast.LENGTH_LONG).show();
                    return;
                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    //android 9 以上无法再反射部分方法，这里使用hook
//                } else {

                    //反射创建AssetManager
                    AssetManager assetManager = AssetManager.class.newInstance();
                    //通过反射得到方法：public int addAssetPath(String path)
                    Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                    addAssetPath.setAccessible(true);
                    //调用该方法，传入皮肤包文件路径
                    addAssetPath.invoke(assetManager, skinPath);
                    //得到当前app的Resources
                    Resources appResource = application.getResources();
                    //根据当前的显示与配置(横竖屏、语言等)创建皮肤包的Resources
                    Resources skinResource = new Resources(
                            assetManager,
                            appResource.getDisplayMetrics(),
                            appResource.getConfiguration());
                    //保存当前用户设置的皮肤包路径
                    SkinPreference.getInstance().setSkin(skinPath);
                    //获取外部皮肤包的包名，首先得到PackageManager对象
                    PackageManager packageManager = application.getPackageManager();
                    //通过PackageManager得到皮肤包的包名信息
                    PackageInfo info = packageManager.getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES);
                    if (info == null) {
                        Toast.makeText(application, "解析皮肤包失败", Toast.LENGTH_LONG).show();
                        return;
                    }
                    //得到皮肤包包名
                    String packageName = info.packageName;
                    //设置皮肤包的包名和Resource对象
                    SkinResources.getInstance().applySkin(skinResource, packageName);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //通知所有采集的View更新皮肤
        setChanged();
        //被观察者通知所有观察者
        notifyObservers(null);
    }


}
