package com.admin.skin;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;

/**
 * User: LuHao
 * Date: 2019/9/12 19:46
 * Describe:获取系统资源的值
 */
public class SkinThemeUtils {
    //兼容，如果状态栏的色值没有拿到，则使用系统默认的
    private static int[] a = {R.attr.colorPrimaryDark};
    //状态栏和navigationBar
    private static int[] b = {android.R.attr.statusBarColor, android.R.attr.navigationBarColor};
    //默认字体
    private static int[] c = {R.attr.skinTypeface};

    /**
     * 修改导航栏的颜色
     *
     * @param activity
     */
    public static void updateStatusBarColor(Activity activity) {
        //5.0以上才能修改
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] resbarIds = getResId(activity, b);
            //如果有该值，则可以替换状态栏颜色
            if (resbarIds[0] != 0) {
                activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(resbarIds[0]));
            } else {
                //没有值，则使用兼容色值colorPrimaryDark
                int resbarId = getResId(activity, a)[0];
                if (resbarId != 0) {
                    activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(resbarId));
                }
            }
            //底部NavigationBar如果存在则也要改变色值
            if (resbarIds[1] != 0) {
                activity.getWindow().setNavigationBarColor(SkinResources.getInstance().getColor(resbarIds[1]));
            }
        }
    }

    /**
     * 根据参数的值拿到参数的资源id
     *
     * @param context
     * @param attrs   参数值
     * @return
     */
    public static int[] getResId(Context context, int[] attrs) {
        int[] ints = new int[attrs.length];
        //获得样式属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        for (int i = 0; i < typedArray.length(); i++) {
            ints[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        return ints;
    }


    /**
     * 更新字体
     *
     * @param activity
     */
    public static Typeface getSkinTypeface(Activity activity) {
        int skinTypefaceId = getResId(activity, c)[0];
        return SkinResources.getInstance().getTypeface(skinTypefaceId);
    }
}
