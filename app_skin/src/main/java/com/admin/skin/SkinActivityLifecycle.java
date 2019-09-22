package com.admin.skin;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * User: LuHao
 * Date: 2019/9/10 19:22
 * Describe:监听activity的生命周期。在每个activity创建的时候替换皮肤
 */
public class SkinActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    //缓存当前activity使用到的Factory，用于在该activity销毁的时候清除掉使用的Factory
    private Map<Activity, SkinLayoutFactory> cacheFactoryMap = new HashMap<>();

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        try {
            //更改状态栏
            SkinThemeUtils.updateStatusBarColor(activity);
            //更新字体
            Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);

            //activity在创建的时候拿到布局加载器
            LayoutInflater layoutInflater = LayoutInflater.from(activity);

            //参考LayoutInflater源码中的字段mFactorySet的作用:
            //mFactorySet如果添加过一次会变成true，再次添加LayoutInflater的时候则会抛出异常
            //以下处理的目的是为了修改LayoutInflater源码中的字段mFactorySet的状态，使之不抛出异常
            //得到字段mFactorySet
            Field mFactorysets = LayoutInflater.class.getDeclaredField("mFactorySet");
            //设置字段mFactorySet可以被访问
            mFactorysets.setAccessible(true);
            //设置字段mFactorySet的值为false
            mFactorysets.setBoolean(layoutInflater, false);

            //创建一个皮肤工厂
            SkinLayoutFactory skinLayoutFactory = new SkinLayoutFactory(activity,typeface);
            //给当前activity的布局加载器添加这个工厂
            LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutFactory);
            //添加观察者
            SkinManager.getInstance().addObserver(skinLayoutFactory);
            //添加缓存，以便于activity在销毁的时候删除观察者，以免造成内存泄漏
            cacheFactoryMap.put(activity, skinLayoutFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        //删除观察者
        SkinLayoutFactory skinLayoutFactory = cacheFactoryMap.remove(activity);
        //注销观察者
        SkinManager.getInstance().deleteObserver(skinLayoutFactory);
    }
}
