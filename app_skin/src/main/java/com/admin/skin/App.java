package com.admin.skin;

import android.app.Application;

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
    }
}
