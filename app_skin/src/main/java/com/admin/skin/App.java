package com.admin.skin;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * User: LuHao
 * Date: 2019/9/12 18:59
 * Describe:
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.getInstance().init(this);
        //初始化app的时候就去设置日间/夜间模式
        //根据app上次退出的状态来判断是否需要设置夜间模式,提前在SharedPreference中存了一个是
        // 否是夜间模式的boolean值
        boolean isNightMode = NightModeConfig.getInstance().getNightMode(this);
        if (isNightMode) {//夜间
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {//日间
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
