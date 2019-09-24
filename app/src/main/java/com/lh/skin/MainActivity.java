package com.lh.skin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.admin.skin.NightModeConfig;
import com.admin.skin.SkinManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态换肤框架
 */
public class MainActivity extends AppCompatActivity {
    private final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,//读sd卡
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//写sd卡
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            //判断权限是否够，6.0以上需要动态权限
            String[] shenqingPERMISSIONS = lacksPermissions(this, PERMISSIONS);
            if (shenqingPERMISSIONS != null) {
                ActivityCompat.requestPermissions(this, shenqingPERMISSIONS, 123);
            } else {
//                SkinManager.getInstance().loadSkin(SkinPreference.getInstance().getSkin());
            }
        } else {
//            SkinManager.getInstance().loadSkin(SkinPreference.getInstance().getSkin());
        }

    }

    /**
     * 判断android6.0以后是否获取了动态权限
     *
     * @param context     上下文
     * @param permissions 所需权限集合
     * @return 如果为null，则有所需的权限了；如果返回String[],则需要动态获取数组中的权限
     */
    public static String[] lacksPermissions(Context context, String[] permissions) {
        if (permissions == null || permissions.length == 0) return null;
        List<String> permissionsList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size() > 0) {
            String[] strings = new String[permissionsList.size()];
            for (int i = 0, j = permissionsList.size(); i < j; i++) {
                strings[i] = permissionsList.get(i);
            }
            return strings;
        }
        return null;
    }

    //换肤
    public void change(View view) {
        //拿到sd卡中的皮肤包
        String path = Environment.getExternalStorageDirectory() + File.separator + "skin_apk_1.apk";
        SkinManager.getInstance().loadSkin(path);
    }

    //切换日间/夜间模式
    public void setNight(View view) {
        boolean isNightMode = NightModeConfig.getInstance().getNightMode(this);
        if (isNightMode) {//这里是夜间，切换成日间
            day();
        } else {
            night();
        }
    }

    /**
     * 夜间模式
     */
    public void night() {
        //获取当前的夜间/日间模式
        int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        //如果当前模式不是夜间，则进行替换
        if (currentMode != Configuration.UI_MODE_NIGHT_YES) {
            //保存夜间模式状态,Application中可以根据这个值判断是否设置夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            //ThemeConfig主题配置，这里只是保存了是否是夜间模式的boolean值
            NightModeConfig.getInstance().setNightMode(getApplicationContext(), true);
            recreate();//需要recreate才能生效
        }
    }

    /**
     * 日间模式
     */
    public void day() {
        //获取当前的夜间/日间模式
        int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        //如果当前模式不是日间，则进行替换
        if (currentMode == Configuration.UI_MODE_NIGHT_YES) {
            //保存夜间模式状态,Application中可以根据这个值判断是否设置夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            //ThemeConfig主题配置，这里只是保存了是否是夜间模式的boolean值
            NightModeConfig.getInstance().setNightMode(getApplicationContext(), false);
            recreate();//需要recreate才能生效
        }
    }
}
